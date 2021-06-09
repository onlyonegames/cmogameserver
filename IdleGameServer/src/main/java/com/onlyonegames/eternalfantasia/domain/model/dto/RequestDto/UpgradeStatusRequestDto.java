package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyStatusInfo;
import lombok.Data;

@Data
public class UpgradeStatusRequestDto {
    Long userId;
    int addLevel;
    MyStatusInfo.STATUS_TYPE type;
}
