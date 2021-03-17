package com.onlyonegames.eternalfantasia.domain.service.Managementtool.HeroInfo;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceDto.LinkforceOpenDtosList.CompanionLinkforceOpenInfo;
import com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceWeaponDto.LinkweaponInfoDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.*;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.Managementtool.HeroInfo.CharacterStatusHelper.SUM_Status;
import com.onlyonegames.eternalfantasia.domain.service.Managementtool.HeroInfo.CharacterStatusHelper.SumTalentStatus;
import lombok.AllArgsConstructor;

import java.util.List;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@AllArgsConstructor
public class HeroCalculate {

    private static ErrorLoggingService errorLoggingService;
    // 매인 영웅
    public static void CalculateStatusFromMainHero(Long userId, int _level, HeroStatusDto growthStatus, HeroStatusDto calculatedStatus, herostable herostable, EquipmentCalculatedDto selectedDecks, List<EquipmentOptionsInfoTable> equipmentOptionsInfoTable)
    {
        SetGrowthStatusFromLevel(_level, growthStatus, herostable);
        SetCalculateStatusFromMainHero(userId, growthStatus, calculatedStatus, selectedDecks, equipmentOptionsInfoTable);
    }

    // 동료
    public static void CalculateStatusFromCharacter(int _level, int condition, HeroStatusDto growthStatus,
                                                    HeroStatusDto calculatedStatus, herostable herostable,
                                                    List<EquipmentOptionsInfoTable> equipmentOptionsInfoTable,
                                                    LegionCostumeTable legionCostumeInventory,
                                                    List<LinkforceTalentsTable> linkforceTalentsTableList,
                                                    CompanionLinkforceOpenInfo companionLinkforceOpenInfo,
                                                    List<LinkweaponTalentsTable> linkweaponTalentsTables,
                                                    List<LinkweaponInfoDtosList.LinkweaponInfoDto> linkweaponInfoDtoList)
    {
        SetGrowthStatusFromLevel(_level, growthStatus, herostable);
        SetCalculateStatusFromCharacter(growthStatus, calculatedStatus, equipmentOptionsInfoTable, legionCostumeInventory, linkforceTalentsTableList, companionLinkforceOpenInfo, linkweaponTalentsTables, linkweaponInfoDtoList);
        SetCalculateCondition(calculatedStatus, condition);
    }

    public static void SetGrowthStatusFromLevel(int _level, HeroStatusDto growthStatus, herostable herostable)
    {
        // 레벨업 능력치 상승 공식 = 기본값 + ((기본값 * 0.1) * (레벨 - 1))
        growthStatus.healthPoint = (float)Math.round(herostable.getHealthPoint() + ((herostable.getHealthPoint() * 0.1f) * (_level - 1)));
        growthStatus.attackPower = (float)Math.round(herostable.getAttackPower() + ((herostable.getAttackPower() * 0.1f) * (_level - 1)));
        growthStatus.physicalDefence = (float)Math.round(herostable.getPhysicalDefence() + ((herostable.getPhysicalDefence() * 0.1f) * (_level - 1)));
        growthStatus.magicDefence = (float)Math.round(herostable.getMagicDefence() + ((herostable.getMagicDefence() * 0.1f) * (_level - 1)));

        growthStatus.attackRate = herostable.getAttackRate();
        growthStatus.setAttackSpeed(1.0f);
        growthStatus.setCriticalChance(herostable.getCriticalChance());
        growthStatus.setCriticalPercent(herostable.getCriticalPercent());
        growthStatus.setPenetrationChance(herostable.getPenetrationChance());
        growthStatus.setPenetrationPercent(herostable.getPenetrationPercent());
        growthStatus.setAccuracyRate(herostable.getAccuracyRate());
        growthStatus.setEvasionRate(herostable.getEvasionRate());
    }

