package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import lombok.Data;

@Data
public class BattlePowerRankingInfoDto {
    Long useridUser;
    String userGameName;
    int ranking;
    double battlePower;
    double totalPercent;

    public void SetBattlePowerRankingInfoDto(Long useridUser, String userGameName, int ranking, double battlePower, double totalPercent) {
        this.useridUser = useridUser;
        this.userGameName = userGameName;
        this.ranking = ranking;
        this.battlePower = battlePower;
        this.totalPercent = totalPercent;
    }
}
