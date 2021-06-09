package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyForTest;
import com.onlyonegames.eternalfantasia.domain.repository.MyForTestRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyForTestService
{
    private final MyForTestRepository myForTestRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> GetMyForTest (Long userId, Map<String, Object> map)
    {
        MyForTest myForTest =  myForTestRepository.findByUseridUser(userId).orElse(null);
        //String json = JsonStringHerlper.WriteValueAsStringFromData(myForTest);
        //map.put("myForTest", json);
        if(!map.isEmpty()) {
            errorLoggingService.SetErrorLog(userId, 0, "Test Transactional", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Not Found UpgradeStatus", ResponseErrorCode.NOT_FIND_DATA);
        }

        map.put("myForTest", myForTest);
        return map;
    }
}
