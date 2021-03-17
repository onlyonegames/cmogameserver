package com.onlyonegames.eternalfantasia.domain.model.dto.Event;

import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventResponseDto {
    public ActiveEventListDto activeEventList;
    public FieldExchangeItemDto fieldExchangeItemEvent;
    public AchieveEventMissionDataDto achieveEventMissionDataDto;
    public MonsterKillEventDataDto monsterKillEventDataDto;

    public void SetEventResponseDto(ActiveEventListDto activeEventList, FieldExchangeItemDto fieldExchangeItemEvent, AchieveEventMissionDataDto achieveEventMissionDataDto, MonsterKillEventDataDto monsterKillEventDataDto) {
        this.activeEventList = activeEventList;
        this.fieldExchangeItemEvent = fieldExchangeItemEvent;
        this.achieveEventMissionDataDto = achieveEventMissionDataDto;
        this.monsterKillEventDataDto = monsterKillEventDataDto;
    }

    public static class ActiveEventListDto {
        public List<Integer> activeEvent;

        public void SetActiveEventList(List<Integer> activeEvent){
            this.activeEvent = activeEvent;
        }
    }

    public static class FieldExchangeItemDto {
        public List<MyFieldExchangeItemEventDto.MaxExchange> maxExchangeList;
        public LocalDateTime endTime;

        public void SetFieldExchangeItemEventDto(List<MyFieldExchangeItemEventDto.MaxExchange> maxExchangeList, LocalDateTime endTime){
            this.maxExchangeList = maxExchangeList;
            this.endTime = endTime;
        }
    }

    public static class MonsterKillEventDataDto {
        public List<MissionsDataDto.MissionData> monsterKillMissionList;
        public LocalDateTime endTime;

        public void SetMonsterKillEventDataDto(List<MissionsDataDto.MissionData> monsterKillMissionList, LocalDateTime endTime){
            this.monsterKillMissionList = monsterKillMissionList;
            this.endTime = endTime;
        }
    }
}
