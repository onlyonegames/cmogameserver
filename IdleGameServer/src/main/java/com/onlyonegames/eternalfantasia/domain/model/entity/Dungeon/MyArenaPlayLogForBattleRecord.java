package com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class MyArenaPlayLogForBattleRecord extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    int attackOrDefence;//1 attack, 2 defence
    int winOrDefeat; //1 win, 2 Defeat
    Long enemyUserId;
    String enemyUserGameName;
    int enemyTeamBattlePower;
    int gettingPoint;
    Long nowScore;
    Long previousScore;
    int nowTier;
    int previousTier;
    Long nowRanking;
    Long previousRanking;
    int enemyTier;
    boolean newLog;// 아직 유저가 확인하지 않은 로그
    //프레임 및 테두리
    String profileHero;
    int profileFrame;
    LocalDateTime battleStartTime;
    LocalDateTime battleEndTime;

    public void CheckedLog(){
        this.newLog = false;
    }
}
