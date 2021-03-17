package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity(name = "heroequipmentstable")
public class HeroEquipmentsTable {

    @Id
    int id;
    String code;
    String name;
    String kind;
    String grade;
    int setInfo;
    String secondKind;
    //기본 수치
    double defaultAbilityValue;
    //보조 수치
    double secondAbilityValue;
    //기본 성장치
    double defaultGrow;
    //보조 성장치
    double secondGrow;
    //
    String DEFAULT_PERCENT_OR_FIXEDVALUE;
    String SECOND_PERCENT_OR_FIXEDVALUE;
    String DEFAULT_HOWTO_PLUSE_KIND;
    String SECOND_HOWTO_PLUSE_KIND;
}