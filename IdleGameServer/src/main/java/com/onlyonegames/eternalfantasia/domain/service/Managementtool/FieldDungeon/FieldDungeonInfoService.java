package com.onlyonegames.eternalfantasia.domain.service.Managementtool.FieldDungeon;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.FieldDungeonInfoDataDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.FieldDungeonInfoData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.PreviousFieldDungeonSeasonRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyFieldDungeonSeasonSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.FieldDungeonInfoDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyFieldDungeonSeasonSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class FieldDungeonInfoService {
    private final FieldDungeonInfoDataRepository fieldDungeonInfoDataRepository;
    private final UserRepository userRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> GetFieldDungeonInfo(Map<String, Object> map) {
        long seasonCount = fieldDungeonInfoDataRepository.count();
        FieldDungeonInfoData fieldDungeonInfoData = fieldDungeonInfoDataRepository.findByNowSeasonNo((int)seasonCount).orElse(null);
        if(fieldDungeonInfoData == null) {
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: FieldDungeonInfoData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
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
        map.put("fieldDungeonSeason", isSeason);
        return map;
    }

    public Map<String, Object> SetDungeonEndTime(LocalDateTime time, Map<String, Object> map) {
        long seasonCount = fieldDungeonInfoDataRepository.count();
        FieldDungeonInfoData fieldDungeonInfoData = fieldDungeonInfoDataRepository.findByNowSeasonNo((int)seasonCount).orElse(null);
        if(fieldDungeonInfoData == null) {
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: FieldDungeonInfoData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: FieldDungeonInfoData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        fieldDungeonInfoData.SetEndTime(time);
        FieldDungeonInfoDataDto fieldDungeonInfoDataDto = new FieldDungeonInfoDataDto();
        fieldDungeonInfoDataDto.InitDbData(fieldDungeonInfoData);
        map.put("fieldDungeonSeasonInfo", fieldDungeonInfoDataDto);
        return map;
    }

    public Map<String, Object> AddPlayableCount(Long userId, Map<String, Object> map) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: User not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: User not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        user.AddFieldDungeonOnePlayable();
        map.put("user", user);
        return map;
    }
}
