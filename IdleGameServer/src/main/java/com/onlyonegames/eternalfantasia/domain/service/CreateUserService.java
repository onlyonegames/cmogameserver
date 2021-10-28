package com.onlyonegames.eternalfantasia.domain.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.RoleName;
import com.onlyonegames.eternalfantasia.domain.model.dto.*;

import com.onlyonegames.eternalfantasia.domain.model.dto.Inventory.MyClassInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Inventory.MyEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mail.MyMailBoxJsonDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.*;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyBelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyClassInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mail.MyMailBox;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.EquipmentTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.HeroClassInfoTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.InitJsonDatasForFirstUser;
import com.onlyonegames.eternalfantasia.domain.repository.*;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyBelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyClassInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mail.MyMailBoxRepository;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class CreateUserService
{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final MyForTestRepository myForTestRepository;

    private final GameDataTableService gameDataTableService;

    private final MyStatusInfoRepository myStatusInfoRepository;

    private final MyClassInventoryRepository myClassInventoryRepository;

    private final MyEquipmentInventoryRepository myEquipmentInventoryRepository;

    private final MyActiveSkillDataRepository myActiveSkillDataRepository;

    private final MyPixieInfoDataRepository myPixieInfoDataRepository;

    private final MyRuneLevelInfoDataRepository myRuneLevelInfoDataRepository;

    private final MyPassiveSkillDataRepository myPassiveSkillDataRepository;

    private final MyEquipmentInfoRepository myEquipmentInfoRepository;

    private final MyContentsInfoRepository myContentsInfoRepository;

    private final MyDungeonInfoRepository myDungeonInfoRepository;

    private final MyBelongingInventoryRepository myBelongingInventoryRepository;

    private final MyGachaInfoRepository myGachaInfoRepository;

    private final MyCollectionInfoRepository myCollectionInfoRepository;

    private final MyQuickMissionDataRepository myQuickMissionDataRepository;

    private final MyMailBoxRepository myMailBoxRepository;

    private final MyMissionInfoRepository myMissionInfoRepository;

    private final MyChatBlockInfoRepository myChatBlockInfoRepository;

    private final MyShopInfoRepository myShopInfoRepository;

    private final ErrorLoggingService errorLoggingService;

    private final StandardTimeRepository standardTimeRepository;

    private final MyBoosterInfoRepository myBoosterInfoRepository;

    public Map<String, Object> createUser(UserBaseDto userCreateDto, Map<String, Object> map)
    {
        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.findByName(RoleName.ROLE_USER).orElse(null);
        if (role == null) {
            errorLoggingService.SetErrorLog(userCreateDto.getId(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: User Role not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: User Role not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        if(userCreateDto.getSocialId().equals("") || userCreateDto.getSocialId() == null) {
            userCreateDto.setSocialId(UUID.randomUUID().toString());
        }
        roles.add(role);
        userCreateDto.setRoles(roles);
        User previousUser = userCreateDto.ToEntity();
        User createdUser = userRepository.save(previousUser);

        StandardTime standardTime = standardTimeRepository.getOne(1);

        createdUser.SetLastDayResetTime(standardTime.getBaseDayTime());
        createdUser.SetLastWeekResetTime(standardTime.getBaseWeekTime());
        createdUser.SetLastMonthResetTime(standardTime.getBaseMonthTime());

        map.put("user", createdUser);
        long userid = createdUser.getId();

        /* UpgradeStatus */
        MyStatusInfoDto myStatusInfoDto = new MyStatusInfoDto(userid);
        myStatusInfoRepository.save(myStatusInfoDto.ToEntity());

        //최초 유저 기본 클래스 생성
        List<MyClassInventory> myClassInventoryList = createMyClassInventory(userid);
        myClassInventoryRepository.saveAll(myClassInventoryList);

        //최초 유저 장비 생성
        List<MyEquipmentInventory> myEquipmentInventoryList = createMyEquipmentInventory(userid);
        myEquipmentInventoryRepository.saveAll(myEquipmentInventoryList);

        //최초 유저 엑티브 스킬 데이터 생성 및 저장
        MyActiveSkillData myActiveSkillData = createMyActiveSkillData(userid);
        myActiveSkillDataRepository.save(myActiveSkillData);

        //최초 유저 정령 정보 데이터 생성 및 저장
        MyPixieInfoData myPixieInfoData = createMyPixieInfoData(userid);
        myPixieInfoDataRepository.save(myPixieInfoData);

        //최초 유저 룬 레벨 정보 데이터 생성 및 저장
        MyRuneLevelInfoData myRuneLevelInfoData = createMyRuneLevelInfoData(userid);
        myRuneLevelInfoDataRepository.save(myRuneLevelInfoData);

        //최초 유저 페시브 스킬 데이터 생성 및 저장
        MyPassiveSkillData myPassiveSkillData = createMyPassiveSkillData(userid);
        myPassiveSkillDataRepository.save(myPassiveSkillData);

        MyEquipmentInfo myEquipmentInfo = createMyEquipmentInfo(userid);
        myEquipmentInfoRepository.save(myEquipmentInfo);

        MyContentsInfo myContentsInfo = createMyContentsInfo(userid);
        myContentsInfoRepository.save(myContentsInfo);

        MyDungeonInfo myDungeonInfo = createMyDungeonInfo(userid);
        myDungeonInfoRepository.save(myDungeonInfo);

        //TODO Test code 생명의 정수 추가 서비스때 삭제 요망
        List<MyBelongingInventory> myBelongingInventoryList = createMyBelongingInventory(userid);
        myBelongingInventoryRepository.saveAll(myBelongingInventoryList);

        MyGachaInfo myGachaInfo = createMyGachaInfo(userid);
        myGachaInfoRepository.save(myGachaInfo);

        MyCollectionInfo myCollectionInfo = createMyCollectionInfo(userid);
        myCollectionInfoRepository.save(myCollectionInfo);

        MyQuickMissionData myQuickMissionData = createMyQuickMissionData(userid);
        myQuickMissionDataRepository.save(myQuickMissionData);

        MyMailBox myMailBox = createMyMailBox(userid);
        myMailBoxRepository.save(myMailBox);

        MyMissionInfo myMissionInfo = createMyMissionInfo(userid);
        myMissionInfoRepository.save(myMissionInfo);

        MyChatBlockInfo myChatBlockInfo = createMyChatBlockInfo(userid);
        myChatBlockInfoRepository.save(myChatBlockInfo);

        MyShopInfo myShopInfo = createMyShopInfo(userid);
        myShopInfoRepository.save(myShopInfo);

        MyBoosterInfo myBoosterInfo = createMyBoosterInfo(userid);
        myBoosterInfoRepository.save(myBoosterInfo);

        return map;
    }

    private List<MyClassInventory> createMyClassInventory(Long userId) {
        List<HeroClassInfoTable> heroClassInfoTableList = gameDataTableService.HeroClassInfoTable();
        List<MyClassInventory> myClassInventoryList = new ArrayList<>();
        for(HeroClassInfoTable temp : heroClassInfoTableList) {
            if(temp.getGrade().equals("normal")){
                MyClassInventoryDto myClassInventoryDto = new MyClassInventoryDto();
                myClassInventoryDto.setCode(temp.getCode());
                myClassInventoryDto.setCount(0);
                myClassInventoryDto.setLevel(1);
                myClassInventoryDto.setUseridUser(userId);
                myClassInventoryDto.setPromotionPercent(0);
                myClassInventoryList.add(myClassInventoryDto.ToEntity());
            }
        }
        return myClassInventoryList;
    }

    private List<MyEquipmentInventory> createMyEquipmentInventory(Long userId) {
        List<EquipmentTable> equipmentTableList = gameDataTableService.EquipmentTable();
        List<MyEquipmentInventory> myEquipmentInventoryList = new ArrayList<>();
        for(EquipmentTable temp : equipmentTableList) {
            if(temp.getGrade().equals("Normal")){
                String[] codeSplit = temp.getCode().split("_");
                if(codeSplit[1].equals("000")){
                    MyEquipmentInventoryDto myEquipmentInventoryDto = new MyEquipmentInventoryDto();
                    myEquipmentInventoryDto.SetMyEquipmentInventoryDto(userId, temp.getCode(), temp.getGrade(), 0, 1);
                    myEquipmentInventoryList.add(myEquipmentInventoryDto.ToEntity());
                }
            }
        }
        return myEquipmentInventoryList;
    }

    private MyActiveSkillData createMyActiveSkillData(Long userId) {
        List<InitJsonDatasForFirstUser> initJsonDatasForFirstUserList = gameDataTableService.InitJsonDatasForFirstUser();
        InitJsonDatasForFirstUser initJsonDatasForFirstUser = initJsonDatasForFirstUserList.get(0); //TODO index가 변경될 수도 있음
        MyActiveSkillDataDto myActiveSkillDataDto = new MyActiveSkillDataDto();
        myActiveSkillDataDto.setJson_saveDataValue(initJsonDatasForFirstUser.getInitJson());
        myActiveSkillDataDto.setUseridUser(userId);
        return myActiveSkillDataDto.ToEntity();
    }

    private MyPixieInfoData createMyPixieInfoData(Long userId) {
        MyPixieInfoDataDto myPixieInfoDataDto = new MyPixieInfoDataDto();
        myPixieInfoDataDto.SetFirstData(userId);
        return myPixieInfoDataDto.ToEntity();
    }

    private MyRuneLevelInfoData createMyRuneLevelInfoData(Long userId) {
        MyRuneLevelInfoDataDto myRuneLevelInfoDataDto = new MyRuneLevelInfoDataDto();
        myRuneLevelInfoDataDto.SetFirstUserData(userId);
        return myRuneLevelInfoDataDto.ToEntity();
    }

    private MyPassiveSkillData createMyPassiveSkillData(Long userId) {
        List<InitJsonDatasForFirstUser> initJsonDatasForFirstUserList = gameDataTableService.InitJsonDatasForFirstUser();
        InitJsonDatasForFirstUser initJsonDatasForFirstUser = initJsonDatasForFirstUserList.get(1);
        MyPassiveSkillDataDto myPassiveSkillDataDto = new MyPassiveSkillDataDto();
        myPassiveSkillDataDto.setJson_saveDataValue(initJsonDatasForFirstUser.getInitJson());
        myPassiveSkillDataDto.setUseridUser(userId);
        return myPassiveSkillDataDto.ToEntity();
    }

    private MyEquipmentInfo createMyEquipmentInfo(Long userId) {
        return MyEquipmentInfo.builder().useridUser(userId).nowUsedClass(0).warriorEquipment(1).thiefEquipment(2).knightEquipment(3)
                .archerEquipment(4).magicianEquipment(5).nowUsedWeapon(0).swordEquipment(1).daggerEquipment(25).spearEquipment(49)
                .bowEquipment(73).wandEquipment(97).earringEquipment(0).necklaceEquipment(0).ringEquipment(0).costumeEquipment(-1).build();
    }

    private MyContentsInfo createMyContentsInfo(Long userId) {
        return MyContentsInfo.builder().useridUser(userId).challengeTowerFloor(0).build();
    }

    private MyDungeonInfo createMyDungeonInfo(Long userId) {
        MyDungeonInfoDto myDungeonInfoDto = new MyDungeonInfoDto();
        myDungeonInfoDto.setUseridUser(userId);
        return myDungeonInfoDto.ToEntity();
    }

    private List<MyBelongingInventory> createMyBelongingInventory(Long userId) {
        List<MyBelongingInventory> myBelongingInventoryList = new ArrayList<>();
        MyBelongingInventory myBelongingInventory = MyBelongingInventory.builder().useridUser(userId).code("item_008").count(50).slotNo(0).slotPercent(0).build();
        myBelongingInventoryList.add(myBelongingInventory);
        MyBelongingInventory lowPotion = MyBelongingInventory.builder().useridUser(userId).code("item_000").count(100).slotNo(0).slotPercent(0).build();
        myBelongingInventoryList.add(lowPotion);
        MyBelongingInventory sexChange = MyBelongingInventory.builder().useridUser(userId).code("item_010").count(1).slotNo(0).slotPercent(0).build();
        myBelongingInventoryList.add(sexChange);
        MyBelongingInventory nameChange = MyBelongingInventory.builder().useridUser(userId).code("item_009").count(1).slotNo(0).slotPercent(0).build();
        myBelongingInventoryList.add(nameChange);
        MyBelongingInventory itemBooster = MyBelongingInventory.builder().useridUser(userId).code("item_011").count(1).slotNo(0).slotPercent(0).build();
        myBelongingInventoryList.add(itemBooster);
        MyBelongingInventory expBooster = MyBelongingInventory.builder().useridUser(userId).code("item_012").count(1).slotNo(0).slotPercent(0).build();
        myBelongingInventoryList.add(expBooster);
        MyBelongingInventory goldBooster = MyBelongingInventory.builder().useridUser(userId).code("item_013").count(1).slotNo(0).slotPercent(0).build();
        myBelongingInventoryList.add(goldBooster);
        MyBelongingInventory soulStoneBooster = MyBelongingInventory.builder().useridUser(userId).code("item_014").count(1).slotNo(0).slotPercent(0).build();
        myBelongingInventoryList.add(soulStoneBooster);
        MyBelongingInventory speedBooster = MyBelongingInventory.builder().useridUser(userId).code("item_015").count(1).slotNo(0).slotPercent(0).build();
        myBelongingInventoryList.add(speedBooster);
        return myBelongingInventoryList;
    }

    private MyGachaInfo createMyGachaInfo(Long userId) {
        return MyGachaInfo.builder().useridUser(userId).weaponLevel(0).weaponExp(0).classLevel(0).classExp(0).weaponAD(5).classAD(5).artifactAD(5).build();
    }

    private MyCollectionInfo createMyCollectionInfo(Long userId) {
        List<InitJsonDatasForFirstUser> forFirstUsers = gameDataTableService.InitJsonDatasForFirstUser();
        return MyCollectionInfo.builder().useridUser(userId).json_weaponCollectionInfo(forFirstUsers.get(2).getInitJson()).json_classCollectionInfo(forFirstUsers.get(3).getInitJson()).json_monsterCollectionInfo(forFirstUsers.get(4).getInitJson()).build();
    }

    private MyQuickMissionData createMyQuickMissionData(Long userId) {
        List<InitJsonDatasForFirstUser> forFirstUsers = gameDataTableService.InitJsonDatasForFirstUser();
        return MyQuickMissionData.builder().useridUser(userId).json_saveDataValue(forFirstUsers.get(5).getInitJson()).build();
    }

    private MyMailBox createMyMailBox(Long userId) {
        MyMailBoxJsonDto myMailBoxJsonDto = new MyMailBoxJsonDto();
        myMailBoxJsonDto.mailBoxInfoList = new ArrayList<>();
        String json_myMailBoxInfo = JsonStringHerlper.WriteValueAsStringFromData(myMailBoxJsonDto);

        return MyMailBox.builder().useridUser(userId).json_myMailBoxInfo(json_myMailBoxInfo).build();
    }

    private MyMissionInfo createMyMissionInfo(Long userId) {
        List<InitJsonDatasForFirstUser> forFirstUsers = gameDataTableService.InitJsonDatasForFirstUser();
        return MyMissionInfo.builder().useridUser(userId).json_saveData(forFirstUsers.get(6).getInitJson()).build();
    }

    private MyChatBlockInfo createMyChatBlockInfo(Long userId) {
        List<InitJsonDatasForFirstUser> forFirstUsers = gameDataTableService.InitJsonDatasForFirstUser();
        return MyChatBlockInfo.builder().useridUser(userId).json_saveData(forFirstUsers.get(7).getInitJson()).build();
    }

    private MyShopInfo createMyShopInfo(Long userId) {
        MyShopInfoDto myShopInfoDto = new MyShopInfoDto();
        myShopInfoDto.setUseridUser(userId);
        return myShopInfoDto.ToEntity();
    }

    private MyBoosterInfo createMyBoosterInfo(Long userId) {
        return MyBoosterInfo.builder().useridUser(userId).soulStoneRisePercent("").allSpeedRisePercent("").itemDropPlusRisePercent("").expRisePercent("").goldRisePercent("").build();
    }
}