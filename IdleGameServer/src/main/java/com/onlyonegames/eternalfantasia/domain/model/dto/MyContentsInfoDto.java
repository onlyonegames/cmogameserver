package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyContentsInfo;
import lombok.Data;

@Data
public class MyContentsInfoDto {
    Long id;
    Long useridUser;
    int challengeTowerFloor;

    public MyContentsInfo ToEntity(){
        return MyContentsInfo.builder().useridUser(useridUser).challengeTowerFloor(challengeTowerFloor).build();
    }
}
