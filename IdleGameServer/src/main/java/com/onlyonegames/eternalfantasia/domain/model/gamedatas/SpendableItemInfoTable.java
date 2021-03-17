package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "item_spendableiteminfotable")
public class SpendableItemInfoTable {
    @Id
    int id;
    int stackLimit;
    String code;
    String name;
    String grade;
    String spendableType;
}
