package com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool;

import lombok.Data;

@Data
public class BelongingPaymentDto {
    public Long useridUser;
    public Long itemType;
    public int itemId;
    public int count;

    public Long getUseridUser(){return this.useridUser;}
    public Long getItemType(){return this.itemType;}
    public int getItemId(){return this.itemId;}
    public int getCount(){return this.count;}

}
