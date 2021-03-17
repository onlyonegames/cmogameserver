package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

import lombok.Data;

@Data
public class StageClearRequestDto {
    int chapterNo;
    int stageNo;
    int aliveCharacterCount;
}
