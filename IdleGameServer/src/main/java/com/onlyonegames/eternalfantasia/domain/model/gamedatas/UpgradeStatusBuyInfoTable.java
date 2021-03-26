package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class UpgradeStatusBuyInfoTable
{
    @Id
    String code;
    int cost;
    int maxLevel;
    double riseValue;
}