package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.GetUserByRankRangeRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.Leaderboard.InfinityLeaderboardService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RestController
public class InfiniteTowerLeaderboardController {
    private final InfinityLeaderboardService infinityLeaderboardService;

    @PostMapping("/api/Dungeon/InfiniteTower/Leaderboard/GetAllLeaderboard")
    public ResponseDTO<Map<String, Object>> GetLeaderboardForAllUser(@RequestBody GetUserByRankRangeRequestDto getUserByRankRangeRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = infinityLeaderboardService.GetLeaderboardForAllUser(userId, getUserByRankRangeRequestDto.getRangeBottom(), getUserByRankRangeRequestDto.getRangeTop(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
