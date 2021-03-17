package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "monsterkilleventtable")
public class MonsterKillEventTable {
    @Id
    int id;
    String code;
    String missionName;
    String missionTypeName;
    String missionParamName;
    int goalCount;
    String gettingItem;
    int gettingCount;
}
