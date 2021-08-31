package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

import lombok.Data;

@Data
public class PassRewardRequestDto {
    int rewardType;
    boolean passReward;
    int levelIndex;
    int index;
}
