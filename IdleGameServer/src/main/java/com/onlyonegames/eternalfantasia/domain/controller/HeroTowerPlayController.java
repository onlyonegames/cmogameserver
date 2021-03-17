package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.HeroTowerPlayRequestCommonDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.StageClearRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.MyHeroTowerPlayService;
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
public class HeroTowerPlayController {
    private final MyHeroTowerPlayService myHeroTowerPlayService;

    @GetMapping("/api/HeroTowerPlayTimeSet")
    public ResponseDTO<Map<String, Object>> HeroTowerPlayTimeSet() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myHeroTowerPlayService.HeroTowerPlayTimeSet(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/HeroTowerPlay")
    public ResponseDTO<Map<String, Object>> Play(@RequestBody HeroTowerPlayRequestCommonDto heroTowerPlayRequestCommonDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myHeroTowerPlayService.StartHeroTowerPlay(userId, heroTowerPlayRequestCommonDto.getFloorNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/HeroTowerClear")
    public ResponseDTO<Map<String, Object>> Clear(@RequestBody HeroTowerPlayRequestCommonDto heroTowerPlayRequestCommonDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myHeroTowerPlayService.HeroTowerClear(userId, heroTowerPlayRequestCommonDto.getPlaySecond(), heroTowerPlayRequestCommonDto.getFloorNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/HeroTowerFail")
    public ResponseDTO<Map<String, Object>> Fail(@RequestBody HeroTowerPlayRequestCommonDto heroTowerPlayRequestCommonDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myHeroTowerPlayService.HeroTowerFail(userId, heroTowerPlayRequestCommonDto.getFloorNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
