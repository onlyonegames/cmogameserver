package com.onlyonegames.eternalfantasia.domain.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.onlyonegames.eternalfantasia.Interceptor.OnlyoneSession;
import com.onlyonegames.eternalfantasia.Interceptor.OnlyoneSessionRepository;
import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;
import com.onlyonegames.eternalfantasia.domain.model.dto.*;
import com.onlyonegames.eternalfantasia.domain.model.dto.ChapterSaveDto.ChapterSaveData;
import com.onlyonegames.eternalfantasia.domain.model.dto.CostumeDto.CostumeDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.MyArenaPlayLogForBattleRecordDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.MyInfiniteTowerRewardReceivedInfosDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.MyInfiniteTowerSaveDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.EventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Event.*;
import com.onlyonegames.eternalfantasia.domain.model.dto.GiftItemDto.GiftItemDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.Gotcha.GotchaMileageDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.HotTimeEventDto.HotTimeFieldObjectInfoListDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.HotTimeEventDto.MyHotTimeInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceDto.LinkforceOpenDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceWeaponDto.LinkweaponInfoDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.MyCharactersDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.MailSendRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.GotchaResponseDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.*;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyCostumeInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyGiftInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyLinkforceInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyLinkweaponInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyArenaPlayLogForBattleRecord;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyInfiniteTowerSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Event.CommonEventScheduler;
import com.onlyonegames.eternalfantasia.domain.model.entity.Event.MyAchieveEventMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Event.MyMonsterKillEventMissionData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Gotcha.GotchaMileage;
import com.onlyonegames.eternalfantasia.domain.model.entity.Gotcha.PickupTable;
import com.onlyonegames.eternalfantasia.domain.model.entity.HotTimeEvent.HotTimeScheduler;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.*;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.*;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyCostumeInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyGiftInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyLinkforceInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyLinkweaponInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyArenaPlayLogForBattleRecordRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyInfiniteTowerSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Event.CommonEventSchedulerRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Event.MyAchieveEventMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Event.MyMonsterKillEventMissionDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Gotcha.GotchaMileageRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Gotcha.PickupTableRepository;
import com.onlyonegames.eternalfantasia.domain.repository.HotTimeEvent.HotTimeSchedulerRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.*;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.MyAncientDragonSeasonService;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.MyArenaSeasonService;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.MyHeroTowerSeasonService;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.MyOrdealDungeonSeasonService;

