package com.onlyonegames.eternalfantasia.domain.service.Managementtool.HeroInfo;

import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.EquipmentCalculatedDto.EquipmentInfo;

public class AnalysisTalentOption {
    public enum TALENTOPTION_PERCENT_OR_FIXED_KIND
    {
        NONE,
        FIXED_VALUE,
        PERCENT_VALUE
    }
    public enum TALENTOPTION_CONTINUITY_KIND
    {
        NONE,
        PERMANENT_OPTION,
        PLAING_OPTION
    }

    public enum TALENTOPTION_KIND
    {
        NONE,
        HP_UP,
        ATTACK_UP,
        DEF_UP,
        MDEF_UP,
        CRITICAL_CHANCE_UP,
        CRITICAL_DMG_UP,
        PENETRATION_CHANCE_UP,
        PENETRATION_PERCENT_UP,
        ACCURACYRATE_UP,
        EVASIONRATE_UP,
        RESISTANCE_CRITICAL_CHANCE,
        RESISTANCE_PENETRATION_CHANCE,
        RESISTANCE_DEBUFF_CHANCE,
        PENETRATE_HIT_DMG_UP,
        SINGLE_HIT_DMG_UP,
        BOSS_DMG_UP,
        DMG_PERCENT_DOWN,
        ATTACK_SPEED_UP,
        MOVE_SPEED_UP,
        SKILL_LEVEL_UP,
        SUPERARMOR_ON,
        REFLECT_DMG,
        SUMMON_ABILITY_PERCENT_UP,
        BLOODSUCKING_ABILITY_ON,
        SKILL_EFFECT_UP,
        //인장 효과
        PASSIVE_OF_NOX,
        PASSIVE_OF_FLAMA,
        PASSIVE_OF_PONTUS,
        PASSIVE_OF_PAPYRIO,
        PASSIVE_OF_CHALYBS,
        PASSIVE_OF_TERA,
        PASSIVE_OF_LUMEN,
        PASSIVE_OF_HERO,
        PASSIVE_OF_GODDESS

    }

    static public double ReturnPluseValueEachPluseKind(float targetValue, double pluseValue, TALENTOPTION_PERCENT_OR_FIXED_KIND pluseKind)
    {
        if (pluseKind == TALENTOPTION_PERCENT_OR_FIXED_KIND.FIXED_VALUE)
            return pluseValue;
        else if (pluseKind == TALENTOPTION_PERCENT_OR_FIXED_KIND.PERCENT_VALUE)
            return (targetValue * (pluseValue * 0.01f));
        return 0.0f;
    }

    ////////////////////////////////////////////////////////////////////
    //
    // 2020-04-27 재형: 영웅 장비 기본 능력 상승 타입 Talent Kind 가져오기
    // Default와 Second가 각각 다르계 가져온다.
    public static TALENTOPTION_KIND GetMainHeroEquipmentDefaultAbilityKind(EquipmentInfo item)
    {
        if ("Sword".equals(item.kind) || "Spear".equals(item.kind) || "Bow".equals(item.kind) || "Gun".equals(item.kind) || "Wand".equals(item.kind)) // # 무기
            return TALENTOPTION_KIND.ATTACK_UP; // 물리 공격력 : 마법 공격력

        if ("Armor".equals(item.kind)) // # 갑옷
            return TALENTOPTION_KIND.DEF_UP; //"물리 방어력"

        if ("Helmet".equals(item.kind)) // # 투구
            return TALENTOPTION_KIND.HP_UP; //"생명력"

        if ("Accessory".equals(item.kind)) // # 악세사리
        {
            switch(item.secondKind){
                case "Sword":
                    return TALENTOPTION_KIND.ACCURACYRATE_UP; //"명중률"
                case "Spear":
                    return TALENTOPTION_KIND.PENETRATION_PERCENT_UP; //"관통력"
                case "Bow":
                    return TALENTOPTION_KIND.CRITICAL_DMG_UP; // "치명 피해량"
                case "Gun":
                    return TALENTOPTION_KIND.PENETRATION_CHANCE_UP; //"관통 확률"
                case "Wand":
                    return TALENTOPTION_KIND.MDEF_UP; //"마법 저항력"
            }
        }

        return TALENTOPTION_KIND.NONE;
    }

    // Default와 Second가 각각 다르계 가져온다.
    public static TALENTOPTION_KIND GetMainHeroEquipmentSecondAbilityKind(EquipmentInfo item)
    {
        if ("Sword".equals(item.kind) || "Spear".equals(item.kind) || "Bow".equals(item.kind) || "Gun".equals(item.kind) || "Wand".equals(item.kind)) // # 무기
        {
            switch(item.secondKind){
                case "Sword":
                    return TALENTOPTION_KIND.DEF_UP; //"물리 방어력"
                case "Spear":
                    return TALENTOPTION_KIND.PENETRATION_CHANCE_UP; //"관통 확률"
                case "Bow":
                    return TALENTOPTION_KIND.CRITICAL_CHANCE_UP; //"치명타 확률"
                case "Gun":
                    return TALENTOPTION_KIND.PENETRATION_PERCENT_UP; // "관통력"
                case "Wand":
                    return TALENTOPTION_KIND.ACCURACYRATE_UP; //"명중률"
            }
        }
        if ("Armor".equals(item.kind)) // # 갑옷
        {
            switch(item.secondKind){
                case "Sword":
                    return TALENTOPTION_KIND.HP_UP; // "생명력"
                case "Spear":
                    return TALENTOPTION_KIND.PENETRATION_PERCENT_UP; // "관통력"
                case "Bow":
                    return TALENTOPTION_KIND.ACCURACYRATE_UP; //"명중률"
                case "Gun":
                    return TALENTOPTION_KIND.ACCURACYRATE_UP; //"명중률"
                case "Wand":
                    return TALENTOPTION_KIND.MDEF_UP; //"마법 저항력"
            }
        }
        if ("Helmet".equals(item.kind)) // # 투구
        {
            switch(item.secondKind){
                case "Sword":
                    return TALENTOPTION_KIND.MDEF_UP; //"마법 저항력"
                case "Spear":
                    return TALENTOPTION_KIND.ATTACK_UP; //"공격력"
                case "Bow":
                    return TALENTOPTION_KIND.CRITICAL_DMG_UP; //"치명 피해량"
                case "Gun":
                    return TALENTOPTION_KIND.DEF_UP; //"물리 방어력"
                case "Wand":
                    return TALENTOPTION_KIND.CRITICAL_DMG_UP; //"치명 피해량"
            }
        }
        if ("Accessory".equals(item.kind)) // # 악세사리
        {
            switch(item.secondKind){
                case "Sword":
                    return TALENTOPTION_KIND.EVASIONRATE_UP; //"회피율"
                case "Spear":
                    return TALENTOPTION_KIND.ATTACK_UP; //"공격력"
                case "Bow":
                    return TALENTOPTION_KIND.ACCURACYRATE_UP; //"명중률"
                case "Gun":
                    return TALENTOPTION_KIND.PENETRATION_PERCENT_UP; //"관통력"
                case "Wand":
                    return TALENTOPTION_KIND.ATTACK_UP; //"공격력"
            }
        }

        return TALENTOPTION_KIND.NONE;
    }
}
