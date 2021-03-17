package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "gotchacharactertable")
public class GotchaCharacterTable {
    @Id
    int ID;
    String code;
    String name;
    float normalGotcha;
    float pickupGotcha;
}
