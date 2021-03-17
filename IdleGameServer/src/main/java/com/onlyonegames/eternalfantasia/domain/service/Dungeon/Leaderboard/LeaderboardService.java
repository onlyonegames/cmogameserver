package com.onlyonegames.eternalfantasia.domain.service.Dungeon.Leaderboard;
import com.google.common.base.Strings;
import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.ProfileDto.ProfileDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RdsScoreDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.RankingInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.RdsScore;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.RedisScore;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyProfileData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyTeamInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.RankingTierTable;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.ArenaSeasonInfoDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.Leaderboard.RdsScoreRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.Leaderboard.RedisScoreRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyProfileDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyTeamInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.util.StringMaker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Transactional
@RequiredArgsConstructor
@Service
public class LeaderboardService {
    public static String ALL_RANKING_LEADERBOARD = "all_ranking_leaderboard";
    //private final RedisTemplate<String, RdsScoreDto> redisRdsScoreDtoTemplate;

    private final RedisTemplate<String, Long> redisLongTemplate;
    private final RedisScoreRepository redisScoreRepository;

    private final MyTeamInfoRepository myTeamInfoRepository;
    private final RdsScoreRepository rdsScoreRepository;
    private final UserRepository userRepository;
    private final MyCharactersRepository myCharactersRepository;
    private final ArenaSeasonInfoDataRepository arenaSeasonInfoDataRepository;
    private final MyProfileDataRepository myProfileDataRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;

