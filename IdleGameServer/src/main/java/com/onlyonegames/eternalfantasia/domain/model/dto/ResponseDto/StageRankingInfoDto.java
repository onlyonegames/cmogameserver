package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import lombok.Data;

@Data
public class StageRankingInfoDto {
    Long useridUser;
    String userGameName;
    int ranking;
    int point;

    public void SetStageRankingInfoDto(Long useridUser, String userGameName, int ranking, int point) {
        this.useridUser = useridUser;
        this.userGameName = userGameName;
        this.ranking = ranking;
        this.point = point;
    }
}
