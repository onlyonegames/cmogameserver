package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "productionslotopencosttable")
public class ProductionSlotOpenCostTable {
    @Id
    int id;
    String code;
    int needDiamond;
}
