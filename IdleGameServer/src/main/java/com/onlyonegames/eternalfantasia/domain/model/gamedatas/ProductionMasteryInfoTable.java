package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "productionmasteryinfotable")
public class ProductionMasteryInfoTable {
    @Id
    int id;
    int levelCondition;
    int giftBoxItemId;
    int gettingCount;
}
