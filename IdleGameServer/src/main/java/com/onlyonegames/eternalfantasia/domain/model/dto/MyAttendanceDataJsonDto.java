package com.onlyonegames.eternalfantasia.domain.model.dto;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class MyAttendanceDataJsonDto {
    public int gettingCount;
    public List<Boolean> rewardList;
    public boolean passPurchase;
    public List<Boolean> passRewardList;

    public boolean ReceiveReward(int index) {
        if (!rewardList.get(index)) {
            rewardList.set(index, true);
            return true;
        }
        else
            return false;
    }

    public boolean ReceivePassReward(int index) {
        if (!passRewardList.get(index)){
            passRewardList.set(index, true);
            return true;
        }
        else
            return false;
    }



    public void Init() {
        List<Boolean> tempList = new ArrayList<>();
        for(int i = 0; i <31; i++){
            tempList.add(false);
        }
        this.gettingCount = 1;
        this.rewardList = tempList;
        this.passPurchase = false;
        this.passRewardList = tempList;
    }
}
