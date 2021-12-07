package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.MailSendRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyEventExchangeInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.AccessoryTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.EquipmentTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.ExchangeItemTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.RuneInfoTable;
import com.onlyonegames.eternalfantasia.domain.repository.MyEventExchangeInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.Mail.MyMailBoxService;
import com.onlyonegames.util.MathHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class EventExchangeService {
    private final MyEventExchangeInfoRepository myEventExchangeInfoRepository;
    private final UserRepository userRepository;
    private final ErrorLoggingService errorLoggingService;
    private final GameDataTableService gameDataTableService;
    private final MyMailBoxService myMailBoxService;

    public Map<String, Object> ExchangeItem(Long userId, int exchangeIndex, Map<String, Object> map) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: User Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: User Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyEventExchangeInfo myEventExchangeInfo = myEventExchangeInfoRepository.findByUseridUser(userId).orElse(null);
        if (myEventExchangeInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyEventExchangeInfo Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEventExchangeInfo Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<ExchangeItemTable> exchangeItemTableList = gameDataTableService.ExchangeItemTable();
        ExchangeItemTable exchangeItemTable = exchangeItemTableList.get(exchangeIndex);
        String[] rewardList = exchangeItemTable.getRewardList().split(",");
        switch (exchangeItemTable.getItemName()) {
            case "골드":
                if (!myEventExchangeInfo.BuyGoldCount()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_EXCHANGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't exchange anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't exchange anymore", ResponseErrorCode.CANT_EXCHANGE_ANYMORE);
                }
                break;
            case "고급 골드":
                if (!myEventExchangeInfo.BuyAdvancedGoldCount()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_EXCHANGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't exchange anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't exchange anymore", ResponseErrorCode.CANT_EXCHANGE_ANYMORE);
                }
                break;
            case "영혼석":
                if (!myEventExchangeInfo.BuySoulStoneCount()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_EXCHANGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't exchange anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't exchange anymore", ResponseErrorCode.CANT_EXCHANGE_ANYMORE);
                }
                break;
            case "고급 영혼석":
                if (!myEventExchangeInfo.BuyAdvancedSoulStoneCount()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_EXCHANGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't exchange anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't exchange anymore", ResponseErrorCode.CANT_EXCHANGE_ANYMORE);
                }
                break;
            case "증폭파편":
                if (!myEventExchangeInfo.BuyFragmentCount()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_EXCHANGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't exchange anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't exchange anymore", ResponseErrorCode.CANT_EXCHANGE_ANYMORE);
                }
                break;
            case "던전열쇠":
                if (!myEventExchangeInfo.BuyDungeonTicketCount()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_EXCHANGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't exchange anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't exchange anymore", ResponseErrorCode.CANT_EXCHANGE_ANYMORE);
                }
                break;
            case "고급 던전열쇠":
                if (!myEventExchangeInfo.BuyAdvancedDungeonTicketCount()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_EXCHANGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't exchange anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't exchange anymore", ResponseErrorCode.CANT_EXCHANGE_ANYMORE);
                }
                break;
            case "고대결정":
                if (!myEventExchangeInfo.BuyCrystalCount()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_EXCHANGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't exchange anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't exchange anymore", ResponseErrorCode.CANT_EXCHANGE_ANYMORE);
                }
                break;
            case "전설클래스(법사)":
                if (!myEventExchangeInfo.BuyLegendClassCount()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_EXCHANGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't exchange anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't exchange anymore", ResponseErrorCode.CANT_EXCHANGE_ANYMORE);
                }
                break;
            case "신화클래스(법사)":
                if (!myEventExchangeInfo.BuyDivineClassCount()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_EXCHANGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't exchange anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't exchange anymore", ResponseErrorCode.CANT_EXCHANGE_ANYMORE);
                }
                break;
            case "루돌프 사슴코":
                if (!myEventExchangeInfo.BuyCostumeACount()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_EXCHANGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't exchange anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't exchange anymore", ResponseErrorCode.CANT_EXCHANGE_ANYMORE);
                }
                break;
            case "크리스마스 산타":
                if (!myEventExchangeInfo.BuyCostumeBCount()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_EXCHANGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't exchange anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't exchange anymore", ResponseErrorCode.CANT_EXCHANGE_ANYMORE);
                }
                break;
            case "신화 B급 장비(랜덤)":
                if (!myEventExchangeInfo.BuyDivineRandomBEquipment()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_EXCHANGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't exchange anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't exchange anymore", ResponseErrorCode.CANT_EXCHANGE_ANYMORE);
                }
                break;
            case "신화 D급 보석(랜덤)":
                if (!myEventExchangeInfo.BuyDivineRandomDRune()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_EXCHANGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't exchange anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't exchange anymore", ResponseErrorCode.CANT_EXCHANGE_ANYMORE);
                }
                break;
            case "장신구(랜덤)":
                if (!myEventExchangeInfo.BuyRandomBasicAccessory()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_EXCHANGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't exchange anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't exchange anymore", ResponseErrorCode.CANT_EXCHANGE_ANYMORE);
                }
                break;
            case "고대 B급 장비 (랜덤)":
                if (!myEventExchangeInfo.BuyAncientRandomBEquipment()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_EXCHANGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't exchange anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't exchange anymore", ResponseErrorCode.CANT_EXCHANGE_ANYMORE);
                }
                break;
            case "연금장신구(랜덤)":
                if (!myEventExchangeInfo.BuyRandomNewAccessory()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_EXCHANGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't exchange anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't exchange anymore", ResponseErrorCode.CANT_EXCHANGE_ANYMORE);
                }
                break;
            case "오브(랜덤)":
                if (!myEventExchangeInfo.BuyRandomOrb()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_EXCHANGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't exchange anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't exchange anymore", ResponseErrorCode.CANT_EXCHANGE_ANYMORE);
                }
                break;
        }
//        if (!user.SpendEventItem((long) exchangeItemTable.getPrice())) {
//            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_EVENT_ITEM.getIntegerValue(), "Fail! -> Cause: NEED_MORE_EVENTITEM", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
//            throw new MyCustomException("Fail! -> Cause: NEED_MORE_EVENTITEM", ResponseErrorCode.NEED_MORE_EVENT_ITEM);
//        }
        long longPrice = 0;
        int intPrice = 0;
        switch (exchangeItemTable.getCurrencyType()) {
            case "eventItem":
                longPrice = Long.parseLong(exchangeItemTable.getPrice());
                break;
            case "advancedEventItem":
                intPrice = Integer.parseInt(exchangeItemTable.getPrice());
                break;
        }
        SpendPrice(user, exchangeItemTable.getCurrencyType(), longPrice, intPrice);
        Map<String, Object> tempMap = new HashMap<>();
        for (String s : rewardList) {
            String[] reward = s.split(":");
            switch(reward[0]) {
                case "randomNewAccessory":
                    AccessoryTable accessoryCode = GetRandomNewAccessory();
                    SendMail(userId, exchangeItemTable.getItemName(), accessoryCode.getCode(), reward[1], tempMap);
                    break;
                case "divineRandomBEquipment":
                    EquipmentTable equipmentTable = GetRandomDivineBGradeEquipment();
                    SendMail(userId, exchangeItemTable.getItemName(), equipmentTable.getCode(), reward[1], tempMap);
                    break;
                case "divineRandomDRune":
                    RuneInfoTable runeInfoTable = GetDivineDGradeRune();
                    SendMail(userId, exchangeItemTable.getItemName(), runeInfoTable.getCode(), reward[1], tempMap);
                    break;
                case "randomBasicAccessory":
                    AccessoryTable accessoryTable = GetRandomBasicAccessory();
                    SendMail(userId, exchangeItemTable.getItemName(), accessoryTable.getCode(), reward[1], tempMap);
                    break;
                case "ancientRandomBEquipment":
                    EquipmentTable ancientEquipmentTable = GetAncientBGradeRandomEquipment();
                    SendMail(userId, exchangeItemTable.getItemName(), ancientEquipmentTable.getCode(), reward[1], tempMap);
                    break;
                case "randomOrb":
                    String orbName = RandomOrb();
                    SendMail(userId, exchangeItemTable.getItemName(), orbName, reward[1], tempMap);
                    break;
                default:
                    SendMail(userId, exchangeItemTable.getItemName(), reward[0], reward[1], tempMap);
                    break;
            }
        }
        map.put("myEventExchangeInfo", myEventExchangeInfo);
        map.put("userInfo", user);
        return map;
    }

    public Map<String, Object> ExchangeAdvancedEventItem(Long userId, int exchangeCount, Map<String, Object> map) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: User Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: User Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        Long spendCount = exchangeCount * 1000L;
        if (!user.SpendEventItem(spendCount)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_EVENT_ITEM.getIntegerValue(), "Fail! -> Cause: NEED_MORE_EVENT_ITEM", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: NEED_MORE_EVENT_ITEM", ResponseErrorCode.NEED_MORE_EVENT_ITEM);
        }
        user.AddAdvancedEventItem(exchangeCount);
        map.put("userInfo", user);
        return map;
    }

    private AccessoryTable GetRandomNewAccessory() {
        int randomIndex = (int) MathHelper.Range(12, 15);
        return gameDataTableService.AccessoryTable().get(randomIndex);
    }

    private AccessoryTable GetRandomBasicAccessory() {
        int randomIndex = (int) (Math.random() * 12);
        return gameDataTableService.AccessoryTable().get(randomIndex);
    }

    private RuneInfoTable GetDivineDGradeRune() {
        List<RuneInfoTable> runeInfoTableList = gameDataTableService.RuneInfoTable();
        List<RuneInfoTable> divineRuneInfoTableList = runeInfoTableList.stream().filter(i -> i.getQualityNo() == 6 && i.getGradeNo() == 1).collect(Collectors.toList());
        int index = (int) (Math.random() * 5);
        return divineRuneInfoTableList.get(index);
    }

    private EquipmentTable GetRandomDivineBGradeEquipment() {
        int randomIndex = (int) (Math.random() * 5);
        List<EquipmentTable> equipmentTableList = gameDataTableService.EquipmentTable().stream().filter(i -> i.getGrade().equals("Divine") && i.getGradeValue() == 3).collect(Collectors.toList());
        return equipmentTableList.get(randomIndex);
    }

    private EquipmentTable GetAncientBGradeRandomEquipment() {
        List<EquipmentTable> equipmentTableList = gameDataTableService.EquipmentTable();
        List<EquipmentTable> ancientEquipmentList = equipmentTableList.stream().filter(i -> i.getGrade().equals("Ancient") && i.getGradeValue() == 3).collect(Collectors.toList());
        int index = (int) (Math.random() * 5);
        return ancientEquipmentList.get(index);
    }

    private String RandomOrb() {
        int randomIndex = (int) (Math.random() * 4);
        switch (randomIndex) {
            case 0:
                return "redOrb";
            case 1:
                return "blueOrb";
            case 2:
                return "greenOrb";
            case 3:
                return "yellowOrb";

        }
        return null;
    }

    private void SpendPrice(User user, String currencyType, long longPrice, int intPrice) {
        if (currencyType.equals("eventItem")) {
            if (longPrice == 0) {
                errorLoggingService.SetErrorLog(user.getId(), ResponseErrorCode.CURRENCY_TYPE_ERROR.getIntegerValue(), "Fail! -> Cause: CURRENCY_TYPE_ERROR.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: CURRENCY_TYPE_ERROR.", ResponseErrorCode.CURRENCY_TYPE_ERROR);
            }
            if (!user.SpendEventItem(longPrice)) {
                errorLoggingService.SetErrorLog(user.getId(), ResponseErrorCode.NEED_MORE_EVENT_ITEM.getIntegerValue(), "Fail! -> Cause: NEED_MORE_EVENT_ITEM.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: NEED_MORE_EVENT_ITEM.", ResponseErrorCode.NEED_MORE_EVENT_ITEM);
            }
        }
        else if (currencyType.equals("advancedEventItem")) {
            if (intPrice == 0) {
                errorLoggingService.SetErrorLog(user.getId(), ResponseErrorCode.CURRENCY_TYPE_ERROR.getIntegerValue(), "Fail! -> Cause: CURRENCY_TYPE_ERROR.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: CURRENCY_TYPE_ERROR.", ResponseErrorCode.CURRENCY_TYPE_ERROR);
            }
            if (!user.SpendAdvancedEventItem(intPrice)) {
                errorLoggingService.SetErrorLog(user.getId(), ResponseErrorCode.NEED_MORE_ADVANCED_EVENT_ITEM.getIntegerValue(), "Fail! -> Cause: NEED_MORE_ADVANCED_EVENT_ITEM.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: NEED_MORE_ADVANCED_EVENT_ITEM.", ResponseErrorCode.NEED_MORE_ADVANCED_EVENT_ITEM);
            }
        }
    }

    private void SendMail(Long userId, String title, String gettingItem, String gettingItemCount, Map<String, Object> tempMap) {
        LocalDateTime now = LocalDateTime.now();
        MailSendRequestDto mailSendRequestDto = new MailSendRequestDto();
        mailSendRequestDto.setToId(userId);
        mailSendRequestDto.setSendDate(now);
        mailSendRequestDto.setMailType(0);
        mailSendRequestDto.setExpireDate(now.plusDays(30));
        mailSendRequestDto.setTitle(title);
        mailSendRequestDto.setGettingItem(gettingItem); //TODO 보상 테이블에 있는 보상으로 지급
        mailSendRequestDto.setGettingItemCount(gettingItemCount);
        myMailBoxService.SendMail(mailSendRequestDto, tempMap);
    }
}
