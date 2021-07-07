package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.AccessoryUpgradeRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.AccessoryInventoryResponseDto;
import com.onlyonegames.eternalfantasia.domain.service.Inventory.MyAccessoryInventoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class MyAccessoryInventoryController {
    private final MyAccessoryInventoryService myAccessoryInventoryService;

    @PostMapping("/api/Inventory/UpgradeAccessory")
    public ResponseDTO<Map<String, Object>> UpgradeAccessory(@RequestBody AccessoryUpgradeRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myAccessoryInventoryService.UpgradeAccessory(userId, dto.getCode(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/Inventory/ChangeOptionAccessory")
    public ResponseDTO<Map<String, Object>> ChangeOptionAccessory(@RequestBody AccessoryInventoryResponseDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myAccessoryInventoryService.ChangeOption(userId, dto, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
