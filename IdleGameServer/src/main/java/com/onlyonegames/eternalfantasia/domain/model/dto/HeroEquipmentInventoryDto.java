package com.onlyonegames.eternalfantasia.domain.model.dto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class HeroEquipmentInventoryDto {
    Long id;
    Long useridUser;
    int item_Id;
    int itemClassValue;
    //2019-10-09. 기획 수정 각 장비에 기본 능력이 하나씩 이었던 것을 기본 능력과 보조 능력 두가지로 추가함.
    float decideDefaultAbilityValue;
    float decideSecondAbilityValue;
    int level;
    int maxLevel;
    int exp;
    int nextExp;
    String itemClass;
    String optionIds;
    String optionValues;
    public HeroEquipmentInventory ToEntity() {
        return HeroEquipmentInventory.builder().useridUser(useridUser).itemClass(itemClass).item_Id(item_Id).itemClassValue(itemClassValue)
                .decideDefaultAbilityValue(decideDefaultAbilityValue).decideSecondAbilityValue(decideSecondAbilityValue).optionIds(optionIds).optionValues(optionValues).level(level).maxLevel(maxLevel).exp(exp).nextExp(nextExp).build();
    }
    public void InitFromDbData(HeroEquipmentInventory dbData) {
        id = dbData.getId();
        useridUser = dbData.getUseridUser();
        item_Id = dbData.getItem_Id();
        itemClassValue = dbData.getItemClassValue();
        decideDefaultAbilityValue = dbData.getDecideDefaultAbilityValue();
        decideSecondAbilityValue = dbData.getDecideSecondAbilityValue();
        level = dbData.getLevel();
        maxLevel = dbData.getMaxLevel();
        exp = dbData.getExp();
        nextExp = dbData.getNextExp();
        itemClass = dbData.getItemClass();
        optionIds = dbData.getOptionIds();
        optionValues = dbData.getOptionValues();
    }
}