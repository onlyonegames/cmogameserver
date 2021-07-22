package com.onlyonegames.eternalfantasia.domain.model.dto.Contents;

import lombok.Data;

@Data
public class ArenaRankingDto {
    int ranking;
    int point;

    public void SetFirstUser() {
        this.ranking = 0;
        this.point = 0;
    }
}
