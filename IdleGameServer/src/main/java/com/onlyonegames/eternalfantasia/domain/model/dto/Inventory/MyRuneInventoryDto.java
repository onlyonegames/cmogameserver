package com.onlyonegames.eternalfantasia.domain.model.dto.Inventory;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyRuneInventory;
import lombok.Data;

@Data
public class MyRuneInventoryDto {
    Long id;
    Long useridUser;
    int rune_Id;
    String itemClass;
    int itemClassValue;
    int grade;
    int count;

    public MyRuneInventory ToEntity() {
        return MyRuneInventory.builder().useridUser(useridUser).rune_Id(rune_Id).itemClass(itemClass).itemClassValue(itemClassValue).grade(grade).count(count).build();
    }

    public void InitFromDBData(MyRuneInventory dbData){
        this.id = dbData.getId();
        this.useridUser = dbData.getUseridUser();
        this.rune_Id = dbData.getRune_Id();
        this.itemClass = dbData.getItemClass();
        this.itemClassValue = dbData.getItemClassValue();
        this.grade = dbData.getGrade();
        this.count = dbData.getCount();
    }

    public void SetMyRuneInventoryDto(Long useridUser, int rune_Id, int itemClassValue, int grade, int count) {
        this.useridUser = useridUser;
        this.rune_Id = rune_Id;
        switch(itemClassValue){
            case 1:
                this.itemClass = "D";
                break;
            case 2:
                this.itemClass = "C";
                break;
            case 3:
                this.itemClass = "B";
                break;
            case 4:
                this.itemClass = "A";
                break;
            case 5:
                this.itemClass = "S";
                break;
        }
        this.itemClassValue = itemClassValue;
        this.grade = grade;
        this.count = count;
    }
}
