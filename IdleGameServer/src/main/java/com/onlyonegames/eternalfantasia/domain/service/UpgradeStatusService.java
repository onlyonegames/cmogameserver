package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.entity.BigIntegerTest;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyStatusInfo;
import com.onlyonegames.eternalfantasia.domain.repository.BigIntegerTestRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyStatusInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class UpgradeStatusService
{
    private final MyStatusInfoRepository myStatusInfoRepository;
    private final ErrorLoggingService errorLoggingService;
    private final BigIntegerTestRepository bigIntegerTestRepository;
    private final UserRepository userRepository;

    public Map<String, Object> GetUpgradeStatus (Long userId, Map<String, Object> map)
    {
        MyStatusInfo myStatusInfo = myStatusInfoRepository.findByUseridUser(userId).orElse(null);
        //String json = JsonStringHerlper.WriteValueAsStringFromData(myForTest);
        //map.put("myForTest", json);
        map.put("myStatusInfo", myStatusInfo);
        return map;
    }

    public Map<String, Object> UpgradeStatus(Long userId, int addLevel, MyStatusInfo.STATUS_TYPE type, Map<String, Object> map) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found User", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Not Found User", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyStatusInfo myStatusInfo = myStatusInfoRepository.findByUseridUser(userId).orElse(null);
        if(myStatusInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found UpgradeStatus", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Not Found UpgradeStatus", ResponseErrorCode.NOT_FIND_DATA);
        }
        BigIntegerTest test = bigIntegerTestRepository.findById(1L).orElse(null);
//        String previousPrice = myStatusInfo.ReturnPreviousPrice(type);
//        if(previousPrice.equals("")) //TODO Should to add ErrorCode
//            throw new MyCustomException("Not Found UpgradeStatus", ResponseErrorCode.NOT_FIND_DATA);
//        BigInteger multiplyNum = new BigInteger("1050000000000");
        int level = myStatusInfo.ReturnTypeLevel(type);
        if(level == -1)//TODO Should to add ErrorCode
            throw new MyCustomException("Not Found UpgradeStatus", ResponseErrorCode.NOT_FIND_DATA);
        BigInteger levelNum = new BigInteger(String.valueOf(level));
        int baseValue = myStatusInfo.ReturnBaseValue(type);
        if(baseValue == -1)//TODO Should to add ErrorCode
            throw new MyCustomException("Not Found UpgradeStatus", ResponseErrorCode.NOT_FIND_DATA);
        /* 능력치 상승비용
         *  능력치 상승비용 계산공식
         *  현재레벨 = n
         *  기본가격 = x
         *  x + x/2(n^3 - n^2) */
        BigInteger secondNum = levelNum.multiply(levelNum);
        BigInteger thirdNum = secondNum.multiply(levelNum);
        BigInteger finalNum = thirdNum.subtract(secondNum);
        finalNum = finalNum.multiply(BigInteger.valueOf(baseValue/2));
        finalNum = finalNum.add(BigInteger.valueOf(baseValue));
//        BigDecimal previousNum = new BigDecimal(previousPrice);
//        BigDecimal multiplyNum = new BigDecimal("1.05");
//
//        BigDecimal presentNum = previousNum.multiply(multiplyNum).setScale(3, BigDecimal.ROUND_UP);
        map.put("bigintegerTest", finalNum.toString());
//        BigInteger gold = new BigInteger(test.getGold());
        if(!user.SpendGold(finalNum))//TODO Should to add ErrorCode
            throw new MyCustomException("Not Found UpgradeStatus", ResponseErrorCode.NOT_FIND_DATA);
        map.put("idleUser", user);
//        BigInteger bigInteger = new BigInteger("100000000000000000000000");
//        bigInteger.
        myStatusInfo.AddStatus(type, addLevel, "1");
        map.put("myStatusInfo", myStatusInfo);
        return map;
    }
}
