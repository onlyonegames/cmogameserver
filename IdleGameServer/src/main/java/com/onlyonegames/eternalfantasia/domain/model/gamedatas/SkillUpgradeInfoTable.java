package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "skillupgradeinfotable")
public class SkillUpgradeInfoTable {
    @Id
    int id;
    String ownerClass;
    String upgradeInfo;
}
