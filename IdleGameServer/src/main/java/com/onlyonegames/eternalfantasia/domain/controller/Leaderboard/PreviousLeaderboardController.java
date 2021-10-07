package com.onlyonegames.eternalfantasia.domain.controller.Leaderboard;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard.PreviousRankingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class PreviousLeaderboardController {
    private final PreviousRankingService previousRankingService;

    @GetMapping("/api/Contents/Arena/PreviousRanking")
    public ResponseDTO<Map<String, Object>> GetPreviousArenaRanking() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = previousRankingService.GetPreviousArenaRanking(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/Contents/WorldBoss/PreviousRanking")
    public ResponseDTO<Map<String, Object>> GetPreviousWorldBossRanking() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = previousRankingService.GetPreviousWorldBossRanking(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/Contents/Stage/PreviousRanking")
    public ResponseDTO<Map<String, Object>> GetPreviousStageRanking() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = previousRankingService.GetPreviousStageRanking(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/Contents/BattlePower/PreviousRanking")
    public ResponseDTO<Map<String, Object>> GetPreviousBattlePowerRanking() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = previousRankingService.GetPreviousBattleRanking(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
