package com.onlyonegames.eternalfantasia.domain.model.entity.Inventory;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.RuneInventoryResponseDto;
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
    String code;
    int count;
    int level;

//    public boolean RuneEvolution() {
//        if(rune_Id != 30 || count >= 2){
//            count -= 2;
//            return true;
//        } else
//            return false;
//    }

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

    public void SetCount(String count) {
        this.count = Integer.parseInt(count);
    }

    public void SetCount(int count) {
        this.count = count;
    }

    public void SetRuneInventory(RuneInventoryResponseDto dto) {
        this.count = dto.getCount();
        this.level = dto.getLevel();
    }
}
