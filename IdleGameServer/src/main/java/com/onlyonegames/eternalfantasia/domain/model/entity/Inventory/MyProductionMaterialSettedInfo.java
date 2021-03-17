package com.onlyonegames.eternalfantasia.domain.model.entity.Inventory;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

//제작시 유저별 최종 선택한 재료들 갯수들. 장비 종류별로 ,로 구분된 스트링값들로 저장
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class MyProductionMaterialSettedInfo extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long userIdUser;
    String weaponMaterialCounts;   //무기 재료 갯수들
    String armorMaterialCounts;    //방어구 재료 갯수
    String helmetMaterialCounts;   //투구 재료 갯수
    String accessoryMaterialCounts;//보조 재료 갯수

    public void SetWeaponMaterialCounts(String weaponMaterialCounts){
        this.weaponMaterialCounts = weaponMaterialCounts;
    }

    public void SetArmorMaterialCounts(String armorMaterialCounts){
        this.armorMaterialCounts = armorMaterialCounts;
    }

    public void SetHelmetMaterialCounts(String helmetMaterialCounts){
        this.helmetMaterialCounts = helmetMaterialCounts;
    }

    public void SetAccessoryMaterialCounts(String accessoryMaterialCounts){
        this.accessoryMaterialCounts = accessoryMaterialCounts;
    }

    public void InitData() {
        this.weaponMaterialCounts = "50,50,50";
        this.armorMaterialCounts = "50,50,50";
        this.helmetMaterialCounts = "50,50,50";
        this.accessoryMaterialCounts = "50,50,50";
    }
}
