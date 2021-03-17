package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "expeditionprocesstable")
public class ExpeditionProcessTable {
    @Id
    int expeditionprocesstable_id;
    int maxFlowtime;
    int gainGoldBaseValue;
    int gainGoldPerSecond;
    int gainLinkpoint;
    int gainLinkpointPerSecond;
    int gainExpBaseValue;
    int gainExpPerSecond;
    int gainMaterialBaseValue;
    int gainMaterialPerSecond;
    int gainEquipmentBaseValue;
    int gainEquipmentPerSecond;
    int gainGiftBaseValue;
    int gainGiftPerSecond;
    int gainEnchantItemBaseValue;
    int gainEnchantItemPerSecond;
    int boostTime;
    int boostPower;
    int boostMaxCountPerDay;
    int boostCost;
}
