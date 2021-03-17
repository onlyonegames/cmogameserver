package com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.FieldDungeonInfoData;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FieldDungeonInfoDataDto {
    int id;
    LocalDateTime dungeonStartTime;
    LocalDateTime dungeonEndTime;
    int nowSeasonNo;
    String bossCode;

    public FieldDungeonInfoData ToEntity() {
        return FieldDungeonInfoData.builder().dungeonStartTime(dungeonStartTime).dungeonEndTime(dungeonEndTime).nowSeasonNo(nowSeasonNo).bossCode(bossCode).build();
    }

    public void InitDbData(FieldDungeonInfoData dbData){
        this.id = dbData.getId();
        this.dungeonStartTime = dbData.getDungeonStartTime();
        this.dungeonEndTime = dbData.getDungeonEndTime();
        this.nowSeasonNo = dbData.getNowSeasonNo();
        this.bossCode = dbData.getBossCode();
    }
}
