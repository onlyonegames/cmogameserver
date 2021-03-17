package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "profileframetable")
public class ProfileFrameTable {
    @Id
    int id;
    String code;
    String info;
}
