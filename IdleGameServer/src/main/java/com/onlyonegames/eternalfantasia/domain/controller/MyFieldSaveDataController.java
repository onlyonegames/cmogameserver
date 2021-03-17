package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.AttendanceRewardRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ChangeSkywalkerRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.FieldGettingObjectRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.SelectedFieldRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.MyNewFieldSaveDataService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class MyFieldSaveDataController {
    private final MyNewFieldSaveDataService myNewFieldSaveDataService;

    @PostMapping("/api/Field/NewSelecteField")
    public ResponseDTO<Map<String, Object>> NewSelecteField(@RequestBody SelectedFieldRequestDto selectedFieldRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myNewFieldSaveDataService.selecteField(userId, selectedFieldRequestDto.getPlaingFieldNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/Field/ChangeSkywalker")
    public ResponseDTO<Map<String, Object>> ChangeSkywalker(@RequestBody ChangeSkywalkerRequestDto changeSkywalkerRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myNewFieldSaveDataService.changeSkywalker(userId, changeSkywalkerRequestDto.getPlaingFieldNo(), changeSkywalkerRequestDto.getChangeId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/Field/NewGettingObject")
    public ResponseDTO<Map<String, Object>> NewGettingObject(@RequestBody FieldGettingObjectRequestDto fieldGettingObjectRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myNewFieldSaveDataService.gettingObject(userId, fieldGettingObjectRequestDto.getSelectObjectId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
    
    @PostMapping("/api/AttendanceReward")
    public ResponseDTO<Map<String, Object>> GetAttendanceReward(@RequestBody AttendanceRewardRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myNewFieldSaveDataService.getAttendanceReward(userId, dto.getRewardIndex(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
