package com.onlyonegames.eternalfantasia.domain.service.Dungeon;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.ChapterSaveDto.ChapterSaveData;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.MyAncientDragonExpandSaveDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.MyAncientDragonStagePlayDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.EventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.HeroEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CharacterFatigabilityLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CurrencyLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.EquipmentLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyCharactersBaseDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyAncientDragonExpandSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyAncientDragonStagePlayData;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyTeamInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyAncientDragonExpandSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyAncientDragonStagePlayDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.HeroEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.ItemTypeRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyTeamInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import com.onlyonegames.eternalfantasia.etc.EquipmentCalculate;
import com.onlyonegames.eternalfantasia.etc.EquipmentItemCategory;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.util.MathHelper;
import lombok.AllArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyAncientDragonPlayService {
    private final UserRepository userRepository;
    private final MyCharactersRepository myCharactersRepository;
    private final HeroEquipmentInventoryRepository heroEquipmentInventoryRepository;
    private final MyAncientDragonStagePlayDataRepository myAncientDragonStagePlayDataRepository;
    private final MyAncientDragonExpandSaveDataRepository myAncientDragonExpandSaveDataRepository;
    private final MyTeamInfoRepository myTeamInfoRepository;
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;

    public Map<String, Object> AncientDragonPlayTimeSet(Long userId, Map<String, Object> map) {
        MyAncientDragonExpandSaveData myAncientDragonExpandSaveData = myAncientDragonExpandSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myAncientDragonExpandSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyOrdealDungeonExpandSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyOrdealDungeonExpandSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        if(myAncientDragonExpandSaveData.getPlayableRemainCount() == 0) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.DONT_PLAY_ANYMORE.getIntegerValue(), "Fail -> Cause: Don't play animore today", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Don't play animore today", ResponseErrorCode.DONT_PLAY_ANYMORE);
        }


        return map;
    }

    public Map<String, Object> StartAncientDragonPlay(Long userId, int floorNo, Map<String, Object> map) {

        MyAncientDragonStagePlayData stagePlayData = myAncientDragonStagePlayDataRepository.findByUseridUser(userId);
        if(stagePlayData == null)  {
            MyAncientDragonStagePlayDataDto newStagePlayDataDto = new MyAncientDragonStagePlayDataDto();
            newStagePlayDataDto.setUseridUser(userId);
            stagePlayData = myAncientDragonStagePlayDataRepository.save(newStagePlayDataDto.ToEntity());
        }
        stagePlayData.StartStagePlay();

        /*업적 : 체크 준비*/
        MyMissionsData myMissionsData = myMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyMissionData = myMissionsData.getJson_saveDataValue();
        MissionsDataDto myMissionsDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyMissionData, MissionsDataDto.class);
        boolean changedMissionsData = false;
        /* 업적 : 고대 드래곤 도전 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.PLAY_ANCIENT_DRAGON.name(), "empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

        /*업적 : 미션 데이터 변경점 적용*/
        if(changedMissionsData) {
            jsonMyMissionData = JsonStringHerlper.WriteValueAsStringFromData(myMissionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMyMissionData);
            map.put("exchange_myMissionsData", true);
            map.put("myMissionsDataDto", myMissionsDataDto);
            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());
        }
        /*패스 업적 : 체크 준비*/
        MyEternalPassMissionsData myEternalPassMissionsData = myEternalPassMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myEternalPassMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEternalPassMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyEternalPassMissionData = myEternalPassMissionsData.getJson_saveDataValue();
        EventMissionDataDto myEternalPassMissionDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyEternalPassMissionData, EventMissionDataDto.class);
        boolean changedEternalPassMissionsData = false;
        /* 패스 업적 : 고대 드래곤 도전 미션 체크*/
        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.PLAY_ANCIENT_DRAGON.name(),"empty", gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
        /* 패스 업적 : 미션 데이터 변경점 적용*/
        if(changedEternalPassMissionsData) {
            jsonMyEternalPassMissionData = JsonStringHerlper.WriteValueAsStringFromData(myEternalPassMissionDataDto);
            myEternalPassMissionsData.ResetSaveDataValue(jsonMyEternalPassMissionData);
            map.put("exchange_myEternalPassMissionsData", true);
            map.put("myEternalPassMissionsDataDto", myEternalPassMissionDataDto);
            map.put("myEternalPassLastDailyMissionClearTime", myEternalPassMissionsData.getLastDailyMissionClearTime());
            map.put("myEternalPassLastWeeklyMissionClearTime", myEternalPassMissionsData.getLastWeeklyMissionClearTime());
        }
        return map;
    }

    /*층수에 따른 보상을 각 인벤토리에 저장 해주고 리턴*/
    public Map<String, Object> AncientDragonClear(Long userId, int floorNo, Map<String, Object> map) {
        MyAncientDragonExpandSaveData myAncientDragonExpandSaveData = myAncientDragonExpandSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myAncientDragonExpandSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyAncientDragonExpandSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyAncientDragonExpandSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String json_saveDataValue = myAncientDragonExpandSaveData.getJson_saveDataValue();
        ChapterSaveData chapterSaveData = JsonStringHerlper.ReadValueFromJson(json_saveDataValue, ChapterSaveData.class);
        ChapterSaveData.ChapterPlayInfo chapterPlayInfo  = chapterSaveData.chapterData.chapterPlayInfosList.get(0);
        ChapterSaveData.StagePlayInfo stagePlayInfo = chapterPlayInfo.stagePlayInfosList.stream()
                .filter(a -> a.stageNo == floorNo && a.isOpend)
                .findAny()
                .orElse(null);
        if(stagePlayInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.INVAILD_CHAPTER_SAVEDATA.getIntegerValue(), "Fail -> Cause: Invaild floor Or not yet stage open", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Invaild floor Or not yet stage open", ResponseErrorCode.INVAILD_CHAPTER_SAVEDATA);
        }
        myAncientDragonExpandSaveData.Clear();
        stagePlayInfo.isCleared = true;

        /*플레이 데이터 클리어 셋팅*/
        MyAncientDragonStagePlayData myAncientDragonStagePlayData = myAncientDragonStagePlayDataRepository.findByUseridUser(userId);
        myAncientDragonStagePlayData.ClearStagePlay();

        int index = floorNo - 1;
        List<AncientDragonStageTable> stageTableList = gameDataTableService.AncientDragonStageTableList();
        AncientDragonStageTable ancientDragonStageTable = stageTableList.get(index);

        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }

        String[] rewardsArray = ancientDragonStageTable.getRepeatReward().split(",");

        for(String rewardInfo : rewardsArray) {
            String[] rewardInfos = rewardInfo.split(":");
            String rewardCode = rewardInfos[0];
            int gettingCount = Integer.parseInt(rewardInfos[1]);
            if(rewardCode.contains("lowDragonScale")){
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousValue = user.getLowDragonScale();
                user.AddLowDragonScale(gettingCount);
                currencyLogDto.setCurrencyLogDto("던전보상 - 용의 시련 "+floorNo+"층", "용의 비늘(전설)", previousValue, gettingCount, user.getLowDragonScale());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                map.put("lowDragonScale", gettingCount);
            }
            else if(rewardCode.contains("middleDragonScale")){
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousValue = user.getMiddleDragonScale();
                user.AddMiddleDragonScale(gettingCount);
                currencyLogDto.setCurrencyLogDto("던전보상 - 용의 시련 "+floorNo+"층", "용의 비늘(신성)", previousValue, gettingCount, user.getMiddleDragonScale());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                map.put("middleDragonScale", gettingCount);
            }
            else if(rewardCode.contains("highDragonScale")){
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousValue = user.getHighDragonScale();
                user.AddHighDragonScale(gettingCount);
                currencyLogDto.setCurrencyLogDto("던전보상 - 용의 시련 "+floorNo+"층", "용의 비늘(고대)", previousValue, gettingCount, user.getHighDragonScale());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                map.put("highDragonScale", gettingCount);
            }
            else {
//                rewardInfo = rewardsArray[1];
//                rewardInfos = rewardInfo.split(":");
//                rewardCode = rewardInfos[0];
                HeroEquipmentInventoryDto heroEquipmentInventoryDto = AddEquipmentItem(userId, rewardCode, "던전보상 - 용의 시련 "+floorNo+"층");
                map.put("heroEquipment", heroEquipmentInventoryDto);
            }
        }

        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);

        //팀동료 케릭터들 피로도 깍기
        MyTeamInfo myTeamInfo = myTeamInfoRepository.findByUseridUser(userId);
        String[] myTeams = myTeamInfo.getAncientDragonDungeonPlayTeam().split(",");
        List<Long> teamIdList = new ArrayList<>();
        for(int i = 0; i < myTeams.length; i++) {
            Long characterId = Long.parseLong(myTeams[i]);
            if(!characterId.equals(0L)) {
                teamIdList.add(characterId);
            }
        }
        /*패스 업적 : 체크 준비*/
        MyEternalPassMissionsData myEternalPassMissionsData = myEternalPassMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myEternalPassMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEternalPassMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyEternalPassMissionData = myEternalPassMissionsData.getJson_saveDataValue();
        EventMissionDataDto myEternalPassMissionDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyEternalPassMissionData, EventMissionDataDto.class);
        boolean changedEternalPassMissionsData = false;

        // List<MyCharacters> myTeamCharactersList = new ArrayList<>();
        //메인영웅(code:hero)은 제외
        List<Long> characterId = new ArrayList<>();
        List<Integer> previousFatigability = new ArrayList<>();
        List<Integer> presentFatigability = new ArrayList<>();
        int spendFatigability = 10;
        for(MyCharacters myCharacter : myCharactersList){
            for(Long myTeamCharactersId : teamIdList){
                if(myCharacter.getId().equals(myTeamCharactersId)){
                    if(!myCharacter.getCodeHerostable().equals("hero")) {
                        characterId.add(myCharacter.getId());
                        previousFatigability.add(myCharacter.getFatigability());
                        myCharacter.SpendFatigability(spendFatigability);
                        presentFatigability.add(myCharacter.getFatigability());
                        /* 패스 업적 : 피로도 소모 체크*/
                        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.SPEND_FATIGABILITY.name(),"empty", spendFatigability, gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
                    }
                    //     myTeamCharactersList.add(myCharacter);
                }
            }
        }
        if(!characterId.isEmpty()){
            CharacterFatigabilityLogDto characterFatigabilityLogDto = new CharacterFatigabilityLogDto();
            characterFatigabilityLogDto.setCharacterFatigabilityLogDto("용의 시련 "+floorNo+"층 진행", characterId, previousFatigability, presentFatigability);
            String fatigabilityLog = JsonStringHerlper.WriteValueAsStringFromData(characterFatigabilityLogDto);
            loggingService.setLogging(userId, 5, fatigabilityLog);
        }
        List<MyCharactersBaseDto> myCharactersBaseDtoList = new ArrayList<>();
        for(MyCharacters temp : myCharactersList){
            MyCharactersBaseDto myCharactersBaseDto = new MyCharactersBaseDto();
            myCharactersBaseDto.InitFromDbData(temp);
            myCharactersBaseDtoList.add(myCharactersBaseDto);
        }
        MyAncientDragonExpandSaveDataDto myAncientDragonExpandSaveDataDto = new MyAncientDragonExpandSaveDataDto();
        myAncientDragonExpandSaveDataDto.InitFromDbData(myAncientDragonExpandSaveData);
        map.put("myCharactersList",myCharactersBaseDtoList);
        map.put("user", user);
        map.put("myAncientDragonExpandSaveData", myAncientDragonExpandSaveDataDto);

        /*업적 : 체크 준비*/
        MyMissionsData myMissionsData = myMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyMissionData = myMissionsData.getJson_saveDataValue();
        MissionsDataDto myMissionsDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyMissionData, MissionsDataDto.class);
        boolean changedMissionsData = false;
        /* 업적 : 고대 드래곤 클리어 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.CLEAR_ANCIENT_DRAGON.name(), "empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

        /*업적 : 미션 데이터 변경점 적용*/
        if(changedMissionsData) {
            jsonMyMissionData = JsonStringHerlper.WriteValueAsStringFromData(myMissionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMyMissionData);
            map.put("exchange_myMissionsData", true);
            map.put("myMissionsDataDto", myMissionsDataDto);
            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());
        }

        /* 패스 업적 : 미션 데이터 변경점 적용*/
        if(changedEternalPassMissionsData) {
            jsonMyEternalPassMissionData = JsonStringHerlper.WriteValueAsStringFromData(myEternalPassMissionDataDto);
            myEternalPassMissionsData.ResetSaveDataValue(jsonMyEternalPassMissionData);
            map.put("exchange_myEternalPassMissionsData", true);
            map.put("myEternalPassMissionsDataDto", myEternalPassMissionDataDto);
            map.put("myEternalPassLastDailyMissionClearTime", myEternalPassMissionsData.getLastDailyMissionClearTime());
            map.put("myEternalPassLastWeeklyMissionClearTime", myEternalPassMissionsData.getLastWeeklyMissionClearTime());
        }
        return map;
    }

    HeroEquipmentInventoryDto AddEquipmentItem(Long userId, String gettingItemCode, String workingPosition) {

        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<HeroEquipmentInventory> heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(userId);
        if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
        }

        String decideGrade = "Normal";
        List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();

        switch (gettingItemCode) {
            case "equipmentItem_NormalAll":
                decideGrade = "Normal";
                break;
            case "equipmentItem_RareAll":
                decideGrade = "Rare";
                break;
            case "equipmentItem_HeroAll":
                decideGrade = "Hero";
                break;
            case "equipmentItem_LegendAll":
                decideGrade = "Legend";
                break;
            case "equipmentItem_DivineAll":
                decideGrade = "Divine";
                break;
            case "equipmentItem_AncientAll":
                decideGrade = "Ancient";
                break;
        }

        List<HeroEquipmentsTable> probabilityList = EquipmentCalculate.GetProbabilityList(heroEquipmentsTableList, EquipmentItemCategory.ALL, decideGrade);

        int randValue = (int)MathHelper.Range(0, probabilityList.size());
        HeroEquipmentsTable selectEquipment = probabilityList.get(randValue);

        //optionsInfoTableList = optionsInfoTableList == null ? equipmentOptionsInfoTableRepository.findAll() : optionsInfoTableList;
        List<EquipmentOptionsInfoTable> optionsInfoTableList = gameDataTableService.OptionsInfoTableList();
        HeroEquipmentInventory generatedItem = EquipmentCalculate.CreateEquipment(userId, selectEquipment, optionsInfoTableList, gameDataTableService.HeroEquipmentClassProbabilityTableList());
        generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
        HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
        heroEquipmentInventoryDto.InitFromDbData(generatedItem);
        EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
        equipmentLogDto.setEquipmentLogDto(workingPosition+" - ["+generatedItem.getId()+"]", generatedItem.getId(), "추가", heroEquipmentInventoryDto);
        String log = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
        loggingService.setLogging(userId, 2, log);
        return heroEquipmentInventoryDto;
    }

    /*케릭터가 죽으면 던전 클리어 실패*/
    public Map<String, Object> AncientDragonFail(Long userId, int floor, Map<String, Object> map) {
        /*플레이 데이터 실패 셋팅*/
        MyAncientDragonStagePlayData stagePlayData = myAncientDragonStagePlayDataRepository.findByUseridUser(userId);
        stagePlayData.DefeatStagePlay();
//        //팀동료 케릭터들 피로도 깍기
//        MyTeamInfo myTeamInfo = myTeamInfoRepository.findByUseridUser(userId);
//        String[] myTeams = myTeamInfo.getAncientDragonDungeonPlayTeam().split(",");
//        List<Long> teamIdList = new ArrayList<>();
//        for(int i = 0; i < myTeams.length; i++) {
//            Long characterId = Long.parseLong(myTeams[i]);
//            if(!characterId.equals(0L)) {
//                teamIdList.add(characterId);
//            }
//        }
//        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
//
//        // List<MyCharacters> myTeamCharactersList = new ArrayList<>();
//        //메인영웅(code:hero)은 제외
//        List<Long> characterId = new ArrayList<>();
//        List<Integer> previousFatigability = new ArrayList<>();
//        List<Integer> presentFatigability = new ArrayList<>();
//        for(MyCharacters myCharacter : myCharactersList){
//            for(Long myTeamCharactersId : teamIdList){
//                if(myCharacter.getId().equals(myTeamCharactersId)){
//                    if(!myCharacter.getCodeHerostable().equals("hero")) {
//                        characterId.add(myCharacter.getId());
//                        previousFatigability.add(myCharacter.getFatigability());
//                        myCharacter.SpendFatigability(10);
//                        presentFatigability.add(myCharacter.getFatigability());
//                    }
//                    //     myTeamCharactersList.add(myCharacter);
//                }
//            }
//        }
//        if(!characterId.isEmpty()){
//            CharacterFatigabilityLogDto characterFatigabilityLogDto = new CharacterFatigabilityLogDto();
//            characterFatigabilityLogDto.setCharacterFatigabilityLogDto("용의 시련 "+floor+"층 진행", characterId, previousFatigability, presentFatigability);
//            String fatigabilityLog = JsonStringHerlper.WriteValueAsStringFromData(characterFatigabilityLogDto);
//            loggingService.setLogging(userId, 5, fatigabilityLog);
//        }
//        List<MyCharactersBaseDto> myCharactersBaseDtoList = new ArrayList<>();
//        for(MyCharacters temp : myCharactersList){
//            MyCharactersBaseDto myCharactersBaseDto = new MyCharactersBaseDto();
//            myCharactersBaseDto.InitFromDbData(temp);
//            myCharactersBaseDtoList.add(myCharactersBaseDto);
//        }
//        map.put("myCharactersList",myCharactersBaseDtoList);
        return map;
    }
}
