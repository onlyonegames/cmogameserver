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

    public void SetBaseDayTime(LocalDateTime localDateTime) {
        baseDayTime = localDateTime;
    }

    public void SetBaseWeekTimeNow() {
        baseWeekTime = LocalDateTime.now();
    }

    public void SetBaseWeekTime(LocalDateTime localDateTime) {
        baseWeekTime = localDateTime;
    }

    public void SetBaseMonthTimeNow() {
        baseMonthTime = LocalDateTime.now();
    }

    public void SetBaseMonthTime(LocalDateTime localDateTime) {
        baseMonthTime = localDateTime;
    }

    public void SetChallengeTowerClassIndex(int challengeTowerClassIndex) {
        this.challengeTowerClassIndex = challengeTowerClassIndex;
    }
}
