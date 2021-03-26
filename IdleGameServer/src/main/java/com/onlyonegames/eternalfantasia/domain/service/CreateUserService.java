package com.onlyonegames.eternalfantasia.domain.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.RoleName;
import com.onlyonegames.eternalfantasia.domain.model.dto.*;
import com.onlyonegames.eternalfantasia.domain.model.dto.CostumeDto.MyCostumeInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.*;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.EventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.MyEternalPassInfoDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.MyEternalPassMissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Event.AchieveEventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Event.MyAchieveEventMissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.GiftItemDto.GiftItemDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.GiftItemDto.MyGiftInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Gotcha.GotchaMileageDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceDto.MyLinkforceInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceWeaponDto.MyLinkweaponInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MailDto.MailBoxDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MailDto.MyMailBoxDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MyMissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyChapterSaveDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ProfileDto.MyProfileDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ProfileDto.MyProfileMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ProfileDto.ProfileDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ShopDto.MyShopInfoDto;

import com.onlyonegames.eternalfantasia.domain.model.entity.*;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.*;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.*;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPass;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Event.MyAchieveEventMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Gotcha.GotchaMileage;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.*;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mail.MyMailBox;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Shop.MyShopInfo;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.*;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.*;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.*;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Event.MyAchieveEventMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Gotcha.GotchaMileageRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.*;

