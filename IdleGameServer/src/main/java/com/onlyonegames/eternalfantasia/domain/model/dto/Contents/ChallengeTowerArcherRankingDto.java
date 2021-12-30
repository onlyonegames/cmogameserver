package com.onlyonegames.eternalfantasia.domain.model.dto.Contents;

import lombok.Data;

@Data
public class ChallengeTowerArcherRankingDto {
    Long useridUser;
    String userGameName;
    int ranking;
    int point;
    double totalPercent;

    public void SetArcherChallengeTowerRankingDto(Long useridUser, String userGameName, int ranking, int point, double totalPercent) {
        this.useridUser = useridUser;
        this.userGameName = userGameName;
        this.ranking = ranking;
        this.point = point;
        this.totalPercent = totalPercent;
    }
}
