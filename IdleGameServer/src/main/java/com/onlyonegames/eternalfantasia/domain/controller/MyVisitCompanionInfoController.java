package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.VisitCompanionInfoCommonRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Companion.MyVisitCompanionInfoService;
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
public class MyVisitCompanionInfoController {
    private final MyVisitCompanionInfoService myVisitCompanionInfoService;

    @Autowired
    public MyVisitCompanionInfoController(MyVisitCompanionInfoService myVisitCompanionInfoService) {
        this.myVisitCompanionInfoService = myVisitCompanionInfoService;
    }

    @GetMapping("/api/GetMyVisitCompanionInfo")
    public ResponseDTO<Map<String, Object>> GetMyVisitCompanionInfo() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myVisitCompanionInfoService.GetMyVisitCompanionInfo(userId, map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/RelationStart")
    public ResponseDTO<Map<String, Object>> RelationStart(@RequestBody VisitCompanionInfoCommonRequestDto visitCompanionInfoCommonRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myVisitCompanionInfoService.RelationStart(userId, visitCompanionInfoCommonRequestDto.getHeroCode(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/BreakeCompanion")
    public ResponseDTO<Map<String, Object>> BreakeCompanion(@RequestBody VisitCompanionInfoCommonRequestDto visitCompanionInfoCommonRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myVisitCompanionInfoService.BreakeCompanion(userId, visitCompanionInfoCommonRequestDto.getHeroCode(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/SayHello")
    public ResponseDTO<Map<String, Object>> SyaHello(@RequestBody VisitCompanionInfoCommonRequestDto visitCompanionInfoCommonRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myVisitCompanionInfoService.SayHello(userId, visitCompanionInfoCommonRequestDto.getHeroCode(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/Hunting")
    public ResponseDTO<Map<String, Object>> Hunting(@RequestBody VisitCompanionInfoCommonRequestDto visitCompanionInfoCommonRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myVisitCompanionInfoService.HuntingTryCurrent(userId, visitCompanionInfoCommonRequestDto.getHeroCode(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/SendGift")
    public ResponseDTO<Map<String, Object>> SendGift(@RequestBody VisitCompanionInfoCommonRequestDto visitCompanionInfoCommonRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myVisitCompanionInfoService.SendGift(userId, visitCompanionInfoCommonRequestDto.getHeroCode(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/InstantRecruit")
    public ResponseDTO<Map<String, Object>> InstantRecruit(@RequestBody VisitCompanionInfoCommonRequestDto visitCompanionInfoCommonRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myVisitCompanionInfoService.InstantRecruit(userId, visitCompanionInfoCommonRequestDto.getHeroCode(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }
    /*해당 부분은 tavernVisitCompanionInfoData.linkGaugePercent 가 100 이 되는순간 자동으로 영입 되도록 하는 것으로 수정되면서 사용되지 않음.*/
    @PostMapping("/api/Recruit")
    public ResponseDTO<Map<String, Object>> Recruit(@RequestBody VisitCompanionInfoCommonRequestDto visitCompanionInfoCommonRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myVisitCompanionInfoService.Recruit(userId, visitCompanionInfoCommonRequestDto.getHeroCode(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }
}
