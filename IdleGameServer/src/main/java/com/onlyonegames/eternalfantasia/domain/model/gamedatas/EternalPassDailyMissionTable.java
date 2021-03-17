package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "eternalpassdailymissiontable")
public class EternalPassDailyMissionTable {
    @Id
    int id;
    String code;
    String missionName;
    String missionTypeName;
    String missionParamName;
    int goalCount;
    int gettingPoint;
    String moveScene;
    String afterAction;
}
