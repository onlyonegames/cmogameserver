package com.onlyonegames.eternalfantasia.domain.model.dto.Logging;

import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.GetSetLog;
import lombok.Data;

@Data
public class GetSetLogDto {
    Long id;
    Long useridUser;
    String cmdLog;

    public void SetGetSetLogDto(Long useridUser, String cmdLog) {
        this.useridUser = useridUser;
        this.cmdLog = cmdLog;
    }

    public GetSetLog ToEntity() {
        return GetSetLog.builder().useridUser(useridUser).cmdLog(cmdLog).build();
    }
}
