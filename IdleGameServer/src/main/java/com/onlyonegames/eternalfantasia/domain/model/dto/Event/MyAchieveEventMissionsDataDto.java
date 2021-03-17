package com.onlyonegames.eternalfantasia.domain.model.dto.Event;

import com.onlyonegames.eternalfantasia.domain.model.entity.Event.MyAchieveEventMissionsData;
import lombok.Data;

@Data
public class MyAchieveEventMissionsDataDto {
    Long id;
    Long useridUser;
    String json_saveDataValue;

    public MyAchieveEventMissionsData ToEntity() {
        return MyAchieveEventMissionsData.builder().useridUser(useridUser).json_saveDataValue(json_saveDataValue).build();
    }
}
