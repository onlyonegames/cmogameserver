package com.onlyonegames.eternalfantasia.domain.model.entity.Inventory;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.RelicInventoryResponseDto;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class MyRelicInventory extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    int table_id;
    int count;
    int level;

    public void SetMyRelicInventoryForResponse(RelicInventoryResponseDto dto) {
        this.count = dto.getCount();
        this.level = dto.getLevel();
    }

    public boolean SpendRelic(){
        if(1>this.count)
            return false;
        this.count -= 1;
        return true;
    }

    public void LevelUp() {
        this.level += 1;
    }
}
