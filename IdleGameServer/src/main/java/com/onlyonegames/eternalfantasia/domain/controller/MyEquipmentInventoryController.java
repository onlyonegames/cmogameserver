package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.EquipmentLevelUpRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.GettingEquipmentItemRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Inventory.MyEquipmentService;
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
public class MyEquipmentInventoryController {
    private final MyEquipmentService myEquipmentService;

    @GetMapping("/api/MyEquipmentInventory")
    public ResponseDTO<Map<String, Object>> GetInventory(){
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myEquipmentService.GetInventoryInfo(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/GettingEquipmentItem")
    public ResponseDTO<Map<String, Object>> GettingEquipmentItem(@RequestBody GettingEquipmentItemRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myEquipmentService.GettingEquipmentItem(userId, dto, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/EquipmentLevelUp")
    public ResponseDTO<Map<String, Object>> EquipmentLevelUp(@RequestBody EquipmentLevelUpRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myEquipmentService.EquipmentLevelUp(userId, dto.getEquipmentInventoryId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
