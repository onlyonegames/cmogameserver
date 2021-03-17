package com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool;

import com.onlyonegames.eternalfantasia.domain.model.dto.HeroEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.HeroEquipmentsTable;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EquipmentCalculatedDto {

    List<EquipmentInfo> equipmentInfoList;

    public static class EquipmentInfo {
        public float decideDefaultAbilityValue;
        public float decideSecondAbilityValue;
        public List<Integer> optionIdList;
        public List<Double> optionValueList;
        public String kind;
        public String secondKind;
        public int setInfo;

        public void setEquipmentInfo(HeroEquipmentInventory heroEquipmentInventory, HeroEquipmentsTable heroEquipmentsTable){
            this.decideDefaultAbilityValue = heroEquipmentInventory.getDecideDefaultAbilityValue();
            this.decideSecondAbilityValue = heroEquipmentInventory.getDecideSecondAbilityValue();
            String[] optionTemp = heroEquipmentInventory.getOptionIds().split(",");
            List<Integer> optionTempList = new ArrayList<>();
            if(heroEquipmentInventory.getOptionIds().length() != 0){
                for(String temp:optionTemp){
                    optionTempList.add(Integer.parseInt(temp));
                }
            }
            this.optionIdList = optionTempList;
            String[] optionValueTemp = heroEquipmentInventory.getOptionValues().split(",");
            List<Double> optionValueTempList = new ArrayList<>();
            if(heroEquipmentInventory.getOptionValues().length() != 0){
                for(String temp:optionValueTemp){
                    optionValueTempList.add(Double.parseDouble(temp));
                }
            }
            this.optionValueList = optionValueTempList;
            this.kind = heroEquipmentsTable.getKind();
            this.secondKind = heroEquipmentsTable.getSecondKind();
            this.setInfo = heroEquipmentsTable.getSetInfo();
        }

        public void setEquipmentInfo(HeroEquipmentInventoryDto heroEquipmentInventory, HeroEquipmentsTable heroEquipmentsTable){
            this.decideDefaultAbilityValue = heroEquipmentInventory.getDecideDefaultAbilityValue();
            this.decideSecondAbilityValue = heroEquipmentInventory.getDecideSecondAbilityValue();
            String[] optionTemp = heroEquipmentInventory.getOptionIds().split(",");
            List<Integer> optionTempList = new ArrayList<>();
            if(heroEquipmentInventory.getOptionIds().length() != 0){
                for(String temp:optionTemp){
                    optionTempList.add(Integer.parseInt(temp));
                }
            }
            this.optionIdList = optionTempList;
            String[] optionValueTemp = heroEquipmentInventory.getOptionValues().split(",");
            List<Double> optionValueTempList = new ArrayList<>();
            if(heroEquipmentInventory.getOptionValues().length() != 0){
                for(String temp:optionValueTemp){
                    optionValueTempList.add(Double.parseDouble(temp));
                }
            }
            this.optionValueList = optionValueTempList;
            this.kind = heroEquipmentsTable.getKind();
            this.secondKind = heroEquipmentsTable.getSecondKind();
            this.setInfo = heroEquipmentsTable.getSetInfo();
        }
    }
    public int GetSameSetOptionCount(EquipmentInfo checkItem)
    {
        int sameCount = 0;
        String type = checkItem.kind;

        int Count = equipmentInfoList.size();
        for(int i = 0; i < Count; i++)
        {
            EquipmentInfo equipment = equipmentInfoList.get(i);
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
    public float GetSameSetOptionValue(EquipmentInfo checkItem)
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

    public void setEquipmentInfoList(List<EquipmentInfo> equipmentInfoList){
        this.equipmentInfoList = equipmentInfoList;
    }
}
