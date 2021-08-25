package com.onlyonegames.eternalfantasia.domain.model.dto.Inventory;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyRuneInventory;
import lombok.Data;

@Data
public class MyRuneInventoryDto {
    Long id;
    Long useridUser;
    String code;
    int count;
    int level;

    public MyRuneInventory ToEntity() {
        return MyRuneInventory.builder().useridUser(useridUser).code(code).count(count).level(level).build();
    }

    public void InitFromDBData(MyRuneInventory dbData){
        this.id = dbData.getId();
        this.useridUser = dbData.getUseridUser();
        this.code = dbData.getCode();
//        this.rune_Id = dbData.getRune_Id();
//        this.itemClass = dbData.getItemClass();
//        this.itemClassValue = dbData.getItemClassValue();
//        this.grade = dbData.getGrade();
        this.count = dbData.getCount();
        this.level = dbData.getLevel();
    }

//    public void SetMyRuneInventoryDto(Long useridUser, int type_Id, int count) {
//        this.useridUser = useridUser;
//        this.type_Id = type_Id;
//        this.count = count;
//    }
//
//    public void SetMyRuneInventoryDto(Long useridUser, String type_Id, String count) {
//        this.useridUser = useridUser;
//        this.type_Id = Integer.parseInt(type_Id);
//        this.count = Integer.parseInt(count);
//    }
    public void SetMyRuneInventoryDto(Long useridUser, String code, int count, int level){
        this.useridUser = useridUser;
        this.code = code;
        this.count = count;
        this.level = level;
    }
}
