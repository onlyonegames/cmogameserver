package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "ancientdragonstagetable")
public class AncientDragonStageTable {
    @Id
    int ancientdragonstagetable_id;
    String Stage;
    String ConditionStar1;
    String ConditionStar2;
    String ConditionStar3;
    String FirstReward;
    String StarsReward;
    String RepeatReward;
    int PercentGetCoinPerKill;
    int PercentBronzCoin;
    int LimitGetableBornzCoin;
    int PercentSilverCoin;
    int LimitGetableSilverCoin;
    int PercentGoldCoin;
    int LimitGetableGoldCoin;
    int PercentGetBoxPerKill;
    int PercentBronzBox;
    int LimitGetableBronzBox;
    int PercentSilverBox;
    int LimitGetableSilverBox;
    int PercentGoldBox;
    int LimitGetableGoldBox;
    int OpenLevel;
    int NeedCountStoneOfDimension;
}
