package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.CompanionStarPointsAverageTable;
import lombok.Data;

@Data
public class CompanionStarPointsAverageTableDto {
    Long id;
    Long useridUser;
    String json_StarPointsAverage;

    public CompanionStarPointsAverageTable ToEntity() {
        return CompanionStarPointsAverageTable.builder().useridUser(useridUser).json_StarPointsAverage(json_StarPointsAverage).build();
    }
}
