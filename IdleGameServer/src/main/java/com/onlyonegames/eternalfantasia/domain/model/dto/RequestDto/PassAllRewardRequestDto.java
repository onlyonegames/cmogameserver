package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

import lombok.Data;

@Data
public class PassAllRewardRequestDto {
    int rewardType;
    int passIndex;
    int levelIndex;
    int index;
}
