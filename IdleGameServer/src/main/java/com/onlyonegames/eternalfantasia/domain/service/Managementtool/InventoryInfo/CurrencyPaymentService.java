package com.onlyonegames.eternalfantasia.domain.service.Managementtool.InventoryInfo;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.CurrencyDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class CurrencyPaymentService {
    private final UserRepository userRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> AddCurrency(CurrencyDto dto, Map<String, Object> map){
        User user = userRepository.findById(dto.userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(dto.userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: user not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: user not find", ResponseErrorCode.NOT_FIND_DATA);
        }
        user.AddDiamond(dto.addDiamond);
        user.AddGold(dto.addGold);
        user.AddLinkforcePoint(dto.addLinkforcePoint);
        user.AddLowDragonScale(dto.addLowDragonScale);
        user.AddMiddleDragonScale(dto.addMiddleDragonScale);
        user.AddHighDragonScale(dto.addHighDragonScale);
        user.AddArenaCoin(dto.addArenaCoin);
        user.AddArenaTicket(dto.addArenaTicket);
        CurrencyDto currencyDto = new CurrencyDto();
        currencyDto.InitFromDbData(user);
        map.put("result", currencyDto);
        return map;
    }
}
