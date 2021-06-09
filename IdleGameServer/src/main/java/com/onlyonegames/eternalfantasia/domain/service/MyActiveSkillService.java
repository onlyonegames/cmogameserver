package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.ActiveSkillDataJsonDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyActiveSkillData;
import com.onlyonegames.eternalfantasia.domain.repository.MyActiveSkillDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyActiveSkillService {
    private final MyActiveSkillDataRepository myActiveSkillDataRepository;
    private final UserRepository userRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> GetMySkillInfo(Long userId, Map<String, Object> map) {
        MyActiveSkillData myActiveSkillData = myActiveSkillDataRepository.findByUseridUser(userId).orElse(null);
        if(myActiveSkillData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyActiveSkillData", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Not Found MyActiveSkillData", ResponseErrorCode.NOT_FIND_DATA);
        }
        String json_saveData = myActiveSkillData.getJson_saveDataValue();
        ActiveSkillDataJsonDto activeSkillDataJsonDto = JsonStringHerlper.ReadValueFromJson(json_saveData, ActiveSkillDataJsonDto.class);
        map.put("activeSkillData", activeSkillDataJsonDto);
        return map;
    }

    public Map<String, Object> SkillLevelUp(Long userId, String skillCode, Map<String, Object> map) {
        User idleUser = userRepository.findById(userId).orElse(null);
        if(idleUser == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found IdleUser", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Not Found IdleUser", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyActiveSkillData myActiveSkillData = myActiveSkillDataRepository.findByUseridUser(userId).orElse(null);
        if(myActiveSkillData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyActiveSkillData", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Not Found MyActiveSkillData", ResponseErrorCode.NOT_FIND_DATA);
        }
        String json_saveData = myActiveSkillData.getJson_saveDataValue();
        ActiveSkillDataJsonDto activeSkillDataJsonDto = JsonStringHerlper.ReadValueFromJson(json_saveData, ActiveSkillDataJsonDto.class);
        ActiveSkillDataJsonDto.SkillInfo selectSkill = activeSkillDataJsonDto.skillInfoList.stream().filter(i -> i.code.equals(skillCode)).findAny().orElse(null);
        if(selectSkill == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found SkillInfo", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Not Found SkillInfo", ResponseErrorCode.NOT_FIND_DATA);
        }
        selectSkill.LevelUp();
        //TODO MaxLevel Up 확인 필요 중요!!!!!!!!!!!
        BigInteger spendSoulStone = new BigInteger("100");
        idleUser.SpendSoulStone(spendSoulStone);

        json_saveData = JsonStringHerlper.WriteValueAsStringFromData(activeSkillDataJsonDto);
        myActiveSkillData.ResetJson_SaveDataValue(json_saveData);
        map.put("user", idleUser);
        map.put("activeSkillData", activeSkillDataJsonDto);
        return map;
    }
}
