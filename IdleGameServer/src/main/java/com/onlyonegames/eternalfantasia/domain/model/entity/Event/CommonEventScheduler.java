package com.onlyonegames.eternalfantasia.domain.model.entity.Event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.EventContentsTable;
import lombok.Getter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "CommonEventScheduler")
public class CommonEventScheduler extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    String title;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime endTime;
    String json_FieldExchangeItemEvent;
    @ManyToOne
    @JoinColumn(name = "eventContentsTable_id")
    EventContentsTable eventContentsTable;

    @Builder
    public CommonEventScheduler(String title, LocalDateTime startTime, LocalDateTime endTime, String json_FieldExchangeItemEvent, EventContentsTable eventContentsTable) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.json_FieldExchangeItemEvent = json_FieldExchangeItemEvent;
        this.eventContentsTable = eventContentsTable;
    }
}
