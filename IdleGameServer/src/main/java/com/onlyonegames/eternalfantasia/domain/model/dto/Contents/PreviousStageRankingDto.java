package com.onlyonegames.eternalfantasia.domain.model.dto.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.PreviousStageRanking;
import lombok.Data;

@Data
public class PreviousStageRankingDto {
    Long id;
    Long useridUser;
    int stageNo;
    int ranking;
    boolean receivable;

    public PreviousStageRanking ToEntity() {
        return PreviousStageRanking.builder().useridUser(useridUser).stageNo(stageNo).ranking(ranking).receivable(receivable).build();
    }
}
