package com.onlyonegames.eternalfantasia.domain.model.entity.HotTimeEvent;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "hotTimeScheduler")
public class HotTimeScheduler extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    int kind;
    String title;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime endTime;
    String json_HotTimeEvent;

    @Builder
    public HotTimeScheduler(int kind, String title, LocalDateTime startTime, LocalDateTime endTime, String json_HotTimeEvent) {
        this.kind = kind;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.json_HotTimeEvent = json_HotTimeEvent;
    }
}
