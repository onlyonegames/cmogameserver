package com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.HeroEquipmentsTable;
import lombok.Data;

import java.util.List;

@Data
public class HeroCalculatedDto {
    public List<Equipment> selectedItem;

    public static class Equipment{
        public float defaultAbilityValue;
        public float secondAbilityValue;
        public List<Integer> optionIds;
        public List<Float> optionValues;
        public String kind;
        public String secondkind;
        public int setInfo;

        public void setHeroCaluculatedDto(HeroEquipmentInventory heroEquipmentInventory, HeroEquipmentsTable heroEquipmentsTable) {
            this.defaultAbilityValue = heroEquipmentInventory.getDecideDefaultAbilityValue();
            this.secondAbilityValue = heroEquipmentInventory.getDecideSecondAbilityValue();
            String[] optionTemp = heroEquipmentInventory.getOptionIds().split(",");
            for(String temp:optionTemp){
                this.optionIds.add(Integer.parseInt(temp));
            }
            String[] optionValueTemp = heroEquipmentInventory.getOptionValues().split(",");
            for(String temp:optionValueTemp){
                this.optionValues.add(Float.parseFloat(temp));
            }
            this.kind = heroEquipmentsTable.getKind();
            this.secondkind = heroEquipmentsTable.getSecondKind();
            this.setInfo = heroEquipmentsTable.getSetInfo();
        }
    }


    public int GetSameSetOptionCount(Equipment checkItem)
    {
        int sameCount = 0;
        String type = checkItem.kind;

        int Count = selectedItem.size();
        for(int i = 0; i < Count; i++)
        {
            Equipment equipment = selectedItem.get(i);
            if (equipment == null)
                continue;

            // 대상과 같은 타입인가?
            if (type.equals(equipment.kind))
                continue;
            //셋 옵션 0번은 옵션이 없는것
            if (equipment.setInfo == 0)
                continue;

            if (!(equipment.setInfo == checkItem.setInfo))
                continue;

            sameCount++;
        }

        return sameCount;
    }

    // 공명(셋트 옵션) 상승치
    public float GetSameSetOptionValue(Equipment checkItem)
    {
        int sameCount = GetSameSetOptionCount(checkItem); // 나 빼고 값
        //if (sameCount == 0 || sameCount == 2) // sameCount == 2 : 3개임
        //    return 0.0f;

        if (sameCount == 1)
            return 0.10f;
        if (sameCount == 2)
            return 0.15f;
        else if (sameCount == 3)
            return 0.30f;

        return 0.0f;
    }
}
