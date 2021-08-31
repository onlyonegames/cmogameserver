package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "adventurestagefreepasstable")
public class AdventureStageFreePassTable {
    @Id
    int id;
    String missionName;
    int goalCount;
    String rewardType;
    String rewardCount;
}
