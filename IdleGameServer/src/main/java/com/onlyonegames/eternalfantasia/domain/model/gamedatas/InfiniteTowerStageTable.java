package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Data
@Entity
@Table(name = "infinitetowerstageinfotable")
public class InfiniteTowerStageTable {
    @Id
    int infinitetowerstageinfotable_id;
    String Stage;
    String ConditionStar1;
    String ConditionStar2;
    String ConditionStar3;
    String FirstReward;
    String StarsReward;
    String RepeatReward;
}
