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
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    public Long classEquipment;
    public int nowUsedWeapon;
    public Long swordEquipment;
    public Long daggerEquipment;
    public Long spearEquipment;
    public Long bowEquipment;
    public Long wandEquipment;
    public Long earringEquipment;
    public Long necklaceEquipment;
    public Long ringEquipment;

    public void SetClassEquipment(String element) {
        this.classEquipment = Long.parseLong(element);
    }

    public void SetNowUsedWeapon(String element) {
        this.nowUsedWeapon = Integer.parseInt(element);
    }

    public void SetSwordEquipment(String element) {
        this.swordEquipment = Long.parseLong(element);
    }

    public void SetDaggerEquipment(String element) {
        this.daggerEquipment = Long.parseLong(element);
    }

    public void SetSpearEquipment(String element) {
        this.spearEquipment = Long.parseLong(element);
    }

    public void SetBowEquipment(String element) {
        this.bowEquipment = Long.parseLong(element);
    }

    public void SetWandEquipment(String element) {
        this.wandEquipment = Long.parseLong(element);
    }

    public void SetEarringEquipment(String element) {
        this.earringEquipment = Long.parseLong(element);
    }

    public void SetNecklaceEquipment(String element) {
        this.necklaceEquipment = Long.parseLong(element);
    }

    public void SetRingEquipment(String element) {
        this.ringEquipment = Long.parseLong(element);
    }
}
