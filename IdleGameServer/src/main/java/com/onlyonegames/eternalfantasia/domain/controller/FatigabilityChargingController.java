package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.FatigabilityChargingCommonRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Companion.FatigabilityChargingService;
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
public class FatigabilityChargingController {
    private final FatigabilityChargingService fatigabilityChargingService;

    @PostMapping("/api/ReduceFatigabilityChargingTimeFromAD")
    public ResponseDTO<Map<String, Object>> ReduceFatigabilityChargingTimeFromAD(@RequestBody FatigabilityChargingCommonRequestDto fatigabilityChargingCommonRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = fatigabilityChargingService.ReduceChargingTimeFromAD(fatigabilityChargingCommonRequestDto.getCharacterId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/FatigabilityFullCharging")
    public ResponseDTO<Map<String, Object>> FatigabilityFullCharging(@RequestBody FatigabilityChargingCommonRequestDto fatigabilityChargingCommonRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = fatigabilityChargingService.FatigabilityFullCharging(userId, fatigabilityChargingCommonRequestDto.getCharacterId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/FatigabilityChargingFromPotion")
    public ResponseDTO<Map<String, Object>> FatigabilityChargingFromPotion(@RequestBody FatigabilityChargingCommonRequestDto fatigabilityChargingCommonRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = fatigabilityChargingService.FatigabilityChargingFromPotion(userId, fatigabilityChargingCommonRequestDto.getCharacterId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
