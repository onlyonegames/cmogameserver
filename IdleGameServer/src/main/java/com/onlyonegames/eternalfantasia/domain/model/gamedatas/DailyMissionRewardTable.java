package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "dailymissionrewardtable")
public class DailyMissionRewardTable {
    @Id
    int id;
    String code;
    String gettingItem;
    int rewardCount;
    int needPoint;
}
