package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "fielddungeonrewardprobabilitytable")
public class FieldDungeonRewardProbabilityTable {
    @Id
    int id;
    double grade1;
    double grade2;
    double grade3;
    double grade4;
    double grade5;
    double grade6;
    double grade7;
    double grade8;
    double grade9;
    double grade10;
    double grade11;
    double grade12;
    double grade13;
    double grade14;
    double grade15;
    double grade16;
    double grade17;
    double grade18;
    double grade19;
    double grade20;
}
