package com.onlyonegames.eternalfantasia.domain.service.Event;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Event.*;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Event.CommonEventScheduler;
import com.onlyonegames.eternalfantasia.domain.model.entity.Event.MyAchieveEventMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Event.MyMonsterKillEventMissionData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyFieldSaveData;
import com.onlyonegames.eternalfantasia.domain.repository.Event.CommonEventSchedulerRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Event.MyAchieveEventMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Event.MyMonsterKillEventMissionDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyFieldSaveDataRepository;
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
import java.util.stream.Collectors;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class EventService {
    private final GameDataTableService gameDataTableService;
    private final MyAchieveEventMissionsDataRepository myAchieveEventMissionsDataRepository;
    private final MyFieldSaveDataRepository myFieldSaveDataRepository;
    private final CommonEventSchedulerRepository commonEventSchedulerRepository;
    private final MyMonsterKillEventMissionDataRepository myMonsterKillEventMissionDataRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> GetEventListInfo(Long userId, Map<String, Object> map) {
        LocalDateTime now = LocalDateTime.now();
        List<Integer> activeEventList = new ArrayList<>();
        EventResponseDto eventResponseDto = new EventResponseDto();
        //공용 스케쥴러 체크
        //TODO FieldExchangeItemEventScheduler 이름 변경 필요 => EventScheduler
        List<CommonEventScheduler> commonEventSchedulerList = commonEventSchedulerRepository.findByStartTimeBeforeAndEndTimeAfter(now, now);
        if(commonEventSchedulerList.size()>2) { //TODO 3을 GameDataTable에 EventContentTable을 추가하여 size를 불러오도록 변경 필요
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Wrong EventScheduler find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Wrong EventScheduler find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        //필드 교환 이벤트 체크
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
        EventResponseDto.FieldExchangeItemDto fieldExchangeItemDto = new EventResponseDto.FieldExchangeItemDto();
        if(commonEventScheduler !=null){
            activeEventList.add(commonEventScheduler.getEventContentsTable().getId());
            MyFieldSaveData myFieldSaveData = myFieldSaveDataRepository.findByUseridUser(userId).orElse(null);
            if(myFieldSaveData == null){
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyFieldSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: MyFieldSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            String json_fieldExchangeItemSaveDataValue = myFieldSaveData.getJson_fieldExchangeItemSaveDataValue();
            MyFieldExchangeItemEventDto myFieldExchangeItemEventDto = JsonStringHerlper.ReadValueFromJson(json_fieldExchangeItemSaveDataValue, MyFieldExchangeItemEventDto.class);
            fieldExchangeItemDto.SetFieldExchangeItemEventDto(myFieldExchangeItemEventDto.maxExchangeList, commonEventScheduler.getEndTime());
        }

        // 누적 달성 이벤트 체크
        MyAchieveEventMissionsData myAchieveEventMissionsData = myAchieveEventMissionsDataRepository.findByUseridUser(userId).orElse(null);
        if(myAchieveEventMissionsData == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyAchieveEventMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyAchieveEventMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String json_AchieveEventMissionsData = myAchieveEventMissionsData.getJson_saveDataValue();
        AchieveEventMissionDataDto achieveEventMissionDataDto = JsonStringHerlper.ReadValueFromJson(json_AchieveEventMissionsData, AchieveEventMissionDataDto.class);
        if(achieveEventMissionDataDto.CheckPossibleMission())
            activeEventList.add(2);
        else
            achieveEventMissionDataDto = null;

        // 레타의 사냥 주문 체크
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
        EventResponseDto.MonsterKillEventDataDto monsterKillEventDataDto = new EventResponseDto.MonsterKillEventDataDto();
        if(monsterKillEventScheduler != null){
            MyMonsterKillEventMissionData myMonsterKillEventMissionData = myMonsterKillEventMissionDataRepository.findByUseridUser(userId).orElse(null);
            if (myMonsterKillEventMissionData == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMonsterKillEventMissionData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: MyMonsterKillEventMissionData not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            activeEventList.add(3);
            String json_MonsterKillEventMissionData = myMonsterKillEventMissionData.getJson_saveDataValue();
            MonsterKillEventMissionDataDto monsterKillEventMissionDataDto = JsonStringHerlper.ReadValueFromJson(json_MonsterKillEventMissionData, MonsterKillEventMissionDataDto.class);
            List<MissionsDataDto.MissionData> clientData = monsterKillEventMissionDataDto.ImportQuestMissionSendToClient(gameDataTableService.MonsterKillEventTableList());
            monsterKillEventDataDto.SetMonsterKillEventDataDto(clientData, monsterKillEventScheduler.getEndTime());
        }
        EventResponseDto.ActiveEventListDto activeEventListDto = new EventResponseDto.ActiveEventListDto();
        activeEventListDto.SetActiveEventList(activeEventList);
        eventResponseDto.SetEventResponseDto(activeEventListDto, fieldExchangeItemDto, achieveEventMissionDataDto, monsterKillEventDataDto);
        map.put("EventList", eventResponseDto);
        return map;
    }
}
