package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "equipmenttable")
public class EquipmentTable {
    @Id
    int id;
    String code;
    String name;
    String grade;
    int gradeValue;
}
