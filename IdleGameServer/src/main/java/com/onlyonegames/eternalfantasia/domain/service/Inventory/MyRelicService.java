package com.onlyonegames.eternalfantasia.domain.service.Inventory;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.RelicInventoryResponseDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyRelicInventory;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyRelicInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.util.MathHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyRelicService {
    private final MyRelicInventoryRepository myRelicInventoryRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> UpgradeRelic (Long userId, int relic_id, Map<String, Object> map) {
        List<MyRelicInventory> myRelicInventoryList = myRelicInventoryRepository.findAllByUseridUser(userId);
        if(myRelicInventoryList == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyRelicInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Not Found MyRelicInventory", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyRelicInventory myRelicInventory = myRelicInventoryList.stream().filter(i -> i.getTable_id() == relic_id).findAny().orElse(null);
        if(myRelicInventory == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyRelicInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Not Found MyRelicInventory", ResponseErrorCode.NOT_FIND_DATA);
        }
        if(!myRelicInventory.SpendRelic()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_RELIC.getIntegerValue(), "Need More Relic", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Need More Relic", ResponseErrorCode.NEED_MORE_RELIC);
        }
        RelicInventoryResponseDto relicInventoryResponseDto = new RelicInventoryResponseDto();
        double rng = MathHelper.Range(0, 1);
        double baseRNG = 1-(0.02*(myRelicInventory.getLevel()-1));
        if(myRelicInventory.getLevel() == 0 || rng <= baseRNG)
            myRelicInventory.LevelUp();

        relicInventoryResponseDto.InitFromDB(myRelicInventory);
        map.put("myRelicInventory", relicInventoryResponseDto);
        return map;
    }
}