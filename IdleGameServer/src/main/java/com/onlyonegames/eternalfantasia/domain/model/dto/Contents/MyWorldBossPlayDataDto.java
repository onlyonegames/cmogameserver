package com.onlyonegames.eternalfantasia.domain.model.dto.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.MyWorldBossPlayData;
import lombok.Data;

@Data
public class MyWorldBossPlayDataDto {
    Long id;
    Long useridUser;
    int playableCount;
    Long playLogId;

    public MyWorldBossPlayData ToEntity() {
        Init();
        return MyWorldBossPlayData.builder().useridUser(useridUser).playableCount(playableCount).playLogId(playLogId).build();
    }

    void Init() {
        this.playableCount = 3;
        this.playLogId = 0L;
    }
}
