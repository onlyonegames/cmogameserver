package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import lombok.Data;

@Data
public class WorldBossRankingInfoDto {
    Long useridUser;
    String userGameName;
    int ranking;
    Long totalDamage;

    public void SetWorldBossRankingInfoDto(Long useridUser, String userGameName, int ranking, Long totalDamage) {
        this.useridUser = useridUser;
        this.userGameName = userGameName;
        this.ranking = ranking;
        this.totalDamage = totalDamage;
    }
}
