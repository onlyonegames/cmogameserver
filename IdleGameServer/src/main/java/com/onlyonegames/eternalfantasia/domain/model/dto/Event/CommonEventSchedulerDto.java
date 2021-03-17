package com.onlyonegames.eternalfantasia.domain.model.dto.Event;

import com.onlyonegames.eternalfantasia.domain.model.entity.Event.CommonEventScheduler;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.EventContentsTable;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommonEventSchedulerDto {
    Long id;
    String title;
    LocalDateTime startTime;
    LocalDateTime endTime;
    String json_FieldExchangeItemEvent;
    EventContentsTable eventContentsTable;

    public CommonEventScheduler ToEntity() {
        return CommonEventScheduler.builder().title(title).startTime(startTime).endTime(endTime).json_FieldExchangeItemEvent(json_FieldExchangeItemEvent).eventContentsTable(eventContentsTable).build();
    }

    public void InitFormDbData(CommonEventScheduler dbData) {
        this.id = dbData.getId();
        this.title = dbData.getTitle();
        this.startTime = dbData.getStartTime();
        this.endTime = dbData.getEndTime();
        this.json_FieldExchangeItemEvent = dbData.getJson_FieldExchangeItemEvent();
        this.eventContentsTable = dbData.getEventContentsTable();
    }

}
