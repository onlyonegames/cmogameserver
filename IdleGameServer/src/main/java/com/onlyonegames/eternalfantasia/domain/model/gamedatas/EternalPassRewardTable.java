package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "eternalpassrewardtable")
public class EternalPassRewardTable {
    @Id
    int ID;
    String FreePassReward;
    int FreePassGettingCount;
    String RoyalPassReward;
    int RoyalPassGettingCount;
}
