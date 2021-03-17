package com.onlyonegames.eternalfantasia.domain.model.dto.CostumeDto;

import java.util.List;

public class CostumeDtosList {
    public static class CostumeDto {
        public int costumeId;
        public boolean isEquip;
        public boolean hasBuy;
        public void Equip() {
            isEquip = true;
        }

        public void UnEquip() {
            isEquip = false;
        }

        public boolean Buy() {
            if(hasBuy)
                return false;
            hasBuy = true;
            return true;
        }
        //운영툴에서 사용하는 기능
        public void ChangeHasBuy(boolean change) {this.hasBuy = change;}
    }

    public List<CostumeDto> hasCostumeIdList;
}
