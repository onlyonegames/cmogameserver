package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "heroequipmentproductionexpand")
public class HeroEquipmentProductionExpandTable {
    @Id
    int id;
    String grade;/*제작 될 장비 등급*/
    int productionSecond;/*제작에 걸리는 시간*/
    int masteryUpValue;/*장비 제작 숙련도 증가 값*/
    double ProductionPercent;/*제작시 해당 등급이 선택될 확률*/
}
