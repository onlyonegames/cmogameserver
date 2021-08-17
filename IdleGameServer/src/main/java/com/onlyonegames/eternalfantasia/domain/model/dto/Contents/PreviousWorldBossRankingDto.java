package com.onlyonegames.eternalfantasia.domain.model.dto.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.PreviousWorldBossRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.WorldBossRanking;
import lombok.Data;

@Data
public class PreviousWorldBossRankingDto {
    Long id;
    Long useridUser;
    String userGameName;
    Long totalDamage;
    Long bestDamage;
    int ranking;
    boolean receivable;

    public void InitFromPreviousDB(WorldBossRanking dbData) {
        this.useridUser = dbData.getUseridUser();
        this.userGameName = dbData.getUserGameName();
        this.totalDamage = dbData.getTotalDamage();
        this.bestDamage = dbData.getBestDamage();
        this.ranking = dbData.getRanking();
    }

    /**
     * 최초 생성시에만 사용해야함
     * receivable 무조건 true 세팅*/
    public PreviousWorldBossRanking ToEntity() {
        return PreviousWorldBossRanking.builder().useridUser(useridUser).userGameName(userGameName).totalDamage(totalDamage)
                .bestDamage(bestDamage).ranking(ranking).receivable(true).build();
    }
}
