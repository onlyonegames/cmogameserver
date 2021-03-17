package com.onlyonegames.eternalfantasia.domain.model.dto.GiftItemDto;

import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyGiftInventory;
import lombok.Data;

@Data
public class MyGiftInventoryDto {
    Long id;
    Long useridUser;
    String inventoryInfos;

    public MyGiftInventory ToEntity() {
        return MyGiftInventory.builder().useridUser(useridUser).inventoryInfos(inventoryInfos).build();
    }
}
