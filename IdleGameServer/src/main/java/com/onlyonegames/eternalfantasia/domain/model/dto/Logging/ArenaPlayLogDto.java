package com.onlyonegames.eternalfantasia.domain.model.dto.Logging;

import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.ArenaPlayLog;
import lombok.Data;

@Data
public class ArenaPlayLogDto {
    Long id;
    Long useridUser;
    Long versusUserId;
    int winFailStatus;

    public void SetArenaPlayLogDto(Long userId, Long versusUserId) {
        this.useridUser = userId;
        this.versusUserId = versusUserId;
    }

    public ArenaPlayLog ToEntity() {
        return ArenaPlayLog.builder().useridUser(useridUser).versusUserId(versusUserId).winFailStatus(0).build();
    }
}
