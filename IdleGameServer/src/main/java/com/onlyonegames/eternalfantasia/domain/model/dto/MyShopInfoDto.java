package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyShopInfo;
import lombok.Data;

@Data
public class MyShopInfoDto {
    Long id;
    int freeDiamond;
    int todayPackage;
    int ancientCrystalPackage;
    int alchemyPackage;
    int buffPackage;
    int specUpPackage;
    int skillAwakeningPackage;
    int soulStonePackage;
    int adRemovePackage;
    int itemAD;
    int expAD;
    int goldAD;
    int soulStoneAD;
    int speedAD;
    int fragmentPackage;
    int newYearBlackPackage;
    int newYearWhitePackage;
    int newYearYellowPackage;
    int newYearRedPackage;
    int newYearGrowthUpPackage1;
    int newYearGrowthUpPackage2;
    int newYearGrowthUpPackage3;
    int newYearGrowthUpPackage4;
    int newYearGrowthUpPackage5;
    int newYearChallengePackage1;
    int newYearChallengePackage2;
    int newYearChallengePackage3;
    int newYearChallengePackage4;
    int newYearChallengePackage5;
    Long useridUser;

    public MyShopInfo ToEntity() {
        Init();
        return MyShopInfo.builder().freeDiamond(freeDiamond).todayPackage(todayPackage).ancientCrystalPackage(ancientCrystalPackage).alchemyPackage(alchemyPackage)
                .buffPackage(buffPackage).specUpPackage(specUpPackage).skillAwakeningPackage(skillAwakeningPackage).soulStonePackage(soulStonePackage).adRemovePackage(adRemovePackage)
                .itemAD(itemAD).expAD(expAD).goldAD(goldAD).soulStoneAD(soulStoneAD).speedAD(speedAD).fragmentPackage(fragmentPackage).newYearBlackPackage(newYearBlackPackage)
                .newYearWhitePackage(newYearWhitePackage).newYearYellowPackage(newYearYellowPackage).newYearRedPackage(newYearRedPackage).newYearGrowthUpPackage1(newYearGrowthUpPackage1)
                .newYearGrowthUpPackage2(newYearGrowthUpPackage2).newYearGrowthUpPackage3(newYearGrowthUpPackage3).newYearGrowthUpPackage4(newYearGrowthUpPackage4)
                .newYearGrowthUpPackage5(newYearGrowthUpPackage5).newYearChallengePackage1(newYearChallengePackage1).newYearChallengePackage2(newYearChallengePackage2)
                .newYearChallengePackage3(newYearChallengePackage3).newYearChallengePackage4(newYearChallengePackage4).newYearChallengePackage5(newYearChallengePackage5).useridUser(useridUser).build();
    }

    void Init() {
        this.freeDiamond = 1;
        this.todayPackage = 3;
        this.ancientCrystalPackage = 3;
        this.alchemyPackage = 3;
        this.buffPackage = 1;
        this.specUpPackage = 1;
        this.skillAwakeningPackage = 1;
        this.soulStonePackage = 3;
        this.adRemovePackage = 1;
        this.itemAD = 3;
        this.expAD = 3;
        this.goldAD = 3;
        this.soulStoneAD = 3;
        this.speedAD = 3;
        this.fragmentPackage = 3;
        this.newYearBlackPackage = 1;
        this.newYearWhitePackage = 1;
        this.newYearYellowPackage = 1;
        this.newYearRedPackage = 1;
        this.newYearGrowthUpPackage1 = 5;
        this.newYearGrowthUpPackage2 = 3;
        this.newYearGrowthUpPackage3 = 3;
        this.newYearGrowthUpPackage4 = 3;
        this.newYearGrowthUpPackage5 = 1;
        this.newYearChallengePackage1 = 5;
        this.newYearChallengePackage2 = 3;
        this.newYearChallengePackage3 = 3;
        this.newYearChallengePackage4 = 3;
        this.newYearChallengePackage5 = 1;
    }
}
