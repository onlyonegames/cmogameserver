package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.controller.managementtool.AddUserIdDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.MyMissionCompleteRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Event.MyAchieveEventService;
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
public class MyAchieveEventcontroller {
    private final MyAchieveEventService myAchieveEventService;

    @GetMapping("api/GetAchieveEventMissionData")
    public ResponseDTO<Map<String, Object>> GetMissionData() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myAchieveEventService.GetMissionData(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);

    }

    @PostMapping("api/GetAchieveEventReward")
    public ResponseDTO<Map<String, Object>> gettingReward(@RequestBody MyMissionCompleteRequestDto myMissionCompleteRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myAchieveEventService.GetRewardAchieveEventItem(userId, myMissionCompleteRequestDto.getMissionCode(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
