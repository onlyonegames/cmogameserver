package com.onlyonegames.eternalfantasia.domain.model.dto.initJsonDataDto;

import java.util.List;

public class InitJsonForLinkforceDto {
    public static class SkillTree
    {
        public int id_Skill;
        public List<Integer> skill_Dependencies;
    }

    public List<SkillTree> skilltree;
}
