package com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyAncientDragonExpandSaveData;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyAncientDragonExpandSaveDataDto {
    Long id;
    Long useridUser;
    String json_saveDataValue;/*각층 오픈 정보, 플레이 결과 정보 Json*/
    String selectedDragonCode; /*요일마다 다른 드래곤*/
    int playableRemainCount;/*매 시즌 고대던전은 총 3번만 플레이 가능*/
    LocalDateTime seasonStartTime;

    public MyAncientDragonExpandSaveData ToEntity(){
        return MyAncientDragonExpandSaveData.builder().useridUser(useridUser).json_saveDataValue(json_saveDataValue).selectedDragonCode(selectedDragonCode)
                .playableRemainCount(playableRemainCount).seasonStartTime(seasonStartTime).build();
    }

    public void InitFromDbData(MyAncientDragonExpandSaveData dbData) {
        this.id = dbData.getId();
        this.useridUser = dbData.getUseridUser();
        this.json_saveDataValue = dbData.getJson_saveDataValue();
        this.selectedDragonCode = dbData.getSelectedDragonCode();
        this.playableRemainCount = dbData.getPlayableRemainCount();
        this.seasonStartTime = dbData.getSeasonStartTime();
    }
}
