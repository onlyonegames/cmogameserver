package com.onlyonegames.eternalfantasia.domain.model.entity;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class MyShopInfo extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence", allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    public int freeDiamond;
    public int todayPackage;
    public int startPackage;
    public int warriorPackage;
    public int thiefPackage;
    public int knightPackage;
    public int archerPackage;
    public int magicianPackage;
    public int soulStonePackage;
    public int weeklyPackage;
    public int monthlyPackage;
    public int adRemovePackage;
    public int itemAD;
    public int expAD;
    public int goldAD;
    public int soulStoneAD;
    public int speedAD;
    Long useridUser;

    public boolean BuyFreeDiamond() {
        if (this.freeDiamond<1)
            return false;
        this.freeDiamond -= 1;
        return true;
    }
    public boolean BuyTodayPackage() {
        if (this.todayPackage<1)
            return false;
        this.todayPackage -= 1;
        return true;
    }
    public boolean BuyStartPackage() {
        if (this.startPackage<1)
            return false;
        this.startPackage -= 1;
        return true;
    }
    public boolean BuyWarriorPackage() {
        if (this.warriorPackage<1)
            return false;
        this.warriorPackage -= 1;
        return true;
    }
    public boolean BuyThiefPackage() {
        if (this.thiefPackage<1)
            return false;
        this.thiefPackage -= 1;
        return true;
    }
    public boolean BuyKnightPackage() {
        if (this.knightPackage<1)
            return false;
        this.knightPackage -= 1;
        return true;
    }
    public boolean BuyArcherPackage() {
        if (this.archerPackage<1)
            return false;
        this.archerPackage -= 1;
        return true;
    }
    public boolean BuyMagicianPackage() {
        if (this.magicianPackage<1)
            return false;
        this.magicianPackage -= 1;
        return true;
    }
    public boolean BuySoulStonePackage() {
        if (this.soulStonePackage<1)
            return false;
        this.soulStonePackage -= 1;
        return true;
    }
    public boolean BuyWeeklyPackage() {
        if (this.weeklyPackage<1)
            return false;
        this.weeklyPackage -= 1;
        return true;
    }
    public boolean BuyMonthlyPackage() {
        if (this.monthlyPackage<1)
            return false;
        this.monthlyPackage -= 1;
        return true;
    }
    public boolean BuyAdRemovePackage() {
        if (this.adRemovePackage<1)
            return false;
        this.adRemovePackage -= 1;
        return true;
    }
    public boolean BuyItemAD() {
        if (this.itemAD<1)
            return false;
        this.itemAD -= 1;
        return true;
    }
    public boolean BuyExpAD() {
        if (this.expAD<1)
            return false;
        this.expAD -= 1;
        return true;
    }
    public boolean BuyGoldAD() {
        if (this.goldAD<1)
            return false;
        this.goldAD -= 1;
        return true;
    }
    public boolean BuySoulStoneAD() {
        if (this.soulStoneAD<1)
            return false;
        this.soulStoneAD -= 1;
        return true;
    }
    public boolean BuySpeedAD() {
        if (this.speedAD<1)
            return false;
        this.speedAD -= 1;
        return true;
    }

    public void RechargeDay() {
        this.freeDiamond = 1;
        this.todayPackage = 3;
        this.soulStonePackage = 3;
        this.itemAD = 3;
        this.expAD = 3;
        this.goldAD = 3;
        this.soulStoneAD = 3;
        this.speedAD = 3;
    }

    public void RechargeWeek() {
        this.weeklyPackage = 3;
    }

    public void RechargeMonth() {
        this.monthlyPackage = 2;
    }
}
