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
    @TableGenerator(name = "hibernate_sequence")
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

    public void RechargeDay() {
        this.freeDiamond = 1;
        this.todayPackage = 1;
        this.soulStonePackage = 1;
    }

    public void RechargeWeek() {
        this.weeklyPackage = 3;
    }

    public void RechargeMonth() {
        this.monthlyPackage = 2;
    }
}
