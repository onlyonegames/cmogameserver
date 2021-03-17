package com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyHeroTowerStagePlayData;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HeroTowerStagePlayDataDto {
    Long id;
    Long useridUser;
    LocalDateTime battleStartTime;
    LocalDateTime battleEndTime;

    public MyHeroTowerStagePlayData ToEntity() {
        return MyHeroTowerStagePlayData.builder().useridUser(useridUser).build();
    }
}
