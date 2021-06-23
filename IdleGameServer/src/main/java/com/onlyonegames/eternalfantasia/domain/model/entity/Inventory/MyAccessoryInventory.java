package com.onlyonegames.eternalfantasia.domain.model.entity.Inventory;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.AccessoryInventoryResponseDto;
import lombok.*;

import javax.persistence.*;

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
    int[] optionLockList;
    String options;

    public void SetMyAccessoryInventory(AccessoryInventoryResponseDto dto) {
        this.count = dto.getCount();
        this.level = dto.getLevel();
        this.optionLockList = dto.getOptionLockList();
        this.options = dto.getOptions();
    }
}
