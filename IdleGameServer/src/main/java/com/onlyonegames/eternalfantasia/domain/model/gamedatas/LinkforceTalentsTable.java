package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import com.onlyonegames.util.StringMaker;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "linkforce_talentstable")
public class LinkforceTalentsTable {
    @Id
    int talentstable_id;
    int TalentID;
    String Owner;
    String TalentName;
    String TalentExplain;
    String CONTINUITY_KIND;
    String KIND;
    String REQUIRE_KIND;
    String TALENTOPTION_HOWTO_PLUSE_KIND;
    String ConditionValues;
    int HaremCost;
    int GFCondition;
}
