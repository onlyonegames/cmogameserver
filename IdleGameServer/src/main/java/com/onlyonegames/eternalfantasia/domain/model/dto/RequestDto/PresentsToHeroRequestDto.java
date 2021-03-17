package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

import com.onlyonegames.eternalfantasia.domain.model.dto.GiftItemDto.GiftItemDtosList;
import lombok.Data;

import java.util.List;

@Data
public class PresentsToHeroRequestDto {
    Long toCharacterId;
    List<GiftItemDtosList.GiftItemDto> presentsList;
}
