package com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyAncientDragonStagePlayData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyArenaPlayLogForBattleRecord;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyArenaPlayLogForBattleRecordDto {
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

    public MyArenaPlayLogForBattleRecord ToEntity() {
        return MyArenaPlayLogForBattleRecord.builder().useridUser(useridUser).attackOrDefence(attackOrDefence).winOrDefeat(winOrDefeat).
                enemyUserId(enemyUserId).enemyUserGameName(enemyUserGameName).enemyTeamBattlePower(enemyTeamBattlePower).gettingPoint(gettingPoint)
                .nowScore(nowScore).previousScore(previousScore).nowTier(nowTier).previousTier(previousTier).nowRanking(nowRanking).previousRanking(previousRanking)
                .newLog(newLog).profileHero(profileHero).profileFrame(profileFrame).enemyTier(enemyTier).battleStartTime(battleStartTime).battleEndTime(battleEndTime)
                .profileHero(profileHero).profileFrame(profileFrame).build();
    }

    public void SetMyArenaPlayLogForBattleRecordDto(MyArenaPlayLogForBattleRecord myArenaPlayLogForBattleRecord) {
        this.attackOrDefence = myArenaPlayLogForBattleRecord.getAttackOrDefence();
        this.winOrDefeat = myArenaPlayLogForBattleRecord.getWinOrDefeat();
        this.enemyUserId = myArenaPlayLogForBattleRecord.getEnemyUserId();
        this.enemyUserGameName = myArenaPlayLogForBattleRecord.getEnemyUserGameName();
        this.enemyTeamBattlePower = myArenaPlayLogForBattleRecord.getEnemyTeamBattlePower();
        this.gettingPoint = myArenaPlayLogForBattleRecord.getGettingPoint();
        this.nowScore = myArenaPlayLogForBattleRecord.getNowScore();
        this.previousScore = myArenaPlayLogForBattleRecord.getPreviousScore();
        this.nowTier = myArenaPlayLogForBattleRecord.getNowTier();
        this.previousTier = myArenaPlayLogForBattleRecord.getPreviousTier();
        this.nowRanking = myArenaPlayLogForBattleRecord.getNowRanking();
        this.previousRanking = myArenaPlayLogForBattleRecord.getPreviousRanking();
        this.enemyTier = myArenaPlayLogForBattleRecord.getEnemyTier();
        this.profileHero = myArenaPlayLogForBattleRecord.getProfileHero();
        this.profileFrame = myArenaPlayLogForBattleRecord.getProfileFrame();
    }

    public void InitFromDbData(MyArenaPlayLogForBattleRecord dbData) {
        this.id = dbData.getId();
        this.useridUser = dbData.getUseridUser();
        this.attackOrDefence = dbData.getAttackOrDefence();
        this.winOrDefeat = dbData.getWinOrDefeat();
        this.enemyUserId = dbData.getEnemyUserId();
        this.enemyUserGameName = dbData.getEnemyUserGameName();
        this.enemyTeamBattlePower = dbData.getEnemyTeamBattlePower();
        this.gettingPoint = dbData.getGettingPoint();
        this.nowScore = dbData.getNowScore();
        this.previousScore = dbData.getPreviousScore();
        this.nowTier = dbData.getNowTier();
        this.previousTier = dbData.getPreviousTier();
        this.nowRanking = dbData.getNowRanking();
        this.previousRanking = dbData.getPreviousRanking();
        this.enemyTier = dbData.getEnemyTier();
        this.newLog = dbData.isNewLog();
        this.battleStartTime = dbData.getBattleStartTime();
        this.battleEndTime = dbData.getBattleEndTime();
        this.profileHero = dbData.getProfileHero();
        this.profileFrame = dbData.getProfileFrame();
    }
}
