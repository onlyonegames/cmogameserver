package com.onlyonegames.eternalfantasia.domain.model.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
// 2021-03-23 재형
public class UpgradeStatus
{
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;

    int physicalAttackPowerLevel; // 물리공격력
    int magicAttackPowerLevel;// 마법공격력
    int maxHealthPointLevel; // 최대 생명력 (int)
    int maxManaPointLevel; // 최대 마나 (int)
    int criticalChanceLevel; // 치명확률
    int criticalPercentLevel; // 치명데미지
}