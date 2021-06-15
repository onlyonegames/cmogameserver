package com.onlyonegames.eternalfantasia.domain.model.dto.Inventory;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyRuneInventory;
import lombok.Data;

@Data
public class MyRuneInventoryDto {
    Long id;
    Long useridUser;
    int type_Id;
    int count;

    public MyRuneInventory ToEntity() {
        return MyRuneInventory.builder().useridUser(useridUser).type_Id(type_Id).count(count).build();
    }

    public void InitFromDBData(MyRuneInventory dbData){
        this.id = dbData.getId();
        this.useridUser = dbData.getUseridUser();
        this.type_Id = dbData.getType_Id();
//        this.rune_Id = dbData.getRune_Id();
//        this.itemClass = dbData.getItemClass();
//        this.itemClassValue = dbData.getItemClassValue();
//        this.grade = dbData.getGrade();
        this.count = dbData.getCount();
    }

    public void SetMyRuneInventoryDto(Long useridUser, int type_Id, int count) {
        this.useridUser = useridUser;
        this.type_Id = type_Id;
        this.count = count;
    }

    public void SetMyRuneInventoryDto(Long useridUser, String type_Id, String count) {
        this.useridUser = useridUser;
        this.type_Id = Integer.parseInt(type_Id);
        this.count = Integer.parseInt(count);
    }
}
