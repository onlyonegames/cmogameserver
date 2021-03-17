package com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyFieldDungeonSeasonSaveData;
import lombok.Data;

@Data
public class MyFieldDungeonSeasonDataDto {
    Long id;
    Long useridUser;
    Long seasonRank;
    Long totalDamage;
    int seasonNo;
    boolean seasonReceivable;
    int purchaseNum;

    public MyFieldDungeonSeasonSaveData ToEntity() {
        return MyFieldDungeonSeasonSaveData.builder().useridUser(useridUser).seasonRank(seasonRank).totalDamage(totalDamage).seasonNo(seasonNo).purchaseNum(purchaseNum).build();
    }

    public void InitFromDbData(MyFieldDungeonSeasonSaveData dbData) {
        this.id = dbData.getId();
        this.useridUser = dbData.getUseridUser();
        this.seasonRank = dbData.getSeasonRank();
        this.totalDamage = dbData.getTotalDamage();
        this.seasonNo = dbData.getSeasonNo();
        this.seasonReceivable = dbData.isSeasonReceived();
        this.purchaseNum = dbData.getPurchaseNum();
    }
}
