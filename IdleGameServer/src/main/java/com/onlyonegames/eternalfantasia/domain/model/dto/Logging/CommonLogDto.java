package com.onlyonegames.eternalfantasia.domain.model.dto.Logging;

import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.CommonLog;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommonLogDto {
    Long id;
    Long useridUser;
    String target;
    LocalDateTime logTime;
    String action;
    String result;
    String changeValue;

    public void SetCommonLogDto(Long userId, String target, LocalDateTime logTime, LogFormatDto dto) {
        this.useridUser = userId;
        this.target = target;
        this.logTime = logTime;
        this.action = dto.getAction();
        this.result = dto.getResult();
        this.changeValue = dto.getChangeValue();
    }

    public CommonLog ToEntity() {
        return CommonLog.builder().useridUser(useridUser).target(target).logTime(logTime).action(action).result(result).changeValue(changeValue).build();
    }
}
