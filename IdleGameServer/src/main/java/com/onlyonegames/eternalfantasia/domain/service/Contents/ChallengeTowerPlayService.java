package com.onlyonegames.eternalfantasia.domain.service.Contents;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyContentsInfo;
import com.onlyonegames.eternalfantasia.domain.repository.MyContentsInfoRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class ChallengeTowerPlayService {
    private final MyContentsInfoRepository myContentsInfoRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> ChallengeTowerSuccess(Long userId, Map<String, Object> map) {
        MyContentsInfo myContentsInfo = myContentsInfoRepository.findByUseridUser(userId).orElse(null);
        if (myContentsInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyContentsInfo not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyContentsInfo not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        if (myContentsInfo.getChallengeTowerFloor() >= 50) {
            //TODO Error 추가
        }
        myContentsInfo.ClearFloor();
        map.put("challeangeTowerFloor", myContentsInfo.getChallengeTowerFloor());
        return map;
    }
}