    public static void SetCalculateStatusFromMainHero(Long userId, HeroStatusDto growthStatus, HeroStatusDto calculatedStatus, EquipmentCalculatedDto selectedDecks, List<EquipmentOptionsInfoTable> equipmentOptionsInfoTable)
    {
        calculatedStatus.healthPoint = growthStatus.healthPoint;
        calculatedStatus.attackPower = growthStatus.attackPower;
        calculatedStatus.physicalDefence = growthStatus.physicalDefence;
        calculatedStatus.magicDefence = growthStatus.magicDefence;

        calculatedStatus.attackRate = growthStatus.attackRate;
        calculatedStatus.setAttackSpeed(growthStatus.getAttackSpeed());
        calculatedStatus.setCriticalChance(growthStatus.getCriticalChance());
        calculatedStatus.setCriticalPercent(growthStatus.getCriticalPercent());
        calculatedStatus.setPenetrationChance(growthStatus.getPenetrationChance());
        calculatedStatus.setPenetrationPercent(growthStatus.getPenetrationPercent());
        calculatedStatus.setAccuracyRate(growthStatus.getAccuracyRate());
        calculatedStatus.setEvasionRate(growthStatus.getEvasionRate());

        SetMainHeroEquipment(userId, growthStatus, calculatedStatus, selectedDecks, equipmentOptionsInfoTable);
    }

    public static void SetCalculateStatusFromCharacter(HeroStatusDto growthStatus, HeroStatusDto calculatedStatus,
                                                       List<EquipmentOptionsInfoTable> equipmentOptionsInfoTable,
                                                       LegionCostumeTable legionCostumeInventory,
                                                       List<LinkforceTalentsTable> linkforceTalentsTableList,
                                                       CompanionLinkforceOpenInfo companionLinkforceOpenInfo,
                                                       List<LinkweaponTalentsTable> linkweaponTalentsTables,
                                                       List<LinkweaponInfoDtosList.LinkweaponInfoDto> linkweaponInfoDtoList)
    {
        calculatedStatus.healthPoint = growthStatus.healthPoint;
        calculatedStatus.attackPower = growthStatus.attackPower;
        calculatedStatus.physicalDefence = growthStatus.physicalDefence;
        calculatedStatus.magicDefence = growthStatus.magicDefence;

        calculatedStatus.attackRate = growthStatus.attackRate;
        calculatedStatus.setAttackSpeed(growthStatus.getAttackSpeed());
        calculatedStatus.setCriticalChance(growthStatus.getCriticalChance());
        calculatedStatus.setCriticalPercent(growthStatus.getCriticalPercent());
        calculatedStatus.setPenetrationChance(growthStatus.getPenetrationChance());
        calculatedStatus.setPenetrationPercent(growthStatus.getPenetrationPercent());
        calculatedStatus.setAccuracyRate(growthStatus.getAccuracyRate());
        calculatedStatus.setEvasionRate(growthStatus.getEvasionRate());

        SetCalculateCostume(legionCostumeInventory, calculatedStatus, equipmentOptionsInfoTable);
        SetCalculateLinkForceTalent(linkforceTalentsTableList, companionLinkforceOpenInfo, calculatedStatus);
        SetCalculateLinkWeapon(linkweaponTalentsTables,  linkweaponInfoDtoList, calculatedStatus);
    }

    public static void SetCalculateCostume(LegionCostumeTable legionCostumeInventory, HeroStatusDto calculatedStatus, List<EquipmentOptionsInfoTable> equipmentOptionsInfoTable)
    {
        if(legionCostumeInventory.getOptionIds().equals("-"))
            return;
        List<EquipmentOptionsInfoTable> equipmentOptionsInfoTables = equipmentOptionsInfoTable;
        EquipmentOptionsInfoTable option = equipmentOptionsInfoTables
                .stream().filter(i -> i.getID() == Integer.parseInt(legionCostumeInventory.getOptionIds()))
                .findAny()
                .orElse(option = null);
        EquipmentOptionInfoDto optionInfo = new EquipmentOptionInfoDto();

        SUM_Status sumStatus = new SUM_Status();

        if(option != null) {
            optionInfo.setEquipmentOptionInfo(option);
            SumTalentStatus.SumTalent(sumStatus, calculatedStatus, optionInfo.TALENTOPTION_KIND, Float.parseFloat(legionCostumeInventory.getValues()), optionInfo.TALENTOPTION_HOWTO_PLUSE_KIND);
        }

        /**/
        AddCalculatedStatusToSumStatus(sumStatus, calculatedStatus);
    }

