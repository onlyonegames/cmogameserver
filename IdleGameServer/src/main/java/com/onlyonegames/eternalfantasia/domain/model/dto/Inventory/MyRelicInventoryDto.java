package com.onlyonegames.eternalfantasia.domain.model.dto.Inventory;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyRelicInventory;
import lombok.Data;

@Data
public class MyRelicInventoryDto {
    Long id;
    Long useridUser;
    int table_id;
    int count;
    int level;

    public MyRelicInventory ToEntity() {
        return MyRelicInventory.builder().useridUser(useridUser).table_id(table_id).count(count).level(level).build();
    }

    public void SetMyRelicInventoryDto(Long useridUser, int table_id, int count, int level) {
        this.useridUser = useridUser;
        this.table_id = table_id;
        this.count = count;
        this.level = level;
    }
}
