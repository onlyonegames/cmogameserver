package com.onlyonegames.eternalfantasia.domain.model.dto.Logging;

import lombok.Data;

@Data
public class MainHeroExpLogDto {
    String workingPosition;
    int previousExp;
    int changeValue;
    int presentExp;

    public void setMainHeroExpLogDto(String workingPosition, int previousExp, int changeValue, int presentExp) {
        this.workingPosition = workingPosition;
        this.previousExp = previousExp;
        this.changeValue = changeValue;
        this.presentExp = presentExp;
    }
}
