package com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass;

import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPass;
import lombok.Data;

@Data
public class MyEternalPassInfoDataDto {
    Long id;
    Long useridUser;
    String json_myEternalPassInfo;

    public MyEternalPass ToEntity() {
        return MyEternalPass.builder().id(id).useridUser(useridUser).json_myEternalPassInfo(json_myEternalPassInfo).build();
    }
}
