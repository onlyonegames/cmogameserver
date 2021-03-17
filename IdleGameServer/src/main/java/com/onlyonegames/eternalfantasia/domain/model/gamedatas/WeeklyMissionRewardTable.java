package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "weeklymissionrewardtable")
public class WeeklyMissionRewardTable {
    @Id
    int id;
    String code;
    String gettingItem;
    int rewardCount;
    int needPoint;
}
