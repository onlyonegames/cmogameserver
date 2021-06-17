package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import lombok.Data;

@Data
public class BelongingInventoryJsonData {
    int count;
    int slot;
    int slotPercent;

    public void SetBelongingInventoryJsonData(int count, int slotNo, int slotPercent) {
        this.count = count;
        this.slot = slotNo;
        this.slotPercent = slotPercent;
    }
}
