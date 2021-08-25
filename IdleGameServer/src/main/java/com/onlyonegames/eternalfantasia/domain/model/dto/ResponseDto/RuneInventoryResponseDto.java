package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyRuneInventory;
import lombok.Data;

@Data
public class RuneInventoryResponseDto {
    String code;
    int level;
    int count;

    public void SetRuneInventoryResponseDto(MyRuneInventory dbData) {
        this.code = dbData.getCode();
        this.level = dbData.getLevel();
        this.count = dbData.getCount();
    }
}
