package com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class HallofHonor extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long honorUserId;//해당 시즌에 명예의 전당 등록된 유저아이디
    String seasonCode;//시즌 코드
    String selectedCharacterCode; //선택된 케릭터 코드
    int seasonNo;//시즌 번호. 첫 시즌 1 부터 증가.
    int selectedPose;//선택된 포즈
    //동료를 선택했을때 필요한 내용.
    String selectedCostumeCode;//선택된 코스튬 코드
    //영웅을 선택했을때 필요한 내용.
    String selectedEquipmentArmorCode;//선택된 장비중 ArmorCode
    String selectedEquipmentHelmetCode;//선택된 장비중 HelmetCode
    String selectedEquipmentAccessoryCode;//선택된 장비중 AccessoryCode
    boolean changedByUser;//유저의 꾸미기 진행 완료. 1번 이상 진행 못하게.
    boolean latest;// 가장 최근 시즌의 것인지 판단 하기위한 변수.(클라이언트 전송용)
    public void OffLatest() {
        latest = false;
    }
    /**유저가 명예의 전당 설정을 하면 changedByUser는 true 가 된다.(해당 명예의 전당은 한번만 수정 할수 있음.)*/
    public void ChangeHallofHonorByClient(String selectedCharacterCode, int selectedPose, String selectedCostumeCode, String selectedEquipmentArmorCode, String selectedEquipmentHelmetCode, String selectedEquipmentAccessoryCode) {
        this.selectedCharacterCode = selectedCharacterCode.toLowerCase();
        this.selectedPose = selectedPose;
        this.selectedCostumeCode = selectedCostumeCode;
        this.selectedEquipmentArmorCode = selectedEquipmentArmorCode;
        this.selectedEquipmentHelmetCode = selectedEquipmentHelmetCode;
        this.selectedEquipmentAccessoryCode = selectedEquipmentAccessoryCode;
        this.changedByUser = true;
    }

    /**시즌이 끝나면 시스템에 의해 신규 명예의 전당 row 가 기본값으로 셋팅되어 생성됨.*/
    public void InitHallofHonorBySystem(Long honorUserId, String seasonCode, int seasonNo){
       this.honorUserId = honorUserId;
       this.seasonCode = seasonCode;
       this.seasonNo = seasonNo;

       this.selectedCharacterCode = "hero";
       this.selectedPose = 1;
       this.selectedCostumeCode = "";
       this.selectedEquipmentArmorCode = "";
       this.selectedEquipmentHelmetCode = "";
       this.selectedEquipmentAccessoryCode = "";
       this.changedByUser = false;
    }
}
