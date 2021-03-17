package com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyOrdealDungeonStagePlayData;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyOrdealDungeonStagePlayDataDto {
    Long id;
    Long useridUser;
    LocalDateTime battleStartTime;
    LocalDateTime battleEndTime;

    public MyOrdealDungeonStagePlayData ToEntity() {
        return MyOrdealDungeonStagePlayData.builder().useridUser(useridUser).build();
    }
}
