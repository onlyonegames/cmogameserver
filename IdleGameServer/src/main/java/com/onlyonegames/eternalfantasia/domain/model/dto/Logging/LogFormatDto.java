package com.onlyonegames.eternalfantasia.domain.model.dto.Logging;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogFormatDto {
    public String logTime;
    public String action;
    public String result;
    public String changeValue;
}