    // 링크 포스 계산
    public static void SetCalculateLinkForceTalent(List<LinkforceTalentsTable> linkforceTalentsTableList, CompanionLinkforceOpenInfo companionLinkforceOpenInfo, HeroStatusDto calculatedStatus)
    {
        SUM_Status sumStatus = new SUM_Status();

        LinkForceDto optionInfo = new LinkForceDto();
        int count = companionLinkforceOpenInfo.linkforceOpenInfoList.size();
        for (int i = 0; i < count; i++)
        {
            optionInfo.setLinkForceOptionInfo(linkforceTalentsTableList.get(i));
            if(companionLinkforceOpenInfo.linkforceOpenInfoList.get(i) == 1){
                if (optionInfo.TALENTOPTION_CONTINUITY_KIND != AnalysisTalentOption.TALENTOPTION_CONTINUITY_KIND.PERMANENT_OPTION)
                continue;

                SumTalentStatus.SumTalent(sumStatus, calculatedStatus, optionInfo.TALENTOPTION_KIND, optionInfo.value, optionInfo.TALENTOPTION_HOWTO_PLUSE_KIND);
            }
        }

        /**/
        AddCalculatedStatusToSumStatus(sumStatus, calculatedStatus);
    }

    // 링크 웨폰(동료 장비) 계산
    public static void SetCalculateLinkWeapon(List<LinkweaponTalentsTable> linkweaponTalentsTables, List<LinkweaponInfoDtosList.LinkweaponInfoDto> linkweaponInfoDtoList, HeroStatusDto calculatedStatus)
    {
        SUM_Status sumStatus = new SUM_Status();
        LinkWeaponDto option = new LinkWeaponDto();

        int count = linkweaponTalentsTables.size();
        for (int i = 0; i < count; i++)
        {
            option.setLinkWeaponOptionInfo(linkweaponTalentsTables.get(i), linkweaponInfoDtoList.get(i));
            if (!linkweaponInfoDtoList.get(i).open|| option.TALENTOPTION_CONTINUITY_KIND != AnalysisTalentOption.TALENTOPTION_CONTINUITY_KIND.PERMANENT_OPTION)
                continue;
            SumTalentStatus.SumTalent(sumStatus, calculatedStatus, option.TALENTOPTION_KIND, option.value, option.TALENTOPTION_HOWTO_PLUSE_KIND);
        }

        /**/
        AddCalculatedStatusToSumStatus(sumStatus, calculatedStatus);
    }

    public static void SetCalculateCondition(HeroStatusDto calculatedStatus, int condition)
    {
        if (condition > 0)
            return;

        /**/
        calculatedStatus.healthPoint *= 0.5f;

        calculatedStatus.attackPower *= 0.5f;
        calculatedStatus.physicalDefence *= 0.5f;
        calculatedStatus.magicDefence *= 0.5f;

        calculatedStatus.setCriticalChance(calculatedStatus.getCriticalChance() * 0.5f);
        calculatedStatus.setCriticalPercent(calculatedStatus.getCriticalPercent() * 0.5f);
        calculatedStatus.setPenetrationChance(calculatedStatus.getPenetrationChance() * 0.5f);
        calculatedStatus.setPenetrationPercent(calculatedStatus.getPenetrationPercent() * 0.5f);
        calculatedStatus.setAccuracyRate(calculatedStatus.getAccuracyRate() * 0.5f);
        calculatedStatus.setEvasionRate(calculatedStatus.getEvasionRate() * 0.5f);
    }

    public static void SetMainHeroEquipment(Long userId, HeroStatusDto growthStatus, HeroStatusDto calculatedStatus, EquipmentCalculatedDto selectedDecks, List<EquipmentOptionsInfoTable> equipmentOptionsInfoTable)
    {
        /* 장착 중인 장비 */
        EquipmentCalculatedDto selectedDeck = selectedDecks;

        // 계산된 값 = (레벨업 된 능력치 + (장비능력*공명값)) + (장비옵션*공명값) : 공명, 장비 능력, 옵션 증가

        MainHeroEquipmentAbilityCalculate(selectedDeck, growthStatus, calculatedStatus);

        MainHeroEquipmentOptionCalculate(userId, selectedDeck, growthStatus, calculatedStatus, equipmentOptionsInfoTable);
    }

