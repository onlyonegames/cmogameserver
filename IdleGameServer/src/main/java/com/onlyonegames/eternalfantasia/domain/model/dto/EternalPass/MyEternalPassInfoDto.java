package com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass;

import com.onlyonegames.eternalfantasia.etc.DefineLimitValue;
import com.onlyonegames.util.MathHelper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MyEternalPassInfoDto {

    int eternalPassId;
    int passLevel;
    int exp;
    int nextExp;
    boolean hasBuyRoyalPass;
    List<Integer> gettedFreePassItemList = new ArrayList<>();
    List<Integer> gettedRoyalPassItemList = new ArrayList<>();

    public void Reset(int resetPassId) {

        this.eternalPassId = resetPassId;
        this.passLevel = 0;
        this.exp = 0;
        this.nextExp = 0;
        this.hasBuyRoyalPass = false;
        gettedFreePassItemList.clear();
        gettedRoyalPassItemList.clear();
        for(int i = 0; i < DefineLimitValue.LIMIT_MAX_PASS_LEVEL; i++) {
            gettedFreePassItemList.add(0);
            gettedRoyalPassItemList.add(0);
        }
    }

    public boolean ReceiveFreePassRward(int selectRewardPassLevel) {
        int selectedIndex = selectRewardPassLevel - 1;
        if(gettedFreePassItemList.get(selectedIndex) == 1)
            return false;
        gettedFreePassItemList.set(selectedIndex, 1);
        return true;
    }

    public boolean ReceiveRoyalPassRward(int selectRewardPassLevel) {
        int selectedIndex = selectRewardPassLevel - 1;
        if(gettedRoyalPassItemList.get(selectedIndex) == 1)
            return false;
        gettedRoyalPassItemList.set(selectedIndex, 1);
        return true;
    }

    public boolean ReceiveAllReward() {
        boolean received = false;
        int i = 0;
        for(Integer freePassReceiveFlage : gettedFreePassItemList){
            if(i + 1 > passLevel)
                break;
            if(freePassReceiveFlage == 0) {
                received = true;
                gettedFreePassItemList.set(i, 1);
            }
            i++;
        }
        if(hasBuyRoyalPass) {
            i = 0;
            for(Integer royalPassReceiveFlage : gettedRoyalPassItemList){
                if(i + 1 > passLevel)
                    break;
                if(royalPassReceiveFlage == 0) {
                    received = true;
                    gettedRoyalPassItemList.set(i, 1);
                }
                i++;
            }
        }

        return received;
    }

    public void BuyRoyalPass(){
        this.hasBuyRoyalPass = true;
    }

    public boolean DirectLevelUp(int pluseLevel, int limitLevel) {

        if(passLevel + pluseLevel > limitLevel){
            return false;
        }
        passLevel += pluseLevel;
        this.exp = GetNextLevelUpExpForLv(passLevel - 1);
        this.nextExp = GetNextLevelUpExpForLv(this.passLevel);
        return true;
    }

    public boolean AddExp(int exp, int limitLevel) {
        //시스템에 의해서는 해당 조건은 충족 될수 없으나 기타 테스트 API등으로 인해 바뀔수 있으므로 방어코드를 추가한다.
        if(passLevel > limitLevel){
            return false;
        }
        this.exp += exp;
        int previousLevel = this.passLevel;
        this.passLevel = GetLevelFromTotalExpForLv(this.exp);

        if(this.passLevel > limitLevel)
        {
            this.passLevel = limitLevel;
            this.exp = GetNextLevelUpExpForLv(this.passLevel);
        }

//        if(previousLevel < this.level)
//            this.fatigability = maxFatigability;
        this.nextExp = GetNextLevelUpExpForLv(this.passLevel);
        return true;
    }

    int GetLevelFromTotalExpForLv(int exp) {
        int level = 0;
        int prevExp = 0;
        int maxLoop = DefineLimitValue.LIMIT_MAX_PASS_LEVEL + 1;
        for (int i = 0; i < maxLoop; i++)
        {
            level = i;
            int nextLevelUpExp = GetNextLevelUpExpForLv(i);
            if (prevExp <= exp && exp < nextLevelUpExp)
                break;
            prevExp = nextLevelUpExp;
        }
        return level;
    }

    int GetNextLevelUpExpForLv(int nowLv) {
        if (nowLv == 100)
            nowLv--;
        int tempLv = nowLv;
        int resultExp = 0;
        while (tempLv >= 0)
        {
            resultExp += CalculateExp(tempLv);
            tempLv--;
        }

        return nowLv >= 0 ? resultExp : 0;
    }

    int CalculateExp(int nowLv) {
        if (nowLv < 0)
            return 0;
        float result = (float) MathHelper.RoundUPMinus2((100 + (nowLv * 10) +(nowLv * nowLv)));
        return (int)result;
    }
}
