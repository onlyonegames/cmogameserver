package com.onlyonegames.eternalfantasia.domain.service.Dungeon;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.FieldDungeonInfoDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.MyFieldDungeonSeasonDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.PreviousArenaSeasonRankingDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.PreviousFieldDungeonSeasonRankingDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.MailSendRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.FieldDungeonInfoData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.*;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyFieldDungeonSeasonSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.FieldDungeonMonsterWaveInfoTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.FieldDungeonRewardTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.RankingTierTable;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.FieldDungeonInfoDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.Leaderboard.FieldDungeonRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.Leaderboard.FieldDungeonRedisRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.Leaderboard.PreviousFieldDungeonSeasonRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyFieldDungeonPlayLogForBattleRecordRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyFieldDungeonSeasonSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.Leaderboard.FieldDungeonLeaderboardService;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.Leaderboard.LeaderboardService;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.Mail.MyMailBoxService;
import com.onlyonegames.util.StringMaker;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyFieldDungeonSeasonService {
    private final FieldDungeonInfoDataRepository fieldDungeonInfoDataRepository;
    private final UserRepository userRepository;
    private final MyFieldDungeonSeasonSaveDataRepository myFieldDungeonSeasonSaveDataRepository;
    private final PreviousFieldDungeonSeasonRankingRepository previousFieldDungeonSeasonRankingRepository;
    private final FieldDungeonRankingRepository fieldDungeonRankingRepository;
    private final FieldDungeonRedisRankingRepository fieldDungeonRedisRankingRepository;
    private final MyFieldDungeonPlayLogForBattleRecordRepository myFieldDungeonPlayLogForBattleRecordRepository;
    private final FieldDungeonLeaderboardService fieldDungeonLeaderboardService;
    private final RedisTemplate<String, Long> redisLongTemplate;
    private final MyMailBoxService myMailBoxService;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> GetSeasonInfoForField(Long userId, Map<String, Object> map){
        long seasonInfoCount = fieldDungeonInfoDataRepository.count();
        FieldDungeonInfoData fieldDungeonInfoData = fieldDungeonInfoDataRepository.findByNowSeasonNo((int)seasonInfoCount).orElse(null);
        if(fieldDungeonInfoData == null) {
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: FieldDungeonInfoData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: FieldDungeonInfoData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        LocalDateTime now = LocalDateTime.now();
        boolean isSeason = false;
        if(fieldDungeonInfoData.getDungeonStartTime().isBefore(now)&&fieldDungeonInfoData.getDungeonEndTime().isAfter(now)) {
            FieldDungeonInfoDataDto fieldDungeonInfoDataDto = new FieldDungeonInfoDataDto();
            isSeason = true;
            fieldDungeonInfoDataDto.InitDbData(fieldDungeonInfoData);
            map.put("fieldDungeonSeasonInfo", fieldDungeonInfoDataDto);
        }
        MyFieldDungeonSeasonSaveData myFieldDungeonSeasonSaveData = myFieldDungeonSeasonSaveDataRepository.findByUseridUser(userId).orElse(null);
        if(myFieldDungeonSeasonSaveData != null) {
            List<PreviousFieldDungeonSeasonRanking> previousFieldDungeonSeasonRankingList = previousFieldDungeonSeasonRankingRepository.findByUseridUser(userId);
            for(PreviousFieldDungeonSeasonRanking previousFieldDungeonSeasonRanking:previousFieldDungeonSeasonRankingList){
                GetSeasonReward(userId, previousFieldDungeonSeasonRanking, map);
                myFieldDungeonSeasonSaveData.Received();
                previousFieldDungeonSeasonRankingRepository.delete(previousFieldDungeonSeasonRanking);
            }
        }
        map.put("fieldDungeonSeason", isSeason);
        return map;
    }

    public Map<String, Object> GetSeasonInfo(Long userId, Map<String, Object> map){
        long seasonInfoCount = fieldDungeonInfoDataRepository.count();
        FieldDungeonInfoData fieldDungeonInfoData = fieldDungeonInfoDataRepository.findByNowSeasonNo((int)seasonInfoCount).orElse(null);
        if(fieldDungeonInfoData == null) {
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: FieldDungeonInfoData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: FieldDungeonInfoData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyFieldDungeonSeasonSaveData myFieldDungeonSeasonSaveData = myFieldDungeonSeasonSaveDataRepository.findByUseridUser(userId).orElse(null);
        if(myFieldDungeonSeasonSaveData == null){
                MyFieldDungeonSeasonDataDto myFieldDungeonSeasonDataDto = new MyFieldDungeonSeasonDataDto();
                myFieldDungeonSeasonDataDto.setUseridUser(userId);
                myFieldDungeonSeasonDataDto.setSeasonNo(fieldDungeonInfoData.getNowSeasonNo());
                myFieldDungeonSeasonDataDto.setSeasonRank(0L);
                myFieldDungeonSeasonDataDto.setTotalDamage(0L);
                myFieldDungeonSeasonDataDto.setPurchaseNum(0);
                MyFieldDungeonSeasonSaveData newMyFieldDungeonSaveData = myFieldDungeonSeasonDataDto.ToEntity();
                myFieldDungeonSeasonSaveData = myFieldDungeonSeasonSaveDataRepository.save(newMyFieldDungeonSaveData);
        }
        if(user.CheckRechargingTimeFreeFieldDungeonTicket())//TODO 리셋하는 것으로 추가
            myFieldDungeonSeasonSaveData.ResetPurchaseNum();
        int previousSeasonNo = myFieldDungeonSeasonSaveData.getSeasonNo();
        if(previousSeasonNo != seasonInfoCount) {
            myFieldDungeonSeasonSaveData.ResetSeasonSaveData();
            myFieldDungeonSeasonSaveData.ResetSeasonNo((int) seasonInfoCount);
        }
        if(myFieldDungeonSeasonSaveData.getSeasonRank() != 0){
            FieldDungeonRanking fieldDungeonRanking = fieldDungeonRankingRepository.findByUseridUser(userId).orElse(null);
            if(fieldDungeonRanking == null){
                errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: FieldDungeonRanking not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: FieldDungeonRanking not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            myFieldDungeonSeasonSaveData.setSeasonRank(fieldDungeonLeaderboardService.getRank(fieldDungeonRanking));
        }
        FieldDungeonInfoDataDto fieldDungeonInfoDataDto = new FieldDungeonInfoDataDto();
        fieldDungeonInfoDataDto.InitDbData(fieldDungeonInfoData);
        MyFieldDungeonSeasonDataDto myFieldDungeonSeasonDataDto = new MyFieldDungeonSeasonDataDto();
        myFieldDungeonSeasonDataDto.InitFromDbData(myFieldDungeonSeasonSaveData);
        map.put("myFieldDungeonSeasonData", myFieldDungeonSeasonDataDto);
        map.put("user", user);
        return map;
    }

    public Map<String, Object> GetSeasonReward(Long userId, PreviousFieldDungeonSeasonRanking previousFieldDungeonSeasonRanking, Map<String, Object> map) { //TODO 필드던전 보상

        List<FieldDungeonRewardTable> fieldDungeonRewardTableList = gameDataTableService.FieldDungeonRewardTableList();
        String rewardItem = "";
        if(previousFieldDungeonSeasonRanking.getRanking() == 1)
            rewardItem = fieldDungeonRewardTableList.get(0).getGettingItem();
        else if(previousFieldDungeonSeasonRanking.getRanking() == 2)
            rewardItem = fieldDungeonRewardTableList.get(1).getGettingItem();
        else if(previousFieldDungeonSeasonRanking.getRanking() == 3)
            rewardItem = fieldDungeonRewardTableList.get(2).getGettingItem();
        else if(previousFieldDungeonSeasonRanking.getRanking() >= 4 && previousFieldDungeonSeasonRanking.getRanking() <= 10)
            rewardItem = fieldDungeonRewardTableList.get(3).getGettingItem();
        else if(previousFieldDungeonSeasonRanking.getRanking() >= 11 && previousFieldDungeonSeasonRanking.getRanking() <= 100)
            rewardItem = fieldDungeonRewardTableList.get(4).getGettingItem();
        else if(previousFieldDungeonSeasonRanking.getRanking() >= 101)
            rewardItem = fieldDungeonRewardTableList.get(5).getGettingItem();

        String[] gettingItems = rewardItem.split(",");
        MailSendRequestDto mailSendRequestDto = new MailSendRequestDto();
        List<MailSendRequestDto.Item> rankingRewardItemList = new ArrayList<>();
        for(String gettingItem: gettingItems){
            String[] temp = gettingItem.split(":");
            MailSendRequestDto.Item item = new MailSendRequestDto.Item();
            item.setItem(temp[0], Integer.parseInt(temp[1]));
            rankingRewardItemList.add(item);
        }
        StringMaker.Clear();
        StringMaker.stringBuilder.append("어둠의 균열 ");
        StringMaker.stringBuilder.append(previousFieldDungeonSeasonRanking.getRanking());
        StringMaker.stringBuilder.append("위 보상 획득");
        String title = StringMaker.stringBuilder.toString();
        LocalDateTime now = LocalDateTime.now();
        mailSendRequestDto.SetMailSendRequestDto(title, 10000L, userId, "어둠의 균열 보상이 지급되었습니다.", rankingRewardItemList, 1, now.plusYears(1));
        Map<String, Object> fakeMap = new HashMap<>();
        myMailBoxService.SendMail(mailSendRequestDto, fakeMap);
        return map;
    }

    public Map<String, Object> SeasonSetting(Map<String, Object> map) {
        long seasonInfoCount = fieldDungeonInfoDataRepository.count();
        FieldDungeonInfoData fieldDungeonInfoData = fieldDungeonInfoDataRepository.findByNowSeasonNo((int)seasonInfoCount).orElse(null);
        if(fieldDungeonInfoData == null) {
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: FieldDungeonInfoData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: FieldDungeonInfoData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        LocalDateTime now = LocalDateTime.now();
        if(fieldDungeonInfoData.getDungeonEndTime().isAfter(now)){
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.UNDEFINED.getIntegerValue(), "Fail! -> Cause: FieldDungeon Season was not end.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            map.put("Success", false);
            return map;
        }
        if(!fieldDungeonInfoData.isSeasonEnd()){
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.UNDEFINED.getIntegerValue(), "Fail! -> Cause: FieldDungeon Season was ended But Season end Process was not worked.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            map.put("Success", false);
            return map;
        }
        int nowSeasonNo = fieldDungeonInfoData.getNowSeasonNo();
        int newSeasonNo = nowSeasonNo + 1;
        List<FieldDungeonMonsterWaveInfoTable> fieldDungeonMonsterWaveInfoTableList = gameDataTableService.FieldDungeonMonsterWaveInfoTableList();
        FieldDungeonMonsterWaveInfoTable previousBoss = fieldDungeonMonsterWaveInfoTableList.stream().filter(i -> i.getCode().equals(fieldDungeonInfoData.getBossCode())).findAny().orElse(null);
        if(previousBoss == null){
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: FieldDungeonMonsterWaveInfoTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: FieldDungeonMonsterWaveInfoTable not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        int bossIndex = previousBoss.getId();
        if(fieldDungeonMonsterWaveInfoTableList.size()<=bossIndex)
            bossIndex = 0;
        String bossCode = fieldDungeonMonsterWaveInfoTableList.get(bossIndex).getCode();
        FieldDungeonInfoDataDto newFieldDungeonInfoDataDto = new FieldDungeonInfoDataDto();
        newFieldDungeonInfoDataDto.setNowSeasonNo(newSeasonNo);
        newFieldDungeonInfoDataDto.setBossCode(bossCode);
        newFieldDungeonInfoDataDto.setDungeonStartTime(LocalDateTime.of(now.toLocalDate(), LocalTime.of(12,0,0)));
        newFieldDungeonInfoDataDto.setDungeonEndTime(LocalDateTime.of(now.plusDays(3).toLocalDate(), LocalTime.of(23,59,59)));
        FieldDungeonInfoData newFieldDungeonInfoData = newFieldDungeonInfoDataDto.ToEntity();
        fieldDungeonInfoDataRepository.save(newFieldDungeonInfoData);



        map.put("newFieldDungeonInfoData", newFieldDungeonInfoData);
        map.put("Success", true);
        return map;
    }

    public Map<String, Object> EndSeason(Map<String, Object> map) {
        long seasonInfoCount = fieldDungeonInfoDataRepository.count();
        FieldDungeonInfoData fieldDungeonInfoData = fieldDungeonInfoDataRepository.findByNowSeasonNo((int)seasonInfoCount).orElse(null);
        if(fieldDungeonInfoData == null) {
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: FieldDungeonInfoData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: FieldDungeonInfoData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        int nowSeasonNo = fieldDungeonInfoData.getNowSeasonNo();
        LocalDateTime now = LocalDateTime.now();
        if(fieldDungeonInfoData.getDungeonEndTime().isAfter(now) || fieldDungeonInfoData.isSeasonEnd()){
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.UNDEFINED.getIntegerValue(), "Fail! -> Cause: FieldDungeon Season was not end.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            map.put("Success", false);
            return map;
        }
        if(fieldDungeonInfoData.isSeasonEnd()){
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.UNDEFINED.getIntegerValue(), "Fail! -> Cause: FieldDungeon Season end Process was already worked.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            map.put("Success", false);
            return map;
        }
        fieldDungeonInfoData.SeasonEnd();
        List<FieldDungeonRanking> thisSeasonRdsScoreList =  fieldDungeonRankingRepository.findAll();
        thisSeasonRdsScoreList.sort((a,b) -> b.getTotalDamage().compareTo(a.getTotalDamage()));

        //previousFieldDungeonSeasonRankingRepository.deleteAll();
        redisLongTemplate.opsForZSet().getOperations().delete(FieldDungeonLeaderboardService.FIELDDUNGEON_RANKING_LEADERBOARD);
        Iterable<FieldDungeonRedisRanking> redisScoreList = fieldDungeonRedisRankingRepository.findAll();

        fieldDungeonRedisRankingRepository.deleteAll(redisScoreList);
        long ranking = 1;
        long tempRanking = 0;
        Long tempDamage = 0L;
        for(FieldDungeonRanking fieldDungeonRanking : thisSeasonRdsScoreList) {
            //직전 시즌 랭킹 정보 셋팅
            //현재 시즌에 참여했던 인원들만 백업 테이블에 기록한다.
            int previousSeasonInfoId = fieldDungeonRanking.getNowSeasonNo();
            if(previousSeasonInfoId == nowSeasonNo) {
                if(!tempDamage.equals(fieldDungeonRanking.getTotalDamage())){
                    tempDamage = fieldDungeonRanking.getTotalDamage();
                    tempRanking = ranking;
                }
                PreviousFieldDungeonSeasonRankingDto previousFieldDungeonSeasonRankingDto = new PreviousFieldDungeonSeasonRankingDto(0L, fieldDungeonRanking.getUseridUser(), previousSeasonInfoId, tempDamage, tempRanking);
                PreviousFieldDungeonSeasonRanking previousFieldDungeonSeasonRanking = previousFieldDungeonSeasonRankingDto.ToEntity();
                previousFieldDungeonSeasonRanking = previousFieldDungeonSeasonRankingRepository.save(previousFieldDungeonSeasonRanking);
                ranking++;
            }
        }
        fieldDungeonRankingRepository.deleteAll();
        myFieldDungeonPlayLogForBattleRecordRepository.deleteAll();
        map.put("Success", true);
        return map;
    }

    public Map<String, Object> ManagementToolSeasonSetting(Map<String, Object> map) {
        long seasonInfoCount = fieldDungeonInfoDataRepository.count();
        FieldDungeonInfoData fieldDungeonInfoData = fieldDungeonInfoDataRepository.findByNowSeasonNo((int)seasonInfoCount).orElse(null);
        if(fieldDungeonInfoData == null) {
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: FieldDungeonInfoData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: FieldDungeonInfoData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        int nowSeasonNo = fieldDungeonInfoData.getNowSeasonNo();
        int newSeasonNo = nowSeasonNo + 1;
        List<FieldDungeonMonsterWaveInfoTable> fieldDungeonMonsterWaveInfoTableList = gameDataTableService.FieldDungeonMonsterWaveInfoTableList();
        FieldDungeonMonsterWaveInfoTable previousBoss = fieldDungeonMonsterWaveInfoTableList.stream().filter(i -> i.getCode().equals(fieldDungeonInfoData.getBossCode())).findAny().orElse(null);
        if(previousBoss == null){
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: FieldDungeonMonsterWaveInfoTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: FieldDungeonMonsterWaveInfoTable not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        LocalDateTime now = LocalDateTime.now();
        int bossIndex = previousBoss.getId();
        if(fieldDungeonMonsterWaveInfoTableList.size()<=bossIndex)
            bossIndex = 0;
        String bossCode = fieldDungeonMonsterWaveInfoTableList.get(bossIndex).getCode();
        FieldDungeonInfoDataDto newFieldDungeonInfoDataDto = new FieldDungeonInfoDataDto();
        newFieldDungeonInfoDataDto.setNowSeasonNo(newSeasonNo);
        newFieldDungeonInfoDataDto.setBossCode(bossCode);
        newFieldDungeonInfoDataDto.setDungeonStartTime(now);
        newFieldDungeonInfoDataDto.setDungeonEndTime(LocalDateTime.of(now.plusDays(3).toLocalDate(), LocalTime.of(23,59,59)));
        FieldDungeonInfoData newFieldDungeonInfoData = newFieldDungeonInfoDataDto.ToEntity();
        fieldDungeonInfoDataRepository.save(newFieldDungeonInfoData);



        map.put("newFieldDungeonInfoData", newFieldDungeonInfoData);
        return map;
    }

    public Map<String, Object> ManagementToolEndSeason(Map<String, Object> map) {
        long seasonInfoCount = fieldDungeonInfoDataRepository.count();
        FieldDungeonInfoData fieldDungeonInfoData = fieldDungeonInfoDataRepository.findByNowSeasonNo((int)seasonInfoCount).orElse(null);
        if(fieldDungeonInfoData == null) {
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: FieldDungeonInfoData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: FieldDungeonInfoData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        int nowSeasonNo = fieldDungeonInfoData.getNowSeasonNo();
        LocalDateTime now = LocalDateTime.now();
        if(fieldDungeonInfoData.getDungeonEndTime().isAfter(now))
            fieldDungeonInfoData.SetEndTime(now);
        if(fieldDungeonInfoData.isSeasonEnd()){
            map.put("Success", false);
            return map;
        }
        fieldDungeonInfoData.SeasonEnd();
        List<FieldDungeonRanking> thisSeasonRdsScoreList =  fieldDungeonRankingRepository.findAll();
        thisSeasonRdsScoreList.sort((a,b) -> b.getTotalDamage().compareTo(a.getTotalDamage()));

        //previousFieldDungeonSeasonRankingRepository.deleteAll();
        redisLongTemplate.opsForZSet().getOperations().delete(FieldDungeonLeaderboardService.FIELDDUNGEON_RANKING_LEADERBOARD);
        Iterable<FieldDungeonRedisRanking> redisScoreList = fieldDungeonRedisRankingRepository.findAll();

        fieldDungeonRedisRankingRepository.deleteAll(redisScoreList);
        long ranking = 1;
        long tempRanking = 0;
        Long tempDamage = 0L;
        for(FieldDungeonRanking fieldDungeonRanking : thisSeasonRdsScoreList) {
            //직전 시즌 랭킹 정보 셋팅
            //현재 시즌에 참여했던 인원들만 백업 테이블에 기록한다.
            int previousSeasonInfoId = fieldDungeonRanking.getNowSeasonNo();
            if(previousSeasonInfoId == nowSeasonNo) {
                if(!tempDamage.equals(fieldDungeonRanking.getTotalDamage())){
                    tempDamage = fieldDungeonRanking.getTotalDamage();
                    tempRanking = ranking;
                }
                PreviousFieldDungeonSeasonRankingDto previousFieldDungeonSeasonRankingDto = new PreviousFieldDungeonSeasonRankingDto(0L, fieldDungeonRanking.getUseridUser(), previousSeasonInfoId, tempDamage, tempRanking);
                PreviousFieldDungeonSeasonRanking previousFieldDungeonSeasonRanking = previousFieldDungeonSeasonRankingDto.ToEntity();
                previousFieldDungeonSeasonRanking = previousFieldDungeonSeasonRankingRepository.save(previousFieldDungeonSeasonRanking);
                ranking++;
            }
        }
        fieldDungeonRankingRepository.deleteAll();
        myFieldDungeonPlayLogForBattleRecordRepository.deleteAll();
        map.put("Success", true);
        return map;
    }
}
