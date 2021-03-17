package com.onlyonegames.eternalfantasia.domain.model.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;

import com.onlyonegames.eternalfantasia.etc.DefineLimitValue;
import com.onlyonegames.eternalfantasia.etc.LinkAbilityCostCalculator;
import com.onlyonegames.util.MathHelper;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MyCharacters extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String codeHerostable;
    int grade;
    int level;
    int exp;
    int nextExp;
    int maxLevel;
    //피로도(0이 되면 일정 시간뒤에 100으로 회복, 하루 단위로 완전 회복)
    int fatigability;
    int maxFatigability;
    //피로도 0이 된 시점. 해당 시점부터 특정 시간 동안 피로도는 0인상태이고 해당 시간이 지나고 나면 다시 100으로 채워짐.영웅 레벨업시 피로도 100으로 리차징
    //2021-02-17 수정으로 피로도가 100 미만이 되기 시작한 순간부터 2시간 뒤에 100으로 리차징
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime chargingStartTime;
    int reduceSecondFromAD;
    //링크 어빌리티
    int linkAbilityLevel;
    //링크 어빌리티는 조각 갯수가 경험치.
    //int linkAbilityExp;
    //다음 링크 어빌리티 업까지 필요한 조각 갯수
    int linkAbilityNextLevelCost;
    boolean gotcha;

    @Builder
    public MyCharacters(Long useridUser, String codeHerostable, int tier) {
        this.useridUser = useridUser;
        this.codeHerostable = codeHerostable;
        InitData(tier);
    }

    public void ClearMyCharacters(int tier) {
        InitData(tier);
    }

    private void InitData(int tier) {
        this.grade = 1;
        this.level = 1;
        this.exp = 0;
        this.nextExp = CalculateExp(level);
        if(this.codeHerostable.equals("hero"))
            this.maxLevel = DefineLimitValue.LIMIT_MAX_HERO_LEVEL;
        else
            this.maxLevel = 1;
//        this.goodFeeling = 0;
//        this.goodFeelingExp = 0;
//        this.goodFeelingNextExp = 1000;
        this.fatigability = 100;
        this.maxFatigability = 100;
        this.gotcha = false;
        this.linkAbilityLevel = tier;
        //this.linkAbilityExp = 0;
        this.linkAbilityNextLevelCost = LinkAbilityCostCalculator.GetNextLinkAbilityCost(linkAbilityLevel);
        this.chargingStartTime = LocalDateTime.now();
    }

    public void setMaxLevel(int level) {
        this.maxLevel = level;
    }

    public void Gotcha() {
        this.gotcha = true;
        this.fatigability = 100;
        this.maxFatigability = 100;
        //this.linkAbilityLevel = 1;
        this.linkAbilityNextLevelCost = LinkAbilityCostCalculator.GetNextLinkAbilityCost(linkAbilityLevel);
    }

    //단순 테스트용도를 위한 함수
    public void NonGotcha() {this.gotcha = false;}

    public boolean AddExp(int exp, int limitLevel) {
        //시스템에 의해서는 해당 조건은 충족 될수 없으나 기타 테스트 API등으로 인해 바뀔수 있으므로 방어코드를 추가한다.
        if(level > limitLevel){
            return false;
        }
        this.exp += exp;
        int previousLevel = this.level;
        this.level = GetLevelFromTotalExpForLv(this.exp);

        if(this.level > limitLevel)
        {
            this.level = limitLevel;
            this.exp = GetNextLevelUpExpForLv(this.level);
        }

//        if(previousLevel < this.level)
//            this.fatigability = maxFatigability;
        this.nextExp = GetNextLevelUpExpForLv(this.level);
        return true;
    }

    public void SetMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }
    ///for developer test
//    public void ResetGoodFeeling() {
//        this.goodFeeling = 0;
//        this.goodFeelingExp = 0;
//        this.goodFeelingNextExp = 1000;
//    }

