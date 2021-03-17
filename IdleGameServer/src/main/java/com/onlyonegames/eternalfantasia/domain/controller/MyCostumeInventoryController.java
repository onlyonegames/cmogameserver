package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.CostumeCommonRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Companion.MyCostumeInventoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RestController
public class MyCostumeInventoryController {
    private final MyCostumeInventoryService myCostumeInventoryService;

    @PostMapping("/api/EquipCostume")
    public ResponseDTO<Map<String, Object>> EquipCostume(@RequestBody CostumeCommonRequestDto costumeCommonRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myCostumeInventoryService.EquipCostume(userId, costumeCommonRequestDto.getCotumeId(), costumeCommonRequestDto.getOwnerCode(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/BuyEquip")
    public ResponseDTO<Map<String, Object>> BuyEquip(@RequestBody CostumeCommonRequestDto costumeCommonRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myCostumeInventoryService.BuyCostume(userId, costumeCommonRequestDto.getCotumeId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
