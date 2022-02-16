package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyHolyThingInventory;
import lombok.Data;

@Data
public class HolyThingInventoryResponseDto {
    int count;
    int level;

    public void InitFromDB(MyHolyThingInventory dbData) {
        this.count = dbData.getCount();
        this.level = dbData.getLevel();
    }
}
