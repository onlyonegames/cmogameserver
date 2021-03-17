package com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyOrdealDungeonExpandSaveData;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyOrdealDungeonExpandSaveDataDto {
    Long id;
    Long useridUser;
    String json_saveDataValue;/*각층 오픈 정보, 플레이 결과 정보 Json*/
    int bonusRemainCount;/*시즌 변경때마다 해당 카운트가 남아있는 경우 보상의 2배를 받을수 있음*/
    LocalDateTime seasonStartTime;

    public MyOrdealDungeonExpandSaveData ToEntity() {
        return MyOrdealDungeonExpandSaveData.builder().useridUser(useridUser).json_saveDataValue(json_saveDataValue).bonusRemainCount(bonusRemainCount).seasonStartTime(seasonStartTime).build();
    }

    public void InitFromDbData(MyOrdealDungeonExpandSaveData dbData){
        this.id = dbData.getId();
        this.useridUser = dbData.getUseridUser();
        this.json_saveDataValue = dbData.getJson_saveDataValue();
        this.bonusRemainCount = dbData.getBonusRemainCount();
        this.seasonStartTime = dbData.getSeasonStartTime();
    }
}
