package com.onlyonegames.eternalfantasia.domain.service.Dungeon;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.MyArenaSeasonSaveDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CurrencyLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ProfileDto.MyProfileMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ProfileDto.ProfileDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RdsScoreDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.MailSendRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.PreviousArenaSeasonRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.RdsScore;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyArenaSeasonSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyProfileData;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.ArenaRankingRewardTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.ArenaRewardsTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.RankingTierTable;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.ArenaSeasonInfoDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.Leaderboard.PreviousArenaSeasonRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.Leaderboard.RdsScoreRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyArenaSeasonSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyProfileDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.Leaderboard.LeaderboardService;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import com.onlyonegames.eternalfantasia.domain.service.Mail.MyMailBoxService;
import com.onlyonegames.eternalfantasia.domain.service.MyProfileService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.eternalfantasia.etc.SystemMailInfos;
import com.onlyonegames.util.StringMaker;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyArenaRewardReceiveService {
    private final MyArenaSeasonSaveDataRepository myArenaSeasonSaveDataRepository;
    private final GameDataTableService gameDataTableService;
    private final UserRepository userRepository;
    private final RdsScoreRepository rdsScoreRepository;
    private final PreviousArenaSeasonRankingRepository previousArenaSeasonRankingRepository;
    private final ArenaSeasonInfoDataRepository arenaSeasonInfoDataRepository;
    private final MyProfileDataRepository myProfileDataRepository;
    private final MyMailBoxService myMailBoxService;
    private final MyProfileService myProfileService;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;

    public Map<String, Object> GetReward(Long userId, Map<String, Object> map) {

        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyArenaSeasonSaveData myArenaSeasonSaveData = myArenaSeasonSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myArenaSeasonSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyArenaSeasonSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyArenaSeasonSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        PreviousArenaSeasonRanking previousArenaSeasonRanking = previousArenaSeasonRankingRepository.findByUseridUser(userId)
                .orElse(null);

        /**아레나 티어가 20보다 크다는것은 현재 시즌에서 한번도 아레나에 참여한적이 없다는것으로 해당 프로세스 돌릴 필요 없다.*/
        if(myArenaSeasonSaveData.getRankingtiertable_id() > 20 && !(myArenaSeasonSaveData.isChangedSeason() && previousArenaSeasonRanking != null)) {
            MyArenaSeasonSaveDataDto myArenaSeasonSaveDataDto = new MyArenaSeasonSaveDataDto();
            myArenaSeasonSaveDataDto.InitFromDbData(myArenaSeasonSaveData);
            map.put("myArenaSeasonSaveData", myArenaSeasonSaveDataDto);
            map.put("user", user);
            //직전 시즌 넘버(시즌 리셋 API 적용후 시즌 번호 받아서 처리해야함.)
            //최신 시즌 정보 아이디
            long seasonInfosCount = arenaSeasonInfoDataRepository.count();
            int nowSeasonId = (int)seasonInfosCount;

            map.put("seasonNo", nowSeasonId - 1);
            return map;
        }



        List<ArenaRewardsTable> arenaRewardsTableList = gameDataTableService.ArenaRewardsTableList();
//
//        ArenaRewardsTable arenaRewardsTable = arenaRewardsTableList.stream()
//                .filter(a -> a.getArenarewardstable_id() == myArenaSeasonSaveData.getRankingtiertable_id())
//                .findAny()
//                .orElse(null);
//        if(arenaRewardsTable == null) {
//            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: arenaRewardsTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
//            throw new MyCustomException("Fail! -> Cause: arenaRewardsTable not find.", ResponseErrorCode.NOT_FIND_DATA);
//        }
//
//
//
//        map.put("arenaRewardsTableId", arenaRewardsTable.getArenarewardstable_id());
        LocalDateTime now = LocalDateTime.now();

        /*프로필 프레임 획득 체크 준비*/
        MyProfileData myProfileData = myProfileDataRepository.findByUseridUser(userId).orElse(null);
        if(myProfileData == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyProfileData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyProfileData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String json_missionData = myProfileData.getJson_missionData();
        String json_saveDataValue = myProfileData.getJson_saveDataValue();
        MyProfileMissionDataDto myProfileMissionDataDto = JsonStringHerlper.ReadValueFromJson(json_missionData, MyProfileMissionDataDto.class);
        ProfileDataDto profileDataDto = JsonStringHerlper.ReadValueFromJson(json_saveDataValue, ProfileDataDto.class);
        boolean changedProfileMissionData = false;

        /*직전 시즌에서 달성한 티어별 보상(시즌보상)*/
        if(myArenaSeasonSaveData.isChangedSeason() && previousArenaSeasonRanking != null) {
            ArenaRewardsTable arenaSeasonRewardsTable = arenaRewardsTableList.stream()
                    .filter(a -> a.getArenarewardstable_id() == previousArenaSeasonRanking.getRankingtiertableId())
                    .findAny()
                    .orElse(null);
            if(arenaSeasonRewardsTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: arenaRewardsTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: arenaRewardsTable not find.", ResponseErrorCode.NOT_FIND_DATA);
            }

            map.put("arenaSeasonRewardsTableId", arenaSeasonRewardsTable.getArenarewardstable_id());
            String[] rewardsArray = arenaSeasonRewardsTable.getSeason().split(",");
            List<MailSendRequestDto.Item> itemList = new ArrayList<>();
            for (String reward:rewardsArray){
                String[] splitTemp = reward.split(":");
                MailSendRequestDto.Item item = new MailSendRequestDto.Item();
                item.setItem(splitTemp[0], Integer.parseInt(splitTemp[1]));
                itemList.add(item);
            }

            Map<String, Object> fakeMap = new HashMap<>();
            MailSendRequestDto mailSendRequestDto = new MailSendRequestDto();
            StringMaker.Clear();
            StringMaker.stringBuilder.append(previousArenaSeasonRanking.getArenaSeasonInfoId());
            StringMaker.stringBuilder.append("시즌 보상");
            String title = StringMaker.stringBuilder.toString();
            mailSendRequestDto.SetMailSendRequestDto(title, 10000L, userId, SystemMailInfos.ARENA_SEASON_REWARD_TITLE, itemList, 1, now.plusYears(1));
            myMailBoxService.SendMail(mailSendRequestDto, fakeMap);
//            for (String rewardInfos : rewardsArray) {
//                String[] rewardInfoArray = rewardInfos.split(":");
//                String rewardInfo = rewardInfoArray[0];
//                int gettingCount = Integer.parseInt(rewardInfoArray[1]);
//                if (rewardInfo.equals("diamond")) {
//                    CurrencyLogDto currencyLogDto = new CurrencyLogDto();
//                    int previousValue = user.getDiamond();
//                    user.AddDiamond(gettingCount);
//                    currencyLogDto.setCurrencyLogDto("전 시즌보상 - " + myArenaSeasonSaveData.getRankingtiertable_id() + "티어", "다이아", previousValue, gettingCount, user.getDiamond());
//                    String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
//                    loggingService.setLogging(userId, 1, log);
//                } else if (rewardInfo.equals("arenaCoin")) {
//                    CurrencyLogDto currencyLogDto = new CurrencyLogDto();
//                    int previousValue = user.getArenaCoin();
//                    user.AddArenaCoin(gettingCount);
//                    currencyLogDto.setCurrencyLogDto("전 시즌보상 - " + myArenaSeasonSaveData.getRankingtiertable_id() + "티어", "아레나 코인", previousValue, gettingCount, user.getArenaCoin());
//                    String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
//                    loggingService.setLogging(userId, 1, log);
//                }
//            }

            /*순위보상 아레나 랭킹 보상은 구조가 달라서 테이블 다르게 씀. 순위 보상은 시즌 보상이랑 같은 곳에서 처리됨(achieveSeasonReward 하나로 퉁칠수 있음)*/
            List<ArenaRankingRewardTable> arenaRankingRewardTableList = gameDataTableService.ArenaRankingRewardTableList();

            Long myRanking = previousArenaSeasonRanking.getRanking();
            if(myRanking == 1) {
                ArenaRankingRewardTable arenaRankingRewardTable = arenaRankingRewardTableList.get(0);
                rewardsArray =  arenaRankingRewardTable.getReward().split(",");
                map.put("arenaRankingRewardTableId", arenaRankingRewardTable.getId());
                List<MailSendRequestDto.Item> rankingRewarditemList = new ArrayList<>();
                for (String reward:rewardsArray){
                    String[] splitTemp = reward.split(":");
                    if(splitTemp[0].equals("profileFrame"))
                        continue;
                    MailSendRequestDto.Item item = new MailSendRequestDto.Item();
                    item.setItem(splitTemp[0], Integer.parseInt(splitTemp[1]));
                    rankingRewarditemList.add(item);
                }
                StringMaker.Clear();
                StringMaker.stringBuilder.append(previousArenaSeasonRanking.getArenaSeasonInfoId());
                StringMaker.stringBuilder.append("시즌 순위 1등 보상");
                title = StringMaker.stringBuilder.toString();
                mailSendRequestDto.SetMailSendRequestDto(title, 10000L, userId, SystemMailInfos.ARENA_SEASON_REWARD_TITLE, rankingRewarditemList, 1, now.plusYears(1));
                myMailBoxService.SendMail(mailSendRequestDto, fakeMap);
                //SetArenaRankingReward(user, arenaRankingRewardTable, "랭킹보상 - "+myRanking+"위");
                //명예의 전당 설정 가능
                map.put("hollofHonorSetable", true);

                changedProfileMissionData = myProfileMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_ARENA_TIER.name(), "top3", gameDataTableService.ProfileFrameMissionTableList()) || changedProfileMissionData;
            }
            else if(myRanking == 2) {
                ArenaRankingRewardTable arenaRankingRewardTable = arenaRankingRewardTableList.get(1);
                rewardsArray =  arenaRankingRewardTable.getReward().split(",");
                map.put("arenaRankingRewardTableId", arenaRankingRewardTable.getId());
                List<MailSendRequestDto.Item> rankingRewarditemList = new ArrayList<>();
                for (String reward:rewardsArray){
                    String[] splitTemp = reward.split(":");
                    if(splitTemp[0].equals("profileFrame"))
                        continue;
                    MailSendRequestDto.Item item = new MailSendRequestDto.Item();
                    item.setItem(splitTemp[0], Integer.parseInt(splitTemp[1]));
                    rankingRewarditemList.add(item);
                }
                StringMaker.Clear();
                StringMaker.stringBuilder.append(previousArenaSeasonRanking.getArenaSeasonInfoId());
                StringMaker.stringBuilder.append("시즌 순위 2등 보상");
                title = StringMaker.stringBuilder.toString();
                mailSendRequestDto.SetMailSendRequestDto(title, 10000L, userId, SystemMailInfos.ARENA_SEASON_REWARD_TITLE, rankingRewarditemList, 1, now.plusYears(1));
                myMailBoxService.SendMail(mailSendRequestDto, fakeMap);
                //SetArenaRankingReward(user, arenaRankingRewardTable, "랭킹보상 - "+myRanking+"위");

                changedProfileMissionData = myProfileMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_ARENA_TIER.name(), "top3", gameDataTableService.ProfileFrameMissionTableList()) || changedProfileMissionData;
            }
            else if(myRanking == 3) {
                ArenaRankingRewardTable arenaRankingRewardTable = arenaRankingRewardTableList.get(2);
                rewardsArray =  arenaRankingRewardTable.getReward().split(",");
                map.put("arenaRankingRewardTableId", arenaRankingRewardTable.getId());
                List<MailSendRequestDto.Item> rankingRewarditemList = new ArrayList<>();
                for (String reward:rewardsArray){
                    String[] splitTemp = reward.split(":");
                    if(splitTemp[0].equals("profileFrame"))
                        continue;
                    MailSendRequestDto.Item item = new MailSendRequestDto.Item();
                    item.setItem(splitTemp[0], Integer.parseInt(splitTemp[1]));
                    rankingRewarditemList.add(item);
                }
                StringMaker.Clear();
                StringMaker.stringBuilder.append(previousArenaSeasonRanking.getArenaSeasonInfoId());
                StringMaker.stringBuilder.append("시즌 순위 3등 보상");
                title = StringMaker.stringBuilder.toString();
                mailSendRequestDto.SetMailSendRequestDto(title, 10000L, userId, SystemMailInfos.ARENA_SEASON_REWARD_TITLE, rankingRewarditemList, 1, now.plusYears(1));
                myMailBoxService.SendMail(mailSendRequestDto, fakeMap);
                //SetArenaRankingReward(user, arenaRankingRewardTable, "랭킹보상 - "+myRanking+"위");

                changedProfileMissionData = myProfileMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_ARENA_TIER.name(), "top3", gameDataTableService.ProfileFrameMissionTableList()) || changedProfileMissionData;
            }
            else {
                long totalCount = previousArenaSeasonRankingRepository.count();

                float myGradePercent = Math.round(myRanking * 100f / totalCount);
                if(myGradePercent <= 10){
                    ArenaRankingRewardTable arenaRankingRewardTable = arenaRankingRewardTableList.get(3);
                    rewardsArray =  arenaRankingRewardTable.getReward().split(",");
                    map.put("arenaRankingRewardTableId", arenaRankingRewardTable.getId());
                    List<MailSendRequestDto.Item> rankingRewarditemList = new ArrayList<>();
                    for (String reward:rewardsArray){
                        String[] splitTemp = reward.split(":");
                        MailSendRequestDto.Item item = new MailSendRequestDto.Item();
                        item.setItem(splitTemp[0], Integer.parseInt(splitTemp[1]));
                        rankingRewarditemList.add(item);
                    }
                    StringMaker.Clear();
                    StringMaker.stringBuilder.append(previousArenaSeasonRanking.getArenaSeasonInfoId());
                    StringMaker.stringBuilder.append("시즌 순위 1 ~ 10% 보상");
                    title = StringMaker.stringBuilder.toString();
                    mailSendRequestDto.SetMailSendRequestDto(title, 10000L, userId, SystemMailInfos.ARENA_SEASON_REWARD_TITLE, rankingRewarditemList, 1, now.plusYears(1));
                    myMailBoxService.SendMail(mailSendRequestDto, fakeMap);
                    //SetArenaRankingReward(user, arenaRankingRewardTable, "랭킹보상 - 1~10%");
                    }
                else if(myGradePercent <= 30) {
                    ArenaRankingRewardTable arenaRankingRewardTable = arenaRankingRewardTableList.get(4);
                    rewardsArray =  arenaRankingRewardTable.getReward().split(",");
                    map.put("arenaRankingRewardTableId", arenaRankingRewardTable.getId());
                    List<MailSendRequestDto.Item> rankingRewarditemList = new ArrayList<>();
                    for (String reward:rewardsArray){
                        String[] splitTemp = reward.split(":");
                        MailSendRequestDto.Item item = new MailSendRequestDto.Item();
                        item.setItem(splitTemp[0], Integer.parseInt(splitTemp[1]));
                        rankingRewarditemList.add(item);
                    }
                    StringMaker.Clear();
                    StringMaker.stringBuilder.append(previousArenaSeasonRanking.getArenaSeasonInfoId());
                    StringMaker.stringBuilder.append("시즌 순위 11 ~ 30% 보상");
                    title = StringMaker.stringBuilder.toString();
                    mailSendRequestDto.SetMailSendRequestDto(title, 10000L, userId, SystemMailInfos.ARENA_SEASON_REWARD_TITLE, rankingRewarditemList, 1, now.plusYears(1));
                    myMailBoxService.SendMail(mailSendRequestDto, fakeMap);
                    //SetArenaRankingReward(user, arenaRankingRewardTable, "랭킹보상 - 11~30%");
                }
                else if(myGradePercent <= 50) {
                    ArenaRankingRewardTable arenaRankingRewardTable = arenaRankingRewardTableList.get(5);
                    rewardsArray =  arenaRankingRewardTable.getReward().split(",");
                    map.put("arenaRankingRewardTableId", arenaRankingRewardTable.getId());
                    List<MailSendRequestDto.Item> rankingRewarditemList = new ArrayList<>();
                    for (String reward:rewardsArray){
                        String[] splitTemp = reward.split(":");
                        MailSendRequestDto.Item item = new MailSendRequestDto.Item();
                        item.setItem(splitTemp[0], Integer.parseInt(splitTemp[1]));
                        rankingRewarditemList.add(item);
                    }
                    StringMaker.Clear();
                    StringMaker.stringBuilder.append(previousArenaSeasonRanking.getArenaSeasonInfoId());
                    StringMaker.stringBuilder.append("시즌 순위 31 ~ 50% 보상");
                    title = StringMaker.stringBuilder.toString();
                    mailSendRequestDto.SetMailSendRequestDto(title, 10000L, userId, SystemMailInfos.ARENA_SEASON_REWARD_TITLE, rankingRewarditemList, 1, now.plusYears(1));
                    myMailBoxService.SendMail(mailSendRequestDto, fakeMap);
                    //SetArenaRankingReward(user, arenaRankingRewardTable, "랭킹보상 - 31~50%");
                }
                else if(myGradePercent <= 70) {
                    ArenaRankingRewardTable arenaRankingRewardTable = arenaRankingRewardTableList.get(6);
                    rewardsArray =  arenaRankingRewardTable.getReward().split(",");
                    map.put("arenaRankingRewardTableId", arenaRankingRewardTable.getId());
                    List<MailSendRequestDto.Item> rankingRewarditemList = new ArrayList<>();
                    for (String reward:rewardsArray){
                        String[] splitTemp = reward.split(":");
                        MailSendRequestDto.Item item = new MailSendRequestDto.Item();
                        item.setItem(splitTemp[0], Integer.parseInt(splitTemp[1]));
                        rankingRewarditemList.add(item);
                    }
                    StringMaker.Clear();
                    StringMaker.stringBuilder.append(previousArenaSeasonRanking.getArenaSeasonInfoId());
                    StringMaker.stringBuilder.append("시즌 순위 51 ~ 10% 보상");
                    title = StringMaker.stringBuilder.toString();
                    mailSendRequestDto.SetMailSendRequestDto(title, 10000L, userId, SystemMailInfos.ARENA_SEASON_REWARD_TITLE, rankingRewarditemList, 1, now.plusYears(1));
                    myMailBoxService.SendMail(mailSendRequestDto, fakeMap);
                    //SetArenaRankingReward(user, arenaRankingRewardTable, "랭킹보상 - 51~70%");
                }
                else if(myGradePercent <= 90) {
                    ArenaRankingRewardTable arenaRankingRewardTable = arenaRankingRewardTableList.get(7);
                    rewardsArray =  arenaRankingRewardTable.getReward().split(",");
                    map.put("arenaRankingRewardTableId", arenaRankingRewardTable.getId());
                    List<MailSendRequestDto.Item> rankingRewarditemList = new ArrayList<>();
                    for (String reward:rewardsArray){
                        String[] splitTemp = reward.split(":");
                        MailSendRequestDto.Item item = new MailSendRequestDto.Item();
                        item.setItem(splitTemp[0], Integer.parseInt(splitTemp[1]));
                        rankingRewarditemList.add(item);
                    }
                    StringMaker.Clear();
                    StringMaker.stringBuilder.append(previousArenaSeasonRanking.getArenaSeasonInfoId());
                    StringMaker.stringBuilder.append("시즌 순위 71 ~ 90% 보상");
                    title = StringMaker.stringBuilder.toString();
                    mailSendRequestDto.SetMailSendRequestDto(title, 10000L, userId, SystemMailInfos.ARENA_SEASON_REWARD_TITLE, rankingRewarditemList, 1, now.plusYears(1));
                    myMailBoxService.SendMail(mailSendRequestDto, fakeMap);
                    //SetArenaRankingReward(user, arenaRankingRewardTable, "랭킹보상 - 71~90%");
                }
                else {
                    ArenaRankingRewardTable arenaRankingRewardTable = arenaRankingRewardTableList.get(8);
                    rewardsArray =  arenaRankingRewardTable.getReward().split(",");
                    map.put("arenaRankingRewardTableId", arenaRankingRewardTable.getId());
                    List<MailSendRequestDto.Item> rankingRewarditemList = new ArrayList<>();
                    for (String reward:rewardsArray){
                        String[] splitTemp = reward.split(":");
                        MailSendRequestDto.Item item = new MailSendRequestDto.Item();
                        item.setItem(splitTemp[0], Integer.parseInt(splitTemp[1]));
                        rankingRewarditemList.add(item);
                    }
                    StringMaker.Clear();
                    StringMaker.stringBuilder.append(previousArenaSeasonRanking.getArenaSeasonInfoId());
                    StringMaker.stringBuilder.append("시즌 순위 91 ~ 100% 보상");
                    title = StringMaker.stringBuilder.toString();
                    mailSendRequestDto.SetMailSendRequestDto(title, 10000L, userId, SystemMailInfos.ARENA_SEASON_REWARD_TITLE, rankingRewarditemList, 1, now.plusYears(1));
                    myMailBoxService.SendMail(mailSendRequestDto, fakeMap);
                    //SetArenaRankingReward(user, arenaRankingRewardTable, "랭킹보상 - 91~100%");
                }
            }
            map.put("seasonReward", true);

            myArenaSeasonSaveData.OffChangedSeasonFlag();
        }
        //등급 상승 보상을 받을수 있는 조건이 된다면 해당 보상 지급.(리그보상)
        if(myArenaSeasonSaveData.isReceiveableTierUpReward()) {
            ArenaRewardsTable arenaTierUpRewardsTable = arenaRewardsTableList.stream()
                    .filter(a -> a.getArenarewardstable_id() == myArenaSeasonSaveData.getHighestRankingtiertable_id())
                    .findAny()
                    .orElse(null);
            if(arenaTierUpRewardsTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: arenaRewardsTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: arenaRewardsTable not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            List<RankingTierTable> rankingTierTableList = gameDataTableService.RankingTierTableList();
            RankingTierTable rankingTierTable = rankingTierTableList.stream().filter(i -> i.getRankingtiertable_id() == myArenaSeasonSaveData.getHighestRankingtiertable_id())
                    .findAny()
                    .orElse(null);
            if(rankingTierTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: RankingTierTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: RankingTierTable not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            map.put("arenaTierUpRewardsTableId", arenaTierUpRewardsTable.getArenarewardstable_id());
            String[] rewardsArray = arenaTierUpRewardsTable.getLeague().split(",");
            List<MailSendRequestDto.Item> itemList = new ArrayList<>();
            for (String reward:rewardsArray){
                String[] splitTemp = reward.split(":");
                MailSendRequestDto.Item item = new MailSendRequestDto.Item();
                item.setItem(splitTemp[0], Integer.parseInt(splitTemp[1]));
                itemList.add(item);
            }

            Map<String, Object> fakeMap = new HashMap<>();
            MailSendRequestDto mailSendRequestDto = new MailSendRequestDto();
            StringMaker.Clear();
            StringMaker.stringBuilder.append("최초 ");
            StringMaker.stringBuilder.append(rankingTierTable.getGradeName());
            StringMaker.stringBuilder.append(" 달성 보상");
            String title = StringMaker.stringBuilder.toString();
            mailSendRequestDto.SetMailSendRequestDto(title, 10000L, userId, SystemMailInfos.ARENA_SEASON_REWARD_TITLE, itemList, 1, now.plusYears(1));
            myMailBoxService.SendMail(mailSendRequestDto, fakeMap);
//            for (String rewardInfos : rewardsArray) {
//                String[] rewardInfoArray = rewardInfos.split(":");
//                String rewardInfo = rewardInfoArray[0];
//                int gettingCount = Integer.parseInt(rewardInfoArray[1]);
//                if (rewardInfo.equals("diamond")) {
//                    CurrencyLogDto currencyLogDto = new CurrencyLogDto();
//                    int previousValue = user.getDiamond();
//                    user.AddDiamond(gettingCount);
//                    currencyLogDto.setCurrencyLogDto("등급 상승보상 - " + myArenaSeasonSaveData.getRankingtiertable_id() + "티어", "다이아", previousValue, gettingCount, user.getDiamond());
//                    String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
//                    loggingService.setLogging(userId, 1, log);
//                } else if (rewardInfo.equals("arenaCoin")) {
//                    CurrencyLogDto currencyLogDto = new CurrencyLogDto();
//                    int previousValue = user.getArenaCoin();
//                    user.AddArenaCoin(gettingCount);
//                    currencyLogDto.setCurrencyLogDto("등급 상승보상 - " + myArenaSeasonSaveData.getRankingtiertable_id() + "티어", "아레나 코인", previousValue, gettingCount, user.getArenaCoin());
//                    String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
//                    loggingService.setLogging(userId, 1, log);
//                }
//            }
            changedProfileMissionData = myProfileMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_ARENA_TIER.name(), rankingTierTable.getRankGrade(), gameDataTableService.ProfileFrameMissionTableList()) || changedProfileMissionData;
            myArenaSeasonSaveData.ReceiveTierUpReward();
            map.put("tierUpReward", true);
        }
        /*일일보상*/
        if(myArenaSeasonSaveData.isReceiveableDailyReward() && myArenaSeasonSaveData.getRankingtiertable_id() < 21) {
            ArenaRewardsTable arenaDailyRewardsTable = arenaRewardsTableList.stream()
                    .filter(a -> a.getArenarewardstable_id() == myArenaSeasonSaveData.getRankingtiertable_id())
                    .findAny()
                    .orElse(null);
            if(arenaDailyRewardsTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: arenaRewardsTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: arenaRewardsTable not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            map.put("arenaDailyRewardsTableId", arenaDailyRewardsTable.getArenarewardstable_id());
            String[] rewardsArray = arenaDailyRewardsTable.getDaily().split(",");
            List<MailSendRequestDto.Item> itemList = new ArrayList<>();
            for (String reward:rewardsArray){
                String[] splitTemp = reward.split(":");
                MailSendRequestDto.Item item = new MailSendRequestDto.Item();
                item.setItem(splitTemp[0], Integer.parseInt(splitTemp[1]));
                itemList.add(item);
            }

            Map<String, Object> fakeMap = new HashMap<>();
            MailSendRequestDto mailSendRequestDto = new MailSendRequestDto();
            mailSendRequestDto.SetMailSendRequestDto("아레나 일일보상", 10000L, userId, SystemMailInfos.ARENA_SEASON_REWARD_TITLE, itemList, 1, now.plusYears(1));
            myMailBoxService.SendMail(mailSendRequestDto, fakeMap);
//           for (String rewardInfos : rewardsArray) {
//                String[] rewardInfoArray = rewardInfos.split(":");
//                String rewardInfo = rewardInfoArray[0];
//                int gettingCount = Integer.parseInt(rewardInfoArray[1]);
//                if (rewardInfo.equals("diamond")) {
//                    CurrencyLogDto currencyLogDto = new CurrencyLogDto();
//                    int previousValue = user.getDiamond();
//                    user.AddDiamond(gettingCount);
//                    currencyLogDto.setCurrencyLogDto("일일 보상 - " + myArenaSeasonSaveData.getRankingtiertable_id() + "티어", "다이아", previousValue, gettingCount, user.getDiamond());
//                    String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
//                    loggingService.setLogging(userId, 1, log);
//                } else if (rewardInfo.equals("arenaCoin")) {
//                    CurrencyLogDto currencyLogDto = new CurrencyLogDto();
//                    int previousValue = user.getArenaCoin();
//                    user.AddArenaCoin(gettingCount);
//                    currencyLogDto.setCurrencyLogDto("일일 보상 - " + myArenaSeasonSaveData.getRankingtiertable_id() + "티어", "아레나 코인", previousValue, gettingCount, user.getArenaCoin());
//                    String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
//                    loggingService.setLogging(userId, 1, log);
//                }
//            }
            myArenaSeasonSaveData.ReceiveDailyReward();
            map.put("dailyReward", true);
        }
        MyArenaSeasonSaveDataDto myArenaSeasonSaveDataDto = new MyArenaSeasonSaveDataDto();
        myArenaSeasonSaveDataDto.InitFromDbData(myArenaSeasonSaveData);
        map.put("myArenaSeasonSaveData", myArenaSeasonSaveDataDto);
        map.put("user", user);
        //직전 시즌 넘버(시즌 리셋 API 적용후 시즌 번호 받아서 처리해야함.)
        //최신 시즌 정보 아이디
        long seasonInfosCount = arenaSeasonInfoDataRepository.count();
        int nowSeasonId = (int)seasonInfosCount;

//        if(changedProfileMissionData)
//            Profile
        //TODO Mail정보 Getting 추가 필요
        myMailBoxService.GetMyMailBox(userId, map);
        map.put("seasonNo", nowSeasonId - 1);
        if(changedProfileMissionData) {
            myProfileService.GetProfileFrame(userId, myProfileMissionDataDto, profileDataDto);
            json_saveDataValue = JsonStringHerlper.WriteValueAsStringFromData(profileDataDto);
            json_missionData = JsonStringHerlper.WriteValueAsStringFromData(myProfileMissionDataDto);
            myProfileData.ResetJson_saveDataValue(json_saveDataValue);
            myProfileData.ResetJson_missionData(json_missionData);
            map.put("myProfileDataDto", profileDataDto);
            map.put("myProfileMissionDataDto", myProfileMissionDataDto);
        }
        return map;
    }

    private void SetArenaRankingReward(User user, ArenaRankingRewardTable arenaRankingRewardTable, String workingPosition) {
        String[] rewardsArray =  arenaRankingRewardTable.getReward().split(",");
        for(int i = 0; i < rewardsArray.length; i++) {
            String rewardInfos = rewardsArray[i];
            String[] rewardInfoArray = rewardInfos.split(":");
            String rewardInfo = rewardInfoArray[0];
            int gettingCount = Integer.parseInt(rewardInfoArray[1]);
            if(rewardInfo.equals("diamond")) {
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousValue = user.getDiamond();
                user.AddDiamond(gettingCount);
                currencyLogDto.setCurrencyLogDto(workingPosition, "다이아", previousValue, gettingCount, user.getDiamond());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(user.getId(), 1, log);
            }
            else if(rewardInfo.equals("arenaCoin")) {
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousValue = user.getArenaCoin();
                user.AddArenaCoin(gettingCount);
                currencyLogDto.setCurrencyLogDto(workingPosition, "아레나 코인", previousValue, gettingCount, user.getArenaCoin());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(user.getId(), 1, log);
            }
            else if(rewardInfo.equals("profileFrame")) {
                //1,2,3 순위에게만 주는 프로필 액자 보상
            }
            else if(rewardInfo.equals("arenaCostume")) {
                //1,2,3 순위에게만 주는 아레나 코스튬
            }
        }
    }

    //반복 보상 요청
    public Map<String, Object> GetRepetitionReward(Long userId, Map<String, Object> map) {
        MyArenaSeasonSaveData myArenaSeasonSaveData = myArenaSeasonSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myArenaSeasonSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyArenaSeasonSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyArenaSeasonSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<ArenaRewardsTable> arenaRewardsTableList = gameDataTableService.ArenaRewardsTableList();
        ArenaRewardsTable arenaRewardsTable = arenaRewardsTableList.stream()
                .filter(a -> a.getArenarewardstable_id() == myArenaSeasonSaveData.getRankingtiertable_id())
                .findAny()
                .orElse(null);
        if(arenaRewardsTableList == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: arenaRewardsTable Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: arenaRewardsTable Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }

        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }

        //5개당 반복보상을 받을수 있음. 5개 미만이면 예외처리.
        if(!myArenaSeasonSaveData.SpendPlayCountPerDay()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_REPETITIONPOINT.getIntegerValue(), "Fail! -> Cause: Need more RepetitionPoint ", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Need more RepetitionPoint ", ResponseErrorCode.NEED_MORE_REPETITIONPOINT);
        }
        String[] rewardsArray = arenaRewardsTable.getPlayCountReward().split(",");
        for(int i = 0; i < rewardsArray.length; i++) {
            String rewardInfos = rewardsArray[i];
            String[] rewardInfoArray = rewardInfos.split(":");
            String rewardInfo = rewardInfoArray[0];
            int gettingCount = Integer.parseInt(rewardInfoArray[1]);
            if(rewardInfo.equals("diamond")) {
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousValue = user.getDiamond();
                user.AddDiamond(gettingCount);
                currencyLogDto.setCurrencyLogDto("아레나 반복 보상", "다이아", previousValue, gettingCount, user.getDiamond());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(user.getId(), 1, log);
            }
            else if(rewardInfo.equals("arenaCoin")) {
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousValue = user.getArenaCoin();
                user.AddArenaCoin(gettingCount);
                currencyLogDto.setCurrencyLogDto("아레나 반복 보상", "아레나 코인", previousValue, gettingCount, user.getArenaCoin());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(user.getId(), 1, log);
            }
        }
        MyArenaSeasonSaveDataDto myArenaSeasonSaveDataDto = new MyArenaSeasonSaveDataDto();
        myArenaSeasonSaveDataDto.InitFromDbData(myArenaSeasonSaveData);
        map.put("myArenaSeasonSaveData", myArenaSeasonSaveDataDto);
        map.put("user", user);
        return map;
    }

}
