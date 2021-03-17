package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyTodayViewingDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyTodayViewingTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.ADLimitInfoTable;
import com.onlyonegames.eternalfantasia.domain.repository.TodayViewingTableRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class ADViewService {
    private final GameDataTableService gameDataTableService;
    private final TodayViewingTableRepository todayViewingTableRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> GetViewStatus(Long userId, Map<String, Object> map){
        ADLimitInfoTable adLimitInfoTable = gameDataTableService.ADLimitInfoTable().get(0);
        MyTodayViewingTable myTodayViewingTable = todayViewingTableRepository.findByUseridUser(userId)
                .orElse(null);
        if (myTodayViewingTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: TodayViewingTable not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: TodayViewingTable not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        if(myTodayViewingTable.IsResetTime())
            myTodayViewingTable.ResetViewingCount();
        if(myTodayViewingTable.getTodayViewingCount() < adLimitInfoTable.getMaxViewing()) {
            myTodayViewingTable.AddViewCount();
            MyTodayViewingDto myTodayViewingDto = new MyTodayViewingDto();
            myTodayViewingDto.SetTodayViewing(myTodayViewingTable,adLimitInfoTable.getMaxViewing());
            map.put("myTodayViewing",myTodayViewingDto);
        }
        else {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_MORE_VIEW.getIntegerValue(), "Fail! -> Cause: Can't More View.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't More View.", ResponseErrorCode.CANT_MORE_VIEW);
        }
        return map;
    }
}
