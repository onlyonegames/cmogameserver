package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ClassLevelUpRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.GettingClassRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.SkillUpgradeRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Inventory.MyClassInventoryService;
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
public class MyClassInventoryController {
    private final MyClassInventoryService myClassInventoryService;

    @GetMapping("/api/GetMyInventoryInfo")
    public ResponseDTO<Map<String, Object>> GetInventoryInfo() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myClassInventoryService.GetMyClassInfo(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/GettingClass")
    public ResponseDTO<Map<String, Object>> GettingClass(@RequestBody GettingClassRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myClassInventoryService.GettingClass(userId, dto.getCode(), dto.getCount(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/ClassLevelUp")
    public ResponseDTO<Map<String, Object>> ClassLevelUp(@RequestBody ClassLevelUpRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myClassInventoryService.ClassLevelUp(userId, dto.getClassInventoryId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/SkillUpgrade")
    public ResponseDTO<Map<String, Object>> SkillUpgrade(@RequestBody SkillUpgradeRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myClassInventoryService.SkillUpgrade(userId, dto.getClassInventoryId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
