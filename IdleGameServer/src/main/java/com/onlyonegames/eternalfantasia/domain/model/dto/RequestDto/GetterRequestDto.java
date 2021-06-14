package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GetterRequestDto {
    public Map<String,List<String>> gettingRequestList;
}
