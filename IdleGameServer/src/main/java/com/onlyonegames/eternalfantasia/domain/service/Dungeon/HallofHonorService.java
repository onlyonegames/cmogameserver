package com.onlyonegames.eternalfantasia.domain.service.Dungeon;
import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.MyArenaSeasonSaveDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CurrencyLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.HallofHonorDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.HallofHonor;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyArenaSeasonSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.ArenaRewardsTable;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.HallofHonorRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyArenaSeasonSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class HallofHonorService {
    private final UserRepository userRepository;
    private final MyArenaSeasonSaveDataRepository myArenaSeasonSaveDataRepository;
    private final HallofHonorRepository hallofHonorRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;
    /**명예의 전당 정보 리스트 불러오기*/
    public Map<String, Object> GetList(Map<String, Object> map) {
        //동일한 클라이언트가 1번 이상에 명예의 전당에 등록 되어 있을수 있음.(1시즌,2시즌 1등 클라이언트는 총 2번 등록 되어 있음)
        List<HallofHonor> hallofHonorAllList = hallofHonorRepository.findAll();
        List<Long> hallofHonorUserIdList = new ArrayList<>();
        List<HallofHonorDto> hallofHonorDtoList = new ArrayList<>();
        for(HallofHonor hallofHonor : hallofHonorAllList) {
            hallofHonorUserIdList.add(hallofHonor.getHonorUserId());
        }
        List<User> hallofHonorUserList = userRepository.findAllById(hallofHonorUserIdList);
        for(HallofHonor hallofHonor : hallofHonorAllList) {
            HallofHonorDto hallofHonorDto = new HallofHonorDto();
            User temp = hallofHonorUserList.stream().filter(i -> i.getId().equals(hallofHonor.getHonorUserId()))
                    .findAny()
                    .orElse(null);
            if(temp == null) {
                errorLoggingService.SetErrorLog(hallofHonor.getHonorUserId(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
            }
            hallofHonorDto.setHallofHonorDto(hallofHonor, temp.getUserGameName());
            hallofHonorDtoList.add(hallofHonorDto);
        }

        map.put("hallofHonorAllList", hallofHonorDtoList);
        return map;
    }

    /**명예의 전당 셋팅*/
    public Map<String, Object> SettingMyHall(Long userId, String selectedCharacterCode, int selectedPose,
                                             String selectedCostumeCode, String selectedEquipmentArmorCode, String selectedEquipmentHelmetCode, String selectedEquipmentAccessoryCode, Map<String, Object> map) {
        //동일한 클라이언트가 1번 이상에 명예의 전당에 등록 되어 있을수 있음.(1시즌,2시즌 1등 클라이언트는 총 2번 등록 되어 있음)
        List<HallofHonor> hallofHonorAllList = hallofHonorRepository.findAll();
        int allListCount = hallofHonorAllList.size();
        HallofHonor lastHallofHonor = hallofHonorAllList.get(allListCount-1);
        if(!lastHallofHonor.getHonorUserId().equals(userId)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.DONT_SET_MYHALLOFHONOR.getIntegerValue(), "Fail! -> Cause: Dont set myHallOfHonor.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Dont set myHallOfHonor.", ResponseErrorCode.DONT_SET_MYHALLOFHONOR);
        }

        //이미 이전에 명예의 전당 셋팅을 맞췄는지 체크
        if(lastHallofHonor.isChangedByUser()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.AREADY_SETTING_HALLOFHONOR.getIntegerValue(), "Fail! -> Cause: Aready setting hallofhonor.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Aready setting hallofhonor.", ResponseErrorCode.AREADY_SETTING_HALLOFHONOR);
        }
        //명예의 전당 셋팅
        lastHallofHonor.ChangeHallofHonorByClient(selectedCharacterCode, selectedPose, selectedCostumeCode, selectedEquipmentArmorCode, selectedEquipmentHelmetCode, selectedEquipmentAccessoryCode);
        List<HallofHonorDto> hallofHonorDtoList = new ArrayList<>();
        for(HallofHonor temp : hallofHonorAllList){
            HallofHonorDto hallofHonorDto = new HallofHonorDto();
            hallofHonorDto.InitFromDbData(temp);
            hallofHonorDtoList.add(hallofHonorDto);
        }
        map.put("hallofHonorAllList", hallofHonorDtoList);
        return map;
    }

    /**명예의 전당 보상 받기 API*/
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

        if(!myArenaSeasonSaveData.isReceiveableHallofHonorReward()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_RECEIVED_HALLOFHONORREWARD.getIntegerValue(), "Fail! -> Cause: Already received hallofhonor_reward", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Already received hallofhonor_reward", ResponseErrorCode.ALREADY_RECEIVED_HALLOFHONORREWARD);
        }
        //명예의 전당 보상은 모든 유저가 동일하게 받게 되므로 테이블 20번째 아이디를 사용한다.
        List<ArenaRewardsTable> arenaRewardsTableList = gameDataTableService.ArenaRewardsTableList();
        ArenaRewardsTable arenaRewardsTable = arenaRewardsTableList.stream()
                .filter(a -> a.getArenarewardstable_id() == 20)
                .findAny()
                .orElse(null);
        if(arenaRewardsTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: arenaRewardsTable Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: arenaRewardsTable Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }

        String[] rewardsArray = arenaRewardsTable.getHallofHonorReward().split(",");
        for(int i = 0; i < rewardsArray.length; i++) {
            String rewardInfos = rewardsArray[i];
            String[] rewardInfoArray = rewardInfos.split(":");
            String rewardInfo = rewardInfoArray[0];
            int gettingCount = Integer.parseInt(rewardInfoArray[1]);
            if(rewardInfo.equals("diamond")) {
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousVale = user.getDiamond();
                user.AddDiamond(gettingCount);
                currencyLogDto.setCurrencyLogDto("던전보상 - 명예의 전당", "다이아", previousVale, gettingCount, user.getDiamond());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
            }
            else if(rewardInfo.equals("arenaCoin")) {
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousVale = user.getArenaCoin();
                user.AddArenaCoin(gettingCount);
                currencyLogDto.setCurrencyLogDto("던전보상 - 명예의 전당", "아레나 코인", previousVale, gettingCount, user.getArenaCoin());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
            }
        }


        myArenaSeasonSaveData.ReceiveHallofHonorReward();
        MyArenaSeasonSaveDataDto myArenaSeasonSaveDataDto = new MyArenaSeasonSaveDataDto();
        myArenaSeasonSaveDataDto.InitFromDbData(myArenaSeasonSaveData);
        map.put("myArenaSeasonSaveData", myArenaSeasonSaveDataDto);
        map.put("user", user);
        return map;
    }
}
