package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "heroclassinfotable")
public class HeroClassInfoTable {
    @Id
    String code;
    String grade;
    int gradeValue;
    String classType;
    String className;
    String equipWeapon;
}
