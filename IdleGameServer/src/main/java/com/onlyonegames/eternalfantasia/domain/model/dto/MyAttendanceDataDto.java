package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyAttendanceData;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyAttendanceDataDto {
    public Long useridUser;
    public int gettingCount;
    public LocalDateTime lastAttendanceDate;
    public boolean receivableReward;

    public void SetMyAttendanceDataDto(Long userId, int gettingCount, LocalDateTime lastAttendanceDate, boolean receivableReward){
        this.useridUser = userId;
        this.gettingCount = gettingCount;
        this.lastAttendanceDate = lastAttendanceDate;
        this.receivableReward = receivableReward;
    }

    public void InitFromDbData(MyAttendanceData dbData) {
        this.useridUser = dbData.getUseridUser();
        this.gettingCount = dbData.getGettingCount();
        this.receivableReward = dbData.isReceivableReward();
    }

    public MyAttendanceData ToEntity(){
        return MyAttendanceData.builder().useridUser(useridUser).gettingCount(gettingCount).lastAttendanceDate(lastAttendanceDate).receivableReward(receivableReward).build();
    }
}
