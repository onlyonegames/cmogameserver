package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SetNoticeRequestDto {
    String contents;
    LocalDateTime expireDate;
    boolean force;
}