//    public boolean AddGoodFeelingExp(int exp) {
//        if(goodFeeling >= 5)
//            return false;
//        this.goodFeelingExp += exp;
//        goodFeelingExp = MathHelper.Clamp( goodFeelingExp, 0, 84000);
//        this.goodFeeling = GetLevelFromTotalExpForGoodFeeling(this.goodFeelingExp);
//        goodFeeling = MathHelper.Clamp( goodFeeling, 0, 5);
//        this.goodFeelingNextExp = GetNextGoodFeelingExp(this.goodFeeling);
//        MathHelper.Clamp( goodFeelingNextExp, 0, 84000);
//        return true;
//    }

    //운영툴에서 사용하는 함수
//    public void SetGoodFeelingExp(int exp) {
//        this.goodFeelingExp = exp;
//        this.goodFeeling = GetLevelFromTotalExpForGoodFeeling(exp);
//        this.goodFeelingNextExp = GetNextGoodFeelingExp(goodFeeling);
//    }
//
//    public void SetGoodFeelingLv(int lv) {
//        this.goodFeeling = lv;
//        int totalExp = 0;
//        for(int i = 0; i < lv; i++){
//            totalExp += GetNextGoodFeelingExp(i);
//        }
//        this.goodFeelingExp = totalExp;
//        this.goodFeelingNextExp = GetNextGoodFeelingExp(goodFeeling);
//    }

    public void SetFatigability(int fatigability) {
        this.fatigability = fatigability;
    }

    public void SetLinkAbilityLv(int lv){
        this.linkAbilityLevel = lv;
        this.linkAbilityNextLevelCost = LinkAbilityCostCalculator.GetNextLinkAbilityCost(lv);
    }

    int CalculateExp(int nowLv) {
        if (nowLv <= 0)
            return 0;
        double result = MathHelper.RoundUPMinus1((100 + ((nowLv - 1) * 10) +(nowLv * (nowLv - 1)) * 2));
        return (int)result;
    }

    int GetNextLevelUpExpForLv(int nowLv) {
        if (nowLv == 100)
            nowLv--;
        int tempLv = nowLv;
        int resultExp = 0;
        while (tempLv >= 1)
        {
            resultExp += CalculateExp(tempLv);
            tempLv--;
        }

        return nowLv >= 1 ? resultExp : 0;
    }

    int GetLevelFromTotalExpForLv(int exp) {
        int level = 0;
        int prevExp = 0;
        int maxLoop = DefineLimitValue.LIMIT_MAX_HERO_LEVEL + 1;
        for (int i = 1; i < maxLoop; i++)
        {
            level = i;
            int nextLevelUpExp = GetNextLevelUpExpForLv(i);
            if (prevExp <= exp && exp < nextLevelUpExp)
                break;
            prevExp = nextLevelUpExp;
        }
        return level;
    }

    int GetNextGoodFeelingExp(int nowLv) {
        if (nowLv == 0)
            return 1000;
        if (nowLv == 1)
            return 3000;
        if (nowLv == 2)
            return 10000;
        if (nowLv == 3)
            return 30000;
        if(nowLv >= 4)
            return 50000;
        return 0;
    }

    int GetLevelFromTotalExpForGoodFeeling(int exp) {
        if (exp < 1000)
            return 0;
        else if (exp >= 1000 && exp < 4000)
            return 1;
        else if (exp >= 4000 && exp < 14000)
            return 2;
        else if (exp >= 14000 && exp < 34000)
            return 3;
        else if (exp >= 34000 && exp < 84000)
            return 4;
        else
            return 5;
    }

    public boolean SpendFatigability(int spendFatigability) {

        if(fatigability == 100) {
            chargingStartTime = LocalDateTime.now();
        }

        fatigability -= spendFatigability;
        fatigability = MathHelper.Clamp(fatigability, 0, 100);

        //if(CheckTimeForFatigability(60*60*2/*2시간 간격*/)) {
        if(CheckTimeForFatigability(60*60*2/*2시간 간격*/)) {
            fatigability = 100;
            reduceSecondFromAD = 0;
        }

        return true;
    }

