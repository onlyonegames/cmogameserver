package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

import lombok.Data;

@Data
public class ArenaPlayRequestCommonDto {
    Long selecteMatchUserId;
    int enemyTeamBattlePower;
    int myTeamBattlePower;
}
