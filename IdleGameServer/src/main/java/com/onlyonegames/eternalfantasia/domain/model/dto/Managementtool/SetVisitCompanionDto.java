package com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool;

import lombok.Data;

import java.util.List;

@Data
public class SetVisitCompanionDto {
    Long userId;
    List<ChangeVisitCompanion> companionList;

    public static class ChangeVisitCompanion{
        public String code;
        public String characterName;
        public float linkGaugePercent;

        public void setChangeVisitCompanion(String code, String characterName, float linkGaugePercent){
            this.code = code;
            this.characterName = characterName;
            this.linkGaugePercent = linkGaugePercent;
        }
    }
}
