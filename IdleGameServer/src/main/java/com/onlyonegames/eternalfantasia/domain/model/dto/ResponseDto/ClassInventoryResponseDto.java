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
    int superiorLevel;
    String superiorOptions;
    String superiorOptionLock;

    public void InitFromDB(MyClassInventory dbData) {
        this.code = dbData.getCode();
        this.level = dbData.getLevel();
        this.count = dbData.getCount();
        this.promotionPercent = dbData.getPromotionPercent();
        this.isPromotionLock = dbData.getIsPromotionLock();
        this.superiorLevel = dbData.getSuperiorLevel();
        this.superiorOptions = dbData.getSuperiorOptions();
        this.superiorOptionLock = dbData.getSuperiorOptionLock();
    }
}
