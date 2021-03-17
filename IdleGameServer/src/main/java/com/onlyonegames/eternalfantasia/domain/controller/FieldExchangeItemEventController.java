package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Event.CommonEventSchedulerDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Event.ItemExchangeDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.FieldGettingObjectRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Event.FieldExchangeItemEventService;
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
public class FieldExchangeItemEventController {
    private final FieldExchangeItemEventService fieldExchangeItemEventService;

    @PostMapping("/api/Test/Event")
    public ResponseDTO<Map<String, Object>> setFieldExchangeItemEvent(@RequestBody CommonEventSchedulerDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = fieldExchangeItemEventService.setFieldExchangeItemEvent(dto, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/FieldExchangeEventGettingObject")
    public ResponseDTO<Map<String, Object>> GettingObject(@RequestBody FieldGettingObjectRequestDto fieldGettingObjectRequestDto){
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = fieldExchangeItemEventService.gettingObject(userId, fieldGettingObjectRequestDto.getSelectObjectId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/ItemExchange")
    public ResponseDTO<Map<String, Object>> ItemExchange(@RequestBody ItemExchangeDto dto){
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = fieldExchangeItemEventService.ExchangeItem(userId, dto.getItemId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/Test/GettingTest")
    public ResponseDTO<Map<String, Object>> GettingTest() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = fieldExchangeItemEventService.GettingTest(map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