import com.onlyonegames.eternalfantasia.domain.repository.Mail.MyMailBoxRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Shop.MyShopInfoRepository;
import com.onlyonegames.eternalfantasia.domain.service.Companion.MyVisitCompanionInfoService;
import com.onlyonegames.eternalfantasia.etc.Defines;
import com.onlyonegames.eternalfantasia.etc.EquipmentCalculate;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class CreateUserService
{
    private final UserRepository userRepository;

    private final HerostableRepository herostableRepository;

    private final MyCharactersRepository myCharactersRepository;

    private final RoleRepository roleRepository;

    private final MyMainHeroSkillRepository myMainHeroSkillRepository;

    private final BelongingInventoryRepository belongingInventoryRepository;

    private final ItemTypeRepository itemTypeRepository;

    private final MyEquipmentDeckRepository myEquipmentDeckRepository;

    private final MyProductionSlotRepository myProductionSlotRepository;

    private final MyProductionMasteryRepository myProductionMasteryRepository;

    private final HeroEquipmentInventoryRepository heroEquipmentInventoryRepository;

    private final MyTeamInfoRepository myTeamInfoRepository;

    private final MyChapterSaveDataRepository myChapterSaveDataRepository;

    private final MyVisitCompanionInfoRepository myVisitCompanionInfoRepository;

    private final MyGiftInventoryRepository myGiftInventoryRepository;

    private final MyLinkforceInfoRepository myLinkforceInfoRepository;

    private final MyLinkweaponInfoRepository myLinkweaponInfoRepository;

    private final MyCostumeInventoryRepository myCostumeInventoryRepository;

    private final MyFieldSaveDataRepository myFieldSaveDataRepository;

    private final MyAncientDragonExpandSaveDataRepository myAncientDragonExpandSaveDataRepository;

    private final MyArenaSeasonSaveDataRepository myArenaSeasonSaveDataRepository;

    private final MyExpeditionDataRepository myExpeditionDataRepository;

    private final MyHeroTowerExpandSaveDataRepository myHeroTowerExpandSaveDataRepository;

    private final MyOrdealDungeonExpandSaveDataRepository myOrdealDungeonExpandSaveDataRepository;

    private final GotchaMileageRepository gotchaMileageRepository;

    private final MyProductionMaterialSettedInfoRepository myProductionMaterialSettedInfoRepository;

    private final CompanionStarPointsAverageRepository companionStarPointsAverageRepository;

    private final MyShopInfoRepository myShopInfoRepository;

    private final MyForTestRepository myForTestRepository;

    private final MyMissionsDataRepository myMissionsDataRepository;

    private final MyMailBoxRepository myMailBoxRepository;

    private final GameDataTableService gameDataTableService;

    private final TodayViewingTableRepository todayViewingTableRepository;

    private final MyInfiniteTowerSaveDataRepository myInfiniteTowerSaveDataRepository;

    private final MyEternalPassRepository myEternalPassRepository;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;

    private final MyAchieveEventMissionsDataRepository myAchieveEventMissionsDataRepository;

    private final MyProfileDataRepository myProfileDataRepository;

    private final MyAttendanceDataRepository myAttendanceDataRepository;

    private final UpgradeStatusRepository upgradeStatusRepository;

    private final ErrorLoggingService errorLoggingService;
    private final ArenaSeasonInfoDataRepository arenaSeasonInfoDataRepository;

    public Map<String, Object> createUser(UserBaseDto userCreateDto, Map<String, Object> map)
    {
        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.findByName(RoleName.ROLE_USER).orElse(null);
        if (role == null) {
            errorLoggingService.SetErrorLog(userCreateDto.getId(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: User Role not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: User Role not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        roles.add(role);
        userCreateDto.setRoles(roles);
        User previousUser = userCreateDto.ToEntity();
        User createdUser = userRepository.save(previousUser);

        map.put("user", createdUser);
        long userid = createdUser.getId();

        /* UpgradeStatus */
        UpgradeStatusDto upgradeStatusDto = new UpgradeStatusDto();
        upgradeStatusDto.setUseridUser(userid);
        upgradeStatusDto.setPhysicalAttackPowerLevel(1); // 물리공격력
        upgradeStatusDto.setMagicAttackPowerLevel(1); // 마법공격력
        upgradeStatusDto.setMaxHealthPointLevel(1); // 최대 생명력 (int)
        upgradeStatusDto.setMaxManaPointLevel(1); // 최대 마나 (int)
        upgradeStatusDto.setCriticalChanceLevel(1); // 치명확률
        upgradeStatusDto.setCriticalPercentLevel(1); // 치명데미지
        upgradeStatusRepository.save(upgradeStatusDto.ToEntity());

        /**/
        // long startTime = System.currentTimeMillis();
        List<herostable> heros = herostableRepository.findAll();
        // long endTime = System.currentTimeMillis();
        // System.out.println("### herostable findAll 시간: " + (endTime - startTime) /
        // 1000.0);

        List<MyCharacters> myCharactersResponse = new ArrayList<>();
        for (herostable hero : heros) {
            MyCharactersBaseDto myCharactersBastDto = new MyCharactersBaseDto();
            String codeHeroTable = hero.getCode();
            myCharactersBastDto.setLinkAbilityLevel(hero.getTier());
            myCharactersBastDto.setUseridUser(userid);
            myCharactersBastDto.setCodeHerostable(codeHeroTable);
            MyCharacters myCharacter = myCharactersBastDto.ToEntity(hero.getTier());
            //메인 영웅과 세릴만 획득한 상태로 셋팅
            if (hero.getCode().equals("hero") || hero.getCode().equals("cr_000") || hero.getCode().equals("cr_004"))
                myCharacter.Gotcha();
            myCharactersResponse.add(myCharacter);
        }

        // startTime = System.currentTimeMillis();
        myCharactersResponse = myCharactersRepository.saveAll(myCharactersResponse);
        // endTime = System.currentTimeMillis();
        // System.out.println("### MyCharacters saveAll 저장 시간: " + (endTime - startTime)
        // / 1000.0);
        List<MyCharactersBaseDto> myCharactersBaseDtoList = new ArrayList<>();
        for(MyCharacters myCharacters : myCharactersResponse){
            MyCharactersBaseDto myCharactersBaseDto = new MyCharactersBaseDto();
            myCharactersBaseDto.InitFromDbData(myCharacters);
            myCharactersBaseDtoList.add(myCharactersBaseDto);
        }
        map.put("myCharacters", myCharactersBaseDtoList);
        createMyMainHeroSkills(userid);

        //메인 히어로 팀덱 첫번째에 넣기
        MyCharacters myMainHero = myCharactersResponse.stream()
                .filter(a -> a.getCodeHerostable().equals("hero"))
                .findAny()
                .orElse(null);
        if(myMainHero == null) {
            errorLoggingService.SetErrorLog(createdUser.getId(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMainHero Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMainHero Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        MyTeamInfoDto myTeamInfoDto = new MyTeamInfoDto();
        myTeamInfoDto.setUseridUser(userid);
        MyTeamInfo myTeamInfo = myTeamInfoDto.ToEntity();
        myTeamInfo.AddTeam(myMainHero.getId(),0, Defines.TEAM_BUILDING_KIND.STAGE_PLAY_TEAM);
        myTeamInfo.AddTeam(myMainHero.getId(),0, Defines.TEAM_BUILDING_KIND.ANCIENT_DRAGON_DUNGEON_TEAM);
        myTeamInfo.AddTeam(myMainHero.getId(),0, Defines.TEAM_BUILDING_KIND.ARENA_TEAM);
        myTeamInfo.AddTeam(myMainHero.getId(),0, Defines.TEAM_BUILDING_KIND.ORDEAL_DUNGEON_TEAM);
        myTeamInfo.AddTeam(myMainHero.getId(),0, Defines.TEAM_BUILDING_KIND.HEROTOWER_DUNGEON_TEAM);
        myTeamInfo.AddTeam(myMainHero.getId(),0, Defines.TEAM_BUILDING_KIND.ARENA_DEFEND_TEAM);
        myTeamInfo.AddTeam(myMainHero.getId(),0, Defines.TEAM_BUILDING_KIND.INFINITE_TOWER_TEAM);
        myTeamInfo = myTeamInfoRepository.save(myTeamInfo);
        myTeamInfoDto.InitFromDBData(myTeamInfo);
        map.put("myTeamInfo", myTeamInfoDto);

        //최초 생성되는 belonginginventory 는 없음.
        //List<BelongingInventory> belongingInventory = createMyBelongingInventory(userid);
        List<BelongingInventoryDto> belongingInventoryDtoList = new ArrayList<>();
//        for(BelongingInventory temp : belongingInventory){
//            BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
//            belongingInventoryDto.InitFromDbData(temp);
//            belongingInventoryDtoList.add(belongingInventoryDto);
//        }
        map.put("belongingInventory", belongingInventoryDtoList);

//        List<HeroEquipmentInventory> firstEquipment = createBaseWeapon(userid);
//        List<HeroEquipmentInventoryDto> firstEquipmentDto = new ArrayList<>();
//        for(HeroEquipmentInventory temp : firstEquipment){
//            HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
//            heroEquipmentInventoryDto.InitFromDbData(temp);
//            firstEquipmentDto.add(heroEquipmentInventoryDto);
//        }

        MyEquipmentDeck myEquipmentDeck = CreateMyEquipmentDeck(userid);
        MyEquipmentDeckDto myEquipmentDeckDto = new MyEquipmentDeckDto();
        myEquipmentDeckDto.InitFromDbData(myEquipmentDeck);
        map.put("myEquipmentDeck", myEquipmentDeckDto);

        //최초 유저는 한개의 슬롯을 기본으로 가져감
        MyProductionSlot myProductionSlot = createMyProductionSlot(userid);
        MyProductionSlotDto myProductionSlotDto = new MyProductionSlotDto();
        myProductionSlotDto.InitFromDbData(myProductionSlot);
        map.put("myProducitonSlot", myProductionSlotDto);

        //최초 유저는 한개의 장비(강철검 sword_00_00) 을 가져감
        //HeroEquipmentInventory heroEquipmentInventory = createBaseWeapon(userid);
        //map.put("firstWeapon", heroEquipmentInventory);

        MyProductionMastery myProductionMastery = createMyProductionMastery(userid);
        MyProductionMasteryDto myProductionMasteryDto = new MyProductionMasteryDto();
        myProductionMasteryDto.InitFromDbData(myProductionMastery);
        map.put("myProductionMastery", myProductionMasteryDto);
        //최초 유저 chaptersavedata 셋팅
        MyChapterSaveData myChapterSaveData = createChapterSaveData(userid);
        myChapterSaveDataRepository.save(myChapterSaveData);
        //최초 유저 MyVisitCompanionInfo 생성
        MyVisitCompanionInfo myVisitCompanionInfo = createMyVisitCompanionInfoData(userid);

        MyVisitCompanionInfoService.ResetVisitCompanionInfo(myVisitCompanionInfo, myCharactersResponse, heros);
        myVisitCompanionInfoRepository.save((myVisitCompanionInfo));
        //최초 유저 giftItemInventory 생성
        MyGiftInventory myGiftInventory = createMyGiftInventory(userid);
        myGiftInventoryRepository.save(myGiftInventory);
        //최초 유저 링크포스트리정보 생성
        MyLinkforceInfo myLinkforceInfo = createMyLinkforceInfo(userid);
        myLinkforceInfoRepository.save(myLinkforceInfo);
        //최초 유저 링크웨폰트리정보 생성
        MyLinkweaponInfo myLinkweaponInfo = createMyLinkweaponInfo(userid);
        myLinkweaponInfoRepository.save(myLinkweaponInfo);
        //최초 유저 동료 코스튬인벤토리정보 생성
        MyCostumeInventory myCostumeInventory = createMyCostumeInventory(userid);
        myCostumeInventoryRepository.save(myCostumeInventory);

        //최초 유저 필드데이터 정보 생성
        MyFieldSaveData myFieldSaveData = createMyFieldSaveData(userid);
        myFieldSaveDataRepository.save(myFieldSaveData);
        //최초 유저 고대드래곤 시즌 정보 생성
        MyAncientDragonExpandSaveData myAncientDragonExpandSaveData = createMyAncientDragonExpandSaveData(userid);
        myAncientDragonExpandSaveData.IsResetSeasonStartTime();
        myAncientDragonExpandSaveDataRepository.save(myAncientDragonExpandSaveData);
        //최초 유저 아레나 시즌 정보 생성
        MyArenaSeasonSaveData myArenaSeasonSaveData = createMyArenaSeasonSaveData(userid);
        myArenaSeasonSaveData.DailyTime();
        myArenaSeasonSaveDataRepository.save(myArenaSeasonSaveData);
        //최초 유저 탐험대 데이터 정보 생성
        MyExpeditionData myExpeditionData = createMyExpeditionData(userid);
        myExpeditionDataRepository.save(myExpeditionData);
        //최초 유저 영웅의 탑 데이터 정보 생성
        MyHeroTowerExpandSaveData myHeroTowerExpandSaveData = createMyHeroTowerExpandSaveData(userid);
        myHeroTowerExpandSaveDataRepository.save(myHeroTowerExpandSaveData);
        //최초 유저 시련의 던전 데이터 정보 생성
        MyOrdealDungeonExpandSaveData myOrdealDungeonExpandSaveData = createMyOrdealDungeonExpandSaveData(userid);
        myOrdealDungeonExpandSaveDataRepository.save(myOrdealDungeonExpandSaveData);
        //최초 유저 가차 마일리지 정보 생성
        GotchaMileage gotchaMileage = createGotchaMileage(userid);
        gotchaMileageRepository.save(gotchaMileage);
        //최초 유저 장비 제작시 재료 선택 정보 생성
        MyProductionMaterialSettedInfo myProductionMaterialSettedInfo = createMyProductionMaterialSettedInfo(userid);
        myProductionMaterialSettedInfoRepository.save(myProductionMaterialSettedInfo);
        //최초 유저 동료 별점 셋팅 테이블 생성
        CompanionStarPointsAverageTable companionStarPointsAverageTable = createCompanionStarPointsAverage(userid);
        companionStarPointsAverageRepository.save(companionStarPointsAverageTable);

        MyForTest myForTest = createMyForTest(userid);
        myForTestRepository.save(myForTest);

        //최초 유저 상점 데이터 생성
        MyShopInfo myShopInfo = createMyShopInfo(userid);
        myShopInfoRepository.save(myShopInfo);
        //최초 유저 업적 데이터 생성
        MyMissionsData myMissionData = createMyMissionData(userid);
        myMissionsDataRepository.save(myMissionData);

        //최초 유저 우편 데이터 생성
        MyMailBox myMailBox = createMyMailBox(userid);
        myMailBoxRepository.save(myMailBox);
        //최초 유저 광고 보기 데이터 생성
        MyTodayViewingTable myTodayViewingTable = createMyTodayViewingTable(userid);
        todayViewingTableRepository.save(myTodayViewingTable);

        //최초 유저 무한의 탑 정보 데이터 생성
        MyInfiniteTowerSaveData myInfiniteTowerSaveData = createMyInfiniteTowerSaveData(userid);
        myInfiniteTowerSaveDataRepository.save(myInfiniteTowerSaveData);

        //최초 유저 이터널 패스 정보 데이터 생성
        MyEternalPass myEternalPass = createMyEternalPassTable(userid);
        myEternalPassRepository.save(myEternalPass);
        //최초 유저 이터널 패스 업적 데이터 생성
        MyEternalPassMissionsData myEternalPassMissionsData = createMyEternalPassMissionData(userid);
        myEternalPassMissionsDataRepository.save(myEternalPassMissionsData);

        //최소 유저 누적 달성 이벤트 업적 데이터 생성
        MyAchieveEventMissionsData myAchieveEventMissionsData = createMyAchieveEventMisstionData(userid);
        myAchieveEventMissionsDataRepository.save(myAchieveEventMissionsData);

        //최초 유저 프로필 정보 및 프레임 미션 데이터 생성
        MyProfileData myProfileData = createMyProfileData(userid);
        myProfileDataRepository.save(myProfileData);

        //최초 유저 출석 데이터 생성
        MyAttendanceData myAttendanceData = createMyAttendanceData(userid);
        myAttendanceDataRepository.save(myAttendanceData);

        return map;
    }

    //최초 유저 장비 제작 재료 선택 정보 생성
    MyProductionMaterialSettedInfo createMyProductionMaterialSettedInfo(Long userId) {

        MyProductionMaterialSettedInfoDto myProductionMaterialSettedInfoDto = new MyProductionMaterialSettedInfoDto();
        myProductionMaterialSettedInfoDto.setUserIdUser(userId);

        List<HeroEquipmentProductionTable> heroEquipmentProductionTableList = gameDataTableService.HeroEquipmentProductionTableList();
        for(HeroEquipmentProductionTable heroEquipmentProductionTable: heroEquipmentProductionTableList) {
            if(heroEquipmentProductionTable.getCode().equals("weapon")) {
                myProductionMaterialSettedInfoDto.setWeaponMaterialCounts(heroEquipmentProductionTable.getNeedCounts());
            }
            else if(heroEquipmentProductionTable.getCode().equals("helmet")) {
                myProductionMaterialSettedInfoDto.setHelmetMaterialCounts(heroEquipmentProductionTable.getNeedCounts());
            }
            else if(heroEquipmentProductionTable.getCode().equals("armor")) {
                myProductionMaterialSettedInfoDto.setArmorMaterialCounts(heroEquipmentProductionTable.getNeedCounts());
            }
            else if(heroEquipmentProductionTable.getCode().equals("accessory")) {
                myProductionMaterialSettedInfoDto.setAccessoryMaterialCounts(heroEquipmentProductionTable.getNeedCounts());
            }
        }
        return myProductionMaterialSettedInfoDto.ToEntity();
    }

    // 메인 영웅 스킬 정보 생성
    List<MyMainHeroSkill> createMyMainHeroSkills(Long userId) {
        List<MyMainHeroSkill> mainHerosSkills = new ArrayList<>();

        List<MainHeroSkillExpandInfo> mainHeroSkillExpandInfos = gameDataTableService.MainHeroSkillExpandInfoList();
        for (MainHeroSkillExpandInfo info : mainHeroSkillExpandInfos) {
            if (info.getInitLevel() > 0) {
                MyMainHeroSkillBaseDto myMainHeroSkillBaseDto = new MyMainHeroSkillBaseDto();
                myMainHeroSkillBaseDto.setUseridUser(userId);
                myMainHeroSkillBaseDto.setBaseSkillid(info.getId());
                myMainHeroSkillBaseDto.setActivatedSkillid(info.getId());
                myMainHeroSkillBaseDto.setLevel(info.getInitLevel());
                MyMainHeroSkill myMainHeroSkill = myMainHeroSkillBaseDto.ToEntity();
                mainHerosSkills.add(myMainHeroSkill);
            }
        }
        myMainHeroSkillRepository.saveAll(mainHerosSkills);
        return mainHerosSkills;
    }
    // 소지품 인벤토리 기본템 생성
    List<BelongingInventory> createMyBelongingInventory(Long userId) {
        List<BelongingInventory> belongingInventories = new ArrayList<>();
        List<ItemType> itemTypes = itemTypeRepository.findAll();

//        BelongingInventory belongingInventory = BelongingInventory.builder().useridUser(userId)
//                .itemType(itemTypes.get(2)).itemId(1).count(1).build();
        BelongingInventory belongingInventory = BelongingInventory.builder().useridUser(userId)
                .itemType(itemTypes.get(3)).itemId(1).count(10).build();
        belongingInventory = belongingInventoryRepository.save(belongingInventory);

        belongingInventories.add(belongingInventory);
        return belongingInventories;
    }
    //영웅 장비 덱 정보 생성
    public MyEquipmentDeck CreateMyEquipmentDeck(Long userId) {
        MyEquipmentDeckDto myEquipmentDeckDto = new MyEquipmentDeckDto();
        myEquipmentDeckDto.setUserIdUser(userId);
        myEquipmentDeckDto.setFirstDeckWeaponInventoryId(0L);
        myEquipmentDeckDto.setFirstDeckArmorInventoryId(0L);
        myEquipmentDeckDto.setFirstDeckHelmetInventoryId(0L);
        myEquipmentDeckDto.setFirstDeckAccessoryInventoryId(0L);
        myEquipmentDeckDto.setSecondDeckWeaponInventoryId(0L);
        myEquipmentDeckDto.setSecondDeckArmorInventoryId(0L);
        myEquipmentDeckDto.setSecondDeckHelmetInventoryId(0L);
        myEquipmentDeckDto.setSecondDeckAccessoryInventoryId(0L);
        myEquipmentDeckDto.setThirdDeckWeaponInventoryId(0L);
        myEquipmentDeckDto.setThirdDeckArmorInventoryId(0L);
        myEquipmentDeckDto.setThirdDeckHelmetInventoryId(0L);
        myEquipmentDeckDto.setThirdDeckAccessoryInventoryId(0L);
        MyEquipmentDeck deck = myEquipmentDeckDto.ToEntity();
        return myEquipmentDeckRepository.save(deck);
    }

    //장비 제작 슬롯 정보 생성 기본으로 하나 줄것인지 튜터리얼에서 오픈할지 결정
    MyProductionSlot createMyProductionSlot(Long userId) {
        MyProductionSlotDto myProductionSlotDto = new MyProductionSlotDto();
        myProductionSlotDto.setUserIdUser(userId);
        myProductionSlotDto.setItemId(0);
        myProductionSlotDto.setSlotNo(1);
        myProductionSlotDto.setState(0);
        myProductionSlotDto.setReduceSecondFromAD(0);
        myProductionSlotDto.setProductionStartTime(LocalDateTime.now());
        MyProductionSlot myProductionSlot = myProductionSlotDto.ToEntity();
        return myProductionSlotRepository.save(myProductionSlot);
    }
    //장비 제작 숙련도 정보 생성
    MyProductionMastery createMyProductionMastery(Long userId) {
        MyProductionMasteryDto myProductionMasteryDto = new MyProductionMasteryDto();
        myProductionMasteryDto.setUserIdUser(userId);
        MyProductionMastery myProductionMastery = myProductionMasteryDto.ToEntity();
        return myProductionMasteryRepository.save(myProductionMastery);
    }
    //최초 유저에게 강철검 기본 제공
    List<HeroEquipmentInventory> createBaseWeapon(Long userId) {
        List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
        HeroEquipmentsTable firstWeaponInfo = heroEquipmentsTableList.get(161);
        HeroEquipmentsTable firstArmorInfo = heroEquipmentsTableList.get(162);
        HeroEquipmentsTable firstHelmetInfo = heroEquipmentsTableList.get(74);
        HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
        List<HeroEquipmentInventory> generatedItemList = new ArrayList<>();
        generatedItemList.add(EquipmentCalculate.CreateFirstWeapon(userId, firstWeaponInfo, classValues, null));
        generatedItemList.add(EquipmentCalculate.CreateFirstWeapon(userId, firstArmorInfo, classValues, null));
        generatedItemList.add(EquipmentCalculate.CreateFirstWeapon(userId, firstHelmetInfo, classValues, null));
        generatedItemList = heroEquipmentInventoryRepository.saveAll(generatedItemList);
        return generatedItemList;
    }
    //최초 유저 StageSaveData 생성
    MyChapterSaveData createChapterSaveData(Long userId) {

        List<InitJsonDatasForFirstUser> initJsonDatasForFirstUserList = gameDataTableService.InitJsonDatasForFirstUserList();
        InitJsonDatasForFirstUser jsonDatasForFirstUser = initJsonDatasForFirstUserList.stream()
                .filter(a -> a.getId() == 10)
                .findAny()
                .orElse(null);
        if(jsonDatasForFirstUser == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ChapterSaveData Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: ChapterSaveData Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        MyChapterSaveDataDto myChapterSaveDataDto = new MyChapterSaveDataDto();
        myChapterSaveDataDto.setSaveDataValue(jsonDatasForFirstUser.getInitJson());
        myChapterSaveDataDto.setUseridUser(userId);

        return myChapterSaveDataDto.ToEntity();
    }
    //최초 유저 MyVisitCompanionInfo 생성
    public MyVisitCompanionInfo createMyVisitCompanionInfoData(Long userId) {
        String visitCompanionInfo = "{\"visitList\":[]}";
        MyVisitCompanionInfoDto myVisitCompanionInfoDto = new MyVisitCompanionInfoDto();
        myVisitCompanionInfoDto.setUseridUser(userId);
        myVisitCompanionInfoDto.setVisitCompanionInfo(visitCompanionInfo);

        return myVisitCompanionInfoDto.ToEntity();
    }
    //최초 유저 MyGiftInventory 생성
    public MyGiftInventory createMyGiftInventory(Long userId) {
        List<GiftTable> giftTableList = gameDataTableService.GiftsTableList();
        GiftItemDtosList giftItemDtosList = new GiftItemDtosList();
        giftItemDtosList.giftItemDtoList = new ArrayList<>();
        for(GiftTable giftTable : giftTableList) {
            GiftItemDtosList.GiftItemDto giftItemDto = new GiftItemDtosList.GiftItemDto(giftTable.getCode(), 0);
            giftItemDtosList.giftItemDtoList.add(giftItemDto);
        }
        String myGiftInventoryInfo = JsonStringHerlper.WriteValueAsStringFromData(giftItemDtosList);
        MyGiftInventoryDto myGiftInventoryDto = new MyGiftInventoryDto();
        myGiftInventoryDto.setUseridUser(userId);
        myGiftInventoryDto.setInventoryInfos(myGiftInventoryInfo);
        return myGiftInventoryDto.ToEntity();
    }
    //최초 유저 동료들 링크포스 정보 생성
    public MyLinkforceInfo createMyLinkforceInfo(Long userId) {
        MyLinkforceInfoDto myLinkforceInfoDto = new MyLinkforceInfoDto();
        myLinkforceInfoDto.setUseridUser(userId);
        List<InitJsonDatasForFirstUser> initJsonDatasForFirstUserList = gameDataTableService.InitJsonDatasForFirstUserList();
        InitJsonDatasForFirstUser linkforceJson = initJsonDatasForFirstUserList.stream()
                .filter(a -> a.getId() == 1)
                .findAny()
                .orElse(null);
        if(linkforceJson == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: linkforceJson Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: linkforceJson Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        myLinkforceInfoDto.setJson_LinkforceInfos(linkforceJson.getInitJson());
        return myLinkforceInfoDto.ToEntity();
    }
    //최초 유저 동료들 링크웨폰 정보 생성
    public MyLinkweaponInfo createMyLinkweaponInfo(Long userId) {
        MyLinkweaponInfoDto myLinkweaponInfoDto = new MyLinkweaponInfoDto();
        myLinkweaponInfoDto.setUseridUser(userId);
        List<InitJsonDatasForFirstUser> initJsonDatasForFirstUserList = gameDataTableService.InitJsonDatasForFirstUserList();
        InitJsonDatasForFirstUser linkweaponJson = initJsonDatasForFirstUserList.stream()
                .filter(a -> a.getId() == 2)
                .findAny()
                .orElse(null);
        if(linkweaponJson == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: linkweaponJson Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: linkweaponJson Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        myLinkweaponInfoDto.setJson_LinkweaponRevolution(linkweaponJson.getInitJson());
        return myLinkweaponInfoDto.ToEntity();
    }
    //최초 유저 동료들 코스튬 정보 생성
    public MyCostumeInventory createMyCostumeInventory(Long userId) {
        MyCostumeInventoryDto myCostumeInventoryDto = new MyCostumeInventoryDto();
        myCostumeInventoryDto.setUseridUser(userId);
        List<InitJsonDatasForFirstUser> initJsonDatasForFirstUserList = gameDataTableService.InitJsonDatasForFirstUserList();
        InitJsonDatasForFirstUser costumeInventoryJson = initJsonDatasForFirstUserList.stream()
                .filter(a -> a.getId() == 3)
                .findAny()
                .orElse(null);
        if(costumeInventoryJson == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myCostumeInventoryJson Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myCostumeInventoryJson Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        myCostumeInventoryDto.setJson_CostumeInventory(costumeInventoryJson.getInitJson());
        return myCostumeInventoryDto.ToEntity();
    }
    //최초 유저 필드데이터 정보 생성
    public MyFieldSaveData createMyFieldSaveData(Long userId) {
        MyFieldSaveDataDto myFieldSaveDataDto = new MyFieldSaveDataDto();
        myFieldSaveDataDto.setUseridUser(userId);
        List<InitJsonDatasForFirstUser> initJsonDatasForFirstUserList = gameDataTableService.InitJsonDatasForFirstUserList();
        InitJsonDatasForFirstUser myFieldSaveDataJson = initJsonDatasForFirstUserList.stream()
                .filter(a -> a.getId() == 8)
                .findAny()
                .orElse(null);
        if(myFieldSaveDataJson == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyFieldSaveDataJson Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyFieldSaveDataJson Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }


        FieldSaveDataDto fieldSaveDataDto = JsonStringHerlper.ReadValueFromJson(myFieldSaveDataJson.getInitJson(), FieldSaveDataDto.class);
        CommonVariableTable commonVariableTable = gameDataTableService.CommonVariableTable().get(0);
        fieldSaveDataDto.userFieldObjectInfoListDto.Reset(commonVariableTable.getFieldSpecialObjectPercent());

        String json_saveDataValue = JsonStringHerlper.WriteValueAsStringFromData(fieldSaveDataDto);
        myFieldSaveDataDto.setJson_saveDataValue(json_saveDataValue);
        myFieldSaveDataDto.setLastClearTime(LocalDateTime.now().minusDays(1));

        return myFieldSaveDataDto.ToEntity();
    }
    //최초 유저 고대드래곤 시즌 정보 생성
    public MyAncientDragonExpandSaveData createMyAncientDragonExpandSaveData(Long userId) {
        MyAncientDragonExpandSaveDataDto myAncientDragonExpandSaveDataDto = new MyAncientDragonExpandSaveDataDto();
        myAncientDragonExpandSaveDataDto.setUseridUser(userId);

        List<InitJsonDatasForFirstUser> initJsonDatasForFirstUserList = gameDataTableService.InitJsonDatasForFirstUserList();
        InitJsonDatasForFirstUser jsonDatasForFirstUser = initJsonDatasForFirstUserList.stream()
                .filter(a -> a.getId() == 4)
                .findAny()
                .orElse(null);
        if(jsonDatasForFirstUser == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyAncientDragonExpandSaveDataJson Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyAncientDragonExpandSaveDataJson Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        myAncientDragonExpandSaveDataDto.setJson_saveDataValue(jsonDatasForFirstUser.getInitJson());
        myAncientDragonExpandSaveDataDto.setSeasonStartTime(LocalDateTime.now().minusDays(10));
        MyAncientDragonExpandSaveData myAncientDragonExpandSaveData = myAncientDragonExpandSaveDataDto.ToEntity();
        myAncientDragonExpandSaveData.IsResetSeasonStartTime();
        //myAncientDragonExpandSaveDataDto.setSeasonStartTime();
        return myAncientDragonExpandSaveData;
    }
    //최초 유저 아레나 시즌 정보 생성
    public MyArenaSeasonSaveData createMyArenaSeasonSaveData(Long userId) {
        MyArenaSeasonSaveDataDto myArenaSeasonSaveDataDto = new MyArenaSeasonSaveDataDto();
        myArenaSeasonSaveDataDto.setUseridUser(userId);
        myArenaSeasonSaveDataDto.setSeasonRank(0L);
        myArenaSeasonSaveDataDto.setScore(0L);
        //21은 티어 테이블에 없음. 최초 유저는 티어 자체가 없는것으로 판단하고 프로세싱 진행
        myArenaSeasonSaveDataDto.setHighestRankingtiertable_id(21);
        myArenaSeasonSaveDataDto.setRankingtiertable_id(21);
        myArenaSeasonSaveDataDto.setWinCount(0);
        myArenaSeasonSaveDataDto.setLoseCount(0);
        myArenaSeasonSaveDataDto.setContinueWinCount(0);
        myArenaSeasonSaveDataDto.setPlayCountPerDay(0);
        myArenaSeasonSaveDataDto.setPlayCountPerDayMax(0);
        myArenaSeasonSaveDataDto.setReceiveableDailyReward(false);
        myArenaSeasonSaveDataDto.setReceiveableTierUpReward(false);
        myArenaSeasonSaveDataDto.setReceiveableHallofHonorReward(false);
        myArenaSeasonSaveDataDto.setPreviousTierId(20);
        myArenaSeasonSaveDataDto.setChangedTierId(20);
        myArenaSeasonSaveDataDto.setChangedSeason(false);

        //최신 시즌 정보 아이디
        long seasonInfosCount = arenaSeasonInfoDataRepository.count();
        int nowSeasonId = (int)seasonInfosCount;
        ArenaSeasonInfoData arenaSeasonInfoData = arenaSeasonInfoDataRepository.findByNowSeasonNo(nowSeasonId)
                .orElse(null);
        if(arenaSeasonInfoData == null) {
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ArenaSeasonInfoData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: ArenaSeasonInfoData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        myArenaSeasonSaveDataDto.setSeasonStartTime(arenaSeasonInfoData.getSeasonStartTime());
        myArenaSeasonSaveDataDto.setSeasonEndTime(arenaSeasonInfoData.getSeasonEndTime());
        MyArenaSeasonSaveData myArenaSeasonSaveData = myArenaSeasonSaveDataDto.ToEntity();

        return myArenaSeasonSaveData;
    }
    //최초 유저 탐색대 정보 생성
    public MyExpeditionData createMyExpeditionData(Long userId) {
        MyExpeditionDataDto myExpeditionDataDto = new MyExpeditionDataDto();
        myExpeditionDataDto.setUseridUser(userId);
        myExpeditionDataDto.setBoostCountForDay(0);
        myExpeditionDataDto.setBoostCompleteCount(0);
        myExpeditionDataDto.setUsingBoost(false);
        myExpeditionDataDto.setRemainBoostTime(0);
        LocalDateTime localDateTime = LocalDateTime.now();
        myExpeditionDataDto.setBoostStartTime(localDateTime);
        myExpeditionDataDto.setLastExpeditionFinishTime(localDateTime);
        myExpeditionDataDto.setBoostCountForDayClearTime(localDateTime);

        return myExpeditionDataDto.ToEntity();
    }
    //최초 유저 영웅의 탑 정보 생성
    public MyHeroTowerExpandSaveData createMyHeroTowerExpandSaveData(Long userId) {
        MyHeroTowerExpandSaveDataDto myHeroTowerExpandSaveDataDto = new MyHeroTowerExpandSaveDataDto();
        myHeroTowerExpandSaveDataDto.setUseridUser(userId);
        List<InitJsonDatasForFirstUser> initJsonDatasForFirstUserList = gameDataTableService.InitJsonDatasForFirstUserList();
        InitJsonDatasForFirstUser jsonDatasForFirstUser = initJsonDatasForFirstUserList.stream()
                .filter(a -> a.getId() == 5)
                .findAny()
                .orElse(null);
        if(jsonDatasForFirstUser == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyHeroTowerExpandSaveDataJson Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyHeroTowerExpandSaveDataJson Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        myHeroTowerExpandSaveDataDto.setJson_saveDataValue(jsonDatasForFirstUser.getInitJson());

        jsonDatasForFirstUser = initJsonDatasForFirstUserList.stream()
                .filter(a -> a.getId() == 6)
                .findAny()
                .orElse(null);
        if(jsonDatasForFirstUser == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyHeroTowerExpandSaveDataJson Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyHeroTowerExpandSaveDataJson Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        myHeroTowerExpandSaveDataDto.setJson_ExpandInfo(jsonDatasForFirstUser.getInitJson());
        myHeroTowerExpandSaveDataDto.setSeasonStartTime(LocalDateTime.now().minusDays(10));
        return myHeroTowerExpandSaveDataDto.ToEntity();
    }
    //최초 유저 시련의 던전 정보 생성
    public MyOrdealDungeonExpandSaveData createMyOrdealDungeonExpandSaveData(Long userId) {
        MyOrdealDungeonExpandSaveDataDto myOrdealDungeonExpandSaveDataDto = new MyOrdealDungeonExpandSaveDataDto();
        myOrdealDungeonExpandSaveDataDto.setUseridUser(userId);
        List<InitJsonDatasForFirstUser> initJsonDatasForFirstUserList = gameDataTableService.InitJsonDatasForFirstUserList();
        InitJsonDatasForFirstUser jsonDatasForFirstUser = initJsonDatasForFirstUserList.stream()
                .filter(a -> a.getId() == 7)
                .findAny()
                .orElse(null);
        if(jsonDatasForFirstUser == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: initJsonDatasForFirstUserList Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: initJsonDatasForFirstUserList Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        myOrdealDungeonExpandSaveDataDto.setJson_saveDataValue(jsonDatasForFirstUser.getInitJson());
        myOrdealDungeonExpandSaveDataDto.setBonusRemainCount(6);
        myOrdealDungeonExpandSaveDataDto.setSeasonStartTime(LocalDateTime.now().minusDays(10));
        return myOrdealDungeonExpandSaveDataDto.ToEntity();
    }
    //최초 유저 가차마일리지 정보 생성
    public GotchaMileage createGotchaMileage(Long userId) {
        GotchaMileageDto gotchaMileageDto = new GotchaMileageDto();
        gotchaMileageDto.setUseridUser(userId);
        return gotchaMileageDto.ToEntity();
    }
    //최초 유저의 동료 별점 셋팅 테이블 생성
    public CompanionStarPointsAverageTable createCompanionStarPointsAverage(Long userId){
        InitJsonDatasForFirstUser starPointsAverageDtoList = gameDataTableService.InitJsonDatasForFirstUserList().get(10);
        CompanionStarPointsAverageTableDto companionStarPointsAverageTableDto = new CompanionStarPointsAverageTableDto();
        companionStarPointsAverageTableDto.setUseridUser(userId);
        companionStarPointsAverageTableDto.setJson_StarPointsAverage(starPointsAverageDtoList.getInitJson());
        return companionStarPointsAverageTableDto.ToEntity();
    }

    public MyForTest createMyForTest(Long userId)
    {
        MyForTestDto myForTestDto = new MyForTestDto();
        myForTestDto.setUseridUser(userId);
        myForTestDto.setMyStringValue("hello");
        myForTestDto.setMyIntValue(10);
        myForTestDto.setMyFloatValue(1.0f);
        myForTestDto.setMyBooleanValue(false);

        return myForTestDto.ToEntity();
    }

    //최초 유저 상점 데이터 셋팅
    public MyShopInfo createMyShopInfo(Long userId) {
        InitJsonDatasForFirstUser myShopJson = gameDataTableService.InitJsonDatasForFirstUserList().get(11);
        MyShopInfoDto myShopInfoDto = new MyShopInfoDto();
        myShopInfoDto.setUseridUser(userId);
        myShopInfoDto.setJson_myShopInfos(myShopJson.getInitJson());
        LocalDateTime now = LocalDateTime.now();
        now = LocalDateTime.of(now.minusHours(5).toLocalDate(), LocalTime.of(5,0,0));
        LocalDateTime shopScheduleStartTime = LocalDateTime.of(now.toLocalDate(), LocalTime.of(5,0,0));
        shopScheduleStartTime = shopScheduleStartTime.minusDays(1);
        myShopInfoDto.setScheduleStartTime(shopScheduleStartTime);
        //패키지 스케쥴 설정
        LocalDateTime dailyPackageScheduleStartTime = shopScheduleStartTime;
        myShopInfoDto.setDailyPackageScheduleStartTime(dailyPackageScheduleStartTime.minusDays(1));
        int dayOfWeek = now.getDayOfWeek().getValue();
        int gap = dayOfWeek - 1;
        LocalDateTime weeklyPackageScheduleStartTime = now.minusDays(gap);
        weeklyPackageScheduleStartTime = LocalDateTime.of(weeklyPackageScheduleStartTime.toLocalDate(), LocalTime.of(5,0,0));
        myShopInfoDto.setWeeklyPackageScheduleStartTime(weeklyPackageScheduleStartTime.minusDays(7));
        LocalDateTime monthlyPackageScheduleStartTime =  now.with(TemporalAdjusters.firstDayOfMonth());
        monthlyPackageScheduleStartTime = LocalDateTime.of(monthlyPackageScheduleStartTime.toLocalDate(), LocalTime.of(5,0,0));
        myShopInfoDto.setMonthlyPackageScheduleStartTime(monthlyPackageScheduleStartTime.minusMonths(1));

        return myShopInfoDto.ToEntity();
    }
    //최초 유저 업적 데이터 셋팅
    public MyMissionsData createMyMissionData(Long userId) {

        MissionsDataDto missionsDataDto = new MissionsDataDto();
        missionsDataDto.dailyMissionsData = new ArrayList<>();
        missionsDataDto.weeklyMissionsData = new ArrayList<>();
        missionsDataDto.questMissionsData = new ArrayList<>();
        missionsDataDto.dailyRewardInfo = new ArrayList<>();
        missionsDataDto.weeklyRewardInfo = new ArrayList<>();

        //일일 업적 셋팅
        List<DailyMissionTable> dailyMissionTableList = gameDataTableService.DailyMissionTableList();
        for(DailyMissionTable dailyMissionTable : dailyMissionTableList){
            MissionsDataDto.MissionData missionData = new MissionsDataDto.MissionData();
            missionData.code = dailyMissionTable.getCode();
            missionData.rewardReceived = false;
            missionData.goalCount = dailyMissionTable.getGoalCount();
            missionData.alreadyOpenStepMax = 0;
            missionsDataDto.dailyMissionsData.add(missionData);
        }
        //주간 업적 셋팅
        List<WeeklyMissionTable> weeklyMissionTableList = gameDataTableService.WeeklyMissionTableList();
        for(WeeklyMissionTable weeklyMissionTable : weeklyMissionTableList) {
            MissionsDataDto.MissionData missionData = new MissionsDataDto.MissionData();
            missionData.code = weeklyMissionTable.getCode();
            missionData.rewardReceived = false;
            missionData.goalCount = weeklyMissionTable.getGoalCount();
            missionData.alreadyOpenStepMax = 0;
            missionsDataDto.weeklyMissionsData.add(missionData);
        }
        //퀘스트 업적 셋팅
        List<QuestMissionTable> questMissionTableList = gameDataTableService.QuestMissionTableList();
        String previousQuestCodeFront = " ";
        for(QuestMissionTable questMissionTable : questMissionTableList) {

            String fullCode = questMissionTable.getCode();
//            if(fullCode.contains(previousQuestCodeFront))
//                continue;
            previousQuestCodeFront = fullCode.split("-")[0];

            MissionsDataDto.MissionData missionData = new MissionsDataDto.MissionData();
            missionData.code = fullCode;
            missionData.rewardReceived = false;
            missionData.goalCount = questMissionTable.getGoalCount();
            missionData.alreadyOpenStepMax = 0;
            missionsDataDto.questMissionsData.add(missionData);
        }
        //일일업적 리워드 정보 셋팅
        List<DailyMissionRewardTable> dailyMissionRewardTableList = gameDataTableService.DailyMissionRewardTableList();
        for(DailyMissionRewardTable dailyMissionRewardTable : dailyMissionRewardTableList) {
            MissionsDataDto.RewardInfoData rewardInfoData = new MissionsDataDto.RewardInfoData();
            rewardInfoData.code = dailyMissionRewardTable.getCode();
            rewardInfoData.rewardReceived = false;
            missionsDataDto.dailyRewardInfo.add(rewardInfoData);
        }
        missionsDataDto.dailyRewardPoint = 0;
        //주간업적 리워드 정보 셋팅
        List<WeeklyMissionRewardTable> weeklyMissionRewardTableList = gameDataTableService.WeeklyMissionRewardTableList();
        for(WeeklyMissionRewardTable weeklyMissionRewardTable : weeklyMissionRewardTableList){
            MissionsDataDto.RewardInfoData rewardInfoData = new MissionsDataDto.RewardInfoData();
            rewardInfoData.code = weeklyMissionRewardTable.getCode();
            rewardInfoData.rewardReceived = false;
            missionsDataDto.weeklyRewardInfo.add(rewardInfoData);
        }
        missionsDataDto.weeklyRewardPoint = 0;

        MyMissionsDataDto myMissionsDataDto = new MyMissionsDataDto();
        String json_MissionData = JsonStringHerlper.WriteValueAsStringFromData(missionsDataDto);
        myMissionsDataDto.setJson_saveDataValue(json_MissionData);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thisMonday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        now = LocalDateTime.of(now.minusHours(5).toLocalDate(), LocalTime.of(5,0,0));
        myMissionsDataDto.setLastDailyMissionClearTime(now);
        thisMonday = LocalDateTime.of(thisMonday.toLocalDate(), LocalTime.of(5,0,0));
        myMissionsDataDto.setLastWeeklyMissionClearTime(thisMonday);
        myMissionsDataDto.setUseridUser(userId);
        return myMissionsDataDto.ToEntity();
    }
    //최초 유저 이터널 패스 정보 데이터 셋팅
    public MyEternalPass createMyEternalPassTable(Long userId) {
        MyEternalPassInfoDataDto myEternalPassInfoDataDto = new MyEternalPassInfoDataDto();
        myEternalPassInfoDataDto.setUseridUser(userId);
        InitJsonDatasForFirstUser myInfiniteTowerSaveDataJson = gameDataTableService.InitJsonDatasForFirstUserList().get(13);
        myEternalPassInfoDataDto.setJson_myEternalPassInfo(myInfiniteTowerSaveDataJson.getInitJson());
        return myEternalPassInfoDataDto.ToEntity();
    }
    //최초 유저 패스 업적 데이터 셋팅
    public MyEternalPassMissionsData createMyEternalPassMissionData(Long userId) {

        EventMissionDataDto eventMissionDataDto = new EventMissionDataDto();

        eventMissionDataDto.dailyMissionsData = new ArrayList<>();
        eventMissionDataDto.weeklyMissionsData = new ArrayList<>();
        eventMissionDataDto.questMissionsData = new ArrayList<>();

        //일일 업적 셋팅
        List<EternalPassDailyMissionTable> dailyMissionTableList = gameDataTableService.EternalPassDailyMissionTableList();
        for(EternalPassDailyMissionTable dailyMissionTable : dailyMissionTableList){
            MissionsDataDto.MissionData missionData = new MissionsDataDto.MissionData();
            missionData.code = dailyMissionTable.getCode();
            missionData.rewardReceived = false;
            missionData.goalCount = dailyMissionTable.getGoalCount();
            missionData.alreadyOpenStepMax = 0;
            missionData.success = dailyMissionTable.getCode().equals("daily_mission_001-1");
            missionData.actionCount = dailyMissionTable.getCode().equals("daily_mission_001-1")?1:0;
            eventMissionDataDto.dailyMissionsData.add(missionData);
        }
        //주간 업적 셋팅
        List<EternalPassWeekMissionTable> weeklyMissionTableList = gameDataTableService.EternalPassWeekMissionTableList();
        for(EternalPassWeekMissionTable weeklyMissionTable : weeklyMissionTableList) {
            MissionsDataDto.MissionData missionData = new MissionsDataDto.MissionData();
            missionData.code = weeklyMissionTable.getCode();
            missionData.rewardReceived = false;
            missionData.goalCount = weeklyMissionTable.getGoalCount();
            missionData.alreadyOpenStepMax = 0;
            eventMissionDataDto.weeklyMissionsData.add(missionData);
        }
        //퀘스트 업적 셋팅
        List<EternalPassQuestMissionTable> questMissionTableList = gameDataTableService.EternalPassQuestMissionTableList();
        String previousQuestCodeFront = " ";
        for(EternalPassQuestMissionTable questMissionTable : questMissionTableList) {

            String fullCode = questMissionTable.getCode();
//            if(fullCode.contains(previousQuestCodeFront))
//                continue;
            previousQuestCodeFront = fullCode.split("-")[0];

            MissionsDataDto.MissionData missionData = new MissionsDataDto.MissionData();
            missionData.code = fullCode;
            missionData.rewardReceived = false;
            missionData.goalCount = questMissionTable.getGoalCount();
            missionData.alreadyOpenStepMax = 0;
            missionData.actionCount = questMissionTable.getCode().equals("quest_mission_001-1")?1:0;
            eventMissionDataDto.questMissionsData.add(missionData);
        }

        MyEternalPassMissionsDataDto myEternalPassMissionsData = new MyEternalPassMissionsDataDto();
        String json_MissionData = JsonStringHerlper.WriteValueAsStringFromData(eventMissionDataDto);
        myEternalPassMissionsData.setJson_saveDataValue(json_MissionData);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thisMonday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        now = LocalDateTime.of(now.minusHours(5).toLocalDate(), LocalTime.of(5,0,0));
        myEternalPassMissionsData.setLastDailyMissionClearTime(now);
        thisMonday = LocalDateTime.of(thisMonday.toLocalDate(), LocalTime.of(5,0,0));
        myEternalPassMissionsData.setLastWeeklyMissionClearTime(thisMonday);
        myEternalPassMissionsData.setUseridUser(userId);
        return myEternalPassMissionsData.ToEntity();
    }
    //최초 유저 누적 달성 이벤트 미션 정보 셋팅
    public MyAchieveEventMissionsData createMyAchieveEventMisstionData(Long userId) {
        AchieveEventMissionDataDto achieveEventMissionDataDto = new AchieveEventMissionDataDto();

        achieveEventMissionDataDto.levelMissionsData = new ArrayList<>();

        List<AchieveEventTable> achieveEventTableList = gameDataTableService.AchieveEventTableList();
        for(AchieveEventTable temp: achieveEventTableList) {
            MissionsDataDto.MissionData missionData = new MissionsDataDto.MissionData();
            missionData.code = temp.getCode();
            missionData.rewardReceived = false;
            missionData.goalCount = temp.getGoalCount();
            missionData.alreadyOpenStepMax = 0;
            achieveEventMissionDataDto.levelMissionsData.add(missionData);
        }
        MyAchieveEventMissionsDataDto myAchieveEventMissionsDataDto = new MyAchieveEventMissionsDataDto();
        String json_MissionData = JsonStringHerlper.WriteValueAsStringFromData(achieveEventMissionDataDto);
        myAchieveEventMissionsDataDto.setJson_saveDataValue(json_MissionData);
        myAchieveEventMissionsDataDto.setUseridUser(userId);
        return myAchieveEventMissionsDataDto.ToEntity();
    }

    //최초 유저 프로필 정보 및 프레임 미션 정보 셋팅
    public MyProfileData createMyProfileData(Long userId) {
        MyProfileMissionDataDto myProfileMissionDataDto = new MyProfileMissionDataDto();

        myProfileMissionDataDto.profileFrameMissionsData = new ArrayList<>();

        List<ProfileFrameMissionTable> profileFrameMissionTableList = gameDataTableService.ProfileFrameMissionTableList();
        for(ProfileFrameMissionTable temp : profileFrameMissionTableList) {
            MissionsDataDto.MissionData missionData = new MissionsDataDto.MissionData();
            missionData.code = temp.getCode();
            missionData.rewardReceived = false;
            missionData.goalCount = temp.getGoalCount();
            missionData.alreadyOpenStepMax = 0;
            myProfileMissionDataDto.profileFrameMissionsData.add(missionData);
        }
        String json_missionData = JsonStringHerlper.WriteValueAsStringFromData(myProfileMissionDataDto);

        ProfileDataDto profileDataDto = new ProfileDataDto();
        profileDataDto.setProfileHero("hero");
        profileDataDto.setProfileFrame(1);
        profileDataDto.profileFrameList = new ArrayList<>();
        List<ProfileFrameTable> profileFrameTableList = gameDataTableService.ProfileFrameTableList();
        for(ProfileFrameTable temp : profileFrameTableList) {
            ProfileDataDto.ProfileFrame profileFrame = new ProfileDataDto.ProfileFrame();
            boolean firstFrame = temp.getId() == 1;
            profileFrame.SetProfileFrame(temp.getId(), temp.getCode(), firstFrame, firstFrame);
            profileDataDto.profileFrameList.add(profileFrame);
        }
        String json_saveDataValue = JsonStringHerlper.WriteValueAsStringFromData(profileDataDto);

        MyProfileDataDto myProfileDataDto = new MyProfileDataDto();
        myProfileDataDto.setUseridUser(userId);
        myProfileDataDto.setJson_missionData(json_missionData);
        myProfileDataDto.setJson_saveDataValue(json_saveDataValue);
        return myProfileDataDto.ToEntity();
    }

    //최초 유저 메일함 셋팅
    public MyMailBox createMyMailBox(Long userId) {
        MailBoxDto mailBoxDto = new MailBoxDto();
        mailBoxDto.mailBoxInfoList = new ArrayList<>();
        String json_myMailBoxInfo = JsonStringHerlper.WriteValueAsStringFromData(mailBoxDto);

        MyMailBoxDto myMailBoxDto = new MyMailBoxDto();
        myMailBoxDto.setUseridUser(userId);
        myMailBoxDto.setJson_myMailBoxInfo(json_myMailBoxInfo);

        return myMailBoxDto.ToEntity();
    }
    //최초 유저 광고보기 셋팅
    public MyTodayViewingTable createMyTodayViewingTable(Long userId) {
        MyTodayViewingDto myTodayViewingDto = new MyTodayViewingDto();
        myTodayViewingDto.setUseridUser(userId);
        myTodayViewingDto.setLastViewing(LocalDateTime.now());
        myTodayViewingDto.setTodayViewingCount(0);
        return myTodayViewingDto.ToEntity();
    }
    //최초 유저 무한의 탑 셋팅
    public MyInfiniteTowerSaveData createMyInfiniteTowerSaveData(Long userId) {
        MyInfiniteTowerSaveDataDto myInfiniteTowerSaveDataDto  = new MyInfiniteTowerSaveDataDto();
        myInfiniteTowerSaveDataDto.setArrivedTopFloor(1);
        myInfiniteTowerSaveDataDto.setUseridUser(userId);
        InitJsonDatasForFirstUser myInfiniteTowerSaveDataJson = gameDataTableService.InitJsonDatasForFirstUserList().get(12);
        MyInfiniteTowerRewardReceivedInfosDto myInfiniteTowerRewardReceivedInfosDto = JsonStringHerlper.ReadValueFromJson(myInfiniteTowerSaveDataJson.getInitJson(), MyInfiniteTowerRewardReceivedInfosDto.class);
        myInfiniteTowerSaveDataDto.setMyInfiniteTowerRewardReceivedInfosDto(myInfiniteTowerRewardReceivedInfosDto);
        return myInfiniteTowerSaveDataDto.ToEntity();
    }
    //최초 유저 출석정보 셋팅
    public MyAttendanceData createMyAttendanceData(Long userId) {
        LocalDateTime setTime = LocalDateTime.of(LocalDateTime.now().minusHours(5).toLocalDate(), LocalTime.of(5,0,0));
        MyAttendanceDataDto myAttendanceDataDto = new MyAttendanceDataDto();
        myAttendanceDataDto.SetMyAttendanceDataDto(userId, 1, setTime, true);
        return myAttendanceDataDto.ToEntity();
    }
}