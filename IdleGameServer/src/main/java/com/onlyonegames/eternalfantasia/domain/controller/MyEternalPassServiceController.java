package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.EternalPassDirectOpenPassLevelRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.EternalPassReceiveItemRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.MyEternalPassCompleteRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.EternalPass.MyEternalPassService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RestController
public class MyEternalPassServiceController {
    private final MyEternalPassService myEternalPassService;

    @GetMapping("/api/EternalPass/GetMyEternalPassInfo")
    public ResponseDTO<Map<String, Object>> GetMyEternalPassInfo() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myEternalPassService.GetMyEternalPassInfo(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/EternalPass/RequestReceiveFreePassItem")
    public ResponseDTO<Map<String, Object>> RequestReceiveFreePassItem(@RequestBody EternalPassReceiveItemRequestDto eternalPassReceiveItemRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int selecteRewardPassLevel = eternalPassReceiveItemRequestDto.getSelectRewardPassLevel();
        Map<String, Object> response = myEternalPassService.RequestReceiveFreePassItem(userId, selecteRewardPassLevel, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/EternalPass/RequestReceiveRoyalPassItem")
    public ResponseDTO<Map<String, Object>> RequestReceiveRoyalPassItem(@RequestBody EternalPassReceiveItemRequestDto eternalPassReceiveItemRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int selecteRewardPassLevel = eternalPassReceiveItemRequestDto.getSelectRewardPassLevel();
        Map<String, Object> response = myEternalPassService.RequestReceiveRoyalPassItem(userId, selecteRewardPassLevel, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/EternalPass/RequestBuyRoyalPass")
    public ResponseDTO<Map<String, Object>> RequestBuyRoyalPass() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myEternalPassService.RequestBuyRoyalPass(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/EternalPass/RequestDirectOpenPassLevel")
    public ResponseDTO<Map<String, Object>> RequestDirectOpenPassLevel(@RequestBody EternalPassDirectOpenPassLevelRequestDto eternalPassDirectOpenPassLevelRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int selectedPassLevel = eternalPassDirectOpenPassLevelRequestDto.getPlusePassLevel();
        Map<String, Object> response = myEternalPassService.RequestDirectOpenPassLevel(userId, selectedPassLevel, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/EternalPass/RequestAllReceiveItem")
    public ResponseDTO<Map<String, Object>> RequestAllReceiveItem() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myEternalPassService.RequestAllReceiveItem(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/EternalPass/CompleteMission")
    public ResponseDTO<Map<String, Object>> CompleteMission(@RequestBody MyEternalPassCompleteRequestDto myEternalPassCompleteRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myEternalPassService.CompleteMission(userId, myEternalPassCompleteRequestDto.getMissionCode(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/EternalPass/GetEternalPassMision")
    public ResponseDTO<Map<String, Object>> GetMyEternalPassMissionData() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myEternalPassService.GetMyEternalPassMissionData(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
