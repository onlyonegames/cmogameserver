package com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool;

import lombok.Data;

@Data
public class UserSkillInfoDto {
    public int id;
    public String skillName;
    public int level;

    public void SetUserSkillInfo(int id, String skillName, int level){
        this.id = id;
        this.skillName = skillName;
        this.level = level;
    }
}
