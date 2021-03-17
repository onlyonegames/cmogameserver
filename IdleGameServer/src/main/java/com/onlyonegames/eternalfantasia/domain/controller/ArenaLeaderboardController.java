package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.GetUserByRankRangeRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ScoreDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.RankingInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.RdsScore;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.RedisScore;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.Leaderboard.LeaderboardService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RestController
public class ArenaLeaderboardController {
    private final LeaderboardService leaderboardService;

//    @PostMapping("/api/Dungeon/Arena/Leaderboard/GetLeagueLeaderboard")
//    public ResponseDTO<Map<String, Object>> GetLeagueLeaderboard(@RequestBody GetUserByRankRangeRequestDto getUserByRankRangeRequestDto) {
//        Map<String, Object> map = new HashMap<>();
//        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        Map<String, Object> response = leaderboardService.GetLeaderboard(userId, getUserByRankRangeRequestDto.getRangeBottom(), getUserByRankRangeRequestDto.getRangeTop(), map);
//        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
//    }

    @PostMapping("/api/Dungeon/Arena/Leaderboard/GetAllLeaderboard")
    public ResponseDTO<Map<String, Object>> GetLeaderboardForAllUser(@RequestBody GetUserByRankRangeRequestDto getUserByRankRangeRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = leaderboardService.GetLeaderboardForAllUser(userId, getUserByRankRangeRequestDto.getRangeBottom(), getUserByRankRangeRequestDto.getRangeTop(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

//    @GetMapping("/api/Dungeon/Arena/Leaderboard/GetMyLeaderboard")
//    public ResponseDTO<Map<String, Object>> GetMyLeaderboard() {
//        Map<String, Object> map = new HashMap<>();
//        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        Map<String, Object> response = leaderboardService.GetMyLeaderboard(userId, map);
//        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
//    }

//    @PostMapping("/api/Dungeon/Arena/Leaderboard/SetScore")
//    public ResponseDTO<Map<String, Object>> SetScore(@RequestBody ScoreDto scoreDto) {
//        Map<String, Object> map = new HashMap<>();
//        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        RdsScore rdsScore = leaderboardService.setScore(userId, scoreDto.getScore());
//
//        Map<String, Object> response =  map;
//        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
//    }
}