    //스코어 랭킹시스템에 저장.
    public RdsScore setScore(Long userId, Long score) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find. userId => " + userId, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find. userId => " + userId, ResponseErrorCode.NOT_FIND_DATA);
        }

        RankingTierTable rankingTierTable = getRankingTier(score, gameDataTableService);

        if(rankingTierTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find RankingTier. score => " + score, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Can't Find RankingTier. score => " + score, ResponseErrorCode.NOT_FIND_DATA);
        }

        int rankingTierTableId = rankingTierTable.getRankingtiertable_id();

        //팀 정보 찾아서 셋팅
        MyTeamInfo myTeamInfo = myTeamInfoRepository.findByUseridUser(userId);
        String teamCharactersIds = myTeamInfo.getArenaPlayTeam();
        String[] teamCharactersIdsArray = teamCharactersIds.split(",");
        List<Long> charactersIdsList = new ArrayList<>();

        for(String characterIdStr : teamCharactersIdsArray) {
            if(Strings.isNullOrEmpty(characterIdStr))
                continue;
            long characterId = Long.parseLong(characterIdStr);
            charactersIdsList.add(characterId);
        }

        List<MyCharacters> myCharactersList = myCharactersRepository.findAllById(charactersIdsList);
        String teamCharacterCodes = getTeamCharacterCodes(myCharactersList);

        //최신 시즌 정보 아이디
        long seasonInfosCount = arenaSeasonInfoDataRepository.count();
        int nowSeasonId = (int)seasonInfosCount;

        MyProfileData myProfileData = myProfileDataRepository.findByUseridUser(userId).orElse(null);
        if(myProfileData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find. userId => " + userId, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find. userId => " + userId, ResponseErrorCode.NOT_FIND_DATA);
        }

        ProfileDataDto profileDataDto = JsonStringHerlper.ReadValueFromJson(myProfileData.getJson_saveDataValue(), ProfileDataDto.class);
        String profileHero = profileDataDto.getProfileHero();
        int profileFrame = profileDataDto.getProfileFrame();

        //Rds 저장 (데이터 원본 및 데이터 분석용)
        RdsScore rdsScore = rdsScoreRepository.findByUseridUser(userId).orElse(null);
        if(rdsScore == null) {
            rdsScore = RdsScore.builder().useridUser(userId).userGameName(
                    user.getUserGameName()).score(score).arenaSeasonInfoId(nowSeasonId).teamCharactersIds(teamCharactersIds).teamCharacterCodes(teamCharacterCodes).rankingtiertableId(rankingTierTableId)
                    .profileHero(profileHero).profileFrame(profileFrame).build();
            rdsScoreRepository.save(rdsScore);
        }
        else {
            rdsScore.refresh(teamCharactersIds, teamCharacterCodes, score, rankingTierTableId, nowSeasonId, profileHero, profileFrame, user.isDummyUser());
        }
        if(!user.isDummyUser()){
            //Redis 캐시 저장
            RedisScore redisScore = redisScoreRepository.findById(userId).orElse(null);
            if (redisScore == null) {//redis 에 해당 유저 캐싱 정보 없으므로 만들어줌.
                redisScore = RedisScore.builder().id(userId).score(score).arenaSeasonInfoId(nowSeasonId).userGameName(
                        user.getUserGameName()).teamCharactersIds(teamCharactersIds).teamCharacterCodes(teamCharacterCodes).rankingtiertableId(rankingTierTableId)
                        .profileHero(profileHero).profileFrame(profileFrame).build();
                redisScoreRepository.save(redisScore);
            } else {
                //재캐싱 하였으니 팀정보 및 점수를 포함한 저장시간 리프레쉬.
                redisScore.refresh(teamCharactersIds, teamCharacterCodes, score, rankingTierTableId, nowSeasonId, profileHero, profileFrame);
                redisScoreRepository.save(redisScore);
            }
            //랭킹을 위한 Redis Set 저장.
            redisLongTemplate.opsForZSet().add(ALL_RANKING_LEADERBOARD, userId, score);
        }
        return rdsScore;
    }

    //특정 유저의 랭킹 정보 획득.
    public Long getRank(Long userId) {
        Long myRank = redisLongTemplate.opsForZSet().reverseRank(ALL_RANKING_LEADERBOARD, userId);
        if(myRank == null)
            myRank = 0L;
        myRank = myRank + 1L;
        return myRank;
    }

    //전체 랭킹
    public Map<String, Object> GetLeaderboardForAllUser(Long userId, long bottom, long top, Map<String, Object> map) {
        Set<ZSetOperations.TypedTuple<Long>> rankings = Optional.ofNullable(redisLongTemplate.opsForZSet().reverseRangeWithScores(ALL_RANKING_LEADERBOARD, bottom, top)).orElse(Collections.emptySet());
        List<RankingInfoDto> list = new ArrayList<>();
        int ranking = 1;
        for(ZSetOperations.TypedTuple<Long> user : rankings) {
            RedisScore findUser = redisScoreRepository.findById(user.getValue()).get();
//            MyProfileData myProfileData = myProfileDataRepository.findByUseridUser(user.getValue()).orElse(null);
//            if(myProfileData == null) {
//                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyProfileData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
//                throw new MyCustomException("Fail! -> Cause: MyProfileData not find.", ResponseErrorCode.NOT_FIND_DATA);
//            }
//            ProfileDataDto profileDataDto = JsonStringHerlper.ReadValueFromJson(myProfileData.getJson_saveDataValue(), ProfileDataDto.class);
            String profileHero = findUser.getProfileHero();
            int profileFrame = findUser.getProfileFrame();
            RankingInfoDto rankingInfoDto = RankingInfoDto.builder().useridUser(findUser.getId())
                    .userGameName(findUser.getUserGameName())
                    .teamCharacterCodes(findUser.getTeamCharacterCodes())
                    .score(findUser.getScore())
                    .score(findUser.getScore())
                    .ranking(ranking++)
                    .rankingtiertableId(findUser.getRankingtiertableId())
                    .profileHero(profileHero)
                    .profileFrame(profileFrame).build();
            list.add(rankingInfoDto);
        }

        //최신 시즌 정보 아이디
        long seasonInfosCount = arenaSeasonInfoDataRepository.count();
        int nowSeasonId = (int)seasonInfosCount;
        //내 랭킹
        RdsScore rdsScore = rdsScoreRepository.findByUseridUser(userId).orElse(null);
        RankingInfoDto myRankingInfo;
        MyProfileData myProfileData = myProfileDataRepository.findByUseridUser(userId).orElse(null);
        if(myProfileData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyProfileData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyProfileData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        ProfileDataDto profileDataDto = JsonStringHerlper.ReadValueFromJson(myProfileData.getJson_saveDataValue(), ProfileDataDto.class);
        String profileHero = profileDataDto.getProfileHero();
        int profileFrame = profileDataDto.getProfileFrame();
        if(rdsScore != null) {
            if(rdsScore.getArenaSeasonInfoId() != nowSeasonId) {
                //시즌 바뀌고 한번도 플레이 하지 않음.
                myRankingInfo = RankingInfoDto.builder().useridUser(userId)
                        .userGameName(rdsScore.getUserGameName())
                        .teamCharacterCodes(rdsScore.getTeamCharacterCodes())
                        .score(0L)
                        .ranking(0)
                        .rankingtiertableId(21)
                        .profileHero(profileHero)
                        .profileFrame(profileFrame).build();
            }
            else {
                myRankingInfo = RankingInfoDto.builder().useridUser(rdsScore.getUseridUser())
                        .userGameName(rdsScore.getUserGameName())
                        .teamCharacterCodes(rdsScore.getTeamCharacterCodes())
                        .score(rdsScore.getScore())
                        .ranking((getRank(rdsScore.getUseridUser()).intValue()))
                        .rankingtiertableId(rdsScore.getRankingtiertableId())
                        .profileHero(profileHero)
                        .profileFrame(profileFrame).build();
            }
        }
        else {
            User user = userRepository.findById(userId).orElse(null);
            if(user == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: user not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: user not find", ResponseErrorCode.NOT_FIND_DATA);
            }

            //팀 정보 찾아서 셋팅
            MyTeamInfo myTeamInfo = myTeamInfoRepository.findByUseridUser(userId);
            String teamCharactersIds = myTeamInfo.getArenaPlayTeam();
            String[] teamCharactersIdsArray = teamCharactersIds.split(",");
            List<Long> charactersIdsList = new ArrayList<>();
            for(String characterIdStr : teamCharactersIdsArray) {
                if(Strings.isNullOrEmpty(characterIdStr))
                    continue;
                long characterId = Long.parseLong(characterIdStr);
                charactersIdsList.add(characterId);
            }
            List<MyCharacters> myCharactersList = myCharactersRepository.findAllById(charactersIdsList);
            String teamCharacterCodes = LeaderboardService.getTeamCharacterCodes(myCharactersList);

            myRankingInfo = RankingInfoDto.builder().useridUser(userId)
                    .userGameName(user.getUserGameName())
                    .teamCharacterCodes(teamCharacterCodes)
                    .score(0L)
                    .ranking(0)
                    .rankingtiertableId(21)
                    .profileHero(profileHero)
                    .profileFrame(profileFrame).build();
        }
        map.put("myRankingInfo", myRankingInfo);
        map.put("ranking", list);
        return map;
    }
    public static String getTeamCharacterCodes(List<MyCharacters> myCharactersList) {
        StringMaker.Clear();
        for(MyCharacters myCharacter : myCharactersList) {
            String myCharacterCode = myCharacter.getCodeHerostable();
            if(StringMaker.stringBuilder.length() > 0) {
                StringMaker.stringBuilder.append(",");
            }
            StringMaker.stringBuilder.append(myCharacterCode);
        }
        return StringMaker.stringBuilder.toString();
    }
    //특정 점수로 RankingTierTable 정보를 리턴.
    public static RankingTierTable getRankingTier(long point, GameDataTableService gameDataTableService) {
        List<RankingTierTable> rankingTierTableList = gameDataTableService.RankingTierTableList();
        for(RankingTierTable rankingTierTable : rankingTierTableList) {
            String pointScope = rankingTierTable.getPointScope();
            String[] pointScopeArray = pointScope.split("~");
            long lowPoint = Long.parseLong(pointScopeArray[0]);
            long highPoint = Long.parseLong(pointScopeArray[1]);
            if(point >= lowPoint && point <= highPoint) {
               return rankingTierTable;
            }
        }
        return null;
    }
}
