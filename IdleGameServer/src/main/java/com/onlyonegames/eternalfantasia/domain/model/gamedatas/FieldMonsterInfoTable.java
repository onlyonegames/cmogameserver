package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "fieldmonsterinfotable")
public class FieldMonsterInfoTable{
    @Id
    int id;
    String code;
    String name;
    String nickName;
    String info;
    String spriteName;
}
