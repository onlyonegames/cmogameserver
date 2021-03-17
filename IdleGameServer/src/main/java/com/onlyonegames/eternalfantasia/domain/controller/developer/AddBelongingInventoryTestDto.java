package com.onlyonegames.eternalfantasia.domain.controller.developer;

import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ItemRequestDto;
import lombok.Data;

import java.util.List;

@Data
public class AddBelongingInventoryTestDto {
    Long userId;
    List<ItemRequestDto> listItems;
}
