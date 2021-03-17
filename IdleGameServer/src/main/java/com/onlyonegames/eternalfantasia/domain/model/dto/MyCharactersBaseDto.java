package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyCharactersBaseDto {
    Long id;
    Long useridUser;
    String codeHerostable;
    int grade;
    int level;
    int exp;
    int nextExp;
    int maxLevel;
//    int goodFeeling;
//    int goodFeelingExp;
//    int goodFeelingNextExp;
    //피로도(0이 되면 일정 시간뒤에 100으로 회복, 하루 단위로 완전 회복)
    int fatigability;
    int maxFatigability;
    int reduceSecondFromAD;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime chargingStartTime;
    //링크 어빌리티
    int linkAbilityLevel;
    //링크 어빌리티는 조각 갯수가 경험치.
    int linkAbilityExp;
    //다음 링크 어빌리티 업까지 필요한 조각 갯수
    int linkAbilityNextLevelCost;
    boolean gotcha;

    public MyCharacters ToEntity(int tier) {
        return MyCharacters.builder().useridUser(useridUser).codeHerostable(codeHerostable).tier(tier).build();
    }

    public void InitFromDbData(MyCharacters dbData) {
        id = dbData.getId();
        useridUser = dbData.getUseridUser();
        codeHerostable = dbData.getCodeHerostable();
        grade = dbData.getGrade();
        level = dbData.getLevel();
        exp = dbData.getExp();
        nextExp = dbData.getNextExp();
        maxLevel = dbData.getMaxLevel();
        chargingStartTime = dbData.getChargingStartTime();
//        goodFeeling = dbData.getGoodFeeling();
//        goodFeelingExp = dbData.getGoodFeelingExp();
//        goodFeelingNextExp = dbData.getGoodFeelingNextExp();
        fatigability = dbData.getFatigability();
        maxFatigability = dbData.getMaxFatigability();
        reduceSecondFromAD = dbData.getReduceSecondFromAD();
        linkAbilityLevel = dbData.getLinkAbilityLevel();
        //linkAbilityExp = dbData.getLinkAbilityExp();
        linkAbilityNextLevelCost = dbData.getLinkAbilityNextLevelCost();
        gotcha = dbData.isGotcha();
    }
}