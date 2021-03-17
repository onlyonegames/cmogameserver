package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "equipmentoptionsinfotable")
public class EquipmentOptionsInfoTable {
    @Id
    int ID;
    boolean OnlySmeltingOption;
    double BaseValue;
    String Option;
    String CONTINUITY_KIND;
    String KIND;
    String REQUIRE_KIND;
    String TALENTOPTION_HOWTO_PLUSE_KIND;
    String PERCENT_OR_FIXEDVALUE;
}
