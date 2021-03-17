package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.controller.managementtool.AddUserIdDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.HotTimeEventDto.HotTimeSchedulerDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.FieldGettingObjectRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.HotTimeEvent.HotTimeEventService;
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
public class HotTimeEventController {
    private final HotTimeEventService hotTimeEventService;

    @GetMapping("/api/Test/HotTimeEvent")
    public ResponseDTO<Map<String, Object>> GetHotTimeEvent() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = hotTimeEventService.getHotTimeEvent(map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/Test/DeleteHotTime")
    public ResponseDTO<Map<String, Object>> DeleteHotTime(@RequestBody AddUserIdDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = hotTimeEventService.deleteHotTimeEvent(dto.getUserId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/Test/SetHotTimeEvent") /* 운영툴용 서비스 */
    public ResponseDTO<Map<String, Object>> SetHotTimeEvent(@RequestBody HotTimeSchedulerDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = hotTimeEventService.setHotTimeEvent(dto, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/HotTimeGettingObject")
    public ResponseDTO<Map<String, Object>> HotTimeGettingObject(@RequestBody FieldGettingObjectRequestDto fieldGettingObjectRequestDto){
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = hotTimeEventService.gettingObject(userId, fieldGettingObjectRequestDto.getSelectObjectId(),  map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
