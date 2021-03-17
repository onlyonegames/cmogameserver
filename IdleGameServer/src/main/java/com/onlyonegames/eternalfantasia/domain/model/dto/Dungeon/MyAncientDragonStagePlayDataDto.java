package com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyAncientDragonStagePlayData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyOrdealDungeonStagePlayData;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyAncientDragonStagePlayDataDto {
    Long id;
    Long useridUser;
    LocalDateTime battleStartTime;
    LocalDateTime battleEndTime;

    public MyAncientDragonStagePlayData ToEntity() {
        return MyAncientDragonStagePlayData.builder().useridUser(useridUser).build();
    }
}
