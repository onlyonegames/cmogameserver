package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.PresentsToHeroRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Companion.MyGiftInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MyGiftInventoryController {
    private final MyGiftInventoryService myGiftInventoryService;

    @Autowired
    public MyGiftInventoryController(MyGiftInventoryService myGiftInventoryService) {
        this.myGiftInventoryService = myGiftInventoryService;
    }

    @GetMapping("/api/GetGiftInventory")
    public ResponseDTO<Map<String, Object>> GetGiftInventory() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myGiftInventoryService.GetGiftInventory(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/GiftPresentsToHero")
    public ResponseDTO<Map<String, Object>> PresentsToHero(@RequestBody PresentsToHeroRequestDto presentsToHeroRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myGiftInventoryService.GivePresentsToHero(userId, presentsToHeroRequestDto.getToCharacterId(), presentsToHeroRequestDto.getPresentsList(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}

