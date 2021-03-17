package com.onlyonegames.eternalfantasia.domain.service.Dungeon;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;
import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ChapterSaveDto.ChapterSaveData;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.HeroTowerStagePlayDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.MyOrdealDungeonStagePlayDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.EventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.BelongingInventoryLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CharacterFatigabilityLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyCharactersBaseDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyHeroTowerStagePlayData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyOrdealDungeonExpandSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyOrdealDungeonStagePlayData;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.ItemType;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyTeamInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyOrdealDungeonExpandSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyOrdealDungeonStagePlayDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.BelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.ItemTypeRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyTeamInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.util.MathHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyOrdealDungeonPlayService {
    private final MyCharactersRepository myCharactersRepository;
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final MyOrdealDungeonStagePlayDataRepository myOrdealDungeonStagePlayDataRepository;
    private final MyOrdealDungeonExpandSaveDataRepository myOrdealDungeonExpandSaveDataRepository;
    private final MyTeamInfoRepository myTeamInfoRepository;
    private final ItemTypeRepository itemTypeRepository;
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;

    public Map<String, Object> OrdealDungeonPlayTimeSet(Long userId, Map<String, Object> map) {
        MyOrdealDungeonStagePlayData stagePlayData = myOrdealDungeonStagePlayDataRepository.findByUseridUser(userId);
        if(stagePlayData == null)
        {
            MyOrdealDungeonStagePlayDataDto newStagePlayDataDto = new MyOrdealDungeonStagePlayDataDto();
            newStagePlayDataDto.setUseridUser(userId);
            stagePlayData = myOrdealDungeonStagePlayDataRepository.save(newStagePlayDataDto.ToEntity());
        }

        stagePlayData.StartStagePlay();
        return map;
    }

    public Map<String, Object> StartOrdealDungeonPlay(Long userId, int floorNo, Map<String, Object> map) {

        MyOrdealDungeonExpandSaveData myOrdealDungeonExpandSaveData = myOrdealDungeonExpandSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myOrdealDungeonExpandSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyOrdealDungeonExpandSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyOrdealDungeonExpandSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        //by rainful 2021.03-08 시련의 던전 무한에서 3회 제한으로 수정.
        if(myOrdealDungeonExpandSaveData.getBonusRemainCount() <= 0) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.DONT_PLAY_ANYMORE.getIntegerValue(), "Fail -> Cause: Don't play anymore today", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Don't play anymore today", ResponseErrorCode.DONT_PLAY_ANYMORE);
        }
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
        /* 업적 : 시련의 던전 도전 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.PLAY_ORDEAL_DUNGEON.name(), "empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

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
        /* 패스 업적 : 시련의 던전 도전 미션 체크*/
        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.PLAY_ORDEAL_DUNGEON.name(),"empty", gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
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
    /*층수에 따른 보상을 각 인벤토리에 저장 해주고 리턴, 매시즌 bonusRemainCount 만큼 기존 보상 * 2배*/
    public Map<String, Object> OrdealDungeonClear(Long userId, int floorNo, Map<String, Object> map) {
        MyOrdealDungeonExpandSaveData myOrdealDungeonExpandSaveData = myOrdealDungeonExpandSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myOrdealDungeonExpandSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyOrdealDungeonExpandSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyOrdealDungeonExpandSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String json_saveDataValue = myOrdealDungeonExpandSaveData.getJson_saveDataValue();
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

        //보너스 배율, 시즌당 bonus를 적용시킬수 있는 횟수는 3회이며 기본 보수에 2배를 받을수 있음.
        int bonusMagnification = 1;
        if(myOrdealDungeonExpandSaveData.getBonusRemainCount() > 3) {
            bonusMagnification = 2;
        }

        myOrdealDungeonExpandSaveData.Clear();
        stagePlayInfo.isCleared = true;
        /*플레이 데이터 클리어 셋팅*/
        MyOrdealDungeonStagePlayData myOrdealDungeonStagePlayData = myOrdealDungeonStagePlayDataRepository.findByUseridUser(userId);
        myOrdealDungeonStagePlayData.ClearStagePlay();

        int index = floorNo - 1;
        List<OrdealStageTable> stageTableList = gameDataTableService.OrdealStageTableList();
        OrdealStageTable ordealStageTable = stageTableList.get(index);
        List<ItemType> itemTypeList = itemTypeRepository.findAll();
        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
        List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
        String[] rewardsArray = ordealStageTable.getRepeatReward().split(",");

        List<BelongingInventoryDto> changedBelongingInventoryList = new ArrayList<>();
        //첫번째 보상
        String rewardInfo = rewardsArray[0];
        String[] rewardInfos = rewardInfo.split(":");
        String rewardCode = rewardInfos[0];
        int gettingCount = Integer.parseInt(rewardInfos[1]);
        gettingCount *= bonusMagnification;
        BelongingInventoryDto belongingInventoryDto = AddSpendAbleItem(userId, rewardCode, gettingCount, itemTypeList, belongingInventoryList, spendableItemInfoTableList, "던전보상 - 시련의 던전 "+floorNo+"층");
        changedBelongingInventoryList.add(belongingInventoryDto);

        //두번째 보상
        rewardInfo = rewardsArray[1];
        rewardInfos = rewardInfo.split(":");
        rewardCode = rewardInfos[0];
        gettingCount = Integer.parseInt(rewardInfos[1]);
        gettingCount *= bonusMagnification;
        belongingInventoryDto = AddSpendAbleItem(userId, rewardCode, gettingCount, itemTypeList, belongingInventoryList, spendableItemInfoTableList, "던전보상 - 시련의 던전 "+floorNo+"층");
        changedBelongingInventoryList.add(belongingInventoryDto);
        //세번째 보상
        rewardInfo = rewardsArray[2];
        rewardInfos = rewardInfo.split(":");
        rewardCode = rewardInfos[0];
        gettingCount = Integer.parseInt(rewardInfos[1]);
        gettingCount *= bonusMagnification;

        int kindCount = 0;
        if(rewardCode.equals("reward_material_low")) {
            //3종
            kindCount = 3;

        }
        else if(rewardCode.equals("reward_material_middle")) {
            //5종
            kindCount = 5;
        }
        else if(rewardCode.equals("reward_material_high")) {
            //8종
            kindCount = 8;
        }

        List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList = gameDataTableService.EquipmentMaterialInfoTableList();
        List<EquipmentMaterialInfoTable> copyEquipmentMaterialInfoTableList = new ArrayList<>();
        copyEquipmentMaterialInfoTableList.addAll(equipmentMaterialInfoTableList);
        for(int i = 0; i < kindCount; i++) {
            int selectedRandomIndex = (int) MathHelper.Range(0, copyEquipmentMaterialInfoTableList.size());
            EquipmentMaterialInfoTable equipmentMaterialInfoTable = copyEquipmentMaterialInfoTableList.get(selectedRandomIndex);
            belongingInventoryDto = AddEquipmentMaterialItem(userId, equipmentMaterialInfoTable.getCode(), gettingCount, itemTypeList, belongingInventoryList, equipmentMaterialInfoTableList, "던정보상 - 시련의 던전 "+floorNo+"층");
            changedBelongingInventoryList.add(belongingInventoryDto);
            copyEquipmentMaterialInfoTableList.remove(selectedRandomIndex);
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

        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);

        //팀동료 케릭터들 피로도 깍기
        MyTeamInfo myTeamInfo = myTeamInfoRepository.findByUseridUser(userId);
        String[] myTeams = myTeamInfo.getOrdealDungeonPlayTeam().split(",");
        List<Long> teamIdList = new ArrayList<>();
        for(int i = 0; i < myTeams.length; i++) {
            Long characterId = Long.parseLong(myTeams[i]);
            if(!characterId.equals(0L)) {
                teamIdList.add(characterId);
            }
        }
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
            characterFatigabilityLogDto.setCharacterFatigabilityLogDto("시련의 던전 " + floorNo + "층 진행", characterId, previousFatigability, presentFatigability);
            String fatigabilityLog = JsonStringHerlper.WriteValueAsStringFromData(characterFatigabilityLogDto);
            loggingService.setLogging(userId, 5, fatigabilityLog);
        }
        List<MyCharactersBaseDto> myCharactersBaseDtoList = new ArrayList<>();
        for(MyCharacters temp : myCharactersList){
            MyCharactersBaseDto myCharactersBaseDto = new MyCharactersBaseDto();
            myCharactersBaseDto.InitFromDbData(temp);
            myCharactersBaseDtoList.add(myCharactersBaseDto);
        }
        map.put("myCharactersList",myCharactersBaseDtoList);
        map.put("changedBelongingInventoryList", changedBelongingInventoryList);

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

    BelongingInventoryDto AddEquipmentMaterialItem(Long userId, String gettingItemCode, int gettingCount, List<ItemType> itemTypeList, List<BelongingInventory> belongingInventoryList, List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList, String workingPosition) {
        EquipmentMaterialInfoTable equipmentMaterial = equipmentMaterialInfoTableList.stream()
                .filter(a -> a.getCode().equals(gettingItemCode))
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
            BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
            belongingInventoryLogDto.setPreviousValue(inventoryItem.getCount());
            inventoryItem.AddItem(gettingCount, equipmentMaterial.getStackLimit());
            belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, inventoryItem.getId(), inventoryItem.getItemId(), inventoryItem.getItemType(), gettingCount, inventoryItem.getCount());
            String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, log);
            BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
            belongingInventoryDto.InitFromDbData(inventoryItem);
            belongingInventoryDto.setCount(gettingCount);
            return belongingInventoryDto;
        }
        else {
            ItemType materialItemType = itemTypeList.stream()
                    .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Material)
                    .findAny()
                    .orElse(null);
            if(materialItemType == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ItemType Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: ItemType Can't find.", ResponseErrorCode.NOT_FIND_DATA);
            }

            BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
            belongingInventoryDto.setUseridUser(userId);
            belongingInventoryDto.setItemId(equipmentMaterial.getId());
            belongingInventoryDto.setCount(gettingCount);
            belongingInventoryDto.setItemType(materialItemType);
            BelongingInventory willAddBelongingInventoryItem = belongingInventoryDto.ToEntity();
            willAddBelongingInventoryItem = belongingInventoryRepository.save(willAddBelongingInventoryItem);
            BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
            belongingInventoryLogDto.setPreviousValue(0);
            belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, willAddBelongingInventoryItem.getId(), willAddBelongingInventoryItem.getItemId(), willAddBelongingInventoryItem.getItemType(), gettingCount, willAddBelongingInventoryItem.getCount());
            String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, log);
            belongingInventoryList.add(willAddBelongingInventoryItem);
            belongingInventoryDto.setId(willAddBelongingInventoryItem.getId());
            return belongingInventoryDto;
        }
    }

    BelongingInventoryDto AddSpendAbleItem(Long userId, String gettingItemCode, int gettingCount, List<ItemType> itemTypeList, List<BelongingInventory> belongingInventoryList, List<SpendableItemInfoTable> spendableItemInfoTableList, String workingPosition) {
        SpendableItemInfoTable spendAbleItem = spendableItemInfoTableList.stream()
                .filter(a -> a.getCode().equals(gettingItemCode))
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
            BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
            belongingInventoryLogDto.setPreviousValue(inventoryItem.getCount());
            inventoryItem.AddItem(gettingCount, spendAbleItem.getStackLimit());
            belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, inventoryItem.getId(), inventoryItem.getItemId(), inventoryItem.getItemType(), gettingCount, inventoryItem.getCount());
            String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, log);
            BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
            belongingInventoryDto.InitFromDbData(inventoryItem);
            belongingInventoryDto.setCount(gettingCount);
            return belongingInventoryDto;
        }
        else {
            ItemType spendableItemType = itemTypeList.stream()
                    .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                    .findAny()
                    .orElse(null);
            if(spendableItemType == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ItemType Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: ItemType Can't find.", ResponseErrorCode.NOT_FIND_DATA);
            }

            BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
            belongingInventoryDto.setUseridUser(userId);
            belongingInventoryDto.setItemId(spendAbleItem.getId());
            belongingInventoryDto.setCount(gettingCount);
            belongingInventoryDto.setItemType(spendableItemType);
            BelongingInventory willAddBelongingInventoryItem = belongingInventoryDto.ToEntity();
            willAddBelongingInventoryItem = belongingInventoryRepository.save(willAddBelongingInventoryItem);
            BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
            belongingInventoryLogDto.setPreviousValue(0);
            belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, willAddBelongingInventoryItem.getId(), willAddBelongingInventoryItem.getItemId(), willAddBelongingInventoryItem.getItemType(), gettingCount, willAddBelongingInventoryItem.getCount());
            String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, log);
            belongingInventoryList.add(willAddBelongingInventoryItem);
            belongingInventoryDto.setId(willAddBelongingInventoryItem.getId());
            return belongingInventoryDto;
        }
    }

    /*케릭터가 죽으면 던전 클리어 실패*/
    public Map<String, Object> OrdealDungeonFail(Long userId, int floor, Map<String, Object> map) {
        /*플레이 데이터 실패 셋팅*/
        MyOrdealDungeonStagePlayData stagePlayData = myOrdealDungeonStagePlayDataRepository.findByUseridUser(userId);
        stagePlayData.DefeatStagePlay();

//        //팀동료 케릭터들 피로도 깍기
//        MyTeamInfo myTeamInfo = myTeamInfoRepository.findByUseridUser(userId);
//        String[] myTeams = myTeamInfo.getOrdealDungeonPlayTeam().split(",");
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
//            characterFatigabilityLogDto.setCharacterFatigabilityLogDto("시련의 던전 " + floor + "층 진행", characterId, previousFatigability, presentFatigability);
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
