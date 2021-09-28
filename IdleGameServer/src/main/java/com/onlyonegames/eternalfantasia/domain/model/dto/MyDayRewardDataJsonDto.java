package com.onlyonegames.eternalfantasia.domain.model.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MyDayRewardDataJsonDto {
    List<Boolean> rewardList;
    List<Boolean> adRewardList;

    public boolean ReceiveReward(int index) {
        if (!rewardList.get(index)) {
            rewardList.set(index, true);
            return true;
        }
        else
            return false;
    }

    public boolean ReceiveADReward(int index) {
        if (!adRewardList.get(index)) {
            adRewardList.set(index, true);
            return true;
        }
        else
            return false;
    }

    public void Init() {
        List<Boolean> tempList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            tempList.add(false);
        }
        this.rewardList = tempList;
        this.adRewardList = tempList;
    }
}
