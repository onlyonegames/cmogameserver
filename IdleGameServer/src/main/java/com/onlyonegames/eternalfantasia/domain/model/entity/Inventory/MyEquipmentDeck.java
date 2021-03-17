package com.onlyonegames.eternalfantasia.domain.model.entity.Inventory;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import com.onlyonegames.eternalfantasia.etc.EquipmentItemCategory;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MyEquipmentDeck extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long userIdUser;
    /*첫번째 덱*/
    Long firstDeckWeaponInventoryId;
    Long firstDeckArmorInventoryId;
    Long firstDeckHelmetInventoryId;
    Long firstDeckAccessoryInventoryId;
    Long firstDeckPassiveInventoryId1;
    Long firstDeckPassiveInventoryId2;
    /*두번째 덱*/
    Long secondDeckWeaponInventoryId;
    Long secondDeckArmorInventoryId;
    Long secondDeckHelmetInventoryId;
    Long secondDeckAccessoryInventoryId;
    Long secondDeckPassiveInventoryId1;
    Long secondDeckPassiveInventoryId2;
    /*세번째 덱*/
    Long thirdDeckWeaponInventoryId;
    Long thirdDeckArmorInventoryId;
    Long thirdDeckHelmetInventoryId;
    Long thirdDeckAccessoryInventoryId;
    Long thirdDeckPassiveInventoryId1;
    Long thirdDeckPassiveInventoryId2;
    int currentUseDeckNo;/*현재 사용중인 덱 번호 : 1 ~ 3 까지*/
    @Builder
    public MyEquipmentDeck(Long userIdUser) {
        this.userIdUser = userIdUser;
        Init();
    }

    public void Init() {
        this.firstDeckWeaponInventoryId = 0L;
        this.firstDeckArmorInventoryId = 0L;
        this.firstDeckHelmetInventoryId = 0L;
        this.firstDeckAccessoryInventoryId = 0L;
        this.secondDeckWeaponInventoryId = 0L;
        this.secondDeckArmorInventoryId = 0L;
        this.secondDeckHelmetInventoryId = 0L;
        this.secondDeckAccessoryInventoryId = 0L;
        this.thirdDeckWeaponInventoryId = 0L;
        this.thirdDeckArmorInventoryId = 0L;
        this.thirdDeckHelmetInventoryId = 0L;
        this.thirdDeckAccessoryInventoryId = 0L;
        this.firstDeckPassiveInventoryId1 = 0L;
        this.firstDeckPassiveInventoryId2 = 0L;
        this.secondDeckPassiveInventoryId1 = 0L;
        this.secondDeckPassiveInventoryId2 = 0L;
        this.thirdDeckPassiveInventoryId1 = 0L;
        this.thirdDeckPassiveInventoryId2 = 0L;
        currentUseDeckNo = 1;
    }

    public void Equip(int deckNo, Long inventoryId, EquipmentItemCategory itemCategory) {
        if(itemCategory == EquipmentItemCategory.WEAPON) {
            if(deckNo == 1)
            {
                firstDeckWeaponInventoryId = inventoryId;
            }
            else if(deckNo == 2)
            {
                secondDeckWeaponInventoryId = inventoryId;
            }
            else if(deckNo == 3)
            {
                thirdDeckWeaponInventoryId = inventoryId;
            }
            return;
        }
        if(itemCategory == EquipmentItemCategory.ARMOR) {
            if(deckNo == 1)
            {
                firstDeckArmorInventoryId = inventoryId;
            }
            else if(deckNo == 2)
            {
                secondDeckArmorInventoryId = inventoryId;
            }
            else if(deckNo == 3)
            {
                thirdDeckArmorInventoryId = inventoryId;
            }
            return;
        }
        if(itemCategory == EquipmentItemCategory.HELMET) {
            if(deckNo == 1)
            {
                firstDeckHelmetInventoryId = inventoryId;
            }
            else if(deckNo == 2)
            {
                secondDeckHelmetInventoryId = inventoryId;
            }
            else if(deckNo == 3)
            {
                thirdDeckHelmetInventoryId = inventoryId;
            }
            return;
        }
        if(itemCategory == EquipmentItemCategory.ACCESSORY) {
            if(deckNo == 1)
            {
                firstDeckAccessoryInventoryId = inventoryId;
            }
            else if(deckNo == 2)
            {
                secondDeckAccessoryInventoryId = inventoryId;
            }
            else if(deckNo == 3)
            {
                thirdDeckAccessoryInventoryId = inventoryId;
            }
            return;
        }
    }

    public void Equip(int deckNo, Long inventoryId, String equipmentItemKind) {
        if(equipmentItemKind.equals("Sword")
                || equipmentItemKind.equals("Spear")
                || equipmentItemKind.equals("Bow")
                || equipmentItemKind.equals("Gun")
                || equipmentItemKind.equals("Wand")) {
            Equip(deckNo, inventoryId, EquipmentItemCategory.WEAPON);
            return;
        }
        if(equipmentItemKind.equals("Armor")) {
            Equip(deckNo, inventoryId, EquipmentItemCategory.ARMOR);
            return;
        }
        if(equipmentItemKind.equals("Helmet")) {
            Equip(deckNo, inventoryId, EquipmentItemCategory.HELMET);
            return;
        }
        if(equipmentItemKind.equals("Accessory")) {
            Equip(deckNo, inventoryId, EquipmentItemCategory.ACCESSORY);
            return;
        }
    }

    public void PassiveItemEquip(int deckNo, Long inventoryId, int passiveSlotIndex) {
        if(passiveSlotIndex < 0) {
            if(deckNo == 1)
            {
                if(firstDeckPassiveInventoryId1 == 0)
                    firstDeckPassiveInventoryId1 = inventoryId;
                else if(firstDeckPassiveInventoryId2 == 0)
                    firstDeckPassiveInventoryId2 = inventoryId;
                return;
            }
            if(deckNo == 2)
            {
                if(secondDeckPassiveInventoryId1 == 0)
                    secondDeckPassiveInventoryId1 = inventoryId;
                else if(secondDeckPassiveInventoryId2 == 0)
                    secondDeckPassiveInventoryId2 = inventoryId;
                return;
            }
            if(deckNo == 3)
            {
                if(thirdDeckPassiveInventoryId1 == 0)
                    thirdDeckPassiveInventoryId1 = inventoryId;
                else if(thirdDeckPassiveInventoryId2 == 0)
                    thirdDeckPassiveInventoryId2 = inventoryId;
                return;
            }
            return;
        }
        if(deckNo == 1)
        {
            if(passiveSlotIndex == 0)
                firstDeckPassiveInventoryId1 = inventoryId;
            else if(passiveSlotIndex == 1)
                firstDeckPassiveInventoryId2 = inventoryId;
            return;
        }
        if(deckNo == 2)
        {
            if(passiveSlotIndex == 0)
                secondDeckPassiveInventoryId1 = inventoryId;
            else if(passiveSlotIndex == 1)
                secondDeckPassiveInventoryId2 = inventoryId;
            return;
        }
        if(deckNo == 3)
        {
            if(passiveSlotIndex == 0)
                thirdDeckPassiveInventoryId1 = inventoryId;
            else if(passiveSlotIndex == 1)
                thirdDeckPassiveInventoryId2 = inventoryId;
            return;
        }
    }

    public void PassiveItemUnEquip(int deckNo, Long inventoryId) {
        if(deckNo == 1)
        {
            if(inventoryId.equals(firstDeckPassiveInventoryId1))
                firstDeckPassiveInventoryId1 = 0L;
            else if(inventoryId.equals(firstDeckPassiveInventoryId2))
                firstDeckPassiveInventoryId2 = 0L;
            return;
        }
        if(deckNo == 2)
        {
            if(inventoryId.equals(secondDeckPassiveInventoryId1))
                secondDeckPassiveInventoryId1 = 0L;
            else if(inventoryId.equals(secondDeckPassiveInventoryId2))
                secondDeckPassiveInventoryId2 = 0L;
            return;
        }
        if(deckNo == 3)
        {
            if(inventoryId.equals(thirdDeckPassiveInventoryId1))
                thirdDeckPassiveInventoryId1 = 0L;
            else if(inventoryId.equals(thirdDeckPassiveInventoryId2))
                thirdDeckPassiveInventoryId2 = 0L;
            return;
        }
    }

    public void UnEquip(int deckNo, String equipmentItemKind) {
        if(equipmentItemKind.equals("Sword")
                || equipmentItemKind.equals("Spear")
                || equipmentItemKind.equals("Bow")
                || equipmentItemKind.equals("Gun")
                || equipmentItemKind.equals("Wand")) {
            if(deckNo == 1)
            {
                firstDeckWeaponInventoryId = 0L;
            }
            else if(deckNo == 2)
            {
                secondDeckWeaponInventoryId = 0L;
            }
            else if(deckNo == 3)
            {
                thirdDeckWeaponInventoryId = 0L;
            }
        }
        else if(equipmentItemKind.equals("Armor")) {
            if(deckNo == 1)
            {
                firstDeckArmorInventoryId = 0L;
            }
            else if(deckNo == 2)
            {
                secondDeckArmorInventoryId = 0L;
            }
            else if(deckNo == 3)
            {
                thirdDeckArmorInventoryId = 0L;
            }
        }
        else if(equipmentItemKind.equals("Helmet")) {
            if(deckNo == 1)
            {
                firstDeckHelmetInventoryId = 0L;
            }
            else if(deckNo == 2)
            {
                secondDeckHelmetInventoryId = 0L;
            }
            else if(deckNo == 3)
            {
                thirdDeckHelmetInventoryId = 0L;
            }
        }
        else if(equipmentItemKind.equals("Accessory")) {
            if(deckNo == 1)
            {
                firstDeckAccessoryInventoryId = 0L;
            }
            else if(deckNo == 2)
            {
                secondDeckAccessoryInventoryId = 0L;
            }
            else if(deckNo == 3)
            {
                thirdDeckAccessoryInventoryId = 0L;
            }
        }
    }

    public void AllUnEquip(int deckNo) {
        if(deckNo == 1) {
            firstDeckWeaponInventoryId = 0L;
            firstDeckArmorInventoryId = 0L;
            firstDeckHelmetInventoryId = 0L;
            firstDeckAccessoryInventoryId = 0L;
            firstDeckPassiveInventoryId1 = 0L;
            firstDeckPassiveInventoryId2 = 0L;
            return;
        }
        if(deckNo == 2) {
            secondDeckWeaponInventoryId = 0L;
            secondDeckArmorInventoryId = 0L;
            secondDeckHelmetInventoryId = 0L;
            secondDeckAccessoryInventoryId = 0L;
            secondDeckPassiveInventoryId1 = 0L;
            secondDeckPassiveInventoryId2 = 0L;
            return;
        }
        if(deckNo == 3) {
            thirdDeckWeaponInventoryId = 0L;
            thirdDeckArmorInventoryId = 0L;
            thirdDeckHelmetInventoryId = 0L;
            thirdDeckAccessoryInventoryId = 0L;
            thirdDeckPassiveInventoryId1 = 0L;
            thirdDeckPassiveInventoryId2 = 0L;
            return;
        }
    }

    public void SetCurrentUseDeckNo(int willUseDeckNo) {
        this.currentUseDeckNo = willUseDeckNo;
    }

    public boolean IsIncludeDeckItem(Long inventoryId) {
        if(firstDeckWeaponInventoryId.equals(inventoryId) ||
                firstDeckArmorInventoryId.equals(inventoryId) ||
                firstDeckHelmetInventoryId.equals(inventoryId) ||
                firstDeckAccessoryInventoryId.equals(inventoryId) ||
                secondDeckWeaponInventoryId.equals(inventoryId) ||
                secondDeckArmorInventoryId.equals(inventoryId) ||
                secondDeckHelmetInventoryId.equals(inventoryId) ||
                secondDeckAccessoryInventoryId.equals(inventoryId) ||
                thirdDeckWeaponInventoryId.equals(inventoryId) ||
                thirdDeckArmorInventoryId.equals(inventoryId) ||
                thirdDeckHelmetInventoryId.equals(inventoryId) ||
                thirdDeckAccessoryInventoryId.equals(inventoryId)
        )
            return true;
        return false;
    }
}
