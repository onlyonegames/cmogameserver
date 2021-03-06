package com.onlyonegames.eternalfantasia.domain.model.dto.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.WorldBossRanking;
import lombok.Data;

@Data
public class WorldBossRankingDto {
    int ranking;
    double totalDamage;
    double bestDamage;
    double totalPercent;

    public void SetFirstUser() {
        this.ranking = 0;
        this.totalDamage = 0;
        this.bestDamage = 0;
        this.totalPercent = 0;
    }

    public void InitDbData(WorldBossRanking dbData, double totalPercent) {
        this.ranking = dbData.getRanking();
        this.totalDamage = dbData.getTotalDamage();
        this.bestDamage = dbData.getBestDamage();
        this.totalPercent = totalPercent;
    }
}
