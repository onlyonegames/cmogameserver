package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "exchangeitemtable")
public class ExchangeItemTable {
    @Id
    int id;
    String itemName;
    String rewardList;
    String currencyType;
    String price;
}
