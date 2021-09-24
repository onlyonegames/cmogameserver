package com.onlyonegames.eternalfantasia.domain.model.dto.Logging;

import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.ShopPurchaseLog;
import lombok.Data;

@Data
public class ShopPurchaseLogDto {
    Long id;
    Long useridUser;
    String itemName;
    String currencyType;

    public void SetShopPurchaseLogDto(Long userId, String itemName, String currencyType) {
        this.useridUser = userId;
        this.itemName = itemName;
        this.currencyType = currencyType;
    }

    public ShopPurchaseLog ToEntity() {
        return ShopPurchaseLog.builder().useridUser(useridUser).itemName(itemName).currencyType(currencyType).build();
    }
}
