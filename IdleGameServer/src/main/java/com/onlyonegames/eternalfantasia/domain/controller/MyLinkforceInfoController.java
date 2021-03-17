package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.LearnLinkforceRequestDto;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyLinkforceInfoRepository;
import com.onlyonegames.eternalfantasia.domain.service.Companion.MyLinkforceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MyLinkforceInfoController {
    private final MyLinkforceInfoService myLinkforceInfoService;

    @Autowired
    public MyLinkforceInfoController(MyLinkforceInfoService myLinkforceInfoService) {
        this.myLinkforceInfoService = myLinkforceInfoService;
    }

    @GetMapping("/api/GetMyLinkforceInfo")
    public ResponseDTO<Map<String, Object>> GetMyLinkforceInfo() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myLinkforceInfoService.GetLinkforceInfo(userId, map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/LearnLinkforce")
    public ResponseDTO<Map<String, Object>> LearnLinkforce(@RequestBody LearnLinkforceRequestDto learnLinkforceRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myLinkforceInfoService.LearnLinkforce(userId, learnLinkforceRequestDto.getCharacterId(), learnLinkforceRequestDto.getLinkforceId(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }
}
