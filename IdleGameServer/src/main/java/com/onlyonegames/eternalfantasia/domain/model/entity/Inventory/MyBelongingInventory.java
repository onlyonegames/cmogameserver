package com.onlyonegames.eternalfantasia.domain.model.entity.Inventory;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class MyBelongingInventory extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String code;
    int count;
    int slotNo; // 0 : 슬롯 미지정  1, 2, 3 : 해당 번호 슬롯에 지정

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

    public void SetCountAndSlotNo(String count, String slotNo) {
        this.count = Integer.parseInt(count);
        this.slotNo = Integer.parseInt(slotNo);
    }

    public void SetCountAndSlotNo(int count, int slotNo) {
        this.count = count;
        this.slotNo = slotNo;
    }

    public void SetSlotNo(String element) {
        this.slotNo = Integer.parseInt(element);
    }
}
