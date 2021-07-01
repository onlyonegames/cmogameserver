package com.onlyonegames.eternalfantasia.domain.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class PassiveSkillDataJsonDto {
    public static class PassiveSkillInfo {
        public int id;
        public String code;
        public String skillName;
        public int level;

        public void SetPassiveSkillInfo(PassiveSkillInfo data) {
            this.level = data.level;
        }

        public void SetFirstPassiveSkillInfo(int id, String code, String skillName, int level) {
            this.id = id;
            this.code = code;
            this.skillName = skillName;
            this.level = level;
        }
    }

    public List<PassiveSkillInfo> passiveSkillInfoList;
}
