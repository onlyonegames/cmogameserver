package com.onlyonegames.eternalfantasia.etc;

public class LinkAbilityCostCalculator {

    public static int GetNextLinkAbilityCost(int nowLv) {
        if (nowLv == 0)
            return 50;
        if (nowLv == 1)
            return 30;
        if (nowLv == 2)
            return 60;
        if (nowLv == 3)
            return 120;
        if (nowLv == 4)
            return 200;
        if(nowLv >= 5)
            return 300;
        return 0;
    }
}
