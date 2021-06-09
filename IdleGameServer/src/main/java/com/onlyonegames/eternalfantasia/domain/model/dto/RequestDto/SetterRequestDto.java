package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

import lombok.Data;

import java.util.Map;

@Data
public class SetterRequestDto {
    Map<String, String> requestList;
}
