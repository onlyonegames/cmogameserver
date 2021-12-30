package com.onlyonegames.eternalfantasia.domain.service.Inventory;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Inventory.MyBelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.BelongingInventoryJsonData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ArenaRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ArenaRedisRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyBelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.*;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyBelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyBelongingInventoryService {
    private final MyBelongingInventoryRepository myBelongingInventoryRepository;
    private final UserRepository userRepository;
    private final ErrorLoggingService errorLoggingService;
    private final ArenaRankingRepository arenaRankingRepository;
    private final ArenaRedisRankingRepository arenaRedisRankingRepository;
    private final PreviousArenaRankingRepository previousArenaRankingRepository;
    private final BattlePowerRankingRepository battlePowerRankingRepository;
    private final BattlePowerRedisRankingRepository battlePowerRedisRankingRepository;
    private final PreviousBattlePowerRankingRepository previousBattlePowerRankingRepository;
    private final StageRankingRepository stageRankingRepository;
    private final StageRedisRankingRepository stageRedisRankingRepository;
    private final PreviousStageRankingRepository previousStageRankingRepository;
    private final WorldBossRankingRepository worldBossRankingRepository;
    private final WorldBossRedisRankingRepository worldBossRedisRankingRepository;
    private final PreviousWorldBossRankingRepository previousWorldBossRankingRepository;

    public Map<String, Object> SpendItem(Long userId, String code, int count, Map<String, Object> map) {
        String[] codeSplit = code.split("_");
        int codeNo = Integer.parseInt(codeSplit[codeSplit.length-1]);
        if(codeNo<8) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyActiveSkillData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyActiveSkillData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyBelongingInventory myBelongingInventory = myBelongingInventoryRepository.findByUseridUserAndCode(userId, code).orElse(null);
        if(myBelongingInventory == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyBelongingInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyBelongingInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        if(!myBelongingInventory.SpendItem(count)){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyActiveSkillData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyActiveSkillData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyBelongingInventoryDto myBelongingInventoryDto = new MyBelongingInventoryDto();
        myBelongingInventoryDto.InitFromDBData(myBelongingInventory);
        map.put("myBelongingInventory", myBelongingInventoryDto);
        return map;
    }

    public Map<String, Object> ChangeGameName(Long userId, String gameName, Map<String, Object> map) {

        User findByUserGameName = userRepository.findByuserGameName(gameName).orElse(null);
        if(findByUserGameName != null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_EXIST_USERNAME.getIntegerValue(), "Fail! -> Cause: ALREADY_EXIST_USERNAME.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: ALREADY_EXIST_USERNAME.", ResponseErrorCode.ALREADY_EXIST_USERNAME);
        }
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: User not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: User not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        if(user.isNew_user())
            user.SetNew_User();
        else {
            MyBelongingInventory myBelongingInventory = myBelongingInventoryRepository.findByUseridUserAndCode(userId, "item_009").orElse(null);
            if(myBelongingInventory == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyBelongingInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: MyBelongingInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            myBelongingInventory.SpendItem(1);
            map.put("count", myBelongingInventory.getCount());
        }
        user.SetUserName(gameName);

        arenaRankingRepository.findByUseridUser(userId).ifPresent(arenaRanking -> arenaRanking.ResetUserGameName(gameName));
        arenaRedisRankingRepository.findById(userId).ifPresent(arenaRedisRanking -> arenaRedisRanking.ResetUserGameName(gameName));
        previousArenaRankingRepository.findByUseridUser(userId).ifPresent(previousArenaRanking -> previousArenaRanking.ResetUserGameName(gameName));
        battlePowerRankingRepository.findByUseridUser(userId).ifPresent(battlePowerRanking -> battlePowerRanking.ResetUserGameName(gameName));
        battlePowerRedisRankingRepository.findById(userId).ifPresent(battlePowerRedisRanking -> battlePowerRedisRanking.ResetUserGameName(gameName));
        previousBattlePowerRankingRepository.findByUseridUser(userId).ifPresent(previousBattlePowerRanking -> previousBattlePowerRanking.ResetUserGameName(gameName));
        stageRankingRepository.findByUseridUser(userId).ifPresent(stageRanking -> stageRanking.ResetUserGameName(gameName));
        stageRedisRankingRepository.findById(userId).ifPresent(stageRedisRanking -> stageRedisRanking.ResetUserGameName(gameName));
        previousStageRankingRepository.findByUseridUser(userId).ifPresent(previousStageRanking -> previousStageRanking.ResetUserGameName(gameName));
        worldBossRankingRepository.findByUseridUser(userId).ifPresent(worldBossRanking -> worldBossRanking.ResetUserGameName(gameName));
        worldBossRedisRankingRepository.findById(userId).ifPresent(worldBossRedisRanking -> worldBossRedisRanking.ResetUserGameName(gameName));
        previousWorldBossRankingRepository.findByUseridUser(userId).ifPresent(previousWorldBossRanking -> previousWorldBossRanking.ResetUserGameName(gameName));

        map.put("userGameName", user.getUserGameName());
        return map;
    }

//    public Map<String, Object> ChangeSex(Long userId, int sexType, Map<String, Object> map) {
//
//    }
}
