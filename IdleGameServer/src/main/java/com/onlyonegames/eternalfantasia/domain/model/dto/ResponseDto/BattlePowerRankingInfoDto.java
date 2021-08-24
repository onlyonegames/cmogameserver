package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import lombok.Data;

@Data
public class BattlePowerRankingInfoDto {
    Long useridUser;
    String userGameName;
    int ranking;
    Long battlePower;

    public void SetBattlePowerRankingInfoDto(Long useridUser, String userGameName, int ranking, Long battlePower) {
        this.useridUser = useridUser;
        this.userGameName = userGameName;
        this.ranking = ranking;
        this.battlePower = battlePower;
    }
}
