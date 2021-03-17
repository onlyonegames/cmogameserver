package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class herostable {
    @Id
    String code;
    String name;
    String attackType;
    double healthPoint;
    double attackPower;
    double physicalDefence;
    double magicDefence;
    double attackRate;
    double criticalChance;
    double criticalPercent;
    double penetrationChance;
    double penetrationPercent;
    double accuracyRate;
    double evasionRate;
    int tier;
    String position;
    String elementsType;
}
