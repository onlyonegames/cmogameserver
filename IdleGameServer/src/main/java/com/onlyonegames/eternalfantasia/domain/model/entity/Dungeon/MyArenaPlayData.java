package com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MyArenaPlayData {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    Long matchedUser;
    String matchingRdsScoreIds;
    //아레나를 플레이하면 다음 매칭 상대를 다시 셋팅할수 있음.
    boolean resetAbleMatchingRdsScoreIds;
    int enemyTeamBattlePower;
    int myTeamBattlePower;
    LocalDateTime battleStartTime;
    LocalDateTime battleEndTime;

    @Builder
    public MyArenaPlayData(Long useridUser) {
        this.useridUser = useridUser;
        Init();
    }

    void Init() {
        battleStartTime = LocalDateTime.now();
        battleEndTime = LocalDateTime.now();
        resetAbleMatchingRdsScoreIds = true;
    }
    /*스테이지 시작(전투시작)시 battleStartTime 설정*/
    public void StartStagePlay() {
        battleStartTime = LocalDateTime.now();
        resetAbleMatchingRdsScoreIds = true;
    }
    /*스테이지 클리어시 battleEndTime 설정*/
    public void ClearStagePlay() {
        battleEndTime = LocalDateTime.now();
    }
    /*스테이지 패배시 battleEndTime 설정*/
    public void DefeatStagePlay() {
        battleEndTime = LocalDateTime.now();
    }

    //유저가 선택한 매칭 유저
    public void SetMatchedUserId(Long matchedUser) {
        this.matchedUser = matchedUser;
    }

    //선택한 매칭 유저의 배틀 파워
    public void SetTeamBattlePower(int enemyTeamBattlePower, int myTeamBattlePower) {
        this.enemyTeamBattlePower = enemyTeamBattlePower;
        this.myTeamBattlePower = myTeamBattlePower;
    }

    //매칭에 활용될 5명의 유저 점수 데이터 아이디들
    public void SetMatchingRdsScoreIds(String matchingRdsScoreIds) {
        this.matchingRdsScoreIds = matchingRdsScoreIds;
        this.resetAbleMatchingRdsScoreIds = false;
    }
}
