package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.HallofHonorRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.HeroEquipmentDivideRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.HallofHonorService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@AllArgsConstructor
public class HallofHonorController {
    private final HallofHonorService hallofHonorService;

    /**명예의 전당 정보 리스트 불러오기*/
    @GetMapping("/api/HallofHonor/GetList")
    public ResponseDTO<Map<String, Object>> GetList() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = hallofHonorService.GetList(map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    /**명예의 전당 셋팅*/
    @PostMapping("/api/HallofHonor/SettingMyHall")
    public ResponseDTO<Map<String, Object>> SettingMyHall(@RequestBody HallofHonorRequestDto hallofHonorRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String selectedCharacterCode = hallofHonorRequestDto.getSelectedCharacterCode();
        int selectedPose = hallofHonorRequestDto.getSelectedPose();
        String selectedCostumeCode = hallofHonorRequestDto.getSelectedCostumeCode();
        String selectedEquipmentArmorCode = hallofHonorRequestDto.getSelectedEquipmentArmorCode();
        String selectedEquipmentHelmetCode = hallofHonorRequestDto.getSelectedEquipmentHelmetCode();
        String selectedEquipmentAccessoryCode = hallofHonorRequestDto.getSelectedEquipmentAccessoryCode();

        Map<String, Object> response = hallofHonorService.SettingMyHall(userId, selectedCharacterCode,
                selectedPose, selectedCostumeCode, selectedEquipmentArmorCode, selectedEquipmentHelmetCode, selectedEquipmentAccessoryCode, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    /**명예의 전당 보상 받기 API*/
    @GetMapping("/api/HallofHonor/GetReward")
    public ResponseDTO<Map<String, Object>> GetReward() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = hallofHonorService.GetReward(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
