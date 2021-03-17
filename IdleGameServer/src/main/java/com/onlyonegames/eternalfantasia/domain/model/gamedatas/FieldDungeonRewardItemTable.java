package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "fielddungeonrewarditemtable")
public class FieldDungeonRewardItemTable {
    @Id
    int id;
    String code;
    String count;
}
