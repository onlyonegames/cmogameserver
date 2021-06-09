package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

import lombok.Data;

import java.util.List;

@Data
public class PixieLevelUpRequestDto {
    public static class PixieGettingRuneInfo{
        public int rune_id;
        public int grade;
        public int classValue;
        public int count;
    }
    List<PixieGettingRuneInfo> pixieGettingRuneInfoList;
}
