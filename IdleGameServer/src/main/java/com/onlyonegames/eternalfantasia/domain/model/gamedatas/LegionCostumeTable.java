package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "legioncostumeinfotable")
public class LegionCostumeTable {
    @Id
    int id;
    String ownerCode;
    String costumeCode;
    String name;
    String grade;
    String optionIds;
    String values;
    int price;
}
