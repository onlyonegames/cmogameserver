package com.onlyonegames.eternalfantasia.domain.model.dto.Contents;

import lombok.Data;

@Data
public class WorldBossRankingDto {
    int ranking;
    Long totalDamage;
    Long bastDamage;

    public void SetFirstUser() {
        this.ranking = 0;
        this.totalDamage = 0L;
        this.bastDamage = 0L;
    }
}
