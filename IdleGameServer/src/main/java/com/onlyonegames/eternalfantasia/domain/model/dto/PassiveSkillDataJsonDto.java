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
        public int maxLevel;
        public boolean open;

        public void SetPassiveSkillInfo(PassiveSkillInfo data) {
            this.level = data.level;
            this.maxLevel = data.maxLevel;
            this.open = data.open;
        }

        public void SetFirstPassiveSkillInfo(int id, String code, String skillName, int level, int maxLevel, boolean open) {
            this.id = id;
            this.code = code;
            this.skillName = skillName;
            this.level = level;
            this.maxLevel = maxLevel;
            this.open = open;
        }
    }

    public List<PassiveSkillInfo> passiveSkillInfoList;
}
