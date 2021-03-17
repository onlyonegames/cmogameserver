package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "exchangeitemevnettable")
public class ExchangeItemEventTable {
    @Id
    int id;
    String gettingItemCode;
    Integer gettingItemCount;
    int maxExchange;
    int spendCost;

}
