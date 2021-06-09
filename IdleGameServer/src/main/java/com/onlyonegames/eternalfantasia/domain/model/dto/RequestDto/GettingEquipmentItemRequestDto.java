package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

import lombok.Data;

import java.util.List;

@Data
public class GettingEquipmentItemRequestDto {
    public static class GettingEquipmentItemInfo{
        public String code;
        public int count;
    }

    public List<GettingEquipmentItemInfo> gettingEquipmentItemInfoList;
}
