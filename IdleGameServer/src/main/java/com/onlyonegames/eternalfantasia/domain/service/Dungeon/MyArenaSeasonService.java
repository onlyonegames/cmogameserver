package com.onlyonegames.eternalfantasia.domain.service.Dungeon;
import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.MyArenaSeasonSaveDataDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.*;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.*;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyArenaSeasonService {
    private final MyArenaSeasonSaveDataRepository myArenaSeasonSaveDataRepository;
    private final GameDataTableService gameDataTableService;
    private final HallofHonorRepository hallofHonorRepository;
    private final ArenaSeasonInfoDataRepository arenaSeasonInfoDataRepository;
    private final UserRepository userRepository;
    private final ArenaSeasonResetDataRepository arenaSeasonResetDataRepository;
    private final ErrorLoggingService errorLoggingService;
    private final MyAreanPlayDataRepository myAreanPlayDataRepository;
    private final MyArenaPlayService myArenaPlayService;
    public Map<String, Object> GetSeasonInfoForLogin(Long userId, Map<String, Object> map) {

        MyArenaSeasonSaveData myArenaSeasonSaveData = myArenaSeasonSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myArenaSeasonSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyArenaSeasonSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyArenaSeasonSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        MyArenaSeasonSaveDataDto myArenaSeasonSaveDataDto = new MyArenaSeasonSaveDataDto();
        myArenaSeasonSaveDataDto.SetDto(myArenaSeasonSaveData);

        map.put("myArenaSeasonSaveData", myArenaSeasonSaveDataDto);

        return map;
    }

    public Map<String, Object> GetSeasonInfo(Long userId, Map<String, Object> map) {

        MyArenaPlayData arenaPlayData = myAreanPlayDataRepository.findByUseridUser(userId);
        if(arenaPlayData != null) {
            LocalDateTime battleStartTime = arenaPlayData.getBattleStartTime();
            LocalDateTime battleEndTime = arenaPlayData.getBattleEndTime();

            if (battleStartTime.isAfter(battleEndTime)) {
                //아레나 전투 시작 시간이 전투 종료 시간 이후라는 것은 기존에 정상 종료 처리 하지 않고 어뷰징 한 계정이라는 뜻.
                //해당 유저 패배 처리.
                Map<String, Object> forFailMap = new HashMap<>();
                myArenaPlayService.ArenaFail(userId, forFailMap);
            }
        }


        MyArenaSeasonSaveData myArenaSeasonSaveData = myArenaSeasonSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myArenaSeasonSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyArenaSeasonSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyArenaSeasonSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        ArenaSeasonResetData arenaSeasonResetData = arenaSeasonResetDataRepository.findById(1).orElse(null);
        if(arenaSeasonResetData.isResetting()) {
            myArenaSeasonSaveData.TurnOnSeasonReady();
        }
        else
            myArenaSeasonSaveData.TurnOffSeasonReady();


//        List<ArenaRewardsTable> arenaRewardsTableList = gameDataTableService.ArenaRewardsTableList();

//        ArenaRewardsTable arenaRewardsTable = arenaRewardsTableList.stream()
//                .filter(a -> a.getArenarewardstable_id() == myArenaSeasonSaveData.getRankingtiertable_id())
//                .findAny()
//                .orElse(null);
//
//        if(arenaRewardsTable != null) {
//            if(myArenaSeasonSaveData.IsResetSeasonStartTime())
//                myArenaSeasonSaveData.ResetSeasonSaveData();
//        }

        /*dailyTime 체크*/
        myArenaSeasonSaveData.DailyTime();
        //최신 시즌 정보 아이디
        long seasonInfosCount = arenaSeasonInfoDataRepository.count();
        int nowSeasonId = (int)seasonInfosCount;
        ArenaSeasonInfoData arenaSeasonInfoData = arenaSeasonInfoDataRepository.findByNowSeasonNo(nowSeasonId)
                .orElse(null);
        if(arenaSeasonInfoData == null) {
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ArenaSeasonInfoData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: ArenaSeasonInfoData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        int previousSeasonNo = myArenaSeasonSaveData.getSeasonNo();
        if(previousSeasonNo != nowSeasonId)
            myArenaSeasonSaveData.ResetSeasonSaveData();

        myArenaSeasonSaveData.ResetSeasonNo(nowSeasonId);
        myArenaSeasonSaveData.ResetSeasonStartTime(arenaSeasonInfoData.getSeasonStartTime());
        myArenaSeasonSaveData.ResetSeasonEndTime(arenaSeasonInfoData.getSeasonEndTime());
        MyArenaSeasonSaveDataDto myArenaSeasonSaveDataDto = new MyArenaSeasonSaveDataDto();
        myArenaSeasonSaveDataDto.SetDto(myArenaSeasonSaveData);

        //클라이언트에서 시즌 정보를 얻어간후 이전 티어와 변경된 티어 싱크를 맞춰준다.
        myArenaSeasonSaveData.ResetPreviousTierId(myArenaSeasonSaveData.getChangedTierId());
        map.put("myArenaSeasonSaveData", myArenaSeasonSaveDataDto);

        //동일한 클라이언트가 1번 이상에 명예의 전당에 등록 되어 있을수 있음.(1시즌,2시즌 1등 클라이언트는 총 2번 등록 되어 있음)
        List<HallofHonor> hallofHonorAllList = hallofHonorRepository.findAll();
        if(hallofHonorAllList != null && hallofHonorAllList.size() > 0) {
            int allListCount = hallofHonorAllList.size();
            int lastIndex = allListCount-1;
            HallofHonor lastHallofHonor = hallofHonorAllList.get(lastIndex);
            if(lastHallofHonor.getHonorUserId().equals(userId)){
                //이미 이전에 명예의 전당 셋팅을 맞췄는지 체크
                if(!lastHallofHonor.isChangedByUser())
                    map.put("hollofHonorSetable", true);
            }
        }


        //무료 아레나티켓 차장 시간 체크
        /*자원 체크*/
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        user.CheckRechargingTimeFreeArenaTicket();

        map.put("user", user);
        return map;
    }
}
