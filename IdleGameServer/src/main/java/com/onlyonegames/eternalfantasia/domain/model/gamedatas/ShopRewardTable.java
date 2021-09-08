package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "shoprewardtable")
public class ShopRewardTable {
    @Id
    int id;
    String itemName;
    String rewardList;
    String currencyType;
    int price;
}
