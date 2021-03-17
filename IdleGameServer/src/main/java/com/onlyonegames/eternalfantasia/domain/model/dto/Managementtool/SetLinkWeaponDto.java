package com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool;

import lombok.Data;

import java.util.List;

@Data
public class SetLinkWeaponDto {
    Long userId;
    String heroCode;
    List<Integer> selectedIds;
    int upgrade;
}
