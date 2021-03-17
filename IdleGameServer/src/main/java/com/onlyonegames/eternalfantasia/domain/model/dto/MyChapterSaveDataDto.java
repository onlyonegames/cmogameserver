package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyChapterSaveData;
import lombok.Data;

@Data
public class MyChapterSaveDataDto {
    Long id;
    Long useridUser;
    String saveDataValue;

    public MyChapterSaveData ToEntity() {
        return MyChapterSaveData.builder().useridUser(useridUser).saveDataValue(saveDataValue).build();
    }
}
