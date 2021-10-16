package com.onlyonegames.eternalfantasia.domain.model.entity;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
public class StandardTime extends BaseTimeEntity {
    @Id
    //@TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    public LocalDateTime baseDayTime;
    public LocalDateTime baseWeekTime;
    public LocalDateTime baseMonthTime;
    int challengeTowerClassIndex;

    public void SetBaseDayTimeNow() {
        baseDayTime = LocalDateTime.now();
    }

    public void SetBaseDayTime() {
        baseDayTime = baseDayTime.plusDays(1);
    }

    public void SetBaseWeekTimeNow() {
        baseWeekTime = LocalDateTime.now();
    }

    public void SetBaseWeekTime() {
        baseWeekTime = baseWeekTime.plusWeeks(1);
    }

    public void SetBaseMonthTimeNow() {
        baseMonthTime = LocalDateTime.now();
    }

    public void SetBaseMonthTime() {
        baseMonthTime = baseMonthTime.plusMonths(1);
    }

    public void SetChallengeTowerClassIndex(int challengeTowerClassIndex) {
        this.challengeTowerClassIndex = challengeTowerClassIndex;
    }
}
