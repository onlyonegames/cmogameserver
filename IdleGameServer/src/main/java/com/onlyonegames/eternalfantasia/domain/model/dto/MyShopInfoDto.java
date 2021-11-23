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
    Long useridUser;

    public MyShopInfo ToEntity() {
        Init();
        return MyShopInfo.builder().freeDiamond(freeDiamond).todayPackage(todayPackage).ancientCrystalPackage(ancientCrystalPackage).alchemyPackage(alchemyPackage)
                .buffPackage(buffPackage).specUpPackage(specUpPackage).skillAwakeningPackage(skillAwakeningPackage).soulStonePackage(soulStonePackage).adRemovePackage(adRemovePackage)
                .itemAD(itemAD).expAD(expAD).goldAD(goldAD).soulStoneAD(soulStoneAD).speedAD(speedAD).useridUser(useridUser).build();
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
    }
}
