package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyEquipmentDeck;
import lombok.Data;

@Data
public class MyEquipmentDeckDto {
    Long id;
    Long userIdUser;
    Long firstDeckWeaponInventoryId;
    Long firstDeckArmorInventoryId;
    Long firstDeckHelmetInventoryId;
    Long firstDeckAccessoryInventoryId;
    Long firstDeckPassiveInventoryId1;
    Long firstDeckPassiveInventoryId2;
    Long secondDeckWeaponInventoryId;
    Long secondDeckArmorInventoryId;
    Long secondDeckHelmetInventoryId;
    Long secondDeckAccessoryInventoryId;
    Long secondDeckPassiveInventoryId1;
    Long secondDeckPassiveInventoryId2;
    Long thirdDeckWeaponInventoryId;
    Long thirdDeckArmorInventoryId;
    Long thirdDeckHelmetInventoryId;
    Long thirdDeckAccessoryInventoryId;
    Long thirdDeckPassiveInventoryId1;
    Long thirdDeckPassiveInventoryId2;

    public MyEquipmentDeck ToEntity() {
        return MyEquipmentDeck.builder().userIdUser(userIdUser).build();
    }

    public void InitFromDbData(MyEquipmentDeck dbData) {
        this.id = dbData.getId();
        this.userIdUser = dbData.getUserIdUser();
        this.firstDeckWeaponInventoryId = dbData.getFirstDeckWeaponInventoryId();
        this.firstDeckArmorInventoryId = dbData.getFirstDeckArmorInventoryId();
        this.firstDeckHelmetInventoryId = dbData.getFirstDeckHelmetInventoryId();
        this.firstDeckAccessoryInventoryId = dbData.getFirstDeckAccessoryInventoryId();
        this.firstDeckPassiveInventoryId1 = dbData.getFirstDeckPassiveInventoryId1();
        this.firstDeckPassiveInventoryId2 = dbData.getFirstDeckPassiveInventoryId2();
        this.secondDeckWeaponInventoryId = dbData.getSecondDeckWeaponInventoryId();
        this.secondDeckArmorInventoryId = dbData.getSecondDeckArmorInventoryId();
        this.secondDeckHelmetInventoryId = dbData.getSecondDeckHelmetInventoryId();
        this.secondDeckAccessoryInventoryId = dbData.getSecondDeckAccessoryInventoryId();
        this.secondDeckPassiveInventoryId1 = dbData.getSecondDeckPassiveInventoryId1();
        this.secondDeckPassiveInventoryId2 = dbData.getSecondDeckPassiveInventoryId2();
        this.thirdDeckWeaponInventoryId = dbData.getThirdDeckWeaponInventoryId();
        this.thirdDeckArmorInventoryId = dbData.getThirdDeckArmorInventoryId();
        this.thirdDeckHelmetInventoryId = dbData.getThirdDeckHelmetInventoryId();
        this.thirdDeckAccessoryInventoryId = dbData.getThirdDeckAccessoryInventoryId();
        this.thirdDeckPassiveInventoryId1 = dbData.getThirdDeckPassiveInventoryId1();
        this.thirdDeckPassiveInventoryId2 = dbData.getThirdDeckPassiveInventoryId2();
    }
}
