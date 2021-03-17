package com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool;

import lombok.Data;

@Data
public class HeroEquipmentInfoDto {
    /*장비 필요한 value : heroequipmentatable(item_name, kind, grade), HeroEquipmentInventory(itemClass, decideDefaultAbilityValue, decideSecondAbilityValue, optionIds, optionValues, level  */
    /*인장 필요한 value : passiveitemtable(name, itemExplain, kind), HeroEquipmentInventory(itemClass) */
    public Deck firstDeck;
    public Deck secondDeck;
    public Deck thirdDeck;

    public static class ItemInfo{
        public Long inventoryId;
        public String name;
        public String kind;
        public String grade; // 여기까지 heroequipmentstable
        public String itemClass;
        public float decideDefaultAbilityValue;
        public float decideSecondAbilityValue;
        public int level;
        public int setInfo;
        public String optionIds;
        public String optionValue; // 여기까지 HeroEquipmentInventory

        public void SetItemInfo(Long inventoryId, String name, String kind, String itemClass, String grade,
                                int setInfo, float decideDefaultAbilityValue, float decideSecondAbilityValue,
                                int level, String optionIds, String optionValue) {
            this.inventoryId = inventoryId;
            this.name = name;
            this.kind = kind;
            this.itemClass = itemClass;
            this.grade = grade;
            this.setInfo = setInfo;
            this.decideDefaultAbilityValue = decideDefaultAbilityValue;
            this.decideSecondAbilityValue = decideSecondAbilityValue;
            this.level = level;
            this.optionIds = optionIds;
            this.optionValue = optionValue;
        }
    }

//    public class passiveiteminfo{
//        String name;
//        String itemExplain;
//        String kind;
//        String itemClass;
//    }

    public static class Deck{
        public ItemInfo weaponInventory;
        public ItemInfo armorInventory;
        public ItemInfo helmetInventory;
        public ItemInfo accessoryInventory;
        public ItemInfo passiveInventory1;
        public ItemInfo passiveInventory2;

        public void DeckSetting(ItemInfo weaponInventory, ItemInfo armorInventory,
                                ItemInfo helmetInventory, ItemInfo accessoryInventory,
                                ItemInfo passiveInventory1, ItemInfo passiveInventory2) {
            this.weaponInventory = weaponInventory;
            this.armorInventory = armorInventory;
            this.helmetInventory = helmetInventory;
            this.accessoryInventory = accessoryInventory;
            this.passiveInventory1 = passiveInventory1;
            this.passiveInventory2 = passiveInventory2;
        }
    }


//    public void setitemInfo(Deck firstDeck, Deck secondDeck, Deck thirdDeck){
//        this.firstDeck = firstDeck;
//        this.secondDeck = secondDeck;
//        this.thirdDeck = thirdDeck;
//    }

}
