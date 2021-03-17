package com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool;

import lombok.Data;

@Data
public class SetCompanionStatusDto {
    Long userId;
    Long characterId;
    String characterCode;
    int level;
    int fatigability;
}
