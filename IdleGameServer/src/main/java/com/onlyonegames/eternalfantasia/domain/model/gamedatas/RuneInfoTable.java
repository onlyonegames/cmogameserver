package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "runeinfotable")
public class RuneInfoTable {
    @Id
    int id;
    String code;
    String runeName;
    int runeKind; // 1: ,2: ,3:, 4:, 5:, 6:
    int gradeNo;
    int qualityNo;
}
