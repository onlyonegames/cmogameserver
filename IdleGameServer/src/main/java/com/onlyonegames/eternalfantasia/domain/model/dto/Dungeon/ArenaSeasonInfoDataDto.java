package com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.ArenaSeasonInfoData;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArenaSeasonInfoDataDto {
    Integer id;
    int nowSeasonNo;
    String seasonName;
    LocalDateTime seasonStartTime;
    LocalDateTime seasonEndTime;
    public ArenaSeasonInfoData ToEntity() {
        return ArenaSeasonInfoData.builder().nowSeasonNo(nowSeasonNo).seasonName(seasonName).seasonStartTime(seasonStartTime).seasonEndTime(seasonEndTime).build();
    }

    public void InitDbData(ArenaSeasonInfoData dbData) {
        this.id = dbData.getId();
        this.nowSeasonNo = dbData.getNowSeasonNo();
        this.seasonName = dbData.getSeasonName();
        this.seasonStartTime = dbData.getSeasonStartTime();
        this.seasonEndTime = dbData.getSeasonEndTime();
    }
}
