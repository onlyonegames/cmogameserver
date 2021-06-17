package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import lombok.Data;

@Data
public class BelongingInventoryJsonData {
    int count;
    int slot;

    public void SetBelongingInventoryJsonData(int count, int slotNo) {
        this.count = count;
        this.slot = slotNo;
    }
}
