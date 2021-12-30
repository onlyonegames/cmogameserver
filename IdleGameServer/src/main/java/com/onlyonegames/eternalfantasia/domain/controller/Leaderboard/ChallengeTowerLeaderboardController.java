package com.onlyonegames.eternalfantasia.domain.controller.Leaderboard;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class ChallengeTowerLeaderboardController {
    private final WarriorChallengeTowerLeaderboardService warriorChallengeTowerLeaderboardService;
    private final ThiefChallengeTowerLeaderboardService thiefChallengeTowerLeaderboardService;
    private final KnightChallengeTowerLeaderboardService knightChallengeTowerLeaderboardService;
    private final ArcherChallengeTowerLeaderboardService archerChallengeTowerLeaderboardService;
    private final MagicianChallengeTowerLeaderboardService magicianChallengeTowerLeaderboardService;

    @GetMapping("/api/Contents/ChallengeTower/Warrior")
    public ResponseDTO<Map<String, Object>> GetWarriorLeaderboard() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = warriorChallengeTowerLeaderboardService.GetLeaderboardForAllUser(userId, 0, 100, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/Contents/ChallengeTower/Thief")
    public ResponseDTO<Map<String, Object>> GetThiefLeaderboard() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = thiefChallengeTowerLeaderboardService.GetLeaderboardForAllUser(userId, 0, 100, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/Contents/ChallengeTower/Knight")
    public ResponseDTO<Map<String, Object>> GetKnightLeaderboard() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = knightChallengeTowerLeaderboardService.GetLeaderboardForAllUser(userId, 0, 100, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/Contents/ChallengeTower/Archer")
    public ResponseDTO<Map<String, Object>> GetArcherLeaderboard() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = archerChallengeTowerLeaderboardService.GetLeaderboardForAllUser(userId, 0, 100, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/Contents/ChallengeTower/Magician")
    public ResponseDTO<Map<String, Object>> GetMagicianLeaderboard() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = magicianChallengeTowerLeaderboardService.GetLeaderboardForAllUser(userId, 0, 100, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
