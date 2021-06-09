package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.PixieLevelUpRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.RuneEquipmentRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.MyPixieInfoService;
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
public class MyPixieInfoController {
    private final MyPixieInfoService myPixieInfoService;

    @GetMapping("/api/MyPixieInfo")
    public ResponseDTO<Map<String, Object>> GetMyPixieInfo() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myPixieInfoService.GetMyPixieInfo(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/MyPixieInfo/LevelUp")
    public ResponseDTO<Map<String, Object>> PixieLevelUp(@RequestBody PixieLevelUpRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myPixieInfoService.PixieLevelUp(userId, dto.getPixieGettingRuneInfoList(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/MyPixieInfo/EquipmentRune")
    public ResponseDTO<Map<String, Object>> EquipmentRune(@RequestBody RuneEquipmentRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myPixieInfoService.EquipmentRune(userId, dto.getRuneInventoryId(), dto.getSlotNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/MyPixieInfo/UnEquipment")
    public ResponseDTO<Map<String, Object>> UnEquipmentRune(@RequestBody RuneEquipmentRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myPixieInfoService.UnEquipmentRune(userId, dto.getSlotNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
