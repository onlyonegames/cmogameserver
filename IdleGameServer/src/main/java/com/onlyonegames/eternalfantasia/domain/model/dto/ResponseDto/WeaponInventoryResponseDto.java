package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyEquipmentInventory;
import lombok.Data;

@Data
public class WeaponInventoryResponseDto {
    String code;
    int count;
    int level;
    int isPromotionLock;

    public void InitFromDB(MyEquipmentInventory dbData) {
        this.code = dbData.getCode();
        this.count = dbData.getCount();
        this.level = dbData.getLevel();
        this.isPromotionLock = dbData.getIsPromotionLock();
    }
}
