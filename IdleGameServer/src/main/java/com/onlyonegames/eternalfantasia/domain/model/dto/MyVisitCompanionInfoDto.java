package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyVisitCompanionInfo;
import lombok.Data;

@Data
public class MyVisitCompanionInfoDto
{
    Long id;
    Long useridUser;
    String visitCompanionInfo;

    public MyVisitCompanionInfo ToEntity() {
        return MyVisitCompanionInfo.builder().useridUser(useridUser).visitCompanionInfo(visitCompanionInfo).build();
    }
}
