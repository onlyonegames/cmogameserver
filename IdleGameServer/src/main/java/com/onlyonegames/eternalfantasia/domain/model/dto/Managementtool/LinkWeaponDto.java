package com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool;

import com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceWeaponDto.LinkweaponInfoDtosList;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.LinkweaponTalentsTable;
import com.onlyonegames.eternalfantasia.domain.service.Managementtool.HeroInfo.AnalysisTalentOption;

public class LinkWeaponDto {
    public int equipmentId;
    public float value;
    public AnalysisTalentOption.TALENTOPTION_PERCENT_OR_FIXED_KIND TALENTOPTION_HOWTO_PLUSE_KIND;/*해당 값 적용될때 퍼센트로 계산될것인지 아닌지*/
    public AnalysisTalentOption.TALENTOPTION_CONTINUITY_KIND TALENTOPTION_CONTINUITY_KIND;
    public AnalysisTalentOption.TALENTOPTION_KIND TALENTOPTION_KIND;
    public AnalysisTalentOption.TALENTOPTION_PERCENT_OR_FIXED_KIND PERCENT_OR_FIXEDVALUE;/*퍼센트 벨류인지 아닌지*/

    public void setLinkWeaponOptionInfo(LinkweaponTalentsTable linkweaponTalentsTable, LinkweaponInfoDtosList.LinkweaponInfoDto linkweaponInfoDto){
        this.equipmentId = linkweaponInfoDto.id;
        int nRank = 1;
        if(equipmentId != 0)
            nRank = (equipmentId-1)/3+2;
        String temp = linkweaponTalentsTable.getExplain().split("@")[1].replace("+","");
        if(temp.charAt(temp.length()-1) == '%')
            temp = temp.replace("%","");
        this.value = (float)Math.round(Float.parseFloat(temp) + (Float.parseFloat(temp) * (nRank * ((linkweaponInfoDto.upgrade-1) * 0.5f))) + (Float.parseFloat(temp)* ((linkweaponInfoDto.upgrade-1) * (nRank * 0.5f))));
        this.TALENTOPTION_HOWTO_PLUSE_KIND = getTALENTOPTION_PERCENT_OR_FIXED_KIND(linkweaponTalentsTable.getTALENTOPTION_HOWTO_PLUSE_KIND());
        this.TALENTOPTION_CONTINUITY_KIND = getTALENTOPTION_CONTINUITY_KIND(linkweaponTalentsTable.getCONTINUITY_KIND());
        this.TALENTOPTION_KIND = getTALENTOPTION_KIND(linkweaponTalentsTable.getKIND());
        this.PERCENT_OR_FIXEDVALUE = getTALENTOPTION_PERCENT_OR_FIXED_KIND(linkweaponTalentsTable.getTALENTOPTION_HOWTO_PLUSE_KIND());
    }

    AnalysisTalentOption.TALENTOPTION_PERCENT_OR_FIXED_KIND getTALENTOPTION_PERCENT_OR_FIXED_KIND(String k){
        switch(k){
            case "FIXED_VALUE":
                return AnalysisTalentOption.TALENTOPTION_PERCENT_OR_FIXED_KIND.FIXED_VALUE;
            case "PERCENT_VALUE":
                return AnalysisTalentOption.TALENTOPTION_PERCENT_OR_FIXED_KIND.PERCENT_VALUE;
        }
        return AnalysisTalentOption.TALENTOPTION_PERCENT_OR_FIXED_KIND.NONE;
    }

    AnalysisTalentOption.TALENTOPTION_CONTINUITY_KIND getTALENTOPTION_CONTINUITY_KIND(String k){
        switch(k){
            case "PERMANENT_OPTION":
                return AnalysisTalentOption.TALENTOPTION_CONTINUITY_KIND.PERMANENT_OPTION;
            case "PLAING_OPTION":
                return AnalysisTalentOption.TALENTOPTION_CONTINUITY_KIND.PLAING_OPTION;
        }
        return AnalysisTalentOption.TALENTOPTION_CONTINUITY_KIND.NONE;
    }

