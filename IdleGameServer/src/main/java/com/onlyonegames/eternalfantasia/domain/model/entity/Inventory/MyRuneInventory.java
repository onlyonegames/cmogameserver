package com.onlyonegames.eternalfantasia.domain.model.entity.Inventory;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class MyRuneInventory extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    int rune_Id;
    String itemClass;
    int itemClassValue;
    int grade;
    int count;

    public boolean RuneEvolution() {
        if(rune_Id != 30 || count >= 2){
            count -= 2;
            return true;
        } else
            return false;
    }

    public void AddCount(int addCount) {
       count += addCount;
    }

    public boolean SpendRune(int spendCount) {
        if(spendCount <= this.count) {
            this.count -= spendCount;
            return true;
        }
        return false;
    }
}
