package com.onlyonegames.eternalfantasia.domain.service.Test;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.ActiveSkillDataJsonDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.PassiveSkillDataJsonDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.MyWorldBossPlayData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Iap.GooglePurchaseData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.WorldBossPlayLog;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyActiveSkillData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyPassiveSkillData;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.MyWorldBossPlayDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Iap.GooglePurchaseDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Logging.WorldBossPlayLogRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyActiveSkillDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyPassiveSkillDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class UserCheckService {
    private final MyActiveSkillDataRepository myActiveSkillDataRepository;
    private final MyPassiveSkillDataRepository myPassiveSkillDataRepository;
    private final UserRepository userRepository;
    private final ErrorLoggingService errorLoggingService;
    private final MyWorldBossPlayDataRepository myWorldBossPlayDataRepository;
    private final WorldBossPlayLogRepository worldBossPlayLogRepository;
    private final GooglePurchaseDataRepository googlePurchaseDataRepository;

    public Map<String, Object> UserSkillPointCheck(Long userId, Map<String, Object> map) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: User not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: User not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        int baseSkillPoint = (user.getLevel() * 2) -2;
        baseSkillPoint -= user.getSkillPoint();
        MyActiveSkillData myActiveSkillData = myActiveSkillDataRepository.findByUseridUser(userId).orElse(null);
        if (myActiveSkillData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyActiveSkillData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyActiveSkillData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyPassiveSkillData myPassiveSkillData = myPassiveSkillDataRepository.findByUseridUser(userId).orElse(null);
        if (myPassiveSkillData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyPassiveSkillData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyPassiveSkillData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        ActiveSkillDataJsonDto activeSkillDataJsonDto = JsonStringHerlper.ReadValueFromJson(myActiveSkillData.getJson_saveDataValue(), ActiveSkillDataJsonDto.class);
        PassiveSkillDataJsonDto passiveSkillDataJsonDto = JsonStringHerlper.ReadValueFromJson(myPassiveSkillData.getJson_saveDataValue(), PassiveSkillDataJsonDto.class);

        for (ActiveSkillDataJsonDto.SkillInfo skillInfo : activeSkillDataJsonDto.skillInfoList) {
            baseSkillPoint -= (skillInfo.level-1)*3;
        }
        for (PassiveSkillDataJsonDto.PassiveSkillInfo passiveSkillInfo : passiveSkillDataJsonDto.passiveSkillInfoList) {
            baseSkillPoint -= passiveSkillInfo.level-1;
        }

        if (baseSkillPoint != 0)
            map.put("Check", false);
        else
            map.put("Check", true);
        map.put("Point", baseSkillPoint);
        return map;
    }

    public Map<String, Object> WorldBossPlayCount(Map<String, Object> map) {
        LocalDateTime date = LocalDateTime.of(LocalDate.of(2021, 10,29), LocalTime.of(0,0,0));
        List<WorldBossPlayLog> worldBossPlayLogList = worldBossPlayLogRepository.findAllByCreateddateAfter(date);
        List<MyWorldBossPlayData> myWorldBossPlayDataList = myWorldBossPlayDataRepository.findAll();
        for (MyWorldBossPlayData data : myWorldBossPlayDataList) {
            if (data.getPlayableCount() == 3)
                continue;
            List<WorldBossPlayLog> myPlayLog = worldBossPlayLogList.stream().filter(i -> i.getUseridUser().equals(data.getUseridUser())).collect(Collectors.toList());
            int n = (int) myPlayLog.stream().filter(i -> i.getCreateddate().isAfter(LocalDateTime.of(LocalDate.of(2021, 10, 30), LocalTime.of(0, 0, 0)))).count();
            if (n == 0) {
                data.SetPlayableCount(3);
                continue;
            }
            int y = (int) myPlayLog.stream().filter(i -> i.getCreateddate().isBefore(LocalDateTime.of(LocalDate.of(2021, 10, 30), LocalTime.of(0, 0, 0)))).count();
            if (y == 0)
                continue;
            if (y >= 3) {
                data.SetPlayableCount(3);
                continue;
            }
            int count = data.getPlayableCount();
            data.SetPlayableCount(count+y);
        }
        return map;
    }

//    public Map<String, Object> Price(Map<String, Object> map) {
//        List<GooglePurchaseData> googlePurchaseDataList = googlePurchaseDataRepository.findAll();
//        List<User> userList = userRepository.findAll();
//
//        for (User user : userList) {
//            List<GooglePurchaseData> googlePurchaseDataList1 = googlePurchaseDataList.stream().filter(i -> i.getUseridUser().equals(user.getId())).collect(Collectors.toList());
//            if (googlePurchaseDataList1.size() == 0)
//                continue;
//            for (GooglePurchaseData googlePurchaseData : googlePurchaseDataList1) {
//                int price = 0;
//                switch (googlePurchaseData.getGoodsId()) {
//                    case "shop_today_package":
//                        price = 1100;
//                        break;
//                    case "shop_ad_package":
//                    case "shop_diamond_00":
//                        price = 5500;
//                        break;
//                    case "pass_01_0":
//                    case "pass_01_1":
//                    case "pass_01_2":
//                    case "pass_01_3":
//                    case "pass_01_4":
//                    case "pass_01_5":
//                    case "pass_01_6":
//                    case "pass_01_7":
//                    case "pass_01_8":
//                    case "pass_01_9":
//                    case "shop_diamond_01":
//                    case "shop_soul_stone_package":
//                    case "shop_start_package":
//                        price = 11000;
//                        break;
//                    case "pass_00":
//                    case "pass_02":
//                    case "shop_week_package":
//                    case "shop_class_package_00":
//                    case "shop_class_package_01":
//                    case "shop_class_package_02":
//                    case "shop_class_package_03":
//                    case "shop_class_package_04":
//                        price = 33000;
//                        break;
//                    case "shop_diamond_02":
//                    case "shop_month_package":
//                        price = 55000;
//                        break;
//                    case "shop_diamond_03":
//                        price = 110000;
//                        break;
//                }
//                user.AddPurchase(price);
//            }
//            googlePurchaseDataList.removeAll(googlePurchaseDataList1);
//        }
//        return map;
//    }
}
