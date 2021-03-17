package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.MyMissionBonusGetRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.MyMissionCompleteRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Mission.MyMissionsDataService;
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
public class MyMissionDataController {

    private final MyMissionsDataService myMissionsDataService;

    @GetMapping("/api/Mission/GetMyMissionData")
    public ResponseDTO<Map<String, Object>> GetMyMissionData() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myMissionsDataService.GetMyMissionData(userId, map);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/Mission/CompleteMission")
    public ResponseDTO<Map<String, Object>> CompleteMission(@RequestBody MyMissionCompleteRequestDto myMissionCompleteRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myMissionsDataService.CompleteMission(userId, myMissionCompleteRequestDto.getMissionCode(), map);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/Mission/GetBonusReward")
    public ResponseDTO<Map<String, Object>> GetBonusReward(@RequestBody MyMissionBonusGetRequestDto myMissionBonusGetRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myMissionsDataService.GetBonusReward(userId, myMissionBonusGetRequestDto.getCode(), map);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }
}
