package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "fieldobjectinfotable")
public class FieldObjectInfoTable {
    @Id
    int id;
    int field;
    int needMaxGenCount;
    String gettingItem;
    /**클라이언트에서 활용하는 데이터*/
    int gettingMin;
    int gettingMax;
    float nextGenCooltime;
    /**추가*/
    int maxGenCount;
    int onceGenCount;
    String type;
}
