package com.onlyonegames.eternalfantasia.domain.model.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class StandardTime {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    int id;
    LocalDateTime baseDayTime;
    LocalDateTime baseWeekTime;
    LocalDateTime baseMonthTime;
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
