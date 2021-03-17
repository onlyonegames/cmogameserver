package com.onlyonegames.eternalfantasia.domain.service.HotTimeEvent;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;
import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.EventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Event.EventResponseDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Event.MonsterKillEventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.FieldSaveDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.HotTimeEventDto.HotTimeSchedulerDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.HotTimeEventDto.MyHotTimeInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.BelongingInventoryLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CurrencyLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.UserFieldObjectInfoListDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Event.CommonEventScheduler;
import com.onlyonegames.eternalfantasia.domain.model.entity.Event.MyMonsterKillEventMissionData;
import com.onlyonegames.eternalfantasia.domain.model.entity.HotTimeEvent.HotTimeScheduler;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.ItemType;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyFieldSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.EquipmentMaterialInfoTable;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Event.CommonEventSchedulerRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Event.MyMonsterKillEventMissionDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.HotTimeEvent.HotTimeSchedulerRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.BelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.ItemTypeRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyFieldSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.util.MathHelper;
import com.onlyonegames.util.StringMaker;
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
public class HotTimeEventService {
    private final MyFieldSaveDataRepository myFieldSaveDataRepository;
    private final GameDataTableService gameDataTableService;
    private final ItemTypeRepository itemTypeRepository;
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final UserRepository userRepository;
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final HotTimeSchedulerRepository hotTimeSchedulerRepository;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;
    private final MyMonsterKillEventMissionDataRepository myMonsterKillEventMissionDataRepository;
    private final CommonEventSchedulerRepository commonEventSchedulerRepository;

    public Map<String, Object> getHotTimeEvent(Map<String, Object> map) {
        List<HotTimeScheduler> hotTimeSchedulerList = hotTimeSchedulerRepository.findAll();
        map.put("HotTimeList",hotTimeSchedulerList);
        return map;
    }

    public Map<String, Object> deleteHotTimeEvent(Long hotTimeId, Map<String, Object> map) {
        hotTimeSchedulerRepository.deleteById(hotTimeId);
        return map;
    }

    public Map<String, Object> setHotTimeEvent(HotTimeSchedulerDto dto, Map<String, Object> map) {
        HotTimeScheduler hotTimeScheduler = dto.ToEntity();
        hotTimeScheduler = hotTimeSchedulerRepository.save(hotTimeScheduler);
        HotTimeSchedulerDto hotTimeSchedulerDto = new HotTimeSchedulerDto();
        hotTimeSchedulerDto.InitFormDbData(hotTimeScheduler);
        map.put("hotTimeScheduler", hotTimeSchedulerDto);
        return map;
    }

