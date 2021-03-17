package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "heroequipmentclassprobability")
public class HeroEquipmentClassProbabilityTable {
    @Id
    int id;
    String category;
    double SSS;
    double SS;
    double S;
    double A;
    double B;
    double C;
    double D;
}

