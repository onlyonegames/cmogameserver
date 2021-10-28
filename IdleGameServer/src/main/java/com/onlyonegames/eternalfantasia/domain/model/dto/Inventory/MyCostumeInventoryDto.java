package com.onlyonegames.eternalfantasia.domain.model.dto.Inventory;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyCostumeInventory;
import lombok.Data;

@Data
public class MyCostumeInventoryDto {
    Long id;
    Long useridUser;
    String code;

    public MyCostumeInventory ToEntity() {
        return MyCostumeInventory.builder().useridUser(useridUser).code(code).build();
    }

    public void SetMyCostumeInventoryDto(Long userId, String code) {
        this.useridUser = userId;
        this.code = code;
    }
}
