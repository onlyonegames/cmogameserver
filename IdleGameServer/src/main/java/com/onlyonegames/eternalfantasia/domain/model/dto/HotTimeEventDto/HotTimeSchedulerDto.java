package com.onlyonegames.eternalfantasia.domain.model.dto.HotTimeEventDto;

import com.onlyonegames.eternalfantasia.domain.model.entity.HotTimeEvent.HotTimeScheduler;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HotTimeSchedulerDto {
    Long id;
    String title;
    LocalDateTime startTime;
    LocalDateTime endTime;
    int kind;
    String json_HotTimeEvent;

    public HotTimeScheduler ToEntity() {
        return HotTimeScheduler.builder().title(title).startTime(startTime).endTime(endTime).kind(kind).json_HotTimeEvent(json_HotTimeEvent).build();
    }

    public void InitFormDbData(HotTimeScheduler dbData) {
        id = dbData.getId();
        title = dbData.getTitle();
        startTime = dbData.getStartTime();
        endTime = dbData.getEndTime();
        kind = dbData.getKind();
        json_HotTimeEvent = dbData.getJson_HotTimeEvent();
    }
}
