package com.onlyonegames.eternalfantasia.domain.model.dto.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.PreviousStageRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.StageRanking;
import lombok.Data;

@Data
public class PreviousStageRankingDto {
    Long id;
    Long useridUser;
    String userGameName;
    int point;
    int ranking;
    boolean receivable;

    public PreviousStageRanking ToEntity() {
        return PreviousStageRanking.builder().useridUser(useridUser).userGameName(userGameName).point(point).ranking(ranking).receivable(true).build();
    }

    public void InitDB(StageRanking dbData) {
        this.id = dbData.getId();
        this.useridUser = dbData.getUseridUser();
        this.userGameName = dbData.getUserGameName();
        this.point = dbData.getPoint();
        this.ranking = dbData.getRanking();
    }
}
