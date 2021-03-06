package com.onlyonegames.eternalfantasia.domain.controller.Contents;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.service.Contents.ArenaRewardService;
import com.onlyonegames.eternalfantasia.domain.service.Contents.BattlePowerRewardService;
import com.onlyonegames.eternalfantasia.domain.service.Contents.StageRewardService;
import com.onlyonegames.eternalfantasia.domain.service.Contents.WorldBossRewardService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class ContentsRewardController {
    private final ArenaRewardService arenaRewardService;
    private final WorldBossRewardService worldBossRewardService;
    private final StageRewardService stageRewardService;
    private final BattlePowerRewardService battlePowerRewardService;

    @GetMapping("/api/Contents/Arena/GetReward")
    public ResponseDTO<Map<String, Object>> GetArenaReward() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = arenaRewardService.GetArenaReward(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/Contents/WorldBoss/GetReward")
    public ResponseDTO<Map<String, Object>> GetWorldBossReward() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = worldBossRewardService.GetWorldBossReward(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/Contents/Stage/GetReward")
    public ResponseDTO<Map<String, Object>> GetStageReward() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = stageRewardService.GetStageReward(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/Contents/BattlePower/GetReward")
    public ResponseDTO<Map<String, Object>> GetBattlePowerReward() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = battlePowerRewardService.GetBattlePowerReward(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
