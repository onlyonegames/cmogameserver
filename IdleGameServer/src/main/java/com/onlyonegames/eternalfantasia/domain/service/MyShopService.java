package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.IapResponseDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.ShopPurchaseLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.MailSendRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Iap.GooglePurchaseData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyShopInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.Iap.GooglePurchaseDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Logging.ShopPurchaseLogRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyShopInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.Iap.IapService;
import com.onlyonegames.eternalfantasia.domain.service.Mail.MyMailBoxService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.util.MathHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyShopService {
    private final MyShopInfoRepository myShopInfoRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;
    private final MyMailBoxService myMailBoxService;
    private final UserRepository userRepository;
    private final GooglePurchaseDataRepository googlePurchaseDataRepository;
    private final ShopPurchaseLogRepository shopPurchaseLogRepository;

    public Map<String, Object> ShopBuy(Long userId, int itemIndex, String payLoad, Map<String, Object> map) throws IOException {
        MyShopInfo myShopInfo = myShopInfoRepository.findByUseridUser(userId).orElse(null);
        if (myShopInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyShopInfo Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyShopInfo Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: User Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: User Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<ShopRewardTable> shopRewardTableList = gameDataTableService.ShopRewardTable();
        ShopRewardTable shopRewardTable = shopRewardTableList.get(itemIndex);
        boolean log = SpendPrice(user, shopRewardTable.getCurrencyType(), shopRewardTable.getPrice(), payLoad);
        String[] rewardList = shopRewardTable.getRewardList().split(",");
        switch (shopRewardTable.getItemName()) {
            case "무료 다이아":
                if (!myShopInfo.BuyFreeDiamond()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "오늘의 패키지":
                if (!myShopInfo.BuyTodayPackage()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "고대결정 패키지":
                if (!myShopInfo.BuyAncientCrystalPackage()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "연금 장신구 패키지":
                if (!myShopInfo.BuyAlchemyPackage()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "버프 패키지":
                if (!myShopInfo.BuyBuffPackage()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "스팩업 패키지":
                if (!myShopInfo.BuySpecUpPackage()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "스킬각성패키지":
                if (!myShopInfo.BuySkillAwakeningPackage()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "영혼석 패키지":
                if (!myShopInfo.BuySoulStonePackage()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "광고제거 패키지":
                if (!myShopInfo.BuyAdRemovePackage()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "증폭 파편 패키지":
                if (!myShopInfo.BuyFragmentPackage()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "골드 획득 부스터":
                if (!myShopInfo.BuyGoldAD()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "영혼석 획득 부스터":
                if (!myShopInfo.BuySoulStoneAD()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "경험치 상승 부스터":
                if (!myShopInfo.BuyExpAD()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "아이템 드랍 부스터":
                if (!myShopInfo.BuyItemAD()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "공속/이속 부스터":
                if (!myShopInfo.BuySpeedAD()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "임인년 특별패키지(흑)":
                if (!myShopInfo.BuyNewYearBlackPackage()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "임인년 특별패키지(백)":
                if (!myShopInfo.BuyNewYearWhitePackage()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "임인년 특별패키지(황)":
                if (!myShopInfo.BuyNewYearYellowPackage()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "임인년 특별패키지(홍)":
                if (!myShopInfo.BuyNewYearRedPackage()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "임인년 성장 패키지 1":
                if (!myShopInfo.BuyNewYearGrowthUpPackage1()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "임인년 성장 패키지 2":
                if (!myShopInfo.BuyNewYearGrowthUpPackage2()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "임인년 성장 패키지 3":
                if (!myShopInfo.BuyNewYearGrowthUpPackage3()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "임인년 성장 패키지 4":
                if (!myShopInfo.BuyNewYearGrowthUpPackage4()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "임인년 성장 패키지 5":
                if (!myShopInfo.BuyNewYearGrowthUpPackage5()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "임인년 도전 패키지 1":
                if (!myShopInfo.BuyNewYearChallengePackage1()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "임인년 도전 패키지 2":
                if (!myShopInfo.BuyNewYearChallengePackage2()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "임인년 도전 패키지 3":
                if (!myShopInfo.BuyNewYearChallengePackage3()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "임인년 도전 패키지 4":
                if (!myShopInfo.BuyNewYearChallengePackage4()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
            case "임인년 도전 패키지 5":
                if (!myShopInfo.BuyNewYearChallengePackage5()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't buy package anymore", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't buy package anymore", ResponseErrorCode.CANT_BUY_PACKAGE_ANYMORE);
                }
                break;
        }
        Map<String, Object> tempMap = new HashMap<>();
        boolean purchase = shopRewardTable.getCurrencyType().equals("cash");
        for (String s : rewardList) {
            String[] reward = s.split(":");
            switch (reward[0]) {
                case "heroRune":
                    for (int j = 0; j < Integer.parseInt(reward[1]); j++) {
                        RuneInfoTable runeInfoTable = GetHeroRandomRune();
                        String code = runeInfoTable.getCode();
                        SendMail(userId, purchase, shopRewardTable.getItemName(), code, "1", tempMap);
                    }
                    break;
                case "accessory":
                    for (int j = 0; j < Integer.parseInt(reward[1]); j++) {
                        AccessoryTable accessoryTable = GetRandomAccessory();
                        String code = accessoryTable.getCode();
                        SendMail(userId, purchase, shopRewardTable.getItemName(), code, "1", tempMap);
                    }
                    break;
                case "legendClass":
                    for (int j = 0; j < Integer.parseInt(reward[1]); j++) {
                        HeroClassInfoTable heroClassInfoTable = GetLegendRandomHeroClassInfoTable();
                        String code = heroClassInfoTable.getCode();
                        SendMail(userId, purchase, shopRewardTable.getItemName(), code, "1", tempMap);
                    }
                    break;
                case "legendEquipment":
                    for (int j = 0; j < Integer.parseInt(reward[1]); j++) {
                        EquipmentTable equipmentTable = GetLegendRandomEquipment();
                        String code = equipmentTable.getCode();
                        SendMail(userId, purchase, shopRewardTable.getItemName(), code, "1", tempMap);
                    }
                    break;
                case "legendDGradeRune":
                    for (int j = 0; j < Integer.parseInt(reward[1]); j++) {
                        RuneInfoTable runeInfoTable = GetLegendDGradeRune();
                        String code = runeInfoTable.getCode();
                        SendMail(userId, purchase, shopRewardTable.getItemName(), code, "1", tempMap);
                    }
                    break;
                case "divineDGradeRune":
                    for (int j = 0; j < Integer.parseInt(reward[1]); j++) {
                        RuneInfoTable runeInfoTable = GetDivineDGradeRune();
                        String code = runeInfoTable.getCode();
                        SendMail(userId, purchase, shopRewardTable.getItemName(), code, "1", tempMap);
                    }
                    break;
                case "legendAGradeEquipment":
                    for (int j = 0; j < Integer.parseInt(reward[1]); j++) {
                        EquipmentTable equipmentTable = GetLegendAGradeRandomEquipment();
                        String code = equipmentTable.getCode();
                        SendMail(userId, purchase, shopRewardTable.getItemName(), code, "1", tempMap);
                    }
                    break;
                case "allAccessory":
                    for (int j = 0; j < 12; j++) {
                        AccessoryTable accessoryTable = gameDataTableService.AccessoryTable().get(j);
                        String code = accessoryTable.getCode();
                        SendMail(userId, purchase, shopRewardTable.getItemName(), code, reward[1], tempMap);
                    }
                    break;
                case "allDivineDGradeRune":
                    for (RuneInfoTable runeInfoTable : GetAllDivineDGradeRune()) {
                        SendMail(userId, purchase, shopRewardTable.getItemName(), runeInfoTable.getCode(), reward[1], tempMap);
                    }
                    break;
                case "allDivineDGradeEquipment":
                    for (EquipmentTable equipmentTable : GetAllDivineDGradeEquipment()) {
                        SendMail(userId, purchase, shopRewardTable.getItemName(), equipmentTable.getCode(), reward[1], tempMap);
                    }
                    break;
                case "ancientDGradeEquipment":
                    EquipmentTable equipmentTable = GetAncientDGradeRandomEquipment();
                    String code = equipmentTable.getCode();
                    SendMail(userId, purchase, shopRewardTable.getItemName(), code, reward[1], tempMap);
                    break;
                case "randomOrb":
                    String orbCode = GetRandomOrb();
                    SendMail(userId, purchase, shopRewardTable.getItemName(), orbCode, reward[1], tempMap);
                    break;
                case "randomNewAccessory":
                    AccessoryTable accessoryCode = GetRandomNewAccessory();
                    SendMail(userId, purchase, shopRewardTable.getItemName(), accessoryCode.getCode(), reward[1], tempMap);
                    break;
                case "adRemove":
                    user.ADRemove();
                    break;
                default:
                    if (reward[0].contains("Booster"))
                        break;
                    SendMail(userId, purchase, shopRewardTable.getItemName(), reward[0], reward[1], tempMap);
                    break;
            }
        }
        if (log) {
            ShopPurchaseLogDto shopPurchaseLogDto = new ShopPurchaseLogDto();
            shopPurchaseLogDto.SetShopPurchaseLogDto(userId, shopRewardTable.getItemName(), shopRewardTable.getCurrencyType());
            shopPurchaseLogRepository.save(shopPurchaseLogDto.ToEntity());
        }
        map.put("myShopInfo", myShopInfo);
        map.put("userInfo", user);
        return map;
    }
    private AccessoryTable GetRandomAccessory() {
        int randomIndex = (int) (Math.random() * 12);
        return gameDataTableService.AccessoryTable().get(randomIndex);
    }

    private EquipmentTable GetLegendRandomEquipment() {

        List<EquipmentTable> equipmentTableList = gameDataTableService.EquipmentTable();
        List<EquipmentTable> legendEquipmentList = equipmentTableList.stream().filter(i -> i.getGrade().equals("Legend")).collect(Collectors.toList());
        List<Double> probabilityList = new ArrayList<>();
        probabilityList.add(0.4);
        probabilityList.add(0.3);
        probabilityList.add(0.2);
        probabilityList.add(0.1);
        int index = MathHelper.RandomIndexWidthProbability(probabilityList) + 1;
        int classIndex = (int) (Math.random() * 5);
        return legendEquipmentList.stream().filter(i -> i.getGradeValue() == index).collect(Collectors.toList()).get(classIndex);
    }

    private EquipmentTable GetLegendAGradeRandomEquipment() {
        List<EquipmentTable> equipmentTableList = gameDataTableService.EquipmentTable();
        List<EquipmentTable> legendEquipmentList = equipmentTableList.stream().filter(i -> i.getGrade().equals("Legend") && i.getGradeValue() == 4).collect(Collectors.toList());
        int index = (int) (Math.random() * 5);
        return legendEquipmentList.get(index);
    }

    private HeroClassInfoTable GetLegendRandomHeroClassInfoTable() {
        List<HeroClassInfoTable> heroClassInfoTableList = gameDataTableService.HeroClassInfoTable();
        List<HeroClassInfoTable> legendHeroClassInfoTableList = heroClassInfoTableList.stream().filter(i -> i.getGrade().equals("legend")).collect(Collectors.toList());
        int index = (int) (Math.random() * 5);
        return legendHeroClassInfoTableList.get(index);
    }

    private HeroClassInfoTable GetDivineRandomHeroClassInfoTable() {
        List<HeroClassInfoTable> heroClassInfoTableList = gameDataTableService.HeroClassInfoTable();
        List<HeroClassInfoTable> legendHeroClassInfoTableList = heroClassInfoTableList.stream().filter(i -> i.getGrade().equals("divine")).collect(Collectors.toList());
        int index = (int) (Math.random() * 5);
        return legendHeroClassInfoTableList.get(index);
    }

    private RuneInfoTable GetHeroRandomRune() {
        List<RuneInfoTable> runeInfoTableList = gameDataTableService.RuneInfoTable();
        List<RuneInfoTable> heroRuneInfoTableList = runeInfoTableList.stream().filter(i -> i.getQualityNo() == 4).collect(Collectors.toList());
        List<Double> probabilityList = new ArrayList<>();
        probabilityList.add(0.4);
        probabilityList.add(0.3);
        probabilityList.add(0.15);
        probabilityList.add(0.1);
        probabilityList.add(0.05);
        int index = MathHelper.RandomIndexWidthProbability(probabilityList) + 1;
        int kindIndex = (int) (Math.random() * 5);
        return heroRuneInfoTableList.stream().filter(i -> i.getGradeNo() == index).collect(Collectors.toList()).get(kindIndex);
    }

    private RuneInfoTable GetLegendDGradeRune() {
        List<RuneInfoTable> runeInfoTableList = gameDataTableService.RuneInfoTable();
        List<RuneInfoTable> legendRuneInfoTableList = runeInfoTableList.stream().filter(i -> i.getQualityNo() == 5 && i.getGradeNo() == 1).collect(Collectors.toList());
        int index = (int) (Math.random() * 5);
        return legendRuneInfoTableList.get(index);
    }
    private RuneInfoTable GetDivineDGradeRune() {
        List<RuneInfoTable> runeInfoTableList = gameDataTableService.RuneInfoTable();
        List<RuneInfoTable> divineRuneInfoTableList = runeInfoTableList.stream().filter(i -> i.getQualityNo() == 6 && i.getGradeNo() == 1).collect(Collectors.toList());
        int index = (int) (Math.random() * 5);
        return divineRuneInfoTableList.get(index);
    }

    private AccessoryTable GetRandomNewAccessory() {
        int randomIndex = (int) MathHelper.Range(12, 15);
        return gameDataTableService.AccessoryTable().get(randomIndex);
    }

    private String GetRandomOrb() {
        String orbCode;
        int randomIndex = (int) (Math.random() * 4);
        switch(randomIndex) {
            case 1:
                orbCode = "blueOrb";
                break;
            case 2:
                orbCode = "greenOrb";
                break;
            case 3:
                orbCode = "yellowOrb";
                break;
            default:
                orbCode = "redOrb";
        }
        return orbCode;
    }

    private EquipmentTable GetAncientDGradeRandomEquipment() {
        List<EquipmentTable> equipmentTableList = gameDataTableService.EquipmentTable();
        List<EquipmentTable> ancientEquipmentList = equipmentTableList.stream().filter(i -> i.getGrade().equals("Ancient") && i.getGradeValue() == 1).collect(Collectors.toList());
        int index = (int) (Math.random() * 5);
        return ancientEquipmentList.get(index);
    }

    private List<RuneInfoTable> GetAllDivineDGradeRune() {
        List<RuneInfoTable> runeInfoTableList = gameDataTableService.RuneInfoTable();
        return runeInfoTableList.stream().filter(i -> i.getQualityNo() == 6 && i.getGradeNo() == 1).collect(Collectors.toList());
    }

    private List<EquipmentTable> GetAllDivineDGradeEquipment() {
        List<EquipmentTable> equipmentTableList = gameDataTableService.EquipmentTable();
        return equipmentTableList.stream().filter(i -> i.getGrade().equals("Divine") && i.getGradeValue() == 1).collect(Collectors.toList());
    }

    private void SendMail(Long userId, boolean purchase, String title, String gettingItem, String gettingItemCount, Map<String, Object> tempMap) {
        LocalDateTime now = LocalDateTime.now();
        MailSendRequestDto mailSendRequestDto = new MailSendRequestDto();
        mailSendRequestDto.setToId(userId);
        mailSendRequestDto.setSendDate(now);
        mailSendRequestDto.setMailType(purchase?1:0);
        mailSendRequestDto.setExpireDate(now.plusDays(30));
        mailSendRequestDto.setTitle(title);
        mailSendRequestDto.setGettingItem(gettingItem); //TODO 보상 테이블에 있는 보상으로 지급
        mailSendRequestDto.setGettingItemCount(gettingItemCount);
        myMailBoxService.SendMail(mailSendRequestDto, tempMap);
    }

    private boolean SpendPrice(User user, String currencyType, int price, String payLoad) throws IOException {
        boolean log = false;
        switch (currencyType) {
            case "arenaCoin":
                if (!user.SpendArenaCoin((long) price)) {
                    errorLoggingService.SetErrorLog(user.getId(), ResponseErrorCode.NEED_MORE_ARENACOIN.getIntegerValue(), "Fail! -> Cause: Need More ArenaCoin.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need More ArenaCoin.", ResponseErrorCode.NEED_MORE_ARENACOIN);
                }
                log = true;
                break;
            case "dragonCoin":
                if (!user.SpendDragonCoin((long) price)) {
                    errorLoggingService.SetErrorLog(user.getId(), ResponseErrorCode.NEED_MORE_DRAGONCOIN.getIntegerValue(), "Fail! -> Cause: Need More DragonCoin.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need More DragonCoin.", ResponseErrorCode.NEED_MORE_DRAGONCOIN);
                }
                log = true;
                break;
            case "cash":
                String test1 = payLoad.replace("Store", "store");
                String test2 = test1.replace("TransactionID", "transactionID");
                String test3 = test2.replace("Payload", "payload");
                IapResponseDto iapResponseDto = JsonStringHerlper.ReadValueFromJson(test3, IapResponseDto.class);
                IapResponseDto.PayLoad payload = JsonStringHerlper.ReadValueFromJson(iapResponseDto.getPayload(), IapResponseDto.PayLoad.class);
                String signature = payload.getSignature();
                String signedData = payload.getJson();
                IapResponseDto.SignedData json = JsonStringHerlper.ReadValueFromJson(signedData, IapResponseDto.SignedData.class);

                GooglePurchaseData googlePurchaseData = googlePurchaseDataRepository.findByOrderId(json.getOrderId()).orElse(null);
                if (googlePurchaseData == null) {
                    googlePurchaseData = GooglePurchaseData.builder().goodsId(json.getProductId()).signedData(signedData).signature(signature).transactionID(iapResponseDto.getTransactionID())
                            .consume(false).orderId(json.getOrderId()).useridUser(user.getId()).build();
                    googlePurchaseData = googlePurchaseDataRepository.save(googlePurchaseData);
                }
                if (googlePurchaseData.isConsume()) {
                    errorLoggingService.SetErrorLog(user.getId(), ResponseErrorCode.ALREADY_RECEIVED_ITEM.getIntegerValue(), "Fail! -> Cause: Already Received Item.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Already Received Item.", ResponseErrorCode.ALREADY_RECEIVED_ITEM);
                }

                if (!IapService.verifyPurchase(signedData, signature)){
                    errorLoggingService.SetErrorLog(user.getId(), ResponseErrorCode.NOT_VERIFIED_PURCHASE.getIntegerValue(), "Fail! -> Cause: Not Verified Purchase.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Not Verified Purchase.", ResponseErrorCode.NOT_VERIFIED_PURCHASE);
                }
                googlePurchaseData.Consume();
                break;
            case "mileage":
                if (!user.SpendMileage(price)) {
                    errorLoggingService.SetErrorLog(user.getId(), ResponseErrorCode.NEED_MORE_MILEAGE.getIntegerValue(), "Fail! -> Cause: Need More Mileage.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need More Mileage.", ResponseErrorCode.NEED_MORE_MILEAGE);
                }
                log = true;
                break;
        }
        return log;
    }
}
