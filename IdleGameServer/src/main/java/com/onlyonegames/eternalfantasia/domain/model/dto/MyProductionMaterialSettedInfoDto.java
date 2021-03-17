package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyProductionMastery;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyProductionMaterialSettedInfo;
import lombok.Data;

@Data
public class MyProductionMaterialSettedInfoDto {
    Long userIdUser;
    String weaponMaterialCounts;   //무기 재료 갯수들
    String armorMaterialCounts;    //방어구 재료 갯수
    String helmetMaterialCounts;   //투구 재료 갯수
    String accessoryMaterialCounts;//보조 재료 갯수

    public MyProductionMaterialSettedInfo ToEntity() {
        return MyProductionMaterialSettedInfo.builder().userIdUser(userIdUser).weaponMaterialCounts(weaponMaterialCounts).armorMaterialCounts(armorMaterialCounts).helmetMaterialCounts(helmetMaterialCounts)
                .accessoryMaterialCounts(accessoryMaterialCounts).build();
    }

    public void InitFromDbData(MyProductionMaterialSettedInfo dbData) {
        this.userIdUser = dbData.getUserIdUser();
        this.weaponMaterialCounts = dbData.getWeaponMaterialCounts();
        this.armorMaterialCounts = dbData.getArmorMaterialCounts();
        this.helmetMaterialCounts = dbData.getHelmetMaterialCounts();
        this.accessoryMaterialCounts = dbData.getAccessoryMaterialCounts();
    }
}
