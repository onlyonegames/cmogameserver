package com.onlyonegames.eternalfantasia.domain.service;
import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;
import com.onlyonegames.eternalfantasia.domain.model.dto.*;
import com.onlyonegames.eternalfantasia.domain.model.dto.ChapterSaveDto.ChapterSaveData;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.EventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Event.EventResponseDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Event.FieldExchangeItemObjectDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Event.MonsterKillEventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Event.MyFieldExchangeItemEventDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.GiftItemDto.GiftItemDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.HotTimeEventDto.HotTimeFieldObjectInfoListDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.HotTimeEventDto.MyHotTimeInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.BelongingInventoryLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CurrencyLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.EquipmentLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.GiftLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.MailSendRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.GotchaCharacterResponseDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ShopDto.MyShopItemsList;
import com.onlyonegames.eternalfantasia.domain.model.entity.*;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyGiftInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Event.CommonEventScheduler;
import com.onlyonegames.eternalfantasia.domain.model.entity.Event.MyMonsterKillEventMissionData;
import com.onlyonegames.eternalfantasia.domain.model.entity.HotTimeEvent.HotTimeScheduler;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.ItemType;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Shop.MyShopInfo;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.*;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyGiftInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Event.CommonEventSchedulerRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Event.MyMonsterKillEventMissionDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.HotTimeEvent.HotTimeSchedulerRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.BelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.HeroEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.ItemTypeRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Shop.MyShopInfoRepository;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.MyFieldDungeonSeasonService;
import com.onlyonegames.eternalfantasia.domain.service.Mail.MyMailBoxService;
import com.onlyonegames.eternalfantasia.etc.*;
import com.onlyonegames.util.MathHelper;
import com.onlyonegames.util.StringMaker;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyNewFieldSaveDataService {

    private final MyFieldSaveDataRepository myFieldSaveDataRepository;
    private final ItemTypeRepository itemTypeRepository;
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final UserRepository userRepository;
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final GameDataTableService gameDataTableService;
    private final MyChapterSaveDataRepository myChapterSaveDataRepository;
    private final HotTimeSchedulerRepository hotTimeSchedulerRepository;
    private final CommonEventSchedulerRepository commonEventSchedulerRepository;
    private final ErrorLoggingService errorLoggingService;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;
    private final MyMonsterKillEventMissionDataRepository myMonsterKillEventMissionDataRepository;
    private final MyAttendanceDataRepository myAttendanceDataRepository;
    private final MyGiftInventoryRepository myGiftInventoryRepository;
    private final HeroEquipmentInventoryRepository heroEquipmentInventoryRepository;
    private final MyCharactersRepository myCharactersRepository;
    private final MyShopInfoRepository myShopInfoRepository;
    private final MyMailBoxService myMailBoxService;
    private final LoggingService loggingService;
    private final MyFieldDungeonSeasonService myFieldDungeonSeasonService;

    /*필드선택*/
    public Map<String, Object> selecteField(Long userId, int plaingFieldNo, Map<String, Object> map) {
        if(plaingFieldNo > 14 || plaingFieldNo < 0) {
            throw new MyCustomException("Fail! -> Cause: Wrong FieldNo", ResponseErrorCode.WRONG_FIELDNO);
        }

        MyChapterSaveData myChapterSaveData = myChapterSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myChapterSaveData == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyChapterSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyChapterSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String saveDataString = myChapterSaveData.getSaveDataValue();
        ChapterSaveData chapterSaveData = JsonStringHerlper.ReadValueFromJson(saveDataString, ChapterSaveData.class);
        int openChapterMax = 0;
        for(ChapterSaveData.ChapterPlayInfo chapterPlayInfo : chapterSaveData.chapterData.chapterPlayInfosList) {
            openChapterMax++;
            if(!chapterPlayInfo.isOpend){
                break;
            }
        }
        if(openChapterMax < plaingFieldNo) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.WRONG_FIELDNO.getIntegerValue(), "Fail! -> Cause: Wrong FieldNo", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Wrong FieldNo", ResponseErrorCode.WRONG_FIELDNO);
        }

        MyFieldSaveData myFieldSaveData = myFieldSaveDataRepository.findByUseridUser(userId).orElse(null);
        if(myFieldSaveData == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyFieldSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyFieldSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
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
        //HotTimeFieldObjectInfoListDto hotTimeFieldObjectInfoListDto = new HotTimeFieldObjectInfoListDto();
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
        List<CommonEventScheduler> commonEventSchedulerList = commonEventSchedulerRepository.findByEventContentsTable_idAndStartTimeBeforeAndEndTimeAfter(1, nowTime, nowTime);
        if(1< commonEventSchedulerList.size()){//TODO ErrorCode 추가 필요 EventScheduler가 중복 기간으로 설정됨.
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: EventScheduler not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: EventScheduler not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        CommonEventScheduler commonEventScheduler = null;
        if(!commonEventSchedulerList.isEmpty()){
            if(commonEventSchedulerList.get(0).getEventContentsTable().getId()==1) {
                commonEventScheduler = commonEventSchedulerList.get(0);
            }
        }
        boolean fieldExchangeItemEvent = true;
        if(commonEventScheduler == null)
            fieldExchangeItemEvent = false;
        MyFieldExchangeItemEventDto myFieldExchangeItemEventDto = new MyFieldExchangeItemEventDto();
        if(fieldExchangeItemEvent){
            if(myFieldSaveData.getJson_fieldExchangeItemSaveDataValue() == null){
                myFieldExchangeItemEventDto = SetFieldExchangeItemEvent(commonEventScheduler.getId(), commonEventScheduler.getJson_FieldExchangeItemEvent());
                myFieldSaveData.ResetFieldExchangeItemSaveDataValue(JsonStringHerlper.WriteValueAsStringFromData(myFieldExchangeItemEventDto));
            }
            myFieldExchangeItemEventDto = JsonStringHerlper.ReadValueFromJson(myFieldSaveData.getJson_fieldExchangeItemSaveDataValue(), MyFieldExchangeItemEventDto.class);
            if(!commonEventScheduler.getId().equals(myFieldExchangeItemEventDto.eventId)) {
                myFieldExchangeItemEventDto = SetFieldExchangeItemEvent(commonEventScheduler.getId(), commonEventScheduler.getJson_FieldExchangeItemEvent());
                myFieldSaveData.ResetFieldExchangeItemSaveDataValue(JsonStringHerlper.WriteValueAsStringFromData(myFieldExchangeItemEventDto));
            }
            //임시 코드, 기존 정보 상이한 유저에 대해서 이벤트 변경 해주도록 처리
            List<MyFieldExchangeItemEventDto.MaxExchange> maxExchangeList = myFieldExchangeItemEventDto.maxExchangeList;
            boolean checked = false;
            for(MyFieldExchangeItemEventDto.MaxExchange maxExchange : maxExchangeList){
                if(maxExchange.itemId == 3 && maxExchange.exchangeableCount > 1) {
                    maxExchange.exchangeableCount = 1;
                    checked = true;
                }
                if(checked && maxExchange.itemId == 4 && maxExchange.exchangeableCount < 11) {
                    maxExchange.exchangeableCount = 20;
                    checked = true;
                }
                else if(checked && maxExchange.itemId == 6 && maxExchange.exchangeableCount > 5 ) {
                    maxExchange.exchangeableCount = 5;
                    checked = true;
                }
                else if(checked && maxExchange.itemId == 7 && maxExchange.exchangeableCount > 1 ) {
                    maxExchange.exchangeableCount = 1;
                    checked = true;
                }
            }
            if(checked) {
                myFieldSaveData.ResetFieldExchangeItemSaveDataValue(JsonStringHerlper.WriteValueAsStringFromData(myFieldExchangeItemEventDto));
            }

        } else {
            myFieldExchangeItemEventDto.userFieldExchangeItemEventDto = new UserFieldObjectInfoListDto();
        }

        String json_saveDataValue = myFieldSaveData.getJson_saveDataValue();
        FieldSaveDataDto fieldSaveDataDto = JsonStringHerlper.ReadValueFromJson(json_saveDataValue, FieldSaveDataDto.class);
        /*출석 보상 : 체크 준비*/
        MyAttendanceData myAttendanceData = myAttendanceDataRepository.findByUseridUser(userId).orElse(null);
        if(myAttendanceData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyAttendanceData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyAttendanceData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
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
        if(myEternalPassMissionsData.IsResetDailyMissionClearTime()) {
            myEternalPassMissionDataDto.DailyMissionsReset();

            myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.LOGIN_COUNT.name(), "empty", gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList());
            changedEternalPassMissionsData = true;
        }

        fieldSaveDataDto.SetPlaingFieldNo(plaingFieldNo);
        if(myFieldSaveData.IsResetTime()) {
            CommonVariableTable commonVariableTable = gameDataTableService.CommonVariableTable().get(0);
            fieldSaveDataDto.userFieldObjectInfoListDto.Reset(commonVariableTable.getFieldSpecialObjectPercent());
            if(fieldExchangeItemEvent) {
                myFieldExchangeItemEventDto.userFieldExchangeItemEventDto.Reset(0);
                myFieldSaveData.ResetFieldExchangeItemSaveDataValue(JsonStringHerlper.WriteValueAsStringFromData(myFieldExchangeItemEventDto));
            }
            gettingPackageItem(userId, map);
        }
        myAttendanceData.CheckAttendanceTime();

        AttendanceResponseDto attendanceResponseDto = new AttendanceResponseDto();
        attendanceResponseDto.SetAttendanceResponseDto(myAttendanceData.isReceivableReward(), myAttendanceData.getGettingCount());
        map.put("attendanceResponse", attendanceResponseDto);

        if(plaingFieldNo == 0) {
            //마을 선택시는 이전 선택된 모드 그대로
        }
        else if(plaingFieldNo != 0 && plaingFieldNo > 7)
            fieldSaveDataDto.modeLevel = true;
        else
            fieldSaveDataDto.modeLevel = false;
        json_saveDataValue = JsonStringHerlper.WriteValueAsStringFromData(fieldSaveDataDto);
        myFieldSaveData.ResetSaveDataValue(json_saveDataValue);

        //필드 선택때 마다 필드던전 정보 반환
        myFieldDungeonSeasonService.GetSeasonInfoForField(userId, map);
        map.put("fieldSaveDataDto", fieldSaveDataDto);
        map.put("hotTime", hotTime);
        map.put("hotTimeList", myHotTimeInfoDto);
        map.put("fieldExchangeItemEvent", fieldExchangeItemEvent);
        map.put("fieldExchangeItemEventList", myFieldExchangeItemEventDto);
        map.put("lastClearTime", myFieldSaveData.getLastClearTime());

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
        myFieldExchangeItemEventDto.eventId = eventId;
        return myFieldExchangeItemEventDto;
    }

    /*영웅 혹은 비공정선택, 0 = 영웅, 1~ 비공정아이디 2020.03.16현재 비공정은 1번 한개뿐*/
    public Map<String, Object> changeSkywalker(Long userId, int plaingFieldNo, int changeId, Map<String, Object> map) {
        MyFieldSaveData myFieldSaveData = myFieldSaveDataRepository.findByUseridUser(userId).orElse(null);
        if(myFieldSaveData == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyFieldSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyFieldSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String json_saveDataValue = myFieldSaveData.getJson_saveDataValue();
        FieldSaveDataDto fieldSaveDataDto = JsonStringHerlper.ReadValueFromJson(json_saveDataValue, FieldSaveDataDto.class);

        if(fieldSaveDataDto.plaingFieldNo != plaingFieldNo) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.WRONG_FIELDNO.getIntegerValue(), "Fail! -> Cause: Wrong FieldNo", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Wrong FieldNo", ResponseErrorCode.WRONG_FIELDNO);
        }

        MyChapterSaveData myChapterSaveData = myChapterSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myChapterSaveData == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyChapterSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyChapterSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String saveDataString = myChapterSaveData.getSaveDataValue();
        ChapterSaveData chapterSaveData = JsonStringHerlper.ReadValueFromJson(saveDataString, ChapterSaveData.class);

        int openChapterMax = 0;
        for(ChapterSaveData.ChapterPlayInfo chapterPlayInfo : chapterSaveData.chapterData.chapterPlayInfosList) {
            openChapterMax++;
            if(!chapterPlayInfo.isOpend){
                break;
            }
        }
        /**By 현호  2020-08-31 추후 비공정 탈 조건이 명확해지면 예외처리 필요*/
//        if(changeId != 0 && openChapterMax < 8)
//            throw new MyCustomException("Fail! -> Cause: Not yet use skywalker", ResponseErrorCode.NOT_YET_USE_SKYWALKER);
        LocalDateTime nowTime = LocalDateTime.now();
        List<CommonEventScheduler> commonEventSchedulerList = commonEventSchedulerRepository.findByEventContentsTable_idAndStartTimeBeforeAndEndTimeAfter(1, nowTime, nowTime);
        if(1< commonEventSchedulerList.size()){//TODO ErrorCode 추가 필요 EventScheduler가 중복 기간으로 설정됨.
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: EventScheduler not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: EventScheduler not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        CommonEventScheduler commonEventScheduler = null;
        if(!commonEventSchedulerList.isEmpty()){
            if(commonEventSchedulerList.get(0).getEventContentsTable().getId()==1) {
                commonEventScheduler = commonEventSchedulerList.get(0);
            }
        }
        boolean fieldExchangeItemEvent = true;
        if(commonEventScheduler == null)
            fieldExchangeItemEvent = false;
        MyFieldExchangeItemEventDto myFieldExchangeItemEventDto = new MyFieldExchangeItemEventDto();
        if(fieldExchangeItemEvent){
            if(myFieldSaveData.getJson_fieldExchangeItemSaveDataValue() == null){
                myFieldExchangeItemEventDto = SetFieldExchangeItemEvent(commonEventScheduler.getId(), commonEventScheduler.getJson_FieldExchangeItemEvent());
                myFieldSaveData.ResetFieldExchangeItemSaveDataValue(JsonStringHerlper.WriteValueAsStringFromData(myFieldExchangeItemEventDto));
            }
            myFieldExchangeItemEventDto = JsonStringHerlper.ReadValueFromJson(myFieldSaveData.getJson_fieldExchangeItemSaveDataValue(), MyFieldExchangeItemEventDto.class);
            if(!commonEventScheduler.getId().equals(myFieldExchangeItemEventDto.eventId)) {
                myFieldExchangeItemEventDto = SetFieldExchangeItemEvent(commonEventScheduler.getId(), commonEventScheduler.getJson_FieldExchangeItemEvent());
                myFieldSaveData.ResetFieldExchangeItemSaveDataValue(JsonStringHerlper.WriteValueAsStringFromData(myFieldExchangeItemEventDto));
            }
        }

        CommonVariableTable commonVariableTable = gameDataTableService.CommonVariableTable().get(0);
        fieldSaveDataDto.SetEquipSkywalkerId(changeId);
        if(myFieldSaveData.IsResetTime()) {
            fieldSaveDataDto.userFieldObjectInfoListDto.Reset(commonVariableTable.getFieldSpecialObjectPercent());
            if(fieldExchangeItemEvent) {
                myFieldExchangeItemEventDto.userFieldExchangeItemEventDto.Reset(0);
                myFieldSaveData.ResetFieldExchangeItemSaveDataValue(JsonStringHerlper.WriteValueAsStringFromData(myFieldExchangeItemEventDto));
            }
        }
        json_saveDataValue = JsonStringHerlper.WriteValueAsStringFromData(fieldSaveDataDto);
        myFieldSaveData.ResetSaveDataValue(json_saveDataValue);

        if(fieldExchangeItemEvent){
            map.put("fieldExchangeItemEvent", true);
            map.put("fieldExchangeItemEventList", myFieldExchangeItemEventDto);
        }

        map.put("fieldSaveDataDto", fieldSaveDataDto);
        map.put("lastClearTime", myFieldSaveData.getLastClearTime());
        return map;
    }
    /*오브젝트 획득*/
    public Map<String, Object> gettingObject(Long userId, int selectObjectId, Map<String, Object> map) {
        MyFieldSaveData myFieldSaveData = myFieldSaveDataRepository.findByUseridUser(userId).orElse(null);
        if(myFieldSaveData == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyFieldSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyFieldSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
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

        int fieldNo = 0;
        String json_saveDataValue = myFieldSaveData.getJson_saveDataValue();
        FieldSaveDataDto fieldSaveDataDto = JsonStringHerlper.ReadValueFromJson(json_saveDataValue, FieldSaveDataDto.class);
        UserFieldObjectInfoListDto.ObjectData tempObjectData = new UserFieldObjectInfoListDto.ObjectData();
        String getItem = new String();
        for(UserFieldObjectInfoListDto.UserFieldObjectListInfoDto userFieldObjectListInfoDto : fieldSaveDataDto.userFieldObjectInfoListDto.userFieldObjectListInfoDtoList){
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
                if(getItem.equals("material") || getItem.equals("Gold") || getItem.equals("Diamond"))
                    break;
            }
            if(getItem.equals("material") || getItem.equals("Gold") || getItem.equals("Diamond"))
                break;
        }

        String temp = tempObjectData.isSpecial?"스페셜":"";
        StringMaker.Clear();
        StringMaker.stringBuilder.append("(일반) ");
        StringMaker.stringBuilder.append(fieldNo);
        StringMaker.stringBuilder.append("번 필드 ");
        StringMaker.stringBuilder.append(temp);

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
            EquipmentMaterialInfoTable equipmentMaterial = equipmentMaterialInfoTableList.stream()
                    .filter(a -> a.getCode().equals(rewardCode))
                    .findAny()
                    .orElse(null);
            if(equipmentMaterial == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: EquipmentMaterialInfoTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: EquipmentMaterialInfoTable not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
            BelongingInventory inventoryItem = belongingInventoryList.stream()
                    .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Material && a.getItemId() == equipmentMaterial.getId())
                    .findAny()
                    .orElse(null);
            if(inventoryItem != null) {
                belongingInventoryLogDto.setPreviousValue(inventoryItem.getCount());
                inventoryItem.AddItem(gettingCount, equipmentMaterial.getStackLimit());

                BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                belongingInventoryDto.InitFromDbData(inventoryItem);
                belongingInventoryDto.setCount(gettingCount);
                StringMaker.stringBuilder.append("광석 획득");
                String workingPosition = StringMaker.stringBuilder.toString();
                belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, inventoryItem.getId(), inventoryItem.getItemId(), inventoryItem.getItemType(),
                        gettingCount, inventoryItem.getCount());
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

                belongingInventoryDto.setId(willAddBelongingInventoryItem.getId());
                belongingInventoryLogDto.setPreviousValue(0);
                StringMaker.stringBuilder.append("광석 획득");
                String workingPosition = StringMaker.stringBuilder.toString();
                belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, willAddBelongingInventoryItem.getId(), willAddBelongingInventoryItem.getItemId(), willAddBelongingInventoryItem.getItemType(),
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
            StringMaker.stringBuilder.append("몬스터 사냥");
            String workingPosition = StringMaker.stringBuilder.toString();
            currencyLogDto.setCurrencyLogDto(workingPosition,"골드", preGold, gettingCount, user.getGold());
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
            StringMaker.stringBuilder.append("보물상자 획득");
            String workingPosition = StringMaker.stringBuilder.toString();
            currencyLogDto.setCurrencyLogDto(workingPosition,"다이아", prediamond, gettingCount, user.getDiamond());
            String goldLog = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
            loggingService.setLogging(userId, 1,goldLog);

            tempObjectData.state = 2;
        }

        LocalDateTime nowTime = LocalDateTime.now();
        List<CommonEventScheduler> fieldExchangeItemEventSchedulerList = commonEventSchedulerRepository.findByEventContentsTable_idAndStartTimeBeforeAndEndTimeAfter(1, nowTime, nowTime);
        if(1<fieldExchangeItemEventSchedulerList.size()){//TODO ErrorCode 추가 필요 EventScheduler가 중복 기간으로 설정됨.
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: EventScheduler not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: EventScheduler not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        CommonEventScheduler commonEventScheduler = null;
        if(!fieldExchangeItemEventSchedulerList.isEmpty()){
            if(fieldExchangeItemEventSchedulerList.get(0).getEventContentsTable().getId()==1) {
                commonEventScheduler = fieldExchangeItemEventSchedulerList.get(0);
            }
        }
        boolean fieldExchangeItemEvent = true;
        if(commonEventScheduler == null)
            fieldExchangeItemEvent = false;
        MyFieldExchangeItemEventDto myFieldExchangeItemEventDto = new MyFieldExchangeItemEventDto();
        if(fieldExchangeItemEvent){
            if(myFieldSaveData.getJson_fieldExchangeItemSaveDataValue() == null){
                myFieldExchangeItemEventDto = SetFieldExchangeItemEvent(commonEventScheduler.getId(), commonEventScheduler.getJson_FieldExchangeItemEvent());
                myFieldSaveData.ResetFieldExchangeItemSaveDataValue(JsonStringHerlper.WriteValueAsStringFromData(myFieldExchangeItemEventDto));
            }
            myFieldExchangeItemEventDto = JsonStringHerlper.ReadValueFromJson(myFieldSaveData.getJson_fieldExchangeItemSaveDataValue(), MyFieldExchangeItemEventDto.class);
            if(!commonEventScheduler.getId().equals(myFieldExchangeItemEventDto.eventId)) {
                myFieldExchangeItemEventDto = SetFieldExchangeItemEvent(commonEventScheduler.getId(), commonEventScheduler.getJson_FieldExchangeItemEvent());
                myFieldSaveData.ResetFieldExchangeItemSaveDataValue(JsonStringHerlper.WriteValueAsStringFromData(myFieldExchangeItemEventDto));
            }
        }

        CommonVariableTable commonVariableTable = gameDataTableService.CommonVariableTable().get(0);
        if(myFieldSaveData.IsResetTime()) {
            fieldSaveDataDto.userFieldObjectInfoListDto.Reset(commonVariableTable.getFieldSpecialObjectPercent());
            if(fieldExchangeItemEvent) {
                myFieldExchangeItemEventDto.userFieldExchangeItemEventDto.Reset(0);
                myFieldSaveData.ResetFieldExchangeItemSaveDataValue(JsonStringHerlper.WriteValueAsStringFromData(myFieldExchangeItemEventDto));
            }
        }
        json_saveDataValue = JsonStringHerlper.WriteValueAsStringFromData(fieldSaveDataDto);
        myFieldSaveData.ResetSaveDataValue(json_saveDataValue);
        if(fieldExchangeItemEvent){
            map.put("fieldExchangeItemEvent", true);
            map.put("fieldExchangeItemEventList", myFieldExchangeItemEventDto);
        }
        map.put("fieldSaveDataDto", fieldSaveDataDto);
        map.put("lastClearTime", myFieldSaveData.getLastClearTime());

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

    public Map<String, Object> Reset(Long userId, Map<String, Object> map) {
        MyFieldSaveData myFieldSaveData = myFieldSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myFieldSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyFieldSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyFieldSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String json_saveDataValue = myFieldSaveData.getJson_saveDataValue();
        FieldSaveDataDto fieldSaveDataDto = JsonStringHerlper.ReadValueFromJson(json_saveDataValue, FieldSaveDataDto.class);

        CommonVariableTable commonVariableTable = gameDataTableService.CommonVariableTable().get(0);
        fieldSaveDataDto.userFieldObjectInfoListDto.Reset(commonVariableTable.getFieldSpecialObjectPercent());
        json_saveDataValue = JsonStringHerlper.WriteValueAsStringFromData(fieldSaveDataDto);
        myFieldSaveData.ResetSaveDataValue(json_saveDataValue);

        map.put("fieldSaveDataDto", fieldSaveDataDto);
        return map;
    }

    /*2020-09-02 by rainful 핫타임 오브젝트 클리어 추가*/
    public Map<String, Object> HotTimeReset(Long userId, Map<String, Object> map) {
        MyFieldSaveData myFieldSaveData = myFieldSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myFieldSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyFieldSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyFieldSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String json_saveDataValue = myFieldSaveData.getJson_hotTimeSaveDataValue();

        MyHotTimeInfoDto myHotTimeInfoDto = JsonStringHerlper.ReadValueFromJson(json_saveDataValue, MyHotTimeInfoDto.class);

        myHotTimeInfoDto.userFieldObjectInfoListDto.Reset(0.5f);
        json_saveDataValue = JsonStringHerlper.WriteValueAsStringFromData(myHotTimeInfoDto);

        myFieldSaveData.ResetHotTimeSaveDataValue(json_saveDataValue);
        map.put("myHotTimeInfo", myHotTimeInfoDto);
        return map;
    }

    public Map<String, Object> getAttendanceReward(Long userId, int rewardIndex, Map<String, Object> map) {
        MyAttendanceData myAttendanceData = myAttendanceDataRepository.findByUseridUser(userId).orElse(null);
        if(myAttendanceData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyAttendanceData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyAttendanceData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        if(rewardIndex!=myAttendanceData.getGettingCount()){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.UNDEFINED.getIntegerValue(), "Fail! -> Cause: Wrong RewardIndex Request.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Wrong RewardIndex Request.", ResponseErrorCode.UNDEFINED);
        }
        if(!myAttendanceData.isReceivableReward()){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_RECEIVED_REWARD.getIntegerValue(), "Fail! -> Cause: Already Received Reward.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Already Received Reward.", ResponseErrorCode.ALREADY_RECEIVED_REWARD);
        }
        myAttendanceData.RewardReceive();
        List<AttendanceRewardTable> attendanceRewardTableList = gameDataTableService.AttendanceRewardTableList();
        AttendanceRewardTable attendanceRewardTable = attendanceRewardTableList.get(rewardIndex-1);

//        ReceiveItemCommonDto receiveItemCommonDto = new ReceiveItemCommonDto();
//        MyMissionsData myMissionsData = null;

        String gettingItem = attendanceRewardTable.getGettingItem();
        int gettingCount = attendanceRewardTable.getGettingCount();
        Map<String, Object> fakeMap = new HashMap<>();
        List<MailSendRequestDto.Item> itemList = new ArrayList<>();
        MailSendRequestDto.Item item = new MailSendRequestDto.Item();
        item.setItem(gettingItem, gettingCount);
        itemList.add(item);
        StringMaker.Clear();
        StringMaker.stringBuilder.append(rewardIndex);
        StringMaker.stringBuilder.append("회차 출석보상 지급");
        String title = StringMaker.stringBuilder.toString();
        MailSendRequestDto mailSendRequestDto = new MailSendRequestDto();
        mailSendRequestDto.SetMailSendRequestDto(title, 10000L, userId, SystemMailInfos.ATTENDANCE_REWARD_TITLE, itemList, 1, LocalDateTime.now().plusYears(1));
        myMailBoxService.SendMail(mailSendRequestDto, fakeMap);

        /*메일 정보 반환*/
        myMailBoxService.GetMyMailBox(userId, map);

//        /*업적 : 체크 준비*/
//        if(myMissionsData == null)
//            myMissionsData = myMissionsDataRepository.findByUseridUser(userId).orElseThrow(() -> new MyCustomException("Fail! -> Cause: MyMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA));
//        String jsonMyMissionData = myMissionsData.getJson_saveDataValue();
//        MissionsDataDto myMissionsDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyMissionData, MissionsDataDto.class);
//        boolean changedMissionsData = false;
//
//        /*패스 업적 : 체크 준비*/
//        MyEternalPassMissionsData myEternalPassMissionsData = myEternalPassMissionsDataRepository.findByUseridUser(userId)
//                .orElse(null);
//        if(myEternalPassMissionsData == null) {
//            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
//            throw new MyCustomException("Fail! -> Cause: MyEternalPassMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
//        }
//        String jsonMyEternalPassMissionData = myEternalPassMissionsData.getJson_saveDataValue();
//        EventMissionDataDto myEternalPassMissionDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyEternalPassMissionData, EventMissionDataDto.class);
//        boolean changedEternalPassMissionsData = false;
//
//        StringMaker.Clear();
//        StringMaker.stringBuilder.append("로그인 보상 ");
//        StringMaker.stringBuilder.append(rewardIndex);
//        StringMaker.stringBuilder.append("회 보상 획득");
//        String logWorkingPosition = StringMaker.stringBuilder.toString();
//
//        changedMissionsData = receiveItem(userId, gettingItem, gettingCount, receiveItemCommonDto, myMissionsDataDto, belongingInventoryRepository,
//                gameDataTableService, itemTypeRepository, loggingService, myGiftInventoryRepository, heroEquipmentInventoryRepository, myCharactersRepository, logWorkingPosition) || changedMissionsData;
//
//        map.put("receiveItemCommonDto", receiveItemCommonDto);
//        if(receiveItemCommonDto.getGettingGold()>0) {
//            /* 패스 업적 : 골드획득*/
//            changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.ERNING_GOLD.name(), "empty", receiveItemCommonDto.getGettingGold(), gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
//        }

        myAttendanceData.RewardReceive();
        AttendanceResponseDto attendanceResponseDto = new AttendanceResponseDto();
        attendanceResponseDto.SetAttendanceResponseDto(myAttendanceData.isReceivableReward(), myAttendanceData.getGettingCount());
        map.put("attendanceData", attendanceResponseDto);

//        /* 패스 업적 : 미션 데이터 변경점 적용*/
//        if(changedEternalPassMissionsData) {
//            jsonMyEternalPassMissionData = JsonStringHerlper.WriteValueAsStringFromData(myEternalPassMissionDataDto);
//            myEternalPassMissionsData.ResetSaveDataValue(jsonMyEternalPassMissionData);
//            map.put("exchange_myEternalPassMissionsData", true);
//            map.put("myEternalPassMissionsDataDto", myEternalPassMissionDataDto);
//            map.put("myEternalPassLastDailyMissionClearTime", myEternalPassMissionsData.getLastDailyMissionClearTime());
//            map.put("myEternalPassLastWeeklyMissionClearTime", myEternalPassMissionsData.getLastWeeklyMissionClearTime());
//        }
//
//        /*업적 : 미션 데이터 변경점 적용*/
//        if(changedMissionsData) {
//            jsonMyMissionData = JsonStringHerlper.WriteValueAsStringFromData(myMissionsDataDto);
//            myMissionsData.ResetSaveDataValue(jsonMyMissionData);
//            map.put("exchange_myMissionsData", true);
//            map.put("myMissionsDataDto", myMissionsDataDto);
//            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
//            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());
//        }
        return map;
    }

    private void gettingPackageItem(Long userId, Map<String, Object> map) {
        MyShopInfo myShopInfo = myShopInfoRepository.findByUseridUser(userId).orElse(null);
        if(myShopInfo == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyShopInfo not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyShopInfo not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String json_shopInfo = myShopInfo.getJson_myShopInfos();
        MyShopItemsList myShopItemsList = JsonStringHerlper.ReadValueFromJson(json_shopInfo, MyShopItemsList.class);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for(MyShopItemsList.PackagePerMonth temp :myShopItemsList.packageSpecialInfos.packagePerMonthList) {
            if(temp.bought&&temp.remainCount>0){//월정액 구입 여부 && 남은 횟수가 있는지 확인
                LocalDateTime lastGettingTime = LocalDateTime.parse(temp.lastGettingTime, formatter);
                LocalDateTime now = LocalDateTime.now();
                Duration duration = Duration.between(lastGettingTime, now);
                List<IapTable> iapTableList = gameDataTableService.IapTableList();
                IapTable iapTable = iapTableList.stream().filter(i -> i.getCode().equals(temp.iapCode)).findAny().orElse(null);
                if(iapTable == null){
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: IapTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: IapTable not find.", ResponseErrorCode.NOT_FIND_DATA);
                }
                String[] receiveItemList = iapTable.getGettingItems().split(",");
                String[] receiveItemInfo = receiveItemList[1].split(":");
                for(int i = 1; i<=duration.toDays()&&temp.remainCount>0;i++) { //마지막 보상 받은 시점으로부터 일수만큼 반복 및 남은 횟수가 있는지 확인
                    String getingItem = receiveItemInfo[0];
                    int getttingCount = Integer.parseInt(receiveItemInfo[1]);
                    List<MailSendRequestDto.Item> itemList = new ArrayList<>();
                    MailSendRequestDto.Item item = new MailSendRequestDto.Item();
                    item.setItem(getingItem, getttingCount);
                    itemList.add(item);
                    StringMaker.Clear();
                    StringMaker.stringBuilder.append(iapTable.getName());
                    StringMaker.stringBuilder.append(" ");
                    StringMaker.stringBuilder.append(31-temp.remainCount);
                    StringMaker.stringBuilder.append("회차 아이템 지급");
                    String content = StringMaker.stringBuilder.toString();
                    MailSendRequestDto mailSendRequestDto = new MailSendRequestDto();
                    mailSendRequestDto.setToId(userId);
                    mailSendRequestDto.setFromId(10000L);
                    mailSendRequestDto.setTitle(content);
                    mailSendRequestDto.setContent(SystemMailInfos.SHOP_PURCHASE_TITLE);
                    mailSendRequestDto.setExpireDate(LocalDateTime.now().plusYears(1));
                    mailSendRequestDto.setItemList(itemList);
                    mailSendRequestDto.setMailType(1);
                    myMailBoxService.SendMail(mailSendRequestDto, map);
                    temp.remainCount--;
                    lastGettingTime = LocalDateTime.of(lastGettingTime.toLocalDate().plusDays(1), LocalTime.of(5, 0, 0));
                }
                temp.lastGettingTime = lastGettingTime.format(formatter);
            }
        }

        json_shopInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsList);
        myShopInfo.ResetMyShopInfos(json_shopInfo);
    }

    private boolean receiveItem(Long userId, String gettingItems, String itemsCounts, ReceiveItemCommonDto receiveItemCommonDto, MissionsDataDto myMissionsDataDto,
                                BelongingInventoryRepository belongingInventoryRepository, GameDataTableService gameDataTableService, ItemTypeRepository itemTypeRepository,
                                LoggingService loggingService, MyGiftInventoryRepository myGiftInventoryRepository, HeroEquipmentInventoryRepository heroEquipmentInventoryRepository,
                                MyCharactersRepository myCharactersRepository, String logWorkingPosition
    ) {

        List<BelongingInventory> belongingInventoryList = null;
        List<ItemType> itemTypeList = null;
        ItemType spendAbleItemType = null;
        ItemType materialItemType = null;
        User user = null;
        MyGiftInventory myGiftInventory = null;
        List<MyCharacters> myCharactersList = null;
        List<HeroEquipmentInventory> heroEquipmentInventoryList = null;

        List<BelongingCharacterPieceTable> orignalBelongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
        List<BelongingCharacterPieceTable> copyBelongingCharacterPieceTableList = new ArrayList<>();
        for(BelongingCharacterPieceTable characterPieceTable : orignalBelongingCharacterPieceTableList) {
            if(characterPieceTable.getCode().equals("characterPieceAll"))
                continue;
            copyBelongingCharacterPieceTableList.add(characterPieceTable);
        }

        boolean changedMissionsData = false;

        String[] gettingItemsArray = gettingItems.split(",");
        String[] itemsCountArray = itemsCounts.split(",");
        int gettingItemsCount = gettingItemsArray.length;
        for(int i = 0; i < gettingItemsCount; i++) {
            String gettingItemCode = gettingItemsArray[i];
            int gettingCount = Integer.parseInt(itemsCountArray[i]);

            //피로도 50 회복 물약, 즉시 제작권, 차원석, 강화석, 재련석, 링크웨폰키, 코스튬 무료 티켓
            if(gettingItemCode.equals("recovery_fatigability") || gettingItemCode.equals("ticket_direct_production_equipment")
                    || gettingItemCode.equals("dimensionStone") || gettingItemCode.contains("enchant") || gettingItemCode.contains("resmelt")
                    || gettingItemCode.equals("linkweapon_bronzeKey") || gettingItemCode.equals("linkweapon_silverKey")
                    || gettingItemCode.equals("linkweapon_goldKey") || gettingItemCode.equals("costume_ticket")) {
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                if(belongingInventoryList == null)
                    belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
                if(itemTypeList == null)
                    itemTypeList = itemTypeRepository.findAll();
                if(spendAbleItemType == null)
                    spendAbleItemType = itemTypeList.stream()
                            .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                            .findAny()
                            .orElse(null);
                List<BelongingInventoryDto> receivedSpendableItemList = receiveItemCommonDto.getChangedBelongingInventoryList();
                BelongingInventoryDto belongingInventoryDto = ApplySomeReward.ApplySpendableItem(userId, gettingItemCode, gettingCount, belongingInventoryRepository, belongingInventoryList, spendableItemInfoTableList, spendAbleItemType, logWorkingPosition, loggingService, errorLoggingService);
                int itemId = belongingInventoryDto.getItemId();
                Long itemTypeId = belongingInventoryDto.getItemType().getId();
                BelongingInventoryDto findBelongingInventoryDto = receivedSpendableItemList.stream().filter(a -> a.getItemId()==itemId && a.getItemType().getId().equals(itemTypeId))
                        .findAny()
                        .orElse(null);
                if(findBelongingInventoryDto == null) {
                    receivedSpendableItemList.add(belongingInventoryDto);
                }
                else {
                    findBelongingInventoryDto.AddCount(gettingCount);
                }
            }
            //골드
            else if(gettingItemCode.equals("gold")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousCount = user.getGold();
                user.AddGold(gettingCount);
                currencyLogDto.setCurrencyLogDto(logWorkingPosition, "골드", previousCount, gettingCount, user.getGold());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                receiveItemCommonDto.AddGettingGold(gettingCount);
            }
            //다이아
            else if(gettingItemCode.equals("diamond")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousCount = user.getDiamond();
                user.AddDiamond(gettingCount);
                currencyLogDto.setCurrencyLogDto(logWorkingPosition, "다이아", previousCount, gettingCount, user.getDiamond());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                receiveItemCommonDto.AddGettingDiamond(gettingCount);
            }
            //링크 포인트
            else if(gettingItemCode.equals("linkPoint")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousCount = user.getLinkforcePoint();
                user.AddLinkforcePoint(gettingCount);
                currencyLogDto.setCurrencyLogDto(logWorkingPosition, "링크포인트", previousCount, gettingCount, user.getLinkforcePoint());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                receiveItemCommonDto.AddGettingLinkPoint(gettingCount);
            }
            //아레나 코인
            else if(gettingItemCode.equals("arenaCoin")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousCount = user.getArenaCoin();
                user.AddArenaCoin(gettingCount);
                currencyLogDto.setCurrencyLogDto(logWorkingPosition, "아레나 코인", previousCount, gettingCount, user.getArenaCoin());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                receiveItemCommonDto.AddGettingArenaCoin(gettingCount);
            }
            //아레나 티켓
            else if(gettingItemCode.equals("arenaTicket")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousCount = user.getArenaTicket();
                user.AddArenaTicket(gettingCount);
                currencyLogDto.setCurrencyLogDto(logWorkingPosition, "아레나 티켓", previousCount, gettingCount, user.getArenaTicket());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                receiveItemCommonDto.AddGettingArenaTicket(gettingCount);
            }
            else if(gettingItemCode.equals("lowDragonScale")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousCount = user.getLowDragonScale();
                user.AddLowDragonScale(gettingCount);
                currencyLogDto.setCurrencyLogDto(logWorkingPosition, "용의 비늘(전설)", previousCount, gettingCount, user.getLowDragonScale());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                receiveItemCommonDto.AddGettingLowDragonScale(gettingCount);
            }
            else if(gettingItemCode.equals("middleDragonScale")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousCount = user.getMiddleDragonScale();
                user.AddMiddleDragonScale(gettingCount);
                currencyLogDto.setCurrencyLogDto(logWorkingPosition, "용의 비늘(신성)", previousCount, gettingCount, user.getMiddleDragonScale());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                receiveItemCommonDto.AddGettingMiddleDragonScale(gettingCount);
            }
            else if(gettingItemCode.equals("highDragonScale")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousCount = user.getHighDragonScale();
                user.AddHighDragonScale(gettingCount);
                currencyLogDto.setCurrencyLogDto(logWorkingPosition, "용의 비늘(고대)", previousCount, gettingCount, user.getHighDragonScale());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                receiveItemCommonDto.AddGettingHighDragonScale(gettingCount);
            }
            /*3종, 5종, 8종 재료 상자*/
            else if(gettingItemCode.equals("reward_material_low") || gettingItemCode.equals("reward_material_middle") || gettingItemCode.equals("reward_material_high")) {
                int kindCount = 0;
                if (gettingItemCode.contains("low")) {
                    //3종
                    kindCount = 3;
                } else if (gettingItemCode.contains("middle")) {
                    //5종
                    kindCount = 5;
                }
                else if (gettingItemCode.contains("high")) {
                    //8종
                    kindCount = 8;
                }
                List<BelongingInventoryDto> receivedSpendableItemList = receiveItemCommonDto.getChangedBelongingInventoryList();
                if(belongingInventoryList == null)
                    belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList = gameDataTableService.EquipmentMaterialInfoTableList();
                if(itemTypeList == null)
                    itemTypeList = itemTypeRepository.findAll();
                if(materialItemType == null) {
                    materialItemType = itemTypeList.stream()
                            .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Material)
                            .findAny()
                            .orElse(null);
                }
                List<EquipmentMaterialInfoTable> copyEquipmentMaterialInfoTableList = new ArrayList<>();
                copyEquipmentMaterialInfoTableList.addAll(equipmentMaterialInfoTableList);
                int addIndex = 0;
                while (addIndex < kindCount) {
                    int selectedRandomIndex = (int) MathHelper.Range(0, copyEquipmentMaterialInfoTableList.size());
                    EquipmentMaterialInfoTable equipmentMaterialInfoTable = copyEquipmentMaterialInfoTableList.get(selectedRandomIndex);
                    BelongingInventoryDto belongingInventoryDto = ApplySomeReward.AddEquipmentMaterialItem(equipmentMaterialInfoTable.getCode(), gettingCount, belongingInventoryRepository, itemTypeList, belongingInventoryList, equipmentMaterialInfoTableList, materialItemType, logWorkingPosition, userId, loggingService, errorLoggingService);
                    int itemId = belongingInventoryDto.getItemId();
                    Long itemTypeId = belongingInventoryDto.getItemType().getId();
                    BelongingInventoryDto findBelongingInventoryDto = receivedSpendableItemList.stream().filter(a -> a.getItemId()==itemId && a.getItemType().getId().equals(itemTypeId))
                            .findAny()
                            .orElse(null);
                    if(findBelongingInventoryDto == null) {
                        receivedSpendableItemList.add(belongingInventoryDto);
                    }
                    else {
                        findBelongingInventoryDto.AddCount(gettingCount);
                    }
                    copyEquipmentMaterialInfoTableList.remove(selectedRandomIndex);
                    addIndex++;
                }
            }
            /*특정 재료*/
            else if(gettingItemCode.contains("material")){
                List<BelongingInventoryDto> receivedSpendableItemList = receiveItemCommonDto.getChangedBelongingInventoryList();
                if(belongingInventoryList == null)
                    belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList = gameDataTableService.EquipmentMaterialInfoTableList();
                if(itemTypeList == null)
                    itemTypeList = itemTypeRepository.findAll();
                if(materialItemType == null) {
                    materialItemType = itemTypeList.stream()
                            .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Material)
                            .findAny()
                            .orElse(null);
                }
                BelongingInventoryDto belongingInventoryDto = ApplySomeReward.AddEquipmentMaterialItem(gettingItemCode, gettingCount, belongingInventoryRepository, itemTypeList, belongingInventoryList, equipmentMaterialInfoTableList, materialItemType, logWorkingPosition, userId, loggingService, errorLoggingService);
                int itemId = belongingInventoryDto.getItemId();
                Long itemTypeId = belongingInventoryDto.getItemType().getId();
                BelongingInventoryDto findBelongingInventoryDto = receivedSpendableItemList.stream().filter(a -> a.getItemId()==itemId && a.getItemType().getId().equals(itemTypeId))
                        .findAny()
                        .orElse(null);
                if(findBelongingInventoryDto == null) {
                    receivedSpendableItemList.add(belongingInventoryDto);
                }
                else {
                    findBelongingInventoryDto.AddCount(gettingCount);
                }
            }
            /*모든 선물중 하나*/
            else if(gettingItemCode.equals("giftAll")) {
                List<GiftTable> giftTableList = gameDataTableService.GiftsTableList();
                List<GiftTable> copyGiftTableList = new ArrayList<>(giftTableList);
                copyGiftTableList.remove(25);
                int randIndex = (int) MathHelper.Range(0, copyGiftTableList.size());
                GiftTable giftTable = copyGiftTableList.get(randIndex);
                if(myGiftInventory == null) {
                    myGiftInventory = myGiftInventoryRepository.findByUseridUser(userId)
                            .orElse(null);
                    if(myGiftInventory == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyGiftInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: MyGiftInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                List<GiftItemDtosList.GiftItemDto> changedMyGiftInventoryList = receiveItemCommonDto.getChangedMyGiftInventoryList();
                String inventoryInfosString = myGiftInventory.getInventoryInfos();
                GiftItemDtosList giftItemInventorys = JsonStringHerlper.ReadValueFromJson(inventoryInfosString, GiftItemDtosList.class);

                GiftItemDtosList.GiftItemDto inventoryItemDto = giftItemInventorys.giftItemDtoList.stream()
                        .filter(a -> a.code.equals(giftTable.getCode()))
                        .findAny()
                        .orElse(null);
                if(inventoryItemDto == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find GiftTable", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail -> Cause: Can't Find GiftTable", ResponseErrorCode.NOT_FIND_DATA);
                }
                GiftLogDto giftLogDto = new GiftLogDto();
                giftLogDto.setPreviousValue(inventoryItemDto.count);
                inventoryItemDto.AddItem(gettingCount ,giftTable.getStackLimit());
                giftLogDto.setGiftLogDto(logWorkingPosition, inventoryItemDto.code,gettingCount, inventoryItemDto.count);
                String log = JsonStringHerlper.WriteValueAsStringFromData(giftLogDto);
                loggingService.setLogging(userId, 4, log);
                inventoryInfosString = JsonStringHerlper.WriteValueAsStringFromData(giftItemInventorys);
                myGiftInventory.ResetInventoryInfos(inventoryInfosString);

                GiftItemDtosList.GiftItemDto responseItemDto = new GiftItemDtosList.GiftItemDto();
                responseItemDto.code = inventoryItemDto.code;
                responseItemDto.count = gettingCount;
                changedMyGiftInventoryList.add(responseItemDto);
            }
            /*특정 선물*/
            else if(gettingItemCode.contains("gift_")) {
                List<GiftTable> giftTableList = gameDataTableService.GiftsTableList();
                GiftTable giftTable = giftTableList.stream().filter(a -> a.getCode().equals(gettingItemCode)).findAny().orElse(null);
                if(giftTable == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: GiftTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: GiftTable not find.", ResponseErrorCode.NOT_FIND_DATA);
                }
                if(myGiftInventory == null) {
                    myGiftInventory = myGiftInventoryRepository.findByUseridUser(userId)
                            .orElse(null);
                    if(myGiftInventory == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyGiftInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: MyGiftInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                List<GiftItemDtosList.GiftItemDto> changedMyGiftInventoryList = receiveItemCommonDto.getChangedMyGiftInventoryList();
                String inventoryInfosString = myGiftInventory.getInventoryInfos();
                GiftItemDtosList giftItemInventorys = JsonStringHerlper.ReadValueFromJson(inventoryInfosString, GiftItemDtosList.class);

                GiftItemDtosList.GiftItemDto inventoryItemDto = giftItemInventorys.giftItemDtoList.stream()
                        .filter(a -> a.code.equals(giftTable.getCode()))
                        .findAny()
                        .orElse(null);
                if(inventoryItemDto == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find GiftTable", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail -> Cause: Can't Find GiftTable", ResponseErrorCode.NOT_FIND_DATA);
                }
                GiftLogDto giftLogDto = new GiftLogDto();
                giftLogDto.setPreviousValue(inventoryItemDto.count);

                inventoryItemDto.AddItem(gettingCount ,giftTable.getStackLimit());
                giftLogDto.setGiftLogDto(logWorkingPosition, inventoryItemDto.code,gettingCount, inventoryItemDto.count);
                String log = JsonStringHerlper.WriteValueAsStringFromData(giftLogDto);
                loggingService.setLogging(userId, 4, log);
                inventoryInfosString = JsonStringHerlper.WriteValueAsStringFromData(giftItemInventorys);
                myGiftInventory.ResetInventoryInfos(inventoryInfosString);

                GiftItemDtosList.GiftItemDto responseItemDto = new GiftItemDtosList.GiftItemDto();
                responseItemDto.code = inventoryItemDto.code;
                responseItemDto.count = gettingCount;
                changedMyGiftInventoryList.add(responseItemDto);
            }
            /*모든 케릭터 조각중 하나*/
            else if(gettingItemCode.equals("characterPieceAll")) {
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
               // List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
                int randIndex = (int) MathHelper.Range(0, copyBelongingCharacterPieceTableList.size());
                BelongingCharacterPieceTable selectedCharacterPiece = copyBelongingCharacterPieceTableList.get(randIndex);
                List<BelongingInventoryDto> changedCharacterPieceList = receiveItemCommonDto.getChangedBelongingInventoryList();
                if(itemTypeList == null)
                    itemTypeList = itemTypeRepository.findAll();
                ItemType belongingCharacterPieceItemType = itemTypeList.stream()
                        .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece)
                        .findAny()
                        .orElse(null);
                if(belongingInventoryList == null)
                    belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                BelongingInventory myCharacterPieceItem = belongingInventoryList.stream()
                        .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece && a.getItemId() == selectedCharacterPiece.getId())
                        .findAny()
                        .orElse(null);

                if(myCharacterPieceItem == null) {
                    belongingInventoryLogDto.setPreviousValue(0);
                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.setUseridUser(userId);
                    belongingInventoryDto.setItemId(selectedCharacterPiece.getId());
                    belongingInventoryDto.setCount(gettingCount);
                    belongingInventoryDto.setItemType(belongingCharacterPieceItemType);
                    myCharacterPieceItem = belongingInventoryDto.ToEntity();
                    myCharacterPieceItem = belongingInventoryRepository.save(myCharacterPieceItem);
                    belongingInventoryLogDto.setBelongingInventoryLogDto(logWorkingPosition, myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
                    String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                    loggingService.setLogging(userId, 3, log);
                    belongingInventoryDto.setId(myCharacterPieceItem.getId());
                    belongingInventoryList.add(myCharacterPieceItem);

                    changedCharacterPieceList.add(belongingInventoryDto);
                }
                else {
                    belongingInventoryLogDto.setPreviousValue(myCharacterPieceItem.getCount());
                    myCharacterPieceItem.AddItem(gettingCount, selectedCharacterPiece.getStackLimit());
                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.setUseridUser(userId);
                    belongingInventoryDto.setItemId(selectedCharacterPiece.getId());
                    belongingInventoryDto.setCount(gettingCount);
                    belongingInventoryDto.setItemType(belongingCharacterPieceItemType);
                    belongingInventoryLogDto.setBelongingInventoryLogDto(logWorkingPosition, myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
                    String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                    loggingService.setLogging(userId, 3, log);
                    belongingInventoryDto.setId(myCharacterPieceItem.getId());
                    changedCharacterPieceList.add(belongingInventoryDto);
                }
            }
            /*특정 케릭터 조각*/
            else if(gettingItemCode.contains("characterPiece")) {
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                List<BelongingInventoryDto> changedCharacterPieceList = receiveItemCommonDto.getChangedBelongingInventoryList();
                String characterCode = gettingItemCode;//.replace("characterPiece_", "");
                if(itemTypeList == null)
                    itemTypeList = itemTypeRepository.findAll();
                ItemType belongingCharacterPieceItemType = itemTypeList.stream()
                        .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece)
                        .findAny()
                        .orElse(null);

                List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
                BelongingCharacterPieceTable characterPiece = belongingCharacterPieceTableList.stream()
                        .filter(a -> a.getCode().equals(characterCode))
                        .findAny()
                        .orElse(null);
                if(characterPiece == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find characterPiece.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't Find characterPiece.", ResponseErrorCode.NOT_FIND_DATA);
                }
                if(belongingInventoryList == null)
                    belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                BelongingInventory myCharacterPieceItem = belongingInventoryList.stream()
                        .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece && a.getItemId() == characterPiece.getId())
                        .findAny()
                        .orElse(null);

                if(myCharacterPieceItem == null) {
                    belongingInventoryLogDto.setPreviousValue(0);
                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.setUseridUser(userId);
                    belongingInventoryDto.setItemId(characterPiece.getId());
                    belongingInventoryDto.setCount(gettingCount);
                    belongingInventoryDto.setItemType(belongingCharacterPieceItemType);
                    myCharacterPieceItem = belongingInventoryDto.ToEntity();
                    myCharacterPieceItem = belongingInventoryRepository.save(myCharacterPieceItem);
                    belongingInventoryLogDto.setBelongingInventoryLogDto(logWorkingPosition, myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
                    belongingInventoryList.add(myCharacterPieceItem);
                    belongingInventoryDto.setId(myCharacterPieceItem.getId());
                    changedCharacterPieceList.add(belongingInventoryDto);
                }
                else {
                    belongingInventoryLogDto.setPreviousValue(myCharacterPieceItem.getCount());
                    myCharacterPieceItem.AddItem(gettingCount, characterPiece.getStackLimit());

                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.setUseridUser(userId);
                    belongingInventoryDto.setItemId(characterPiece.getId());
                    belongingInventoryDto.setCount(gettingCount);
                    belongingInventoryDto.setItemType(belongingCharacterPieceItemType);
                    belongingInventoryLogDto.setBelongingInventoryLogDto(logWorkingPosition, myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
                    belongingInventoryDto.setId(myCharacterPieceItem.getId());
                    changedCharacterPieceList.add(belongingInventoryDto);
                }
            }
            /*특정 케릭터*/
            else if(gettingItemCode.contains("gotcha_")) {
                if(myCharactersList == null)
                    myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
                String heroCode = gettingItemCode.replace("gotcha_","");
                MyCharacters selectedCharacter = myCharactersList.stream().filter(a -> a.getCodeHerostable().equals(heroCode))
                        .findAny()
                        .orElse(null);
                if(selectedCharacter == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myCharactersList not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: myCharactersList not find.", ResponseErrorCode.NOT_FIND_DATA);
                }
                List<herostable> herostables = gameDataTableService.HerosTableList();
                herostable herostable = herostables.stream()
                        .filter(e -> e.getCode().equals(selectedCharacter.getCodeHerostable()))
                        .findAny()
                        .orElse(null);
                if(herostable == null){
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: not find userId.", ResponseErrorCode.NOT_FIND_DATA);
                }

                if(selectedCharacter.isGotcha()) {
                    if(belongingInventoryList == null)
                        belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);

                    List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
                    List<CharacterToPieceTable> characterToPieceTableList = gameDataTableService.CharacterToPieceTableList();

                    String willFindCharacterCode = selectedCharacter.getCodeHerostable();
                    if(herostable.getTier() == 1) {
                        willFindCharacterCode = "characterPiece_cr_common";
                    }
                    String finalWillFindCharacterCode = willFindCharacterCode;
                    BelongingCharacterPieceTable finalCharacterPieceTable = belongingCharacterPieceTableList.stream()
                            .filter(a -> a.getCode().contains(finalWillFindCharacterCode))
                            .findAny()
                            .orElse(null);
                    if(finalCharacterPieceTable == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: characterPieceTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: characterPieceTable not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    CharacterToPieceTable characterToPieceTable = characterToPieceTableList.stream().filter(a -> a.getId() == herostable.getTier()).findAny().orElse(null);
                    if(characterToPieceTable == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: characterToPieceTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: characterToPieceTable not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    if(itemTypeList == null)
                        itemTypeList = itemTypeRepository.findAll();
                    BelongingInventoryDto belongingInventoryDto = AddSelectedCharacterPiece(userId, characterToPieceTable.getPieceCount(), itemTypeList, finalCharacterPieceTable, belongingInventoryList, "마일리지 확정 뽑기");

                    GotchaCharacterResponseDto gotchaCharacterResponseDto = receiveItemCommonDto.getGotchaCharacterResponseDto();
                    StringMaker.Clear();
                    StringMaker.stringBuilder.append("reward_");
                    StringMaker.stringBuilder.append(selectedCharacter.getCodeHerostable());
                    String rewardCharacterCode = StringMaker.stringBuilder.toString();
                    GotchaCharacterResponseDto.GotchaCharacterInfo gotchaCharacterInfo = new GotchaCharacterResponseDto.GotchaCharacterInfo(rewardCharacterCode, belongingInventoryDto, null);
                    gotchaCharacterResponseDto.getCharacterInfoList().add(gotchaCharacterInfo);

                }
                else {
                    selectedCharacter.Gotcha();
                    GotchaCharacterResponseDto gotchaCharacterResponseDto = receiveItemCommonDto.getGotchaCharacterResponseDto();
                    GotchaCharacterResponseDto.GotchaCharacterInfo gotchaCharacterInfo = new GotchaCharacterResponseDto.GotchaCharacterInfo(selectedCharacter.getCodeHerostable(), null, selectedCharacter);
                    gotchaCharacterResponseDto.getCharacterInfoList().add(gotchaCharacterInfo);
                }
            }
            /*모든 장비 중 하나*/
            else if(gettingItemCode.equals("equipmentAll")){
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                //장비
                if(heroEquipmentInventoryList == null)
                    heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());

                if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot())
                    throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
                //등급 확률	영웅 70%	 전설 20%	신성 9%	고대 1%
                //품질	D	C	B	A	S	SS	SSS
                //확률	15%	25%	45%	9%	5%	1%	0%
                List<Double> gradeProbabilityList = new ArrayList<>();
                gradeProbabilityList.add(70D);
                gradeProbabilityList.add(20D);
                gradeProbabilityList.add(9D);
                gradeProbabilityList.add(1D);
                String selectedGrade = "";
                int selectedIndex = MathHelper.RandomIndexWidthProbability(gradeProbabilityList);
                switch (selectedIndex) {
                    case 0:
                        selectedGrade = "Hero";
                        break;
                    case 1:
                        selectedGrade = "Legend";
                        break;
                    case 2:
                        selectedGrade = "Divine";
                        break;
                    case 3:
                        selectedGrade = "Ancient";
                        break;
                }

                List<Double> classProbabilityList = new ArrayList<>();
                classProbabilityList.add(15D);
                classProbabilityList.add(25D);
                classProbabilityList.add(45D);
                classProbabilityList.add(9D);
                classProbabilityList.add(5D);
                classProbabilityList.add(1D);
                classProbabilityList.add(0D);
                String selectedClass = "";
                selectedIndex = MathHelper.RandomIndexWidthProbability(classProbabilityList);
                switch (selectedIndex) {
                    case 0:
                        selectedClass = "D";
                        break;
                    case 1:
                        selectedClass = "C";
                        break;
                    case 2:
                        selectedClass = "B";
                        break;
                    case 3:
                        selectedClass = "A";
                        break;
                    case 4:
                        selectedClass = "S";
                        break;
                    case 5:
                        selectedClass = "SS";
                        break;
                    case 6:
                        selectedClass = "SSS";
                        break;
                }
                List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
                HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
                int classValue = (int) EquipmentCalculate.GetClassValueFromClassCategory(selectedClass, classValues);
                List<HeroEquipmentsTable> probabilityList = EquipmentCalculate.GetProbabilityList(heroEquipmentsTableList, EquipmentItemCategory.ALL, selectedGrade);
                int randValue = (int)MathHelper.Range(0, probabilityList.size());
                HeroEquipmentsTable selectEquipment = probabilityList.get(randValue);
                List<EquipmentOptionsInfoTable> optionsInfoTableList = gameDataTableService.OptionsInfoTableList();
                HeroEquipmentInventory generatedItem = EquipmentCalculate.CreateEquipment(user.getId(), selectEquipment, selectedClass, classValue, optionsInfoTableList);
                generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
                HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
                heroEquipmentInventoryDto.InitFromDbData(generatedItem);
                List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = receiveItemCommonDto.getChangedHeroEquipmentInventoryList();
                changedHeroEquipmentInventoryList.add(heroEquipmentInventoryDto);
                EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                equipmentLogDto.setEquipmentLogDto(logWorkingPosition, generatedItem.getId(), "추가", heroEquipmentInventoryDto);
                String log = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                loggingService.setLogging(userId, 2, log);

                /* 업적 : 장비 획득 (특정 품질) 미션 체크*/
                changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(), generatedItem.getItemClass(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
                changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), selectedGrade, gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

            }
            /*특정 등급, 특정 클래스, 특정 종류의 장비중 하나*/
            else if(gettingItemCode.contains("equipment")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                if(heroEquipmentInventoryList == null)
                    heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());

                if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
                }

                List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
                HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
                List<EquipmentOptionsInfoTable> optionsInfoTableList = gameDataTableService.OptionsInfoTableList();
                HeroEquipmentInventoryDto heroEquipmentInventoryDto = ApplySomeReward.AddEquipmentItem(user, gettingItemCode, heroEquipmentInventoryList, heroEquipmentInventoryRepository, heroEquipmentsTableList, classValues, optionsInfoTableList, logWorkingPosition, loggingService, errorLoggingService);
                List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = receiveItemCommonDto.getChangedHeroEquipmentInventoryList();
                changedHeroEquipmentInventoryList.add(heroEquipmentInventoryDto);

                HeroEquipmentsTable selectedEquipmentItemTable = heroEquipmentsTableList.stream().filter(a -> a.getId() == heroEquipmentInventoryDto.getItem_Id()).findAny().orElse(null);

                /* 업적 : 장비 획득 (특정 품질) 미션 체크*/
                changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(), heroEquipmentInventoryDto.getItemClass(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
                changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), selectedEquipmentItemTable.getGrade(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

            }
            /*인장 중 하나*/
            else if(gettingItemCode.equals("stampAll")){
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }

                if(heroEquipmentInventoryList == null)
                    heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());

                if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
                }

                List<PassiveItemTable> passiveItemTables = gameDataTableService.PassiveItemTableList();
                List<PassiveItemTable> copyPassiveItemTables = new ArrayList<>();
                copyPassiveItemTables.addAll(passiveItemTables);
                List<PassiveItemTable> deleteList = new ArrayList<>();
                boolean deleted = false;
                for(PassiveItemTable passiveItemTable : copyPassiveItemTables){
                    String code = passiveItemTable.getCode();
                    if(code.equals("passiveItem_00_10")) {
                        deleteList.add(passiveItemTable);
                        deleted = true;
                        break;
                    }
                }
                if(deleted)
                    copyPassiveItemTables.removeAll(deleteList);

                int selectedIndex  = (int)MathHelper.Range(0, copyPassiveItemTables.size());
                PassiveItemTable selectedPassiveItem = copyPassiveItemTables.get(selectedIndex);
                List<Double> classProbabilityList = new ArrayList<>();
                classProbabilityList.add(15D);
                classProbabilityList.add(25D);
                classProbabilityList.add(45D);
                classProbabilityList.add(9D);
                classProbabilityList.add(5D);
                classProbabilityList.add(1D);
                classProbabilityList.add(0D);
                String selectedClass = "";
                selectedIndex = MathHelper.RandomIndexWidthProbability(classProbabilityList);
                switch (selectedIndex) {
                    case 0:
                        selectedClass = "D";
                        break;
                    case 1:
                        selectedClass = "C";
                        break;
                    case 2:
                        selectedClass = "B";
                        break;
                    case 3:
                        selectedClass = "A";
                        break;
                    case 4:
                        selectedClass = "S";
                        break;
                    case 5:
                        selectedClass = "SS";
                        break;
                    case 6:
                        selectedClass = "SSS";
                        break;
                }
                HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
                int classValue = (int) EquipmentCalculate.GetClassValueFromClassCategory(selectedClass, classValues);

                HeroEquipmentInventoryDto dto = new HeroEquipmentInventoryDto();
                dto.setUseridUser(userId);
                dto.setItem_Id(selectedPassiveItem.getId());
                dto.setItemClassValue(classValue);
                dto.setDecideDefaultAbilityValue(0);
                dto.setDecideSecondAbilityValue(0);
                dto.setLevel(1);
                dto.setMaxLevel(1);
                dto.setExp(0);
                dto.setNextExp(0);
                dto.setItemClass(selectedClass);

                HeroEquipmentInventory generatedItem = dto.ToEntity();
                generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
                HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
                heroEquipmentInventoryDto.InitFromDbData(generatedItem);
                EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                equipmentLogDto.setEquipmentLogDto(logWorkingPosition, generatedItem.getId(), "추가", heroEquipmentInventoryDto);
                String log = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                loggingService.setLogging(userId, 2, log);

                List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = receiveItemCommonDto.getChangedHeroEquipmentInventoryList();
                changedHeroEquipmentInventoryList.add(heroEquipmentInventoryDto);
            }
            /*특정 품질의 인장*/
            else if(gettingItemCode.contains("stamp")){

                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }

                if(heroEquipmentInventoryList == null)
                    heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());

                if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
                }

                String itemClass = "D";
                if(gettingItemCode.contains("ClassD")) {
                    itemClass = "D";
                }
                else if(gettingItemCode.contains("ClassC")){
                    itemClass = "C";
                }
                else if(gettingItemCode.contains("ClassB")){
                    itemClass = "B";
                }
                else if(gettingItemCode.contains("ClassA")){
                    itemClass = "A";
                }
                else if(gettingItemCode.contains("ClassSSS")){
                    itemClass = "SSS";
                }
                else if(gettingItemCode.contains("ClassSS")){
                    itemClass = "SS";
                }
                else if(gettingItemCode.contains("ClassS")){
                    itemClass = "S";
                }

                List<PassiveItemTable> passiveItemTables = gameDataTableService.PassiveItemTableList();
                List<PassiveItemTable> copyPassiveItemTables = new ArrayList<>();
                copyPassiveItemTables.addAll(passiveItemTables);
                List<PassiveItemTable> deleteList = new ArrayList<>();
                boolean deleted = false;
                for(PassiveItemTable passiveItemTable : copyPassiveItemTables){
                    String code = passiveItemTable.getCode();
                    if(code.equals("passiveItem_00_10")) {
                        deleteList.add(passiveItemTable);
                        deleted = true;
                        break;
                    }
                }
                if(deleted)
                    copyPassiveItemTables.removeAll(deleteList);

                int selectedIndex  = (int)MathHelper.Range(0, copyPassiveItemTables.size());
                PassiveItemTable selectedPassiveItem = copyPassiveItemTables.get(selectedIndex);

                HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
                int classValue = (int) EquipmentCalculate.GetClassValueFromClassCategory(itemClass, classValues);

                HeroEquipmentInventoryDto dto = new HeroEquipmentInventoryDto();
                dto.setUseridUser(userId);
                dto.setItem_Id(selectedPassiveItem.getId());
                dto.setItemClassValue(classValue);
                dto.setDecideDefaultAbilityValue(0);
                dto.setDecideSecondAbilityValue(0);
                dto.setLevel(1);
                dto.setMaxLevel(1);
                dto.setExp(0);
                dto.setNextExp(0);
                dto.setItemClass(itemClass);

                HeroEquipmentInventory generatedItem = dto.ToEntity();
                generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
                HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
                heroEquipmentInventoryDto.InitFromDbData(generatedItem);
                EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                equipmentLogDto.setEquipmentLogDto(logWorkingPosition, generatedItem.getId(), "추가", heroEquipmentInventoryDto);
                String log = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                loggingService.setLogging(userId, 2, log);

                List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = receiveItemCommonDto.getChangedHeroEquipmentInventoryList();
                changedHeroEquipmentInventoryList.add(heroEquipmentInventoryDto);
            }
            /*특정 인장 중 하나*/
            else if(gettingItemCode.contains("passiveItem")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }

                if(heroEquipmentInventoryList == null)
                    heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());

                if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
                }

                List<PassiveItemTable> passiveItemTables = gameDataTableService.PassiveItemTableList();

                int selectedIndex  = (int)MathHelper.Range(0, passiveItemTables.size());
                PassiveItemTable selectedPassiveItem = passiveItemTables.stream().filter(a -> a.getCode().equals(gettingItemCode)).findAny().orElse(null);
                if(selectedPassiveItem == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find PassiveItemTable", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail -> Cause: Can't Find PassiveItemTable", ResponseErrorCode.NOT_FIND_DATA);
                }
                List<Double> classProbabilityList = new ArrayList<>();
                classProbabilityList.add(15D);
                classProbabilityList.add(25D);
                classProbabilityList.add(45D);
                classProbabilityList.add(9D);
                classProbabilityList.add(5D);
                classProbabilityList.add(1D);
                classProbabilityList.add(0D);
                String selectedClass = "";
                selectedIndex = MathHelper.RandomIndexWidthProbability(classProbabilityList);
                switch (selectedIndex) {
                    case 0:
                        selectedClass = "D";
                        break;
                    case 1:
                        selectedClass = "C";
                        break;
                    case 2:
                        selectedClass = "B";
                        break;
                    case 3:
                        selectedClass = "A";
                        break;
                    case 4:
                        selectedClass = "S";
                        break;
                    case 5:
                        selectedClass = "SS";
                        break;
                    case 6:
                        selectedClass = "SSS";
                        break;
                }
                HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
                int classValue = (int) EquipmentCalculate.GetClassValueFromClassCategory(selectedClass, classValues);

                HeroEquipmentInventoryDto dto = new HeroEquipmentInventoryDto();
                dto.setUseridUser(userId);
                dto.setItem_Id(selectedPassiveItem.getId());
                dto.setItemClassValue(classValue);
                dto.setDecideDefaultAbilityValue(0);
                dto.setDecideSecondAbilityValue(0);
                dto.setLevel(1);
                dto.setMaxLevel(1);
                dto.setExp(0);
                dto.setNextExp(0);
                dto.setItemClass(selectedClass);

                HeroEquipmentInventory generatedItem = dto.ToEntity();
                generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
                HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
                heroEquipmentInventoryDto.InitFromDbData(generatedItem);
                EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                equipmentLogDto.setEquipmentLogDto(logWorkingPosition, generatedItem.getId(), "추가", heroEquipmentInventoryDto);
                String log = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                loggingService.setLogging(userId, 2, log);

                List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = receiveItemCommonDto.getChangedHeroEquipmentInventoryList();
                changedHeroEquipmentInventoryList.add(heroEquipmentInventoryDto);
            }
        }
        return changedMissionsData;
    }
    BelongingInventoryDto AddSelectedCharacterPiece(Long userId, int gettingCount, List<ItemType> itemTypeList, BelongingCharacterPieceTable characterPieceTable, List<BelongingInventory> belongingInventoryList, String workingPosition) {
        ItemType belongingCharacterPieceItem = itemTypeList.stream()
                .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece)
                .findAny()
                .orElse(null);

        BelongingCharacterPieceTable finalCharacterPieceTable = characterPieceTable;
        BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
        BelongingInventory myCharacterPieceItem = belongingInventoryList.stream()
                .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece && a.getItemId() == finalCharacterPieceTable.getId())
                .findAny()
                .orElse(null);

        if(myCharacterPieceItem == null) {
            BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
            belongingInventoryLogDto.setPreviousValue(0);
            belongingInventoryDto.setUseridUser(userId);
            belongingInventoryDto.setItemId(characterPieceTable.getId());
            belongingInventoryDto.setCount(gettingCount);
            belongingInventoryDto.setItemType(belongingCharacterPieceItem);
            myCharacterPieceItem = belongingInventoryDto.ToEntity();
            myCharacterPieceItem = belongingInventoryRepository.save(myCharacterPieceItem);
            belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
            String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, belongingLog);
            belongingInventoryDto.setId(myCharacterPieceItem.getId());
            belongingInventoryList.add(myCharacterPieceItem);
        }
        else {
            BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
            belongingInventoryLogDto.setPreviousValue(myCharacterPieceItem.getCount());
            myCharacterPieceItem.AddItem(gettingCount, characterPieceTable.getStackLimit());
            belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
            String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, belongingLog);
            belongingInventoryDto.setId(myCharacterPieceItem.getId());
            belongingInventoryDto.setUseridUser(userId);
            belongingInventoryDto.setItemId(characterPieceTable.getId());
            belongingInventoryDto.setCount(gettingCount);
            belongingInventoryDto.setItemType(belongingCharacterPieceItem);
        }
        return belongingInventoryDto;
    }
}

