package com.onlyonegames.eternalfantasia.domain.controller.Leaderboard;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard.ArenaLeaderboardService;
import com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard.StageLeaderboardService;
import com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard.WorldBossLeaderboardService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class LeaderboardController {
    private final WorldBossLeaderboardService worldBossLeaderboardService;
    private final ArenaLeaderboardService arenaLeaderboardService;
    private final StageLeaderboardService stageLeaderboardService;

    @GetMapping("/api/Contents/WorldBoss/GetAllLeaderboard")
    public ResponseDTO<Map<String, Object>> GetWorldBossLeaderboard() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = worldBossLeaderboardService.GetLeaderboardForAllUser(userId, 0, 100, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/Contents/Arena/GetAllLeaderboard")
    public ResponseDTO<Map<String, Object>> GetArenaLeaderboard() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = arenaLeaderboardService.GetLeaderboardForAllUser(userId, 0, 100, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/Contents/Stage/GetAllLeaderboard")
    public ResponseDTO<Map<String, Object>> GetStageLeaderboard() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = stageLeaderboardService.GetLeaderboardForAllUser(userId, 0, 100, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}