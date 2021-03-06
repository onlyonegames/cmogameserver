package com.onlyonegames.eternalfantasia.domain.model.entity;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class StagePlayData {
    @Id
    @TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    LocalDateTime battleStartTime;
    LocalDateTime battleEndTime;

    @Builder
    public StagePlayData(Long useridUser) {
        this.useridUser = useridUser;
        Init();
    }

    void Init() {
        battleStartTime = LocalDateTime.now();
        battleEndTime = LocalDateTime.now();
    }
    /*스테이지 시작(전투시작)시 battleStartTime 설정*/
    public void StartStagePlay() {
        battleStartTime = LocalDateTime.now();
    }
    /*스테이지 클리어시 battleEndTime 설정*/
    public void ClearStagePlay() {
        battleEndTime = LocalDateTime.now();
    }
    /*스테이지 패배시 battleEndTime 설정*/
    public void DefeatStagePlay() {
        battleEndTime = LocalDateTime.now();
    }
}
