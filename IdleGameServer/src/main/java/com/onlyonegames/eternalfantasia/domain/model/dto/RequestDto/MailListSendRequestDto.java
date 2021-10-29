package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

import lombok.Data;

@Data
public class MailListSendRequestDto {
    Long userId;
    int plusDay;
    String title;
    String gettingItem;
}
