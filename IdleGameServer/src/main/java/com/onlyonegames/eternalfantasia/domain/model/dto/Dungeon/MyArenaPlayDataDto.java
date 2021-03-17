package com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyArenaPlayData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyHeroTowerStagePlayData;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyArenaPlayDataDto {
    Long id;
    Long useridUser;
    LocalDateTime battleStartTime;
    LocalDateTime battleEndTime;

    public MyArenaPlayData ToEntity() {
        return MyArenaPlayData.builder().useridUser(useridUser).build();
    }
}
