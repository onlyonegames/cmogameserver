package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "gotchatable")
public class GotchaTable {
    @Id
    int id;
    String gotchaCharacterCost1_1;
    String gotchaCharacterCost1_2;
    String gotchaCharacterCost10_1;
    String gotchaCharacterCost10_2;
    String gotchaEtcCost1_1;
    String gotchaEtcCost1_2;
    String gotchaEtcCost10_1;
    String gotchaEtcCost10_2;
    int maxMileage;
    int maxFreeGotcha;
}
