package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.RequestDto;
import com.onlyonegames.eternalfantasia.domain.service.MyForTestService;
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
public class MyForTestController
{
    private final MyForTestService myForTestService;

    @GetMapping("api/GetForTest")
    public ResponseDTO<Map<String, Object>> GetMyForTest()
    {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myForTestService.GetMyForTest(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("api/SetForTest")
    public ResponseDTO<Map<String, Object>> SetMyForTest(@RequestBody RequestDto requestDto)
    {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myForTestService.SetMyForTest(userId, requestDto.getKey(), requestDto.getValue(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}