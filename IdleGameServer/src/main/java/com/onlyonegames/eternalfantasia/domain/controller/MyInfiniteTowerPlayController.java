package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.InfiniteTowerPlayRequestCommonDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.OrdealDungeonPlayRequestCommonDto;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.MyInfiniteTowerPlayService;
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
public class MyInfiniteTowerPlayController {
    private final MyInfiniteTowerPlayService myInfiniteTowerPlayService;

    @GetMapping("/api/InfiniteTowerPlayTimeSet")
    public ResponseDTO<Map<String, Object>> InfiniteTowerPlayTimeSet() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myInfiniteTowerPlayService.InfiniteTowerPlayTimeSet(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/InfiniteTowerPlay")
    public ResponseDTO<Map<String, Object>> Play(@RequestBody InfiniteTowerPlayRequestCommonDto infiniteTowerPlayRequestCommonDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myInfiniteTowerPlayService.StartInfiniteTowerPlay(userId, infiniteTowerPlayRequestCommonDto.getFloorNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/InfiniteTowerClear")
    public ResponseDTO<Map<String, Object>> Clear(@RequestBody InfiniteTowerPlayRequestCommonDto infiniteTowerPlayRequestCommonDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myInfiniteTowerPlayService.InfiniteTowerClear(userId, infiniteTowerPlayRequestCommonDto.getFloorNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/InfiniteTowerFail")
    public ResponseDTO<Map<String, Object>> Fail(@RequestBody InfiniteTowerPlayRequestCommonDto infiniteTowerPlayRequestCommonDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myInfiniteTowerPlayService.InfiniteTowerFail(userId, infiniteTowerPlayRequestCommonDto.getFloorNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
