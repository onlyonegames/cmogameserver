package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "charactertopiecetable")
public class CharacterToPieceTable {
    @Id
    int id;
    String code;
    String name;
    int pieceCount;
}
