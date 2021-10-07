package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import lombok.Data;

@Data
public class WorldBossRankingInfoDto {
    Long useridUser;
    String userGameName;
    int ranking;
    Long totalDamage;
    double totalPercent;

    public void SetWorldBossRankingInfoDto(Long useridUser, String userGameName, int ranking, Long totalDamage, double totalPercent) {
        this.useridUser = useridUser;
        this.userGameName = userGameName;
        this.ranking = ranking;
        this.totalDamage = totalDamage;
        this.totalPercent = totalPercent;
    }
}
