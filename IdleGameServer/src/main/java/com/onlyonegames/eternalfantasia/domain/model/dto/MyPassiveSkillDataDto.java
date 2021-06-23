package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyPassiveSkillData;
import lombok.Data;

@Data
public class MyPassiveSkillDataDto {
    Long id;
    String json_saveDataValue;
    Long useridUser;

    public MyPassiveSkillData ToEntity() {
        return MyPassiveSkillData.builder().json_saveDataValue(json_saveDataValue).useridUser(useridUser).build();
    }

    public void SetMyPassiveSkillDataDto(Long useridUser, String jsonData) {
        this.json_saveDataValue = jsonData;
        this.useridUser = useridUser;
    }
}
