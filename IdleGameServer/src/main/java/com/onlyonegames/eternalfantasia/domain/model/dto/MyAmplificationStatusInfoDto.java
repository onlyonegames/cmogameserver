package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyAmplificationStatusInfo;
import lombok.Data;

@Data
public class MyAmplificationStatusInfoDto {
    Long id;
    Long useridUser;
    int physicalAttackPowerLevel;
    int maxHealthPointLevel;
    int criticalPercentLevel;
    int damageUPPercentLevel;
    int damageDownPercentLevel;
    int skillEffectUPLevel;

    private void Init() {
        this.physicalAttackPowerLevel = 0;
        this.maxHealthPointLevel = 0;
        this.criticalPercentLevel = 0;
        this.damageUPPercentLevel = 0;
        this.damageDownPercentLevel = 0;
        this.skillEffectUPLevel = 0;
    }

    public MyAmplificationStatusInfo ToEntity(){
        Init();
        return MyAmplificationStatusInfo.builder().useridUser(useridUser).physicalAttackPowerLevel(physicalAttackPowerLevel).maxHealthPointLevel(maxHealthPointLevel)
                .criticalPercentLevel(criticalPercentLevel).damageUPPercentLevel(damageUPPercentLevel).damageDownPercentLevel(damageDownPercentLevel)
                .skillEffectUPLevel(skillEffectUPLevel).build();
    }
}