    public Map<String, Object> gettingObject(Long userId, int selectObjectId, Map<String, Object> map) {
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
        if(!hotTime){
//            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: HotTime TimeOut.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
//            throw new MyCustomException("Fail! -> Cause: HotTime TimeOut.", ResponseErrorCode.NOT_FIND_DATA);
            MyFieldSaveData myFieldSaveData = myFieldSaveDataRepository.findByUseridUser(userId).orElse(null);
            if(myFieldSaveData == null){
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyFieldSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: MyFieldSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            String json_saveDataValue = myFieldSaveData.getJson_saveDataValue();
            FieldSaveDataDto fieldSaveDataDto = JsonStringHerlper.ReadValueFromJson(json_saveDataValue, FieldSaveDataDto.class);

            map.put("fieldSaveDataDto", fieldSaveDataDto);
            map.put("lastClearTime", myFieldSaveData.getLastClearTime());
            map.put("hotTime", hotTime);
            map.put("hotTimeList", myHotTimeInfoDto);
            return map;
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

        /*레타의 사냥 주문 : 체크 준비*/
        LocalDateTime now = LocalDateTime.now();
        List<CommonEventScheduler> commonEventSchedulerList = commonEventSchedulerRepository.findByEventContentsTable_idAndStartTimeBeforeAndEndTimeAfter(3,now,now);
        if(commonEventSchedulerList.size()>1){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Wrong MonsterKillEventScheduler find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Wrong MonsterKillEventScheduler find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        CommonEventScheduler monsterKillEventScheduler = null;
        if(!commonEventSchedulerList.isEmpty())
            monsterKillEventScheduler = commonEventSchedulerList.get(0);
        MyMonsterKillEventMissionData myMonsterKillEventMissionData = null;
        String jsonMyMonsterKillEventMissionData = null;
        MonsterKillEventMissionDataDto monsterKillEventMissionDataDto = null;
        if(monsterKillEventScheduler != null){
            myMonsterKillEventMissionData = myMonsterKillEventMissionDataRepository.findByUseridUser(userId).orElse(null);
            if (myMonsterKillEventMissionData == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMonsterKillEventMissionData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: MyMonsterKillEventMissionData not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            jsonMyMonsterKillEventMissionData = myMonsterKillEventMissionData.getJson_saveDataValue();
            monsterKillEventMissionDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyMonsterKillEventMissionData, MonsterKillEventMissionDataDto.class);
        }
        boolean changedMonsterKillEventMissionData = false;

        MyFieldSaveData myFieldSaveData = myFieldSaveDataRepository.findByUseridUser(userId).orElse(null);
        if(myFieldSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyFieldSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyFieldSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        int fieldNo = 0;
        String json_saveDataValue = myFieldSaveData.getJson_hotTimeSaveDataValue();
        myHotTimeInfoDto = JsonStringHerlper.ReadValueFromJson(json_saveDataValue, MyHotTimeInfoDto.class);
        UserFieldObjectInfoListDto.ObjectData tempObjectData = new UserFieldObjectInfoListDto.ObjectData();
        String getItem = "";
        for(UserFieldObjectInfoListDto.UserFieldObjectListInfoDto userFieldObjectListInfoDto : myHotTimeInfoDto.userFieldObjectInfoListDto.userFieldObjectListInfoDtoList){
            for(UserFieldObjectInfoListDto.UserFieldObjectInfoDto userFieldObjectInfoDto:userFieldObjectListInfoDto.userFieldObjectInfoDtoList){
                for(UserFieldObjectInfoListDto.ObjectData objectData:userFieldObjectInfoDto.objectViewList){
                    if(selectObjectId == objectData.id) {
                        if(objectData.state == 2) {
                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_GETTING.getIntegerValue(), "Fail! -> Cause: ALREADY_GETTING", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                            throw new MyCustomException("Fail! -> Cause: ALREADY_GETTING", ResponseErrorCode.ALREADY_GETTING);
                        }
                        getItem = userFieldObjectInfoDto.gettingItem;
                        tempObjectData = objectData;
                        fieldNo = userFieldObjectListInfoDto.field;
                        break;
                    }
                }
                if(getItem.equals("material")||getItem.equals("Gold") ||getItem.equals("Diamond"))
                    break;
            }
            if(getItem.equals("material")||getItem.equals("Gold") ||getItem.equals("Diamond"))
                break;
        }
        if(getItem.equals("material")) {
            BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
            List<ItemType> itemTypeList = itemTypeRepository.findAll();
            ItemType materialItemType = itemTypeList.stream()
                    .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Material)
                    .findAny()
                    .orElse(null);
            if(materialItemType == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ItemType Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: ItemType Can't find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList = gameDataTableService.EquipmentMaterialInfoTableList();
            int randomIndex = (int)MathHelper.Range(0, equipmentMaterialInfoTableList.size());
            EquipmentMaterialInfoTable selectedMaterial = equipmentMaterialInfoTableList.get(randomIndex);

            String rewardCode = selectedMaterial.getCode();
            int gettingCount = 2;
            if(tempObjectData.isSpecial)
                gettingCount = 5;
//            EquipmentMaterialInfoTable equipmentMaterial = equipmentMaterialInfoTableList.stream()
//                    .filter(a -> a.getCode().equals(rewardCode))
//                    .findAny()
//                    .orElse(null);
//            if(equipmentMaterial == null) {
//                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: EquipmentMaterialInfoTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
//                throw new MyCustomException("Fail! -> Cause: EquipmentMaterialInfoTable not find.", ResponseErrorCode.NOT_FIND_DATA);
//            }
            List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
            BelongingInventory inventoryItem = belongingInventoryList.stream()
                    .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Material && a.getItemId() == selectedMaterial.getId())
                    .findAny()
                    .orElse(null);
            if(inventoryItem != null) {
                belongingInventoryLogDto.setPreviousValue(inventoryItem.getCount());
                inventoryItem.AddItem(gettingCount, selectedMaterial.getStackLimit());

                BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                belongingInventoryDto.InitFromDbData(inventoryItem);
                belongingInventoryDto.setCount(gettingCount);
                String temp = tempObjectData.isSpecial?"스페셜":"";
                belongingInventoryLogDto.setBelongingInventoryLogDto("(HotTimeEvent) "+fieldNo+"번 필드 "+temp+"광석 획득", inventoryItem.getId(), inventoryItem.getItemId(), inventoryItem.getItemType(),
                        gettingCount, inventoryItem.getCount());
                map.put("reward_material", belongingInventoryDto);
            }
            else {
                BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                belongingInventoryDto.setUseridUser(userId);
                belongingInventoryDto.setItemId(selectedMaterial.getId());
                belongingInventoryDto.setCount(gettingCount);
                belongingInventoryDto.setItemType(materialItemType);
                BelongingInventory willAddBelongingInventoryItem = belongingInventoryDto.ToEntity();
                willAddBelongingInventoryItem = belongingInventoryRepository.save(willAddBelongingInventoryItem);
                belongingInventoryList.add(willAddBelongingInventoryItem);

                belongingInventoryDto.setCount(gettingCount);
                belongingInventoryLogDto.setPreviousValue(0);
                String temp = tempObjectData.isSpecial?"스페셜":"일반";
                belongingInventoryLogDto.setBelongingInventoryLogDto("(HotTimeEvent) "+fieldNo+"번 필드 "+temp+"광석 획득", willAddBelongingInventoryItem.getId(), willAddBelongingInventoryItem.getItemId(), willAddBelongingInventoryItem.getItemType(),
                        gettingCount, willAddBelongingInventoryItem.getCount());
                map.put("reward_material", belongingInventoryDto);
            }
            tempObjectData.state = 2;
            String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, belongingLog);
        }
        else if(getItem.equals("Gold")){
            User user = userRepository.findById(userId).orElse(null);
            if(user == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
            }
            CurrencyLogDto currencyLogDto = new CurrencyLogDto();
            int gettingCount = 1000;
            if(tempObjectData.isSpecial)
                gettingCount = 2500;
            int preGold = user.getGold();
            user.AddGold(gettingCount);
            map.put("reward_gold", gettingCount);
            String temp = tempObjectData.isSpecial?"스페셜":"일반";
            currencyLogDto.setCurrencyLogDto("(HotTimeEvent) "+fieldNo+"번 필드 "+temp+"몬스터 사냥","골드", preGold, gettingCount, user.getGold());
            String goldLog = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
            loggingService.setLogging(userId, 1,goldLog);
            tempObjectData.state = 2;
            if(fieldNo>7)
                fieldNo -= 7;
            StringMaker.Clear();
            StringMaker.stringBuilder.append("Chapter");
            StringMaker.stringBuilder.append(fieldNo);
            String missionParamStr = StringMaker.stringBuilder.toString();
            /* 패스 업적 : 골드획득*/
            changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.ERNING_GOLD.name(),"empty", gettingCount, gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
            if(monsterKillEventScheduler != null){
                /*레타의 사냥 주문 : 몬스터 사냥*/
                changedMonsterKillEventMissionData = monsterKillEventMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.MONSTER_KILL_COUNT.name(), missionParamStr, gameDataTableService.MonsterKillEventTableList()) || changedMonsterKillEventMissionData;
            }
        }
        else if(getItem.equals("Diamond")) {
            User user = userRepository.findById(userId).orElse(null);
            if(user == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
            }
            CurrencyLogDto currencyLogDto = new CurrencyLogDto();
            int gettingCount = 2;
            if(tempObjectData.isSpecial)
                gettingCount = 5;
            int prediamond = user.getDiamond();
            user.AddDiamond(gettingCount);
            map.put("reward_diamond", gettingCount);
            String temp = tempObjectData.isSpecial?"스페셜":"일반";
            currencyLogDto.setCurrencyLogDto("(HotTimeEvent) "+fieldNo+"번 필드 "+temp+"보물상자 획득","다이아", prediamond, gettingCount, user.getDiamond());
            String goldLog = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
            loggingService.setLogging(userId, 1,goldLog);
            tempObjectData.state = 2;
        }
        json_saveDataValue = JsonStringHerlper.WriteValueAsStringFromData(myHotTimeInfoDto);
        myFieldSaveData.ResetHotTimeSaveDataValue(json_saveDataValue);
        map.put("hotTime", hotTime);
        map.put("hotTimeList", myHotTimeInfoDto);

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
        /* 업적 : 필드 아이템 획득 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GET_FIELD_OBJECT.name(),"empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
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
        /* 레타의 사냥 주문 : 미션 데이터 변경점 적용*/
        if(changedMonsterKillEventMissionData) {
            EventResponseDto.MonsterKillEventDataDto monsterKillEventDataDto = new EventResponseDto.MonsterKillEventDataDto();
            List<MissionsDataDto.MissionData> clientData = new ArrayList<>();
            clientData = monsterKillEventMissionDataDto.ImportQuestMissionSendToClient(gameDataTableService.MonsterKillEventTableList());
            monsterKillEventDataDto.SetMonsterKillEventDataDto(clientData, monsterKillEventScheduler.getEndTime());
            jsonMyMonsterKillEventMissionData = JsonStringHerlper.WriteValueAsStringFromData(monsterKillEventMissionDataDto);
            myMonsterKillEventMissionData.ResetSaveDataValue(jsonMyMonsterKillEventMissionData);
            map.put("exchange_myMonsterKillEventMissionData", true);
            map.put("myMonsterKillEventMissionData", monsterKillEventDataDto);
        }
        return map;
    }
}
