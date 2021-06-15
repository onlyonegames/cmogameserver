package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "runeinfotable")
public class RuneInfoTable {
    @Id
    int id;
    String runeName;
    int qualityNo;
    int gradeNo;
    int rune_Id; // 1: ,2: ,3:, 4:, 5:, 6:
}
