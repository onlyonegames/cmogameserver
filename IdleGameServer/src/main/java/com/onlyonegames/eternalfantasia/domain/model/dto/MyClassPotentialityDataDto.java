package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyClassPotentialityData;
import lombok.Data;

@Data
public class MyClassPotentialityDataDto {
    Long id;
    Long useridUser;
    String warrior;
    String thief;
    String knight;
    String archer;
    String magician;
    Long markOfAttackCount;
    Long markOfHealthCount;
    Long markOfDamageUpCount;
    Long markOfDamageDownCount;
    Long markOfCriticalCount;

    public void SetMyClassPotentialityDataDto(Long userId, String initFirst) {
        Init();
        this.useridUser = userId;
        this.warrior = initFirst;
        this.thief = initFirst;
        this.knight = initFirst;
        this.archer = initFirst;
        this.magician = initFirst;
    }

    private void Init() {
        this.markOfAttackCount = 0L;
        this.markOfHealthCount = 0L;
        this.markOfDamageUpCount = 0L;
        this.markOfDamageDownCount = 0L;
        this.markOfCriticalCount = 0L;
    }

    public MyClassPotentialityData ToEntity() {
        return MyClassPotentialityData.builder().useridUser(useridUser).warrior(warrior).thief(thief).knight(knight).archer(archer).magician(magician)
                .markOfAttackCount(markOfAttackCount).markOfHealthCount(markOfHealthCount).markOfDamageUpCount(markOfDamageUpCount)
                .markOfDamageDownCount(markOfDamageDownCount).markOfCriticalCount(markOfCriticalCount).build();
    }
}
