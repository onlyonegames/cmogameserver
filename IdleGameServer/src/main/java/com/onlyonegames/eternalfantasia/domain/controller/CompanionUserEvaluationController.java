package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.EvaluationRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Companion.CompanionUserEvaluationService;
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
public class CompanionUserEvaluationController {
    private final CompanionUserEvaluationService companionUserEvaluationService;

    @Autowired
    public CompanionUserEvaluationController(CompanionUserEvaluationService companionUserEvaluationService) {
        this.companionUserEvaluationService = companionUserEvaluationService;
    }

    @GetMapping("/api/GetEvaluation")
    public ResponseDTO<Map<String, Object>> GetEvaluation() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = companionUserEvaluationService.GetEvaluation(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/AddEvaluation")
    public ResponseDTO<Map<String, Object>> AddEvaluation(@RequestBody EvaluationRequestDto evaluationRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = companionUserEvaluationService.AddEvaluation(userId, evaluationRequestDto.getHeroCode(), evaluationRequestDto.getStarPoint(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
