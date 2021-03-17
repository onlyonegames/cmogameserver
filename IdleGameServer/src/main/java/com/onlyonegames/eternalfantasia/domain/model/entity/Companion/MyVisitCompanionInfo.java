package com.onlyonegames.eternalfantasia.domain.model.entity.Companion;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity

public class MyVisitCompanionInfo extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String visitCompanionInfo;
    /*이번 방문 스케쥴 시작한 시간. 최초 유저 등록 시작일 + 5일 주기로 계속 업데이트*/
    LocalDateTime visitScheduleStartTime;

    @Builder
    public MyVisitCompanionInfo(Long useridUser, String visitCompanionInfo) {
        this.useridUser = useridUser;
        this.visitCompanionInfo = visitCompanionInfo;
        this.visitScheduleStartTime = LocalDateTime.now();
        Init();
    }

    void Init() {
        visitScheduleStartTime = LocalDateTime.now().minusDays(10);
    }

    public boolean IsRecycleTime(int recycleSecond) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(visitScheduleStartTime, now);
        if(duration.getSeconds() >= recycleSecond) {
            visitScheduleStartTime = now;
            visitScheduleStartTime.plusSeconds(recycleSecond);
            return true;
        }
        return false;
    }

    public void ResetVisitCompanionInfo(String visitCompanionInfo) {
        this.visitCompanionInfo = visitCompanionInfo;
    }
}
