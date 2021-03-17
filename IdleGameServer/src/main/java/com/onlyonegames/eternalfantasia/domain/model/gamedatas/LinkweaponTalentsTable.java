package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "linkweapon_talenttable")
public class LinkweaponTalentsTable {
    @Id
    int linkweapon_talenttable_id;
    int ID;
    String Owner;
    int Rank;
    String Name;
    String Explain;
    String CONTINUITY_KIND;
    String KIND;
    String PERCENT_OR_FIXEDVALUE;
    String TALENTOPTION_HOWTO_PLUSE_KIND;
    String LinkpointCostsForStrength;
    String GoldCostForStrength;
    String NeedMaterialsForStrength;
    int LAbilityLevelCondition;
}
