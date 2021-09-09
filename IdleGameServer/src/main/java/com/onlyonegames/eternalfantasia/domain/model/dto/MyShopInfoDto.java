package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyShopInfo;
import lombok.Data;

@Data
public class MyShopInfoDto {
    Long id;
    int freeDiamond;
    int todayPackage;
    int startPackage;
    int warriorPackage;
    int thiefPackage;
    int knightPackage;
    int archerPackage;
    int magicianPackage;
    int soulStonePackage;
    int weeklyPackage;
    int monthlyPackage;
    int adRemovePackage;
    Long useridUser;

    public MyShopInfo ToEntity() {
        Init();
        return MyShopInfo.builder().freeDiamond(freeDiamond).todayPackage(todayPackage).startPackage(startPackage).warriorPackage(warriorPackage)
                .thiefPackage(thiefPackage).knightPackage(knightPackage).archerPackage(archerPackage).magicianPackage(magicianPackage)
                .soulStonePackage(soulStonePackage).weeklyPackage(weeklyPackage).monthlyPackage(monthlyPackage).adRemovePackage(adRemovePackage).useridUser(useridUser).build();
    }

    void Init() {
        this.freeDiamond = 1;
        this.todayPackage = 1;
        this.startPackage = 1;
        this.warriorPackage = 1;
        this.thiefPackage = 1;
        this.knightPackage = 1;
        this.archerPackage = 1;
        this.magicianPackage = 1;
        this.soulStonePackage = 1;
        this.weeklyPackage = 2;
        this.monthlyPackage = 2;
        this.adRemovePackage = 1;
    }
}
