package com.onlyonegames.eternalfantasia.domain.controller.Test;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.*;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.*;
import com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard.ArenaLeaderboardService;
import com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard.BattlePowerLeaderboardService;
import com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard.StageLeaderboardService;
import com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard.WorldBossLeaderboardService;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class RedisScoreSetting {
    private final WorldBossRankingRepository worldBossRankingRepository;
    private final RedisTemplate<String, Long> redisLongTemplate;
    private final WorldBossRedisRankingRepository worldBossRedisRankingRepository;
    private final ArenaRankingRepository arenaRankingRepository;
    private final ArenaRedisRankingRepository arenaRedisRankingRepository;
    private final StageRankingRepository stageRankingRepository;
    private final StageRedisRankingRepository stageRedisRankingRepository;
    private final BattlePowerRankingRepository battlePowerRankingRepository;
    private final BattlePowerRedisRankingRepository battlePowerRedisRankingRepository;

    @GetMapping("/api/Test/RedisScoreSetting")
    public ResponseDTO<Map<String, Object>> ResidScoreSetting() {
        Map<String, Object> map = new HashMap<>();

        redisLongTemplate.opsForZSet().getOperations().delete(WorldBossLeaderboardService.WORLD_BOSS_RANKING_LEADERBOARD);
        Iterable<WorldBossRedisRanking> worldBossRedisRankingList = worldBossRedisRankingRepository.findAll();
        worldBossRedisRankingRepository.deleteAll(worldBossRedisRankingList);

        redisLongTemplate.opsForZSet().getOperations().delete(ArenaLeaderboardService.ARENA_RANKING_LEADERBOARD);
        Iterable<ArenaRedisRanking> arenaRedisRankingList = arenaRedisRankingRepository.findAll();
        arenaRedisRankingRepository.deleteAll(arenaRedisRankingList);

        redisLongTemplate.opsForZSet().getOperations().delete(StageLeaderboardService.STAGE_RANKING_LEADERBOARD);
        Iterable<StageRedisRanking> stageRedisRankingList = stageRedisRankingRepository.findAll();
        stageRedisRankingRepository.deleteAll();

        redisLongTemplate.opsForZSet().getOperations().delete(BattlePowerLeaderboardService.BATTLE_POWER_LEADERBOARD);
        Iterable<BattlePowerRedisRanking> battlePowerRedisRankingList = battlePowerRedisRankingRepository.findAll();
        battlePowerRedisRankingRepository.deleteAll();

        List<WorldBossRanking> worldBossRankingList = worldBossRankingRepository.findAll();
        for(WorldBossRanking worldBossRanking : worldBossRankingList) {
            if (worldBossRanking.getTotalDamage().equals(0L))
                continue;
            WorldBossRedisRanking worldBossRedisRanking = WorldBossRedisRanking.builder().id(worldBossRanking.getUseridUser())
                    .userGameName(worldBossRanking.getUserGameName()).totalDamage(worldBossRanking.getTotalDamage()).build();
            worldBossRedisRankingRepository.save(worldBossRedisRanking);

            redisLongTemplate.opsForZSet().add(WorldBossLeaderboardService.WORLD_BOSS_RANKING_LEADERBOARD, worldBossRanking.getUseridUser(), worldBossRanking.getTotalDamage());
        }

        List<ArenaRanking> arenaRankingList = arenaRankingRepository.findAll();
        for(ArenaRanking arenaRanking : arenaRankingList) {
            ArenaRedisRanking arenaRedisRanking = ArenaRedisRanking.builder().id(arenaRanking.getUseridUser())
                    .userGameName(arenaRanking.getUserGameName()).point(arenaRanking.getPoint()).build();
            arenaRedisRankingRepository.save(arenaRedisRanking);

            redisLongTemplate.opsForZSet().add(ArenaLeaderboardService.ARENA_RANKING_LEADERBOARD, arenaRanking.getUseridUser(), arenaRanking.getPoint());
        }

        List<StageRanking> stageRankingList = stageRankingRepository.findAll();
        for (StageRanking stageRanking : stageRankingList) {
            StageRedisRanking stageRedisRanking = StageRedisRanking.builder().id(stageRanking.getUseridUser())
                    .userGameName(stageRanking.getUserGameName()).point(stageRanking.getPoint()).build();
            stageRedisRankingRepository.save(stageRedisRanking);

            redisLongTemplate.opsForZSet().add(StageLeaderboardService.STAGE_RANKING_LEADERBOARD, stageRanking.getUseridUser(), stageRanking.getPoint());
        }

        List<BattlePowerRanking> battlePowerRankingList = battlePowerRankingRepository.findAll();
        for (BattlePowerRanking battlePowerRanking : battlePowerRankingList) {
            BattlePowerRedisRanking battlePowerRedisRanking = BattlePowerRedisRanking.builder().id(battlePowerRanking.getUseridUser())
                    .userGameName(battlePowerRanking.getUserGameName()).battlePower(battlePowerRanking.getBattlePower()).build();
            battlePowerRedisRankingRepository.save(battlePowerRedisRanking);

            redisLongTemplate.opsForZSet().add(BattlePowerLeaderboardService.BATTLE_POWER_LEADERBOARD, battlePowerRanking.getUseridUser(), battlePowerRanking.getBattlePower());
        }

        map.put("test", worldBossRedisRankingRepository.findAll());
        map.put("success", true);

        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, map);
    }
}
