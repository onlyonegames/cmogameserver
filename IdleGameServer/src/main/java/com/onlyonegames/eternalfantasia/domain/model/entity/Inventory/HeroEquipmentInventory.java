package com.onlyonegames.eternalfantasia.domain.model.entity.Inventory;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class HeroEquipmentInventory extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    int item_Id;
    int itemClassValue;
    //float decideValue;
    //2019-10-09. 기획 수정 각 장비에 기본 능력이 하나씩 이었던 것을 기본 능력과 보조 능력 두가지로 추가함.
    float decideDefaultAbilityValue;
    float decideSecondAbilityValue;

    String itemClass;
    String optionIds;
    String optionValues;
    int level;
    int maxLevel;
    int exp;
    int nextExp;

    @Builder
    public HeroEquipmentInventory(Long useridUser, int item_Id, int itemClassValue, float decideDefaultAbilityValue, float decideSecondAbilityValue, String itemClass, String optionIds,
                                  String optionValues, int level, int maxLevel, int exp, int nextExp) {
        this.useridUser = useridUser;
        this.item_Id = item_Id;
        this.itemClassValue = itemClassValue;
        this.decideDefaultAbilityValue = decideDefaultAbilityValue;
        this.decideSecondAbilityValue = decideSecondAbilityValue;
        this.itemClass = itemClass;
        this.optionIds = optionIds;
        this.optionValues = optionValues;
        this.level = level;
        this.maxLevel = maxLevel;
        this.exp = exp;
        this.nextExp = nextExp;
    }

    boolean ChangeLevel(int willChangeLevel) {
        if(maxLevel < willChangeLevel)
            return false;
        this.level = willChangeLevel;
        return true;
    }
//
//    public void ChangeExp(int exp) {
//        this.exp = exp;
//    }
//
//    public void ChangeNextExp(int nextExp) {
//        this.nextExp = nextExp;
//    }

    public boolean Strengthen(int willChangeLevel, int newExp, int newNextLevelUpExp, double newDecideDefaultAbilityValue, double newDecideSecondAbilityValue) {
        if(!ChangeLevel(willChangeLevel))
            return false;

        this.exp = newExp;
        this.decideDefaultAbilityValue = (float)newDecideDefaultAbilityValue;
        this.decideSecondAbilityValue = (float)newDecideSecondAbilityValue;
        this.nextExp = newNextLevelUpExp;

        return true;
    }

    public void Promotion(int item_Id, float decideDefaultAbilityValue, float decideSecondAbilityValue, int maxLevel, int nextExp, String optionIds, String optionValues) {
        this.item_Id = item_Id;
        this.decideDefaultAbilityValue = decideDefaultAbilityValue;
        this.decideSecondAbilityValue = decideSecondAbilityValue;
        this.optionIds = optionIds;
        this.optionValues = optionValues;
        this.level = 1;
        this.maxLevel = maxLevel;
        this.exp = 0;
        this.nextExp = nextExp;
    }

    public void OptionResmelting(String optionIds, String optionValues) {
        this.optionIds = optionIds;
        this.optionValues = optionValues;
    }

    public void QuilityResmelting(double decideDefaultAbilityValue, double decideSecondAbilityValue, String itemClass, int itemClassValue, String optionValues) {
        this.decideDefaultAbilityValue = (float)decideDefaultAbilityValue;
        this.decideSecondAbilityValue = (float)decideSecondAbilityValue;
        this.itemClass = itemClass;
        this.itemClassValue = itemClassValue;
        this.optionValues = optionValues;
    }
}