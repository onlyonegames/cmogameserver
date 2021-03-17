package com.onlyonegames.eternalfantasia.domain.model.entity.Shop;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import com.onlyonegames.eternalfantasia.etc.MyDateTimeHelper;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@Builder
public class MyShopInfo extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String json_myShopInfos;
    /*상점 스케쥴 시간. 24시간마다 갱신*/
    LocalDateTime scheduleStartTime;
    /*일패키지 스케쥴 갱신*/
    LocalDateTime dailyPackageScheduleStartTime;
    /*주간패키지 스케쥴 갱신*/
    LocalDateTime weeklyPackageScheduleStartTime;
    /*월간패키지 스케쥴 갱신*/
    LocalDateTime monthlyPackageScheduleStartTime;

    //개인화 상점 갱신체크
    public boolean IsMyShopRecycleTime(int recycleSecond) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(scheduleStartTime, now);
        if(duration.getSeconds() >= recycleSecond) {
            scheduleStartTime = now;
            scheduleStartTime = LocalDateTime.of(scheduleStartTime.minusHours(5).toLocalDate(), LocalTime.of(5,0,0));
            return true;
        }
        return false;
    }
    //일 패키지 스케쥴 갱신체크
    public boolean IsDailyPackageRecycleTime() {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dailyPackageScheduleStartTime, now);
        if(duration.getSeconds() >= MyDateTimeHelper.SECOENDOFDAY) {
            dailyPackageScheduleStartTime = now;
            dailyPackageScheduleStartTime = LocalDateTime.of(dailyPackageScheduleStartTime.minusHours(5).toLocalDate(), LocalTime.of(5,0,0));
            return true;
        }
        return false;
    }
    //주간 패키지 스케쥴 갱신체크
    public boolean IsWeeklyPackageRecycleTime() {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(weeklyPackageScheduleStartTime, now);
        if(duration.getSeconds() >= MyDateTimeHelper.SECOENDOFDAY * 7) {
            weeklyPackageScheduleStartTime = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            weeklyPackageScheduleStartTime = LocalDateTime.of(weeklyPackageScheduleStartTime.toLocalDate(), LocalTime.of(5,0,0));
            return true;
        }
        return false;
    }
    //월간 패키지 스케쥴 갱신체크
    public boolean IsMonthlyPackageRecycleTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastDayOfMonth = monthlyPackageScheduleStartTime.with(TemporalAdjusters.lastDayOfMonth());
        if(now.isAfter(lastDayOfMonth)) {
            monthlyPackageScheduleStartTime = now.with(TemporalAdjusters.firstDayOfMonth());
            monthlyPackageScheduleStartTime = LocalDateTime.of(monthlyPackageScheduleStartTime.toLocalDate(), LocalTime.of(5,0,0));
            return true;
        }
        return false;
    }

    public void ResetMyShopInfos(String json_myShopInfos) {
        this.json_myShopInfos = json_myShopInfos;
    }
}
