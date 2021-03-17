package com.onlyonegames.eternalfantasia.domain.controller.developer;

import lombok.Data;

@Data
public class FatigabilityTestDto {
    Long userId;
    String heroCode;
    int fatigability;
}
