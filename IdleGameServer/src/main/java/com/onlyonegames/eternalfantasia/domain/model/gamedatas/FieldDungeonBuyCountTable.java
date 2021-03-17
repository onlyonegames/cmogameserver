package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "fielddungeonbuycounttable")
public class FieldDungeonBuyCountTable {
    @Id
    int id;
    int count;
    int diamondCount;
}
