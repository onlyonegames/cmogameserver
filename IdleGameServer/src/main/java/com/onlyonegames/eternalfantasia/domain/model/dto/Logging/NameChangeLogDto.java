package com.onlyonegames.eternalfantasia.domain.model.dto.Logging;

import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.NameChangeLog;
import lombok.Data;

@Data
public class NameChangeLogDto {
    Long id;
    Long useridUser;
    String previousName;
    String nowName;

    public void SetNameChangeLogDto(Long userId, String previousName, String nowName) {
        this.useridUser = userId;
        this.previousName = previousName;
        this.nowName = nowName;
    }

    public NameChangeLog ToEntity() {
        return NameChangeLog.builder().useridUser(useridUser).previousName(previousName).nowName(nowName).build();
    }
}
