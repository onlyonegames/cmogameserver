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
    public int newYearBlackPackage;
    public int newYearWhitePackage;
    public int newYearYellowPackage;
    public int newYearRedPackage;
    public int newYearGrowthUpPackage1;
    public int newYearGrowthUpPackage2;
    public int newYearGrowthUpPackage3;
    public int newYearGrowthUpPackage4;
    public int newYearGrowthUpPackage5;
    public int newYearChallengePackage1;
    public int newYearChallengePackage2;
    public int newYearChallengePackage3;
    public int newYearChallengePackage4;
    public int newYearChallengePackage5;
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


    public boolean BuyNewYearBlackPackage() {
        if (this.newYearBlackPackage < 1)
            return false;
        this.newYearBlackPackage -= 1;
        return true;
    }
    public boolean BuyNewYearWhitePackage() {
        if (this.newYearWhitePackage < 1)
            return false;
        this.newYearWhitePackage -= 1;
        return true;
    }
    public boolean BuyNewYearYellowPackage() {
        if (this.newYearYellowPackage < 1)
            return false;
        this.newYearYellowPackage -= 1;
        return true;
    }
    public boolean BuyNewYearRedPackage() {
        if (this.newYearRedPackage < 1)
            return false;
        this.newYearRedPackage -= 1;
        return true;
    }
    public boolean BuyNewYearGrowthUpPackage1() {
        if (this.newYearGrowthUpPackage1 < 1)
            return false;
        this.newYearGrowthUpPackage1 -= 1;
        return true;
    }
    public boolean BuyNewYearGrowthUpPackage2() {
        if (this.newYearGrowthUpPackage2 < 1)
            return false;
        this.newYearGrowthUpPackage2 -= 1;
        return true;
    }
    public boolean BuyNewYearGrowthUpPackage3() {
        if (this.newYearGrowthUpPackage3 < 1)
            return false;
        this.newYearGrowthUpPackage3 -= 1;
        return true;
    }
    public boolean BuyNewYearGrowthUpPackage4() {
        if (this.newYearGrowthUpPackage4 < 1)
            return false;
        this.newYearGrowthUpPackage4 -= 1;
        return true;
    }
    public boolean BuyNewYearGrowthUpPackage5() {
        if (this.newYearGrowthUpPackage5 < 1)
            return false;
        this.newYearGrowthUpPackage5 -= 1;
        return true;
    }
    public boolean BuyNewYearChallengePackage1() {
        if (this.newYearChallengePackage1 < 1)
            return false;
        this.newYearChallengePackage1 -= 1;
        return true;
    }
    public boolean BuyNewYearChallengePackage2() {
        if (this.newYearChallengePackage2 < 1)
            return false;
        this.newYearChallengePackage2 -= 1;
        return true;
    }
    public boolean BuyNewYearChallengePackage3() {
        if (this.newYearChallengePackage3 < 1)
            return false;
        this.newYearChallengePackage3 -= 1;
        return true;
    }
    public boolean BuyNewYearChallengePackage4() {
        if (this.newYearChallengePackage4 < 1)
            return false;
        this.newYearChallengePackage4 -= 1;
        return true;
    }
    public boolean BuyNewYearChallengePackage5() {
        if (this.newYearChallengePackage5 < 1)
            return false;
        this.newYearChallengePackage5 -= 1;
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
        this.newYearGrowthUpPackage2 = 3;
        this.newYearGrowthUpPackage3 = 3;
        this.newYearChallengePackage2 = 3;
        this.newYearChallengePackage3 = 3;
    }

    public void RechargeWeek() {
        this.alchemyPackage = 3;
        this.buffPackage = 1;
    }

    public void RechargeMonth() {

    }
}
