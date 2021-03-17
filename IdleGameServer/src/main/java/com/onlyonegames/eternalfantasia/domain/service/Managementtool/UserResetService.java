package com.onlyonegames.eternalfantasia.domain.service.Managementtool;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.GiftItemDto.GiftItemDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.MailDto.MailBoxDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyProductionSlotDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.*;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.*;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.*;
import com.onlyonegames.eternalfantasia.domain.model.entity.Gotcha.GotchaMileage;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.*;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mail.MyMailBox;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Shop.MyShopInfo;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.*;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.*;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.*;
import com.onlyonegames.eternalfantasia.domain.repository.Gotcha.GotchaMileageRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.*;
import com.onlyonegames.eternalfantasia.domain.repository.Mail.MyMailBoxRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Shop.MyShopInfoRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class UserResetService {
    private final UserRepository userRepository;
    private final MyCharactersRepository myCharactersRepository;
    private final GameDataTableService gameDataTableService;
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final MyProductionSlotRepository myProductionSlotRepository;
    private final MyProductionMasteryRepository myProductionMasteryRepository;
    private final ErrorLoggingService errorLoggingService;
    private final ItemTypeRepository itemTypeRepository;
    private final MyEquipmentDeckRepository myEquipmentDeckRepository;
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
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final MyMailBoxRepository myMailBoxRepository;
    private final TodayViewingTableRepository todayViewingTableRepository;
    private final MyInfiniteTowerSaveDataRepository myInfiniteTowerSaveDataRepository;
    private final ArenaSeasonInfoDataRepository arenaSeasonInfoDataRepository;
    public Map<String, Object> ResetUser(Long userId, Map<String, Object> map) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        user.InitData();
        List<herostable> herostableList = gameDataTableService.HerosTableList();
        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
        for(MyCharacters myCharacters:myCharactersList){
            herostable herostable = herostableList.stream().filter(i -> i.getCode().equals(myCharacters.getCodeHerostable())).findAny().orElse(null);
            if(herostable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: CodeHerostable Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: CodeHerostable Can't find", ResponseErrorCode.NOT_FIND_DATA);
            }
            myCharacters.ClearMyCharacters(herostable.getTier());
            if (herostable.getCode().equals("hero") || herostable.getCode().equals("cr_000") || herostable.getCode().equals("cr_004"))
                myCharacters.Gotcha();
        }
        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
        belongingInventoryRepository.deleteAll(belongingInventoryList);
        List<MyProductionSlot> myProductionSlotList = myProductionSlotRepository.findByUserIdUser(userId);
        myProductionSlotRepository.deleteAll(myProductionSlotList);
        MyProductionSlotDto myProductionSlotDto = new MyProductionSlotDto();
        myProductionSlotDto.setUserIdUser(userId);
        myProductionSlotDto.setItemId(0);
        myProductionSlotDto.setSlotNo(1);
        myProductionSlotDto.setState(0);
        myProductionSlotDto.setReduceSecondFromAD(0);
        myProductionSlotDto.setProductionStartTime(LocalDateTime.now());
        MyProductionSlot myProductionSlot = myProductionSlotDto.ToEntity();
        myProductionSlotRepository.save(myProductionSlot);
        MyProductionMastery myProductionMastery = myProductionMasteryRepository.findByUserIdUser(userId);
        myProductionMastery.Init();

        List<HeroEquipmentInventory> heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(userId);
        heroEquipmentInventoryRepository.deleteAll(heroEquipmentInventoryList);

        MyCharacters myCharacters = myCharactersList.stream().filter(i -> i.getCodeHerostable().equals("hero")).findAny().orElse(null);
        if(myCharacters == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyCharacters Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyCharacters Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyTeamInfo myTeamInfo = myTeamInfoRepository.findByUseridUser(userId);
        myTeamInfo.InitData(myCharacters.getId());

        List<InitJsonDatasForFirstUser> initJsonDatasForFirstUserList = gameDataTableService.InitJsonDatasForFirstUserList();

        //최초 유저 chaptersavedata 셋팅

        MyChapterSaveData myChapterSaveData = myChapterSaveDataRepository.findByUseridUser(userId).orElse(null);
        if(myChapterSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myChapterSaveDataRepository Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myChapterSaveDataRepository Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        InitJsonDatasForFirstUser chapterSaveData = initJsonDatasForFirstUserList.stream().filter(i -> i.getId()==10).findAny().orElse(null);
        if(chapterSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: InitJsonDatasForFirstUser Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: InitJsonDatasForFirstUser Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        myChapterSaveData.ResetSaveDataValue(chapterSaveData.getInitJson());
        myChapterSaveDataRepository.save(myChapterSaveData);
        //최초 유저 giftItemInventory 생성
        MyGiftInventory myGiftInventory = myGiftInventoryRepository.findByUseridUser(userId).orElse(null);
        if(myGiftInventory == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyGiftInventory Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyGiftInventory Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<GiftTable> giftTableList = gameDataTableService.GiftsTableList();
        GiftItemDtosList giftItemDtosList = new GiftItemDtosList();
        giftItemDtosList.giftItemDtoList = new ArrayList<>();
        for(GiftTable giftTable : giftTableList) {
            GiftItemDtosList.GiftItemDto giftItemDto = new GiftItemDtosList.GiftItemDto(giftTable.getCode(), 0);
            giftItemDtosList.giftItemDtoList.add(giftItemDto);
        }
        String myGiftInventoryInfo = JsonStringHerlper.WriteValueAsStringFromData(giftItemDtosList);
        myGiftInventory.ResetInventoryInfos(myGiftInventoryInfo);
        myGiftInventoryRepository.save(myGiftInventory);
        //최초 유저 링크포스트리정보 생성
        MyLinkforceInfo myLinkforceInfo = myLinkforceInfoRepository.findByUseridUser(userId).orElse(null);
        if(myLinkforceInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyLinkforceInfo Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyLinkforceInfo Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        InitJsonDatasForFirstUser linkforceInfo = initJsonDatasForFirstUserList.stream().filter(i -> i.getId()==1).findAny().orElse(null);
        if(linkforceInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: InitJsonDatasForFirstUser Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: InitJsonDatasForFirstUser Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        myLinkforceInfo.ResetLinkforceInfos(linkforceInfo.getInitJson());
        myLinkforceInfoRepository.save(myLinkforceInfo);
        //최초 유저 링크웨폰트리정보 생성
        MyLinkweaponInfo myLinkweaponInfo = myLinkweaponInfoRepository.findByUseridUser(userId).orElse(null);
        if(myLinkweaponInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyLinkweaponInfo Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyLinkweaponInfo Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        InitJsonDatasForFirstUser linkweaponInfo = initJsonDatasForFirstUserList.stream().filter(i -> i.getId()==2).findAny().orElse(null);
        if(linkweaponInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: InitJsonDatasForFirstUser Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: InitJsonDatasForFirstUser Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        myLinkweaponInfo.ResetLinkweaponRevolution(linkweaponInfo.getInitJson());
        myLinkweaponInfoRepository.save(myLinkweaponInfo);
        //최초 유저 동료 코스튬인벤토리정보 생성
        MyCostumeInventory myCostumeInventory = myCostumeInventoryRepository.findByUseridUser(userId).orElse(null);
        if(myCostumeInventory == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyCostumeInventory Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyCostumeInventory Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        InitJsonDatasForFirstUser costumeInventory = initJsonDatasForFirstUserList.stream().filter(i -> i.getId()==3).findAny().orElse(null);
        if(costumeInventory == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: InitJsonDatasForFirstUser Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: InitJsonDatasForFirstUser Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        myCostumeInventory.ResetCostumeInventory(costumeInventory.getInitJson());
        myCostumeInventoryRepository.save(myCostumeInventory);

        //최초 유저 필드데이터 정보 생성
        MyFieldSaveData myFieldSaveData = myFieldSaveDataRepository.findByUseridUser(userId).orElse(null);
        if(myFieldSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyFieldSaveData Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyFieldSaveData Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        InitJsonDatasForFirstUser fieldSaveData = initJsonDatasForFirstUserList.stream().filter(i -> i.getId()==8).findAny().orElse(null);
        if(fieldSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: InitJsonDatasForFirstUser Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: InitJsonDatasForFirstUser Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        myFieldSaveData.ResetSaveDataValue(fieldSaveData.getInitJson());
        myFieldSaveDataRepository.save(myFieldSaveData);
        //최초 유저 고대드래곤 시즌 정보 생성
        MyAncientDragonExpandSaveData myAncientDragonExpandSaveData = myAncientDragonExpandSaveDataRepository.findByUseridUser(userId).orElse(null);
        if(myAncientDragonExpandSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyAncientDragonExpandSaveData Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyAncientDragonExpandSaveData Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        InitJsonDatasForFirstUser ancientDragonExpandSaveData = initJsonDatasForFirstUserList.stream().filter(i -> i.getId()==4).findAny().orElse(null);
        if(ancientDragonExpandSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: InitJsonDatasForFirstUser Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: InitJsonDatasForFirstUser Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        myAncientDragonExpandSaveData.ResetSaveDataValue(ancientDragonExpandSaveData.getInitJson());
        myAncientDragonExpandSaveData.IsResetSeasonStartTime();
        myAncientDragonExpandSaveDataRepository.save(myAncientDragonExpandSaveData);
        //최초 유저 아레나 시즌 정보 생성
        MyArenaSeasonSaveData myArenaSeasonSaveData = myArenaSeasonSaveDataRepository.findByUseridUser(userId).orElse(null);
        if(myArenaSeasonSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyArenaSeasonSaveData Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyArenaSeasonSaveData Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        myArenaSeasonSaveData.InitData();
        //최신 시즌 정보 아이디
        long seasonInfosCount = arenaSeasonInfoDataRepository.count();
        int nowSeasonId = (int)seasonInfosCount;
        ArenaSeasonInfoData arenaSeasonInfoData = arenaSeasonInfoDataRepository.findByNowSeasonNo(nowSeasonId)
                .orElse(null);
        if(arenaSeasonInfoData == null) {
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ArenaSeasonInfoData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: ArenaSeasonInfoData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        myArenaSeasonSaveData.ResetSeasonSaveData();
        myArenaSeasonSaveData.ResetSeasonNo(nowSeasonId);
        myArenaSeasonSaveData.ResetSeasonStartTime(arenaSeasonInfoData.getSeasonStartTime());
        myArenaSeasonSaveData.ResetSeasonEndTime(arenaSeasonInfoData.getSeasonEndTime());
        myArenaSeasonSaveData.DailyTime();
        myArenaSeasonSaveDataRepository.save(myArenaSeasonSaveData);
        //최초 유저 탐험대 데이터 정보 생성
        MyExpeditionData myExpeditionData = myExpeditionDataRepository.findByUseridUser(userId).orElse(null);
        if(myExpeditionData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyExpeditionData Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyExpeditionData Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        myExpeditionData.InitData();
        myExpeditionDataRepository.save(myExpeditionData);
        //최초 유저 영웅의 탑 데이터 정보 생성
        MyHeroTowerExpandSaveData myHeroTowerExpandSaveData = myHeroTowerExpandSaveDataRepository.findByUseridUser(userId).orElse(null);
        if(myHeroTowerExpandSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyHeroTowerExpandSaveData Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyHeroTowerExpandSaveData Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        InitJsonDatasForFirstUser jsonDatasForFirstUser = initJsonDatasForFirstUserList.stream()
                .filter(a -> a.getId() == 5)
                .findAny()
                .orElse(null);
        if(jsonDatasForFirstUser == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyHeroTowerExpandSaveDataJson Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyHeroTowerExpandSaveDataJson Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        myHeroTowerExpandSaveData.ResetSaveDataValue(jsonDatasForFirstUser.getInitJson());

        jsonDatasForFirstUser = initJsonDatasForFirstUserList.stream()
                .filter(a -> a.getId() == 6)
                .findAny()
                .orElse(null);
        if(jsonDatasForFirstUser == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyHeroTowerExpandSaveDataJson Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyHeroTowerExpandSaveDataJson Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        myHeroTowerExpandSaveData.ResetExpandInfo(jsonDatasForFirstUser.getInitJson());
        myHeroTowerExpandSaveDataRepository.save(myHeroTowerExpandSaveData);
        //최초 유저 시련의 던전 데이터 정보 생성
        MyOrdealDungeonExpandSaveData myOrdealDungeonExpandSaveData = myOrdealDungeonExpandSaveDataRepository.findByUseridUser(userId).orElse(null);
        if(myOrdealDungeonExpandSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyOrdealDungeonExpandSaveData Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyOrdealDungeonExpandSaveData Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        InitJsonDatasForFirstUser ordealDungeonExpandSaveData = initJsonDatasForFirstUserList.stream()
                .filter(a -> a.getId() == 7)
                .findAny()
                .orElse(null);
        if(ordealDungeonExpandSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: initJsonDatasForFirstUserList Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: initJsonDatasForFirstUserList Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        myOrdealDungeonExpandSaveData.ResetSaveDataValue(ordealDungeonExpandSaveData.getInitJson());
        myOrdealDungeonExpandSaveData.ResetBonus();
        myOrdealDungeonExpandSaveDataRepository.save(myOrdealDungeonExpandSaveData);
        //최초 유저 가차 마일리지 정보 생성
        GotchaMileage gotchaMileage = gotchaMileageRepository.findByUseridUser(userId).orElse(null);
        if(gotchaMileage == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: GotchaMileage Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: GotchaMileage Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        gotchaMileage.Init();
        gotchaMileageRepository.save(gotchaMileage);
        //최초 유저 장비 제작시 재료 선택 정보 생성
        MyProductionMaterialSettedInfo myProductionMaterialSettedInfo = myProductionMaterialSettedInfoRepository.findByUserIdUser(userId).orElse(null);
        if(myProductionMaterialSettedInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyProductionMaterialSettedInfo Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyProductionMaterialSettedInfo Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        myProductionMaterialSettedInfo.InitData();
        myProductionMaterialSettedInfoRepository.save(myProductionMaterialSettedInfo);
        //최초 유저 동료 별점 셋팅 테이블 생성
        CompanionStarPointsAverageTable companionStarPointsAverageTable = companionStarPointsAverageRepository.findByUseridUser(userId).orElse(null);
        if(companionStarPointsAverageTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: CompanionStarPointsAverageTable Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: CompanionStarPointsAverageTable Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }

        InitJsonDatasForFirstUser companionStarPointsAverage = initJsonDatasForFirstUserList.stream()
                .filter(a -> a.getId() == 11)
                .findAny()
                .orElse(null);
        if(companionStarPointsAverage == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: initJsonDatasForFirstUserList Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: initJsonDatasForFirstUserList Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        companionStarPointsAverageTable.ResetStarPointsAverage(companionStarPointsAverage.getInitJson());
        companionStarPointsAverageRepository.save(companionStarPointsAverageTable);
        //최초 유저 상점 데이터 생성
        MyShopInfo myShopInfo = myShopInfoRepository.findByUseridUser(userId).orElse(null);
        if(myShopInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyShopInfo Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyShopInfo Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }

        InitJsonDatasForFirstUser shopInfo = initJsonDatasForFirstUserList.stream()
                .filter(a -> a.getId() == 12)
                .findAny()
                .orElse(null);
        if(shopInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: initJsonDatasForFirstUserList Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: initJsonDatasForFirstUserList Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        myShopInfo.ResetMyShopInfos(shopInfo.getInitJson());
        myShopInfoRepository.save(myShopInfo);
        //최초 유저 업적 데이터 생성
        MyMissionsData myMissionData = myMissionsDataRepository.findByUseridUser(userId).orElse(null);
        if(myMissionData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMissionsData Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
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

        String json_MissionData = JsonStringHerlper.WriteValueAsStringFromData(missionsDataDto);
        myMissionData.ResetSaveDataValue(json_MissionData);
        myMissionsDataRepository.save(myMissionData);

        //최초 유저 우편 데이터 생성
        MyMailBox myMailBox = myMailBoxRepository.findByUseridUser(userId).orElse(null);
        if(myMailBox == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMailBox Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMailBox Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        MailBoxDto mailBoxDto = new MailBoxDto();
        mailBoxDto.mailBoxInfoList = new ArrayList<>();
        String json_myMailBoxInfo = JsonStringHerlper.WriteValueAsStringFromData(mailBoxDto);
        myMailBox.ResetJsonMyMailBoxInfo(json_myMailBoxInfo);
        myMailBoxRepository.save(myMailBox);
        //최초 유저 광고 보기 데이터 생성
        MyTodayViewingTable myTodayViewingTable = todayViewingTableRepository.findByUseridUser(userId).orElse(null);
        if(myTodayViewingTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyTodayViewingTable Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyTodayViewingTable Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        myTodayViewingTable.ResetViewingCount();
        todayViewingTableRepository.save(myTodayViewingTable);

        //최초 유저 무한의 탑 정보 데이터 생성
        MyInfiniteTowerSaveData myInfiniteTowerSaveData = myInfiniteTowerSaveDataRepository.findByUseridUser(userId).orElse(null);
        if(myInfiniteTowerSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyInfiniteTowerSaveData Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyInfiniteTowerSaveData Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        InitJsonDatasForFirstUser infiniteTowerSaveData = initJsonDatasForFirstUserList.stream()
                .filter(a -> a.getId() == 13)
                .findAny()
                .orElse(null);
        if(infiniteTowerSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: initJsonDatasForFirstUserList Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: initJsonDatasForFirstUserList Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        myInfiniteTowerSaveData.ResetReceivedRewardInfoJson(infiniteTowerSaveData.getInitJson());
        myInfiniteTowerSaveData.SetArrivedTopFloor(1);
        myInfiniteTowerSaveDataRepository.save(myInfiniteTowerSaveData);

        MyEquipmentDeck myEquipmentDeck = myEquipmentDeckRepository.findByUserIdUser(userId).orElse(null);
        if(myEquipmentDeck == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyEquipmentDeck Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEquipmentDeck Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        myEquipmentDeck.Init();

        return map;
    }
}
