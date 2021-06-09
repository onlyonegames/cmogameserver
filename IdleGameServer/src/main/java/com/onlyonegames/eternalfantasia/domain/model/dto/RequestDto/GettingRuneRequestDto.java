package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

import lombok.Data;

import java.util.List;

@Data
public class GettingRuneRequestDto {
    public static class GettingRuneInfo {
        public int rune_id;
        public int itemClassValue;
        public int grade;
        public int count;
    }
    public List<GettingRuneInfo> gettingRuneInfoList;
}
