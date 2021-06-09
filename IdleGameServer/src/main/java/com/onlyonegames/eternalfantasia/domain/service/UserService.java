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
import com.onlyonegames.eternalfantasia.domain.model.entity.*;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.*;

import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
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
    private final ErrorLoggingService errorLoggingService;
    private final GameDataTableService gameDataTableService;



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

//        if(user.isBlackUser()) {
//            errorLoggingService.SetErrorLog(user.getId(), ResponseErrorCode.BLACK_USER.getIntegerValue(), "Black User", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
//            throw new MyCustomException("Black User", ResponseErrorCode.BLACK_USER);
//        }

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

//    public Map<String, Object> IncreaseMaxInventorySlot(Long userId, Map<String, Object> map){
//
//        User findUser = userRepository.findById(userId).orElse(null);
//        if(findUser == null) {
//            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: user not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
//            throw new MyCustomException("Fail! -> Cause: user not find", ResponseErrorCode.NOT_FIND_DATA);
//        }
//        //확장비용 체크
//        int cost = 30 + (((findUser.getHeroEquipmentInventoryMaxSlot() - 60) / 8) * 30);
//        if(!findUser.SpendDiamond(cost)) {
//            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail -> Cause: Need More Diamond", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
//            throw new MyCustomException("Fail -> Cause: Need More Diamond", ResponseErrorCode.NEED_MORE_DIAMOND);
//        }
//        if(!findUser.IncreaseSlot()) {
//            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_ANIMORE_INCREASE_INVENTORY.getIntegerValue(), "Fail! -> Cause: CANT_ANIMORE_INCREASE_INVENTORY", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
//            throw new MyCustomException("Fail! -> Cause: CANT_ANIMORE_INCREASE_INVENTORY", ResponseErrorCode.CANT_ANIMORE_INCREASE_INVENTORY);
//        }
//
//        map.put("user", findUser);
//        return map;
//    }

}