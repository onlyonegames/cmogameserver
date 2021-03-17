package com.onlyonegames.eternalfantasia.domain.model.dto;

import java.util.List;

public class CompanionStarPointsAverageDtosList {
    public static class StarPointsAverageDto {
        public String heroCode;
        public int starPoints;
    }
    public List<StarPointsAverageDto> starPointsAverageDtoList;
}
