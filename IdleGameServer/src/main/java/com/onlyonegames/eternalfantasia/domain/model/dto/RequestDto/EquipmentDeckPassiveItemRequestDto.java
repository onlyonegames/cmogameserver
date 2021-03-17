package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

import lombok.Data;

@Data
public class EquipmentDeckPassiveItemRequestDto {
    public int deckNo;
    public Long itemInventoryId;
    public int passiveSlotIndex;
}
