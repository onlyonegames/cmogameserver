package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class monsterstable {
    @Id
    String Code;
    String Name;
    String AttackType;
    double HealthPoint;
    double AttackPower;
    double PhysicalDefence;
    double MagicDefence;
    double AttackSpeed;
    double CriticalProbability;
    double CriticalPercent;
    double CriticalResistance;
    double PenetrationProbability;
    double PenetrationPercent;
    double PenetrationResistance;
    double AccuracyRate;
    double EvasionRate;
    String Position;
    String ElementsType;
}