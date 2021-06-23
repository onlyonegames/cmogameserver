package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyRelicInventory;
import lombok.Data;

@Data
public class RelicInventoryResponseDto {
    int count;
    int level;

    public void InitFromDB(MyRelicInventory dbData) {
        this.count = dbData.getCount();
        this.level = dbData.getLevel();
    }
}
