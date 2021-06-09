package com.onlyonegames.eternalfantasia.domain.model.dto.Inventory;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyClassInventory;
import lombok.Data;

@Data
public class MyClassInventoryDto {
    Long id;
    Long useridUser;
    String code;
    int level;
    int skillUpgradeIndex;
    int count;

    public MyClassInventory ToEntity() {
        return MyClassInventory.builder().useridUser(useridUser).code(code).level(level).skillUpgradeIndex(skillUpgradeIndex).count(count).build();
    }

    public void InitFromDBData(MyClassInventory dbData) {
        this.id = dbData.getId();
        this.useridUser = dbData.getUseridUser();
        this.code = dbData.getCode();
        this.level = dbData.getLevel();
        this.skillUpgradeIndex = dbData.getSkillUpgradeIndex();
        this.count = dbData.getCount();
    }
}
