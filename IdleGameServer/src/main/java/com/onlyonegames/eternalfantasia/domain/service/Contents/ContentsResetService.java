package com.onlyonegames.eternalfantasia.domain.service.Contents;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyContentsInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.StandardTime;
import com.onlyonegames.eternalfantasia.domain.repository.MyContentsInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.StandardTimeRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class ContentsResetService {
    private final StandardTimeRepository standardTimeRepository;
    private final MyContentsInfoRepository myContentsInfoRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> ResetChallengeTower(Map<String, Object> map) {
        StandardTime standardTime = standardTimeRepository.findById(1).orElse(null);
        if(standardTime == null) {
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found StandardTime", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Not Found StandardTime", ResponseErrorCode.NOT_FIND_DATA);
        }
        LocalDateTime now = LocalDateTime.now();
        if(now.isAfter(standardTime.getBaseDayTime())) {
            int classIndex = standardTime.getChallengeTowerClassIndex()+1;
            standardTime.SetChallengeTowerClassIndex(classIndex<5?classIndex:0);
            List<MyContentsInfo> myContentsInfoList = myContentsInfoRepository.findAllByChallengeTowerFloorNot(0);
            for(MyContentsInfo temp : myContentsInfoList) {
                temp.SetChallengeTowerFloor(0);
            }
        }
        map.put("standardTime", standardTime);
        return map;
    }
}
