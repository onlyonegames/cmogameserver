package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.StageClearRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.StagePlayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class StagePlayController {
    private final StagePlayService stagePlayService;

    @Autowired
    public StagePlayController(StagePlayService stagePlayService) {
        this.stagePlayService = stagePlayService;
    }

    @PostMapping("/api/StagePlay")
    public ResponseDTO<Map<String, Object>> StagePlay(@RequestBody StageClearRequestDto stageClearRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = stagePlayService.stageStartPlay(userId, stageClearRequestDto.getChapterNo(), stageClearRequestDto.getStageNo(), stageClearRequestDto.getAliveCharacterCount() ,map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/StageClear")
    public ResponseDTO<Map<String, Object>> StageClear(@RequestBody StageClearRequestDto stageClearRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = stagePlayService.stageClear(userId, stageClearRequestDto.getChapterNo(), stageClearRequestDto.getStageNo(), stageClearRequestDto.getAliveCharacterCount() ,map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/StageFail")
    public ResponseDTO<Map<String, Object>> StageFail(@RequestBody StageClearRequestDto stageClearRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = stagePlayService.stageFail(userId, stageClearRequestDto.getChapterNo(), stageClearRequestDto.getStageNo(), stageClearRequestDto.getAliveCharacterCount() ,map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
