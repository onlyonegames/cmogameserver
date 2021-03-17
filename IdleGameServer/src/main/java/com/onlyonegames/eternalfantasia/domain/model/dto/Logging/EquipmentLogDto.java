package com.onlyonegames.eternalfantasia.domain.model.dto.Logging;

import com.onlyonegames.eternalfantasia.domain.model.dto.HeroEquipmentInventoryDto;
import lombok.Data;

@Data
public class EquipmentLogDto {
    String workingPosition;
    Long equipmentInventoryId;
    String changeStatus;
    HeroEquipmentInventoryDto heroEquipmentInventoryDto;

    public void setEquipmentLogDto(String workingPosition,
                                   Long equipmentInventoryId,
                                   String changeStatus,
                                   HeroEquipmentInventoryDto heroEquipmentInventoryDto) {
        this.workingPosition = workingPosition;
        this.equipmentInventoryId = equipmentInventoryId;
        this.changeStatus = changeStatus;
        this.heroEquipmentInventoryDto = heroEquipmentInventoryDto;
    }
}
