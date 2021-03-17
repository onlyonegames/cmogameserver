package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyTodayViewingTable;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyTodayViewingDto {
    Long id;
    Long useridUser;
    int todayViewingCount;
    int maxViewing;
    LocalDateTime lastViewing;

    public void SetTodayViewing(MyTodayViewingTable todayViewingTable, int maxViewing){
        this.id = todayViewingTable.getId();
        this.useridUser = todayViewingTable.getUseridUser();
        this.todayViewingCount = todayViewingTable.getTodayViewingCount();
        this.maxViewing = maxViewing;
        this.lastViewing = todayViewingTable.getLastViewing();
    }

    public MyTodayViewingTable ToEntity() {
        return MyTodayViewingTable.builder().useridUser(useridUser).todayViewingCount(todayViewingCount).lastViewing(lastViewing).build();
    }
}