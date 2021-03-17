package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

import lombok.Data;

@Data
public class HallofHonorRequestDto {

    String selectedCharacterCode;
    int selectedPose;
    String selectedCostumeCode;
    String selectedEquipmentArmorCode;
    String selectedEquipmentHelmetCode;
    String selectedEquipmentAccessoryCode;
}
