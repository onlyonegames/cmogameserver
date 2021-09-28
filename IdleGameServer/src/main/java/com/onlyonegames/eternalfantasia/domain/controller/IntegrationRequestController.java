package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.IntegrationRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.IntegrationRequestService;
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
public class IntegrationRequestController {
    private final IntegrationRequestService integrationRequestService;

    @PostMapping("/api/IntegrationRequest")
    public ResponseDTO<Map<String, Object>> IntegrationRequest(@RequestBody IntegrationRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = integrationRequestService.IdleItemGetting(userId, dto, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
