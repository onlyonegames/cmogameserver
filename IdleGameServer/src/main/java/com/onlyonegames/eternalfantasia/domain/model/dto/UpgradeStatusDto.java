package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyForTest;
import com.onlyonegames.eternalfantasia.domain.model.entity.UpgradeStatus;
import lombok.Data;

@Data
public class UpgradeStatusDto
{
    Long useridUser;

    int physicalAttackPowerLevel; // 물리공격력
    int magicAttackPowerLevel;// 마법공격력
    int maxHealthPointLevel; // 최대 생명력 (int)
    int maxManaPointLevel; // 최대 마나 (int)
    int criticalChanceLevel; // 치명확률
    int criticalPercentLevel; // 치명데미지

    public UpgradeStatus ToEntity()
    {
        return UpgradeStatus.builder().useridUser(useridUser).physicalAttackPowerLevel(physicalAttackPowerLevel).
                magicAttackPowerLevel(magicAttackPowerLevel).maxHealthPointLevel(maxHealthPointLevel).
                maxManaPointLevel(maxManaPointLevel).criticalChanceLevel(criticalChanceLevel).
                criticalPercentLevel(criticalPercentLevel).build();
    }
}