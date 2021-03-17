package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GoodsItemDto {
    String code;/*RESMELTING_TICKET, LINK_FORCE_POINT*/
    int count;
}
