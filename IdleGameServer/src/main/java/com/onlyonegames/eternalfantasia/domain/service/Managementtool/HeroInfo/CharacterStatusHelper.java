package com.onlyonegames.eternalfantasia.domain.service.Managementtool.HeroInfo;

import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.HeroStatusDto;
import com.onlyonegames.util.MathHelper;

public class CharacterStatusHelper {

    public static class SUM_Status
    {
        public float SUM_HP = 0.0f; // 체력
        public float SUM_ATK = 0.0f; // 공격력
        public float SUM_DEF = 0.0f; // 물리 방어력
        public float SUM_MDEF = 0.0f; // 마법 방어력
        public float SUM_CRITICAL_CHANCE = 0.0f; // 치명타 확률
        public float SUM_CRITICAL_PERCENT = 0.0f; // 치명타 량
        public float SUM_PENETRATION_CHANCE = 0.0f; // 관통 확률
        public float SUM_PENETRATION_PERCENT = 0.0f; // 관통 량
        public float SUM_ACCURACY = 0.0f; // 명중률
        public float SUM_EVASION = 0.0f; // 회피율
        public float SUM_CRITICAL_RESISTANCE = 0.0f;  // 치명 저항
        public float SUM_PENETRATION_RESISTANCE = 0.0f; // 관통 저항
        public float SUM_DEBUFF_RESISTANCE = 0.0f; // 해로운 효과 저항
        public float SUM_PENETRATE_HIT_DAMAGE_PERCENT = 0.0f; // 광역 피해량
        public float SUM_SINGLE_HIT_DAMAGE_PERCENT = 0.0f; // 단일 피해량
        public float SUM_BOSS_DAMAGE_PERCENT = 0.0f; // 보스 피해량
        public float SUM_DAMAGE_DOWN_PERCENT = 0.0f; // 자신이 받는 피해량 감소
        public float SUM_ATTACK_SPEED_PERCENT = 0.0f; // 공격 속도
        public float SUM_MOVE_SPEED_PERCENT = 0.0f;  // 이동속도
    }

    public static class SumTalentStatus
    {
        public static void SumTalent(SUM_Status _sumStatus, HeroStatusDto characterInfo, AnalysisTalentOption.TALENTOPTION_KIND TALENTOPTION_KIND, double talentOptionValue, AnalysisTalentOption.TALENTOPTION_PERCENT_OR_FIXED_KIND pluseKind)
        {
            switch (TALENTOPTION_KIND)
            {
                case HP_UP:
                    _sumStatus.SUM_HP += Math.round(AnalysisTalentOption.ReturnPluseValueEachPluseKind(characterInfo.getHealthPoint(), talentOptionValue, pluseKind));
                    break;
                case ATTACK_UP:
                    _sumStatus.SUM_ATK += Math.round(AnalysisTalentOption.ReturnPluseValueEachPluseKind(characterInfo.getAttackPower(), talentOptionValue, pluseKind));
                    break;
                case DEF_UP:
                    _sumStatus.SUM_DEF += Math.round(AnalysisTalentOption.ReturnPluseValueEachPluseKind(characterInfo.getPhysicalDefence(), talentOptionValue, pluseKind));
                    break;
                case MDEF_UP:
                    _sumStatus.SUM_MDEF += Math.round(AnalysisTalentOption.ReturnPluseValueEachPluseKind(characterInfo.getMagicDefence(), talentOptionValue, pluseKind));
                    break;
                case CRITICAL_CHANCE_UP: // 3 자릿수 까지 올림
                    _sumStatus.SUM_CRITICAL_CHANCE += (float)MathHelper.Round2(AnalysisTalentOption.ReturnPluseValueEachPluseKind(characterInfo.getCriticalChance(), talentOptionValue, pluseKind));
                    break;
                case CRITICAL_DMG_UP: // 3 자릿수 까지 올림
                    _sumStatus.SUM_CRITICAL_PERCENT += (float)MathHelper.Round2(AnalysisTalentOption.ReturnPluseValueEachPluseKind(characterInfo.getCriticalPercent(), talentOptionValue, pluseKind));
                    break;
                case PENETRATION_CHANCE_UP: // 3 자릿수 까지 올림
                    _sumStatus.SUM_PENETRATION_CHANCE += (float)MathHelper.Round2(AnalysisTalentOption.ReturnPluseValueEachPluseKind(characterInfo.getPenetrationChance(), talentOptionValue, pluseKind));
                    break;
                case PENETRATION_PERCENT_UP: // 3 자릿수 까지 올림
                    _sumStatus.SUM_PENETRATION_PERCENT += (float)MathHelper.Round2(AnalysisTalentOption.ReturnPluseValueEachPluseKind(characterInfo.getPenetrationPercent(), talentOptionValue, pluseKind));
                    break;
                case ACCURACYRATE_UP: // 3 자릿수 까지 올림
                    _sumStatus.SUM_ACCURACY += (float)MathHelper.Round2(AnalysisTalentOption.ReturnPluseValueEachPluseKind(characterInfo.getAccuracyRate(), talentOptionValue, pluseKind));
                    break;
                case EVASIONRATE_UP: // 3 자릿수 까지 올림
                    _sumStatus.SUM_EVASION += (float)MathHelper.Round2(AnalysisTalentOption.ReturnPluseValueEachPluseKind(characterInfo.getEvasionRate(), talentOptionValue, pluseKind));
                    break;
                case ATTACK_SPEED_UP:
                    _sumStatus.SUM_ATTACK_SPEED_PERCENT += (float)MathHelper.Round2(AnalysisTalentOption.ReturnPluseValueEachPluseKind(characterInfo.getEvasionRate(), talentOptionValue, pluseKind));
                    break;

            }
        }
    }
}
