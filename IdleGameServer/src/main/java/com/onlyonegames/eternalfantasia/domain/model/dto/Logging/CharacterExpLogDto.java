package com.onlyonegames.eternalfantasia.domain.model.dto.Logging;

import lombok.Data;

@Data
public class CharacterExpLogDto {
    String workingPosition;
    String heroCode;
    int previousExp;
    int changeExp;
    int presentExp;

    public void setCharacterExpLogDto(String workingPosition, String heroCode, int previousExp, int changeExp, int presentExp) {
        this.workingPosition = workingPosition;
        this.heroCode = heroCode;
        this.previousExp = previousExp;
        this.changeExp = changeExp;
        this.presentExp = presentExp;
    }
}
