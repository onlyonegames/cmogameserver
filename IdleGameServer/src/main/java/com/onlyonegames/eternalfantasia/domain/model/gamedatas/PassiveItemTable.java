package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "passiveitemtable")
public class PassiveItemTable {
    @Id
    int id;
    String code;
    String name;
    String itemExplain;
    String CONTINUITY_KIND;
    String KIND;
}