import com.onlyonegames.eternalfantasia.domain.service.EternalPass.MyEternalPassService;
import com.onlyonegames.eternalfantasia.domain.service.Mail.MyMailBoxService;
import com.onlyonegames.eternalfantasia.domain.service.Mission.MyMissionsDataService;
import com.onlyonegames.eternalfantasia.domain.service.Shop.MyShopService;
import com.onlyonegames.eternalfantasia.etc.Defines;
import com.onlyonegames.eternalfantasia.etc.EquipmentCalculate;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.eternalfantasia.etc.LinkAbilityCostCalculator;
import com.onlyonegames.util.StringMaker;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MyCharactersRepository myCharactersRepository;
    private final MyMainHeroSkillRepository myMainHeroSkillRepository;
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final HeroEquipmentInventoryRepository heroEquipmentInventoryRepository;
    private final MyEquipmentDeckRepository myEquipmentDeckRepository;
    private final MyProductionSlotRepository myProductionSlotRepository;
    private final MyProductionMasteryRepository myProductionMasteryRepository;
    private final MyTeamInfoRepository myTeamInfoRepository;
    private final MyGiftInventoryRepository myGiftInventoryRepository;
    private final MyChapterSaveDataRepository myChapterSaveDataRepository;
    private final MyLinkforceInfoRepository myLinkforceInfoRepository;
    private final MyLinkweaponInfoRepository myLinkweaponInfoRepository;
    private final MyCostumeInventoryRepository myCostumeInventoryRepository;
    private final MyFieldSaveDataRepository myFieldSaveDataRepository;
    private final MyExpeditionDataRepository myExpeditionDataRepository;
    private final MyProductionMaterialSettedInfoRepository myProductionMaterialSettedInfoRepository;
    private final MyArenaPlayLogForBattleRecordRepository myArenaPlayLogForBattleRecordRepository;
    private final TodayViewingTableRepository todayViewingTableRepository;
    private final GotchaMileageRepository gotchaMileageRepository;

    //각던전 시즌 정보
    private final MyHeroTowerSeasonService myHeroTowerSeasonService;
    private final MyAncientDragonSeasonService myAncientDragonSeasonService;
    private final MyOrdealDungeonSeasonService myOrdealDungeonSeasonService;
    private final MyArenaSeasonService myArenaSeasonService;
    private final MyShopService myShopService;
    private final ItemTypeRepository itemTypeRepository;
    private final PickupTableRepository pickupTableRepository;

    //천공의 계단 던전 정보
    private final MyInfiniteTowerSaveDataRepository myInfiniteTowerSaveDataRepository;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;
    private final HotTimeSchedulerRepository hotTimeSchedulerRepository;
    private final CommonEventSchedulerRepository commonEventSchedulerRepository;
    private final MyMonsterKillEventMissionDataRepository myMonsterKillEventMissionDataRepository;
    private final ErrorLoggingService errorLoggingService;

    //이터널 패스 정보
    private final MyEternalPassService myEternalPassService;
    private final MyMailBoxService myMailBoxService;
    private final GameDataTableService gameDataTableService;

    //업적 정보
    private final MyMissionsDataService myMissionsDataService;

    //프로필 정보
    private final MyProfileService myProfileService;
    private final MyAchieveEventMissionsDataRepository myAchieveEventMissionsDataRepository;



    private final UpgradeStatusService upgradeStatusService;

    //세션 redis
    private final OnlyoneSessionRepository sessionRepository;

    public Map<String, Object> login(Long userId, String jwt, Map<String, Object> map) {

        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }

        OnlyoneSession onlyoneSession = sessionRepository.findById(userId).orElse(null);
        if(onlyoneSession != null) {
            sessionRepository.delete(onlyoneSession);
        }
        onlyoneSession = new OnlyoneSession(userId, jwt);
        sessionRepository.save(onlyoneSession);

        if(user.isBlackUser()) {
            errorLoggingService.SetErrorLog(user.getId(), ResponseErrorCode.BLACK_USER.getIntegerValue(), "Black User", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Black User", ResponseErrorCode.BLACK_USER);
        }

        user.SetLastLoginDate();
        map.put("userInfo", user);

        // UpgradeStatus
        upgradeStatusService.GetUpgradeStatus(userId, map);

        // upgradeStatusBuyInfoTableList
        List<UpgradeStatusBuyInfoTable> upgradeStatusBuyInfoTableList = gameDataTableService.UpgradeStatusBuyInfoTableList();
        String json = JsonStringHerlper.WriteValueAsStringFromData(upgradeStatusBuyInfoTableList);
        map.put("upgradeStatusBuyInfoTable", upgradeStatusBuyInfoTableList);


        /*
        //차원석 차징타임 및 차징에 따른 차원석 갯수 갱신
        //user.CheckStoneOfDimensionChargingTime();
        user.SetLastLoginDate();

        List<herostable> herostableList = gameDataTableService.HerosTableList();

        // 영웅 리스트 Get
        List<MyCharacters> myHeros = myCharactersRepository.findAllByuseridUser(userId);
        MyCharacters myCharacters = myHeros.stream().filter(a -> a.getCodeHerostable().equals("hero")).findAny().orElse(null);
        MyCharacters mainHero = myCharacters;
        //신규 케릭터 추가시 해당 케릭터 유저 데이터에 정보 갱신 해주기.
        int maxLevel = mainHero.getLevel();
        boolean checkMainHeroLevel = false;
        for(herostable heroData : herostableList) {
            MyCharacters myCharacter = myHeros.stream().filter(a -> a.getCodeHerostable().equals(heroData.getCode())).findAny().orElse(null);

            if(myCharacter == null){
                MyCharactersBaseDto myCharactersBastDto = new MyCharactersBaseDto();
                String codeHeroTable = heroData.getCode();
                myCharactersBastDto.setLinkAbilityLevel(heroData.getTier());
                myCharactersBastDto.setUseridUser(userId);
                myCharactersBastDto.setCodeHerostable(codeHeroTable);
                MyCharacters newMyCharacter = myCharactersBastDto.ToEntity(heroData.getTier());
                newMyCharacter.setMaxLevel(maxLevel);
                newMyCharacter = myCharactersRepository.save(newMyCharacter);
                myHeros.add(newMyCharacter);
            }
        }


        List<MyCharactersBaseDto> myHerosDto = new ArrayList<>();
        for(MyCharacters myCharacter : myHeros) {
            //피로도 체크
            myCharacter.CheckAfterDayForFatigability();
        }

        for(MyCharacters temp : myHeros){
            MyCharactersBaseDto myCharactersBaseDto = new MyCharactersBaseDto();
            myCharactersBaseDto.InitFromDbData(temp);
            myHerosDto.add(myCharactersBaseDto);
        }
        map.put("myCharacters", myHerosDto);
        // 팀정보 리스트 Get
        MyTeamInfo myTeamInfo = myTeamInfoRepository.findByUseridUser(userId);
        String team = myTeamInfo.getStageTeam();
        if(!team.contains(mainHero.getId().toString()))
        {
            myTeamInfo.initHeroForStage(mainHero.getId());
        }
        team = myTeamInfo.getHeroTowerDungeonPlayTeam();
        if(!team.contains(mainHero.getId().toString()))
        {
            myTeamInfo.initHeroForHeroTowerDungeonPlayTeam(mainHero.getId());
        }
        team = myTeamInfo.getOrdealDungeonPlayTeam();
        if(!team.contains(mainHero.getId().toString()))
        {
            myTeamInfo.initHeroForOrdealDungeonPlayTeam(mainHero.getId());
        }
        team = myTeamInfo.getArenaPlayTeam();
        if(!team.contains(mainHero.getId().toString()))
        {
            myTeamInfo.initHeroForArenaPlayTeam(mainHero.getId());
        }
        team = myTeamInfo.getArenaDefenceTeam();
        if(!team.contains(mainHero.getId().toString()))
        {
            myTeamInfo.initHeroForArenaDefenceTeam(mainHero.getId());
        }
        team = myTeamInfo.getAncientDragonDungeonPlayTeam();
        if(!team.contains(mainHero.getId().toString()))
        {
            myTeamInfo.initHeroForAncientDragonDungeonPlayTeam(mainHero.getId());
        }
        team = myTeamInfo.getInfiniteTowerTeam();
        if(!team.contains(mainHero.getId().toString()))
        {
            myTeamInfo.initHeroForInfiniteTowerTeam(mainHero.getId());
        }
        team = myTeamInfo.getFieldDungeonPlayTeam();
        if(!team.contains(mainHero.getId().toString()))
        {
            myTeamInfo.initHeroForFieldDungeonPlayTeam(mainHero.getId());
        }

        MyTeamInfoDto myTeamInfoDto = new MyTeamInfoDto();
        myTeamInfoDto.InitFromDBData(myTeamInfo);
        map.put("myTeamInfo", myTeamInfoDto);
        // 메인 영웅 스킬 정보 Get
        List<MyMainHeroSkill> myMainHeroSkillList = myMainHeroSkillRepository.findAllByUseridUser(userId);
        List<MyMainHeroSkillBaseDto> myMainHeroSkillBaseDtoList = new ArrayList<>();
        for(MyMainHeroSkill temp : myMainHeroSkillList){
            MyMainHeroSkillBaseDto myMainHeroSkillBaseDto = new MyMainHeroSkillBaseDto();
            myMainHeroSkillBaseDto.InitFromDbData(temp);
            myMainHeroSkillBaseDtoList.add(myMainHeroSkillBaseDto);
        }
        map.put("myMainHeroSkillList", myMainHeroSkillBaseDtoList);
        // 소지품 인벤토리 정보 Get
        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);

        // 장비 인벤토리 정보 Get
        List<HeroEquipmentInventory> heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(userId);
        List<HeroEquipmentInventoryDto> heroEquipmentInventoryDtoList = new ArrayList<>();
        for(HeroEquipmentInventory temp : heroEquipmentInventoryList){
            HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
            heroEquipmentInventoryDto.InitFromDbData(temp);
            heroEquipmentInventoryDtoList.add(heroEquipmentInventoryDto);
        }
        map.put("heroEquipmentInventory", heroEquipmentInventoryDtoList);
        //영웅 장비 덱 정보 Get
        MyEquipmentDeck myEquipmentDeck = myEquipmentDeckRepository.findByUserIdUser(userId).orElse(null);
        if(myEquipmentDeck == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyEquipmentDeck not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEquipmentDeck not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyEquipmentDeckDto myEquipmentDeckDto = new MyEquipmentDeckDto();
        myEquipmentDeckDto.InitFromDbData(myEquipmentDeck);
        map.put("myEquipmentDeck", myEquipmentDeckDto);
        //장비 제작 리스트 정보 Get
        List<MyProductionSlot> myProductionSlotList = myProductionSlotRepository.findByUserIdUser(userId);
        List<MyProductionSlotDto> myProductionSlotDtoList = new ArrayList<>();
        for(MyProductionSlot temp : myProductionSlotList){
            MyProductionSlotDto myProductionSlotDto = new MyProductionSlotDto();
            myProductionSlotDto.InitFromDbData(temp);
            myProductionSlotDtoList.add(myProductionSlotDto);
        }
        map.put("myProductionSlotList", myProductionSlotDtoList);
        //장비 제작 숙련도 정보 Get
        MyProductionMastery myProductionMastery = myProductionMasteryRepository.findByUserIdUser(userId);
        MyProductionMasteryDto myProductionMasteryDto = new MyProductionMasteryDto();
        myProductionMasteryDto.InitFromDbData(myProductionMastery);
        map.put("myProductionMastery", myProductionMasteryDto);
        //스테이지 오픈 정보
        MyChapterSaveData myChapterSaveData = myChapterSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myChapterSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyChapterSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyChapterSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        map.put("myChapterSaveDataString",myChapterSaveData.getSaveDataValue());

        //선물 인벤토리 정보
        MyGiftInventory giftInventory = myGiftInventoryRepository.findByUseridUser(userId)
                .orElse(null);
        if(giftInventory == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyGiftInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyGiftInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        GiftItemDtosList giftItemDtosList = JsonStringHerlper.ReadValueFromJson(giftInventory.getInventoryInfos(), GiftItemDtosList.class);
        //신규 케릭터 추가시 선물 정보 추가 해주기.
        List<GiftTable> giftTableList = gameDataTableService.GiftsTableList();
        for(GiftTable giftTable : giftTableList){
            GiftItemDtosList.GiftItemDto findGiftItemDto = giftItemDtosList.giftItemDtoList.stream().filter(a -> a.code.equals(giftTable.getCode())).findAny().orElse(null);
            if(findGiftItemDto == null) {
                GiftItemDtosList.GiftItemDto newGiftItemDto = new GiftItemDtosList.GiftItemDto();
                newGiftItemDto.code = giftTable.getCode();
                newGiftItemDto.count = 0;
                giftItemDtosList.giftItemDtoList.add(newGiftItemDto);
            }
        }

        String giftItemJson = JsonStringHerlper.WriteValueAsStringFromData(giftItemDtosList);
        giftInventory.ResetInventoryInfos(giftItemJson);

        map.put("myGiftInventoryInfos",giftItemDtosList.giftItemDtoList);

        //링크포스 정보
        MyLinkforceInfo myLinkforceInfo = myLinkforceInfoRepository.findByUseridUser(userId).orElse(null);
        if(myLinkforceInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyLinkforceInfo not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyLinkforceInfo not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        LinkforceOpenDtosList linkforceOpenDtosList = JsonStringHerlper.ReadValueFromJson(myLinkforceInfo.getJson_LinkforceInfos(), LinkforceOpenDtosList.class);

        //신규 케릭터 추가시 링크포스 정보 갱신 해주기.
        for(herostable heroData : herostableList){
            String heroCode = heroData.getCode();
            if(heroCode.equals("hero")){
                continue;
            }
            LinkforceOpenDtosList.CompanionLinkforceOpenInfo companionLinkforceOpenInfo = linkforceOpenDtosList.openInfoList.stream().filter(a -> a.code.equals(heroCode)).findAny().orElse(null);

            if(companionLinkforceOpenInfo == null) {

                LinkforceOpenDtosList.CompanionLinkforceOpenInfo newCompanionLinkforceOpenInfo = new LinkforceOpenDtosList.CompanionLinkforceOpenInfo();
                newCompanionLinkforceOpenInfo.code = heroCode;
                newCompanionLinkforceOpenInfo.linkforceOpenInfoList = new ArrayList<>();
                for(int i = 0; i < 165; i++){
                    newCompanionLinkforceOpenInfo.linkforceOpenInfoList.add(0);
                }
                linkforceOpenDtosList.openInfoList.add(newCompanionLinkforceOpenInfo);
            }
        }

        String changedlinkforceOpenDtosList = JsonStringHerlper.WriteValueAsStringFromData(linkforceOpenDtosList);
        myLinkforceInfo.ResetLinkforceInfos(changedlinkforceOpenDtosList);

        map.put("myLinkforceInfoList", linkforceOpenDtosList.openInfoList);

        //링크웨폰 정보
        MyLinkweaponInfo myLinkweaponInfo = myLinkweaponInfoRepository.findByUseridUser(userId).orElse(null);
        if(myLinkweaponInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyLinkweaponInfo not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyLinkweaponInfo not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        LinkweaponInfoDtosList linkweaponInfoDtosList = JsonStringHerlper.ReadValueFromJson(myLinkweaponInfo.getJson_LinkweaponRevolution(), LinkweaponInfoDtosList.class);
        //신규 케릭터 추가시 링크웨폰 정보 갱신 해주기.
        for(herostable heroData : herostableList){
            String heroCode = heroData.getCode();
            if(heroCode.equals("hero"))
                continue;
            LinkweaponInfoDtosList.CompanionLinkweaponInfo companionLinkweaponInfoFirstVaules = linkweaponInfoDtosList.companionLinkweaponInfoList.get(0);
            LinkweaponInfoDtosList.CompanionLinkweaponInfo companionLinkweaponInfo = linkweaponInfoDtosList.companionLinkweaponInfoList.stream().filter(a -> a.ownerCode.equals(heroCode)).findAny().orElse(null);
            //String linkforceInfoDtosListStr = JsonStringHerlper.WriteValueAsStringFromData(linkforceOpenDtosList);
            if(companionLinkweaponInfo == null) {

                LinkweaponInfoDtosList.CompanionLinkweaponInfo newCompanionLinkweaponInfo = new LinkweaponInfoDtosList.CompanionLinkweaponInfo();
                newCompanionLinkweaponInfo.ownerCode = heroCode;
                newCompanionLinkweaponInfo.selectedIds = new ArrayList<>();
                newCompanionLinkweaponInfo.selectedIds.add(0);
                newCompanionLinkweaponInfo.strengthenId = 0;
                newCompanionLinkweaponInfo.linkweaponInfoDtoList = new ArrayList<>();

                for(int i = 0; i < 16; i++) {
                    LinkweaponInfoDtosList.LinkweaponInfoDto linkweaponInfoDto = new LinkweaponInfoDtosList.LinkweaponInfoDto();
                    linkweaponInfoDto.dependenciesList = new ArrayList<>();
                    linkweaponInfoDto.id = i;
                    linkweaponInfoDto.open = false;
                    linkweaponInfoDto.upgrade = 0;
                    if(i == 0) {
                        linkweaponInfoDto.open = true;
                        linkweaponInfoDto.upgrade = 1;
                        newCompanionLinkweaponInfo.linkweaponInfoDtoList.add(linkweaponInfoDto);
                        continue;
                    }

                    else if(i  < 4) {
                        linkweaponInfoDto.dependenciesList.add(0);
                    }
                    else if(i < 7) {
                        linkweaponInfoDto.dependenciesList.add(1);
                        linkweaponInfoDto.dependenciesList.add(2);
                        linkweaponInfoDto.dependenciesList.add(3);
                    }
                    else if(i < 10) {
                        linkweaponInfoDto.dependenciesList.add(4);
                        linkweaponInfoDto.dependenciesList.add(6);
                        linkweaponInfoDto.dependenciesList.add(5);
                    }
                    else if(i < 13) {
                        linkweaponInfoDto.dependenciesList.add(7);
                        linkweaponInfoDto.dependenciesList.add(8);
                        linkweaponInfoDto.dependenciesList.add(9);
                    }
                    else {
                        linkweaponInfoDto.dependenciesList.add(10);
                        linkweaponInfoDto.dependenciesList.add(11);
                        linkweaponInfoDto.dependenciesList.add(12);
                    }
                    newCompanionLinkweaponInfo.linkweaponInfoDtoList.add(linkweaponInfoDto);
                }

                linkweaponInfoDtosList.companionLinkweaponInfoList.add(newCompanionLinkweaponInfo);
            }
        }

        String changedlinkWeaponInfoDtosList = JsonStringHerlper.WriteValueAsStringFromData(linkweaponInfoDtosList);
        myLinkweaponInfo.ResetLinkweaponRevolution(changedlinkWeaponInfoDtosList);


        map.put("myLinkweaponInfoList", linkweaponInfoDtosList.companionLinkweaponInfoList);

        //동료 코스튬 인벤토리 정보
        MyCostumeInventory myCostumeInventory = myCostumeInventoryRepository.findByUseridUser(userId).orElse(null);
        if(myCostumeInventory == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyCostumeInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyCostumeInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        CostumeDtosList myCostumeDtosList = JsonStringHerlper.ReadValueFromJson(myCostumeInventory.getJson_CostumeInventory(), CostumeDtosList.class);

        //신규 케릭터 추가시 해당 케릭터 코스튬 정보 업데이트 해주기.
        List<InitJsonDatasForFirstUser> initJsonDatasForFirstUserList = gameDataTableService.InitJsonDatasForFirstUserList();
        InitJsonDatasForFirstUser costumeInventoryJson = initJsonDatasForFirstUserList.stream()
                .filter(a -> a.getId() == 3)
                .findAny()
                .orElse(null);
        
        CostumeDtosList initCostumeDtosList = JsonStringHerlper.ReadValueFromJson(costumeInventoryJson.getInitJson(), CostumeDtosList.class);
        
        for(CostumeDtosList.CostumeDto costumeDto : initCostumeDtosList.hasCostumeIdList){
            CostumeDtosList.CostumeDto gettingCostumeDto = myCostumeDtosList.hasCostumeIdList.stream().filter(a -> a.costumeId == costumeDto.costumeId).findAny().orElse(null);
            if(gettingCostumeDto == null) {
                CostumeDtosList.CostumeDto newCCostumeDto = new CostumeDtosList.CostumeDto();
                newCCostumeDto.costumeId = costumeDto.costumeId;
                newCCostumeDto.isEquip = costumeDto.isEquip;
                newCCostumeDto.hasBuy = costumeDto.hasBuy;
                myCostumeDtosList.hasCostumeIdList.add(newCCostumeDto);
            }
        }
        String jsonNewMyCostumeDtosList = JsonStringHerlper.WriteValueAsStringFromData(myCostumeDtosList);
        myCostumeInventory.ResetCostumeInventory(jsonNewMyCostumeDtosList);

        map.put("myCostumeInventory", myCostumeDtosList.hasCostumeIdList);

        //필드데이터 정보
        MyFieldSaveData myFieldSaveData = myFieldSaveDataRepository.findByUseridUser(userId).orElse(null);
        if(myFieldSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myFieldSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myFieldSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String json_saveDataValue = myFieldSaveData.getJson_saveDataValue();
        FieldSaveDataDto fieldSaveDataDto = JsonStringHerlper.ReadValueFromJson(json_saveDataValue, FieldSaveDataDto.class);
        map.put("fieldSaveDataDto", fieldSaveDataDto);
        JsonStringHerlper.ReadValueFromJson(json_saveDataValue, FieldSaveDataDto.class);
        map.put("lastFieldClearTime", myFieldSaveData.getLastClearTime());

        //필드 핫타임 정보
        LocalDateTime nowTime = LocalDateTime.now();
        List<HotTimeScheduler> hotTimeSchedulerList = hotTimeSchedulerRepository.findByStartTimeBeforeAndEndTimeAfterAndKind(nowTime,nowTime,1);
        if(hotTimeSchedulerList.size()>=2) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Wrong HotTimeScheduler find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Wrong HotTimeScheduler find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        HotTimeScheduler hotTimeScheduler = null;
        if(!hotTimeSchedulerList.isEmpty())
            hotTimeScheduler = hotTimeSchedulerList.get(0);
        boolean hotTime = true;
        if(hotTimeScheduler == null)
            hotTime = false;
        MyHotTimeInfoDto myHotTimeInfoDto = new MyHotTimeInfoDto();
        if(hotTime){
            if(myFieldSaveData.getJson_hotTimeSaveDataValue() == null){
                myHotTimeInfoDto = SetHotTimeInfo(hotTimeScheduler.getId(),hotTimeScheduler.getJson_HotTimeEvent());
                myFieldSaveData.ResetHotTimeSaveDataValue(JsonStringHerlper.WriteValueAsStringFromData(myHotTimeInfoDto));
            }
            myHotTimeInfoDto = JsonStringHerlper.ReadValueFromJson(myFieldSaveData.getJson_hotTimeSaveDataValue(), MyHotTimeInfoDto.class);
            if(!hotTimeScheduler.getId().equals(myHotTimeInfoDto.hotTimeSchedulerId)) {
                myHotTimeInfoDto = SetHotTimeInfo(hotTimeScheduler.getId(), hotTimeScheduler.getJson_HotTimeEvent());
                myFieldSaveData.ResetHotTimeSaveDataValue(JsonStringHerlper.WriteValueAsStringFromData(myHotTimeInfoDto));
            }
        }
        map.put("hotTime", hotTime);
        map.put("hotTimeList", myHotTimeInfoDto);

        //필드 교환 이벤트 정보
        List<CommonEventScheduler> commonEventSchedulerList = commonEventSchedulerRepository.findByStartTimeBeforeAndEndTimeAfter(nowTime, nowTime);
        if(commonEventSchedulerList.size()>2) { //TODO 3을 GameDataTable에 EventContentTable을 추가하여 size를 불러오도록 변경 필요
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Wrong EventScheduler find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Wrong EventScheduler find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        CommonEventScheduler commonEventScheduler = null;
        if(!commonEventSchedulerList.isEmpty()){
            List<CommonEventScheduler> tempScheduler = commonEventSchedulerList.stream().filter(i -> i.getEventContentsTable().getId()==1).collect(Collectors.toList());
            if(tempScheduler.size()>1){
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Wrong FieldExchangeItemEventScheduler find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Wrong FieldExchangeItemEventScheduler find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            if(!tempScheduler.isEmpty())
                commonEventScheduler = tempScheduler.get(0);
        }
        boolean fieldExchangeItemEvent = true;
        if(commonEventScheduler == null) {
            fieldExchangeItemEvent = false;
            List<CommonEventScheduler> previusCommonEventSchedulerList = commonEventSchedulerRepository.findByEventContentsTable_idOrderByEndTimeDesc(1);
            if(!previusCommonEventSchedulerList.isEmpty()) {
                CommonEventScheduler lastFieldExchangeEventScheduler = previusCommonEventSchedulerList.get(0);
                LocalDateTime now = LocalDateTime.now();
                Duration duration = Duration.between(lastFieldExchangeEventScheduler.getEndTime(), now);
                if (duration.toDays() > 7) {
                    BelongingInventory belongingInventory = belongingInventoryList.stream().filter(i -> i.getItemType().getId() == 3 && i.getItemId() == 20).findAny().orElse(null);
                    if (belongingInventory != null) {
                        belongingInventoryRepository.delete(belongingInventory);
                        belongingInventoryList.remove(belongingInventory);
                    }
                } else{
                    BelongingInventory belongingInventory = belongingInventoryList.stream().filter(i -> i.getItemType().getId() == 3 && i.getItemId() == 20).findAny().orElse(null);
                    if (belongingInventory != null) {
                        if(belongingInventory.getCount()>0){
                            List<MailSendRequestDto.Item> itemList = new ArrayList<>();
                            MailSendRequestDto.Item item = new MailSendRequestDto.Item();
                            item.setItem("gold", 100 * belongingInventory.getCount());
                            itemList.add(item);
                            StringMaker.Clear();
                            StringMaker.stringBuilder.append(lastFieldExchangeEventScheduler.getTitle());
                            StringMaker.stringBuilder.append(" 획득 아이템 교환");
                            String content = StringMaker.stringBuilder.toString();
                            MailSendRequestDto mailSendRequestDto = new MailSendRequestDto();
                            mailSendRequestDto.setToId(userId);
                            mailSendRequestDto.setFromId(10000L);
                            mailSendRequestDto.setTitle("아이템 교환");
                            mailSendRequestDto.setContent(content);
                            mailSendRequestDto.setExpireDate(LocalDateTime.now().plusDays(7));
                            mailSendRequestDto.setItemList(itemList);
                            mailSendRequestDto.setMailType(1);
                            myMailBoxService.SendMail(mailSendRequestDto, map);
                            belongingInventoryRepository.delete(belongingInventory);
                            belongingInventoryList.remove(belongingInventory);
                        }
                    }
                }
            }
        }
        MyFieldExchangeItemEventDto myFieldExchangeItemEventDto = new MyFieldExchangeItemEventDto();
        if(fieldExchangeItemEvent){
            if(myFieldSaveData.getJson_fieldExchangeItemSaveDataValue() == null){
                BelongingInventory belongingInventory = belongingInventoryList.stream().filter(i -> i.getItemType().getId() == 3 && i.getItemId() == 20).findAny().orElse(null);
                if(belongingInventory != null) {
                    belongingInventoryRepository.delete(belongingInventory);
                    belongingInventoryList.remove(belongingInventory);
                }
                myFieldExchangeItemEventDto = SetFieldExchangeItemEvent(commonEventScheduler.getId(), commonEventScheduler.getJson_FieldExchangeItemEvent());
                myFieldSaveData.ResetFieldExchangeItemSaveDataValue(JsonStringHerlper.WriteValueAsStringFromData(myFieldExchangeItemEventDto));
            }
            myFieldExchangeItemEventDto = JsonStringHerlper.ReadValueFromJson(myFieldSaveData.getJson_fieldExchangeItemSaveDataValue(), MyFieldExchangeItemEventDto.class);
            if(!commonEventScheduler.getId().equals(myFieldExchangeItemEventDto.eventId)) {
                BelongingInventory belongingInventory = belongingInventoryList.stream().filter(i -> i.getItemType().getId() == 3 && i.getItemId() == 20).findAny().orElse(null);
                if(belongingInventory != null) {
                    belongingInventoryRepository.delete(belongingInventory);
                    belongingInventoryList.remove(belongingInventory);
                }
                myFieldExchangeItemEventDto = SetFieldExchangeItemEvent(commonEventScheduler.getId(), commonEventScheduler.getJson_FieldExchangeItemEvent());
                myFieldSaveData.ResetFieldExchangeItemSaveDataValue(JsonStringHerlper.WriteValueAsStringFromData(myFieldExchangeItemEventDto));
            }
        } else {
            myFieldExchangeItemEventDto.userFieldExchangeItemEventDto = new UserFieldObjectInfoListDto();
        }

        map.put("fieldExchangeItemEvent", fieldExchangeItemEvent);
        map.put("fieldExchangeItemEventList", myFieldExchangeItemEventDto);

        //레타의 사냥 주문 미션데이터 체크 및 추가
        CommonEventScheduler monsterKillEventScheduler = null;
        if(!commonEventSchedulerList.isEmpty()){
            List<CommonEventScheduler> tempScheduler = commonEventSchedulerList.stream().filter(i -> i.getEventContentsTable().getId()==3).collect(Collectors.toList());
            if(tempScheduler.size()>1){
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Wrong MonsterKillEventScheduler find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Wrong MonsterKillEventScheduler find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            if(!tempScheduler.isEmpty())
                monsterKillEventScheduler = tempScheduler.get(0);
        }
        boolean monsterKillEvent = true;
        if(monsterKillEventScheduler == null) {
            monsterKillEvent = false;
            MyMonsterKillEventMissionData myMonsterKillEventMissionData = myMonsterKillEventMissionDataRepository.findByUseridUser(userId).orElse(null);
            if(myMonsterKillEventMissionData != null)
                myMonsterKillEventMissionData = null;
        }
        EventResponseDto.MonsterKillEventDataDto monsterKillEventDataDto = new EventResponseDto.MonsterKillEventDataDto();
        List<MissionsDataDto.MissionData> clientData = new ArrayList<>();
        if(monsterKillEvent){
            MyMonsterKillEventMissionData myMonsterKillEventMissionData = myMonsterKillEventMissionDataRepository.findByUseridUser(userId).orElse(null);
            if(myMonsterKillEventMissionData==null) {
                MonsterKillEventMissionDataDto monsterKillEventMissionDataDto = new MonsterKillEventMissionDataDto();
                monsterKillEventMissionDataDto.setEventId(monsterKillEventScheduler.getId());
                monsterKillEventMissionDataDto.monsterKillEventMissionsData = new ArrayList<>();
                List<MonsterKillEventTable> monsterKillEventTableList = gameDataTableService.MonsterKillEventTableList();
                for(MonsterKillEventTable temp:monsterKillEventTableList){
                    MissionsDataDto.MissionData missionData = new MissionsDataDto.MissionData();
                    missionData.code = temp.getCode();
                    missionData.rewardReceived = false;
                    missionData.goalCount = temp.getGoalCount();
                    missionData.alreadyOpenStepMax = 0;
                    monsterKillEventMissionDataDto.monsterKillEventMissionsData.add(missionData);

                }
                MyMonsterKillEventMissionDataDto myMonsterKillEventMissionDataDto = new MyMonsterKillEventMissionDataDto();
                String json_MissionData = JsonStringHerlper.WriteValueAsStringFromData(monsterKillEventMissionDataDto);
                myMonsterKillEventMissionDataDto.setJson_saveDataValue(json_MissionData);
                myMonsterKillEventMissionDataDto.setUseridUser(userId);
                myMonsterKillEventMissionData = myMonsterKillEventMissionDataDto.ToEntity();
                myMonsterKillEventMissionDataRepository.save(myMonsterKillEventMissionData);
                clientData = monsterKillEventMissionDataDto.ImportQuestMissionSendToClient(gameDataTableService.MonsterKillEventTableList());
                monsterKillEventDataDto.SetMonsterKillEventDataDto(clientData, monsterKillEventScheduler.getEndTime());
            } else {
                MonsterKillEventMissionDataDto monsterKillEventMissionDataDto = JsonStringHerlper.ReadValueFromJson(myMonsterKillEventMissionData.getJson_saveDataValue(), MonsterKillEventMissionDataDto.class);
                if(!monsterKillEventMissionDataDto.getEventId().equals(monsterKillEventScheduler.getId())){
                    monsterKillEventMissionDataDto.setEventId(monsterKillEventScheduler.getId());
                    monsterKillEventMissionDataDto.monsterKillEventMissionsData = new ArrayList<>();
                    List<MonsterKillEventTable> monsterKillEventTableList = gameDataTableService.MonsterKillEventTableList();
                    for(MonsterKillEventTable temp:monsterKillEventTableList){
                        MissionsDataDto.MissionData missionData = new MissionsDataDto.MissionData();
                        missionData.code = temp.getCode();
                        missionData.rewardReceived = false;
                        missionData.goalCount = temp.getGoalCount();
                        missionData.alreadyOpenStepMax = 0;
                        monsterKillEventMissionDataDto.monsterKillEventMissionsData.add(missionData);
                    }
                    clientData = monsterKillEventMissionDataDto.ImportQuestMissionSendToClient(gameDataTableService.MonsterKillEventTableList());
                    monsterKillEventDataDto.SetMonsterKillEventDataDto(clientData, monsterKillEventScheduler.getEndTime());
                    String json_MissionData = JsonStringHerlper.WriteValueAsStringFromData(monsterKillEventMissionDataDto);
                    myMonsterKillEventMissionData.ResetSaveDataValue(json_MissionData);
                }else{
                    clientData = monsterKillEventMissionDataDto.ImportQuestMissionSendToClient(gameDataTableService.MonsterKillEventTableList());
                    monsterKillEventDataDto.SetMonsterKillEventDataDto(clientData, monsterKillEventScheduler.getEndTime());
                    String json_MissionData = JsonStringHerlper.WriteValueAsStringFromData(monsterKillEventMissionDataDto);
                    myMonsterKillEventMissionData.ResetSaveDataValue(json_MissionData);
                }
            }


        }
        map.put("monsterKillEvent", monsterKillEvent);
        map.put("monsterKillEventDataDto", monsterKillEventDataDto);

        //누적 달성 이벤트
        MyAchieveEventMissionsData myAchieveEventMissionsData = myAchieveEventMissionsDataRepository.findByUseridUser(userId).orElse(null);
        if(myAchieveEventMissionsData == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyAchieveEventMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyAchieveEventMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String json_AchieveEventMissionsData = myAchieveEventMissionsData.getJson_saveDataValue();
        AchieveEventMissionDataDto achieveEventMissionDataDto = JsonStringHerlper.ReadValueFromJson(json_AchieveEventMissionsData, AchieveEventMissionDataDto.class);
        boolean achieveEvent = true;
        if(!achieveEventMissionDataDto.CheckPossibleMission()) {
            achieveEventMissionDataDto = null;
            achieveEvent = false;
        }
        map.put("achieveEvent", achieveEvent);
        map.put("myAchieveEventMissionsDataDto", achieveEventMissionDataDto);
        List<BelongingInventoryDto> belongingInventoryDtoList = new ArrayList<>();
        for(BelongingInventory temp : belongingInventoryList){
            BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
            belongingInventoryDto.InitFromDbData(temp);
            belongingInventoryDtoList.add(belongingInventoryDto);
        }
        map.put("belongingInventory", belongingInventoryDtoList);

        //탐험대 정보
        MyExpeditionData myExpeditionData = myExpeditionDataRepository.findByUseridUser(userId).orElse(null);
        if(myExpeditionData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myExpeditionData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myExpeditionData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyExpeditionDataDto myExpeditionDataDto = new MyExpeditionDataDto();
        myExpeditionDataDto.InitFromDbData(myExpeditionData);
        map.put("myExpeditionData", myExpeditionDataDto);
        //제작 장비 재료 셋팅 정보
        MyProductionMaterialSettedInfo myProductionMaterialSettedInfo = myProductionMaterialSettedInfoRepository.findByUserIdUser(userId).orElse(null);
        if(myProductionMaterialSettedInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myProductionMaterialSettedInfo not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myProductionMaterialSettedInfo not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyProductionMaterialSettedInfoDto myProductionMaterialSettedInfoDto = new MyProductionMaterialSettedInfoDto();
        myProductionMaterialSettedInfoDto.InitFromDbData(myProductionMaterialSettedInfo);
        map.put("myProductionMaterialSettedInfo", myProductionMaterialSettedInfoDto);

        List<MyArenaPlayLogForBattleRecord> myArenaPlayLogForBattleRecordsList = myArenaPlayLogForBattleRecordRepository.findAllByUseridUser(userId, sortByBattleEndTime());
        List<MyArenaPlayLogForBattleRecordDto> myArenaPlayLogForBattleRecordDtoList = new ArrayList<>();
        for(MyArenaPlayLogForBattleRecord temp : myArenaPlayLogForBattleRecordsList){
            MyArenaPlayLogForBattleRecordDto myArenaPlayLogForBattleRecordDto = new MyArenaPlayLogForBattleRecordDto();
            myArenaPlayLogForBattleRecordDto.InitFromDbData(temp);
            myArenaPlayLogForBattleRecordDtoList.add(myArenaPlayLogForBattleRecordDto);
        }
        map.put("myArenaPlayLogForBattleRecordsListWhenLoginTime", myArenaPlayLogForBattleRecordDtoList);

        ADLimitInfoTable adLimitInfoTable = gameDataTableService.ADLimitInfoTable().get(0);
        MyTodayViewingTable todayViewingTable = todayViewingTableRepository.findByUseridUser(userId)
                .orElse(null);
        if(todayViewingTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: TodayViewingTable not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: TodayViewingTable not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        if(todayViewingTable.IsResetTime())
            todayViewingTable.ResetViewingCount();
        MyTodayViewingDto myTodayViewingDto = new MyTodayViewingDto();
        myTodayViewingDto.SetTodayViewing(todayViewingTable,adLimitInfoTable.getMaxViewing());
        map.put("myTodayViewing",myTodayViewingDto);

        GotchaMileage gotchaMileage = gotchaMileageRepository.findByUseridUser(userId)
                .orElseThrow(() -> new MyCustomException("Fail! -> Cause: gotchaMileage not find userId.", ResponseErrorCode.NOT_FIND_DATA));

        GotchaMileageDto gotchaMileageDto = new GotchaMileageDto();
        gotchaMileageDto.InitFromDbData(gotchaMileage);
        map.put("mileage", gotchaMileageDto);

        //프로필 정보
        myProfileService.GetProfileInfo(userId, map);
        //영웅의 탑 시즌 정보
        myHeroTowerSeasonService.GetSeasonInfo(userId, map);
        //고대 던전 시즌 정보
        myAncientDragonSeasonService.GetSeasonInfo(userId, map);
        //시련의 던전 시즌 정보
        myOrdealDungeonSeasonService.GetSeasonInfo(userId, map);
        //아레나 시즌 정보
        myArenaSeasonService.GetSeasonInfoForLogin(userId, map);
        //천공의 계단 정보
        MyInfiniteTowerSaveData myInfiniteTowerSaveData = myInfiniteTowerSaveDataRepository.findByUseridUser(userId).orElse(null);
        if(myInfiniteTowerSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myInfiniteTowerSaveData not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myInfiniteTowerSaveData not find", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyInfiniteTowerSaveDataDto myInfiniteTowerSaveDataDto = new MyInfiniteTowerSaveDataDto();
        myInfiniteTowerSaveDataDto.setId(myInfiniteTowerSaveData.getId());
        myInfiniteTowerSaveDataDto.setUseridUser(myInfiniteTowerSaveData.getUseridUser());
        myInfiniteTowerSaveDataDto.setArrivedTopFloor(myInfiniteTowerSaveData.getArrivedTopFloor());
        MyInfiniteTowerRewardReceivedInfosDto myInfiniteTowerRewardReceivedInfosDto = JsonStringHerlper.ReadValueFromJson(myInfiniteTowerSaveData.getReceivedRewardInfoJson(), MyInfiniteTowerRewardReceivedInfosDto.class);
        myInfiniteTowerSaveDataDto.setMyInfiniteTowerRewardReceivedInfosDto(myInfiniteTowerRewardReceivedInfosDto);
        map.put("myInfiniteTowerSaveData",myInfiniteTowerSaveDataDto);

        LocalDateTime now = LocalDateTime.now();
        List<PickupTable> pickupTableList = pickupTableRepository.findAllByStartDateBeforeAndEndDateAfter(now, now);
        map.put("pickupTableList", pickupTableList);
        map.put("serverTime", user.getLastloginDate());
        map.put("user", user);


        myMissionsDataService.GetMyMissionData(userId, map);
        //패스 업적 : 체크 준비
        MyEternalPassMissionsData myEternalPassMissionsData = myEternalPassMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myEternalPassMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyEternalPassMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEternalPassMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyEternalPassMissionData = myEternalPassMissionsData.getJson_saveDataValue();
        EventMissionDataDto myEternalPassMissionDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyEternalPassMissionData, EventMissionDataDto.class);
        boolean changedEternalPassMissionsData = false;

        //미션 리셋 체크
        boolean missionReseted = false;
        //미션 리셋 체크
        if(myEternalPassMissionsData.IsResetDailyMissionClearTime()) {
            myEternalPassMissionDataDto.DailyMissionsReset();
            missionReseted = true;

            changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.LOGIN_COUNT.name(), "empty", gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
        }
        if(myEternalPassMissionsData.IsResetWeeklyMissionClearTime()) {
            myEternalPassMissionDataDto.WeeklyMissionsReset();
            if(!missionReseted)
                missionReseted = true;
        }
        if(missionReseted) {
            jsonMyEternalPassMissionData = JsonStringHerlper.WriteValueAsStringFromData(myEternalPassMissionDataDto);
            myEternalPassMissionsData.ResetSaveDataValue(jsonMyEternalPassMissionData);
        }

        map.put("myEternalPassMissionsDataDto", myEternalPassMissionDataDto);
        map.put("myEternalPassLastDailyMissionClearTime", myEternalPassMissionsData.getLastDailyMissionClearTime());
        map.put("myEternalPassLastWeeklyMissionClearTime", myEternalPassMissionsData.getLastWeeklyMissionClearTime());
        
        myEternalPassService.GetMyEternalPassInfo(userId, map);
        myShopService.getMyShopData(userId, map);
        */

        return map;
    }

    MyHotTimeInfoDto SetHotTimeInfo(Long hotTimeSchedulerId, String HotTimeInfo) {
        HotTimeFieldObjectInfoListDto hotTimeFieldObjectInfoListDto = JsonStringHerlper.ReadValueFromJson(HotTimeInfo, HotTimeFieldObjectInfoListDto.class);
        MyHotTimeInfoDto myHotTimeInfoDto = new MyHotTimeInfoDto();
        myHotTimeInfoDto.SettingHotTimeInfo(hotTimeFieldObjectInfoListDto.hotTimeFieldObjectInfoList);
        myHotTimeInfoDto.hotTimeSchedulerId = hotTimeSchedulerId;
        return myHotTimeInfoDto;
    }

    MyFieldExchangeItemEventDto SetFieldExchangeItemEvent(Long eventId, String fieldExchangeInfo) {
        FieldExchangeItemObjectDto fieldExchangeItemObjectInfoListDto = JsonStringHerlper.ReadValueFromJson(fieldExchangeInfo, FieldExchangeItemObjectDto.class);
        MyFieldExchangeItemEventDto myFieldExchangeItemEventDto = new MyFieldExchangeItemEventDto();
        myFieldExchangeItemEventDto.SettingMyFieldExchangeItemEvent(fieldExchangeItemObjectInfoListDto.getFieldInfo(), gameDataTableService.ExchangeItemEventTableList());
        myFieldExchangeItemEventDto.maxExchangeList = new ArrayList<>();
        for(ExchangeItemEventTable temp: gameDataTableService.ExchangeItemEventTableList()){
            MyFieldExchangeItemEventDto.MaxExchange maxExchange = new MyFieldExchangeItemEventDto.MaxExchange();
            maxExchange.SetMaxExchange(temp.getId(), temp.getMaxExchange());
            myFieldExchangeItemEventDto.maxExchangeList.add(maxExchange);
        }
        myFieldExchangeItemEventDto.eventId = eventId;
        return myFieldExchangeItemEventDto;
    }

    private Sort sortByBattleEndTime() {
        return Sort.by(Sort.Direction.DESC, "battleEndTime");
    }

    public Map<String, Object> setUserName(Long userId, String userName, Map<String, Object> map) {

        User findUser = userRepository.findById(userId).orElse(null);
        if(findUser == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: user not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: user not find", ResponseErrorCode.NOT_FIND_DATA);
        }
        findUser.SetUserName(userName);
        return map;
    }

    public Map<String, Object> eventJumped(Long userId, int startEventNo, int jumpedEventNo, Map<String, Object> map) {

        User findUser = userRepository.findById(userId).orElse(null);
        if(findUser == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: user not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: user not find", ResponseErrorCode.NOT_FIND_DATA);
        }

        findUser.EventJumped(jumpedEventNo);

        TutorialDataSettingForEventJump(findUser, startEventNo, jumpedEventNo, map);
        EventCheckForEventJump(userId, startEventNo, jumpedEventNo, map);
        map.put("currentEventNo", findUser.getNextEventNo());

        return map;
    }

    public Map<String, Object> eventComplete(Long userId, int currentEventNo, Map<String, Object> map) {

        User findUser = userRepository.findById(userId).orElse(null);
        if(findUser == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: user not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: user not find", ResponseErrorCode.NOT_FIND_DATA);
        }

//        if(currentEventNo >= 137) {
//            TutorialDataSetting(findUser, currentEventNo, map);
//            EventCheck(userId, currentEventNo, map);
//            map.put("currentEventNo", findUser.getNextEventNo());
//            return map;
//        }

        if(!findUser.EventComplete(currentEventNo)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Invaild EventNo", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Invaild EventNo", ResponseErrorCode.INVAILD_EVENTNO);
        }
        TutorialDataSetting(findUser, currentEventNo, map);
        EventCheck(userId, currentEventNo, map);
        map.put("currentEventNo", findUser.getNextEventNo());

        return map;
    }

    public Map<String, Object> IncreaseMaxInventorySlot(Long userId, Map<String, Object> map){

        User findUser = userRepository.findById(userId).orElse(null);
        if(findUser == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: user not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: user not find", ResponseErrorCode.NOT_FIND_DATA);
        }
        //확장비용 체크
        int cost = 30 + (((findUser.getHeroEquipmentInventoryMaxSlot() - 60) / 8) * 30);
        if(!findUser.SpendDiamond(cost)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail -> Cause: Need More Diamond", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Need More Diamond", ResponseErrorCode.NEED_MORE_DIAMOND);
        }
        if(!findUser.IncreaseSlot()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_ANIMORE_INCREASE_INVENTORY.getIntegerValue(), "Fail! -> Cause: CANT_ANIMORE_INCREASE_INVENTORY", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: CANT_ANIMORE_INCREASE_INVENTORY", ResponseErrorCode.CANT_ANIMORE_INCREASE_INVENTORY);
        }

        map.put("user", findUser);
        return map;
    }
    void EventCheckForEventJump(Long userId, int startEventNo, int jumpedEventNo, Map<String, Object> map) {
        int nextEventNo = startEventNo;
        do{
            EventCheck(userId, nextEventNo, map);
            nextEventNo++;
            if(nextEventNo > jumpedEventNo)
                break;
        }while(true);


    }
    void EventCheck(Long userId, int currentEventNo, Map<String, Object> map) {
        MyChapterSaveData myChapterSaveData = myChapterSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myChapterSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyChapterSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyChapterSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String saveDataString = myChapterSaveData.getSaveDataValue();
        ChapterSaveData chapterSaveData = JsonStringHerlper.ReadValueFromJson(saveDataString, ChapterSaveData.class);

        ChapterSaveData.ChapterPlayInfo selecteChapter = null;
        ChapterSaveData.ChapterPlayInfo nextChapter = null;
        for(ChapterSaveData.ChapterPlayInfo chapterPlayInfo : chapterSaveData.chapterData.chapterPlayInfosList) {
            nextChapter = chapterPlayInfo;
            if(!chapterPlayInfo.isOpend)
                break;
            selecteChapter = chapterPlayInfo;
        }

        ChapterSaveData.StagePlayInfo selecteStageInfo = null;
        for(ChapterSaveData.StagePlayInfo stagePlayInfo : selecteChapter.stagePlayInfosList){
            if(!stagePlayInfo.isOpend)
                break;
            selecteStageInfo = stagePlayInfo;
        }

        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: user not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: user not find", ResponseErrorCode.NOT_FIND_DATA);
        }

        if(selecteChapter.chapterNo != 0) {
            boolean changedChapterSaveData = false;
            if(selecteStageInfo == null){
                selecteStageInfo = selecteChapter.stagePlayInfosList.get(0);

                StringMaker.Clear();
                StringMaker.stringBuilder.append(selecteChapter.chapterNo);
                StringMaker.stringBuilder.append("-");
                StringMaker.stringBuilder.append(selecteStageInfo.stageNo);
                String findStage = StringMaker.stringBuilder.toString();
                List<StageOpenCondtionTable> stageOpenCondtionTableList = gameDataTableService.StageOpenConditionTableList();
                StageOpenCondtionTable stageOpenCondtionTable = stageOpenCondtionTableList.stream()
                        .filter(a -> a.getStage().equals(findStage))
                        .findAny()
                        .orElse(null);
                if(stageOpenCondtionTable != null) {
                    String conditionEventCode = stageOpenCondtionTable.getEventCode();
                    List<MainQuestInfoTable> mainQuestInfoTableListList = gameDataTableService.MainQuestInfoTableList();
                    MainQuestInfoTable mainQuestInfoTable = mainQuestInfoTableListList.stream()
                            .filter(a -> a.getCode().equals(conditionEventCode))
                            .findAny()
                            .orElse(null);
                    if (mainQuestInfoTable == null || mainQuestInfoTable.getIndex() < user.getNextEventNo()) {
                        selecteStageInfo.isOpend = true;
                        changedChapterSaveData = true;
                    }
                }
            }
            //챕터 오픈 조건 체크
            else if(selecteChapter.chapterNo < 8 && selecteStageInfo.stageNo == 20){
                int nextChapterNo = selecteChapter.chapterNo + 1;

                List<ChapterOpenConditionTable> chapterOpenConditionTableList = gameDataTableService.ChapterOpenConditionTableList();
                ChapterOpenConditionTable chapterOpenConditionTable = chapterOpenConditionTableList.stream()
                        .filter(a -> a.getChapterNo() == nextChapterNo)
                        .findAny()
                        .orElse(null);
                if(chapterOpenConditionTable != null) {

                    String conditionEventCode = chapterOpenConditionTable.getEventCode();

                    List<MainQuestInfoTable> mainQuestInfoTableListList = gameDataTableService.MainQuestInfoTableList();
                    MainQuestInfoTable mainQuestInfoTable = mainQuestInfoTableListList.stream()
                            .filter(a -> a.getCode().equals(conditionEventCode))
                            .findAny()
                            .orElse(null);
                    if (mainQuestInfoTable != null) {
                        if (mainQuestInfoTable.getIndex() < user.getNextEventNo()) {
                            nextChapter.isOpend = true;
                            nextChapter.stagePlayInfosList.get(0).isOpend = true;
                            changedChapterSaveData = true;
                        }
                    }
                }
            }
            //스테이지 오픈 조건 체크
            else if(selecteStageInfo.stageNo < 20 && selecteStageInfo.isCleared){
                int nextStageNo = selecteStageInfo.stageNo + 1;
                ChapterSaveData.StagePlayInfo nextStagePlayInfo = selecteChapter.stagePlayInfosList.stream()
                        .filter(a -> a.stageNo == nextStageNo)
                        .findAny()
                        .orElse(null);
                if(nextStagePlayInfo == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.INVAILD_CHAPTER_SAVEDATA.getIntegerValue(), "Fail -> Cause: Invaild stageNo Or not yet stage open", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail -> Cause: Invaild stageNo Or not yet stage open", ResponseErrorCode.INVAILD_CHAPTER_SAVEDATA);
                }

                StringMaker.Clear();
                StringMaker.stringBuilder.append(selecteChapter.chapterNo);
                StringMaker.stringBuilder.append("-");
                StringMaker.stringBuilder.append(nextStageNo);
                String findStage = StringMaker.stringBuilder.toString();
                List<StageOpenCondtionTable> stageOpenCondtionTableList = gameDataTableService.StageOpenConditionTableList();
                StageOpenCondtionTable stageOpenCondtionTable = stageOpenCondtionTableList.stream()
                        .filter(a -> a.getStage().equals(findStage))
                        .findAny()
                        .orElse(null);
                if(stageOpenCondtionTable != null) {
                    String conditionEventCode = stageOpenCondtionTable.getEventCode();
                    List<MainQuestInfoTable> mainQuestInfoTableListList = gameDataTableService.MainQuestInfoTableList();
                    MainQuestInfoTable mainQuestInfoTable = mainQuestInfoTableListList.stream()
                            .filter(a -> a.getCode().equals(conditionEventCode))
                            .findAny()
                            .orElse(null);
                    if (mainQuestInfoTable != null) {
                        if (mainQuestInfoTable.getIndex() <= user.getNextEventNo()) {

                            nextStagePlayInfo.isOpend = true;
                            changedChapterSaveData = true;
                        }
                    }
                }
            }
            if(changedChapterSaveData){
                String changeChapterSaveData = JsonStringHerlper.WriteValueAsStringFromData(chapterSaveData);
                map.put("changeChapterSaveData", changeChapterSaveData);
                myChapterSaveData.ResetSaveDataValue(changeChapterSaveData);
            }
        }
        //해당 이벤트 끝난후 6번 챕터로 강제 이동
        if(currentEventNo == 79) {
            MyFieldSaveData myFieldSaveData = myFieldSaveDataRepository.findByUseridUser(userId).orElse(null);
            if(myFieldSaveData == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyAncientDragonExpandSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: MyAncientDragonExpandSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
            }

            String json_saveDataValue = myFieldSaveData.getJson_saveDataValue();

            FieldSaveDataDto fieldSaveDataDto = JsonStringHerlper.ReadValueFromJson(json_saveDataValue, FieldSaveDataDto.class);
            fieldSaveDataDto.plaingFieldNo = 6;
            json_saveDataValue = JsonStringHerlper.WriteValueAsStringFromData(fieldSaveDataDto);
            myFieldSaveData.ResetSaveDataValue(json_saveDataValue);

            map.put("fieldSaveDataDto", fieldSaveDataDto);
            map.put("lastClearTime", myFieldSaveData.getLastClearTime());
        }
        //하드모드 오픈 시점.
        else if(currentEventNo == 92){
            MyFieldSaveData myFieldSaveData = myFieldSaveDataRepository.findByUseridUser(userId).orElse(null);
            if(myFieldSaveData == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyAncientDragonExpandSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: MyAncientDragonExpandSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
            }

            String json_saveDataValue = myFieldSaveData.getJson_saveDataValue();

            FieldSaveDataDto fieldSaveDataDto = JsonStringHerlper.ReadValueFromJson(json_saveDataValue, FieldSaveDataDto.class);
            fieldSaveDataDto.modeLevel = true;
            fieldSaveDataDto.hardModeOpenable = true;
            json_saveDataValue = JsonStringHerlper.WriteValueAsStringFromData(fieldSaveDataDto);
            myFieldSaveData.ResetSaveDataValue(json_saveDataValue);

            map.put("fieldSaveDataDto", fieldSaveDataDto);
            map.put("lastClearTime", myFieldSaveData.getLastClearTime());
        }
        //비공정 탑승 가능
        else if(currentEventNo == 93) {
            MyFieldSaveData myFieldSaveData = myFieldSaveDataRepository.findByUseridUser(userId).orElse(null);
            if(myFieldSaveData == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyAncientDragonExpandSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: MyAncientDragonExpandSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
            }

            String json_saveDataValue = myFieldSaveData.getJson_saveDataValue();

            FieldSaveDataDto fieldSaveDataDto = JsonStringHerlper.ReadValueFromJson(json_saveDataValue, FieldSaveDataDto.class);
            fieldSaveDataDto.changeableSkywalker = true;
            json_saveDataValue = JsonStringHerlper.WriteValueAsStringFromData(fieldSaveDataDto);
            myFieldSaveData.ResetSaveDataValue(json_saveDataValue);
            map.put("fieldSaveDataDto", fieldSaveDataDto);
            map.put("lastClearTime", myFieldSaveData.getLastClearTime());
        }
    }

    void TutorialDataSettingForEventJump(User user, int startEventNo, int jumpedEventNo, Map<String, Object> map) {
        int nextEventNo = startEventNo;
        do{
            TutorialDataSetting(user, nextEventNo, map);
            nextEventNo++;
            if(nextEventNo > jumpedEventNo)
                break;
        }while(true);
    }
    void TutorialDataSetting(User user, int currentEventNo, Map<String, Object> map){
        Long userId = user.getId();
        switch (currentEventNo){
            case 7: {
                //정보팝업(필드액션)표출 >스테이지 1-1 진입 >전투편성 튜토리얼 세릴 레타 장착
                List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
                MyCharacters cr_000Hero = myCharactersList.stream()
                        .filter(a -> a.getCodeHerostable().equals("cr_000"))
                        .findAny()
                        .orElse(null);

                MyCharacters cr_004Hero = myCharactersList.stream()
                        .filter(a -> a.getCodeHerostable().equals("cr_004"))
                        .findAny()
                        .orElse(null);

                MyTeamInfo myTeamInfo = myTeamInfoRepository.findByUseridUser(userId);

                myTeamInfo.AddTeam(cr_000Hero.getId(), 1, Defines.TEAM_BUILDING_KIND.STAGE_PLAY_TEAM);
                myTeamInfo.AddTeam(cr_004Hero.getId(), 2, Defines.TEAM_BUILDING_KIND.STAGE_PLAY_TEAM);
                myTeamInfo.SwitchTeam(1, 2, Defines.TEAM_BUILDING_KIND.STAGE_PLAY_TEAM);
                MyTeamInfoDto myTeamInfoDto = new MyTeamInfoDto();
                myTeamInfoDto.InitFromDBData(myTeamInfo);
                map.put("myTeamInfo", myTeamInfoDto);
            }
            break;
            case 11: {
                //[필드튜토리얼] 퀵메뉴 용사관리 - 장비 생성 및 장비 장착 + //[장비관리튜토리얼] - 장비 장착 +  //[장비관리튜토리얼] - 장비 강화
                List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
                HeroEquipmentsTable firstWeaponInfo = heroEquipmentsTableList.get(161);
                HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
                EquipmentOptionsInfoTable equipmentOptionsInfoTable = gameDataTableService.OptionsInfoTableList().get(4);
                HeroEquipmentInventory generatedItem = EquipmentCalculate.CreateFirstWeapon(userId, firstWeaponInfo, classValues, equipmentOptionsInfoTable);
                generatedItem.Strengthen(1, 15, generatedItem.getNextExp(), generatedItem.getDecideDefaultAbilityValue(), generatedItem.getDecideSecondAbilityValue());
                heroEquipmentInventoryRepository.save(generatedItem);

                HeroEquipmentInventoryDto generatedItemDto = new HeroEquipmentInventoryDto();
                generatedItemDto.InitFromDbData(generatedItem);
                map.put("generatedItem", generatedItemDto);

                MyEquipmentDeck myEquipmentDeck = myEquipmentDeckRepository.findByUserIdUser(userId)
                        .orElseThrow(() -> new MyCustomException("Fail! -> Cause: MyEquipmentDeck not find userId.", ResponseErrorCode.NOT_FIND_DATA));

                String selectedItemKind = firstWeaponInfo.getKind();
                myEquipmentDeck.Equip(1, generatedItem.getId(), selectedItemKind);
                MyEquipmentDeckDto myEquipmentDeckDto = new MyEquipmentDeckDto();
                myEquipmentDeckDto.InitFromDbData(myEquipmentDeck);
                map.put("myEquipmentDeck", myEquipmentDeckDto);
            }
            break;
            case 13: {
                //[필드튜토리얼] (퀵메뉴_탐색대) 후 정보팝업(05)
                user.AddGold(100);
                user.AddLinkforcePoint(100);
                map.put("user", user);

                List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
                MyCharacters mainHero = myCharactersList.stream()
                        .filter(a -> a.getCodeHerostable().equals("hero"))
                        .findAny()
                        .orElse(null);
                if(mainHero == null)
                    throw new MyCustomException("Fail! -> Cause: SpendableItmyCharactersListind.", ResponseErrorCode.NOT_FIND_DATA);
                mainHero.AddExp(10, mainHero.getMaxLevel());
                map.put("myCharacter", mainHero);
            }
            break;
            case 15: {
                //[동료관리 튜토리얼] (강화)
                List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);

                MyCharacters cr_000Hero = myCharactersList.stream()
                        .filter(a -> a.getCodeHerostable().equals("cr_000"))
                        .findAny()
                        .orElse(null);
                if (cr_000Hero == null)
                    throw new MyCustomException("Fail! -> Cause: myCharactersList.", ResponseErrorCode.NOT_FIND_DATA);
                cr_000Hero.setMaxLevel(2);
                cr_000Hero.AddExp(cr_000Hero.getNextExp(), 2);
                map.put("myCharacter", cr_000Hero);

                //해당 유저의 MyLinkforceInfo 검색
                MyLinkforceInfo myLinkforceInfo = myLinkforceInfoRepository.findByUseridUser(userId)
                        .orElse(null);
                if (myLinkforceInfo == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myLinkforceInfo not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: myLinkforceInfo not find userId.", ResponseErrorCode.NOT_FIND_DATA);
                }

                //json_LinkforceInfos을 활용 LinkforceInfoDtosList를 만들고 linkforceId에 해당하는 linkforceInfoDto Get
                LinkforceOpenDtosList linkforceOpenDtosList = JsonStringHerlper.ReadValueFromJson(myLinkforceInfo.getJson_LinkforceInfos(), LinkforceOpenDtosList.class);
                LinkforceOpenDtosList.CompanionLinkforceOpenInfo companionLinkforceInfo = linkforceOpenDtosList.openInfoList.stream()
                        .filter(a -> a.code.equals(cr_000Hero.getCodeHerostable()))
                        .findAny()
                        .orElse(null);
                if (companionLinkforceInfo == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Not find companionLinkforceInfo.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Not find companionLinkforceInfo.", ResponseErrorCode.NOT_FIND_DATA);
                }
                companionLinkforceInfo.linkforceOpenInfoList.set(0, 1);
                String linkforceInfoDtosListStr = JsonStringHerlper.WriteValueAsStringFromData(linkforceOpenDtosList);
                myLinkforceInfo.ResetLinkforceInfos(linkforceInfoDtosListStr);

                map.put("companionLinkforceInfo", companionLinkforceInfo);
            }
                break;
            case 19: {
                //[선율의문 튜토리얼]
                GotchaResponseDto gotchaResponseDto = new GotchaResponseDto();

                //쉐릴 케릭터 조각 20개 + 40개
                int gettingCount = 60;
                String cr_000_characterPieceCode = "characterPiece_cr_000";
                List<ItemType> itemTypeList = itemTypeRepository.findAll();
                ItemType characterPieceItemType = itemTypeList.stream()
                        .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece)
                        .findAny()
                        .orElse(null);

                List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
                BelongingCharacterPieceTable cr_000_characterPiece = belongingCharacterPieceTableList.stream()
                        .filter(a -> a.getCode().equals(cr_000_characterPieceCode))
                        .findAny()
                        .orElse(null);
                if (cr_000_characterPiece == null)
                    throw new MyCustomException("Fail! -> Cause: Can't Find characterPiece.", ResponseErrorCode.NOT_FIND_DATA);

                List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                BelongingInventory cr_000_myCharacterPieceItem = belongingInventoryList.stream()
                        .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece && a.getItemId() == cr_000_characterPiece.getId())
                        .findAny()
                        .orElse(null);

                if (cr_000_myCharacterPieceItem == null) {
                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.setUseridUser(userId);
                    belongingInventoryDto.setItemId(cr_000_characterPiece.getId());
                    belongingInventoryDto.setCount(gettingCount);
                    belongingInventoryDto.setItemType(characterPieceItemType);
                    cr_000_myCharacterPieceItem = belongingInventoryDto.ToEntity();
                    cr_000_myCharacterPieceItem = belongingInventoryRepository.save(cr_000_myCharacterPieceItem);
                    belongingInventoryList.add(cr_000_myCharacterPieceItem);

                    gotchaResponseDto.getChangedBelongingInventoryList().add(belongingInventoryDto);
                } else {
                    cr_000_myCharacterPieceItem.AddItem(gettingCount, cr_000_characterPiece.getStackLimit());

                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.setUseridUser(userId);
                    belongingInventoryDto.setItemId(cr_000_characterPiece.getId());
                    belongingInventoryDto.setCount(gettingCount);
                    belongingInventoryDto.setItemType(characterPieceItemType);

                    gotchaResponseDto.getChangedBelongingInventoryList().add(belongingInventoryDto);
                }
                map.put("belongingInventoryList", belongingInventoryList);
            }
            break;
            case 20: {
                //[동료관리 튜토리얼] (링크업)

                List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
                MyCharacters cr_000Hero = myCharactersList.stream()
                        .filter(a -> a.getCodeHerostable().equals("cr_000"))
                        .findAny()
                        .orElse(null);
                if (cr_000Hero == null)
                    throw new MyCustomException("Fail! -> Cause: myCharactersList.", ResponseErrorCode.NOT_FIND_DATA);

                List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                BelongingInventory characterPiece = belongingInventoryList.stream()
                        .filter(a -> a.getItemType().getItemTypeName().equals(ItemTypeName.BelongingItem_CharacterPiece))
                        .findAny()
                        .orElse(null);
                if(characterPiece == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_CHARACTERPIECE.getIntegerValue(), "Fail! -> Cause: Need more characterPiece", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more characterPiece", ResponseErrorCode.NEED_MORE_CHARACTERPIECE);
                }
                
                int cost = LinkAbilityCostCalculator.GetNextLinkAbilityCost(cr_000Hero.getLinkAbilityLevel());
                characterPiece.SpendItem(cost);


                if(!cr_000Hero.LevelUpLinkAbility()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_LEVELUP_LINKABILITY.getIntegerValue(), "Fail! -> Cause: Cant levelup LinkAbility.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Cant levelup LinkAbility.", ResponseErrorCode.CANT_LEVELUP_LINKABILITY);
                }
                map.put("myCharacter", cr_000Hero);

                map.put("belongingInventoryList", belongingInventoryList);
            }
            break;
        }
    }

    void TutorialLinkweaponStrengthen(Long userId){

//        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
//        MyCharacters myCharacter = myCharactersList.stream()
//                .filter(a -> a.getCodeHerostable().equals("cr_000"))
//                .findAny()
//                .orElse(null);
//        if(myCharacter == null)
//            throw new MyCustomException("Fail -> Cause: Can't Find Character", ResponseErrorCode.NOT_FIND_DATA);
//
//        //해당 유저의 MyLinkweapon 검색
//        MyLinkweaponInfo myLinkweaponInfo = myLinkweaponInfoRepository.findByUseridUser(userId)
//                .orElseThrow(()->new MyCustomException("Fail! -> Cause: myLinkweaponInfo not find userId.", ResponseErrorCode.NOT_FIND_DATA));
//
//        LinkweaponInfoDtosList linkweaponInfoDtosList = JsonStringHerlper.ReadValueFromJson(myLinkweaponInfo.getJson_LinkweaponRevolution(), LinkweaponInfoDtosList.class);
//
//        LinkweaponInfoDtosList.CompanionLinkweaponInfo companionLinkweaponInfo = linkweaponInfoDtosList.companionLinkweaponInfoList.stream()
//                .filter(a -> a.ownerCode.equals(myCharacter.getCodeHerostable()))
//                .findAny()
//                .orElse(null);
//        if(companionLinkweaponInfo == null)
//            throw new MyCustomException("Fail! -> Cause: Not find companionweaponInfo.", ResponseErrorCode.NOT_EXIST_CODE);
//       int selectedWeaponId = 0;
//        LinkweaponInfoDtosList.LinkweaponInfoDto linkweaponInfoDto = companionLinkweaponInfo.linkweaponInfoDtoList.stream()
//                .filter(a -> a.id == selectedWeaponId)
//                .findAny()
//                .orElse(null);
//        if(linkweaponInfoDto == null)
//            throw new MyCustomException("Fail! -> Cause: Not find LinkweaponInfoDto.", ResponseErrorCode.NOT_FIND_DATA);
//
//        //링크 포인트 체크
//        User user = userRepository.findById(userId).orElseThrow(
//                () -> new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA));
//
//        List<LinkweaponTalentsTable> linkweaponTalentsTableList = gameDataTableService.LinkweaponTalentsTableList();
//        LinkweaponTalentsTable linkweaponTalentsTable = linkweaponTalentsTableList.stream()
//                .filter(a->a.getEquipmentID() == selectedWeaponId && a.getOwner().equals(myCharacter.getCodeHerostable()))
//                .findAny()
//                .orElse(null);
//        if(linkweaponTalentsTable == null)
//            throw new MyCustomException("Fail! -> Cause: Not find linkweaponTalentsTable.", ResponseErrorCode.NOT_FIND_DATA);
//        String json_LinkpointCostsForStrength = linkweaponTalentsTable.getLinkpointCostsForStrength();
//        LinkpointCost linkpointCost = JsonStringHerlper.ReadValueFromJson(json_LinkpointCostsForStrength, LinkpointCost.class);
//        //linkweaponInfoDto.level 1 은 진화로 인해 오픈 되었다는 뜻. 실제 강화 레벨은 해당 값 - 1이다.
//        int strengthenLevel = linkweaponInfoDto.level-1;
//        if(!user.SpendLinkforcePoint(linkpointCost.cntList.get(strengthenLevel))) {
//            throw new MyCustomException("Fail -> Cause: Need More LinkforcePoint", ResponseErrorCode.NEED_MORE_LINKFORCEPOINT);
//        }
//
//        //골드 체크
//        String json_GoldCostsForStrength = linkweaponTalentsTable.getGoldCostForStrength();
//        GoldCost goldCost = JsonStringHerlper.ReadValueFromJson(json_GoldCostsForStrength, GoldCost.class);
//        if(!user.SpendGold(goldCost.cntList.get(strengthenLevel))) {
//            throw new MyCustomException("Fail -> Cause: Need More LinkforcePoint", ResponseErrorCode.NEED_MORE_LINKFORCEPOINT);
//        }
//
//        //재료 체크
//        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
//        String json_NeedMaterialsForStrength = linkweaponTalentsTable.getNeedMaterialsForStrength();
//        MaterialsCost materialsCost = JsonStringHerlper.ReadValueFromJson(json_NeedMaterialsForStrength, MaterialsCost.class);
//        MaterialsCost.NeedMaterialsInfo needMaterialsInfo = materialsCost.needMaterialList.get(strengthenLevel);
//        List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList = gameDataTableService.EquipmentMaterialInfoTableList();
//        List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
//
//        for(MaterialsCost.NeedMaterial needMaterial : needMaterialsInfo.list) {
//
//            if(needMaterial.code.equals(myCharacter.getCodeHerostable())){
//                //케릭터 조각
//                if(!myCharacter.SpendPieces(needMaterial.cnt))
//                    throw  new MyCustomException("Fail! -> Cause: Need more characterPiece", ResponseErrorCode.NEED_MORE_CHARACTERPIECE);
//            }
//            else if(needMaterial.code.contains("linkweapon")) {
//                //브론즈 키, 실버 키, 골드 키
//                SpendableItemInfoTable spendableItemInfoTable = spendableItemInfoTableList.stream()
//                        .filter(a -> a.getCode().equals(needMaterial.code))
//                        .findAny()
//                        .orElse(null);
//                if(spendableItemInfoTable == null)
//                    throw  new MyCustomException("Fail! -> Cause: spendableItemInfoTable Can't Find", ResponseErrorCode.NOT_EXIST_CODE);
//
//                BelongingInventory belongingInventoryItem = belongingInventoryList.stream()
//                        .filter(a -> a.getItemId() == spendableItemInfoTable.getId() && a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
//                        .findAny()
//                        .orElse(null);
//                if(belongingInventoryItem == null)
//                    throw  new MyCustomException("Fail! -> Cause: BelongingInventory Can't Find", ResponseErrorCode.NEED_MORE_MATERIAL);
//                int needCount = needMaterial.cnt;
//                if(!belongingInventoryItem.SpendItem(needCount))
//                    throw  new MyCustomException("Fail! -> Cause: Need more BelongingInventoryItem count", ResponseErrorCode.NEED_MORE_MATERIAL);
//            }
//        }
//        linkweaponInfoDto.level++;
//
//        String updated_Json_LinkweaponRevolution = JsonStringHerlper.WriteValueAsStringFromData(linkweaponInfoDtosList);
//        /*정보 업데이트*/
//        myLinkweaponInfo.ResetLinkweaponRevolution(updated_Json_LinkweaponRevolution);
//
//        map.put("myCharacter", myCharacter);
//        map.put("companionLinkweaponInfoList", linkweaponInfoDtosList.companionLinkweaponInfoList);
//        map.put("user", user);
//        map.put("belongingInventoryList", belongingInventoryList);
    }

    void GetHeroFromEvent(Long userId, String heroCode, Map<String, Object> map) {
        List<MyCharacters> myHeros = myCharactersRepository.findAllByuseridUser(userId);
        for(MyCharacters myCharacter : myHeros) {
            if(myCharacter.getCodeHerostable().equals(heroCode))
            {
                myCharacter.Gotcha();
                MyCharactersBaseDto myCharactersBaseDto = new MyCharactersBaseDto();
                myCharactersBaseDto.InitFromDbData(myCharacter);
                map.put("myCharacter", myCharactersBaseDto);
                break;
            }
        }
    }

}