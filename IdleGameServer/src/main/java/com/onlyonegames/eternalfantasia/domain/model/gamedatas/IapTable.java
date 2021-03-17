package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "iaptable")
public class IapTable {
    @Id
    int id;
    String code;
    String type;
    String productId;
    String name;
    String gettingItems;
    int cost;
    //해당 상품 일정내에 구매 가능 횟수. 0 이면 제한 없음
    int limit;
}
