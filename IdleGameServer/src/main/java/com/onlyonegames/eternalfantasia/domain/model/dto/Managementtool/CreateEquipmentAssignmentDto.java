package com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool;

import lombok.Data;

import java.util.List;

@Data
public class CreateEquipmentAssignmentDto {
    Long userId;
    int item_Id;
    int level;
    String itemClass;
    List<Integer> optionIds;
    List<Double> optionValues;
}
