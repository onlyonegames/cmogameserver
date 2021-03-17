package com.onlyonegames.eternalfantasia.domain.model.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;
import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MyMainHeroSkill extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    // 가장 낮은 단계의 스킬 아이디
    // ex. 노멀 스킬 1, 강화 스킬 2, 초월 스킬 3 일때 baseSkillid = 1
    int baseSkillid;
    // 활성화 된 등급의 스킬 아이디
    int activatedSkillid;
    int level;

    @Builder
    public MyMainHeroSkill(Long useridUser, int baseSkillid, int activatedSkillid, int level) {
        this.useridUser = useridUser;
        this.baseSkillid = baseSkillid;
        this.activatedSkillid = activatedSkillid;
        this.level = level;
    }

    public void LevelUp() {
        this.level++;
    }

    //운영툴에서만 사용하는 함수
    public void LevelChange(int changeLv) {
        this.level = changeLv;
    }
}