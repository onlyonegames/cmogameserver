package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import lombok.Data;

@Data
public class ArenaRankingInfoDto {
    Long useridUser;
    int ranking;
    int point;

    public void SetArenaRankingInfoDto(Long useridUser, int ranking, int point) {
        this.useridUser = useridUser;
        this.ranking = ranking;
        this.point = point;
    }
}
