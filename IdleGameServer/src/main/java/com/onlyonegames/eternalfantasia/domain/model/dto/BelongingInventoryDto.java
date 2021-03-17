package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.ItemType;
import lombok.Data;

@Data
public class BelongingInventoryDto {
    Long id;
    Long useridUser;
    ItemType itemType;
    int itemId;
    int count;

    public BelongingInventory ToEntity() {
        return BelongingInventory.builder().useridUser(useridUser).itemType(itemType).itemId(itemId).count(count).build();
    }

    public void AddCount(int chargeCount) {
        count += chargeCount;
    }

    public void InitFromDbData(BelongingInventory dbData) {
        id = dbData.getId();
        useridUser = dbData.getUseridUser();
        itemType = dbData.getItemType();
        itemId = dbData.getItemId();
        count = dbData.getCount();
    }
}
