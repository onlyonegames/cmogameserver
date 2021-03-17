package com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MyInventoryInfoDto {
    public int diamond;
    public int gold;
    public int linkforcePoint;
    public int arenaCoin;
    public int freeFieldDungeonTicket;
    public int lowDragonScale;
    public int middleDragonScale;
    public int highDragonScale;     //user table에 존재함
    public List<InventoryItem> inventoryItems; //BelongingInventory
    public List<EquipmentItem> equipmentItems; //HeroEquipmentInventory

    public void setMyInventoryInfo(int diamond, int gold, int linkforcePoint, int arenaCoin, int freeFieldDungeonTicket,
                                   int lowDragonScale, int middleDragonScale, int highDragonScale,
                                   List<InventoryItem> inventoryItems,
                                   List<EquipmentItem> EquipmentItems){
        this.diamond = diamond;
        this.gold = gold;
        this.linkforcePoint = linkforcePoint;
        this.arenaCoin = arenaCoin;
        this.freeFieldDungeonTicket = freeFieldDungeonTicket;
        this.lowDragonScale = lowDragonScale;
        this.middleDragonScale = middleDragonScale;
        this.highDragonScale = highDragonScale;
        this.inventoryItems = inventoryItems;
        this.equipmentItems = EquipmentItems;
    }
    public static class InventoryItem{
        public String name;
        public int count;
        public int itemId;
        public Long itemType;
        public LocalDateTime modifiedDate;

        public void SetInventoryItem(String name, int count, int itemId, Long itemType, LocalDateTime modifiedDate){
            this.name = name;
            this.count = count;
            this.itemId = itemId;
            this.itemType = itemType;
            this.modifiedDate = modifiedDate;
        }
    }

    public static class EquipmentItem{
        public String name;
        public String kind;
        public String grade;
        public String itemClass;
        public int level;
        public int exp;
        public int itemId;
        public int setInfo;
        public Long inventoryId;
        public LocalDateTime createdDate;
        public List<EquipmentOptionDto> equipmentDefaultDtoList;
        public List<EquipmentOptionDto> equipmentOptionDtoList;

        public void SetEquipmentItem(String name, String kind, String grade, String itemClass,
                                     int level, int exp, int itemId, int setInfo, Long inventoryId,
                                     LocalDateTime createdDate, List<EquipmentOptionDto> equipmentDefaultDtoList,
                                     List<EquipmentOptionDto> equipmentOptionDtoList){
            switch(kind) {
                case "Sword":
                    this.kind = "검";
                    break;
                case "Spear":
                    this.kind = "창";
                    break;
                case "Bow":
                    this.kind = "활";
                    break;
                case "Gun":
                    this.kind = "총";
                    break;
                case "Wand":
                    this.kind = "지팡이";
                    break;
                case "Helmet":
                    this.kind = "투구";
                    break;
                case "Armor":
                    this.kind = "갑옷";
                    break;
                case "Accessory":
                    this.kind = "악세사리";
                    break;
            }
            switch(grade){
                case "Normal":
                    this.grade = "일반";
                    break;
                case "Rare":
                    this.grade = "희귀";
                    break;
                case "Hero":
                    this.grade = "영웅";
                    break;
                case "Legend":
                    this.grade = "전설";
                    break;
                case "Divine":
                    this.grade = "신성";
                    break;
                case "Ancient":
                    this.grade = "고대";
                    break;
            }
            this.name = name;
            this.itemClass = itemClass;
            this.level = level;
            this.exp = exp;
            this.itemId = itemId;
            this.setInfo = setInfo;
            this.inventoryId = inventoryId;
            this.createdDate = createdDate;
            this.equipmentDefaultDtoList = equipmentDefaultDtoList;
            this.equipmentOptionDtoList = equipmentOptionDtoList;
        }
    }
}
