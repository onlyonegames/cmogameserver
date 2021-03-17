package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyTeamInfo;
import lombok.Data;

@Data
public class MyTeamInfoDto {
    Long id;
    Long useridUser;
    String stageTeam;
    String ancientDragonDungeonPlayTeam;
    String arenaPlayTeam;
    String heroTowerDungeonPlayTeam;
    String ordealDungeonPlayTeam;
    String arenaDefenceTeam;
    String infinityTowerTeam;
    String fieldDungeonPlayTeam;

    public MyTeamInfo ToEntity(){
        return MyTeamInfo.builder().useridUser(useridUser).build();
    }

    public void InitFromDBData(MyTeamInfo myTeamInfo){
        this.id = myTeamInfo.getId();
        this.useridUser = myTeamInfo.getUseridUser();
        this.stageTeam = myTeamInfo.getStageTeam();
        this.ancientDragonDungeonPlayTeam = myTeamInfo.getAncientDragonDungeonPlayTeam();
        this.arenaPlayTeam = myTeamInfo.getArenaPlayTeam();
        this.heroTowerDungeonPlayTeam = myTeamInfo.getHeroTowerDungeonPlayTeam();
        this.ordealDungeonPlayTeam =  myTeamInfo.getOrdealDungeonPlayTeam();
        this.arenaDefenceTeam = myTeamInfo.getArenaDefenceTeam();
        this.infinityTowerTeam = myTeamInfo.getInfiniteTowerTeam();
        this.fieldDungeonPlayTeam = myTeamInfo.getFieldDungeonPlayTeam();
    }
}