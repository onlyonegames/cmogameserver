package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyAccessoryInventory;
import lombok.Data;

@Data
public class AccessoryInventoryResponseDto {
    Long id;
    String code;
    int count;
    int level;
    String optionLockList;
    String options;

    public void InitFromDB(MyAccessoryInventory dbData) {
        this.id = dbData.getId();
        this.code = dbData.getCode();
        this.count = dbData.getCount();
        this.level = dbData.getLevel();
        this.optionLockList = dbData.getOptionLockList();
        this.options = dbData.getOptions();
    }
}
