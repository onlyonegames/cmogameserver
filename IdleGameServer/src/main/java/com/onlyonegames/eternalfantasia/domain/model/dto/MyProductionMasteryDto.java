package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyProductionMastery;
import lombok.Data;

@Data
public class MyProductionMasteryDto {
    Long userIdUser;
    int masteryLevel;
    int remainLevel;
    String openedGifts;
    long masteryCycle;

    public MyProductionMastery ToEntity() {
        return MyProductionMastery.builder().userIdUser(userIdUser).build();
    }

    public void InitFromDbData(MyProductionMastery dbData) {
        this.userIdUser = dbData.getUserIdUser();
        this.masteryLevel = dbData.getMasteryLevel();
        this.remainLevel = dbData.getRemainLevel();
        this.openedGifts = dbData.getOpenedGifts();
        this.masteryCycle = dbData.getMasteryCycle();
    }
}
