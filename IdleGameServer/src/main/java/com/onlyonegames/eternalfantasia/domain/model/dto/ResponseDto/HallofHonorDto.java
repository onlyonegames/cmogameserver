package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.HallofHonor;
import lombok.Data;

@Data
public class HallofHonorDto {
    Long id;
    Long honorUserId;//해당 시즌에 명예의 전당 등록된 유저아이디
    String userGameName;
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

    public HallofHonor ToEntity(){
        return HallofHonor.builder().honorUserId(honorUserId).seasonCode(seasonCode).selectedCharacterCode(selectedCharacterCode).seasonNo(seasonNo).selectedPose(selectedPose).selectedCostumeCode(selectedCostumeCode).selectedEquipmentArmorCode(selectedEquipmentArmorCode).selectedEquipmentHelmetCode(selectedEquipmentHelmetCode).selectedEquipmentAccessoryCode(selectedEquipmentAccessoryCode).changedByUser(changedByUser).latest(latest).build();
    }

    public void setHallofHonorDto(HallofHonor hallofHonor, String userGameName){
        this.id = hallofHonor.getId();
        this.honorUserId = hallofHonor.getHonorUserId();
        this.userGameName = userGameName;
        this.seasonCode = hallofHonor.getSeasonCode();
        this.selectedCharacterCode = hallofHonor.getSelectedCharacterCode();
        this.seasonNo = hallofHonor.getSeasonNo();
        this.selectedPose = hallofHonor.getSelectedPose();
        this.selectedCostumeCode = hallofHonor.getSelectedCostumeCode();
        this.selectedEquipmentArmorCode = hallofHonor.getSelectedEquipmentArmorCode();
        this.selectedEquipmentHelmetCode = hallofHonor.getSelectedEquipmentHelmetCode();
        this.selectedEquipmentAccessoryCode = hallofHonor.getSelectedEquipmentAccessoryCode();
        this.changedByUser = hallofHonor.isChangedByUser();
        this.latest = hallofHonor.isLatest();
    }

    public void InitFromDbData(HallofHonor dbData) {
        this.id = dbData.getId();
        this.honorUserId = dbData.getHonorUserId();
        this.seasonCode = dbData.getSeasonCode();
        this.selectedCharacterCode = dbData.getSelectedCharacterCode();
        this.seasonNo = dbData.getSeasonNo();
        this.selectedPose = dbData.getSelectedPose();
        this.selectedCostumeCode = dbData.getSelectedCostumeCode();
        this.selectedEquipmentArmorCode = dbData.getSelectedEquipmentArmorCode();
        this.selectedEquipmentHelmetCode = dbData.getSelectedEquipmentHelmetCode();
        this.selectedEquipmentAccessoryCode = dbData.getSelectedEquipmentAccessoryCode();
    }
}
