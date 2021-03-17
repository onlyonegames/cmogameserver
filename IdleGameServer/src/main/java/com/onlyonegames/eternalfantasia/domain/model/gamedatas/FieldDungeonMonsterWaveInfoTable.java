package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "fielddungeonmonsterwaveinfotable")
public class FieldDungeonMonsterWaveInfoTable {
    @Id
    int id;
    int level;
    int grade;
    String code;
    String type;
}
