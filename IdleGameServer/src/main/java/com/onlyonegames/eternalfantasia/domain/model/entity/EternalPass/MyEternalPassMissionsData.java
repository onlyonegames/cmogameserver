package com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class MyEternalPassMissionsData extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String json_saveDataValue;/*업적 상황 저장 데이터*/
    LocalDateTime lastDailyMissionClearTime;/*일일 업적 클리어 시간*/
    LocalDateTime lastWeeklyMissionClearTime;/*주간 업적 클리어 시간*/

    public boolean IsResetDailyMissionClearTime() {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(lastDailyMissionClearTime, now);
        if(duration.toDays() >= 1) {
            lastDailyMissionClearTime = LocalDateTime.of(now.minusHours(5).toLocalDate(), LocalTime.of(5,0,0));
            return true;
        }
        return false;
    }

    public boolean IsResetWeeklyMissionClearTime() {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(lastWeeklyMissionClearTime, now);
        if(duration.toDays() >= 7) {
            lastWeeklyMissionClearTime = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            lastWeeklyMissionClearTime = LocalDateTime.of(lastWeeklyMissionClearTime.toLocalDate(), LocalTime.of(5,0,0));
            return true;
        }
        return false;
    }

    public void ResetSaveDataValue(String json_saveDataValue) { this.json_saveDataValue = json_saveDataValue;}
}
