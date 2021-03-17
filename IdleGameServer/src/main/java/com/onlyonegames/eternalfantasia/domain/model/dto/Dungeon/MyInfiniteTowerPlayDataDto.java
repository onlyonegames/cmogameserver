package com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyInfiniteTowerPlayData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyOrdealDungeonStagePlayData;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class MyInfiniteTowerPlayDataDto {
    Long id;
    Long useridUser;
    LocalDateTime battleStartTime;
    LocalDateTime battleEndTime;

    public MyInfiniteTowerPlayData ToEntity() {
        return MyInfiniteTowerPlayData.builder().useridUser(useridUser).build();
    }
}
