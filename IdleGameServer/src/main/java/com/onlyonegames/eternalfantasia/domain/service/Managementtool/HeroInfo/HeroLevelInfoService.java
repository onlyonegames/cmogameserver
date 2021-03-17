package com.onlyonegames.eternalfantasia.domain.service.Managementtool.HeroInfo;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.UserDetailInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.util.MathHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class HeroLevelInfoService {
    private final UserRepository userRepository;
    private final MyCharactersRepository myCharactersRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> findUserLevelInfo(Long userId, Map<String, Object> map){
        UserDetailInfoDto userDetailInfoDto = new UserDetailInfoDto();
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyCharacters myCharacters = myCharactersRepository.findByUseridUserAndCodeHerostable(userId, "hero")
                .orElse(null);
        if(myCharacters == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyCharacter not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyCharacter not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        userDetailInfoDto.setUserDetailInfo(userId, myCharacters.getLevel(),myCharacters.getExp(), user.getSkillPoint(), myCharacters.getFatigability());
        map.put("UserLevelInfo", userDetailInfoDto);
        return map;
    }

    public Map<String, Object> setUserLevelInfo(UserDetailInfoDto dto, Map<String, Object> map){
        User user = userRepository.findById(dto.userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(dto.getUserId(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyCharacters myCharacters = myCharactersRepository.findByUseridUserAndCodeHerostable(dto.userId, "hero")
                .orElse(null);
        if(myCharacters == null) {
            errorLoggingService.SetErrorLog(dto.getUserId(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyCharacter not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyCharacter not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        if(dto.level == myCharacters.getLevel() && dto.exp != myCharacters.getExp()){
            myCharacters.AddExp((dto.exp - myCharacters.getExp()),myCharacters.getMaxLevel());
        } else if(dto.level != myCharacters.getLevel() && dto.exp == myCharacters.getExp()){
            myCharacters.AddExp((GetNextLevelUpExpForLv(dto.level-1) - myCharacters.getExp()),myCharacters.getMaxLevel());
        } else if(dto.level != myCharacters.getLevel() && dto.exp != myCharacters.getExp()) {
            errorLoggingService.SetErrorLog(dto.getUserId(), ResponseErrorCode.NONE.getIntegerValue(), "[ManagementTool] Fail! -> Cause : 레벨과 경험치를 동시에 변경할 수 없습니다.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause : 레벨과 경험치를 동시에 변경할 수 없습니다.", ResponseErrorCode.NONE);
        }
        if(user.getSkillPoint() != dto.skillPoint){
            user.AddSkillPoint(dto.skillPoint-user.getSkillPoint());
        }
        dto.setUserDetailInfo(dto.userId,myCharacters.getLevel(),myCharacters.getExp(),user.getSkillPoint(), myCharacters.getFatigability());
        map.put("HeroDetailInfo", dto);
        return map;
    }

    int GetNextLevelUpExpForLv(int nowLv) {
        if (nowLv == 100)
            nowLv--;
        int tempLv = nowLv;
        int resultExp = 0;
        while (tempLv >= 1)
        {
            resultExp += CalculateExp(tempLv);
            tempLv--;
        }

        return nowLv >= 1 ? resultExp : 0;
    }

    int CalculateExp(int nowLv) {
        if (nowLv <= 0)
            return 0;
        float result = (float) MathHelper.RoundUPMinus1((100 + ((nowLv - 1) * 10) +(nowLv * (nowLv - 1)) * 2));
        return (int)result;
    }
}
