package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

import com.onlyonegames.eternalfantasia.etc.EquipmentItemCategory;
import lombok.Data;

@Data
public class ProductionMaterialSetRequestDto {
    EquipmentItemCategory itemCategory;
    int materialIndex;
    int setCount;
}
