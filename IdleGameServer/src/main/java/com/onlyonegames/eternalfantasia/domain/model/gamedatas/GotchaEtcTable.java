package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "gotchaetctable")
public class GotchaEtcTable {
    @Id
    int id;
    String code;
    String name;
    int gettingCount;
    double chance;
}
