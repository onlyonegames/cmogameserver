package com.onlyonegames.eternalfantasia.domain.model.entity;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.ItemType;
import lombok.*;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MyAttendanceData extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    int gettingCount;
    boolean receivableReward;
    LocalDateTime lastAttendanceDate;

    @Builder
    public MyAttendanceData(Long useridUser, int gettingCount, boolean receivableReward, LocalDateTime lastAttendanceDate) {
        this.useridUser = useridUser;
        this.gettingCount = gettingCount;
        this.receivableReward = receivableReward;
        this.lastAttendanceDate = lastAttendanceDate;
    }

    public boolean CheckAttendanceTime() {
        if(receivableReward)
            return false;
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(lastAttendanceDate, now);
        if(duration.toDays()>=1) {
            lastAttendanceDate = LocalDateTime.of(now.minusHours(5).toLocalDate(), LocalTime.of(5, 0, 0));
            PlusCount();
            receivableReward = true;
            return true;
        }
        return false;
    }

    void PlusCount() {
        if(this.gettingCount == 28)
            this.gettingCount = 1;
        else
            this.gettingCount += 1;
    }

    public void RewardReceive() {
        this.receivableReward = false;
        this.lastAttendanceDate = LocalDateTime.of(LocalDateTime.now().minusHours(5).toLocalDate(), LocalTime.of(5, 0, 0));
    }

    //운영툴용 함수
    public void SetReward() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(2);
        this.lastAttendanceDate = LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.of(5, 0, 0));
    }
}
