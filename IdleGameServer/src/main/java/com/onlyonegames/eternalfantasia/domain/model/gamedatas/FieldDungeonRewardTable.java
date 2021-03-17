package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "fielddungeonrewardtable")
public class FieldDungeonRewardTable {
    @Id
    int id;
    String rankingName;
    String gettingItem;
}
