package com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass;

import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyEternalPassMissionsDataDto {
    Long id;
    Long useridUser;
    String json_saveDataValue;/*업적 상황 저장 데이터*/
    LocalDateTime lastDailyMissionClearTime;/*일일 업적 클리어 시간*/
    LocalDateTime lastWeeklyMissionClearTime;/*주간 업적 클리어 시간*/

    public MyEternalPassMissionsData ToEntity() {
        return MyEternalPassMissionsData.builder().useridUser(useridUser).json_saveDataValue(json_saveDataValue).lastDailyMissionClearTime(lastDailyMissionClearTime).lastWeeklyMissionClearTime(lastWeeklyMissionClearTime).build();
    }
}
