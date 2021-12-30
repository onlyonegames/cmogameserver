package com.onlyonegames.eternalfantasia.domain.controller.Test;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.*;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.*;
import com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard.*;
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
    private final ChallengeTowerWarriorRankingRepository challengeTowerWarriorRankingRepository;
    private final ChallengeTowerWarriorRedisRankingRepository challengeTowerWarriorRedisRankingRepository;
    private final ChallengeTowerThiefRankingRepository challengeTowerThiefRankingRepository;
    private final ChallengeTowerThiefRedisRankingRepository challengeTowerThiefRedisRankingRepository;
    private final ChallengeTowerKnightRankingRepository challengeTowerKnightRankingRepository;
    private final ChallengeTowerKnightRedisRankingRepository challengeTowerKnightRedisRankingRepository;
    private final ChallengeTowerArcherRankingRepository challengeTowerArcherRankingRepository;
    private final ChallengeTowerArcherRedisRankingRepository challengeTowerArcherRedisRankingRepository;
    private final ChallengeTowerMagicianRankingRepository challengeTowerMagicianRankingRepository;
    private final ChallengeTowerMagicianRedisRankingRepository challengeTowerMagicianRedisRankingRepository;

    @GetMapping("/api/Test/RedisScoreSetting")
    public ResponseDTO<Map<String, Object>> RedisScoreSetting() {
        Map<String, Object> map = new HashMap<>();

        redisLongTemplate.opsForZSet().getOperations().delete(WorldBossLeaderboardService.WORLD_BOSS_RANKING_LEADERBOARD);
        worldBossRedisRankingRepository.deleteAll();

        redisLongTemplate.opsForZSet().getOperations().delete(ArenaLeaderboardService.ARENA_RANKING_LEADERBOARD);
        arenaRedisRankingRepository.deleteAll();

        redisLongTemplate.opsForZSet().getOperations().delete(StageLeaderboardService.STAGE_RANKING_LEADERBOARD);
        stageRedisRankingRepository.deleteAll();

        redisLongTemplate.opsForZSet().getOperations().delete(BattlePowerLeaderboardService.BATTLE_POWER_LEADERBOARD);
        battlePowerRedisRankingRepository.deleteAll();

        redisLongTemplate.opsForZSet().getOperations().delete(WarriorChallengeTowerLeaderboardService.WARRIOR_CHALLENGE_RANKING_LEADERBOARD);
        challengeTowerWarriorRedisRankingRepository.deleteAll();

        redisLongTemplate.opsForZSet().getOperations().delete(ThiefChallengeTowerLeaderboardService.THIEF_CHALLENGE_RANKING_LEADERBOARD);
        challengeTowerThiefRedisRankingRepository.deleteAll();

        redisLongTemplate.opsForZSet().getOperations().delete(KnightChallengeTowerLeaderboardService.KNIGHT_CHALLENGE_RANKING_LEADERBOARD);
        challengeTowerKnightRedisRankingRepository.deleteAll();

        redisLongTemplate.opsForZSet().getOperations().delete(ArcherChallengeTowerLeaderboardService.ARCHER_CHALLENGE_RANKING_LEADERBOARD);
        challengeTowerArcherRedisRankingRepository.deleteAll();

        redisLongTemplate.opsForZSet().getOperations().delete(MagicianChallengeTowerLeaderboardService.MAGICIAN_CHALLENGE_RANKING_LEADERBOARD);
        challengeTowerMagicianRedisRankingRepository.deleteAll();


        List<WorldBossRanking> worldBossRankingList = worldBossRankingRepository.findAll();
        for(WorldBossRanking worldBossRanking : worldBossRankingList) {
            if (worldBossRanking.getTotalDamage() == 0D || worldBossRanking.isBlack())
                continue;
            WorldBossRedisRanking worldBossRedisRanking = WorldBossRedisRanking.builder().id(worldBossRanking.getUseridUser())
                    .userGameName(worldBossRanking.getUserGameName()).totalDamage(worldBossRanking.getTotalDamage()).build();
            worldBossRedisRankingRepository.save(worldBossRedisRanking);

            redisLongTemplate.opsForZSet().add(WorldBossLeaderboardService.WORLD_BOSS_RANKING_LEADERBOARD, worldBossRanking.getUseridUser(), worldBossRanking.getTotalDamage());
        }

        List<ArenaRanking> arenaRankingList = arenaRankingRepository.findAll();
        for(ArenaRanking arenaRanking : arenaRankingList) {
            if (arenaRanking.isBlack())
                continue;
            ArenaRedisRanking arenaRedisRanking = ArenaRedisRanking.builder().id(arenaRanking.getUseridUser())
                    .userGameName(arenaRanking.getUserGameName()).point(arenaRanking.getPoint()).build();
            arenaRedisRankingRepository.save(arenaRedisRanking);

            redisLongTemplate.opsForZSet().add(ArenaLeaderboardService.ARENA_RANKING_LEADERBOARD, arenaRanking.getUseridUser(), arenaRanking.getPoint());
        }

        List<StageRanking> stageRankingList = stageRankingRepository.findAll();
        for (StageRanking stageRanking : stageRankingList) {
            if (stageRanking.isBlack())
                continue;
            StageRedisRanking stageRedisRanking = StageRedisRanking.builder().id(stageRanking.getUseridUser())
                    .userGameName(stageRanking.getUserGameName()).point(stageRanking.getPoint()).build();
            stageRedisRankingRepository.save(stageRedisRanking);

            redisLongTemplate.opsForZSet().add(StageLeaderboardService.STAGE_RANKING_LEADERBOARD, stageRanking.getUseridUser(), stageRanking.getPoint());
        }

        List<BattlePowerRanking> battlePowerRankingList = battlePowerRankingRepository.findAll();
        for (BattlePowerRanking battlePowerRanking : battlePowerRankingList) {
            if (battlePowerRanking.isBlack())
                continue;
            BattlePowerRedisRanking battlePowerRedisRanking = BattlePowerRedisRanking.builder().id(battlePowerRanking.getUseridUser())
                    .userGameName(battlePowerRanking.getUserGameName()).battlePower(battlePowerRanking.getBattlePower()).build();
            battlePowerRedisRankingRepository.save(battlePowerRedisRanking);

            redisLongTemplate.opsForZSet().add(BattlePowerLeaderboardService.BATTLE_POWER_LEADERBOARD, battlePowerRanking.getUseridUser(), battlePowerRanking.getBattlePower());
        }

        List<ChallengeTowerWarriorRanking> challengeTowerWarriorRankingList = challengeTowerWarriorRankingRepository.findAll();
        for (ChallengeTowerWarriorRanking challengeTowerWarriorRanking : challengeTowerWarriorRankingList) {
            if (challengeTowerWarriorRanking.isBlack())
                continue;
            ChallengeTowerWarriorRedisRanking challengeTowerWarriorRedisRanking = ChallengeTowerWarriorRedisRanking.builder()
                    .id(challengeTowerWarriorRanking.getUseridUser()).userGameName(challengeTowerWarriorRanking.getUserGameName())
                    .point(challengeTowerWarriorRanking.getPoint()).build();
            challengeTowerWarriorRedisRankingRepository.save(challengeTowerWarriorRedisRanking);

            redisLongTemplate.opsForZSet().add(WarriorChallengeTowerLeaderboardService.WARRIOR_CHALLENGE_RANKING_LEADERBOARD, challengeTowerWarriorRanking.getUseridUser(), challengeTowerWarriorRanking.getPoint());
        }
        List<ChallengeTowerThiefRanking> challengeTowerThiefRankingList = challengeTowerThiefRankingRepository.findAll();
        for (ChallengeTowerThiefRanking challengeTowerThiefRanking : challengeTowerThiefRankingList) {
            if (challengeTowerThiefRanking.isBlack())
                continue;
            ChallengeTowerThiefRedisRanking challengeTowerThiefRedisRanking = ChallengeTowerThiefRedisRanking.builder()
                    .id(challengeTowerThiefRanking.getUseridUser()).userGameName(challengeTowerThiefRanking.getUserGameName())
                    .point(challengeTowerThiefRanking.getPoint()).build();
            challengeTowerThiefRedisRankingRepository.save(challengeTowerThiefRedisRanking);

            redisLongTemplate.opsForZSet().add(ThiefChallengeTowerLeaderboardService.THIEF_CHALLENGE_RANKING_LEADERBOARD, challengeTowerThiefRanking.getUseridUser(), challengeTowerThiefRanking.getPoint());
        }
        List<ChallengeTowerKnightRanking> challengeTowerKnightRankingList = challengeTowerKnightRankingRepository.findAll();
        for (ChallengeTowerKnightRanking challengeTowerKnightRanking : challengeTowerKnightRankingList) {
            if (challengeTowerKnightRanking.isBlack())
                continue;
            ChallengeTowerKnightRedisRanking challengeTowerKnightRedisRanking = ChallengeTowerKnightRedisRanking.builder()
                    .id(challengeTowerKnightRanking.getUseridUser()).userGameName(challengeTowerKnightRanking.getUserGameName())
                    .point(challengeTowerKnightRanking.getPoint()).build();
            challengeTowerKnightRedisRankingRepository.save(challengeTowerKnightRedisRanking);

            redisLongTemplate.opsForZSet().add(KnightChallengeTowerLeaderboardService.KNIGHT_CHALLENGE_RANKING_LEADERBOARD, challengeTowerKnightRanking.getUseridUser(), challengeTowerKnightRanking.getPoint());
        }
        List<ChallengeTowerArcherRanking> challengeTowerArcherRankingList = challengeTowerArcherRankingRepository.findAll();
        for (ChallengeTowerArcherRanking challengeTowerArcherRanking : challengeTowerArcherRankingList) {
            if (challengeTowerArcherRanking.isBlack())
                continue;
            ChallengeTowerArcherRedisRanking challengeTowerArcherRedisRanking = ChallengeTowerArcherRedisRanking.builder()
                    .id(challengeTowerArcherRanking.getUseridUser()).userGameName(challengeTowerArcherRanking.getUserGameName())
                    .point(challengeTowerArcherRanking.getPoint()).build();
            challengeTowerArcherRedisRankingRepository.save(challengeTowerArcherRedisRanking);

            redisLongTemplate.opsForZSet().add(ArcherChallengeTowerLeaderboardService.ARCHER_CHALLENGE_RANKING_LEADERBOARD, challengeTowerArcherRanking.getUseridUser(), challengeTowerArcherRanking.getPoint());
        }
        List<ChallengeTowerMagicianRanking> challengeTowerMagicianRankingList = challengeTowerMagicianRankingRepository.findAll();
        for (ChallengeTowerMagicianRanking challengeTowerMagicianRanking : challengeTowerMagicianRankingList) {
            if (challengeTowerMagicianRanking.isBlack())
                continue;
            ChallengeTowerMagicianRedisRanking challengeTowerMagicianRedisRanking = ChallengeTowerMagicianRedisRanking.builder()
                    .id(challengeTowerMagicianRanking.getUseridUser()).userGameName(challengeTowerMagicianRanking.getUserGameName())
                    .point(challengeTowerMagicianRanking.getPoint()).build();
            challengeTowerMagicianRedisRankingRepository.save(challengeTowerMagicianRedisRanking);

            redisLongTemplate.opsForZSet().add(MagicianChallengeTowerLeaderboardService.MAGICIAN_CHALLENGE_RANKING_LEADERBOARD, challengeTowerMagicianRanking.getUseridUser(), challengeTowerMagicianRanking.getPoint());
        }

//        map.put("test", worldBossRedisRankingRepository.findAll());
        map.put("success", true);

        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, map);
    }
    @GetMapping("/api/Test/RedisScoreSettingForBattlePower")
    public ResponseDTO<Map<String, Object>> RedisScoreSettingForBattlePower() {
        Map<String, Object> map = new HashMap<>();
//
//        redisLongTemplate.opsForZSet().getOperations().delete(WorldBossLeaderboardService.WORLD_BOSS_RANKING_LEADERBOARD);
//        Iterable<WorldBossRedisRanking> worldBossRedisRankingList = worldBossRedisRankingRepository.findAll();
//        worldBossRedisRankingRepository.deleteAll(worldBossRedisRankingList);

//        redisLongTemplate.opsForZSet().getOperations().delete(ArenaLeaderboardService.ARENA_RANKING_LEADERBOARD);
//        Iterable<ArenaRedisRanking> arenaRedisRankingList = arenaRedisRankingRepository.findAll();
//        arenaRedisRankingRepository.deleteAll(arenaRedisRankingList);

//        redisLongTemplate.opsForZSet().getOperations().delete(StageLeaderboardService.STAGE_RANKING_LEADERBOARD);
//        Iterable<StageRedisRanking> stageRedisRankingList = stageRedisRankingRepository.findAll();
//        stageRedisRankingRepository.deleteAll();

        redisLongTemplate.opsForZSet().getOperations().delete(BattlePowerLeaderboardService.BATTLE_POWER_LEADERBOARD);
        Iterable<BattlePowerRedisRanking> battlePowerRedisRankingList = battlePowerRedisRankingRepository.findAll();
        battlePowerRedisRankingRepository.deleteAll();

//        List<WorldBossRanking> worldBossRankingList = worldBossRankingRepository.findAll();
//        for(WorldBossRanking worldBossRanking : worldBossRankingList) {
//            if (worldBossRanking.getTotalDamage() == 0D)
//                continue;
//            WorldBossRedisRanking worldBossRedisRanking = WorldBossRedisRanking.builder().id(worldBossRanking.getUseridUser())
//                    .userGameName(worldBossRanking.getUserGameName()).totalDamage(worldBossRanking.getTotalDamage()).build();
//            worldBossRedisRankingRepository.save(worldBossRedisRanking);
//
//            redisLongTemplate.opsForZSet().add(WorldBossLeaderboardService.WORLD_BOSS_RANKING_LEADERBOARD, worldBossRanking.getUseridUser(), worldBossRanking.getTotalDamage());
//        }
//
//        List<ArenaRanking> arenaRankingList = arenaRankingRepository.findAll();
//        for(ArenaRanking arenaRanking : arenaRankingList) {
//            ArenaRedisRanking arenaRedisRanking = ArenaRedisRanking.builder().id(arenaRanking.getUseridUser())
//                    .userGameName(arenaRanking.getUserGameName()).point(arenaRanking.getPoint()).build();
//            arenaRedisRankingRepository.save(arenaRedisRanking);
//
//            redisLongTemplate.opsForZSet().add(ArenaLeaderboardService.ARENA_RANKING_LEADERBOARD, arenaRanking.getUseridUser(), arenaRanking.getPoint());
//        }
//
//        List<StageRanking> stageRankingList = stageRankingRepository.findAll();
//        for (StageRanking stageRanking : stageRankingList) {
//            StageRedisRanking stageRedisRanking = StageRedisRanking.builder().id(stageRanking.getUseridUser())
//                    .userGameName(stageRanking.getUserGameName()).point(stageRanking.getPoint()).build();
//            stageRedisRankingRepository.save(stageRedisRanking);
//
//            redisLongTemplate.opsForZSet().add(StageLeaderboardService.STAGE_RANKING_LEADERBOARD, stageRanking.getUseridUser(), stageRanking.getPoint());
//        }

        List<BattlePowerRanking> battlePowerRankingList = battlePowerRankingRepository.findAll();
        for (BattlePowerRanking battlePowerRanking : battlePowerRankingList) {
            battlePowerRanking.refresh(0);
            BattlePowerRedisRanking battlePowerRedisRanking = BattlePowerRedisRanking.builder().id(battlePowerRanking.getUseridUser())
                    .userGameName(battlePowerRanking.getUserGameName()).battlePower(battlePowerRanking.getBattlePower()).build();
            battlePowerRedisRankingRepository.save(battlePowerRedisRanking);

            redisLongTemplate.opsForZSet().add(BattlePowerLeaderboardService.BATTLE_POWER_LEADERBOARD, battlePowerRanking.getUseridUser(), battlePowerRanking.getBattlePower());
        }

        map.put("test", battlePowerRankingList);
        map.put("success", true);

        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, map);
    }
}
