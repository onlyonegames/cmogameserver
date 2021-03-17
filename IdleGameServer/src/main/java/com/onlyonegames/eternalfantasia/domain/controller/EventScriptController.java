package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.EventScriptCompleteDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.EventScriptJumpDto;
import com.onlyonegames.eternalfantasia.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController

public class EventScriptController {
    private final UserService userService;
    @Autowired
    public EventScriptController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping({"/api/EventComplete"})
    public ResponseDTO<Map<String, Object>> EventComplete(@RequestBody EventScriptCompleteDto eventScriptCompleteDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = userService.eventComplete(userId, eventScriptCompleteDto.getCurrentEventNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
    @PostMapping({"/api/EventJump"})
    public ResponseDTO<Map<String, Object>> EventJump(@RequestBody EventScriptJumpDto eventScriptJumpDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = userService.eventJumped(userId, eventScriptJumpDto.getStartEventNo(), eventScriptJumpDto.getJumpedEventNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