//    public boolean SpendFatigability(int spendFatigability) {
//        if(fatigability == 0) {
//            //피로도 회복 전이라면 곧바로 리턴 false 아니면 피로도 100으로 충전
//            if(!CheckTimeForFatigability(60*60*2/*2시간 간격*/)) {
//            //if(!CheckTimeForFatigability(60/*1분 간격*/)) {//테스트용도
//                return false;
//            }
//            fatigability = 100;
//        }
//        fatigability -= spendFatigability;
//        //해당 전투에 들어갈때 피로도를 다썼다면 다음 피로도 회복 프로세스를 위해 chargingStartTime 설정
//        if(fatigability < 1) {
//            fatigability = 0;
//            chargingStartTime = LocalDateTime.now();
//            reduceSecondFromAD = 0;
//        }
//        return true;
//    }

    /*테스트 용도로 강제 피로도 차징*/
    public void FatigabilityCharging_TEST() {
        fatigability = 100;
    }
    public boolean FatigabilityFullCharging() {
        if(fatigability != 0)
            return false;
        fatigability = 100;
        return true;
    }

    public long RemainChargingFatigabilitySecond(int fullChargingSecond) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(chargingStartTime, now);
        long remainSecond = fullChargingSecond - duration.getSeconds() - reduceSecondFromAD;
        if(remainSecond < 0)
            remainSecond = 0;
        return remainSecond;
    }

    //2020-01-31 광고로 시간을 줄이는 루틴에서 광고로 50% 피로도 채우는것으로 수정
//    public boolean ReduceChargingTimeFromAD(int reduceSecond) {
//        if(reduceSecondFromAD != 0)
//            return false;
//        if(fatigability != 0)
//            return false;
//        this.reduceSecondFromAD = reduceSecond;
//        return true;
//    }
    //광고로 20% 피로도 채움
    public boolean ChargingFatigabilityFromAD() {
        if(reduceSecondFromAD != 0)
            return false;
        if(fatigability != 0)
            return false;
        fatigability = 20;
        return true;
    }
    //물약으로 50% 피로도 채움
    public boolean ChargingFatigabilityFromPosition() {
        if(fatigability != 0)
            return false;
        fatigability = 50;
        return true;
    }

    //피로도0 시점부터 2시간 후에 다시 100으로 채워짐.
    boolean CheckTimeForFatigability(int recycleSecond) {

        if(CheckAfterDayForFatigability())
            return true;
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(chargingStartTime, now);
        return duration.getSeconds() >= (recycleSecond - reduceSecondFromAD);
    }
    //피로도는 매일 23:59:99에 100으로 충전
    public boolean CheckAfterDayForFatigability() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime chargingStartDay = LocalDateTime.of(chargingStartTime.toLocalDate(), LocalTime.of(5,0,0));
        Duration duration = Duration.between(chargingStartDay, now);

        return duration.toDays() >= 1;
    }

//    public void AddPieces(int addCount) {
////        linkAbilityExp += addCount;
////        if(linkAbilityExp > DefineLimitValue.LIMIT_CHARACTER_PECIES)
////            linkAbilityExp = DefineLimitValue.LIMIT_CHARACTER_PECIES;
//    }

//    public boolean SpendPieces(int spendCount) {
////        if(linkAbilityExp < spendCount)
////            return false;
////        linkAbilityExp -= spendCount;
//        return true;
//    }

    public boolean LevelUpLinkAbility() {
        //이미 최대 레벨
        if(linkAbilityLevel >= 6)
            return false;
        linkAbilityLevel++;
        if(!gotcha)
            gotcha = true;
        this.linkAbilityNextLevelCost = LinkAbilityCostCalculator.GetNextLinkAbilityCost(linkAbilityLevel);
        return true;
    }
}