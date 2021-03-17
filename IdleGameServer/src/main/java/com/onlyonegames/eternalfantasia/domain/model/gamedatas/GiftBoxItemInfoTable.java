package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "item_giftboxiteminfotable")
public class GiftBoxItemInfoTable {
    @Id
    int id;
    String code;
    String name;
    String desc;
    String grade;
    int stackLimit;
    boolean selectable;
}
