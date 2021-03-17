package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MailSendRequestDto {
    public static class Item{
        public String gettingItem;
        public int gettingItemCount;

        public void setItem(String gettingItem, int gettingItemCount){
            this.gettingItem = gettingItem;
            this.gettingItemCount = gettingItemCount;
        }
    }
    public String title;
    public long fromId;
    public long toId;
    public String content;
    public List<Item> itemList;
    public int mailType;//0 기본 메일 , 1 아이템 첨부 메일
    public LocalDateTime sendDate;
    public LocalDateTime expireDate;

    public void SetMailSendRequestDto(String title, Long fromId, Long toId, String content, List<Item> itemList, int mailType, LocalDateTime expireDate){
        this.title = title;
        this.fromId = fromId;
        this.toId = toId;
        this.content = content;
        this.itemList = itemList;
        this.mailType = mailType;
        this.expireDate = expireDate;
    }

    public void SetMailSendRequestDto(String title, Long fromId, Long toId, String content, List<Item> itemList, int mailType, LocalDateTime sendDate, LocalDateTime expireDate){
        this.title = title;
        this.fromId = fromId;
        this.toId = toId;
        this.content = content;
        this.itemList = itemList;
        this.mailType = mailType;
        this.sendDate = sendDate;
        this.expireDate = expireDate;
    }
}
