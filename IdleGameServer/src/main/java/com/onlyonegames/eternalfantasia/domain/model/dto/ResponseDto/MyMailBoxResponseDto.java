package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyMailBoxResponseDto {
    Long mailId;
    String title;
    String gettingItem;
    String gettingItemCount;
    LocalDateTime sendDate;
    LocalDateTime expireDate;
    int mailType;
    boolean hasRead;
    boolean received;
}
