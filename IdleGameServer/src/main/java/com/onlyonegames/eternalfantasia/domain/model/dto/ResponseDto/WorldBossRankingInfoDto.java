package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import lombok.Data;

@Data
public class WorldBossRankingInfoDto {
    Long useridUser;
    int ranking;
    Long totalDamage;

    public void SetWorldBossRankingInfoDto(Long useridUser, int ranking, Long totalDamage) {
        this.useridUser = useridUser;
        this.ranking = ranking;
        this.totalDamage = totalDamage;
    }
}
