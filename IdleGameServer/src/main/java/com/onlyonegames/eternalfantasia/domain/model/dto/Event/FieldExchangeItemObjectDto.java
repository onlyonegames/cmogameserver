package com.onlyonegames.eternalfantasia.domain.model.dto.Event;

import com.onlyonegames.eternalfantasia.domain.model.dto.HotTimeEventDto.HotTimeFieldObjectInfoListDto.HotTimeFieldObjectInfoList;
import lombok.Data;

import java.util.List;

@Data
public class FieldExchangeItemObjectDto {
    List<HotTimeFieldObjectInfoList> fieldInfo;
}
