package com.onlyonegames.eternalfantasia.domain.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "MyTodayViewingTable")
@Builder
public class MyTodayViewingTable {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    int todayViewingCount;
    LocalDateTime lastViewing;

    public boolean IsResetTime() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        Duration duration = Duration.between(lastViewing, now);
        if(duration.toDays() >= 1) {
            lastViewing = LocalDateTime.of(now.minusHours(5).toLocalDate(), LocalTime.of(5,0,0));
            return true;
        }
        return false;
    }

    public void AddViewCount(){
        this.todayViewingCount += 1;
    }

    public void ResetViewingCount(){
        this.todayViewingCount = 0;
    }
}