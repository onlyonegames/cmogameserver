package com.onlyonegames.eternalfantasia.domain.model.dto.ShopDto;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Shop.MyShopInfo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyShopInfoDto {
    Long id;
    Long useridUser;
    String json_myShopInfos;
    /*상점 스케쥴 시간. 24시간마다 갱신*/
    LocalDateTime scheduleStartTime;
    /*일패키지 스케쥴 갱신*/
    LocalDateTime dailyPackageScheduleStartTime;
    /*주간패키지 스케쥴 갱신*/
    LocalDateTime weeklyPackageScheduleStartTime;
    /*월간패키지 스케쥴 갱신*/
    LocalDateTime monthlyPackageScheduleStartTime;
    public MyShopInfo ToEntity() {
        return MyShopInfo.builder().useridUser(useridUser).json_myShopInfos(json_myShopInfos).scheduleStartTime(scheduleStartTime).dailyPackageScheduleStartTime(dailyPackageScheduleStartTime).weeklyPackageScheduleStartTime(weeklyPackageScheduleStartTime).monthlyPackageScheduleStartTime(monthlyPackageScheduleStartTime).build();
    }
}
