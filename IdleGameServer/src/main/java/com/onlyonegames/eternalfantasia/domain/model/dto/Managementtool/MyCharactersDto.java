package com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool;

import com.onlyonegames.eternalfantasia.domain.controller.managementtool.CostumeDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MyCharactersDto {
    public Long id;
    public String name;
//    public int goodFeeling;
//    public int goodFeelingExp;
    public int level;
    public int exp;
    public int linkAbilityLevel;
    public int fatigability;
    public boolean gotcha;
    public List<CostumeDto> costume;
    public List<CompanionWeaponInfo> selectedIds;
    public List<CompanionWeaponInfo> companionWeaponInfoList;
    public List<OpenInfo> openInfos;

    public static class OpenInfo{
        public String openInfo;

        public void setOpenInfo(String openInfo){
            this.openInfo = openInfo;
        }
    }

    public static class CompanionWeaponInfo{
        public String linkWeaponName;
        public boolean open;
        public int upgrade;

        public void setCompanionWeaponInfo(String linkWeaponName, boolean open, int upgrade){
            this.linkWeaponName = linkWeaponName;
            this.open = open;
            this.upgrade = upgrade;
        }
    }

    public void setMyCharacters(String name, Long id, /*int goodFeeling, int goodFeelingExp,*/
                                int fatigability, int level, int exp,
                                int linkAbilityLevel, boolean gotcha,
                                List<CostumeDto> costume,
                                List<CompanionWeaponInfo> selectedIds,
                                List<CompanionWeaponInfo> companionWeaponInfoList
                                /*CompanionLinkweaponInfo companionLinkweaponInfo*/,
                                List<OpenInfo> openInfos){
        this.id = id;
        this.name = name;
//        this.goodFeeling = goodFeeling;
//        this.goodFeelingExp = goodFeelingExp;
        this.level = level;
        this.exp = exp;
        this.linkAbilityLevel = linkAbilityLevel;
        this.fatigability = fatigability;
        this.gotcha = gotcha;
        this.costume = costume;
        this.selectedIds = selectedIds;
        //this.companionLinkweaponInfo = companionLinkweaponInfo;
        this.companionWeaponInfoList = companionWeaponInfoList;
        this.openInfos = openInfos;
    }

    public static class TavernVisitCompanionInfo{
        public List<VisitCompanionInfo> visitCompanionInfoList;
        public LocalDateTime visitScheduleStartTime;

        public void setTavernVisitCompanionInfo(List<VisitCompanionInfo> visitCompanionInfoList, LocalDateTime visitScheduleStartTime){
            this.visitCompanionInfoList = visitCompanionInfoList;
            this.visitScheduleStartTime = visitScheduleStartTime.plusDays(5L);
        }

    }
    public static class VisitCompanionInfo{
        public String characterName;
        public float linkgaugePercent;
        public boolean recruited;

        public void setVisitCompanionInfo(String characterName, float linkgaugePercent, boolean recruited){
            this.characterName = characterName;
            this.linkgaugePercent = linkgaugePercent;
            this.recruited = recruited;
        }
    }
}
