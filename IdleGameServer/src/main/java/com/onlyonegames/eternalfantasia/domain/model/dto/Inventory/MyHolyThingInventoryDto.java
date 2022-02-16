package com.onlyonegames.eternalfantasia.domain.model.dto.Inventory;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyHolyThingInventory;
import lombok.Data;

@Data
public class MyHolyThingInventoryDto {
    Long id;
    Long useridUser;
    String code;
    int count;
    int level;

    public MyHolyThingInventory ToEntity() {
        return MyHolyThingInventory.builder().useridUser(useridUser).code(code).count(count).level(level).build();
    }

    public void SetMyHolyThingInventoryDto(Long useridUser, String code, int count, int level) {
        this.useridUser = useridUser;
        this.code = code;
        this.count = count;
        this.level = level;
    }
}
