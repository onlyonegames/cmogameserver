package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyExpeditionData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyFieldSaveData;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyFieldSaveDataDto {
    Long id;
    Long useridUser; //유저 아이디
    String json_saveDataValue;/*필드상황저장데이터*/
    LocalDateTime lastClearTime;/*필드오브젝트들 하루마다 일괄 초기화 하기 위해 마지막으로 초기화 한 시간 저장*/

    public MyFieldSaveData ToEntity() {
        return MyFieldSaveData.builder().useridUser(useridUser).json_saveDataValue(json_saveDataValue).lastClearTime(lastClearTime).build();
    }
}
