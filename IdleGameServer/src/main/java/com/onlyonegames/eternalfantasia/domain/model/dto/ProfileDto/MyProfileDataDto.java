package com.onlyonegames.eternalfantasia.domain.model.dto.ProfileDto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyProfileData;
import lombok.Data;

@Data
public class MyProfileDataDto {
    Long id;
    Long useridUser;
    String json_saveDataValue;
    String json_missionData;

    public MyProfileData ToEntity() {
        return MyProfileData.builder().useridUser(useridUser).json_saveDataValue(json_saveDataValue).json_missionData(json_missionData).build();
    }
}
