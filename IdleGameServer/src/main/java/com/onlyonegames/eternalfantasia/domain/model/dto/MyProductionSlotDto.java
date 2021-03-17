package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyProductionSlot;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyProductionSlotDto {
    Long userIdUser;
    int slotNo;
    int itemId;
    int state;
    int reduceSecondFromAD;
    LocalDateTime productionStartTime;

    public MyProductionSlot ToEntity() {
        return MyProductionSlot.builder().userIdUser(userIdUser).slotNo(slotNo).itemId(itemId).state(state).reduceSecondFromAD(reduceSecondFromAD).productionStartTime(productionStartTime).build();
    }

    public void InitFromDbData(MyProductionSlot dbData) {
        this.userIdUser = dbData.getUserIdUser();
        this.slotNo = dbData.getSlotNo();
        this.itemId = dbData.getItemId();
        this.state = dbData.getState();
        this.reduceSecondFromAD = dbData.getReduceSecondFromAD();
        this.productionStartTime = dbData.getProductionStartTime();
    }
}
