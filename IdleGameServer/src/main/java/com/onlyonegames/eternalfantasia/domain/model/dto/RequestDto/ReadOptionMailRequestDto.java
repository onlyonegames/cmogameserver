package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

import lombok.Data;

@Data
public class ReadOptionMailRequestDto {
    Long mailId;
    String selectedCode;
}
