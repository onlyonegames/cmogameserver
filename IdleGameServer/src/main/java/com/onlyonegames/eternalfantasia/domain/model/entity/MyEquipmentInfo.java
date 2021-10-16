package com.onlyonegames.eternalfantasia.domain.model.entity;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class MyEquipmentInfo extends BaseTimeEntity {
    @Id
    //@TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long useridUser;
    public int nowUsedClass;
    public int warriorEquipment;
    public int thiefEquipment;
    public int knightEquipment;
    public int archerEquipment;
    public int magicianEquipment;
    public int nowUsedWeapon;
    public int swordEquipment;
    public int daggerEquipment;
    public int spearEquipment;
    public int bowEquipment;
    public int wandEquipment;
    public int earringEquipment;
    public int necklaceEquipment;
    public int ringEquipment;

    public void SetNowUsedClass(String element) {
        this.nowUsedClass = Integer.parseInt(element);
    }

    public void SetWarriorEquipment(String element) {
        this.warriorEquipment = Integer.parseInt(element);
    }

    public void SetThiefEquipment(String element) {
        this.thiefEquipment = Integer.parseInt(element);
    }

    public void SetKnightEquipment(String element) {
        this.knightEquipment = Integer.parseInt(element);
    }

    public void SetArcherEquipment(String element) {
        this.archerEquipment = Integer.parseInt(element);
    }

    public void SetMagicianEquipment(String element) {
        this.magicianEquipment = Integer.parseInt(element);
    }

    public void SetNowUsedWeapon(String element) {
        this.nowUsedWeapon = Integer.parseInt(element);
    }

    public void SetSwordEquipment(String element) {
        this.swordEquipment = Integer.parseInt(element);
    }

    public void SetDaggerEquipment(String element) {
        this.daggerEquipment = Integer.parseInt(element);
    }

    public void SetSpearEquipment(String element) {
        this.spearEquipment = Integer.parseInt(element);
    }

    public void SetBowEquipment(String element) {
        this.bowEquipment = Integer.parseInt(element);
    }

    public void SetWandEquipment(String element) {
        this.wandEquipment = Integer.parseInt(element);
    }

    public void SetEarringEquipment(String element) {
        this.earringEquipment = Integer.parseInt(element);
    }

    public void SetNecklaceEquipment(String element) {
        this.necklaceEquipment = Integer.parseInt(element);
    }

    public void SetRingEquipment(String element) {
        this.ringEquipment = Integer.parseInt(element);
    }
}
