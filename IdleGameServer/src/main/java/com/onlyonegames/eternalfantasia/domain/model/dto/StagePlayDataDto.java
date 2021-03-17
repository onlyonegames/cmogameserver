package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.StagePlayData;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StagePlayDataDto {
    Long id;
    Long useridUser;
    LocalDateTime battleStartTime;
    LocalDateTime battleEndTime;

    public StagePlayData ToEntity() {
        return StagePlayData.builder().useridUser(useridUser).build();
    }
}
