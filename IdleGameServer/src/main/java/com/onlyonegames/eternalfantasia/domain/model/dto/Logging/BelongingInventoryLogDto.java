package com.onlyonegames.eternalfantasia.domain.model.dto.Logging;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.ItemType;
import lombok.Data;

@Data
public class BelongingInventoryLogDto {
    String workingPosition;
    Long inventoryId;
    int itemId;
    ItemType itemType_id;
    int previousValue;
    int changeNum;
    int presentValue;

    public void setBelongingInventoryLogDto(String workingPosition, Long inventoryId, int itemId, ItemType itemType_id, int changeNum, int presentValue) {
        this.workingPosition = workingPosition;
        this.inventoryId = inventoryId;
        this.itemId = itemId;
        this.itemType_id = itemType_id;
        this.changeNum = changeNum;
        this.presentValue = presentValue;
    }
}
