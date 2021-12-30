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
    @TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String code;
    int level;
    int count;
    int promotionPercent;
    int isPromotionLock;
    int superiorLevel;
    String superiorOptions;
    String superiorOptionLock;

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

    public void ReturnLegend() {
        int percent = this.promotionPercent;
        int count = (percent / 5) * 4;
        this.promotionPercent = 0;
        this.count += count;
    }

    public void SetMyClassInventory(ClassInventoryResponseDto dto) {
        this.level = dto.getLevel();
        this.count = dto.getCount();
        this.promotionPercent = dto.getPromotionPercent();
        this.isPromotionLock = dto.getIsPromotionLock();
        this.superiorOptions = dto.getSuperiorOptions();
        this.superiorOptionLock = dto.getSuperiorOptionLock();
    }

    public void SetterMyClassInventory(ClassInventoryResponseDto dto) {
        this.level = dto.getLevel();
        this.count = dto.getCount();
        this.promotionPercent = dto.getPromotionPercent();
        this.isPromotionLock = dto.getIsPromotionLock();
        this.superiorOptionLock = dto.getSuperiorOptionLock();
    }

    public boolean UpgradeClass() {
        if (this.superiorLevel > 5)
            return false;
        this.superiorLevel += 1;
        return true;
    }

    public void ResetOptions(String superiorOptions){
        this.superiorOptions = superiorOptions;
    }

    public void ResetOptionLock(String superiorOptionLock) {
        this.superiorOptionLock = superiorOptionLock;
    }
}
