package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ExchangeCurrencyDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ExchangeItemRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.EventExchangeService;
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
public class EventExchangeController {
    private final EventExchangeService eventExchangeService;

    @PostMapping("/api/Event/ExchangeItem")
    public ResponseDTO<Map<String, Object>> ExchangeItem(@RequestBody ExchangeItemRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = eventExchangeService.ExchangeItem(userId, dto.getExchangeItemIndex(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/Event/ExchangeCurrency")
    public ResponseDTO<Map<String, Object>> ExchangeCurrency(@RequestBody ExchangeCurrencyDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = eventExchangeService.ExchangeAdvancedEventItem(userId, dto.getExchangeCount(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
