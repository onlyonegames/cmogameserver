package com.onlyonegames.eternalfantasia.domain.model.entity.Inventory;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class MyBelongingInventory extends BaseTimeEntity {
    @Id
    Long id;
    Long useridUser;
    String code;
    int count;
    //TODO 슬롯 넘버를 이곳에 넣어야 할까??

    public void SetCount(String count) {
        this.count = Integer.parseInt(count);
    }

    public void SetCount(int count) {
        this.count = count;
    }

    public boolean SpendItem(int count) {
        if(this.count < count)
            return false;
        else
            this.count -= count;
        return true;
    }
}
