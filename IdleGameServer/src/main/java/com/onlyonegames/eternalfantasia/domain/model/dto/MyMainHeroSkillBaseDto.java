package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyMainHeroSkill;
import lombok.Data;

@Data
public class MyMainHeroSkillBaseDto {
    Long id;
    Long useridUser;
    int baseSkillid;
    int activatedSkillid;
    int level;

    public MyMainHeroSkill ToEntity() {
        return MyMainHeroSkill.builder().useridUser(useridUser).baseSkillid(baseSkillid)
                .activatedSkillid(activatedSkillid).level(level).build();
    }

    public void InitFromDbData(MyMainHeroSkill dbData) {
        this.id = dbData.getId();
        this.useridUser = dbData.getUseridUser();
        this.baseSkillid = dbData.getBaseSkillid();
        this.activatedSkillid = dbData.getActivatedSkillid();;
        this.level = dbData.getLevel();
    }
}