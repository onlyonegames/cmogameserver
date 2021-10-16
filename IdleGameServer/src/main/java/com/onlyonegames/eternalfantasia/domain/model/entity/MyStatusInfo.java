package com.onlyonegames.eternalfantasia.domain.model.entity;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
// 2021-03-23 재형
public class MyStatusInfo extends BaseTimeEntity
{
    public enum STATUS_TYPE {
        PHYSICAL_ATTACK_POWER, MAGIC_ATTACK_POWER, MAX_HEALTH_POINT, MAX_MANA_POINT, CRITICAL_CHANCE, CRITICAL_PERCENT
    }
    @Id
    @TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;

    public int physicalAttackPowerLevel; // 물리공격력
    public int maxHealthPointLevel; // 최대 생명력 (int)
    public int maxManaPointLevel; // 최대 마나 (int)
    public int criticalChanceLevel; // 치명확률
    public int criticalPercentLevel; // 치명데미지
    public int damageUPPercentLevel;
    public int damageDownPercentLevel;
    public int criticalResistanceLevel;
    public int badEffectResistanceLevel;

    public void AddStatus(STATUS_TYPE status_type, int addLevel, String previousPrice) {
        switch(status_type){
            case PHYSICAL_ATTACK_POWER:
                this.physicalAttackPowerLevel += addLevel;
//                this.physicalAttackPowerPreviousPrice = previousPrice;
                break;
            case MAX_HEALTH_POINT:
                this.maxHealthPointLevel += addLevel;
//                this.maxHealthPointPreviousPrice = previousPrice;
                break;
            case MAX_MANA_POINT:
                this.maxManaPointLevel += addLevel;
//                this.maxManaPointPreviousPrice = previousPrice;
                break;
            case CRITICAL_CHANCE:
                this.criticalChanceLevel += addLevel;
//                this.criticalChancePreviousPrice = previousPrice;
                break;
            case CRITICAL_PERCENT:
                this.criticalPercentLevel += addLevel;
//                this.criticalPercentPreviousPrice = previousPrice;
                break;
        }
    }

//    public String ReturnPreviousPrice(STATUS_TYPE status_type) {
//        switch(status_type){
//            case PHYSICAL_ATTACK_POWER:
//                return physicalAttackPowerPreviousPrice;
//            case MAGIC_ATTACK_POWER:
//                return magicAttackPowerPreviousPrice;
//            case MAX_HEALTH_POINT:
//                return maxHealthPointPreviousPrice;
//            case MAX_MANA_POINT:
//                return maxManaPointPreviousPrice;
//            case CRITICAL_CHANCE:
//                return criticalChancePreviousPrice;
//            case CRITICAL_PERCENT:
//                return criticalPercentPreviousPrice;
//        }
//        return "";
//    }

    public int ReturnTypeLevel(STATUS_TYPE status_type) {
        switch(status_type){
            case PHYSICAL_ATTACK_POWER:
                return physicalAttackPowerLevel;
            case MAX_HEALTH_POINT:
                return maxHealthPointLevel;
            case MAX_MANA_POINT:
                return maxManaPointLevel;
            case CRITICAL_CHANCE:
                return criticalChanceLevel;
            case CRITICAL_PERCENT:
                return criticalPercentLevel;
        }
        return -1;
    }

    public int ReturnBaseValue(STATUS_TYPE status_type) {
        switch(status_type){
            case PHYSICAL_ATTACK_POWER:
            case MAGIC_ATTACK_POWER:
                return 10;
            case MAX_HEALTH_POINT:
                return 50;
            case MAX_MANA_POINT:
                return 100;
            case CRITICAL_CHANCE:
            case CRITICAL_PERCENT:
                return 200;
        }
        return -1;
    }
}