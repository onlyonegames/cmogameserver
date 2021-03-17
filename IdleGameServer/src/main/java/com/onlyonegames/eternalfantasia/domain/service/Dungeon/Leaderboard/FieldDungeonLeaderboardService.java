package com.onlyonegames.eternalfantasia.domain.service.Dungeon.Leaderboard;

import com.google.common.base.Strings;
import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.ProfileDto.ProfileDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.RankingInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.FieldDungeonRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.FieldDungeonRedisRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.InfiniteRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.InfiniteTowerRedisScore;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyProfileData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyTeamInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.Leaderboard.FieldDungeonRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.Leaderboard.FieldDungeonRedisRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyProfileDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyTeamInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Transactional
@RequiredArgsConstructor
@Service
public class FieldDungeonLeaderboardService {
    public static String FIELDDUNGEON_RANKING_LEADERBOARD = "fielddungeon_ranking_leaderboard";
    private final RedisTemplate<String, Long> redisLongTemplate;
    private final FieldDungeonRedisRankingRepository fieldDungeonRedisRankingRepository;

    private final MyTeamInfoRepository myTeamInfoRepository;
    private final FieldDungeonRankingRepository fieldDungeonRankingRepository;
    private final UserRepository userRepository;
    private final MyCharactersRepository myCharactersRepository;
    private final MyProfileDataRepository myProfileDataRepository;
    private final ErrorLoggingService errorLoggingService;

    public Long getRank(FieldDungeonRanking userRdsScore) {
        Long userId = userRdsScore.getUseridUser();
        Long myRank = redisLongTemplate.opsForZSet().reverseRank(FIELDDUNGEON_RANKING_LEADERBOARD, userId);
        if(myRank == null)
            myRank = 0L;
        myRank = myRank + 1L;
        return myRank;
    }

    public Map<String, Object> GetLeaderboardForAllUser(Long userId, long bottom, long top, Map<String, Object> map) {
        Set<ZSetOperations.TypedTuple<Long>> rankings = Optional.ofNullable(redisLongTemplate.opsForZSet().reverseRangeWithScores(FIELDDUNGEON_RANKING_LEADERBOARD, bottom, top)).orElse(Collections.emptySet());
        List<RankingInfoDto> list = new ArrayList<>();
        int ranking = 1;
        Long tempScore = 0L;
        int tempRanking = 0;
        for(ZSetOperations.TypedTuple<Long> user : rankings) {
            Long id = user.getValue();
            FieldDungeonRedisRanking value = fieldDungeonRedisRankingRepository.findById(id).get();
            MyProfileData myProfileData = myProfileDataRepository.findByUseridUser(user.getValue()).orElse(null);
            if(myProfileData == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyProfileData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: MyProfileData not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            if(!tempScore.equals(value.getTotalDamage())) {
                tempScore = value.getTotalDamage();
                tempRanking = ranking;
            }

            ProfileDataDto profileDataDto = JsonStringHerlper.ReadValueFromJson(myProfileData.getJson_saveDataValue(), ProfileDataDto.class);
            String profileHero = profileDataDto.getProfileHero();
            int profileFrame = profileDataDto.getProfileFrame();
            RankingInfoDto rankingInfoDto = RankingInfoDto.builder().useridUser(value.getId())
                    .userGameName(value.getUserGameName())
                    .teamCharacterCodes(value.getTeamCharacterCodes())
                    .score(value.getTotalDamage())
                    .ranking(tempRanking)
                    .rankingtiertableId(1)
                    .profileHero(profileHero)
                    .profileFrame(profileFrame).build();
            list.add(rankingInfoDto);
            ranking++;
        }

        //내 랭킹
        FieldDungeonRanking fieldDungeonRanking = fieldDungeonRankingRepository.findByUseridUser(userId).orElse(null);
        RankingInfoDto myRankingInfo;
        MyProfileData myProfileData = myProfileDataRepository.findByUseridUser(userId).orElse(null);
        if(myProfileData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyProfileData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyProfileData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        ProfileDataDto profileDataDto = JsonStringHerlper.ReadValueFromJson(myProfileData.getJson_saveDataValue(), ProfileDataDto.class);
        String profileHero = profileDataDto.getProfileHero();
        int profileFrame = profileDataDto.getProfileFrame();
        if(fieldDungeonRanking != null) {
            myRankingInfo = RankingInfoDto.builder().useridUser(fieldDungeonRanking.getUseridUser())
                    .userGameName(fieldDungeonRanking.getUserGameName())
                    .teamCharacterCodes(fieldDungeonRanking.getTeamCharacterCodes())
                    .score(fieldDungeonRanking.getTotalDamage())
                    .ranking((getRank(fieldDungeonRanking).intValue()))
                    .rankingtiertableId(1)
                    .profileHero(profileHero)
                    .profileFrame(profileFrame).build();
        }
        else {
            User user = userRepository.findById(userId).orElse(null);
            if(user == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: user not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: user not find", ResponseErrorCode.NOT_FIND_DATA);
            }

            //팀 정보 찾아서 셋팅
            MyTeamInfo myTeamInfo = myTeamInfoRepository.findByUseridUser(userId);
            String teamCharactersIds = myTeamInfo.getFieldDungeonPlayTeam();
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
}
