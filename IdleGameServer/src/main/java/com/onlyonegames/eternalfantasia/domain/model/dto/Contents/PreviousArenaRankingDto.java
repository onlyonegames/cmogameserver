package com.onlyonegames.eternalfantasia.domain.model.dto.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ArenaRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.PreviousArenaRanking;
import lombok.Data;

@Data
public class PreviousArenaRankingDto {
    Long id;
    Long useridUser;
    String userGameName;
    int point;
    int ranking;

    public void InitFromPreviousDb(ArenaRanking dbData) {
        this.useridUser = dbData.getUseridUser();
        this.userGameName = dbData.getUserGameName();
        this.point = dbData.getPoint();
        this.ranking = dbData.getRanking();
    }

    public PreviousArenaRanking ToEntity() {
        return PreviousArenaRanking.builder().useridUser(useridUser).userGameName(userGameName).point(point).ranking(ranking).build();
    }
}
