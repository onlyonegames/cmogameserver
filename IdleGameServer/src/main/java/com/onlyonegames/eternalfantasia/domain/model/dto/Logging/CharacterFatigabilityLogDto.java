package com.onlyonegames.eternalfantasia.domain.model.dto.Logging;

import lombok.Data;

import java.util.List;

@Data
public class CharacterFatigabilityLogDto {
    String workingPosition;
    List<Long> characterId;
    List<Integer> previousFatigability;
    List<Integer> presentFatigability;

    public void setCharacterFatigabilityLogDto(String workingPosition, List<Long> characterId, List<Integer> previousFatigability, List<Integer> presentFatigability) {
        this.workingPosition = workingPosition;
        this.characterId = characterId;
        this.previousFatigability = previousFatigability;
        this.presentFatigability = presentFatigability;
    }
}
