package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Entity
@Table(name = "commonVariableTable")
public class CommonVariableTable {
    @Id
    int id;
    double fieldSpecialObjectPercent;
}