    public static void MainHeroEquipmentAbilityCalculate(EquipmentCalculatedDto selectedDeck, HeroStatusDto growthStatus, HeroStatusDto calculatedStatus)
    {
        SUM_Status sumStatus = new SUM_Status();

        float sameTempValue = 0.0f; //공명으로 상승된 계산된 값

        /* 장비 능력치 계산 */
        EquipmentCalculatedDto.EquipmentInfo item = null;
        int Count = selectedDeck.getEquipmentInfoList().size();
        for (int i = 0; i < Count; i++)
        {
            item = selectedDeck.getEquipmentInfoList().get(i);
            if (item == null)
                continue;

            // DefaultAbility
            sameTempValue = item.decideDefaultAbilityValue * selectedDeck.GetSameSetOptionValue(item);
            SumTalentStatus.SumTalent(sumStatus, growthStatus, AnalysisTalentOption.GetMainHeroEquipmentDefaultAbilityKind(item), item.decideDefaultAbilityValue + sameTempValue, AnalysisTalentOption.TALENTOPTION_PERCENT_OR_FIXED_KIND.FIXED_VALUE);

            // SecondAbility
            sameTempValue = item.decideSecondAbilityValue * selectedDeck.GetSameSetOptionValue(item);
            SumTalentStatus.SumTalent(sumStatus, growthStatus, AnalysisTalentOption.GetMainHeroEquipmentSecondAbilityKind(item), item.decideSecondAbilityValue + sameTempValue, AnalysisTalentOption.TALENTOPTION_PERCENT_OR_FIXED_KIND.FIXED_VALUE);
        }

        /**/
        calculatedStatus.healthPoint += sumStatus.SUM_HP;
        calculatedStatus.attackPower += sumStatus.SUM_ATK;
        calculatedStatus.physicalDefence += sumStatus.SUM_DEF;
        calculatedStatus.magicDefence += sumStatus.SUM_MDEF;

        calculatedStatus.setCriticalChance(calculatedStatus.getCriticalChance() + (sumStatus.SUM_CRITICAL_CHANCE * 0.01f));
        calculatedStatus.setCriticalPercent(calculatedStatus.getCriticalPercent() + (sumStatus.SUM_CRITICAL_PERCENT * 0.01f));
        calculatedStatus.setPenetrationChance(calculatedStatus.getPenetrationChance() + (sumStatus.SUM_PENETRATION_CHANCE * 0.01f));
        calculatedStatus.setPenetrationPercent(calculatedStatus.getPenetrationPercent() + (sumStatus.SUM_PENETRATION_PERCENT * 0.01f));
        calculatedStatus.setAccuracyRate(calculatedStatus.getAccuracyRate() + (sumStatus.SUM_ACCURACY * 0.01f));
        calculatedStatus.setEvasionRate(calculatedStatus.getEvasionRate() + (sumStatus.SUM_EVASION * 0.01f));

        sumStatus = null;
    }

