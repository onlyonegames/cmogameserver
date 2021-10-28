package com.onlyonegames.eternalfantasia.domain.service.Test;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.ActiveSkillDataJsonDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.PassiveSkillDataJsonDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyActiveSkillData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyPassiveSkillData;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.MyActiveSkillDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyPassiveSkillDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@AllArgsConstructor
public class UserCheckService {
    private final MyActiveSkillDataRepository myActiveSkillDataRepository;
    private final MyPassiveSkillDataRepository myPassiveSkillDataRepository;
    private final UserRepository userRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> UserSkillPointCheck(Long userId, Map<String, Object> map) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: User not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: User not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        int baseSkillPoint = (user.getLevel() * 2) -2;
        baseSkillPoint -= user.getSkillPoint();
        MyActiveSkillData myActiveSkillData = myActiveSkillDataRepository.findByUseridUser(userId).orElse(null);
        if (myActiveSkillData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyActiveSkillData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyActiveSkillData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyPassiveSkillData myPassiveSkillData = myPassiveSkillDataRepository.findByUseridUser(userId).orElse(null);
        if (myPassiveSkillData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyPassiveSkillData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyPassiveSkillData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        ActiveSkillDataJsonDto activeSkillDataJsonDto = JsonStringHerlper.ReadValueFromJson(myActiveSkillData.getJson_saveDataValue(), ActiveSkillDataJsonDto.class);
        PassiveSkillDataJsonDto passiveSkillDataJsonDto = JsonStringHerlper.ReadValueFromJson(myPassiveSkillData.getJson_saveDataValue(), PassiveSkillDataJsonDto.class);

        for (ActiveSkillDataJsonDto.SkillInfo skillInfo : activeSkillDataJsonDto.skillInfoList) {
            baseSkillPoint -= (skillInfo.level-1)*3;
        }
        for (PassiveSkillDataJsonDto.PassiveSkillInfo passiveSkillInfo : passiveSkillDataJsonDto.passiveSkillInfoList) {
            baseSkillPoint -= passiveSkillInfo.level-1;
        }

        if (baseSkillPoint != 0)
            map.put("Check", false);
        else
            map.put("Check", true);
        map.put("Point", baseSkillPoint);
        return map;
    }
}
