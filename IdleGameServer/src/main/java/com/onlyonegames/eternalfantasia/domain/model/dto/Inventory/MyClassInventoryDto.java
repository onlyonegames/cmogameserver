package com.onlyonegames.eternalfantasia.domain.model.dto.Inventory;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyClassInventory;
import lombok.Data;

@Data
public class MyClassInventoryDto {
    Long id;
    Long useridUser;
    String code;
    int level;
    int count;
    int promotionPercent;
    int isPromotionLock;
    int superiorLevel;
    String superiorOptions;
    String superiorOptionLock;

    public MyClassInventory ToEntity() {
        return MyClassInventory.builder().useridUser(useridUser).code(code).level(level).count(count).promotionPercent(promotionPercent).superiorLevel(superiorLevel)
                .superiorOptions(superiorOptions).superiorOptionLock(superiorOptionLock).build();
    }

    public void InitFromDBData(MyClassInventory dbData) {
        this.id = dbData.getId();
        this.useridUser = dbData.getUseridUser();
        this.code = dbData.getCode();
        this.level = dbData.getLevel();
        this.count = dbData.getCount();
        this.promotionPercent = dbData.getPromotionPercent();
        this.superiorLevel = dbData.getSuperiorLevel();
        this.superiorOptions = dbData.getSuperiorOptions();
        this.superiorOptionLock = dbData.getSuperiorOptionLock();
    }

    public void SetMyClassInventoryDto(Long useridUser, String code, int level, int count, int promotionPercent, int isPromotionLock, int superiorLevel, String superiorOptions, String superiorOptionLock) {
        this.useridUser = useridUser;
        this.code = code;
        this.level = level;
        this.count = count;
        this.promotionPercent = promotionPercent;
        this.isPromotionLock = isPromotionLock;
        this.superiorLevel = superiorLevel;
        this.superiorOptions = superiorOptions;
        this.superiorOptionLock = superiorOptionLock;
    }
}
