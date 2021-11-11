package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyClassInventory;
import lombok.Data;

@Data
public class ClassInventoryResponseDto {
    String code;
    int level;
    int count;
    int promotionPercent;
    int isPromotionLock;

    public void InitFromDB(MyClassInventory dbData) {
        this.code = dbData.getCode();
        this.level = dbData.getLevel();
        this.count = dbData.getCount();
        this.promotionPercent = dbData.getPromotionPercent();
        this.isPromotionLock = dbData.getIsPromotionLock();
    }
}
