package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MailSendRequestDto {
    String title;
    Long toId;
    String gettingItem;
    String gettingItemCount;
    int mailType;
    LocalDateTime sendDate;
    LocalDateTime expireDate;

    public void SetMailSendRequestDto(String title, Long toId, String gettingItem, String gettingItemCount, int mailType, LocalDateTime sendDate, LocalDateTime expireDate) {
        this.title = title;
        this.toId = toId;
        this.gettingItem = gettingItem;
        this.gettingItemCount = gettingItemCount;
        this.mailType = mailType;
        this.sendDate = sendDate;
        this.expireDate = expireDate;
    }
}
