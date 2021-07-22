package com.onlyonegames.eternalfantasia.domain.model.dto.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.MyArenaPlayData;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyArenaPlayDataDto {
    Long id;
    Long useridUser;
    Long matchedUserId;
    int playableCount;
    boolean resetAbleMatchingUser;
    LocalDateTime battleStartTime;
    LocalDateTime battleEndTime;

    public MyArenaPlayData ToEntity() {
        Init();
        return MyArenaPlayData.builder().useridUser(useridUser).matchedUserId(matchedUserId).playableCount(playableCount)
                .resetAbleMatchingUser(resetAbleMatchingUser).battleStartTime(battleStartTime).battleEndTime(battleEndTime).build();
    }

    void Init() {
        this.matchedUserId = 0L;
        this.playableCount = 5;
        this.resetAbleMatchingUser = false;
        this.battleStartTime = LocalDateTime.now();
        this.battleEndTime = LocalDateTime.now();
    }
}
