package com.onlyonegames.eternalfantasia.domain.service.Dungeon;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.MyInfiniteTowerRewardReceivedInfosDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.MyInfiniteTowerSaveDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CurrencyLogDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.InfiniteTowerRecords;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyInfiniteTowerSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.InfiniteTowerRewardsTable;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.InfinityTowerRecordsRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyInfiniteTowerSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyInfiniteTowerSaveDataService {
    private final UserRepository userRepository;
    private final MyInfiniteTowerSaveDataRepository myInfiniteTowerSaveDataRepository;
    private final InfinityTowerRecordsRepository infinityTowerRecordsRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;
    public Map<String, Object> GetMyInfiniteTowerSaveData(Long userId, Map<String, Object> map) {
        //천공의 계단 정보
        MyInfiniteTowerSaveData myInfiniteTowerSaveData = myInfiniteTowerSaveDataRepository.findByUseridUser(userId).orElse(null);
        if(myInfiniteTowerSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myInfiniteTowerSaveData not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myInfiniteTowerSaveData not find", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyInfiniteTowerSaveDataDto myInfiniteTowerSaveDataDto = new MyInfiniteTowerSaveDataDto();
        myInfiniteTowerSaveDataDto.setId(myInfiniteTowerSaveData.getId());
        myInfiniteTowerSaveDataDto.setUseridUser(myInfiniteTowerSaveData.getUseridUser());
        myInfiniteTowerSaveDataDto.setArrivedTopFloor(myInfiniteTowerSaveData.getArrivedTopFloor());
        MyInfiniteTowerRewardReceivedInfosDto myInfiniteTowerRewardReceivedInfosDto = JsonStringHerlper.ReadValueFromJson(myInfiniteTowerSaveData.getReceivedRewardInfoJson(), MyInfiniteTowerRewardReceivedInfosDto.class);
        myInfiniteTowerSaveDataDto.setMyInfiniteTowerRewardReceivedInfosDto(myInfiniteTowerRewardReceivedInfosDto);
        map.put("myInfiniteTowerSaveData",myInfiniteTowerSaveDataDto);

        return map;
    }

    public Map<String, Object> ReceiveInfiniteTowerReward(Long userId, int floor, Map<String, Object> map) {

        //실제 해당층을 깬 기록이 있는지 확인.
        List<InfiniteTowerRecords> infiniteTowerRecordsList = infinityTowerRecordsRepository.findAll();
        InfiniteTowerRecords infiniteTowerRecord = infiniteTowerRecordsList.stream()
                .filter(a -> a.getFloor() == floor)
                .findAny()
                .orElse(null);
        if(infiniteTowerRecord == null || infiniteTowerRecord.getUseridUser().equals(0L)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: infiniteTowerRecord not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: infiniteTowerRecord not find", ResponseErrorCode.NOT_FIND_DATA);
        }

        //해당 유저의 천공의 계단 저장 데이터에서 해당 층에 대한 보상을 이미 받았는지 체크.
        MyInfiniteTowerSaveData myInfiniteTowerSaveData = myInfiniteTowerSaveDataRepository.findByUseridUser(userId).orElse(null);
        if(myInfiniteTowerSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myInfiniteTowerSaveData not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myInfiniteTowerSaveData not find", ResponseErrorCode.NOT_FIND_DATA);
        }

        String json_ReceivedRewardInfo = myInfiniteTowerSaveData.getReceivedRewardInfoJson();
        MyInfiniteTowerRewardReceivedInfosDto myInfiniteTowerRewardReceivedInfosDto = JsonStringHerlper.ReadValueFromJson(json_ReceivedRewardInfo, MyInfiniteTowerRewardReceivedInfosDto.class);
        MyInfiniteTowerRewardReceivedInfosDto.InfiniteTowerRewardReceivedInfo infiniteTowerRewardReceivedInfo = myInfiniteTowerRewardReceivedInfosDto.infiniteTowerRewardReceivedInfos.stream()
                .filter(a -> a.floor == floor)
                .findAny()
                .orElse(null);
        if(infiniteTowerRewardReceivedInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: infiniteTowerRewardReceivedInfo not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: infiniteTowerRewardReceivedInfo not find", ResponseErrorCode.NOT_FIND_DATA);
        }
        //이미 해당 보상을 받음.
        if(infiniteTowerRewardReceivedInfo.received) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_RECEIVED_INFINITETOWERREWARD.getIntegerValue(), "Fail! -> Cause: already InfiniteTowerRewardReceived", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: already InfiniteTowerRewardReceived", ResponseErrorCode.ALREADY_RECEIVED_INFINITETOWERREWARD);
        }
        infiniteTowerRewardReceivedInfo.received = true;

        //해당 선물 주기
        List<InfiniteTowerRewardsTable> infiniteTowerRewardsTableList = gameDataTableService.InfiniteTowerRewardsTableList();
        InfiniteTowerRewardsTable infiniteTowerRewardsTable = infiniteTowerRewardsTableList.stream()
                .filter(a -> a.getFloor() == floor)
                .findAny()
                .orElse(null);
        if(infiniteTowerRewardsTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: infiniteRewardsTableList not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: infiniteRewardsTableList not find", ResponseErrorCode.NOT_FIND_DATA);
        }

        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }

        String[] rewardsArray = infiniteTowerRewardsTable.getReward().split(",");
        for(int i = 0; i < rewardsArray.length; i++) {
            String rewardInfos = rewardsArray[i];
            String[] rewardInfoArray = rewardInfos.split(":");
            String rewardInfo = rewardInfoArray[0];
            int gettingCount = Integer.parseInt(rewardInfoArray[1]);
            if(rewardInfo.equals("diamond")) {
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousValue = user.getDiamond();
                user.AddDiamond(gettingCount);
                currencyLogDto.setCurrencyLogDto("던전보상 - 무한의 탑 "+floor+"층", "다이아", previousValue, gettingCount, user.getDiamond());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
            }
            else if(rewardInfo.equals("arenaCoin")) {
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousValue = user.getArenaCoin();
                user.AddArenaCoin(gettingCount);
                currencyLogDto.setCurrencyLogDto("던전보상 - 무한의 탑 "+floor+"층", "아레나 코인", previousValue, gettingCount, user.getArenaCoin());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
            }
        }
        json_ReceivedRewardInfo = JsonStringHerlper.WriteValueAsStringFromData(myInfiniteTowerRewardReceivedInfosDto);
        myInfiniteTowerSaveData.ResetReceivedRewardInfoJson(json_ReceivedRewardInfo);
        MyInfiniteTowerSaveDataDto myInfiniteTowerSaveDataDto = new MyInfiniteTowerSaveDataDto();
        myInfiniteTowerSaveDataDto.setId(myInfiniteTowerSaveData.getId());
        myInfiniteTowerSaveDataDto.setUseridUser(myInfiniteTowerSaveData.getUseridUser());
        myInfiniteTowerSaveDataDto.setArrivedTopFloor(myInfiniteTowerSaveData.getArrivedTopFloor());
        myInfiniteTowerSaveDataDto.setMyInfiniteTowerRewardReceivedInfosDto(myInfiniteTowerRewardReceivedInfosDto);
        map.put("myInfiniteTowerSaveData",myInfiniteTowerSaveDataDto);
        map.put("user", user);
        return map;
    }
}
