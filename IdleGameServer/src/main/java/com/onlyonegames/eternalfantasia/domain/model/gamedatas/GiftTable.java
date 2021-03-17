package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "gifttable")
public class GiftTable {
    @Id
    int ID;
    String Code;
    String GiftName;
    String BestGiftTo;
    String GracefulGiftTo;
    String GoodGiftTo;
    String NomalGiftTo;
    String HateGiftTo;
    int StackLimit;
}
