package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "levelfreepasstable")
public class LevelFreePassTable {
    @Id
    int id;
    int groupId;
    int groupIndex;
    String missionName;
    int goalCount;
    String rewardType;
    String rewardCount;
}
