package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "passiveskilltable")
public class PassiveSkillTable {
    @Id
    int id;
    String code;
    String name;
}
