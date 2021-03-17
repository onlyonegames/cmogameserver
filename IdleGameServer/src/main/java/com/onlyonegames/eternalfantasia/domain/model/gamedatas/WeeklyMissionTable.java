package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "weeklymissiontable")
public class WeeklyMissionTable {
    @Id
    int id;
    int gettingPoint;
    int goalCount;
    int rewardCount;
    String code;
    String missionName;
    String missionTypeName;
    String missionParamName;
    String gettingItem;
    String moveScene;
}
