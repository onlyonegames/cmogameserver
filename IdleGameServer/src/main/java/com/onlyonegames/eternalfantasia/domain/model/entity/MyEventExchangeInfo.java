package com.onlyonegames.eternalfantasia.domain.model.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class MyEventExchangeInfo {
    @Id
    @TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    public int goldCount; //일반 골드
    public int advancedGoldCount; //고급 골드
    public int soulStoneCount; //일반 영혼석
    public int advancedSoulStoneCount; //고급 영혼석
    public int fragmentCount; // 증폭파편
    public int dungeonTicketCount; //일반 던전티켓
    public int advancedDungeonTicketCount; //고급 던전티켓
    public int crystalCount; //고대결정
    public int randomBasicAccessoryCount; //일반 장신구
    public int randomNewAccessoryCount; // 연금 장신구
    public int legendClassCount; //전설 클래스
    public int divineClassCount; //신화 클래스
    public int costumeACount; // 코스튬 A
    public int costumeBCount; //코스튬 B
    public int divineRandomBEquipment; //신화 B 장비
    public int divineRandomDRune; //신화 D 보석
    public int randomOrb; //랜덤 오브
    public int ancientRandomBEquipment; //고대 B 장비

    public void RechargeDay() {
        this.goldCount = 2;
        this.advancedGoldCount = 2;
        this.soulStoneCount = 2;
        this.advancedSoulStoneCount = 5;
        this.dungeonTicketCount = 10;
        this.fragmentCount = 3;
        this.crystalCount = 3;
    }

    public void RechargeWeek() {
        this.legendClassCount = 8;
        this.divineRandomBEquipment = 10;
        this.divineRandomDRune = 10;
        this.randomBasicAccessoryCount = 20;
        this.randomOrb = 10;
        this.advancedDungeonTicketCount = 5;
    }

    public boolean BuyGoldCount() {
        if (this.goldCount < 1)
            return false;
        this.goldCount -= 1;
        return true;
    }

    public boolean BuyAdvancedGoldCount() {
        if (this.advancedGoldCount < 1)
            return false;
        this.advancedGoldCount -= 1;
        return true;
    }

    public boolean BuySoulStoneCount() {
        if (this.soulStoneCount < 1)
            return false;
        this.soulStoneCount -= 1;
        return true;
    }

    public boolean BuyAdvancedSoulStoneCount() {
        if (this.advancedSoulStoneCount < 1)
            return false;
        this.advancedSoulStoneCount -= 1;
        return true;
    }

    public boolean BuyFragmentCount() {
        if (this.fragmentCount < 1)
            return false;
        this.fragmentCount -= 1;
        return true;
    }

    public boolean BuyDungeonTicketCount() {
        if (this.dungeonTicketCount < 1)
            return false;
        this.dungeonTicketCount -= 1;
        return true;
    }

    public boolean BuyAdvancedDungeonTicketCount() {
        if (this.advancedDungeonTicketCount < 1)
            return false;
        this.advancedDungeonTicketCount -= 1;
        return true;
    }

    public boolean BuyCrystalCount() {
        if (this.crystalCount < 1)
            return false;
        this.crystalCount -= 1;
        return true;
    }

    public boolean BuyLegendClassCount() {
        if (this.legendClassCount < 1)
            return false;
        this.legendClassCount -= 1;
        return true;
    }

    public boolean BuyDivineClassCount() {
        if (this.divineClassCount < 1)
            return false;
        this.divineClassCount -= 1;
        return true;
    }

    public boolean BuyCostumeACount() {
        if (this.costumeACount < 1)
            return false;
        this.costumeACount -= 1;
        return true;
    }

    public boolean BuyCostumeBCount() {
        if (this.costumeBCount < 1)
            return false;
        this.costumeBCount -= 1;
        return true;
    }

    public boolean BuyDivineRandomBEquipment() {
        if (this.divineRandomBEquipment < 1)
            return false;
        this.divineRandomBEquipment -= 1;
        return true;
    }

    public boolean BuyDivineRandomDRune() {
        if (this.divineRandomDRune < 1)
            return false;
        this.divineRandomDRune -= 1;
        return true;
    }

    public boolean BuyRandomBasicAccessory() {
        if (this.randomBasicAccessoryCount < 1)
            return false;
        this.randomBasicAccessoryCount -= 1;
        return true;
    }

    public boolean BuyAncientRandomBEquipment() {
        if (this.ancientRandomBEquipment < 1)
            return false;
        this.ancientRandomBEquipment -= 1;
        return true;
    }

    public boolean BuyRandomNewAccessory() {
        if (this.randomNewAccessoryCount < 1)
            return false;
        this.randomNewAccessoryCount -= 1;
        return true;
    }

    public boolean BuyRandomOrb() {
        if (this.randomOrb < 1)
            return false;
        this.randomOrb -= 1;
        return true;
    }
}
