package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyPixieInfoData;
import lombok.Data;

@Data
public class PixieUserDataDto {
    public int level;
    public Long exp;

    public void SetPixieUserDataDto(MyPixieInfoData data) {
        this.level = data.getLevel();
        this.exp = data.getExp();
    }
}
