package com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool;

import lombok.Data;

@Data
public class UserDetailInfoDto {
    public Long userId;
    public int level;
    public int exp;
    public int skillPoint;
    public int fatigability;

    public void setUserDetailInfo(Long userId, int level, int exp, int skillPoint, int fatigability){
        this.userId = userId;
        this.level = level;
        this.exp = exp;
        this.skillPoint =skillPoint;
        this.fatigability = fatigability;
    }
}
