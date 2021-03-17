package com.onlyonegames.eternalfantasia.domain.service.Iap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;
import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.EventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.MyEternalPassInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.GamePot.CouponItemsInfoJsonDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.GamePot.CouponWebHookDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.GamePot.IapWebHookDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.GiftItemDto.GiftItemDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.HeroEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Iap.IapTransactionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.BelongingInventoryLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CurrencyLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.EquipmentLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.GiftLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ReceiveItemCommonDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.MailSendRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.GotchaCharacterResponseDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ShopDto.MyShopItemsList;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyGiftInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.EternalPasses;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPass;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Iap.IapTransactionData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Iap.SeasonLimitPackageSchedule;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.ItemType;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.entity.Shop.MyShopInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyGiftInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.EternalPassesRepository;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Iap.IapTransactionDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Iap.SeasonLimitPackageScheduleRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.BelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.HeroEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.ItemTypeRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mail.MyMailBoxRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Shop.MyShopInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.EternalPass.MyEternalPassService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import com.onlyonegames.eternalfantasia.domain.service.Mail.MyMailBoxService;
import com.onlyonegames.eternalfantasia.etc.*;
import com.onlyonegames.util.MathHelper;
import com.onlyonegames.util.StringMaker;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class IapService {
    private final IapTransactionDataRepository iapTransactionDataRepository;
    private final UserRepository userRepository;
    private final ErrorLoggingService errorLoggingService;
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final LoggingService loggingService;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;
    private final GameDataTableService gameDataTableService;
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final ItemTypeRepository itemTypeRepository;
    private final MyGiftInventoryRepository myGiftInventoryRepository;
    private final HeroEquipmentInventoryRepository heroEquipmentInventoryRepository;
    private final MyCharactersRepository myCharactersRepository;
    private final MyShopInfoRepository myShopInfoRepository;
    private final MyEternalPassRepository myEternalPassRepository;
    private final EternalPassesRepository eternalPassesRepository;
    private final SeasonLimitPackageScheduleRepository seasonLimitPackageScheduleRepository;
    private final MyMailBoxService myMailBoxService;

    //이전에 체크
    public Map<String, Object> CheckAlreadyTransaction (Long userId, Map<String, Object> map) {
        IapTransactionData selectedIapTransactionData = null;
        List<IapTransactionData> iapTransactionDataList = iapTransactionDataRepository.findByUseridUserAndState(userId, 1);
        for(IapTransactionData iapTransactionData : iapTransactionDataList) {
            if(Strings.isNullOrEmpty(iapTransactionData.getTransactionId())){
                continue;
            }
            selectedIapTransactionData = iapTransactionData;
        }
        if(selectedIapTransactionData != null) {
            IapTransactionData iapTransactionData = iapTransactionDataList.get(0);
            map.put("alreadyTransaction", true);
            iapTransactionData.CompleteTransaction();
            return ApplyItem (iapTransactionData, map);
        }
        map.put("alreadyTransaction", false);
        return map;
    }

    public Map<String, Object> StartIapTransaction (Long userId, String productId, Map<String, Object> map) {

        //각 패키지 타입별로 구매 가능한 상황인지 체크
        List<IapTable> iapTableList = gameDataTableService.IapTableList();
        IapTable iapTable = iapTableList.stream().filter( a -> a.getProductId().equals(productId)).findAny().orElse(null);
        if(iapTable == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: IapTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: IapTable not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String packageType = iapTable.getType();
        String iapCode = iapTable.getCode();
        //다이아몬드 타입 패키지를 제외한 모든 패키지는 시즌 리셋 체크 및 이미 구매했는지 체크 필요.
        if(!packageType.equals("diamond")) {
            MyShopInfo myShopInfo = myShopInfoRepository.findByUseridUser(userId).orElse(null);
            if (myShopInfo == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find myShopMain.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Can't Find myShopMain.", ResponseErrorCode.NOT_FIND_DATA);
            }
            String json_myShopMainInfo = myShopInfo.getJson_myShopInfos();
            MyShopItemsList myShopItemsLis = JsonStringHerlper.ReadValueFromJson(myShopInfo.getJson_myShopInfos(), MyShopItemsList.class);
            if (myShopInfo.IsMyShopRecycleTime(86400/*총 1일에 대한 초*/)) {
                myShopItemsLis.ResetAllShop(userId, gameDataTableService.ShopTableList(), gameDataTableService.ArenaShopTableList(), gameDataTableService.GiftsTableList(), gameDataTableService.HeroEquipmentsTableList(), gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1), gameDataTableService.OptionsInfoTableList(), gameDataTableService.HerosTableList(), gameDataTableService.AncientShopTableList(), gameDataTableService.CurrencyShopTableList(), gameDataTableService.SpendableItemInfoTableList(), gameDataTableService.PieceShopTableList(), gameDataTableService.DarkObeShopTableList(), gameDataTableService.PassiveItemTableList());

                LocalDateTime now = LocalDateTime.now();
                List<SeasonLimitPackageSchedule> seasonLimitPackageScheduleList = seasonLimitPackageScheduleRepository.findAllBySeasonStartTimeBeforeAndSeasonEndTimeAfter(now, now);
                myShopItemsLis.ResetPackageSpecialInfos(gameDataTableService.PackageSpecialShopTableList(),seasonLimitPackageScheduleList, gameDataTableService.IapTableList());

                if(myShopInfo.IsDailyPackageRecycleTime()){
                    myShopItemsLis.ResetPackageDailyInfos(gameDataTableService.PackageDailyShopTableList(), gameDataTableService.IapTableList());
                }
                if(myShopInfo.IsWeeklyPackageRecycleTime()){
                    myShopItemsLis.ResetPackageWeeklyInfos(gameDataTableService.PackageWeeklyShopTableList(), gameDataTableService.IapTableList());
                }
                if(myShopInfo.IsMonthlyPackageRecycleTime()){
                    myShopItemsLis.ResetPackageMonthlyInfos(gameDataTableService.PackageMonthlyShopTableList(), gameDataTableService.IapTableList());
                }

                json_myShopMainInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsLis);
                myShopInfo.ResetMyShopInfos(json_myShopMainInfo);
                map.put("packageRecycle", true);
                map.put("myShopItemsLis", myShopItemsLis);
                map.put("scheduleStartTime", myShopInfo.getScheduleStartTime());

                map.put("dailyPackageScheduleStartTime", myShopInfo.getDailyPackageScheduleStartTime());
                map.put("weeklyPackageScheduleStartTime", myShopInfo.getWeeklyPackageScheduleStartTime());
                map.put("monthlyPackageScheduleStartTime", myShopInfo.getMonthlyPackageScheduleStartTime());
                return map;
            }
            switch (packageType){
                case "packagePerMonth" :
                    List<MyShopItemsList.PackagePerMonth> packagePerMonthList = myShopItemsLis.packageSpecialInfos.packagePerMonthList;
                    MyShopItemsList.PackagePerMonth packagePerMonth = packagePerMonthList.stream().filter(a -> a.iapCode.equals(iapCode)).findAny().orElse(null);
                    if(packagePerMonth == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: packagePerMonth not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: packagePerMonth not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    if(packagePerMonth.bought){
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_BOUGHT.getIntegerValue(), "Fail! -> Cause: Aleady bought", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Aleady bought", ResponseErrorCode.ALREADY_BOUGHT);
                    }
                    break;

                case "packageLimit" :
                    List<MyShopItemsList.LimitPackage> limitPackageList = myShopItemsLis.packageSpecialInfos.fixLimitPackageList;
                    MyShopItemsList.LimitPackage limitPackage = limitPackageList.stream().filter(a -> a.iapCode.equals(iapCode)).findAny().orElse(null);
                    if(limitPackage == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: limitPackage not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: limitPackage not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    if(limitPackage.bought && limitPackage.remainCount <= 0){
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_BOUGHT.getIntegerValue(), "Fail! -> Cause: Aleady bought and no remainCount", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Aleady bought and no remainCount", ResponseErrorCode.ALREADY_BOUGHT);
                    }
                    break;
                case "packageSeasonLimit" :
                    List<MyShopItemsList.SeasonLimitPackage> seasonLimitPackageList = myShopItemsLis.packageSpecialInfos.seasonLimitPackageList;
                    MyShopItemsList.SeasonLimitPackage seasonLimitPackage = seasonLimitPackageList.stream().filter(a -> a.iapCode.equals(iapCode)).findAny().orElse(null);
                    if(seasonLimitPackage == null)  {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: seasonLimitPackage not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: seasonLimitPackage not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    if(seasonLimitPackage.bought && seasonLimitPackage.remainCount <= 0){
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_BOUGHT.getIntegerValue(), "Fail! -> Cause: Aleady bought and no remainCount", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Aleady bought and no remainCount", ResponseErrorCode.ALREADY_BOUGHT);
                    }
                    break;
                case "packageDaily" :
                    List<MyShopItemsList.LimitPackage> packageDailyInfosList = myShopItemsLis.packageDailyInfos.packageList;
                    MyShopItemsList.LimitPackage dailyPackage = packageDailyInfosList.stream().filter(a -> a.iapCode.equals(iapCode)).findAny().orElse(null);
                    if(dailyPackage == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: dailyPackage not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: dailyPackage not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    if(dailyPackage.bought && dailyPackage.remainCount <= 0){
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_BOUGHT.getIntegerValue(), "Fail! -> Cause: Aleady bought and no remainCount", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Aleady bought and no remainCount", ResponseErrorCode.ALREADY_BOUGHT);
                    }
                    break;
                case "packageWeekly" :
                    List<MyShopItemsList.LimitPackage> packageWeeklyInfosList = myShopItemsLis.packageWeeklyInfos.packageList;
                    MyShopItemsList.LimitPackage weeklyPackage = packageWeeklyInfosList.stream().filter(a -> a.iapCode.equals(iapCode)).findAny().orElse(null);
                    if(weeklyPackage == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: weeklyPackage not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: weeklyPackage not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    if(weeklyPackage.bought && weeklyPackage.remainCount <= 0){
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_BOUGHT.getIntegerValue(), "Fail! -> Cause: Aleady bought and no remainCount", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Aleady bought and no remainCount", ResponseErrorCode.ALREADY_BOUGHT);
                    }
                    break;
                case "packageMonthly" :
                    List<MyShopItemsList.LimitPackage> packageMonthly = myShopItemsLis.packageMonthlyInfos.packageList;
                    MyShopItemsList.LimitPackage monthlyPackage = packageMonthly.stream().filter(a -> a.iapCode.equals(iapCode)).findAny().orElse(null);
                    if(monthlyPackage == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: monthlyPackage not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: monthlyPackage not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    if(monthlyPackage.bought && monthlyPackage.remainCount <= 0){
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_BOUGHT.getIntegerValue(), "Fail! -> Cause: Aleady bought and no remainCount", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Aleady bought and no remainCount", ResponseErrorCode.ALREADY_BOUGHT);
                    }
                    break;
                case "eternalPass" :
                    MyEternalPass myEternalPass = myEternalPassRepository.findByUseridUser(userId)
                            .orElseThrow(() -> new MyCustomException("Fail! -> Cause: MyEternalPass not find.", ResponseErrorCode.NOT_FIND_DATA));
                    String json_myEternalPassInfo = myEternalPass.getJson_myEternalPassInfo();
                    MyEternalPassInfoDto myEternalPassInfoDto = JsonStringHerlper.ReadValueFromJson(json_myEternalPassInfo, MyEternalPassInfoDto.class);
                    LocalDateTime now = LocalDateTime.now();
                    List<EternalPasses> eternalPassesList = eternalPassesRepository.findByPassStartTimeBeforeAndPassEndTimeAfter(now, now);
                    EternalPasses eternalPasses = null;
                    if(!eternalPassesList.isEmpty())
                        eternalPasses = eternalPassesList.get(0);
                    if(eternalPasses == null)
                        return map;

                    /*패스 업적 : 체크 준비*/
                    MyEternalPassMissionsData myEternalPassMissionsData = myEternalPassMissionsDataRepository.findByUseridUser(userId)
                            .orElse(null);
                    if(myEternalPassMissionsData == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: MyEternalPassMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    String jsonMyEternalPassMissionData = myEternalPassMissionsData.getJson_saveDataValue();
                    EventMissionDataDto myEternalPassMissionDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyEternalPassMissionData, EventMissionDataDto.class);
                    boolean changedEternalPassMissionsData = false;

                    if(MyEternalPassService.IsChangePassSeason(myEternalPass, eternalPassesList, myEternalPassInfoDto, myEternalPassMissionDataDto)) {
                        map.put("myEternalPassInfoDto", myEternalPassInfoDto);
                        map.put("eternalPasses", eternalPasses);
                        map.put("eternalPassSeasonChanged", true); //기존의 패스가 시간이 지나 새로운 패스로 바뀜

                        jsonMyEternalPassMissionData = JsonStringHerlper.WriteValueAsStringFromData(myEternalPassMissionDataDto);
                        myEternalPassMissionsData.ResetSaveDataValue(jsonMyEternalPassMissionData);
                        map.put("exchange_myEternalPassMissionsData", true);
                        map.put("myEternalPassMissionsDataDto", myEternalPassMissionDataDto);
                        map.put("myEternalPassLastDailyMissionClearTime", myEternalPassMissionsData.getLastDailyMissionClearTime());
                        map.put("myEternalPassLastWeeklyMissionClearTime", myEternalPassMissionsData.getLastWeeklyMissionClearTime());
                        return map;
                    }

                    if(myEternalPassInfoDto.isHasBuyRoyalPass()) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_BOUGHT.getIntegerValue(), "Fail! -> Cause: Aleady bought and no remainCount", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Aleady bought and no remainCount", ResponseErrorCode.ALREADY_BOUGHT);
                    }
                    break;
            }
        }

        IapTransactionDataDto iapTransactionDataDto = new IapTransactionDataDto();
        iapTransactionDataDto.setUseridUser(userId);
        iapTransactionDataDto.setProductId(productId);
        iapTransactionDataDto.setState(0);
        IapTransactionData iapTransactionData = iapTransactionDataDto.ToEntity();
        iapTransactionData = iapTransactionDataRepository.save(iapTransactionData);
        map.put("uniqueId", iapTransactionData.getId());
        return map;
    }

    /**
     * 게임팟으로부터 WebHook으로 온 응답. uniqueId로 트랜잭션 테이블 검색 해보고 있으면 데이터 넣고 리턴 없으면 0 리턴
     * */
    public int IapWebHookFromGamepot (IapWebHookDto iapWebHookDto, Map<String, Object> map) {
        String strUniqueId = iapWebHookDto.getUniqueId();
        Long uniqueId = Long.parseLong(strUniqueId);

        IapTransactionData iapTransactionData = iapTransactionDataRepository.findById(uniqueId).orElse(null);
        if(iapTransactionData == null){
            map.put("message","Can't Find gamepotOrderId");
            return 0;
        }
        iapTransactionData.SetWebHookData(iapWebHookDto.getTransactionId(), iapWebHookDto.getStore(), iapWebHookDto.getProjectId(), iapWebHookDto.getProductId(), iapWebHookDto.getPlatform(), iapWebHookDto.getPayment(), iapWebHookDto.getGamepotOrderId());

        return 1;
    }
    /**
     *  클라이언트가 게임팟의 응답후 최종 트랜잭션 완료 요청. uniqueId로 트랜잭션 체크하고 해당 상품에 대한 아이템 등록후 전송
     * */
    public Map<String, Object> CompleteIapTransaction (Long userId, Long uniqueId, Map<String, Object> map) {

        IapTransactionData iapTransactionData = iapTransactionDataRepository.findById(uniqueId).orElseThrow(() -> new MyCustomException("Fail! -> Cause: IapTransactionId not find.", ResponseErrorCode.NOT_FIND_DATA));
//        if(iapTransactionData.getState() > 1){
//            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_COMPLETE_TRANSACTION.getIntegerValue(), "Fail! -> Cause: Already complete transaction.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
//            throw new MyCustomException("Fail! -> Cause: Already complete transaction.", ResponseErrorCode.ALREADY_COMPLETE_TRANSACTION);
//        }

        //아래 상품들은 각각 무료
        if(iapTransactionData.getProductId().equals("com.skyent.eternalfantasia.dailypack1")) {
            iapTransactionData.CompleteTransaction();
            return ApplyItem (iapTransactionData, map);
        }
        else if(iapTransactionData.getProductId().equals("com.skyent.eternalfantasia.weeklypack1")) {
            iapTransactionData.CompleteTransaction();
            return ApplyItem (iapTransactionData, map);
        }
        else if(iapTransactionData.getProductId().equals("com.skyent.eternalfantasia.monthlypack1")) {
            iapTransactionData.CompleteTransaction();
            return ApplyItem (iapTransactionData, map);
        }
//        else if(Strings.isNullOrEmpty(iapTransactionData.getTransactionId())) {
//            throw new MyCustomException("Fail! -> Cause: Can't Find TransactionId.", ResponseErrorCode.WRONG_TRANSACTIONID);
//        }

        List<IapTable> iapTableList = gameDataTableService.IapTableList();

        String prevProductId = iapTransactionData.getProductId();

        if (prevProductId.equals("com.skyent.eternalfantasia.monthlycard"))
            prevProductId = "com.skyent.eternalfantasia.monthly";
        String productId = prevProductId;

        IapTable iapTable = iapTableList.stream().filter( a -> a.getProductId().equals(productId)).findAny().orElse(null);
        if(iapTable == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: IapTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: IapTable not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
        }
        boolean previousFirstBuy = user.isFirstBuy();
        user.BuyFirst();
        boolean afterFirstBuy = user.isFirstBuy();
        if(previousFirstBuy != afterFirstBuy) {
            //최초 결제 치치 보내기
            List<MailSendRequestDto.Item> itemList = new ArrayList<>();
            MailSendRequestDto.Item item = new MailSendRequestDto.Item();

            item.setItem("cr_024", 1);
            itemList.add(item);

            MailSendRequestDto mailSendRequestDto = new MailSendRequestDto();
            mailSendRequestDto.setToId(userId);
            mailSendRequestDto.setFromId(10000L);
            mailSendRequestDto.setTitle("첫 결제 감사 선물");
            mailSendRequestDto.setContent("첫 결제 보상 치치");
            mailSendRequestDto.setExpireDate(LocalDateTime.now().plusYears(1));
            mailSendRequestDto.setItemList(itemList);
            mailSendRequestDto.setMailType(2);
            myMailBoxService.SendMail(mailSendRequestDto, map);
        }
        user.PurchaseSum(iapTable.getCost());


        iapTransactionData.CompleteTransaction();
        map.put("user", user);
        return ApplyItem (iapTransactionData, map);
    }


    Map<String, Object> ApplyItem(IapTransactionData iapTransactionData, Map<String, Object> map) {

        Long userId = iapTransactionData.getUseridUser();

        String prevProductId = iapTransactionData.getProductId();

        if (prevProductId.equals("com.skyent.eternalfantasia.monthlycard"))
            prevProductId = "com.skyent.eternalfantasia.monthly";

        String productId = prevProductId;

        List<IapTable> iapTableList = gameDataTableService.IapTableList();
        IapTable iapTable = iapTableList.stream().filter( a -> a.getProductId().equals(productId)).findAny().orElse(null);
        if(iapTable == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: IapTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: IapTable not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String packageType = iapTable.getType();
        String iapCode = iapTable.getCode();
        if(!packageType.equals("diamond") && !packageType.equals("eternalPass")) {
            MyShopInfo myShopInfo = myShopInfoRepository.findByUseridUser(userId).orElse(null);
            if (myShopInfo == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find myShopMain.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Can't Find myShopMain.", ResponseErrorCode.NOT_FIND_DATA);
            }
            String json_myShopInfo = myShopInfo.getJson_myShopInfos();
            MyShopItemsList myShopItemsLis = JsonStringHerlper.ReadValueFromJson(json_myShopInfo, MyShopItemsList.class);

            switch (packageType){
                case "packagePerMonth" :
                    List<MyShopItemsList.PackagePerMonth> packagePerMonthList = myShopItemsLis.packageSpecialInfos.packagePerMonthList;
                    MyShopItemsList.PackagePerMonth packagePerMonth = packagePerMonthList.stream().filter(a -> a.iapCode.equals(iapCode)).findAny().orElse(null);
                    if(packagePerMonth == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: packagePerMonth not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: packagePerMonth not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    //MyNewFieldSaveDataService.gettingPackageItem(userId, myShopInfo, iapTableList, myMailBoxService, errorLoggingService, map);
                    packagePerMonth.bought = true;
                    String[] receiveItemList = iapTable.getGettingItems().split(",");
                    String[] receiveItemInfo = receiveItemList[1].split(":");
                    String getingItem = receiveItemInfo[0];
                    int getttingCount = Integer.parseInt(receiveItemInfo[1]);
                    List<MailSendRequestDto.Item> itemList = new ArrayList<>();
                    MailSendRequestDto.Item item = new MailSendRequestDto.Item();
                    item.setItem(getingItem, getttingCount);
                    itemList.add(item);
                    StringMaker.Clear();
                    StringMaker.stringBuilder.append(iapTable.getName());
                    StringMaker.stringBuilder.append(" ");
                    StringMaker.stringBuilder.append("1회차 아이템 지급");
                    String content = StringMaker.stringBuilder.toString();
                    MailSendRequestDto mailSendRequestDto = new MailSendRequestDto();
                    mailSendRequestDto.setToId(userId);
                    mailSendRequestDto.setFromId(10000L);
                    mailSendRequestDto.setTitle(content);
                    mailSendRequestDto.setContent(SystemMailInfos.SHOP_PURCHASE_TITLE);
                    mailSendRequestDto.setExpireDate(LocalDateTime.now().plusYears(1));
                    mailSendRequestDto.setItemList(itemList);
                    mailSendRequestDto.setMailType(1);
                    myMailBoxService.SendMail(mailSendRequestDto, map);
                    packagePerMonth.remainCount--;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime gettingTime = LocalDateTime.of(LocalDateTime.now().minusHours(5).toLocalDate(), LocalTime.of(5,0,0));
                    packagePerMonth.lastGettingTime = gettingTime.format(formatter);
                    break;
                case "packageLimit" :
                    List<MyShopItemsList.LimitPackage> limitPackageList = myShopItemsLis.packageSpecialInfos.fixLimitPackageList;
                    MyShopItemsList.LimitPackage limitPackage = limitPackageList.stream().filter(a -> a.iapCode.equals(iapCode)).findAny().orElse(null);
                    if(limitPackage == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: limitPackage not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: limitPackage not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    limitPackage.remainCount--;
                    if(limitPackage.remainCount <= 0) {
                        limitPackage.bought = true;
                    }
                    break;
                case "packageSeasonLimit" :
                    List<MyShopItemsList.SeasonLimitPackage> seasonLimitPackageList = myShopItemsLis.packageSpecialInfos.seasonLimitPackageList;
                    MyShopItemsList.SeasonLimitPackage seasonLimitPackage = seasonLimitPackageList.stream().filter(a -> a.iapCode.equals(iapCode)).findAny().orElse(null);
                    if(seasonLimitPackage == null)  {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: seasonLimitPackage not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: seasonLimitPackage not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    seasonLimitPackage.remainCount--;
                    if(seasonLimitPackage.remainCount <= 0)
                        seasonLimitPackage.bought = true;
                    break;
                case "packageDaily" :
                    List<MyShopItemsList.LimitPackage> packageDailyInfosList = myShopItemsLis.packageDailyInfos.packageList;
                    MyShopItemsList.LimitPackage dailyPackage = packageDailyInfosList.stream().filter(a -> a.iapCode.equals(iapCode)).findAny().orElse(null);
                    if(dailyPackage == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: dailyPackage not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: dailyPackage not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    dailyPackage.remainCount--;
                    if(dailyPackage.remainCount <= 0)
                        dailyPackage.bought = true;
                    break;
                case "packageWeekly" :
                    List<MyShopItemsList.LimitPackage> packageWeeklyInfosList = myShopItemsLis.packageWeeklyInfos.packageList;
                    MyShopItemsList.LimitPackage weeklyPackage = packageWeeklyInfosList.stream().filter(a -> a.iapCode.equals(iapCode)).findAny().orElse(null);
                    if(weeklyPackage == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: weeklyPackage not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: weeklyPackage not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    weeklyPackage.remainCount--;
                    if(weeklyPackage.remainCount <= 0)
                        weeklyPackage.bought = true;
                    break;
                case "packageMonthly" :
                    List<MyShopItemsList.LimitPackage> packageMonthly = myShopItemsLis.packageMonthlyInfos.packageList;
                    MyShopItemsList.LimitPackage monthlyPackage = packageMonthly.stream().filter(a -> a.iapCode.equals(iapCode)).findAny().orElse(null);
                    if(monthlyPackage == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: monthlyPackage not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: monthlyPackage not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }

                    monthlyPackage.remainCount--;
                    if(monthlyPackage.remainCount <= 0)
                        monthlyPackage.bought = true;

                    break;
            }
            json_myShopInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsLis);
            myShopInfo.ResetMyShopInfos(json_myShopInfo);
            map.put("isPackage", true);
            map.put("myShopItemsLis", myShopItemsLis);
            map.put("scheduleStartTime", myShopInfo.getScheduleStartTime());
            map.put("dailyPackageScheduleStartTime", myShopInfo.getDailyPackageScheduleStartTime());
            map.put("weeklyPackageScheduleStartTime", myShopInfo.getWeeklyPackageScheduleStartTime());
            map.put("monthlyPackageScheduleStartTime", myShopInfo.getMonthlyPackageScheduleStartTime());
        }
        else if(packageType.equals("eternalPass")) {
            MyEternalPass myEternalPass = myEternalPassRepository.findByUseridUser(userId)
                    .orElseThrow(() -> new MyCustomException("Fail! -> Cause: MyEternalPass not find.", ResponseErrorCode.NOT_FIND_DATA));
            String json_myEternalPassInfo = myEternalPass.getJson_myEternalPassInfo();
            MyEternalPassInfoDto myEternalPassInfoDto = JsonStringHerlper.ReadValueFromJson(json_myEternalPassInfo, MyEternalPassInfoDto.class);
            myEternalPassInfoDto.BuyRoyalPass();
            json_myEternalPassInfo = JsonStringHerlper.WriteValueAsStringFromData(myEternalPassInfoDto);
            myEternalPass.ResetJsonMyEternalPassInfo(json_myEternalPassInfo);
            map.put("buyEternalPass", true);
            map.put("myEternalPassInfoDto", myEternalPassInfoDto);
            return map;
        }
        String[] rewardItemsArray = iapTable.getGettingItems().split(",");
        StringMaker.Clear();
        int count = rewardItemsArray.length;
        //월정액 상품은 rewardItemsArray에 첫번째 상품만 곧바로 지급되고, 두번째 상품은 매일 한번씩 우편으로 지급된다.
        if(packageType.equals("packagePerMonth"))
            count = 1;
        for(int i = 0; i < count; i++) {
            if (i > 0)
                StringMaker.stringBuilder.append(",");
            String item = rewardItemsArray[i].split(":")[0];
            StringMaker.stringBuilder.append(item);
        }
        String gettingItems = StringMaker.stringBuilder.toString();
        StringMaker.Clear();
        for(int i = 0; i < count; i++) {
            if (i > 0)
                StringMaker.stringBuilder.append(",");
            String item = rewardItemsArray[i].split(":")[1];
            StringMaker.stringBuilder.append(item);
        }
        String gettingCounts = StringMaker.stringBuilder.toString();

        /*패스 업적 : 체크 준비*/
        MyEternalPassMissionsData myEternalPassMissionsData = myEternalPassMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myEternalPassMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEternalPassMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyEternalPassMissionData = myEternalPassMissionsData.getJson_saveDataValue();
        EventMissionDataDto myEternalPassMissionDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyEternalPassMissionData, EventMissionDataDto.class);
        boolean changedEternalPassMissionsData = false;

        if(gettingItems.contains("gold") || gettingItems.contains("Gold")) {
            int totalGoldGettingCount = 0;
            String[] gettingItemsArray = gettingItems.split(",");
            String[] itemsCountArray = gettingCounts.split(",");
            int gettingItemsCount = gettingItemsArray.length;
            for(int i = 0; i < gettingItemsCount; i++) {
                String gettingItemCode = gettingItemsArray[i];

                if(gettingItemCode.equals("gold") || gettingItemCode.equals("Gold")) {
                    int gettingCount = Integer.parseInt(itemsCountArray[i]);
                    totalGoldGettingCount += gettingCount;
                }
            }
            /* 패스 업적 : 골드획득*/
            changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.ERNING_GOLD.name(),"empty", totalGoldGettingCount, gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
        }
        ReceiveItemCommonDto receiveItemCommonDto = new ReceiveItemCommonDto();
        MyMissionsData myMissionsData = null;
        /*업적 : 체크 준비*/
        if(myMissionsData == null)
            myMissionsData = myMissionsDataRepository.findByUseridUser(userId).orElseThrow(() -> new MyCustomException("Fail! -> Cause: MyMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA));
        String jsonMyMissionData = myMissionsData.getJson_saveDataValue();
        MissionsDataDto myMissionsDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyMissionData, MissionsDataDto.class);
        boolean changedMissionsData = false;

        StringMaker.Clear();
        StringMaker.stringBuilder.append("Iap => productId ");
        StringMaker.stringBuilder.append(iapTransactionData.getProductId());
        StringMaker.stringBuilder.append(" 구매 ");
        String logWorkingPosition = StringMaker.stringBuilder.toString();

        changedMissionsData = receiveItem(userId, gettingItems, gettingCounts, receiveItemCommonDto, myMissionsDataDto, belongingInventoryRepository,
                gameDataTableService, itemTypeRepository, loggingService, myGiftInventoryRepository, heroEquipmentInventoryRepository, myCharactersRepository, logWorkingPosition) || changedMissionsData;

        map.put("receiveItemCommonDto", receiveItemCommonDto);
        /* 패스 업적 : 미션 데이터 변경점 적용*/
        if(changedEternalPassMissionsData) {
            jsonMyEternalPassMissionData = JsonStringHerlper.WriteValueAsStringFromData(myEternalPassMissionDataDto);
            myEternalPassMissionsData.ResetSaveDataValue(jsonMyEternalPassMissionData);
            map.put("exchange_myEternalPassMissionsData", true);
            map.put("myEternalPassMissionsDataDto", myEternalPassMissionDataDto);
            map.put("myEternalPassLastDailyMissionClearTime", myEternalPassMissionsData.getLastDailyMissionClearTime());
            map.put("myEternalPassLastWeeklyMissionClearTime", myEternalPassMissionsData.getLastWeeklyMissionClearTime());
        }

        /*업적 : 미션 데이터 변경점 적용*/
        if(changedMissionsData) {
            jsonMyMissionData = JsonStringHerlper.WriteValueAsStringFromData(myMissionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMyMissionData);
            map.put("exchange_myMissionsData", true);
            map.put("myMissionsDataDto", myMissionsDataDto);
            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());
        }
        return map;
    }

    private boolean receiveItem(Long userId, String gettingItems, String itemsCounts, ReceiveItemCommonDto receiveItemCommonDto, MissionsDataDto myMissionsDataDto,
                                BelongingInventoryRepository belongingInventoryRepository, GameDataTableService gameDataTableService, ItemTypeRepository itemTypeRepository,
                                LoggingService loggingService, MyGiftInventoryRepository myGiftInventoryRepository, HeroEquipmentInventoryRepository heroEquipmentInventoryRepository,
                                MyCharactersRepository myCharactersRepository, String logWorkingPosition
    ) {

        List<BelongingCharacterPieceTable> orignalBelongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
        List<BelongingCharacterPieceTable> copyBelongingCharacterPieceTableList = new ArrayList<>();
        for(BelongingCharacterPieceTable characterPieceTable : orignalBelongingCharacterPieceTableList) {
            if(characterPieceTable.getCode().equals("characterPieceAll"))
                continue;
            copyBelongingCharacterPieceTableList.add(characterPieceTable);
        }

        List<BelongingInventory> belongingInventoryList = null;
        List<ItemType> itemTypeList = null;
        ItemType spendAbleItemType = null;
        ItemType materialItemType = null;
        User user = null;
        MyGiftInventory myGiftInventory = null;
        List<MyCharacters> myCharactersList = null;
        List<HeroEquipmentInventory> heroEquipmentInventoryList = null;

        boolean changedMissionsData = false;

        String[] gettingItemsArray = gettingItems.split(",");
        String[] itemsCountArray = itemsCounts.split(",");
        int gettingItemsCount = gettingItemsArray.length;
        for(int i = 0; i < gettingItemsCount; i++) {
            String gettingItemCode = gettingItemsArray[i];
            int gettingCount = Integer.parseInt(itemsCountArray[i]);

            //피로도 50 회복 물약, 즉시 제작권, 차원석, 강화석, 재련석, 링크웨폰키, 코스튬 무료 티켓
            if(gettingItemCode.equals("recovery_fatigability") || gettingItemCode.equals("ticket_direct_production_equipment")
                    || gettingItemCode.equals("dimensionStone") || gettingItemCode.contains("enchant") || gettingItemCode.contains("resmelt")
                    || gettingItemCode.equals("linkweapon_bronzeKey") || gettingItemCode.equals("linkweapon_silverKey")
                    || gettingItemCode.equals("linkweapon_goldKey") || gettingItemCode.equals("costume_ticket")) {
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                if(belongingInventoryList == null)
                    belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
                if(itemTypeList == null)
                    itemTypeList = itemTypeRepository.findAll();
                if(spendAbleItemType == null)
                    spendAbleItemType = itemTypeList.stream()
                            .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                            .findAny()
                            .orElse(null);
                List<BelongingInventoryDto> receivedSpendableItemList = receiveItemCommonDto.getChangedBelongingInventoryList();
                BelongingInventoryDto belongingInventoryDto = ApplySomeReward.ApplySpendableItem(userId, gettingItemCode, gettingCount, belongingInventoryRepository, belongingInventoryList, spendableItemInfoTableList, spendAbleItemType, logWorkingPosition, loggingService, errorLoggingService);
                int itemId = belongingInventoryDto.getItemId();
                Long itemTypeId = belongingInventoryDto.getItemType().getId();
                BelongingInventoryDto findBelongingInventoryDto = receivedSpendableItemList.stream().filter(a -> a.getItemId()==itemId && a.getItemType().getId().equals(itemTypeId))
                        .findAny()
                        .orElse(null);
                if(findBelongingInventoryDto == null) {
                    receivedSpendableItemList.add(belongingInventoryDto);
                }
                else {
                    findBelongingInventoryDto.AddCount(gettingCount);
                }
            }
            //골드
            else if(gettingItemCode.equals("gold")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousCount = user.getGold();
                user.AddGold(gettingCount);
                currencyLogDto.setCurrencyLogDto(logWorkingPosition, "골드", previousCount, gettingCount, user.getGold());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                receiveItemCommonDto.AddGettingGold(gettingCount);
            }
            //다이아
            else if(gettingItemCode.equals("diamond")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousCount = user.getDiamond();
                user.AddDiamond(gettingCount);
                currencyLogDto.setCurrencyLogDto(logWorkingPosition, "다이아", previousCount, gettingCount, user.getDiamond());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                receiveItemCommonDto.AddGettingDiamond(gettingCount);
            }
            //링크 포인트
            else if(gettingItemCode.equals("linkPoint")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousCount = user.getLinkforcePoint();
                user.AddLinkforcePoint(gettingCount);
                currencyLogDto.setCurrencyLogDto(logWorkingPosition, "링크포인트", previousCount, gettingCount, user.getLinkforcePoint());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                receiveItemCommonDto.AddGettingLinkPoint(gettingCount);
            }
            //아레나 코인
            else if(gettingItemCode.equals("arenaCoin")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousCount = user.getArenaCoin();
                user.AddArenaCoin(gettingCount);
                currencyLogDto.setCurrencyLogDto(logWorkingPosition, "아레나 코인", previousCount, gettingCount, user.getArenaCoin());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                receiveItemCommonDto.AddGettingArenaCoin(gettingCount);
            }
            //아레나 티켓
            else if(gettingItemCode.equals("arenaTicket")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousCount = user.getArenaTicket();
                user.AddArenaTicket(gettingCount);
                currencyLogDto.setCurrencyLogDto(logWorkingPosition, "아레나 티켓", previousCount, gettingCount, user.getArenaTicket());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                receiveItemCommonDto.AddGettingArenaTicket(gettingCount);
            }
            else if(gettingItemCode.equals("lowDragonScale")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousCount = user.getLowDragonScale();
                user.AddLowDragonScale(gettingCount);
                currencyLogDto.setCurrencyLogDto(logWorkingPosition, "용의 비늘(전설)", previousCount, gettingCount, user.getLowDragonScale());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                receiveItemCommonDto.AddGettingLowDragonScale(gettingCount);
            }
            else if(gettingItemCode.equals("middleDragonScale")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousCount = user.getMiddleDragonScale();
                user.AddMiddleDragonScale(gettingCount);
                currencyLogDto.setCurrencyLogDto(logWorkingPosition, "용의 비늘(신성)", previousCount, gettingCount, user.getMiddleDragonScale());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                receiveItemCommonDto.AddGettingMiddleDragonScale(gettingCount);
            }
            else if(gettingItemCode.equals("highDragonScale")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousCount = user.getHighDragonScale();
                user.AddHighDragonScale(gettingCount);
                currencyLogDto.setCurrencyLogDto(logWorkingPosition, "용의 비늘(고대)", previousCount, gettingCount, user.getHighDragonScale());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                receiveItemCommonDto.AddGettingHighDragonScale(gettingCount);
            }
            /*3종, 5종, 8종 재료 상자*/
            else if(gettingItemCode.equals("reward_material_low") || gettingItemCode.equals("reward_material_middle") || gettingItemCode.equals("reward_material_high")) {
                int kindCount = 0;
                if (gettingItemCode.contains("low")) {
                    //3종
                    kindCount = 3;
                } else if (gettingItemCode.contains("middle")) {
                    //5종
                    kindCount = 5;
                }
                else if (gettingItemCode.contains("high")) {
                    //8종
                    kindCount = 8;
                }
                List<BelongingInventoryDto> receivedSpendableItemList = receiveItemCommonDto.getChangedBelongingInventoryList();
                if(belongingInventoryList == null)
                    belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList = gameDataTableService.EquipmentMaterialInfoTableList();
                if(itemTypeList == null)
                    itemTypeList = itemTypeRepository.findAll();
                if(materialItemType == null) {
                    materialItemType = itemTypeList.stream()
                            .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Material)
                            .findAny()
                            .orElse(null);
                }
                List<EquipmentMaterialInfoTable> copyEquipmentMaterialInfoTableList = new ArrayList<>();
                copyEquipmentMaterialInfoTableList.addAll(equipmentMaterialInfoTableList);
                int addIndex = 0;
                while (addIndex < kindCount) {
                    int selectedRandomIndex = (int) MathHelper.Range(0, copyEquipmentMaterialInfoTableList.size());
                    EquipmentMaterialInfoTable equipmentMaterialInfoTable = copyEquipmentMaterialInfoTableList.get(selectedRandomIndex);
                    BelongingInventoryDto belongingInventoryDto = ApplySomeReward.AddEquipmentMaterialItem(equipmentMaterialInfoTable.getCode(), gettingCount, belongingInventoryRepository, itemTypeList, belongingInventoryList, equipmentMaterialInfoTableList, materialItemType, logWorkingPosition, userId, loggingService, errorLoggingService);
                    int itemId = belongingInventoryDto.getItemId();
                    Long itemTypeId = belongingInventoryDto.getItemType().getId();
                    BelongingInventoryDto findBelongingInventoryDto = receivedSpendableItemList.stream().filter(a -> a.getItemId()==itemId && a.getItemType().getId().equals(itemTypeId))
                            .findAny()
                            .orElse(null);
                    if(findBelongingInventoryDto == null) {
                        receivedSpendableItemList.add(belongingInventoryDto);
                    }
                    else {
                        findBelongingInventoryDto.AddCount(gettingCount);
                    }
                    copyEquipmentMaterialInfoTableList.remove(selectedRandomIndex);
                    addIndex++;
                }
            }
            /*특정 재료*/
            else if(gettingItemCode.contains("material")){
                List<BelongingInventoryDto> receivedSpendableItemList = receiveItemCommonDto.getChangedBelongingInventoryList();
                if(belongingInventoryList == null)
                    belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList = gameDataTableService.EquipmentMaterialInfoTableList();
                if(itemTypeList == null)
                    itemTypeList = itemTypeRepository.findAll();
                if(materialItemType == null) {
                    materialItemType = itemTypeList.stream()
                            .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Material)
                            .findAny()
                            .orElse(null);
                }
                BelongingInventoryDto belongingInventoryDto = ApplySomeReward.AddEquipmentMaterialItem(gettingItemCode, gettingCount, belongingInventoryRepository, itemTypeList, belongingInventoryList, equipmentMaterialInfoTableList, materialItemType, logWorkingPosition, userId, loggingService, errorLoggingService);
                int itemId = belongingInventoryDto.getItemId();
                Long itemTypeId = belongingInventoryDto.getItemType().getId();
                BelongingInventoryDto findBelongingInventoryDto = receivedSpendableItemList.stream().filter(a -> a.getItemId()==itemId && a.getItemType().getId().equals(itemTypeId))
                        .findAny()
                        .orElse(null);
                if(findBelongingInventoryDto == null) {
                    receivedSpendableItemList.add(belongingInventoryDto);
                }
                else {
                    findBelongingInventoryDto.AddCount(gettingCount);
                }
            }
            /*모든 선물중 하나*/
            else if(gettingItemCode.equals("giftAll")) {
                List<GiftTable> giftTableList = gameDataTableService.GiftsTableList();
                List<GiftTable> copyGiftTableList = new ArrayList<>(giftTableList);
                copyGiftTableList.remove(25);
                int randIndex = (int) MathHelper.Range(0, copyGiftTableList.size());
                GiftTable giftTable = copyGiftTableList.get(randIndex);
                if(myGiftInventory == null) {
                    myGiftInventory = myGiftInventoryRepository.findByUseridUser(userId)
                            .orElse(null);
                    if(myGiftInventory == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyGiftInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: MyGiftInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                List<GiftItemDtosList.GiftItemDto> changedMyGiftInventoryList = receiveItemCommonDto.getChangedMyGiftInventoryList();
                String inventoryInfosString = myGiftInventory.getInventoryInfos();
                GiftItemDtosList giftItemInventorys = JsonStringHerlper.ReadValueFromJson(inventoryInfosString, GiftItemDtosList.class);

                GiftItemDtosList.GiftItemDto inventoryItemDto = giftItemInventorys.giftItemDtoList.stream()
                        .filter(a -> a.code.equals(giftTable.getCode()))
                        .findAny()
                        .orElse(null);
                if(inventoryItemDto == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find GiftTable", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail -> Cause: Can't Find GiftTable", ResponseErrorCode.NOT_FIND_DATA);
                }
                GiftLogDto giftLogDto = new GiftLogDto();
                giftLogDto.setPreviousValue(inventoryItemDto.count);
                inventoryItemDto.AddItem(gettingCount ,giftTable.getStackLimit());
                giftLogDto.setGiftLogDto(logWorkingPosition, inventoryItemDto.code,gettingCount, inventoryItemDto.count);
                String log = JsonStringHerlper.WriteValueAsStringFromData(giftLogDto);
                loggingService.setLogging(userId, 4, log);
                inventoryInfosString = JsonStringHerlper.WriteValueAsStringFromData(giftItemInventorys);
                myGiftInventory.ResetInventoryInfos(inventoryInfosString);

                GiftItemDtosList.GiftItemDto responseItemDto = new GiftItemDtosList.GiftItemDto();
                responseItemDto.code = inventoryItemDto.code;
                responseItemDto.count = gettingCount;
                changedMyGiftInventoryList.add(responseItemDto);
            }
            /*특정 선물*/
            else if(gettingItemCode.contains("gift_")) {
                List<GiftTable> giftTableList = gameDataTableService.GiftsTableList();
                GiftTable giftTable = giftTableList.stream().filter(a -> a.getCode().equals(gettingItemCode)).findAny().orElse(null);
                if(giftTable == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: GiftTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: GiftTable not find.", ResponseErrorCode.NOT_FIND_DATA);
                }
                if(myGiftInventory == null) {
                    myGiftInventory = myGiftInventoryRepository.findByUseridUser(userId)
                            .orElse(null);
                    if(myGiftInventory == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyGiftInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: MyGiftInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                List<GiftItemDtosList.GiftItemDto> changedMyGiftInventoryList = receiveItemCommonDto.getChangedMyGiftInventoryList();
                String inventoryInfosString = myGiftInventory.getInventoryInfos();
                GiftItemDtosList giftItemInventorys = JsonStringHerlper.ReadValueFromJson(inventoryInfosString, GiftItemDtosList.class);

                GiftItemDtosList.GiftItemDto inventoryItemDto = giftItemInventorys.giftItemDtoList.stream()
                        .filter(a -> a.code.equals(giftTable.getCode()))
                        .findAny()
                        .orElse(null);
                if(inventoryItemDto == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find GiftTable", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail -> Cause: Can't Find GiftTable", ResponseErrorCode.NOT_FIND_DATA);
                }
                GiftLogDto giftLogDto = new GiftLogDto();
                giftLogDto.setPreviousValue(inventoryItemDto.count);

                inventoryItemDto.AddItem(gettingCount ,giftTable.getStackLimit());
                giftLogDto.setGiftLogDto(logWorkingPosition, inventoryItemDto.code,gettingCount, inventoryItemDto.count);
                String log = JsonStringHerlper.WriteValueAsStringFromData(giftLogDto);
                loggingService.setLogging(userId, 4, log);
                inventoryInfosString = JsonStringHerlper.WriteValueAsStringFromData(giftItemInventorys);
                myGiftInventory.ResetInventoryInfos(inventoryInfosString);

                GiftItemDtosList.GiftItemDto responseItemDto = new GiftItemDtosList.GiftItemDto();
                responseItemDto.code = inventoryItemDto.code;
                responseItemDto.count = gettingCount;
                changedMyGiftInventoryList.add(responseItemDto);
            }
            /*모든 케릭터 조각중 하나*/
            else if(gettingItemCode.equals("characterPieceAll")) {
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                //List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
                int randIndex = (int) MathHelper.Range(0, copyBelongingCharacterPieceTableList.size());
                BelongingCharacterPieceTable selectedCharacterPiece = copyBelongingCharacterPieceTableList.get(randIndex);
                List<BelongingInventoryDto> changedCharacterPieceList = receiveItemCommonDto.getChangedBelongingInventoryList();
                if(itemTypeList == null)
                    itemTypeList = itemTypeRepository.findAll();
                ItemType belongingCharacterPieceItemType = itemTypeList.stream()
                        .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece)
                        .findAny()
                        .orElse(null);
                if(belongingInventoryList == null)
                    belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                BelongingInventory myCharacterPieceItem = belongingInventoryList.stream()
                        .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece && a.getItemId() == selectedCharacterPiece.getId())
                        .findAny()
                        .orElse(null);

                if(myCharacterPieceItem == null) {
                    belongingInventoryLogDto.setPreviousValue(0);
                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.setUseridUser(userId);
                    belongingInventoryDto.setItemId(selectedCharacterPiece.getId());
                    belongingInventoryDto.setCount(gettingCount);
                    belongingInventoryDto.setItemType(belongingCharacterPieceItemType);
                    myCharacterPieceItem = belongingInventoryDto.ToEntity();
                    myCharacterPieceItem = belongingInventoryRepository.save(myCharacterPieceItem);
                    belongingInventoryLogDto.setBelongingInventoryLogDto(logWorkingPosition, myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
                    String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                    loggingService.setLogging(userId, 3, log);
                    belongingInventoryList.add(myCharacterPieceItem);
                    belongingInventoryDto.setId(myCharacterPieceItem.getId());
                    changedCharacterPieceList.add(belongingInventoryDto);
                }
                else {
                    belongingInventoryLogDto.setPreviousValue(myCharacterPieceItem.getCount());
                    myCharacterPieceItem.AddItem(gettingCount, selectedCharacterPiece.getStackLimit());
                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.setUseridUser(userId);
                    belongingInventoryDto.setItemId(selectedCharacterPiece.getId());
                    belongingInventoryDto.setCount(gettingCount);
                    belongingInventoryDto.setItemType(belongingCharacterPieceItemType);
                    belongingInventoryLogDto.setBelongingInventoryLogDto(logWorkingPosition, myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
                    String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                    loggingService.setLogging(userId, 3, log);
                    belongingInventoryDto.setId(myCharacterPieceItem.getId());
                    changedCharacterPieceList.add(belongingInventoryDto);
                }
            }
            /*특정 케릭터 조각*/
            else if(gettingItemCode.contains("characterPiece")) {
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                List<BelongingInventoryDto> changedCharacterPieceList = receiveItemCommonDto.getChangedBelongingInventoryList();
                String characterCode = gettingItemCode;//.replace("characterPiece_", "");
                if(itemTypeList == null)
                    itemTypeList = itemTypeRepository.findAll();
                ItemType belongingCharacterPieceItemType = itemTypeList.stream()
                        .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece)
                        .findAny()
                        .orElse(null);

                List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
                BelongingCharacterPieceTable characterPiece = belongingCharacterPieceTableList.stream()
                        .filter(a -> a.getCode().equals(characterCode))
                        .findAny()
                        .orElse(null);
                if(characterPiece == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find characterPiece.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't Find characterPiece.", ResponseErrorCode.NOT_FIND_DATA);
                }
                if(belongingInventoryList == null)
                    belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                BelongingInventory myCharacterPieceItem = belongingInventoryList.stream()
                        .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece && a.getItemId() == characterPiece.getId())
                        .findAny()
                        .orElse(null);

                if(myCharacterPieceItem == null) {
                    belongingInventoryLogDto.setPreviousValue(0);
                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.setUseridUser(userId);
                    belongingInventoryDto.setItemId(characterPiece.getId());
                    belongingInventoryDto.setCount(gettingCount);
                    belongingInventoryDto.setItemType(belongingCharacterPieceItemType);
                    myCharacterPieceItem = belongingInventoryDto.ToEntity();
                    myCharacterPieceItem = belongingInventoryRepository.save(myCharacterPieceItem);
                    belongingInventoryLogDto.setBelongingInventoryLogDto(logWorkingPosition, myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
                    belongingInventoryList.add(myCharacterPieceItem);
                    belongingInventoryDto.setId(myCharacterPieceItem.getId());
                    changedCharacterPieceList.add(belongingInventoryDto);
                }
                else {
                    belongingInventoryLogDto.setPreviousValue(myCharacterPieceItem.getCount());
                    myCharacterPieceItem.AddItem(gettingCount, characterPiece.getStackLimit());

                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.setUseridUser(userId);
                    belongingInventoryDto.setItemId(characterPiece.getId());
                    belongingInventoryDto.setCount(gettingCount);
                    belongingInventoryDto.setItemType(belongingCharacterPieceItemType);
                    belongingInventoryLogDto.setBelongingInventoryLogDto(logWorkingPosition, myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
                    belongingInventoryDto.setId(myCharacterPieceItem.getId());
                    changedCharacterPieceList.add(belongingInventoryDto);
                }
            }
            /*특정 케릭터*/
            else if(gettingItemCode.contains("gotcha_")) {
                if(myCharactersList == null)
                    myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
                String heroCode = gettingItemCode.replace("gotcha_","");
                MyCharacters selectedCharacter = myCharactersList.stream().filter(a -> a.getCodeHerostable().equals(heroCode))
                        .findAny()
                        .orElse(null);
                if(selectedCharacter == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myCharactersList not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: myCharactersList not find.", ResponseErrorCode.NOT_FIND_DATA);
                }
                List<herostable> herostables = gameDataTableService.HerosTableList();
                herostable herostable = herostables.stream()
                        .filter(e -> e.getCode().equals(selectedCharacter.getCodeHerostable()))
                        .findAny()
                        .orElse(null);
                if(herostable == null){
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: not find userId.", ResponseErrorCode.NOT_FIND_DATA);
                }

                if(selectedCharacter.isGotcha()) {
                    if(belongingInventoryList == null)
                        belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);

                    List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
                    List<CharacterToPieceTable> characterToPieceTableList = gameDataTableService.CharacterToPieceTableList();

                    String willFindCharacterCode = selectedCharacter.getCodeHerostable();
                    if(herostable.getTier() == 1) {
                        willFindCharacterCode = "characterPiece_cr_common";
                    }
                    String finalWillFindCharacterCode = willFindCharacterCode;
                    BelongingCharacterPieceTable finalCharacterPieceTable = belongingCharacterPieceTableList.stream()
                            .filter(a -> a.getCode().contains(finalWillFindCharacterCode))
                            .findAny()
                            .orElse(null);
                    if(finalCharacterPieceTable == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: characterPieceTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: characterPieceTable not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    CharacterToPieceTable characterToPieceTable = characterToPieceTableList.stream().filter(a -> a.getId() == herostable.getTier()).findAny().orElse(null);
                    if(characterToPieceTable == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: characterToPieceTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: characterToPieceTable not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    if(itemTypeList == null)
                        itemTypeList = itemTypeRepository.findAll();
                    BelongingInventoryDto belongingInventoryDto = AddSelectedCharacterPiece(userId, characterToPieceTable.getPieceCount(), itemTypeList, finalCharacterPieceTable, belongingInventoryList, "마일리지 확정 뽑기");

                    GotchaCharacterResponseDto gotchaCharacterResponseDto = receiveItemCommonDto.getGotchaCharacterResponseDto();
                    StringMaker.Clear();
                    StringMaker.stringBuilder.append("reward_");
                    StringMaker.stringBuilder.append(selectedCharacter.getCodeHerostable());
                    String rewardCharacterCode = StringMaker.stringBuilder.toString();
                    GotchaCharacterResponseDto.GotchaCharacterInfo gotchaCharacterInfo = new GotchaCharacterResponseDto.GotchaCharacterInfo(rewardCharacterCode, belongingInventoryDto, null);
                    gotchaCharacterResponseDto.getCharacterInfoList().add(gotchaCharacterInfo);

                }
                else {
                    selectedCharacter.Gotcha();
                    GotchaCharacterResponseDto gotchaCharacterResponseDto = receiveItemCommonDto.getGotchaCharacterResponseDto();
                    GotchaCharacterResponseDto.GotchaCharacterInfo gotchaCharacterInfo = new GotchaCharacterResponseDto.GotchaCharacterInfo(selectedCharacter.getCodeHerostable(), null, selectedCharacter);
                    gotchaCharacterResponseDto.getCharacterInfoList().add(gotchaCharacterInfo);
                }
            }
            /*모든 장비 중 하나*/
            else if(gettingItemCode.equals("equipmentAll")){
                for(int j = 0;j<gettingCount;j++){
                    if (user == null) {
                        user = userRepository.findById(userId)
                                .orElse(null);
                        if (user == null) {
                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                            throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                        }
                    }
                    //장비
                    if (heroEquipmentInventoryList == null)
                        heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());

                    if (heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot())
                        throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
                    //등급 확률	영웅 70%	 전설 20%	신성 9%	고대 1%
                    //품질	D	C	B	A	S	SS	SSS
                    //확률	15%	25%	45%	9%	5%	1%	0%
                    List<Double> gradeProbabilityList = new ArrayList<>();
                    gradeProbabilityList.add(70D);
                    gradeProbabilityList.add(20D);
                    gradeProbabilityList.add(9D);
                    gradeProbabilityList.add(1D);
                    String selectedGrade = "";
                    int selectedIndex = MathHelper.RandomIndexWidthProbability(gradeProbabilityList);
                    switch (selectedIndex) {
                        case 0:
                            selectedGrade = "Hero";
                            break;
                        case 1:
                            selectedGrade = "Legend";
                            break;
                        case 2:
                            selectedGrade = "Divine";
                            break;
                        case 3:
                            selectedGrade = "Ancient";
                            break;
                    }

                    List<Double> classProbabilityList = new ArrayList<>();
                    classProbabilityList.add(15D);
                    classProbabilityList.add(25D);
                    classProbabilityList.add(45D);
                    classProbabilityList.add(9D);
                    classProbabilityList.add(5D);
                    classProbabilityList.add(1D);
                    classProbabilityList.add(0D);
                    String selectedClass = "";
                    selectedIndex = MathHelper.RandomIndexWidthProbability(classProbabilityList);
                    switch (selectedIndex) {
                        case 0:
                            selectedClass = "D";
                            break;
                        case 1:
                            selectedClass = "C";
                            break;
                        case 2:
                            selectedClass = "B";
                            break;
                        case 3:
                            selectedClass = "A";
                            break;
                        case 4:
                            selectedClass = "S";
                            break;
                        case 5:
                            selectedClass = "SS";
                            break;
                        case 6:
                            selectedClass = "SSS";
                            break;
                    }
                    List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
                    HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
                    int classValue = (int) EquipmentCalculate.GetClassValueFromClassCategory(selectedClass, classValues);
                    List<HeroEquipmentsTable> probabilityList = EquipmentCalculate.GetProbabilityList(heroEquipmentsTableList, EquipmentItemCategory.ALL, selectedGrade);
                    int randValue = (int) MathHelper.Range(0, probabilityList.size());
                    HeroEquipmentsTable selectEquipment = probabilityList.get(randValue);
                    List<EquipmentOptionsInfoTable> optionsInfoTableList = gameDataTableService.OptionsInfoTableList();
                    HeroEquipmentInventory generatedItem = EquipmentCalculate.CreateEquipment(user.getId(), selectEquipment, selectedClass, classValue, optionsInfoTableList);
                    generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
                    HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
                    heroEquipmentInventoryDto.InitFromDbData(generatedItem);
                    List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = receiveItemCommonDto.getChangedHeroEquipmentInventoryList();
                    changedHeroEquipmentInventoryList.add(heroEquipmentInventoryDto);
                    EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                    equipmentLogDto.setEquipmentLogDto(logWorkingPosition, generatedItem.getId(), "추가", heroEquipmentInventoryDto);
                    String log = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                    loggingService.setLogging(userId, 2, log);

                    /* 업적 : 장비 획득 (특정 품질) 미션 체크*/
                    changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(), generatedItem.getItemClass(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                    /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
                    changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), selectedGrade, gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                }

            }
            /*특정 등급, 특정 클래스, 특정 종류의 장비중 하나*/
            else if(gettingItemCode.contains("equipment")) {
                for(int j = 0;j<gettingCount;j++){
                    if (user == null) {
                        user = userRepository.findById(userId)
                                .orElse(null);
                        if (user == null) {
                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                            throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                        }
                    }
                    if (heroEquipmentInventoryList == null)
                        heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());

                    if (heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
                    }

                    List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
                    HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
                    List<EquipmentOptionsInfoTable> optionsInfoTableList = gameDataTableService.OptionsInfoTableList();
                    HeroEquipmentInventoryDto heroEquipmentInventoryDto = ApplySomeReward.AddEquipmentItem(user, gettingItemCode, heroEquipmentInventoryList, heroEquipmentInventoryRepository, heroEquipmentsTableList, classValues, optionsInfoTableList, logWorkingPosition, loggingService, errorLoggingService);
                    List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = receiveItemCommonDto.getChangedHeroEquipmentInventoryList();
                    changedHeroEquipmentInventoryList.add(heroEquipmentInventoryDto);

                    HeroEquipmentsTable selectedEquipmentItemTable = heroEquipmentsTableList.stream().filter(a -> a.getId() == heroEquipmentInventoryDto.getItem_Id()).findAny().orElse(null);

                    /* 업적 : 장비 획득 (특정 품질) 미션 체크*/
                    changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(), heroEquipmentInventoryDto.getItemClass(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                    /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
                    changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), selectedEquipmentItemTable.getGrade(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                }

            }
            /*인장 중 하나*/
            else if(gettingItemCode.equals("stampAll")){
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }

                if(heroEquipmentInventoryList == null)
                    heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());

                if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
                }

                List<PassiveItemTable> passiveItemTables = gameDataTableService.PassiveItemTableList();
                List<PassiveItemTable> copyPassiveItemTables = new ArrayList<>();
                copyPassiveItemTables.addAll(passiveItemTables);
                List<PassiveItemTable> deleteList = new ArrayList<>();
                boolean deleted = false;
                for(PassiveItemTable passiveItemTable : copyPassiveItemTables){
                    String code = passiveItemTable.getCode();
                    if(code.equals("passiveItem_00_10")) {
                        deleteList.add(passiveItemTable);
                        deleted = true;
                        break;
                    }
                }
                if(deleted)
                    copyPassiveItemTables.removeAll(deleteList);

                int selectedIndex  = (int)MathHelper.Range(0, copyPassiveItemTables.size());
                PassiveItemTable selectedPassiveItem = copyPassiveItemTables.get(selectedIndex);
                List<Double> classProbabilityList = new ArrayList<>();
                classProbabilityList.add(15D);
                classProbabilityList.add(25D);
                classProbabilityList.add(45D);
                classProbabilityList.add(9D);
                classProbabilityList.add(5D);
                classProbabilityList.add(1D);
                classProbabilityList.add(0D);
                String selectedClass = "";
                selectedIndex = MathHelper.RandomIndexWidthProbability(classProbabilityList);
                switch (selectedIndex) {
                    case 0:
                        selectedClass = "D";
                        break;
                    case 1:
                        selectedClass = "C";
                        break;
                    case 2:
                        selectedClass = "B";
                        break;
                    case 3:
                        selectedClass = "A";
                        break;
                    case 4:
                        selectedClass = "S";
                        break;
                    case 5:
                        selectedClass = "SS";
                        break;
                    case 6:
                        selectedClass = "SSS";
                        break;
                }
                HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
                int classValue = (int) EquipmentCalculate.GetClassValueFromClassCategory(selectedClass, classValues);

                HeroEquipmentInventoryDto dto = new HeroEquipmentInventoryDto();
                dto.setUseridUser(userId);
                dto.setItem_Id(selectedPassiveItem.getId());
                dto.setItemClassValue(classValue);
                dto.setDecideDefaultAbilityValue(0);
                dto.setDecideSecondAbilityValue(0);
                dto.setLevel(1);
                dto.setMaxLevel(1);
                dto.setExp(0);
                dto.setNextExp(0);
                dto.setItemClass(selectedClass);

                HeroEquipmentInventory generatedItem = dto.ToEntity();
                generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
                HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
                heroEquipmentInventoryDto.InitFromDbData(generatedItem);
                EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                equipmentLogDto.setEquipmentLogDto(logWorkingPosition, generatedItem.getId(), "추가", heroEquipmentInventoryDto);
                String log = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                loggingService.setLogging(userId, 2, log);

                List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = receiveItemCommonDto.getChangedHeroEquipmentInventoryList();
                changedHeroEquipmentInventoryList.add(heroEquipmentInventoryDto);
            }
            /*특정 품질의 인장*/
            else if(gettingItemCode.contains("stamp")){

                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }

                if(heroEquipmentInventoryList == null)
                    heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());

                if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
                }

                String itemClass = "D";
                if(gettingItemCode.contains("ClassD")) {
                    itemClass = "D";
                }
                else if(gettingItemCode.contains("ClassC")){
                    itemClass = "C";
                }
                else if(gettingItemCode.contains("ClassB")){
                    itemClass = "B";
                }
                else if(gettingItemCode.contains("ClassA")){
                    itemClass = "A";
                }
                else if(gettingItemCode.contains("ClassSSS")){
                    itemClass = "SSS";
                }
                else if(gettingItemCode.contains("ClassSS")){
                    itemClass = "SS";
                }
                else if(gettingItemCode.contains("ClassS")){
                    itemClass = "S";
                }

                List<PassiveItemTable> passiveItemTables = gameDataTableService.PassiveItemTableList();
                List<PassiveItemTable> copyPassiveItemTables = new ArrayList<>();
                copyPassiveItemTables.addAll(passiveItemTables);
                List<PassiveItemTable> deleteList = new ArrayList<>();
                boolean deleted = false;
                for(PassiveItemTable passiveItemTable : copyPassiveItemTables){
                    String code = passiveItemTable.getCode();
                    if(code.equals("passiveItem_00_10")) {
                        deleteList.add(passiveItemTable);
                        deleted = true;
                        break;
                    }
                }
                if(deleted)
                    copyPassiveItemTables.removeAll(deleteList);

                int selectedIndex  = (int)MathHelper.Range(0, copyPassiveItemTables.size());
                PassiveItemTable selectedPassiveItem = copyPassiveItemTables.get(selectedIndex);

                HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
                int classValue = (int) EquipmentCalculate.GetClassValueFromClassCategory(itemClass, classValues);

                HeroEquipmentInventoryDto dto = new HeroEquipmentInventoryDto();
                dto.setUseridUser(userId);
                dto.setItem_Id(selectedPassiveItem.getId());
                dto.setItemClassValue(classValue);
                dto.setDecideDefaultAbilityValue(0);
                dto.setDecideSecondAbilityValue(0);
                dto.setLevel(1);
                dto.setMaxLevel(1);
                dto.setExp(0);
                dto.setNextExp(0);
                dto.setItemClass(itemClass);

                HeroEquipmentInventory generatedItem = dto.ToEntity();
                generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
                HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
                heroEquipmentInventoryDto.InitFromDbData(generatedItem);
                EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                equipmentLogDto.setEquipmentLogDto(logWorkingPosition, generatedItem.getId(), "추가", heroEquipmentInventoryDto);
                String log = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                loggingService.setLogging(userId, 2, log);

                List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = receiveItemCommonDto.getChangedHeroEquipmentInventoryList();
                changedHeroEquipmentInventoryList.add(heroEquipmentInventoryDto);
            }
            /*특정 인장 중 하나*/
            else if(gettingItemCode.contains("passiveItem")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }

                if(heroEquipmentInventoryList == null)
                    heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());

                if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
                }

                List<PassiveItemTable> passiveItemTables = gameDataTableService.PassiveItemTableList();

                int selectedIndex  = (int)MathHelper.Range(0, passiveItemTables.size());
                PassiveItemTable selectedPassiveItem = passiveItemTables.stream().filter(a -> a.getCode().equals(gettingItemCode)).findAny().orElse(null);
                if(selectedPassiveItem == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find PassiveItemTable", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail -> Cause: Can't Find PassiveItemTable", ResponseErrorCode.NOT_FIND_DATA);
                }
                List<Double> classProbabilityList = new ArrayList<>();
                classProbabilityList.add(15D);
                classProbabilityList.add(25D);
                classProbabilityList.add(45D);
                classProbabilityList.add(9D);
                classProbabilityList.add(5D);
                classProbabilityList.add(1D);
                classProbabilityList.add(0D);
                String selectedClass = "";
                selectedIndex = MathHelper.RandomIndexWidthProbability(classProbabilityList);
                switch (selectedIndex) {
                    case 0:
                        selectedClass = "D";
                        break;
                    case 1:
                        selectedClass = "C";
                        break;
                    case 2:
                        selectedClass = "B";
                        break;
                    case 3:
                        selectedClass = "A";
                        break;
                    case 4:
                        selectedClass = "S";
                        break;
                    case 5:
                        selectedClass = "SS";
                        break;
                    case 6:
                        selectedClass = "SSS";
                        break;
                }
                HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
                int classValue = (int) EquipmentCalculate.GetClassValueFromClassCategory(selectedClass, classValues);

                HeroEquipmentInventoryDto dto = new HeroEquipmentInventoryDto();
                dto.setUseridUser(userId);
                dto.setItem_Id(selectedPassiveItem.getId());
                dto.setItemClassValue(classValue);
                dto.setDecideDefaultAbilityValue(0);
                dto.setDecideSecondAbilityValue(0);
                dto.setLevel(1);
                dto.setMaxLevel(1);
                dto.setExp(0);
                dto.setNextExp(0);
                dto.setItemClass(selectedClass);

                HeroEquipmentInventory generatedItem = dto.ToEntity();
                generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
                HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
                heroEquipmentInventoryDto.InitFromDbData(generatedItem);
                EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                equipmentLogDto.setEquipmentLogDto(logWorkingPosition, generatedItem.getId(), "추가", heroEquipmentInventoryDto);
                String log = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                loggingService.setLogging(userId, 2, log);

                List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = receiveItemCommonDto.getChangedHeroEquipmentInventoryList();
                changedHeroEquipmentInventoryList.add(heroEquipmentInventoryDto);
            }
        }
        return changedMissionsData;
    }
    BelongingInventoryDto AddSelectedCharacterPiece(Long userId, int gettingCount, List<ItemType> itemTypeList, BelongingCharacterPieceTable characterPieceTable, List<BelongingInventory> belongingInventoryList, String workingPosition) {
        ItemType belongingCharacterPieceItem = itemTypeList.stream()
                .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece)
                .findAny()
                .orElse(null);

        BelongingCharacterPieceTable finalCharacterPieceTable = characterPieceTable;
        BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
        BelongingInventory myCharacterPieceItem = belongingInventoryList.stream()
                .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece && a.getItemId() == finalCharacterPieceTable.getId())
                .findAny()
                .orElse(null);

        if(myCharacterPieceItem == null) {
            BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
            belongingInventoryLogDto.setPreviousValue(0);
            belongingInventoryDto.setUseridUser(userId);
            belongingInventoryDto.setItemId(characterPieceTable.getId());
            belongingInventoryDto.setCount(gettingCount);
            belongingInventoryDto.setItemType(belongingCharacterPieceItem);
            myCharacterPieceItem = belongingInventoryDto.ToEntity();
            myCharacterPieceItem = belongingInventoryRepository.save(myCharacterPieceItem);
            belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
            String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, belongingLog);
            belongingInventoryDto.setId(myCharacterPieceItem.getId());
            belongingInventoryList.add(myCharacterPieceItem);
        }
        else {
            BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
            belongingInventoryLogDto.setPreviousValue(myCharacterPieceItem.getCount());
            myCharacterPieceItem.AddItem(gettingCount, characterPieceTable.getStackLimit());
            belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
            String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, belongingLog);
            belongingInventoryDto.setId(myCharacterPieceItem.getId());
            belongingInventoryDto.setUseridUser(userId);
            belongingInventoryDto.setItemId(characterPieceTable.getId());
            belongingInventoryDto.setCount(gettingCount);
            belongingInventoryDto.setItemType(belongingCharacterPieceItem);
        }
        return belongingInventoryDto;
    }
}
