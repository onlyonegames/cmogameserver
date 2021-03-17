package com.onlyonegames.eternalfantasia.domain.controller.managementtool;

import lombok.Data;

@Data
public class CostumeDto {
    public int costumeId;
    public String name;
    public boolean isEquip;
    public boolean hasBuy;

    public void setCostumeDto(int costumeId, String name, boolean isEquip, boolean hasBuy) {
        this.costumeId = costumeId;
        this.name = name;
        this.isEquip = isEquip;
        this.hasBuy = hasBuy;
    }
}
