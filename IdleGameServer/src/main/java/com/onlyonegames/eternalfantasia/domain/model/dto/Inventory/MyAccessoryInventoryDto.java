package com.onlyonegames.eternalfantasia.domain.model.dto.Inventory;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyAccessoryInventory;
import lombok.Data;

@Data
public class MyAccessoryInventoryDto {
    Long id;
    Long useridUser;
    String code;
    int count;
    int level;
    String optionLockList;
    String options;

    public MyAccessoryInventory ToEntity() {
        return MyAccessoryInventory.builder().useridUser(useridUser).code(code).count(count).level(level).optionLockList(optionLockList).options(options).build();
    }

    public void InitFromDB(MyAccessoryInventory dbData) {
        this.id = dbData.getId();
        this.useridUser = dbData.getUseridUser();
        this.code = dbData.getCode();
        this.count = dbData.getCount();
        this.level = dbData.getLevel();
        this.optionLockList = dbData.getOptionLockList();
    }

    public void SetMyAccessoryInventoryDto(Long useridUser, String code, int count, int level, String optionLockList, String options) {
        this.useridUser = useridUser;
        this.code = code;
        this.count = count;
        this.level = level;
        this.optionLockList = optionLockList;
        this.options = options;
    }
}
