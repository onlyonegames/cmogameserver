package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.UserBaseDto;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.MyArenaMatchingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RestController
public class ArenaMatchingController {
    private final MyArenaMatchingService myArenaMatchingService;
    //총 3명의 매칭 상대 리턴
    @GetMapping("/api/Dungeon/Arena/MatchingList")
    public ResponseDTO<Map<String, Object>> GetLeadyVersus3() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myArenaMatchingService.GetLeadyVersus3(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    //총 3명의 매칭 상대 리턴(다이아를 사용하여 한번더 매칭 가능)
    @GetMapping("/api/Dungeon/Arena/ForceMatchingList")
    public ResponseDTO<Map<String, Object>> ForceGetLeadyVersus3ByDiamond() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myArenaMatchingService.ForceGetLeadyVersus3ByDiamond(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    //매칭 상대 정보
    @PostMapping("/api/Dungeon/Arena/MatchingUserInfo")
    public ResponseDTO<Map<String, Object>> MatchingUserInfo(@RequestBody UserBaseDto userBaseDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myArenaMatchingService.MatchingUserInfo(userId, userBaseDto.getId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    //아레나 상대 정보
    @PostMapping("/api/Dungeon/Arena/ArenaUserInfo")
    public ResponseDTO<Map<String, Object>> ArenaUserInfo(@RequestBody UserBaseDto userBaseDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myArenaMatchingService.GetArenaUserInfo(userId, userBaseDto.getId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
