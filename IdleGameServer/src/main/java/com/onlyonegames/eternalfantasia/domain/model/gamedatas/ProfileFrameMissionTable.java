package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "profileframemissiontable")
public class ProfileFrameMissionTable {
    @Id
    int id;
    String code;
    String missionTypeName;
    String missionParamName;
    String MissionName;
    int goalCount;
    String gettingFrame;
}
