package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyActiveSkillData;
import lombok.Data;

@Data
public class MyActiveSkillDataDto {
    Long id;
    String json_saveDataValue;
    Long useridUser;

    public MyActiveSkillData ToEntity() {
        return MyActiveSkillData.builder().json_saveDataValue(json_saveDataValue).useridUser(useridUser).build();
    }
}
