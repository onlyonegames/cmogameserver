package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "heroequipmentspromotiontable")
public class HeroEquipmentsPromotionTable {
    @Id
    int id;
    String code;
    String materials;
    String needCounts;
    int needGold;
    int needRetryDimond;
}
