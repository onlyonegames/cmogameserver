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
    @TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    public int freeDiamond;
    public int todayPackage;
    public int ancientCrystalPackage;
    public int alchemyPackage;
    public int buffPackage;
    public int specUpPackage;
    public int skillAwakeningPackage;
    public int soulStonePackage;
    public int adRemovePackage;
    public int itemAD;
    public int expAD;
    public int goldAD;
    public int soulStoneAD;
    public int speedAD;
    public int fragmentPackage;
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
    public boolean BuyAncientCrystalPackage() {
        if (this.ancientCrystalPackage<1)
            return false;
        this.ancientCrystalPackage -= 1;
        return true;
    }
    public boolean BuyAlchemyPackage() {
        if (this.alchemyPackage<1)
            return false;
        this.alchemyPackage -= 1;
        return true;
    }
    public boolean BuyBuffPackage() {
        if (this.buffPackage<1)
            return false;
        this.buffPackage -= 1;
        return true;
    }
    public boolean BuySpecUpPackage() {
        if (this.specUpPackage<1)
            return false;
        this.specUpPackage -= 1;
        return true;
    }
    public boolean BuySkillAwakeningPackage() {
        if (this.skillAwakeningPackage<1)
            return false;
        this.skillAwakeningPackage -= 1;
        return true;
    }
    public boolean BuySoulStonePackage() {
        if (this.soulStonePackage<1)
            return false;
        this.soulStonePackage -= 1;
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

    public boolean BuyFragmentPackage() {
        if (this.fragmentPackage < 1)
            return false;
        this.fragmentPackage -= 1;
        return true;
    }

    public void RechargeDay() {
        this.freeDiamond = 1;
        this.todayPackage = 3;
        this.soulStonePackage = 3;
        this.ancientCrystalPackage = 3;
        this.itemAD = 3;
        this.expAD = 3;
        this.goldAD = 3;
        this.soulStoneAD = 3;
        this.speedAD = 3;
        this.fragmentPackage = 3;
    }

    public void RechargeWeek() {
        this.alchemyPackage = 3;
        this.buffPackage = 1;
    }

    public void RechargeMonth() {

    }
}
