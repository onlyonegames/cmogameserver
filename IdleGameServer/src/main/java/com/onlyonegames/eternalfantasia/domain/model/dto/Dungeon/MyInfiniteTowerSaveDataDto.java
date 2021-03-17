package com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyInfiniteTowerSaveData;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.Data;

@Data
public class MyInfiniteTowerSaveDataDto {
    Long id;
    Long useridUser;
    int arrivedTopFloor;
    MyInfiniteTowerRewardReceivedInfosDto myInfiniteTowerRewardReceivedInfosDto;

    public MyInfiniteTowerSaveData ToEntity() {
        String receivedRewardInfoJson = JsonStringHerlper.WriteValueAsStringFromData(myInfiniteTowerRewardReceivedInfosDto);
        return MyInfiniteTowerSaveData.builder().useridUser(useridUser).arrivedTopFloor(arrivedTopFloor).receivedRewardInfoJson(receivedRewardInfoJson).build();
    }
}
