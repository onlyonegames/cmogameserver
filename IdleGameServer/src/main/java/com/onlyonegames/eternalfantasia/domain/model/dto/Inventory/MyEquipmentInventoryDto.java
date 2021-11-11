package com.onlyonegames.eternalfantasia.domain.model.dto.Inventory;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyEquipmentInventory;
import lombok.Data;

@Data
public class MyEquipmentInventoryDto {
    Long id;
    Long useridUser;
    String code;
    String grade;
    int count;
    int level;
    int isPromotionLock;

    public MyEquipmentInventory ToEntity() {
        return MyEquipmentInventory.builder().useridUser(useridUser).code(code).grade(grade).count(count).level(level).build();
    }

    public void InitFromDBData(MyEquipmentInventory dbData){
        this.id = dbData.getId();
        this.useridUser = dbData.getUseridUser();
        this.code = dbData.getCode();
        this.grade = dbData.getGrade();
        this.count = dbData.getCount();
        this.level = dbData.getLevel();
    }

    public void SetMyEquipmentInventoryDto(Long useridUser, String code, String grade, int count, int level, int isPromotionLock) {
        this.useridUser = useridUser;
        this.code = code;
        this.grade = grade;
        this.count = count;
        this.level = level;
        this.isPromotionLock = isPromotionLock;
    }
}
