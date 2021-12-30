package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ClassTranscendenceRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.ClassInventoryResponseDto;
import com.onlyonegames.eternalfantasia.domain.service.Inventory.MyClassInventoryService;
import lombok.AllArgsConstructor;
import org.hibernate.type.ObjectType;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class MyClassInventoryController {
    private final MyClassInventoryService myClassInventoryService;

    @PostMapping("/api/Inventory/Transcendence")
    public ResponseDTO<Map<String, Object>> Transcendence(@RequestBody ClassTranscendenceRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myClassInventoryService.ClassTranscendence(userId, dto.getCode(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/Inventory/ClassChangeOption")
    public ResponseDTO<Map<String, Object>> ChangeOption(@RequestBody ClassInventoryResponseDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myClassInventoryService.ChangeOption(userId, dto, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
