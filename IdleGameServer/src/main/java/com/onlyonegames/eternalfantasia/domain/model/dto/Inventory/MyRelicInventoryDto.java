package com.onlyonegames.eternalfantasia.domain.model.dto.Inventory;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyRelicInventory;
import lombok.Data;

@Data
public class MyRelicInventoryDto {
    Long id;
    Long useridUser;
    int index;
    int count;
    int level;

    public MyRelicInventory ToEntity() {
        return MyRelicInventory.builder().useridUser(useridUser).index(index).count(count).level(level).build();
    }

    public void SetMyRelicInventoryDto(Long useridUser, int index, int count, int level) {
        this.useridUser = useridUser;
        this.index = index;
        this.count = count;
        this.level = level;
    }
}
