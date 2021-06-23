package com.onlyonegames.eternalfantasia.domain.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ActiveSkillDataJsonDto {
    public static class SkillInfo {
        public int id;
        public String code;
        public String skillName;
        public int level;
        public int maxLevel;
        public boolean open;
        public int awakeningLevel;
        public List<Boolean> optionOpenList;

        public void LevelUp() {
            this.level++;
        }

        public void MaxLevelUp(int levelUp) {
            this.maxLevel += levelUp;
        }

//        public void SetSkillInfo(int id, String code, String skillName, int level, int maxLevel) {
//            this.id = id;
//            this.code = code;
//            this.skillName = skillName;
//            this.level = level;
//            this.maxLevel = maxLevel;
//        }
        public void SkillOpen() {
            this.open = true;
        }

        public void OptionOpen(int index){
            this.optionOpenList.set(index, true);
        }
    }
    public List<SkillInfo> skillInfoList;
}