    AnalysisTalentOption.TALENTOPTION_KIND getTALENTOPTION_KIND(String k){
        switch(k){
            case "HP_UP":
                return AnalysisTalentOption.TALENTOPTION_KIND.HP_UP;
            case "ATTACK_UP":
                return AnalysisTalentOption.TALENTOPTION_KIND.ATTACK_UP;
            case "DEF_UP":
                return AnalysisTalentOption.TALENTOPTION_KIND.DEF_UP;
            case "MDEF_UP":
                return AnalysisTalentOption.TALENTOPTION_KIND.MDEF_UP;
            case "CRITICAL_CHANCE_UP":
                return AnalysisTalentOption.TALENTOPTION_KIND.CRITICAL_CHANCE_UP;
            case "CRITICAL_DMG_UP":
                return AnalysisTalentOption.TALENTOPTION_KIND.CRITICAL_DMG_UP;
            case "PENETRATION_CHANCE_UP":
                return AnalysisTalentOption.TALENTOPTION_KIND.PENETRATION_CHANCE_UP;
            case "PENETRATION_PERCENT_UP":
                return AnalysisTalentOption.TALENTOPTION_KIND.PENETRATION_PERCENT_UP;
            case "ACCURACYRATE_UP":
                return AnalysisTalentOption.TALENTOPTION_KIND.ACCURACYRATE_UP;
            case "EVASIONRATE_UP":
                return AnalysisTalentOption.TALENTOPTION_KIND.EVASIONRATE_UP;
            case "RESISTANCE_CRITICAL_CHANCE":
                return AnalysisTalentOption.TALENTOPTION_KIND.RESISTANCE_CRITICAL_CHANCE;
            case "RESISTANCE_PENETRATION_CHANCE":
                return AnalysisTalentOption.TALENTOPTION_KIND.RESISTANCE_PENETRATION_CHANCE;
            case "RESISTANCE_DEBUFF_CHANCE":
                return AnalysisTalentOption.TALENTOPTION_KIND.RESISTANCE_DEBUFF_CHANCE;
            case "PENETRATE_HIT_DMG_UP":
                return AnalysisTalentOption.TALENTOPTION_KIND.PENETRATE_HIT_DMG_UP;
            case "SINGLE_HIT_DMG_UP":
                return AnalysisTalentOption.TALENTOPTION_KIND.SINGLE_HIT_DMG_UP;
            case "BOSS_DMG_UP":
                return AnalysisTalentOption.TALENTOPTION_KIND.BOSS_DMG_UP;
            case "DMG_PERCENT_DOWN":
                return AnalysisTalentOption.TALENTOPTION_KIND.DMG_PERCENT_DOWN;
            case "ATTACK_SPEED_UP":
                return AnalysisTalentOption.TALENTOPTION_KIND.ATTACK_SPEED_UP;
            case "MOVE_SPEED_UP":
                return AnalysisTalentOption.TALENTOPTION_KIND.MOVE_SPEED_UP;
            case "SKILL_LEVEL_UP":
                return AnalysisTalentOption.TALENTOPTION_KIND.SKILL_LEVEL_UP;
            case "SUPERARMOR_ON":
                return AnalysisTalentOption.TALENTOPTION_KIND.SUPERARMOR_ON;
            case "REFLECT_DMG":
                return AnalysisTalentOption.TALENTOPTION_KIND.REFLECT_DMG;
            case "SUMMON_ABILITY_PERCENT_UP":
                return AnalysisTalentOption.TALENTOPTION_KIND.SUMMON_ABILITY_PERCENT_UP;
            case "BLOODSUCKING_ABILITY_ON":
                return AnalysisTalentOption.TALENTOPTION_KIND.BLOODSUCKING_ABILITY_ON;
            case "SKILL_EFFECT_UP":
                return AnalysisTalentOption.TALENTOPTION_KIND.SKILL_EFFECT_UP;
        }
        return AnalysisTalentOption.TALENTOPTION_KIND.NONE;
    }

//    AnalysisTalentOption.TALENTOPTION_REQUIRE_KIND getTALENTOPTION_REQUIRE_KIND(String k){
//        switch(k){
//            case "KILL_ENEMY":
//                return AnalysisTalentOption.TALENTOPTION_REQUIRE_KIND.KILL_ENEMY;
//            case "USE_SKILL":
//                return AnalysisTalentOption.TALENTOPTION_REQUIRE_KIND.USE_SKILL;
//            case "LESS_HP":
//                return AnalysisTalentOption.TALENTOPTION_REQUIRE_KIND.LESS_HP;
//            case "TEAM_LESS_HP":
//                return AnalysisTalentOption.TALENTOPTION_REQUIRE_KIND.TEAM_LESS_HP;
//            case "MYSTERIOUSDANCE_SKILL_USE":
//                return AnalysisTalentOption.TALENTOPTION_REQUIRE_KIND.MYSTERIOUSDANCE_SKILL_USE;
//            case "COLOSSEUM":
//                return AnalysisTalentOption.TALENTOPTION_REQUIRE_KIND.COLOSSEUM;
//            case "HIT_AND_CHANCE":
//                return AnalysisTalentOption.TALENTOPTION_REQUIRE_KIND.HIT_AND_CHANCE;
//            case "HIT_AND_TIME_AND_STACKLIMIT":
//                return AnalysisTalentOption.TALENTOPTION_REQUIRE_KIND.HIT_AND_TIME_AND_STACKLIMIT;
//            case "CHANCE":
//                return AnalysisTalentOption.TALENTOPTION_REQUIRE_KIND.CHANCE;
//            case "LESS_HP_AND_TIME":
//                return AnalysisTalentOption.TALENTOPTION_REQUIRE_KIND.LESS_HP_AND_TIME;
//            case "LESS_HP_50_40_30":
//                return AnalysisTalentOption.TALENTOPTION_REQUIRE_KIND.LESS_HP_50_40_30;
//            case "HIT_AND_CHANCE_AND_TIME":
//                return AnalysisTalentOption.TALENTOPTION_REQUIRE_KIND.HIT_AND_CHANCE_AND_TIME;
//            case "USE_SKILL_AND_TIME":
//                return AnalysisTalentOption.TALENTOPTION_REQUIRE_KIND.USE_SKILL_AND_TIME;
//            case "CRITICAL_HITON":
//                return AnalysisTalentOption.TALENTOPTION_REQUIRE_KIND.CRITICAL_HITON;
//            case "TEAM_DEATH_AND_CHANCE":
//                return AnalysisTalentOption.TALENTOPTION_REQUIRE_KIND.TEAM_DEATH_AND_CHANCE;
//            case "USE_MANA_AND_TIME_AND_STACKLIMIT":
//                return AnalysisTalentOption.TALENTOPTION_REQUIRE_KIND.USE_MANA_AND_TIME_AND_STACKLIMIT;
//            case "HITON_AND_CHANCE_AND_TIME":
//                return AnalysisTalentOption.TALENTOPTION_REQUIRE_KIND.HITON_AND_CHANCE_AND_TIME;
//            case "HITON_AND_TIME_AND_STACKLIMIT":
//                return AnalysisTalentOption.TALENTOPTION_REQUIRE_KIND.HITON_AND_TIME_AND_STACKLIMIT;
//        }
//        return AnalysisTalentOption.TALENTOPTION_REQUIRE_KIND.NONE;
//    }
}
