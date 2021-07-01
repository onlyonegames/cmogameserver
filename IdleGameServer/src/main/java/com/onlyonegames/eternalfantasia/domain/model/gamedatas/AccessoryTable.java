package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "accessorytable")
public class AccessoryTable {
    @Id
    int id;
    String code;
    String name;
    int type;
    String riseStatusType;
    double riseStatusValue;
}
