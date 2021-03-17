package com.onlyonegames.eternalfantasia.domain.model.dto.Mission;

import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyMissionsDataDto {
    Long id;
    Long useridUser;
    String json_saveDataValue;
    LocalDateTime lastDailyMissionClearTime;/*일일 업적 클리어 시간*/
    LocalDateTime lastWeeklyMissionClearTime;/*주간 업적 클리어 시간*/

    public MyMissionsData ToEntity() {
        return MyMissionsData.builder().useridUser(useridUser).json_saveDataValue(json_saveDataValue).lastDailyMissionClearTime(lastDailyMissionClearTime).lastWeeklyMissionClearTime(lastWeeklyMissionClearTime).build();
    }
}
