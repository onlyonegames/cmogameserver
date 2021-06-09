package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "relicinfotable")
public class RelicInfoTable {
    @Id
    int index;
    String name;
}
