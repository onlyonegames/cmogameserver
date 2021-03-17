package com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool;

import com.onlyonegames.eternalfantasia.domain.model.gamedatas.herostable;
import com.onlyonegames.util.MathHelper;
import lombok.Data;

@Data
public class HeroStatusDto {
    int level;
    public float healthPoint;
//    public float HealthPoint() {
//        return (float)MathHelper.Round2(healthPoint);
//    }
    public float attackPower;
    public float damagePerSecond;
    public float physicalDefence;
    public float magicDefence;
    public double attackRate;
    float attackSpeed;
    public void setAttackSpeed(float attackSpeed) {
        this.attackSpeed = (float)MathHelper.Round4(attackSpeed);
    }
    float criticalChance;
    public void setCriticalChance(double criticalChance) {
        this.criticalChance = (float)MathHelper.Round4(criticalChance);
    }
    float criticalPercent;
    public void setCriticalPercent(double criticalPercent) {
        this.criticalPercent = (float)MathHelper.Round4(criticalPercent);
    }
    float penetrationChance;
    public void setPenetrationChance(double penetrationChance) {
        this.penetrationChance = (float)MathHelper.Round4(penetrationChance);
    }
    float penetrationPercent;
    public void setPenetrationPercent(double penetrationPercent) {
        this.penetrationPercent = (float)MathHelper.Round4(penetrationPercent);
    }
    float accuracyRate;
    public void setAccuracyRate(double accuracyRate) {
        this.accuracyRate = (float)MathHelper.Round4(accuracyRate);
    }
    float evasionRate;
    public void setEvasionRate(double evasionRate) {
        this.evasionRate = (float)MathHelper.Round4(evasionRate);
    }

    public void setLevel(int level, herostable herostable){
        this.level = level;
        this.attackRate = herostable.getAttackRate();
    }

    public void setDPS() {
        this.damagePerSecond = Math.round(this.getAttackPower() * this.getAttackSpeed());
    }

//    public HeroStatusDto(int level, herostable herostable){
//        this.level = level;
//        this.healthPoint = herostable.getHealthPoint();
//        this.attackPower = herostable.getAttackPower();
//        this.damagePerSecond = herostable.getDamagePerSecond();
//        this.physicalDefence = herostable.getPhysicalDefence();
//        this.magicDefence = herostable.getMagicDefence();
//        this.attackSpeed = herostable.getAttackSpeed();
//        this.attackRate = herostable.getAttackRate();
//        this.criticalChance = herostable.getCriticalProbability();
//        this.criticalPercent = herostable.getCriticalPercent();
//        this.penetrationChance = herostable.getPenetrationProbability();
//        this.penetrationPercent = herostable.getPenetrationPercent();
//        this.accuracyRate = herostable.getAccuracyRate();
//        this.evasionRate = herostable.getEvasionRate();
//
//    }

//    public void setHeroStatus(int level, HeroStatusDto dto){
//        this.level = level;
//        this.healthPoint = dto.getHealthPoint();
//        this.attackPower = dto.getAttackPower();
//        this.damagePerSecond = dto.getDamagePerSecond();
//        this.physicalDefence = dto.getPhysicalDefence();
//        this.magicDefence = dto.getMagicDefence();
//        this.attackSpeed = dto.getAttackSpeed();
//        this.criticalProbability = dto.getCriticalProbability();
//        this.criticalPercent = dto.getCriticalPercent();
//        this.penetrationProbability = dto.getPenetrationProbability();
//        this.penetrationPercent = dto.getPenetrationPercent();
//        this.accuracyRate = dto.getAccuracyRate();
//        this.evasionRate = dto.getEvasionRate();
//    }
}
