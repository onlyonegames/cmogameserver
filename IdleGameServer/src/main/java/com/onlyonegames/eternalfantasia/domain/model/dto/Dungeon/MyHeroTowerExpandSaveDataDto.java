package com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyHeroTowerExpandSaveData;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyHeroTowerExpandSaveDataDto {
    Long id;
    Long useridUser;
    String json_saveDataValue;/*각층 오픈 정보, 플레이 결과 정보 Json*/
    String json_ExpandInfo/*확장 정보 (각 층별 결정된 보상 및 플레이시 필요한 차원석 갯수)*/;
    LocalDateTime seasonStartTime;

    public MyHeroTowerExpandSaveData ToEntity() {
        return MyHeroTowerExpandSaveData.builder().useridUser(useridUser).json_ExpandInfo(json_ExpandInfo).json_saveDataValue(json_saveDataValue).seasonStartTime(seasonStartTime).build();
    }

    public void InitFromDbData(MyHeroTowerExpandSaveData dbData) {
        this.id = dbData.getId();
        this.useridUser = dbData.getUseridUser();
        this.json_saveDataValue = dbData.getJson_saveDataValue();
        this.json_ExpandInfo = dbData.getJson_ExpandInfo();
        this.seasonStartTime = dbData.getSeasonStartTime();
    }
}
