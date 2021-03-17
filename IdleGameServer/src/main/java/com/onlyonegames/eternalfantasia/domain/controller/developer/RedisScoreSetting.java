package com.onlyonegames.eternalfantasia.domain.controller.developer;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RdsScoreDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.RankingInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.UserBaseDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.*;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.ArenaSeasonInfoDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.FieldDungeonInfoDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.Leaderboard.*;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.Leaderboard.FieldDungeonLeaderboardService;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.Leaderboard.InfinityLeaderboardService;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.Leaderboard.LeaderboardService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;

@AllArgsConstructor
@RestController
public class RedisScoreSetting {
    private final LeaderboardService leaderboardService;
    private final RdsScoreRepository rdsScoreRepository;
    private final ArenaSeasonInfoDataRepository arenaSeasonInfoDataRepository;
    //private final RedisTemplate<String, RdsScoreDto> redisRdsScoreDtoTemplate;
    private final RedisTemplate<String, Long> redisLongTemplate;
    private final RedisScoreRepository redisScoreRepository;
    private final InfiniteTowerRedisScoreRepository infiniteTowerRedisScoreRepository;
    private final InfiniteRankingRepository infiniteRankingRepository;
    private final FieldDungeonRankingRepository fieldDungeonRankingRepository;
    private final FieldDungeonInfoDataRepository fieldDungeonInfoDataRepository;
    private final FieldDungeonRedisRankingRepository fieldDungeonRedisRankingRepository;

    @GetMapping("/api/Test/RedisScoreSetting")
    public ResponseDTO<Map<String, Object>> GetMyVisitCompanionInfo() {
        Map<String, Object> map = new HashMap<>();

        //최신 시즌 정보 아이디
        long seasonInfosCount = arenaSeasonInfoDataRepository.count();
        int nowSeasonId = (int)seasonInfosCount;

        long fieldDungeonSeasonInfo = fieldDungeonInfoDataRepository.count();
        int nowFieldDungeonSeason = (int)fieldDungeonSeasonInfo;

        redisLongTemplate.opsForZSet().getOperations().delete(LeaderboardService.ALL_RANKING_LEADERBOARD);
        Iterable<RedisScore> redisScoreList = redisScoreRepository.findAll();
        redisScoreRepository.deleteAll(redisScoreList);

        redisLongTemplate.opsForZSet().getOperations().delete(InfinityLeaderboardService.INFINITETOWER_RANKING_LEADERBOARD);
        Iterable<InfiniteTowerRedisScore> redisScoreRepositoryAll = infiniteTowerRedisScoreRepository.findAll();
        infiniteTowerRedisScoreRepository.deleteAll(redisScoreRepositoryAll);

        redisLongTemplate.opsForZSet().getOperations().delete(FieldDungeonLeaderboardService.FIELDDUNGEON_RANKING_LEADERBOARD);
        Iterable<FieldDungeonRedisRanking> fieldDungeonRedisRankingIterable = fieldDungeonRedisRankingRepository.findAll();
        fieldDungeonRedisRankingRepository.deleteAll(fieldDungeonRedisRankingIterable);

        List<RdsScore> rdsScoreList = rdsScoreRepository.findAll();
        for(RdsScore rdsScore : rdsScoreList)  {
            if(rdsScore.isDummyUser())
                continue;
            //Redis 캐시 저장
            RedisScore redisScore = RedisScore.builder().id(rdsScore.getUseridUser()).score(rdsScore.getScore()).arenaSeasonInfoId(nowSeasonId).userGameName(
                    rdsScore.getUserGameName()).teamCharactersIds(rdsScore.getTeamCharactersIds()).teamCharacterCodes(rdsScore.getTeamCharacterCodes()).rankingtiertableId(rdsScore.getRankingtiertableId()).profileHero(rdsScore.getProfileHero()).profileFrame(rdsScore.getProfileFrame()).build();
            redisScoreRepository.save(redisScore);

            //랭킹을 위한 Redis Set 저장.
            redisLongTemplate.opsForZSet().add(LeaderboardService.ALL_RANKING_LEADERBOARD, rdsScore.getUseridUser(), rdsScore.getScore());
        }
        List<InfiniteRanking> infiniteRankingList = infiniteRankingRepository.findAll();
        for(InfiniteRanking infiniteRanking:infiniteRankingList){
            InfiniteTowerRedisScore infiniteTowerRedisScore = InfiniteTowerRedisScore.builder().id(infiniteRanking.getUseridUser()).score((long)infiniteRanking.getFloor()).userGameName(infiniteRanking.getUserGameName()).teamCharactersIds(infiniteRanking.getTeamCharactersIds())
                    .teamCharacterCodes(infiniteRanking.getTeamCharacterCodes()).profileHero(infiniteRanking.getProfileHero()).profileFrame(infiniteRanking.getProfileFrame()).build();
            infiniteTowerRedisScoreRepository.save(infiniteTowerRedisScore);

            redisLongTemplate.opsForZSet().add(InfinityLeaderboardService.INFINITETOWER_RANKING_LEADERBOARD, infiniteRanking.getUseridUser(), (long)infiniteRanking.getFloor());
        }
        List<FieldDungeonRanking> fieldDungeonRankingList = fieldDungeonRankingRepository.findAll();
        for(FieldDungeonRanking fieldDungeonRanking:fieldDungeonRankingList){
            FieldDungeonRedisRanking infiniteTowerRedisScore = FieldDungeonRedisRanking.builder().id(fieldDungeonRanking.getUseridUser()).totalDamage(fieldDungeonRanking.getTotalDamage()).userGameName(fieldDungeonRanking.getUserGameName()).teamCharactersIds(fieldDungeonRanking.getTeamCharactersIds())
                    .teamCharacterCodes(fieldDungeonRanking.getTeamCharacterCodes()).profileHero(fieldDungeonRanking.getProfileHero()).profileFrame(fieldDungeonRanking.getProfileFrame()).build();
            fieldDungeonRedisRankingRepository.save(infiniteTowerRedisScore);

            redisLongTemplate.opsForZSet().add(FieldDungeonLeaderboardService.FIELDDUNGEON_RANKING_LEADERBOARD, fieldDungeonRanking.getUseridUser(), fieldDungeonRanking.getTotalDamage());
        }

        //map = leaderboardService.GetLeaderboardForAllUser(55L,0, 1000, map);

        map.put("success", true);
        Map<String, Object> response = map;

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }
}
