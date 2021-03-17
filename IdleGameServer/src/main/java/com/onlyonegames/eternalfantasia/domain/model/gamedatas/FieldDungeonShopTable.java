package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "fielddungeonshoptable")
public class FieldDungeonShopTable {
    @Id
    int id;
    String code;
    String name;
    String gettingItem;
    String currency;
    int gettingCount;
    int cost;
}
