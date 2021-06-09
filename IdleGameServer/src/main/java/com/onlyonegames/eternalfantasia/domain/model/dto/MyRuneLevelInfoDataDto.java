package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyRuneLevelInfoData;
import lombok.Data;

@Data
public class MyRuneLevelInfoDataDto {
    Long id;
    Long useridUser;
    int level;

    public MyRuneLevelInfoData ToEntity() {
        return MyRuneLevelInfoData.builder().useridUser(useridUser).level(level).build();
    }

    public void InitFromDBData(MyRuneLevelInfoData dbData) {
        this.id = dbData.getId();
        this.useridUser = dbData.getUseridUser();
        this.level = dbData.getLevel();
    }

    public void SetFirstUserData(Long useridUser) {
        this.useridUser = useridUser;
        this.level = 1;
    }
}
