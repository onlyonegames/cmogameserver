package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

import com.onlyonegames.eternalfantasia.etc.EquipmentItemCategory;
import lombok.Data;

import java.util.List;

@Data
public class StartProductRequestDto {
    int slotNo;
    EquipmentItemCategory itemCategory;
}
