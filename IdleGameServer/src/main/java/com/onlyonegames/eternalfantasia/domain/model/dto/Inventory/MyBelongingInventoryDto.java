package com.onlyonegames.eternalfantasia.domain.model.dto.Inventory;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyBelongingInventory;
import lombok.Data;

@Data
public class MyBelongingInventoryDto {
    Long id;
    Long useridUser;
    String code;
    int count;

    public MyBelongingInventory ToEntity() {
        return MyBelongingInventory.builder().useridUser(useridUser).code(code).count(count).build();
    }

    public void InitFromDBData(MyBelongingInventory dbData) {
        this.id = dbData.getId();
        this.useridUser = dbData.getUseridUser();
        this.code = dbData.getCode();
        this.count = dbData.getCount();
    }

    public void SetMyBelongingInventoryDto(Long useridUser, String code, int count) {
        this.useridUser = useridUser;
        this.code = code;
        this.count = count;
    }
}
