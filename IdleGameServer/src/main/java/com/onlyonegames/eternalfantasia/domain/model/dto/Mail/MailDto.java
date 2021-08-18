package com.onlyonegames.eternalfantasia.domain.model.dto.Mail;

import com.onlyonegames.eternalfantasia.domain.model.entity.Mail.Mail;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MailDto {
    Long id;
    Long toId;/*0이면 시스템내 모든 유저*/
    String title;
    String gettingItems;
    String gettingItemCounts;
    LocalDateTime sendDate;//우편 보낸 날자
    LocalDateTime expireDate;//우편 만료 날자
    int mailType;/*0 only messeage, 1 아이템 선물*/

    public Mail ToEntity() {
        return Mail.builder().toId(toId).title(title).gettingItems(gettingItems).gettingItemCounts(gettingItemCounts).sendDate(sendDate).expireDate(expireDate).mailType(mailType).build();
    }

    public void InitFromDbData(Mail dbData) {
        this.id = dbData.getId();
        this.toId = dbData.getToId();
        this.title = dbData.getTitle();
        this.gettingItems = dbData.getGettingItems();
        this.gettingItemCounts = dbData.getGettingItemCounts();
        this.sendDate = dbData.getSendDate();
        this.expireDate = dbData.getExpireDate();
        this.mailType = dbData.getMailType();
    }
}
