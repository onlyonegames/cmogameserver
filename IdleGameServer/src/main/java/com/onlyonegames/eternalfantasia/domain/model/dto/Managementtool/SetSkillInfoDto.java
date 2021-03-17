package com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool;

import lombok.Data;

import java.util.List;

@Data
public class SetSkillInfoDto {
    public Long userId;
    public List<Integer> skillLevel;

    public void setSetSkillInfo(List<Integer> skillLevel){
        this.skillLevel = skillLevel;
    }
}
