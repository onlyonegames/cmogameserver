package com.onlyonegames.eternalfantasia.domain.model.dto.CostumeDto;

import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyCostumeInventory;
import lombok.Data;

@Data
public class MyCostumeInventoryDto {
    Long id;
    Long useridUser;
    String json_CostumeInventory;

    public MyCostumeInventory ToEntity() {
        return MyCostumeInventory.builder().useridUser(useridUser).json_CostumeInventory(json_CostumeInventory).build();
    }
}
