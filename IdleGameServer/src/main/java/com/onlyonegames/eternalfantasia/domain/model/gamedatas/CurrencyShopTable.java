package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
@Data
@Entity(name = "currencyshoptable")
public class CurrencyShopTable {
    @Id
    int id;
    String code;
    String name;
    String gettingItem;
    int bonusPercent;
    String currency;
    int gettingCount;
    int cost;
    int bonus;
}
