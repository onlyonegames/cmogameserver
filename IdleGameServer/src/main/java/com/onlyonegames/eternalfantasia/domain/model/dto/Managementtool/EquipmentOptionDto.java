package com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool;

import lombok.Data;

@Data
public class EquipmentOptionDto {
    public String option;
    public float value;

    public void setOption(String option, float value, boolean isOptions) {
        if(!isOptions){
            switch (option) {
                case "ATTACK_UP":
                    this.option = "공격력";
                    break;
                case "DEF_UP":
                    this.option = "물리 방어력";
                    break;
                case "HP_UP":
                    this.option = "생명력";
                    break;
                case "ACCURACYRATE_UP":
                    this.option = "명중률";
                    break;
                case "PENETRATION_PERCENT_UP":
                    this.option = "관통력";
                    break;
                case "CRITICAL_DMG_UP":
                    this.option = "치명 피해량";
                    break;
                case "PENETRATION_CHANCE_UP":
                    this.option = "관통 확률";
                    break;
                case "MDEF_UP":
                    this.option = "마법 저항력";
                    break;
                case "CRITICAL_CHANCE_UP":
                    this.option = "치명타 확률";
                    break;
                case "EVASIONRATE_UP":
                    this.option = "회피율";
                    break;
            }
        } else
            this.option = option;
        this.value = value;
    }
}
