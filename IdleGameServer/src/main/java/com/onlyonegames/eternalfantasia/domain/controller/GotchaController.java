package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.PickupGotchaRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.UseMileageRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Gotcha.GotchaService;
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
public class GotchaController {
    private final GotchaService gotchaService;

    @Autowired
    public GotchaController(GotchaService gotchaService) {
        this.gotchaService = gotchaService;
    }

    @GetMapping("/api/GetPickupEventList")
    public ResponseDTO<Map<String, Object>> GetPickupEventList() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = gotchaService.GetPickupEventList(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/FreeGotchaCharacter")
    public ResponseDTO<Map<String, Object>> FreeGotchaCharacter(@RequestBody PickupGotchaRequestDto pickupGotchaRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = gotchaService.FreeGotchaCharacter(userId, pickupGotchaRequestDto.getPickupGotchaId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/GotchaCharacter")
    public ResponseDTO<Map<String, Object>> GotchaCharacter(@RequestBody PickupGotchaRequestDto pickupGotchaRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = gotchaService.GotchaCharacter(userId, pickupGotchaRequestDto.getPickupGotchaId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/GotchaCharacter10")
    public ResponseDTO<Map<String, Object>> GotchaCharacter10(@RequestBody PickupGotchaRequestDto pickupGotchaRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = gotchaService.GotchaCharacter10(userId, pickupGotchaRequestDto.getPickupGotchaId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/GotchaEtc")
    public ResponseDTO<Map<String, Object>> GotchaEtc() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = gotchaService.GotchaEtc(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/GotchaEtc10")
    public ResponseDTO<Map<String, Object>> GotchaEtc10() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = gotchaService.GotchaEtc10(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/UseMileage")
    public ResponseDTO<Map<String, Object>> UseMileage(@RequestBody UseMileageRequestDto useMileageRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = gotchaService.GettingHeroPieceFromMileage(userId, useMileageRequestDto.getSelectCharacterCode(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}