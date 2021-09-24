package com.onlyonegames.eternalfantasia.domain.model.dto.Logging;

import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.PassReceiveLog;
import lombok.Data;

@Data
public class PassReceiveLogDto {
    Long id;
    Long useridUser;
    String passName;
    int levelIndex;
    int passIndex;

    public void SetPassReceiveLogDto(Long userId, String passName, int levelIndex, int passIndex) {
        this.useridUser = userId;
        this.passName = passName;
        this.levelIndex = levelIndex;
        this.passIndex = passIndex;
    }

    public PassReceiveLog ToEntity() {
        return PassReceiveLog.builder().useridUser(useridUser).passName(passName).levelIndex(levelIndex).passIndex(passIndex).build();
    }
}
