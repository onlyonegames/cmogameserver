package com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyFieldDungeonPlayLogForBattleRecord;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyFieldDungeonPlayLogForBattleRecordDto {
    Long id;
    Long useridUser;
    Long damage;
    boolean newLog;
    String profileHero;
    int profileFrame;
    LocalDateTime battleEndTime;

    public MyFieldDungeonPlayLogForBattleRecord ToEntity() {
        return MyFieldDungeonPlayLogForBattleRecord.builder().useridUser(useridUser).damage(damage).newLog(newLog).profileHero(profileHero)
                .profileFrame(profileFrame).battleEndTime(battleEndTime).build();
    }

    public void InitFromDbData(MyFieldDungeonPlayLogForBattleRecord dbData) {
        this.id = dbData.getId();
        this.useridUser = dbData.getUseridUser();
        this.damage = dbData.getDamage();
        this.newLog = dbData.isNewLog();
        this.profileHero = dbData.getProfileHero();
        this.profileFrame = dbData.getProfileFrame();
        this.battleEndTime = dbData.getBattleEndTime();
    }

    public void SetMyFieldDungeonPlayLogForBattleRecordDto(Long userId, Long damage, boolean newLog, String profileHero, int profileFrame, LocalDateTime battleEndTime) {
        this.useridUser = userId;
        this.damage = damage;
        this.newLog = newLog;
        this.profileHero = profileHero;
        this.profileFrame = profileFrame;
        this.battleEndTime = battleEndTime;
    }
}
