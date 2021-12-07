package com.onlyonegames.eternalfantasia.domain.model.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class MyAmplificationStatusInfo {
    @Id
    @TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    public int physicalAttackPowerLevel;
    public int maxHealthPointLevel;
    public int criticalPercentLevel;
    public int damageUPPercentLevel;
    public int damageDownPercentLevel;
    public int skillEffectUPLevel;
}
