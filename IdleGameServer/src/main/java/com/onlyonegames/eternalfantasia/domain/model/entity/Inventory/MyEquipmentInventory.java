package com.onlyonegames.eternalfantasia.domain.model.entity.Inventory;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.WeaponInventoryResponseDto;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class MyEquipmentInventory extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String code;
    String grade;
    int count;
    int level;
    int isPromotionLock;
    String carveData;

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

    public void UpgradeLevel(){
        this.level++;
    }

    public void SetMyEquipmentInventory(WeaponInventoryResponseDto dto) {
        this.count = dto.getCount();
        this.level = dto.getLevel();
        this.isPromotionLock = dto.getIsPromotionLock();
        this.carveData = dto.getCarveData();
    }
}
