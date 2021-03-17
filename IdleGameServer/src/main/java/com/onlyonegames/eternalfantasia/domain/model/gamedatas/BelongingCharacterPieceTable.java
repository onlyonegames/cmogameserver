package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "belongingcharacterpiecetable")
public class BelongingCharacterPieceTable {
    @Id
    int id;
    String code;
    String name;
    String desc;
    String grade;
    int stackLimit;
}