    public static void MainHeroEquipmentOptionCalculate(Long userId,
                                                        EquipmentCalculatedDto selectedDeck,
                                                        HeroStatusDto growthStatus,
                                                        HeroStatusDto calculatedStatus,
                                                        List<EquipmentOptionsInfoTable> equipmentOptionsInfoTable)
    {
        List<EquipmentOptionsInfoTable> equipmentOptionsInfoTables = equipmentOptionsInfoTable;
        SUM_Status sumStatus = new SUM_Status();

        double sameTempValue = 0.0f; //공명으로 상승된 계산된 값

        /* 장비 옵션 계산 */
        EquipmentCalculatedDto.EquipmentInfo item;
        int optionCount = 0;

        int Count = selectedDeck.getEquipmentInfoList().size();
        for (int i = 0; i < Count; i++)
        {
            item = selectedDeck.getEquipmentInfoList().get(i);
            if (item == null)
                continue;
            optionCount = item.optionIdList.size();
            for (int j = 0; j < optionCount; j++)
            {
                EquipmentOptionInfoDto optionInfo = new EquipmentOptionInfoDto();
                EquipmentCalculatedDto.EquipmentInfo finalItem = item;
                int finalJ = j;
                EquipmentOptionsInfoTable temp = equipmentOptionsInfoTables.stream()
                        .filter(option -> option.getID() == finalItem.optionIdList.get(finalJ))
                        .findAny()
                        .orElse(null);
                if(temp == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: Can't Find EquipmentOptionsInfoTable.", "HeroCalculate", Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't Find EquipmentOptionsInfoTable.", ResponseErrorCode.NOT_FIND_DATA);
                }
                optionInfo.setEquipmentOptionInfo(temp);
                if (optionInfo.TALENTOPTION_CONTINUITY_KIND != AnalysisTalentOption.TALENTOPTION_CONTINUITY_KIND.PERMANENT_OPTION)
                    continue;

                sameTempValue = item.optionValueList.get(j) * selectedDeck.GetSameSetOptionValue(item);
                SumTalentStatus.SumTalent(sumStatus, calculatedStatus, optionInfo.TALENTOPTION_KIND, item.optionValueList.get(j) + sameTempValue, optionInfo.TALENTOPTION_HOWTO_PLUSE_KIND);
            }
        }

        /**/
        calculatedStatus.setHealthPoint( calculatedStatus.getHealthPoint() + sumStatus.SUM_HP);

        calculatedStatus.attackPower += sumStatus.SUM_ATK;
        calculatedStatus.physicalDefence += sumStatus.SUM_DEF;
        calculatedStatus.magicDefence += sumStatus.SUM_MDEF;

        calculatedStatus.setCriticalChance(calculatedStatus.getCriticalChance() + (sumStatus.SUM_CRITICAL_CHANCE * 0.01f));
        calculatedStatus.setCriticalPercent(calculatedStatus.getCriticalPercent() + (sumStatus.SUM_CRITICAL_PERCENT * 0.01f));
        calculatedStatus.setPenetrationChance(calculatedStatus.getPenetrationChance() + (sumStatus.SUM_PENETRATION_CHANCE * 0.01f));
        calculatedStatus.setPenetrationPercent(calculatedStatus.getPenetrationPercent() + (sumStatus.SUM_PENETRATION_PERCENT * 0.01f));
        calculatedStatus.setAccuracyRate(calculatedStatus.getAccuracyRate() + (sumStatus.SUM_ACCURACY * 0.01f));
        calculatedStatus.setEvasionRate(calculatedStatus.getEvasionRate() + (sumStatus.SUM_EVASION * 0.01f));

        sumStatus = null;
    }

    public static void AddCalculatedStatusToSumStatus(SUM_Status sum_status, HeroStatusDto calculatedStatus)
    {
        calculatedStatus.healthPoint += sum_status.SUM_HP;
        calculatedStatus.attackPower += sum_status.SUM_ATK;
        calculatedStatus.physicalDefence += sum_status.SUM_DEF;
        calculatedStatus.magicDefence += sum_status.SUM_MDEF;

        calculatedStatus.setAttackSpeed(calculatedStatus.getAttackSpeed() + (sum_status.SUM_ATTACK_SPEED_PERCENT * 0.01f));
        calculatedStatus.setCriticalChance(calculatedStatus.getCriticalChance() + (sum_status.SUM_CRITICAL_CHANCE * 0.01f));
        calculatedStatus.setCriticalPercent(calculatedStatus.getCriticalPercent() + (sum_status.SUM_CRITICAL_PERCENT * 0.01f));
        calculatedStatus.setPenetrationChance(calculatedStatus.getPenetrationChance() + (sum_status.SUM_PENETRATION_CHANCE * 0.01f));
        calculatedStatus.setPenetrationPercent(calculatedStatus.getPenetrationPercent() + (sum_status.SUM_PENETRATION_PERCENT * 0.01f));
        calculatedStatus.setAccuracyRate(calculatedStatus.getAccuracyRate() + (sum_status.SUM_ACCURACY * 0.01f));
        calculatedStatus.setEvasionRate(calculatedStatus.getEvasionRate() + (sum_status.SUM_EVASION * 0.01f));
    }
}
