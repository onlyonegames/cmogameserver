package com.onlyonegames.eternalfantasia.domain.model.entity.Inventory;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.ClassInventoryResponseDto;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class MyClassInventory extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence", allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String code;
    int level;
    int count;
    int promotionPercent;

    public void AddCount(int addCount) {
        this.count += addCount;
    }

    public boolean SpendCount(int spendCount) {
        if(this.count >= spendCount){
            this.count -= spendCount;
            return true;
        }
        return false;
    }

    public void LevelUp(){
        this.level += 1;
    }

    public void SetMyClassInventory(ClassInventoryResponseDto dto) {
        this.level = dto.getLevel();
        this.count = dto.getCount();
        this.promotionPercent = dto.getPromotionPercent();
    }
}
