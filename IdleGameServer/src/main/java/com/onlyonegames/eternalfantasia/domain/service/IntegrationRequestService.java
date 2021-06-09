package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.IntegrationRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.SetGoldRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyEquipmentInventoryRepository;
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
public class IntegrationRequestService {
    private final UserRepository userRepository;
    private final MyEquipmentInventoryRepository myEquipmentInventoryRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> IdleItemGetting(Long userId, IntegrationRequestDto requestList, Map<String, Object> map) {
        User user = null;
        for(IntegrationRequestDto.RequestContent temp : requestList.getRequestContentList()){
            switch(temp.path){
                case "Set_Gold":
                    if(user == null) {
                        user = userRepository.findById(userId).orElse(null);
                        if(user == null) {
                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found IdleUser", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                            throw new MyCustomException("Not Found IdleUser", ResponseErrorCode.NOT_FIND_DATA);
                        }
                    }
                    SetGoldRequestDto setGoldRequestDto = JsonStringHerlper.ReadValueFromJson(temp.body, SetGoldRequestDto.class);
                    BigInteger addGold = new BigInteger(setGoldRequestDto.getAddGold());
                    user.AddGold(addGold);
                    break;
                case "Set_Exp":
                    if(user == null) {
                        user = userRepository.findById(userId).orElse(null);
                        if(user == null) {
                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found IdleUser", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                            throw new MyCustomException("Not Found IdleUser", ResponseErrorCode.NOT_FIND_DATA);
                        }
                    }
                    //TODO 유저 경험치 추가 후 획득 코드 추가 필요
                    break;

            }
        }
        map.put("user", user);
        return map;
    }
}
