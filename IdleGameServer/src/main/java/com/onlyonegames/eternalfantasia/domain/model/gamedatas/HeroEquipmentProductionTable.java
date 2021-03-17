package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name ="heroequipmentproductionnewtable")
public class HeroEquipmentProductionTable {
    @Id
    int id;
    String code;
    String materials;
    String needCounts;
    int needGold;
}
