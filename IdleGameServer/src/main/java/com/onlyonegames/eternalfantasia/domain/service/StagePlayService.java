package com.onlyonegames.eternalfantasia.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;
import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ChapterSaveDto.ChapterSaveData;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.EventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Event.AchieveEventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.GiftItemDto.GiftItemDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.HeroEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.*;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.MyCharactersDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MyMissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyCharactersBaseDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.StagePlayDataDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.*;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyGiftInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyAncientDragonExpandSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyHeroTowerExpandSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyOrdealDungeonExpandSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Event.MyAchieveEventMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.ItemType;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.*;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyGiftInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyAncientDragonExpandSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyHeroTowerExpandSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyOrdealDungeonExpandSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Event.MyAchieveEventMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.BelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.HeroEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.ItemTypeRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
import com.onlyonegames.eternalfantasia.etc.EquipmentCalculate;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.util.StringMaker;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springfox.documentation.spring.web.json.Json;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class StagePlayService {
    private final UserRepository userRepository;
    private final MyCharactersRepository myCharactersRepository;
    private final MyGiftInventoryRepository myGiftInventoryRepository;
    private final MyChapterSaveDataRepository myChapterSaveDataRepository;
    private final MyTeamInfoRepository myTeamInfoRepository;
    private final StagePlayDataRepository stagePlayDataRepository;
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final HeroEquipmentInventoryRepository heroEquipmentInventoryRepository;
    private final ItemTypeRepository itemTypeRepository;
    private final MyHeroTowerExpandSaveDataRepository myHeroTowerExpandSaveDataRepository;
    private final MyOrdealDungeonExpandSaveDataRepository myOrdealDungeonExpandSaveDataRepository;
    private final MyAncientDragonExpandSaveDataRepository myAncientDragonExpandSaveDataRepository;
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;
    private final MyAchieveEventMissionsDataRepository myAchieveEventMissionsDataRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;

    public Map<String, Object> stageStartPlay(Long userId, int chapterNo, int stageNo, int aliveCharacterCount, Map<String, Object> map) {

        StagePlayData stagePlayData = stagePlayDataRepository.findByUseridUser(userId);
        if(stagePlayData == null)
        {
            StagePlayDataDto newStagePlayDataDto = new StagePlayDataDto();
            newStagePlayDataDto.setUseridUser(userId);
            stagePlayData = stagePlayDataRepository.save(newStagePlayDataDto.ToEntity());
        }
        stagePlayData.StartStagePlay();
        return map;
    }

    public Map<String, Object> stageClear(Long userId, int chapterNo, int stageNo, int aliveCharacterCount, Map<String, Object> map){
        MyChapterSaveData myChapterSaveData = myChapterSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myChapterSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyChapterSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyChapterSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String saveDataString = myChapterSaveData.getSaveDataValue();
        ChapterSaveData chapterSaveData = JsonStringHerlper.ReadValueFromJson(saveDataString, ChapterSaveData.class);
        ChapterSaveData.ChapterPlayInfo chapterPlayInfo = chapterSaveData.chapterData.chapterPlayInfosList.stream()
                .filter(a -> a.chapterNo == chapterNo && a.isOpend)
                .findAny()
                .orElse(null);
        if(chapterPlayInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.INVAILD_CHAPTER_SAVEDATA.getIntegerValue(), "Fail -> Cause: Invaild chapterNo Or not yet chapter open", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Invaild chapterNo Or not yet chapter open", ResponseErrorCode.INVAILD_CHAPTER_SAVEDATA);
        }
        ChapterSaveData.StagePlayInfo stagePlayInfo = chapterPlayInfo.stagePlayInfosList.stream()
                .filter(a -> a.stageNo == stageNo && a.isOpend)
                .findAny()
                .orElse(null);
        if(stagePlayInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.INVAILD_CHAPTER_SAVEDATA.getIntegerValue(), "Fail -> Cause: Invaild stageNo Or not yet stage open", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Invaild stageNo Or not yet stage open", ResponseErrorCode.INVAILD_CHAPTER_SAVEDATA);
        }
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }

        /*스테이지 클리어하면 무조건 */
        boolean alreadyCleared = stagePlayInfo.isCleared;
        stagePlayInfo.isCleared = true;
        if(chapterNo != 0) {
            /*다음 챕터 오픈 조건 체크*/
            if(chapterNo < 14 && stageNo == 20) {
                int nextChapterNo = chapterNo + 1;
                List<ChapterOpenConditionTable> chapterOpenConditionTableList = gameDataTableService.ChapterOpenConditionTableList();
                ChapterOpenConditionTable chapterOpenConditionTable = chapterOpenConditionTableList.stream()
                        .filter(a -> a.getChapterNo() == nextChapterNo)
                        .findAny()
                        .orElse(null);
                if(chapterOpenConditionTable == null) {
                    /*이벤트 조건이 없으면 마지막 스테이지를 깼을때 다음 챕터를 오픈해준다.*/
                    ChapterSaveData.ChapterPlayInfo nextChapterPlayInfo = chapterSaveData.chapterData.chapterPlayInfosList.stream()
                            .filter(a -> a.chapterNo == nextChapterNo)
                            .findAny()
                            .orElse(null);
                    if (chapterPlayInfo == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Invaild chapterNo Or not yet chapter open", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail -> Cause: Invaild chapterNo Or not yet chapter open", ResponseErrorCode.INVAILD_CHAPTER_SAVEDATA);
                    }

                    nextChapterPlayInfo.isOpend = true;
                    nextChapterPlayInfo.stagePlayInfosList.get(0).isOpend = true;
                    map.put("nextChapterPlayInfo", nextChapterPlayInfo);
                }
                else{
                    String conditionEventCode = chapterOpenConditionTable.getEventCode();

                    List<MainQuestInfoTable> mainQuestInfoTableListList = gameDataTableService.MainQuestInfoTableList();
                    MainQuestInfoTable mainQuestInfoTable = mainQuestInfoTableListList.stream()
                            .filter(a -> a.getCode().equals(conditionEventCode))
                            .findAny()
                            .orElse(null);
                    if (mainQuestInfoTable == null) {
                        ChapterSaveData.ChapterPlayInfo nextChapterPlayInfo = chapterSaveData.chapterData.chapterPlayInfosList.stream()
                                .filter(a -> a.chapterNo == nextChapterNo)
                                .findAny()
                                .orElse(null);
                        if (chapterPlayInfo == null) {
                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Invaild chapterNo Or not yet chapter open", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                            throw new MyCustomException("Fail -> Cause: Invaild chapterNo Or not yet chapter open", ResponseErrorCode.INVAILD_CHAPTER_SAVEDATA);
                        }

                        nextChapterPlayInfo.isOpend = true;
                        nextChapterPlayInfo.stagePlayInfosList.get(0).isOpend = true;
                        map.put("nextChapterPlayInfo", nextChapterPlayInfo);
                    }
                }
            }
            /*다음 스테이지 오픈*/
            else if(stageNo < 20) {
                int nextStageNo = stageNo + 1;
                ChapterSaveData.StagePlayInfo nextStagePlayInfo = chapterPlayInfo.stagePlayInfosList.stream()
                        .filter(a -> a.stageNo == nextStageNo)
                        .findAny()
                        .orElse(null);
                if(nextStagePlayInfo == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.INVAILD_CHAPTER_SAVEDATA.getIntegerValue(), "Fail -> Cause: Invaild stageNo Or not yet stage open", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail -> Cause: Invaild stageNo Or not yet stage open", ResponseErrorCode.INVAILD_CHAPTER_SAVEDATA);
                }

                StringMaker.Clear();
                StringMaker.stringBuilder.append(chapterNo);
                StringMaker.stringBuilder.append("-");
                StringMaker.stringBuilder.append(nextStageNo);
                String findStage = StringMaker.stringBuilder.toString();
                List<StageOpenCondtionTable> stageOpenCondtionTableList = gameDataTableService.StageOpenConditionTableList();
                StageOpenCondtionTable stageOpenCondtionTable = stageOpenCondtionTableList.stream()
                        .filter(a -> a.getStage().equals(findStage))
                        .findAny()
                        .orElse(null);
                if(stageOpenCondtionTable == null) {
                    nextStagePlayInfo.isOpend = true;
                    map.put("nextstagePlayInfo", nextStagePlayInfo);
                }
                else {
                    String conditionEventCode = stageOpenCondtionTable.getEventCode();
                    List<MainQuestInfoTable> mainQuestInfoTableListList = gameDataTableService.MainQuestInfoTableList();
                    MainQuestInfoTable mainQuestInfoTable = mainQuestInfoTableListList.stream()
                            .filter(a -> a.getCode().equals(conditionEventCode))
                            .findAny()
                            .orElse(null);
                    if (mainQuestInfoTable == null) {
                        nextStagePlayInfo.isOpend = true;
                        map.put("nextstagePlayInfo", nextStagePlayInfo);
                    }
                }
            }
        }
        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);

        /*패스 업적 : 체크 준비*/
        MyEternalPassMissionsData myEternalPassMissionsData = myEternalPassMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myEternalPassMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyEternalPassMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEternalPassMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyEternalPassMissionData = myEternalPassMissionsData.getJson_saveDataValue();
        EventMissionDataDto myEternalPassMissionDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyEternalPassMissionData, EventMissionDataDto.class);
        boolean changedEternalPassMissionsData = false;

        //팀동료 케릭터들 피로도 깍기
        MyTeamInfo myTeamInfo = myTeamInfoRepository.findByUseridUser(userId);
        String[] myTeams = myTeamInfo.getStageTeam().split(",");
        List<Long> teamIdList = new ArrayList<>();
        for(int i = 0; i < myTeams.length; i++) {
            Long characterId = Long.parseLong(myTeams[i]);
            if(!characterId.equals(0L)) {
                teamIdList.add(characterId);
            }
        }
       // List<MyCharacters> myTeamCharactersList = new ArrayList<>();
        //메인영웅(code:hero)은 제외
        int spendFatigability = 10;
        List<Long> characterId = new ArrayList<>();
        List<Integer> previousFatigability = new ArrayList<>();
        List<Integer> presentFatigability = new ArrayList<>();
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

        CharacterFatigabilityLogDto characterFatigabilityLogDto = new CharacterFatigabilityLogDto();
        characterFatigabilityLogDto.setCharacterFatigabilityLogDto(chapterNo+"-"+stageNo+"스테이지 도전", characterId, previousFatigability, presentFatigability);
        String fatigabilityLog = JsonStringHerlper.WriteValueAsStringFromData(characterFatigabilityLogDto);
        loggingService.setLogging(userId, 5, fatigabilityLog);

        List<StageTable> stageTableList = gameDataTableService.StageTableList();

        StringMaker.Clear();
        StringMaker.stringBuilder.append(chapterNo);
        StringMaker.stringBuilder.append("-");
        StringMaker.stringBuilder.append(stageNo);
        String stageCode = StringMaker.stringBuilder.toString();
        StageTable stageTable = stageTableList.stream()
                .filter(a -> a.getStage().equals(stageCode))
                .findAny()
                .orElse(null);
        if(stageTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Not found stageCode", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Not found stageCode", ResponseErrorCode.NOT_EXIST_CODE);
        }

        CurrencyLogDto diamondLog = new CurrencyLogDto();
        CurrencyLogDto goldLog = new CurrencyLogDto();
        CurrencyLogDto linkforceLog = new CurrencyLogDto();
        int previousDiamond = user.getDiamond();
        int previousGold = user.getGold();
        int previousLinkforce = user.getLinkforcePoint();
        int gettedDiamond = 0;
        int gettedGold = 0;
        int gettedLinkforce = 0;
        String[] rewardsArray = stageTable.getStarsReward().split(",");
        if(GetCondition(stageTable.getConditionStar1(), userId, aliveCharacterCount)) {
            if(false == stagePlayInfo.isGettedStar1) {
                //최초 달성시 보상 추가.
                String rewardInfo = rewardsArray[0];
                String[] rewardInfos = rewardInfo.split(":");
                String rewardCode = rewardInfos[0];
                int gettingCount = Integer.parseInt(rewardInfos[1]);
                if(rewardCode.contains("Diamond")) {
                    user.AddDiamond(gettingCount);
                    gettedDiamond += gettingCount;
                }
            }
            stagePlayInfo.isGettedStar1 = true;
        }
        if(GetCondition(stageTable.getConditionStar2(), userId, aliveCharacterCount)) {
            if(false == stagePlayInfo.isGettedStar2) {
                //최초 달성시 보상 추가.
                String rewardInfo = rewardsArray[1];
                String[] rewardInfos = rewardInfo.split(":");
                String rewardCode = rewardInfos[0];
                int gettingCount = Integer.parseInt(rewardInfos[1]);
                if(rewardCode.contains("Diamond")) {
                    user.AddDiamond(gettingCount);
                    gettedDiamond += gettingCount;
                }
            }
            stagePlayInfo.isGettedStar2 = true;
        }
        if(GetCondition(stageTable.getConditionStar3(), userId, aliveCharacterCount)) {
            if(false == stagePlayInfo.isGettedStar3) {
                //최초 달성시 보상 추가.
                String rewardInfo = rewardsArray[2];
                String[] rewardInfos = rewardInfo.split(":");
                String rewardCode = rewardInfos[0];
                int gettingCount = Integer.parseInt(rewardInfos[1]);
                if(rewardCode.contains("Diamond")) {
                    user.AddDiamond(gettingCount);
                    gettedDiamond += gettingCount;
                }
            }
            stagePlayInfo.isGettedStar3 = true;
        }
        myChapterSaveData.ResetSaveDataValue(JsonStringHerlper.WriteValueAsStringFromData(chapterSaveData));
        map.put("stagePlayInfo", stagePlayInfo);

        if(alreadyCleared) {
            rewardsArray = stageTable.getRepeatReward().split(",");
            map.put("isFirstClear", false);
        }
        else {
            rewardsArray = stageTable.getFirstReward().split(",");
            map.put("isFirstClear", true);
        }

        List<ItemType> itemTypeList = itemTypeRepository.findAll();

        ItemType materialItemType = itemTypeList.stream()
                .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Material)
                .findAny()
                .orElse(null);
        if(materialItemType == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ItemType Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: ItemType Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        ItemType spendableItemType = itemTypeList.stream()
                .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                .findAny()
                .orElse(null);
        if(spendableItemType == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ItemType Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: ItemType Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
        List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList = gameDataTableService.EquipmentMaterialInfoTableList();
        List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
        MyGiftInventory myGiftInventory = myGiftInventoryRepository.findByUseridUser(userId)
                .orElse(null);
        if(myGiftInventory == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyGiftInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyGiftInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<GiftTable> giftTableList = gameDataTableService.GiftsTableList();
        String inventoryInfosString = myGiftInventory.getInventoryInfos();
        GiftItemDtosList giftItemInventorys = JsonStringHerlper.ReadValueFromJson(inventoryInfosString, GiftItemDtosList.class);


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

        /*누적 업적 : 체크 준비*/
        MyAchieveEventMissionsData myAchieveEventMissionsData = myAchieveEventMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myAchieveEventMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyAchieveEventMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyAchieveEventMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyAchieveEventMissionData = myAchieveEventMissionsData.getJson_saveDataValue();
        AchieveEventMissionDataDto myAchieveEventMissionDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyAchieveEventMissionData, AchieveEventMissionDataDto.class);
        boolean changedAchieveEventMissionsData = false;

        for(int i = 0; i < rewardsArray.length; i++)
        {
            String rewardInfo = rewardsArray[i];
            String[] rewardInfos = rewardInfo.split(":");
            String rewardCode = rewardInfos[0];
            int gettingCount = Integer.parseInt(rewardInfos[1]);
            //
            if(rewardCode.contains("Gold")) {
                user.AddGold(gettingCount);

                gettedGold += gettingCount;
            }
            else if(rewardCode.contains("Diamond")) {
                user.AddDiamond(gettingCount);

                gettedDiamond += gettingCount;
            }
            else if(rewardCode.contains("Linkpoint")) {
                user.AddLinkforcePoint(gettingCount);

                gettedLinkforce += gettingCount;
            }
            else if(rewardCode.contains("Exp")) {

                MyCharacters myMainHero = myCharactersList.stream()
                        .filter(a -> a.getCodeHerostable().equals("hero"))
                        .findAny()
                        .orElseThrow(null);
                if(myMainHero == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find Character", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail -> Cause: Can't Find Character", ResponseErrorCode.NOT_FIND_DATA);
                }
                int previousLevel = myMainHero.getLevel();
               //경험치 적용
                myMainHero.AddExp(gettingCount,myMainHero.getMaxLevel());

                int maxLevel = myMainHero.getLevel();
                for(MyCharacters myCharacter : myCharactersList) {
                    if(!myCharacter.getCodeHerostable().equals("hero"))
                        myCharacter.SetMaxLevel(maxLevel);
                }

                int afterLevel = myMainHero.getLevel();
                //user.SetUserLevel(afterLevel);
                if(previousLevel < afterLevel) {

                    //모든 동료 피로도 회복
                    for(MyCharacters myCharacter : myCharactersList){
                        if(!myCharacter.getCodeHerostable().equals("hero"))
                            myCharacter.SetFatigability(100);
                        //     myTeamCharactersList.add(myCharacter);
                    }


                    for(int j = previousLevel+1; j <= afterLevel; j++) {


                        /* 업적 : 영웅 레벨 달성 미션 체크*/
                        StringMaker.Clear();
                        StringMaker.stringBuilder.append(j);
                        StringMaker.stringBuilder.append(" 달성");
                        String levelParameter = StringMaker.stringBuilder.toString();

                        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.LEVEL_UP_MAIN_HERO.name(), levelParameter, gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                        changedAchieveEventMissionsData = myAchieveEventMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.LEVEL_UP_MAIN_HERO.name(), levelParameter, gameDataTableService.AchieveEventTableList()) || changedAchieveEventMissionsData;
                    }

                    //SP 추가
                    int addSkillPoint = afterLevel - previousLevel;
                    user.AddSkillPoint(addSkillPoint);
                    map.put("skill_point", addSkillPoint);


                    MyHeroTowerExpandSaveData myHeroTowerExpandSaveData = myHeroTowerExpandSaveDataRepository.findByUseridUser(userId)
                            .orElse(null);
                    if(myHeroTowerExpandSaveData == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyHeroTowerExpandSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: MyHeroTowerExpandSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }

                    MyOrdealDungeonExpandSaveData myOrdealDungeonExpandSaveData = myOrdealDungeonExpandSaveDataRepository.findByUseridUser(userId)
                            .orElse(null);
                    if(myOrdealDungeonExpandSaveData == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyOrdealDungeonExpandSaveDataRepository not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: MyOrdealDungeonExpandSaveDataRepository not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }

                    MyAncientDragonExpandSaveData myAncientDragonExpandSaveData = myAncientDragonExpandSaveDataRepository.findByUseridUser(userId)
                            .orElse(null);
                    if(myAncientDragonExpandSaveData == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyOrdealDungeonExpandSaveDataRepository not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: MyOrdealDungeonExpandSaveDataRepository not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }

                    //영웅의 탑 각 스테이지 오픈 조건 레벨 체크
                    int checkOpenLevel = 1;
                    while(true) {
                        int finalCheckOpenLevel = checkOpenLevel;
                        int previousCheckOpenLevel = finalCheckOpenLevel - 1;
                        HeroTowerStageTable openableHeroTowerStageInfo = gameDataTableService.HeroTowerStageTableList().stream()
                                .filter(a -> a.getOpenLevel() <= finalCheckOpenLevel && a.getOpenLevel() > previousCheckOpenLevel)
                                .findAny()
                                .orElse(null);
                        if(openableHeroTowerStageInfo != null) {
                            String heroTowerChapterAndStageNoString = openableHeroTowerStageInfo.getStage();
                            String[] heroTowerChapterAndStageNoStringArray =  heroTowerChapterAndStageNoString.split("-");
                            int heroTowerStageNo = Integer.parseInt(heroTowerChapterAndStageNoStringArray[1]);


                            String json_heroTowerSaveData = myHeroTowerExpandSaveData.getJson_saveDataValue();
                            ChapterSaveData heroTowerChapterSaveData = JsonStringHerlper.ReadValueFromJson(json_heroTowerSaveData, ChapterSaveData.class);
                            ChapterSaveData.ChapterPlayInfo heroTowerExpandChapterPlayInfo = heroTowerChapterSaveData.chapterData.chapterPlayInfosList.get(0);

                            for(ChapterSaveData.StagePlayInfo heroTowerExpandStagePlayInfo : heroTowerExpandChapterPlayInfo.stagePlayInfosList) {
                                if(heroTowerExpandStagePlayInfo.stageNo <= heroTowerStageNo) {
                                    heroTowerExpandStagePlayInfo.isOpend = true;
                                }
                            }
                            json_heroTowerSaveData = JsonStringHerlper.WriteValueAsStringFromData(heroTowerChapterSaveData);
                            myHeroTowerExpandSaveData.ResetSaveDataValue(json_heroTowerSaveData);
                        }
                        //시련의 탑 각 스테이지 오픈 조건 레벨 체크
                        OrdealStageTable openableOrdealStageInfo = gameDataTableService.OrdealStageTableList().stream()
                                .filter(a -> a.getOpenLevel() <= finalCheckOpenLevel && a.getOpenLevel() > previousCheckOpenLevel)
                                .findAny()
                                .orElse(null);
                        if(openableOrdealStageInfo != null) {
                            String ordealChapterAndStageNoString = openableOrdealStageInfo.getStage();
                            String[] ordealChapterAndStageNoStringArray =  ordealChapterAndStageNoString.split("-");
                            int ordealStageNo = Integer.parseInt(ordealChapterAndStageNoStringArray[1]);


                            String json_ordealSaveData = myOrdealDungeonExpandSaveData.getJson_saveDataValue();
                            ChapterSaveData ordealChapterSaveData = JsonStringHerlper.ReadValueFromJson(json_ordealSaveData, ChapterSaveData.class);
                            ChapterSaveData.ChapterPlayInfo ordealExpandChapterPlayInfo = ordealChapterSaveData.chapterData.chapterPlayInfosList.get(0);

                            for(ChapterSaveData.StagePlayInfo ordealExpandStagePlayInfo : ordealExpandChapterPlayInfo.stagePlayInfosList) {
                                if(ordealExpandStagePlayInfo.stageNo <= ordealStageNo) {
                                    ordealExpandStagePlayInfo.isOpend = true;
                                }
                            }
                            json_ordealSaveData = JsonStringHerlper.WriteValueAsStringFromData(ordealChapterSaveData);
                            myOrdealDungeonExpandSaveData.ResetSaveDataValue(json_ordealSaveData);
                        }
                        //고대 던전 각 스테이지 오픈 조건 레벨 체크
                        AncientDragonStageTable openableAncientStageInfo = gameDataTableService.AncientDragonStageTableList().stream()
                                .filter(a -> a.getOpenLevel() <= finalCheckOpenLevel && a.getOpenLevel() > previousCheckOpenLevel)
                                .findAny()
                                .orElse(null);
                        if(openableAncientStageInfo != null) {
                            String ancientChapterAndStageNoString = openableAncientStageInfo.getStage();
                            String[] ancientChapterAndStageNoStringArray =  ancientChapterAndStageNoString.split("-");
                            int ancientStageNo = Integer.parseInt(ancientChapterAndStageNoStringArray[1]);


                            String json_ancientSaveData = myAncientDragonExpandSaveData.getJson_saveDataValue();
                            ChapterSaveData ancientChapterSaveData = JsonStringHerlper.ReadValueFromJson(json_ancientSaveData, ChapterSaveData.class);
                            ChapterSaveData.ChapterPlayInfo ancientExpandChapterPlayInfo = ancientChapterSaveData.chapterData.chapterPlayInfosList.get(0);

                            for(ChapterSaveData.StagePlayInfo ancientExpandStagePlayInfo : ancientExpandChapterPlayInfo.stagePlayInfosList) {
                                if(ancientExpandStagePlayInfo.stageNo <= ancientStageNo) {
                                    ancientExpandStagePlayInfo.isOpend = true;
                                }
                            }
                            json_ancientSaveData = JsonStringHerlper.WriteValueAsStringFromData(ancientChapterSaveData);
                            myAncientDragonExpandSaveData.ResetSaveDataValue(json_ancientSaveData);
                        }
                        checkOpenLevel++;
                        if(checkOpenLevel > afterLevel)
                            break;
                    }
                 }

                map.put("reward_exp", gettingCount);
            }
            else if(rewardCode.contains("gift")) {
                GiftLogDto giftLogDto = new GiftLogDto();
                GiftItemDtosList.GiftItemDto giftItemDto = giftItemInventorys.giftItemDtoList.stream()
                        .filter(a -> a.code.equals(rewardCode))
                        .findAny()
                        .orElse(null);
                if(giftItemDto == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: GiftTableItem not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: GiftTableItem not find.", ResponseErrorCode.NOT_FIND_DATA);
                }

                GiftTable giftTableItem = giftTableList.stream()
                        .filter(a -> a.getCode().equals(rewardCode))
                        .findAny()
                        .orElse(null);
                if(giftTableItem == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: GiftTableItem not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: GiftTableItem not find.", ResponseErrorCode.NOT_FIND_DATA);
                }
                giftLogDto.setPreviousValue(giftItemDto.count);
                giftItemDto.AddItem(gettingCount, giftTableItem.getStackLimit());
                inventoryInfosString = JsonStringHerlper.WriteValueAsStringFromData(giftItemInventorys);
                /*정보 업데이트*/
                myGiftInventory.ResetInventoryInfos(inventoryInfosString);
                giftLogDto.setGiftLogDto(chapterNo+"-"+stageNo+"스테이지 클리어 보상획득", giftItemDto.code, gettingCount, giftItemDto.count);
                String giftLog = JsonStringHerlper.WriteValueAsStringFromData(giftLogDto);
                loggingService.setLogging(userId, 4, giftLog);
                map.put("giftItemDto", giftItemDto);
            }
            else if(rewardCode.contains("material")) {
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                EquipmentMaterialInfoTable equipmentMaterial = equipmentMaterialInfoTableList.stream()
                        .filter(a -> a.getCode().equals(rewardCode))
                        .findAny()
                        .orElse(null);
                if(equipmentMaterial == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: EquipmentMaterialInfoTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: EquipmentMaterialInfoTable not find.", ResponseErrorCode.NOT_FIND_DATA);
                }
                BelongingInventory inventoryItem = belongingInventoryList.stream()
                        .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Material && a.getItemId() == equipmentMaterial.getId())
                        .findAny()
                        .orElse(null);
                if(inventoryItem != null) {
                    belongingInventoryLogDto.setPreviousValue(inventoryItem.getCount());
                    inventoryItem.AddItem(gettingCount, equipmentMaterial.getStackLimit());
                    belongingInventoryLogDto.setBelongingInventoryLogDto(chapterNo+"-"+stageNo+"스테이지 클리어 보상획득", inventoryItem.getId(),
                            inventoryItem.getItemId(), inventoryItem.getItemType(), gettingCount, inventoryItem.getCount());
                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.InitFromDbData(inventoryItem);
                    belongingInventoryDto.setCount(gettingCount);
                    map.put("reward_material", belongingInventoryDto);
                }
                else {
                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.setUseridUser(userId);
                    belongingInventoryDto.setItemId(equipmentMaterial.getId());
                    belongingInventoryDto.setCount(gettingCount);
                    belongingInventoryDto.setItemType(materialItemType);
                    BelongingInventory willAddBelongingInventoryItem = belongingInventoryDto.ToEntity();
                    willAddBelongingInventoryItem = belongingInventoryRepository.save(willAddBelongingInventoryItem);
                    belongingInventoryList.add(willAddBelongingInventoryItem);
                    belongingInventoryLogDto.setPreviousValue(0);
                    belongingInventoryLogDto.setBelongingInventoryLogDto(chapterNo+"-"+stageNo+"스테이지 클리어 보상획득", inventoryItem.getId(),
                            inventoryItem.getItemId(), inventoryItem.getItemType(), gettingCount, inventoryItem.getCount());
                    map.put("reward_material", willAddBelongingInventoryItem);
                }
                String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                loggingService.setLogging(userId, 3, belongingLog);
            }
            else if(rewardCode.contains("enchant") || rewardCode.contains("resmelt")) {
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                SpendableItemInfoTable spendAbleItem = spendableItemInfoTableList.stream()
                        .filter(a -> a.getCode().equals(rewardCode))
                        .findAny()
                        .orElse(null);
                if(spendAbleItem == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: SpendableItemInfoTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: SpendableItemInfoTable not find.", ResponseErrorCode.NOT_FIND_DATA);
                }
                BelongingInventory inventoryItem = belongingInventoryList.stream()
                        .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == spendAbleItem.getId())
                        .findAny()
                        .orElse(null);
                if(inventoryItem != null) {
                    belongingInventoryLogDto.setPreviousValue(inventoryItem.getCount());
                    inventoryItem.AddItem(gettingCount, spendAbleItem.getStackLimit());
                    belongingInventoryLogDto.setBelongingInventoryLogDto(chapterNo+"-"+stageNo+"스테이지 클리어 보상획득", inventoryItem.getId(),
                            inventoryItem.getItemId(), inventoryItem.getItemType(), gettingCount, inventoryItem.getCount());
                    BelongingInventoryDto inventoryItemDto = new BelongingInventoryDto();
                    inventoryItemDto.InitFromDbData(inventoryItem);
                    inventoryItemDto.setCount(gettingCount);
                    map.put("reward_spendable", inventoryItemDto);
                }
                else {
                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.setUseridUser(userId);
                    belongingInventoryDto.setItemId(spendAbleItem.getId());
                    belongingInventoryDto.setCount(gettingCount);
                    belongingInventoryDto.setItemType(spendableItemType);
                    BelongingInventory willAddBelongingInventoryItem = belongingInventoryDto.ToEntity();
                    willAddBelongingInventoryItem = belongingInventoryRepository.save(willAddBelongingInventoryItem);
                    belongingInventoryList.add(willAddBelongingInventoryItem);
                    BelongingInventoryDto willAddBelongingInventoryItemDto = new BelongingInventoryDto();
                    willAddBelongingInventoryItemDto.InitFromDbData(willAddBelongingInventoryItem);
                    belongingInventoryLogDto.setPreviousValue(0);
                    belongingInventoryLogDto.setBelongingInventoryLogDto(chapterNo+"-"+stageNo+"스테이지 클리어 보상획득", willAddBelongingInventoryItem.getId(),
                            willAddBelongingInventoryItem.getItemId(), willAddBelongingInventoryItem.getItemType(), gettingCount, willAddBelongingInventoryItem.getCount());
                    map.put("reward_spendable", willAddBelongingInventoryItemDto);
                }
                String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                loggingService.setLogging(userId, 3, belongingLog);
            }
            else {
                //장비
                EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                HeroEquipmentsTable equipment = heroEquipmentsTableList.stream()
                        .filter(a -> a.getCode().equals(rewardCode))
                        .findAny()
                        .orElse(null);
                if(equipment != null) {
                    String itemClass = null;
                    //2019.11.04 플레이에서 획득하는 장비의 퀄리티 등급은 C, 추후 [아이템코드:갯수:퀄리티등급] 형태로 엑셀에 작업하여 원하는 등급을 뽑을수 있도록 정해야함.
                    if(rewardInfos.length > 2)
                        itemClass = rewardInfos[2];
                    else
                        itemClass = "C";
                    HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
                    int classValue = (int)EquipmentCalculate.GetClassValueFromClassCategory(itemClass, classValues);

                    HeroEquipmentInventory generatedItem = EquipmentCalculate.CreateEquipment(userId, equipment, itemClass, classValue, gameDataTableService.OptionsInfoTableList());
                    generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
                    HeroEquipmentInventoryDto generatedItemDto = new HeroEquipmentInventoryDto();
                    generatedItemDto.InitFromDbData(generatedItem);
                    equipmentLogDto.setEquipmentLogDto(chapterNo+"-"+stageNo+"스테이지 클리어 보상획득 - ["+ generatedItem.getId()+"]", generatedItem.getId(), "추가", generatedItemDto);
                    String equipmentLog = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                    loggingService.setLogging(userId, 2, equipmentLog);
                    map.put("reward_equipment", generatedItemDto);

                    /* 업적 : 장비 획득 (특정 품질) 미션 체크*/
                    changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(), itemClass, gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                    /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
                    changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), equipment.getGrade(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                }
            }
        }

        if(stageCode.equals("2-5")&&!alreadyCleared){
            MyCharacters myCharacters = myCharactersList.stream().filter(i -> i.getCodeHerostable().equals("cr_003")).findAny().orElse(null);
            if(myCharacters == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyCharacters not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: MyCharacters not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            if(!myCharacters.isGotcha())
                myCharacters.Gotcha();
            else{
                ItemType characterPieceItemType = itemTypeList.stream()
                        .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece)
                        .findAny()
                        .orElse(null);
                if(characterPieceItemType == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ItemType Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: ItemType Can't find.", ResponseErrorCode.NOT_FIND_DATA);
                }
                List<herostable> herostableList = gameDataTableService.HerosTableList();
                herostable herostable = herostableList.stream().filter(i -> i.getCode().equals("cr_003")).findAny().orElse(null);
                if(herostable == null){
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: herostable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: herostable not find.", ResponseErrorCode.NOT_FIND_DATA);
                }
                List<CharacterToPieceTable> characterToPieceTableList = gameDataTableService.CharacterToPieceTableList();
                CharacterToPieceTable characterToPieceTable = characterToPieceTableList.stream().filter(i -> i.getId()==herostable.getTier()).findAny().orElse(null);
                if(characterToPieceTable == null){
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: CharacterToPieceTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: CharacterToPieceTable not find.", ResponseErrorCode.NOT_FIND_DATA);
                }
                List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
                int gettingCount = characterToPieceTable.getPieceCount();
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                BelongingCharacterPieceTable characterPieceItem = belongingCharacterPieceTableList.stream()
                        .filter(a -> a.getCode().equals("characterPiece_cr_003"))
                        .findAny()
                        .orElse(null);
                if(characterPieceItem == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: SpendableItemInfoTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: SpendableItemInfoTable not find.", ResponseErrorCode.NOT_FIND_DATA);
                }
                BelongingInventory inventoryItem = belongingInventoryList.stream()
                        .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece && a.getItemId() == characterPieceItem.getId())
                        .findAny()
                        .orElse(null);
                if(inventoryItem != null) {
                    belongingInventoryLogDto.setPreviousValue(inventoryItem.getCount());
                    inventoryItem.AddItem(gettingCount, characterPieceItem.getStackLimit());
                    belongingInventoryLogDto.setBelongingInventoryLogDto(chapterNo+"-"+stageNo+"스테이지 클리어 보상획득", inventoryItem.getId(),
                            inventoryItem.getItemId(), inventoryItem.getItemType(), gettingCount, inventoryItem.getCount());
                    BelongingInventoryDto inventoryItemDto = new BelongingInventoryDto();
                    inventoryItemDto.InitFromDbData(inventoryItem);
                    inventoryItemDto.setCount(gettingCount);
                    map.put("reward_CharacterPiece", inventoryItemDto);
                }
                else {
                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.setUseridUser(userId);
                    belongingInventoryDto.setItemId(characterPieceItem.getId());
                    belongingInventoryDto.setCount(gettingCount);
                    belongingInventoryDto.setItemType(characterPieceItemType);
                    BelongingInventory willAddBelongingInventoryItem = belongingInventoryDto.ToEntity();
                    willAddBelongingInventoryItem = belongingInventoryRepository.save(willAddBelongingInventoryItem);
                    belongingInventoryList.add(willAddBelongingInventoryItem);
                    BelongingInventoryDto willAddBelongingInventoryItemDto = new BelongingInventoryDto();
                    willAddBelongingInventoryItemDto.InitFromDbData(willAddBelongingInventoryItem);
                    belongingInventoryLogDto.setPreviousValue(0);
                    belongingInventoryLogDto.setBelongingInventoryLogDto(chapterNo+"-"+stageNo+"스테이지 클리어 보상획득", willAddBelongingInventoryItem.getId(),
                            willAddBelongingInventoryItem.getItemId(), willAddBelongingInventoryItem.getItemType(), gettingCount, willAddBelongingInventoryItem.getCount());
                    map.put("reward_CharacterPiece", willAddBelongingInventoryItemDto);
                }
                String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                loggingService.setLogging(userId, 3, belongingLog);
            }
        }

        /* 업적 : 스테이지 1회 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.STAGE_CLEAR.name(), "empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
        /* 업적 : 해당 챕터 모두 클리어 체크*/
        if(stageNo == 20) {
            StringMaker.Clear();
            StringMaker.stringBuilder.append("Chapter");
            StringMaker.stringBuilder.append(chapterNo);
            String chapterParam = StringMaker.stringBuilder.toString();
            changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.CHAPTER_CLEAR.name(),chapterParam, gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
        }
        /* 업적 : 특정 스테이지 클리어 체크*/
        String stageClearParam = stageCode;
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.STAGE_CLEAR.name(), stageClearParam, gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

        /*업적 : 미션 데이터 변경점 적용*/
        if(changedMissionsData) {
            jsonMyMissionData = JsonStringHerlper.WriteValueAsStringFromData(myMissionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMyMissionData);
            myMissionsDataDto.questMissionsData = myMissionsDataDto.ImportQuestMissionSendToClient(gameDataTableService.QuestMissionTableList());
            map.put("exchange_myMissionsData", true);
            map.put("myMissionsDataDto", myMissionsDataDto);
            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());
        }

        /* 패스 업적 : 스테이지 1회 미션 체크*/
        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.STAGE_CLEAR.name(),"empty", gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;

        if(gettedDiamond != 0){
            map.put("reward_diamond", gettedDiamond);
            diamondLog.setCurrencyLogDto(chapterNo+"-"+stageNo+"스테이지 클리어 보상획득","다이아", previousDiamond, gettedDiamond, user.getDiamond());
            String log = JsonStringHerlper.WriteValueAsStringFromData(diamondLog);
            loggingService.setLogging(userId, 1, log);
        }
        if(gettedGold != 0){
            map.put("reward_gold", gettedGold);

            /* 패스 업적 : 골드획득*/
            changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.ERNING_GOLD.name(),"empty", gettedGold, gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
            goldLog.setCurrencyLogDto(chapterNo+"-"+stageNo+"스테이지 클리어 보상획득","골드", previousGold, gettedGold, user.getGold());
            String log = JsonStringHerlper.WriteValueAsStringFromData(goldLog);
            loggingService.setLogging(userId, 1, log);
        }
        if(gettedLinkforce != 0){
            map.put("reward_Linkpoint", gettedLinkforce);
            linkforceLog.setCurrencyLogDto(chapterNo+"-"+stageNo+"스테이지 클리어 보상획득","링크포인트", previousLinkforce, gettedLinkforce, user.getLinkforcePoint());
            String log = JsonStringHerlper.WriteValueAsStringFromData(linkforceLog);
            loggingService.setLogging(userId, 1, log);
        }

        map.put("myCharactersList",myCharactersList);

        /* 패스 업적 : 미션 데이터 변경점 적용*/
        if(changedEternalPassMissionsData) {
            jsonMyEternalPassMissionData = JsonStringHerlper.WriteValueAsStringFromData(myEternalPassMissionDataDto);
            myEternalPassMissionsData.ResetSaveDataValue(jsonMyEternalPassMissionData);
            map.put("exchange_myEternalPassMissionsData", true);
            map.put("myEternalPassMissionsDataDto", myEternalPassMissionDataDto);
            map.put("myEternalPassLastDailyMissionClearTime", myEternalPassMissionsData.getLastDailyMissionClearTime());
            map.put("myEternalPassLastWeeklyMissionClearTime", myEternalPassMissionsData.getLastWeeklyMissionClearTime());
        }
        /* 누적 업적 : 미션 데이터 변경점 적용*/
        if(changedAchieveEventMissionsData) {
            jsonMyAchieveEventMissionData = JsonStringHerlper.WriteValueAsStringFromData(myAchieveEventMissionDataDto);
            myAchieveEventMissionsData.ResetSaveDataValue(jsonMyAchieveEventMissionData);
            map.put("exchange_myAchieveEventMisstionsData", true);
            map.put("myAchieveEventMissionsDataDto", myAchieveEventMissionDataDto);
        }
        return map;
    }

    public Map<String, Object> stageFail(Long userId, int chapterNo, int stageNo, int aliveCharacterCount, Map<String, Object> map){
        StagePlayData stagePlayData = stagePlayDataRepository.findByUseridUser(userId);
        stagePlayData.DefeatStagePlay();

//        //팀동료 케릭터들 피로도 깍기
//        MyTeamInfo myTeamInfo = myTeamInfoRepository.findByUseridUser(userId);
//        String[] myTeams = myTeamInfo.getStageTeam().split(",");
//        List<Long> teamIdList = new ArrayList<>();
//        for(int i = 0; i < myTeams.length; i++) {
//            Long characterId = Long.parseLong(myTeams[i]);
//            if(!characterId.equals(0L)) {
//                teamIdList.add(characterId);
//            }
//        }
//
//        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
//
//        //List<MyCharacters> myTeamCharactersList = new ArrayList<>();
//        List<MyCharactersBaseDto> myTeamCharactersDtoList = new ArrayList<>();
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
//                    //myTeamCharactersList.add(myCharacter);
//                    MyCharactersBaseDto myCharactersBaseDto = new MyCharactersBaseDto();
//                    myCharactersBaseDto.InitFromDbData(myCharacter);
//                    myTeamCharactersDtoList.add(myCharactersBaseDto);
//                }
//            }
//        }
//        CharacterFatigabilityLogDto characterFatigabilityLogDto = new CharacterFatigabilityLogDto();
//        characterFatigabilityLogDto.setCharacterFatigabilityLogDto(chapterNo+"-"+stageNo+"스테이지 도전", characterId, previousFatigability, presentFatigability);
//        String fatigabilityLog = JsonStringHerlper.WriteValueAsStringFromData(characterFatigabilityLogDto);
//        loggingService.setLogging(userId, 5, fatigabilityLog);
//        map.put("myTeamCharactersList",myTeamCharactersDtoList); /**/

        return map;
    }

    boolean GetCondition(String conditionString, Long userId, int aliveCharacterCount) {
        if (conditionString.equals("StageClear")) {
            return true;
        }
        if (conditionString.equals("TeamAllAlive")) {
            MyTeamInfo myTeamInfo = myTeamInfoRepository.findByUseridUser(userId);

            String[] myTeams = myTeamInfo.getStageTeam().split(",");

            int myTeamSettedCount = 0;
            for(int i = 0; i < myTeams.length; i++) {
                Long characterId = Long.parseLong(myTeams[i]);
                if(!characterId.equals(0L)){
                    myTeamSettedCount++;
                }
            }
            /*모든 케릭터 살아있다면 */
            if(myTeamSettedCount == aliveCharacterCount)
                return true;
            else
                return false;
        }
        if (conditionString.contains("Time")) {
            String[] arrayConditionString = conditionString.split(",");
            String[] arrayTimeInfoString = arrayConditionString[1].split(":");
            float minute = Float.parseFloat(arrayTimeInfoString[0]);
            float second = Float.parseFloat(arrayTimeInfoString[1]);
            second += minute * 60;

            /*플레이 시작 시간 - 끝난 시간이 일정 시간 이하*/
            StagePlayData stagePlayData = stagePlayDataRepository.findByUseridUser(userId);
            stagePlayData.ClearStagePlay();
            Duration duration = Duration.between(stagePlayData.getBattleStartTime(), stagePlayData.getBattleEndTime());
            if(duration.getSeconds() <= second) {
                return true;
            }
        }
        return false;
    }
}
