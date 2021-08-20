package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyStatusInfo;
import lombok.Data;

@Data
public class MyStatusInfoDto
{
    Long useridUser;

    int physicalAttackPowerLevel; // 물리공격력
    int maxHealthPointLevel; // 최대 생명력 (int)
    int maxManaPointLevel; // 최대 마나 (int)
    int criticalChanceLevel; // 치명확률
    int criticalPercentLevel; // 치명데미지
    int damageUPPercentLevel;
    int damageDownPercentLevel;
    int criticalResistanceLevel;
    int badEffectResistanceLevel;

    public MyStatusInfo ToEntity()
    {
        return MyStatusInfo.builder().useridUser(useridUser).physicalAttackPowerLevel(physicalAttackPowerLevel)
                .maxHealthPointLevel(maxHealthPointLevel).maxManaPointLevel(maxManaPointLevel).criticalChanceLevel(criticalChanceLevel)
                .criticalPercentLevel(criticalPercentLevel).damageUPPercentLevel(damageUPPercentLevel).damageDownPercentLevel(damageDownPercentLevel)
                .criticalResistanceLevel(criticalResistanceLevel).badEffectResistanceLevel(badEffectResistanceLevel).build();
    }

    public MyStatusInfoDto(Long userId) {
        this.useridUser = userId;
        this.physicalAttackPowerLevel = 1;
        this.maxHealthPointLevel = 1;
        this.maxManaPointLevel = 1;
        this.criticalChanceLevel = 1;
        this.criticalPercentLevel = 1;
        this.damageUPPercentLevel = 1;
        this.damageDownPercentLevel = 1;
        this.criticalResistanceLevel = 1;
        this.badEffectResistanceLevel = 1;
    }

    public void InitFormDBData(MyStatusInfo dbData) {
        this.useridUser = dbData.getUseridUser();
        this.physicalAttackPowerLevel = dbData.getPhysicalAttackPowerLevel();
        this.maxHealthPointLevel = dbData.getMaxHealthPointLevel();
        this.maxManaPointLevel = dbData.getMaxManaPointLevel();
        this.criticalChanceLevel = dbData.getCriticalChanceLevel();
        this.criticalPercentLevel = dbData.getCriticalPercentLevel();
    }

    public MyStatusInfoDto(){

    }
}