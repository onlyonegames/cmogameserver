package com.onlyonegames.eternalfantasia.domain.model.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MyLevelRewardDataJsonDto {
    public static class LevelReward{
        public List<Boolean> rewardList;
        public boolean passPurchase;
        public List<Boolean> passRewardList;

        public boolean ReceiveReward ( int index){
            if (!rewardList.get(index)) {
                rewardList.set(index, true);
                return true;
            }
            else
                return false;
        }

        public boolean ReceivePassReward ( int index){
            if (!passRewardList.get(index)) {
                passRewardList.set(index, true);
                return true;
            }
            else
                return false;
        }

        public void Init () {
            List<Boolean> tempList = new ArrayList<>();
            for(int i = 0; i < 30; i++) {
                tempList.add(false);
            }
            this.rewardList = tempList;
            this.passRewardList = tempList;
            this.passPurchase = false;
        }
    }

    public List<LevelReward> levelRewardList;

    public void Init() {
        List<LevelReward> tempList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            LevelReward levelReward = new LevelReward();
            levelReward.Init();
            tempList.add(levelReward);
        }
        this.levelRewardList = tempList;
    }
}
