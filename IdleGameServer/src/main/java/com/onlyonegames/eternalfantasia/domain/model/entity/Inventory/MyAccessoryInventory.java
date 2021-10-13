package com.onlyonegames.eternalfantasia.domain.model.entity.Inventory;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.AccessoryInventoryResponseDto;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class MyAccessoryInventory extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String code;
    int count;
    int level;
    String optionLockList;
    String options;

    public void SetMyAccessoryInventory(AccessoryInventoryResponseDto dto) {
        this.count = dto.getCount();
        this.level = dto.getLevel();
        this.optionLockList = dto.getOptionLockList();
        this.options = dto.getOptions();
    }

    public void SetterMyAccessoryInventory(AccessoryInventoryResponseDto dto) {
        this.count = dto.getCount();
//        this.level = dto.getLevel();
        this.optionLockList = dto.getOptionLockList();
    }

    public void Reset_Options(String options) {
        this.options = options;
    }

    public void AccessoryLevelUp() {
        this.level += 1;
    }

    public void SpendAccessory() {
        this.count -= 1;
    }

    public void SetOptionLockList(String optionLockList) {
        this.optionLockList = optionLockList;
    }
}
