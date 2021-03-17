package com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool;

import lombok.Data;

import java.util.List;

@Data
public class SetCompanionCostumeDto {
    Long userId;
    String heroCode;
    List<Boolean> costumeHasBuy;
}
