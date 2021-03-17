package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ReceiveInfiniteTowerRewardRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.MyInfiniteTowerSaveDataService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class MyInfiniteTowerSaveDataController {
    MyInfiniteTowerSaveDataService myInfiniteTowerSaveDataService;

    @GetMapping("/api/GetMyInfiniteTowerSaveData")
    public ResponseDTO<Map<String, Object>> GetMyInfiniteTowerSaveData() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myInfiniteTowerSaveDataService.GetMyInfiniteTowerSaveData(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/ReceiveInfiniteTowerReward")
    public ResponseDTO<Map<String, Object>> ReceiveInfiniteTowerReward(@RequestBody ReceiveInfiniteTowerRewardRequestDto receiveInfiniteTowerRewardRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myInfiniteTowerSaveDataService.ReceiveInfiniteTowerReward(userId, receiveInfiniteTowerRewardRequestDto.getFloor(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
