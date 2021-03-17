package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "gotchastep2table")
public class GotchaStep2Table {
    @Id
    int id;
    String code;
    int step1Id;
    String gettingItem;
    int lowClassGettingCount;
    double lowClassChance;
    int highClassGettingCount;
    double hightClassChance;
}
