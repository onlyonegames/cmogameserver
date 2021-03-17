package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "skilltable")
public class SkillTable {
    @Id
    int id;
    String code;
    String owner;
    String skillName;
    String attackType;
    int skillPowerPercent;
    int manaCost;
    int maxDeckCount;
    String skillExplain;
}