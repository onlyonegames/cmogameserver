package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.HeroEquipmentDivideRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Inventory.HeroEquipmentDivideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping({"/api/HeroEquipmentDivide"})
public class HeroEquipmentDivideController {
    private final HeroEquipmentDivideService heroEquipmentDivideService;

    @Autowired
    public HeroEquipmentDivideController(HeroEquipmentDivideService heroEquipmentDivideService) {
        this.heroEquipmentDivideService = heroEquipmentDivideService;
    }

    @PostMapping
    public ResponseDTO<Map<String, Object>> HeroEquipmentDivide(@RequestBody HeroEquipmentDivideRequestDto divideRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = heroEquipmentDivideService.RequestEquipmentDivide(userId, divideRequestDto.getDivideItemList(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
