package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "gotchastep1table")
public class GotchaStep1Table {
    @Id
    int id;
    String name;
    double lowClassChance;
    double highClassChance;
}
