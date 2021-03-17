package com.onlyonegames.eternalfantasia.domain.service.Shop;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;
import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.EventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.GiftItemDto.GiftItemDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.HeroEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.BelongingInventoryLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CurrencyLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.EquipmentLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.GiftLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ShopDto.MyShopItemsList;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyGiftInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Iap.SeasonLimitPackageSchedule;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.ItemType;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Shop.MyShopInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyGiftInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Iap.SeasonLimitPackageScheduleRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.BelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.HeroEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.ItemTypeRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Shop.MyShopInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.Iap.IapService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import com.onlyonegames.eternalfantasia.etc.EquipmentCalculate;
import com.onlyonegames.eternalfantasia.etc.EquipmentItemCategory;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.util.MathHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyShopService {
    private final MyShopInfoRepository myShopInfoRepository;
    private final GameDataTableService gameDataTableService;
    private final UserRepository userRepository;
    private final ItemTypeRepository itemTypeRepository;
    private final MyGiftInventoryRepository myGiftInventoryRepository;
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final HeroEquipmentInventoryRepository heroEquipmentInventoryRepository;
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final MyCharactersRepository myCharactersRepository;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;
    private final SeasonLimitPackageScheduleRepository seasonLimitPackageScheduleRepository;
    private final IapService iapService;
    public Map<String, Object> getMyShopData(Long userId, Map<String, Object> map) {

        //이전에 결제 한거 남아있어서 그거 리턴
        map = iapService.CheckAlreadyTransaction(userId, map);
        Boolean.parseBoolean(map.get("alreadyTransaction").toString());

        MyShopInfo myShopInfo = myShopInfoRepository.findByUseridUser(userId).orElse(null);
        if (myShopInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find myShopMain.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find myShopMain.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String json_myShopMainInfo = myShopInfo.getJson_myShopInfos();
        MyShopItemsList myShopItemsLis = JsonStringHerlper.ReadValueFromJson(myShopInfo.getJson_myShopInfos(), MyShopItemsList.class);
        if(myShopItemsLis.myDarkObeShopInfos == null) {
            myShopItemsLis.CreateDarkObeShop();
            myShopItemsLis.myDarkObeShopInfos.ResetShop(userId, gameDataTableService.DarkObeShopTableList(), gameDataTableService.HeroEquipmentsTableList(), gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1), gameDataTableService.OptionsInfoTableList(), gameDataTableService.SpendableItemInfoTableList(), gameDataTableService.HerosTableList(), gameDataTableService.PassiveItemTableList());
        }
        myShopItemsLis.CheckSeasonLimitPackage();

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
            map.put("recycle", true);
            map.put("myShopItemsLis", myShopItemsLis);
            map.put("scheduleStartTime", myShopInfo.getScheduleStartTime());

            map.put("dailyPackageScheduleStartTime", myShopInfo.getDailyPackageScheduleStartTime());
            map.put("weeklyPackageScheduleStartTime", myShopInfo.getWeeklyPackageScheduleStartTime());
            map.put("monthlyPackageScheduleStartTime", myShopInfo.getMonthlyPackageScheduleStartTime());
            return map;
        }

        map.put("myShopItemsLis", myShopItemsLis);
        map.put("scheduleStartTime", myShopInfo.getScheduleStartTime());
        map.put("dailyPackageScheduleStartTime", myShopInfo.getDailyPackageScheduleStartTime());
        map.put("weeklyPackageScheduleStartTime", myShopInfo.getWeeklyPackageScheduleStartTime());
        map.put("monthlyPackageScheduleStartTime", myShopInfo.getMonthlyPackageScheduleStartTime());

        return map;
    }

    public Map<String, Object> mainShopRecycle(Long userId, Map<String, Object> map) {
        MyShopInfo myShopInfo = myShopInfoRepository.findByUseridUser(userId).orElse(null);
        if (myShopInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find myShopMain.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find myShopMain.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String json_myShopMainInfo = myShopInfo.getJson_myShopInfos();
        MyShopItemsList myShopItemsLis = JsonStringHerlper.ReadValueFromJson(json_myShopMainInfo, MyShopItemsList.class);

        if (myShopInfo.IsMyShopRecycleTime(86400/*총 1일에 대한 초*/)) {
            myShopItemsLis.ResetAllShop(userId, gameDataTableService.ShopTableList(), gameDataTableService.ArenaShopTableList(), gameDataTableService.GiftsTableList(), gameDataTableService.HeroEquipmentsTableList(), gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1), gameDataTableService.OptionsInfoTableList(), gameDataTableService.HerosTableList(), gameDataTableService.AncientShopTableList(), gameDataTableService.CurrencyShopTableList(), gameDataTableService.SpendableItemInfoTableList(), gameDataTableService.PieceShopTableList(), gameDataTableService.DarkObeShopTableList(), gameDataTableService.PassiveItemTableList());
            json_myShopMainInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsLis);
            myShopInfo.ResetMyShopInfos(json_myShopMainInfo);
            map.put("recycle", true);
            map.put("myShopItemsLis", myShopItemsLis);
            map.put("scheduleStartTime", myShopInfo.getScheduleStartTime());
            return map;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
        }
        int previousValue = user.getDiamond();
        int cost = 30;
        if (!user.SpendDiamond(cost)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
        }
        CurrencyLogDto currencyLogDto = new CurrencyLogDto();
        currencyLogDto.setCurrencyLogDto("상점 메인상점 상품 갱신", "다이아", previousValue, -cost, user.getDiamond());
        String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
        loggingService.setLogging(userId, 1, log);
        myShopItemsLis.ResetMainShop(userId, gameDataTableService.ShopTableList(), gameDataTableService.GiftsTableList(), gameDataTableService.HeroEquipmentsTableList(), gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1), gameDataTableService.OptionsInfoTableList());
        json_myShopMainInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsLis);
        myShopInfo.ResetMyShopInfos(json_myShopMainInfo);
        map.put("myShopItemsLis", myShopItemsLis);
        map.put("user", user);
        return map;
    }

    public Map<String, Object> arenaShopRecycle(Long userId, Map<String, Object> map) {
        MyShopInfo myShopInfo = myShopInfoRepository.findByUseridUser(userId).orElse(null);
        if (myShopInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find myShopMain.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find myShopMain.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String json_myShopMainInfo = myShopInfo.getJson_myShopInfos();
        MyShopItemsList myShopItemsLis = JsonStringHerlper.ReadValueFromJson(json_myShopMainInfo, MyShopItemsList.class);

        if (myShopInfo.IsMyShopRecycleTime(86400/*총 1일에 대한 초*/)) {
            myShopItemsLis.ResetAllShop(userId, gameDataTableService.ShopTableList(), gameDataTableService.ArenaShopTableList(), gameDataTableService.GiftsTableList(), gameDataTableService.HeroEquipmentsTableList(), gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1), gameDataTableService.OptionsInfoTableList(), gameDataTableService.HerosTableList(), gameDataTableService.AncientShopTableList(), gameDataTableService.CurrencyShopTableList(), gameDataTableService.SpendableItemInfoTableList(), gameDataTableService.PieceShopTableList(), gameDataTableService.DarkObeShopTableList(), gameDataTableService.PassiveItemTableList());
            json_myShopMainInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsLis);
            myShopInfo.ResetMyShopInfos(json_myShopMainInfo);
            map.put("recycle", true);
            map.put("myShopItemsLis", myShopItemsLis);
            map.put("scheduleStartTime", myShopInfo.getScheduleStartTime());
            return map;
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
        }
        int previousValue = user.getDiamond();
        int cost = 30;
        if (!user.SpendDiamond(cost)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
        }
        CurrencyLogDto currencyLogDto = new CurrencyLogDto();
        currencyLogDto.setCurrencyLogDto("상점 아레나상점 상품 갱신", "다이아", previousValue, -cost, user.getDiamond());
        String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
        loggingService.setLogging(userId, 1, log);
        myShopItemsLis.ResetArenaShop(gameDataTableService.ArenaShopTableList(), gameDataTableService.HerosTableList(), gameDataTableService.GiftsTableList(), gameDataTableService.SpendableItemInfoTableList());
        json_myShopMainInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsLis);
        myShopInfo.ResetMyShopInfos(json_myShopMainInfo);
        map.put("myShopItemsLis", myShopItemsLis);
        map.put("user", user);
        return map;
    }

    public Map<String, Object> ancientShopRecycle(Long userId, Map<String, Object> map) {
        MyShopInfo myShopInfo = myShopInfoRepository.findByUseridUser(userId).orElse(null);
        if (myShopInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find myShopMain.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find myShopMain.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String json_myShopMainInfo = myShopInfo.getJson_myShopInfos();
        MyShopItemsList myShopItemsLis = JsonStringHerlper.ReadValueFromJson(json_myShopMainInfo, MyShopItemsList.class);

        if (myShopInfo.IsMyShopRecycleTime(86400/*총 1일에 대한 초*/)) {
            myShopItemsLis.ResetAllShop(userId, gameDataTableService.ShopTableList(), gameDataTableService.ArenaShopTableList(), gameDataTableService.GiftsTableList(), gameDataTableService.HeroEquipmentsTableList(), gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1), gameDataTableService.OptionsInfoTableList(), gameDataTableService.HerosTableList(), gameDataTableService.AncientShopTableList(), gameDataTableService.CurrencyShopTableList(), gameDataTableService.SpendableItemInfoTableList(), gameDataTableService.PieceShopTableList(), gameDataTableService.DarkObeShopTableList(), gameDataTableService.PassiveItemTableList());
            json_myShopMainInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsLis);
            myShopInfo.ResetMyShopInfos(json_myShopMainInfo);
            map.put("recycle", true);
            map.put("myShopItemsLis", myShopItemsLis);
            map.put("scheduleStartTime", myShopInfo.getScheduleStartTime());
            return map;
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
        }
        int previousValue = user.getDiamond();
        int cost = 30;
        if (!user.SpendDiamond(cost)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
        }
        CurrencyLogDto currencyLogDto = new CurrencyLogDto();
        currencyLogDto.setCurrencyLogDto("상점 고대상점 상품 갱신", "다이아", previousValue, -cost, user.getDiamond());
        String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
        loggingService.setLogging(userId, 1, log);
        myShopItemsLis.ResetAncientShop(gameDataTableService.AncientShopTableList());
        json_myShopMainInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsLis);
        myShopInfo.ResetMyShopInfos(json_myShopMainInfo);
        map.put("myShopItemsLis", myShopItemsLis);
        map.put("user", user);
        return map;
    }

    public Map<String, Object> currencyShopRecycle(Long userId, Map<String, Object> map) {
        MyShopInfo myShopInfo = myShopInfoRepository.findByUseridUser(userId).orElse(null);
        if (myShopInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find myShopMain.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find myShopMain.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String json_myShopMainInfo = myShopInfo.getJson_myShopInfos();
        MyShopItemsList myShopItemsLis = JsonStringHerlper.ReadValueFromJson(json_myShopMainInfo, MyShopItemsList.class);

        if (myShopInfo.IsMyShopRecycleTime(86400/*총 1일에 대한 초*/)) {
            myShopItemsLis.ResetAllShop(userId, gameDataTableService.ShopTableList(), gameDataTableService.ArenaShopTableList(), gameDataTableService.GiftsTableList(), gameDataTableService.HeroEquipmentsTableList(), gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1), gameDataTableService.OptionsInfoTableList(), gameDataTableService.HerosTableList(), gameDataTableService.AncientShopTableList(), gameDataTableService.CurrencyShopTableList(), gameDataTableService.SpendableItemInfoTableList(), gameDataTableService.PieceShopTableList(), gameDataTableService.DarkObeShopTableList(), gameDataTableService.PassiveItemTableList());
            json_myShopMainInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsLis);
            myShopInfo.ResetMyShopInfos(json_myShopMainInfo);
            map.put("recycle", true);
            map.put("myShopItemsLis", myShopItemsLis);
            map.put("scheduleStartTime", myShopInfo.getScheduleStartTime());
            return map;
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
        }
        int previousValue = user.getDiamond();
        int cost = 30;
        if (!user.SpendDiamond(cost)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
        }
        CurrencyLogDto currencyLogDto = new CurrencyLogDto();
        currencyLogDto.setCurrencyLogDto("상점 화폐상점 상품 갱신", "다이아", previousValue, -cost, user.getDiamond());
        String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
        loggingService.setLogging(userId, 1, log);
        myShopItemsLis.ResetCurrencyShop(gameDataTableService.CurrencyShopTableList());
        json_myShopMainInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsLis);
        myShopInfo.ResetMyShopInfos(json_myShopMainInfo);
        map.put("myShopItemsLis", myShopItemsLis);
        map.put("user", user);
        return map;
    }

    public Map<String, Object> pieceShopRecycle(Long userId, Map<String, Object> map) {
        MyShopInfo myShopInfo = myShopInfoRepository.findByUseridUser(userId).orElse(null);
        if (myShopInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find myShopMain.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find myShopMain.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String json_myShopMainInfo = myShopInfo.getJson_myShopInfos();
        MyShopItemsList myShopItemsLis = JsonStringHerlper.ReadValueFromJson(json_myShopMainInfo, MyShopItemsList.class);

        if (myShopInfo.IsMyShopRecycleTime(86400/*총 1일에 대한 초*/)) {
            myShopItemsLis.ResetAllShop(userId, gameDataTableService.ShopTableList(), gameDataTableService.ArenaShopTableList(), gameDataTableService.GiftsTableList(), gameDataTableService.HeroEquipmentsTableList(), gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1), gameDataTableService.OptionsInfoTableList(), gameDataTableService.HerosTableList(), gameDataTableService.AncientShopTableList(), gameDataTableService.CurrencyShopTableList(), gameDataTableService.SpendableItemInfoTableList(), gameDataTableService.PieceShopTableList(), gameDataTableService.DarkObeShopTableList(), gameDataTableService.PassiveItemTableList());
            json_myShopMainInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsLis);
            myShopInfo.ResetMyShopInfos(json_myShopMainInfo);
            map.put("recycle", true);
            map.put("myShopItemsLis", myShopItemsLis);
            map.put("scheduleStartTime", myShopInfo.getScheduleStartTime());
            return map;
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
        }
        int previousValue = user.getDiamond();
        int cost = 30;
        if (!user.SpendDiamond(cost)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
        }
        CurrencyLogDto currencyLogDto = new CurrencyLogDto();
        currencyLogDto.setCurrencyLogDto("상점 고대상점 상품 갱신", "다이아", previousValue, -cost, user.getDiamond());
        String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
        loggingService.setLogging(userId, 1, log);
        myShopItemsLis.ResetPieceShop(gameDataTableService.PieceShopTableList(), gameDataTableService.HerosTableList());
        json_myShopMainInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsLis);
        myShopInfo.ResetMyShopInfos(json_myShopMainInfo);
        map.put("myShopItemsLis", myShopItemsLis);
        map.put("user", user);
        return map;
    }

    public Map<String, Object> darkObeShopRecycle(Long userId, Map<String, Object> map) {
        MyShopInfo myShopInfo = myShopInfoRepository.findByUseridUser(userId).orElse(null);
        if (myShopInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find myShopMain.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find myShopMain.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String json_myShopMainInfo = myShopInfo.getJson_myShopInfos();
        MyShopItemsList myShopItemsLis = JsonStringHerlper.ReadValueFromJson(json_myShopMainInfo, MyShopItemsList.class);

        if (myShopInfo.IsMyShopRecycleTime(86400/*총 1일에 대한 초*/)) {
            myShopItemsLis.ResetAllShop(userId, gameDataTableService.ShopTableList(), gameDataTableService.ArenaShopTableList(), gameDataTableService.GiftsTableList(), gameDataTableService.HeroEquipmentsTableList(), gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1), gameDataTableService.OptionsInfoTableList(), gameDataTableService.HerosTableList(), gameDataTableService.AncientShopTableList(), gameDataTableService.CurrencyShopTableList(), gameDataTableService.SpendableItemInfoTableList(), gameDataTableService.PieceShopTableList(), gameDataTableService.DarkObeShopTableList(), gameDataTableService.PassiveItemTableList());
            json_myShopMainInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsLis);
            myShopInfo.ResetMyShopInfos(json_myShopMainInfo);
            map.put("recycle", true);
            map.put("myShopItemsLis", myShopItemsLis);
            map.put("scheduleStartTime", myShopInfo.getScheduleStartTime());
            return map;
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
        }
        int previousValue = user.getDiamond();
        int cost = 30;
        if (!user.SpendDiamond(cost)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
        }
        CurrencyLogDto currencyLogDto = new CurrencyLogDto();
        currencyLogDto.setCurrencyLogDto("상점 어둠의 보주 상점 상품 갱신", "다이아", previousValue, -cost, user.getDiamond());
        String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
        loggingService.setLogging(userId, 1, log);
        myShopItemsLis.ResetDarkObeShop(userId, gameDataTableService.DarkObeShopTableList(), gameDataTableService.HeroEquipmentsTableList(), gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1), gameDataTableService.OptionsInfoTableList(), gameDataTableService.SpendableItemInfoTableList(), gameDataTableService.HerosTableList(), gameDataTableService.PassiveItemTableList());
        json_myShopMainInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsLis);
        myShopInfo.ResetMyShopInfos(json_myShopMainInfo);
        map.put("myShopItemsLis", myShopItemsLis);
        map.put("user", user);
        return map;
    }

    /*메인 상점*/
    public Map<String, Object> mainShopBuy(Long userId, int slotIndex, Map<String, Object> map) {

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
        }

        MyShopInfo myShopInfo = myShopInfoRepository.findByUseridUser(userId).orElse(null);
        if (myShopInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find myShopMain.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find myShopMain.", ResponseErrorCode.NOT_FIND_DATA);
        }

        CurrencyLogDto currencyLogDto = new CurrencyLogDto();
        String json_myShopMainInfo = myShopInfo.getJson_myShopInfos();
        MyShopItemsList myShopItemsLis = JsonStringHerlper.ReadValueFromJson(json_myShopMainInfo, MyShopItemsList.class);

        if (myShopInfo.IsMyShopRecycleTime(86400/*총 1일에 대한 초*/)) {
            myShopItemsLis.ResetAllShop(userId, gameDataTableService.ShopTableList(), gameDataTableService.ArenaShopTableList(), gameDataTableService.GiftsTableList(), gameDataTableService.HeroEquipmentsTableList(), gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1), gameDataTableService.OptionsInfoTableList(), gameDataTableService.HerosTableList(), gameDataTableService.AncientShopTableList(), gameDataTableService.CurrencyShopTableList(), gameDataTableService.SpendableItemInfoTableList(), gameDataTableService.PieceShopTableList(), gameDataTableService.DarkObeShopTableList(), gameDataTableService.PassiveItemTableList());
            json_myShopMainInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsLis);
            myShopInfo.ResetMyShopInfos(json_myShopMainInfo);
            map.put("recycle", true);
            map.put("myShopItemsLis", myShopItemsLis);
            map.put("scheduleStartTime", myShopInfo.getScheduleStartTime());
            return map;
        }

        /*업적 : 체크 준비*/
        MyMissionsData myMissionsData = myMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if (myMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyMissionData = myMissionsData.getJson_saveDataValue();
        MissionsDataDto myMissionsDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyMissionData, MissionsDataDto.class);
        boolean changedMissionsData = false;

        MyShopItemsList.MyShopMainInfos myShopMainInfos = myShopItemsLis.myShopMainInfos;

        MyShopItemsList.MyShopItem myShopItem = myShopMainInfos.myShopItemList.stream()
                .filter(a -> a.slotIndex == slotIndex)
                .findAny()
                .orElse(null);
        if (myShopItem != null) {
            if (myShopItem.bought) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_BOUGHT.getIntegerValue(), "Fail! -> Cause: Aleady bought", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Aleady bought", ResponseErrorCode.ALREADY_BOUGHT);
            }
            myShopItem.bought = true;
            ShopTable shopTable = gameDataTableService.ShopTableList().stream()
                    .filter(a -> a.getId() == myShopItem.shopTableId)
                    .findAny()
                    .orElse(null);
            if (shopTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find shopTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Can't Find shopTable.", ResponseErrorCode.NOT_FIND_DATA);
            }
            //가격 체크
            if (shopTable.getCurrency().equals("gold")) {
                currencyLogDto.setPreviousValue(user.getGold());
                if (!user.SpendGold(shopTable.getCost())) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail! -> Cause: Need more Gold.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more Gold.", ResponseErrorCode.NEED_MORE_GOLD);
                }
                currencyLogDto.setChangeNum(-shopTable.getCost());
                currencyLogDto.setPresentValue(user.getGold());
                currencyLogDto.setCurrencyType("골드");
            } else if (shopTable.getCurrency().equals("diamond")) {
                currencyLogDto.setPreviousValue(user.getDiamond());
                if (!user.SpendDiamond(shopTable.getCost())) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
                }
                currencyLogDto.setChangeNum(-shopTable.getCost());
                currencyLogDto.setPresentValue(user.getDiamond());
                currencyLogDto.setCurrencyType("다이아");

            }
            List<ItemType> itemTypeList = itemTypeRepository.findAll();
            List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
            List<BelongingInventoryDto> changedBelongingInventoryList = new ArrayList<>();

            //링크포인트
            //myShopItemList 중 하나
            if (shopTable.getGettingItem().contains("linkPoint")) {
                int previousValue = user.getLinkforcePoint();
                user.AddLinkforcePoint(shopTable.getGettingCount());
                currencyLogDto.setWorkingPosition("상점 메인상점 구매 -> 링크포인트");
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                CurrencyLogDto addCurrency = new CurrencyLogDto();
                addCurrency.setCurrencyLogDto("상점 메인상점 구매 -> 링크포인트", "링크포인트", previousValue, shopTable.getGettingCount(), user.getLinkforcePoint());
                String addLog = JsonStringHerlper.WriteValueAsStringFromData(addCurrency);
                loggingService.setLogging(userId, 1, addLog);
            }
            //재료상자
            else if (shopTable.getGettingItem().contains("reward_material")) {
                int kindCount = 0;
                if (shopTable.getGettingItem().contains("low")) {
                    //3종
                    kindCount = 3;
                } else if (shopTable.getGettingItem().contains("middle")) {
                    //5종
                    kindCount = 5;
                } else if (shopTable.getGettingItem().contains("high")) {
                    //8종
                    kindCount = 8;
                }

                List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList = gameDataTableService.EquipmentMaterialInfoTableList();
                List<EquipmentMaterialInfoTable> copyEquipmentMaterialInfoTableList = new ArrayList<>();
                copyEquipmentMaterialInfoTableList.addAll(equipmentMaterialInfoTableList);
                String itemTemp = new String();
                for (int i = 0; i < kindCount; i++) {
                    int selectedRandomIndex = (int) MathHelper.Range(0, copyEquipmentMaterialInfoTableList.size());
                    EquipmentMaterialInfoTable equipmentMaterialInfoTable = copyEquipmentMaterialInfoTableList.get(selectedRandomIndex);
                    BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                    BelongingInventoryDto belongingInventoryDto = AddEquipmentMaterialItem(userId, equipmentMaterialInfoTable.getCode(), shopTable.getGettingCount(), itemTypeList, belongingInventoryList, equipmentMaterialInfoTableList, belongingInventoryLogDto);
                    changedBelongingInventoryList.add(belongingInventoryDto);
                    copyEquipmentMaterialInfoTableList.remove(selectedRandomIndex);
                    belongingInventoryLogDto.setWorkingPosition("상점 메인상점 구매 -> " + shopTable.getGettingItem());
                    belongingInventoryLogDto.setInventoryId(belongingInventoryDto.getId());
                    belongingInventoryLogDto.setItemType_id(belongingInventoryDto.getItemType());
                    String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                    loggingService.setLogging(userId, 3, belongingLog);
                    itemTemp = itemTemp + equipmentMaterialInfoTable.getName() + " X " + shopTable.getGettingCount() + " ";
                }
                currencyLogDto.setWorkingPosition("상점 메인상점 구매 -> " + shopTable.getGettingItem());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                map.put("changedBelongingInventoryList", changedBelongingInventoryList);
            }
        }
        else {
            //선물
            GiftLogDto giftLogDto = new GiftLogDto();
            MyShopItemsList.MyGiftItem myGiftItem = myShopMainInfos.myGiftItemList.stream()
                    .filter(a -> a.slotIndex == slotIndex)
                    .findAny()
                    .orElse(null);
            if (myGiftItem != null) {
                if (myGiftItem.bought) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_BOUGHT.getIntegerValue(), "Fail! -> Cause: Aleady bought", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Aleady bought", ResponseErrorCode.ALREADY_BOUGHT);
                }
                myGiftItem.bought = true;
                ShopTable shopTable = gameDataTableService.ShopTableList().stream()
                        .filter(a -> a.getId() == myGiftItem.shopTableId)
                        .findAny()
                        .orElse(null);
                if (shopTable == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find shopTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't Find shopTable.", ResponseErrorCode.NOT_FIND_DATA);
                }
                //가격 체크
                if (shopTable.getCurrency().equals("gold")) {
                    currencyLogDto.setPreviousValue(user.getGold());
                    if (!user.SpendGold(shopTable.getCost())) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail! -> Cause: Need more Gold.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Need more Gold.", ResponseErrorCode.NEED_MORE_GOLD);
                    }
                    currencyLogDto.setChangeNum(-shopTable.getCost());
                    currencyLogDto.setPresentValue(user.getGold());
                    currencyLogDto.setCurrencyType("골드");

                } else if (shopTable.getCurrency().equals("diamond")) {
                    currencyLogDto.setPreviousValue(user.getDiamond());
                    if (!user.SpendDiamond(shopTable.getCost())) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
                    }
                    currencyLogDto.setChangeNum(-shopTable.getCost());
                    currencyLogDto.setPresentValue(user.getDiamond());
                    currencyLogDto.setCurrencyType("다이아");
                }

                List<GiftTable> giftTableList = gameDataTableService.GiftsTableList();
                GiftTable giftTableItem = giftTableList.stream()
                        .filter(a -> a.getCode().equals(myGiftItem.giftItemCode))
                        .findAny()
                        .orElse(null);
                if (giftTableItem == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: GiftTableItem not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: GiftTableItem not find.", ResponseErrorCode.NOT_FIND_DATA);
                }

                MyGiftInventory myGiftInventory = myGiftInventoryRepository.findByUseridUser(userId)
                        .orElse(null);
                if (myGiftInventory == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyGiftInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: MyGiftInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
                }
                String inventoryInfosString = myGiftInventory.getInventoryInfos();
                GiftItemDtosList giftItemInventorys = JsonStringHerlper.ReadValueFromJson(inventoryInfosString, GiftItemDtosList.class);
                GiftItemDtosList.GiftItemDto inventoryItemDto = giftItemInventorys.giftItemDtoList.stream()
                        .filter(a -> a.code.equals(myGiftItem.giftItemCode))
                        .findAny()
                        .orElse(null);
                if (inventoryItemDto == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find GiftTable", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail -> Cause: Can't Find GiftTable", ResponseErrorCode.NOT_FIND_DATA);
                }
                giftLogDto.setPreviousValue(inventoryItemDto.count);
                inventoryItemDto.AddItem(shopTable.getGettingCount(), giftTableItem.getStackLimit());
                inventoryInfosString = JsonStringHerlper.WriteValueAsStringFromData(giftItemInventorys);
                myGiftInventory.ResetInventoryInfos(inventoryInfosString);
                currencyLogDto.setWorkingPosition("상점 메인상점 구매 -> " + inventoryItemDto.code + " X " + shopTable.getGettingCount());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                giftLogDto.setGiftLogDto("상점 메인상점 구매 -> " + inventoryItemDto.code + " X " + shopTable.getGettingCount(), inventoryItemDto.code, shopTable.getGettingCount(), inventoryItemDto.count);
                String giftLog = JsonStringHerlper.WriteValueAsStringFromData(giftLogDto);
                loggingService.setLogging(userId, 4, giftLog);
                map.put("myGiftInventoryInfos", giftItemInventorys.giftItemDtoList);
            } else {
                MyShopItemsList.MyEquipmentShopItem myEquipmentShopItem = myShopMainInfos.myEquipmentShopItemList.stream()
                        .filter(a -> a.slotIndex == slotIndex)
                        .findAny()
                        .orElse(null);
                if (myEquipmentShopItem != null) {
                    //장비
                    List<HeroEquipmentInventory> heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());
                    if (heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
                    }

                    List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();


                    if (myEquipmentShopItem == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.SHOP_CANT_FIND_SHOPID.getIntegerValue(), "Fail! -> Cause: Can't Find myEquipmentShopItem.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find myEquipmentShopItem.", ResponseErrorCode.SHOP_CANT_FIND_SHOPID);
                    }
                    if (myEquipmentShopItem.bought) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_BOUGHT.getIntegerValue(), "Fail! -> Cause: Aleady bought", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Aleady bought", ResponseErrorCode.ALREADY_BOUGHT);
                    }
                    myEquipmentShopItem.bought = true;

                    ShopTable shopTable = gameDataTableService.ShopTableList().stream()
                            .filter(a -> a.getId() == myEquipmentShopItem.shopTableId)
                            .findAny()
                            .orElse(null);
                    if (shopTable == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find shopTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find shopTable.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    //가격 체크
                    if (shopTable.getCurrency().equals("gold")) {
                        currencyLogDto.setPreviousValue(user.getGold());
                        if (!user.SpendGold(shopTable.getCost())) {
                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail! -> Cause: Need more Gold.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                            throw new MyCustomException("Fail! -> Cause: Need more Gold.", ResponseErrorCode.NEED_MORE_GOLD);
                        }
                        currencyLogDto.setChangeNum(-shopTable.getCost());
                        currencyLogDto.setPresentValue(user.getGold());
                        currencyLogDto.setCurrencyType("골드");
                    } else if (shopTable.getCurrency().equals("diamond")) {
                        currencyLogDto.setPreviousValue(user.getDiamond());
                        if (!user.SpendDiamond(shopTable.getCost())) {
                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                            throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
                        }
                        currencyLogDto.setChangeNum(-shopTable.getCost());
                        currencyLogDto.setPresentValue(user.getDiamond());
                        currencyLogDto.setCurrencyType("다이아");
                    }
                    HeroEquipmentsTable heroEquipmentsTable = heroEquipmentsTableList.stream()
                            .filter(a -> a.getId() == myEquipmentShopItem.item_id)
                            .findAny()
                            .orElse(null);

                    HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
                    heroEquipmentInventoryDto.setUseridUser(userId);
                    heroEquipmentInventoryDto.setItem_Id(myEquipmentShopItem.item_id);
                    heroEquipmentInventoryDto.setItemClassValue(myEquipmentShopItem.itemClassValue);
                    heroEquipmentInventoryDto.setDecideDefaultAbilityValue(myEquipmentShopItem.decideDefaultAbilityValue);
                    heroEquipmentInventoryDto.setDecideSecondAbilityValue(myEquipmentShopItem.decideSecondAbilityValue);
                    heroEquipmentInventoryDto.setLevel(1);
                    String grade = heroEquipmentsTable.getGrade();
                    heroEquipmentInventoryDto.setMaxLevel(EquipmentCalculate.MaxLevel(grade));
                    heroEquipmentInventoryDto.setExp(0);
                    int gradeValue = EquipmentCalculate.GradeValue(grade);
                    heroEquipmentInventoryDto.setNextExp(EquipmentCalculate.CalculateNeedExp(1, gradeValue));
                    heroEquipmentInventoryDto.setItemClass(myEquipmentShopItem.itemClass);
                    heroEquipmentInventoryDto.setItemClassValue(myEquipmentShopItem.itemClassValue);
                    heroEquipmentInventoryDto.setOptionIds(myEquipmentShopItem.optionIds);
                    heroEquipmentInventoryDto.setOptionValues(myEquipmentShopItem.optionValues);
                    HeroEquipmentInventory generatedItem = heroEquipmentInventoryDto.ToEntity();
                    generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
                    heroEquipmentInventoryDto.setId(generatedItem.getId());
                    currencyLogDto.setWorkingPosition("상점 메인상점 구매 -> " + myEquipmentShopItem.itemName + " " + generatedItem.getId());
                    String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                    loggingService.setLogging(userId, 1, log);
                    EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                    equipmentLogDto.setEquipmentLogDto("상점 메인상점 구매 -> " + myEquipmentShopItem.itemName + " " + generatedItem.getId(), generatedItem.getId(), "추가", heroEquipmentInventoryDto);
                    String equipmentLog = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                    loggingService.setLogging(userId, 2, equipmentLog);
                    map.put("heroEquipmentInventory", heroEquipmentInventoryDto);

                    /* 업적 : 장비 획득 (특정 품질) 미션 체크*/
                    changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(), generatedItem.getItemClass(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                    /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
                    changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), grade, gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                } else {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find MyMainShop slotIndex.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't Find MyMainShop slotIndex.", ResponseErrorCode.NOT_FIND_DATA);
                }
            }
        }
        /* 업적 : 상점 아이템 1회 구매 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.BUY_MAIN_SHOP.name(), "empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

        /*업적 : 미션 데이터 변경점 적용*/
        if (changedMissionsData) {
            jsonMyMissionData = JsonStringHerlper.WriteValueAsStringFromData(myMissionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMyMissionData);
            map.put("exchange_myMissionsData", true);
            map.put("myMissionsDataDto", myMissionsDataDto);
            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());
        }


        json_myShopMainInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsLis);
        myShopInfo.ResetMyShopInfos(json_myShopMainInfo);
        map.put("user", user);
        map.put("recycle", false);
        map.put("myShopItemsLis", myShopItemsLis);

        /*패스 업적 : 체크 준비*/
        MyEternalPassMissionsData myEternalPassMissionsData = myEternalPassMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if (myEternalPassMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEternalPassMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyEternalPassMissionData = myEternalPassMissionsData.getJson_saveDataValue();
        EventMissionDataDto myEternalPassMissionDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyEternalPassMissionData, EventMissionDataDto.class);
        boolean changedEternalPassMissionsData = false;
        /* 패스 업적 : 상점 아이템 1회 구매 미션 체크*/
        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.BUY_MAIN_SHOP.name(), "empty", gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
        /* 패스 업적 : 미션 데이터 변경점 적용*/
        if (changedEternalPassMissionsData) {
            jsonMyEternalPassMissionData = JsonStringHerlper.WriteValueAsStringFromData(myEternalPassMissionDataDto);
            myEternalPassMissionsData.ResetSaveDataValue(jsonMyEternalPassMissionData);
            map.put("exchange_myEternalPassMissionsData", true);
            map.put("myEternalPassMissionsDataDto", myEternalPassMissionDataDto);
            map.put("myEternalPassLastDailyMissionClearTime", myEternalPassMissionsData.getLastDailyMissionClearTime());
            map.put("myEternalPassLastWeeklyMissionClearTime", myEternalPassMissionsData.getLastWeeklyMissionClearTime());
        }
        return map;
    }

    /*아레나 상점*/
    public Map<String, Object> arenaShopBuy(Long userId, int slotIndex, Map<String, Object> map) {

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
        }

        MyShopInfo myShopInfo = myShopInfoRepository.findByUseridUser(userId).orElse(null);
        if (myShopInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find myShopMain.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find myShopMain.", ResponseErrorCode.NOT_FIND_DATA);
        }
        CurrencyLogDto currencyLogDto = new CurrencyLogDto();
        String json_myShopMainInfo = myShopInfo.getJson_myShopInfos();
        MyShopItemsList myShopItemsLis = JsonStringHerlper.ReadValueFromJson(json_myShopMainInfo, MyShopItemsList.class);

        if (myShopInfo.IsMyShopRecycleTime(86400/*총 1일에 대한 초*/)) {
            myShopItemsLis.ResetAllShop(userId, gameDataTableService.ShopTableList(), gameDataTableService.ArenaShopTableList(), gameDataTableService.GiftsTableList(), gameDataTableService.HeroEquipmentsTableList(), gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1), gameDataTableService.OptionsInfoTableList(), gameDataTableService.HerosTableList(), gameDataTableService.AncientShopTableList(), gameDataTableService.CurrencyShopTableList(), gameDataTableService.SpendableItemInfoTableList(), gameDataTableService.PieceShopTableList(), gameDataTableService.DarkObeShopTableList(), gameDataTableService.PassiveItemTableList());
            json_myShopMainInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsLis);
            myShopInfo.ResetMyShopInfos(json_myShopMainInfo);
            map.put("recycle", true);
            map.put("myShopItemsLis", myShopItemsLis);
            return map;
        }
        MyShopItemsList.MyArenaShopInfos myArenaShopInfos = myShopItemsLis.myArenaShopInfos;

        //케릭터 조각 체크
        MyShopItemsList.MyCharacterPiece myCharacterPiece = myArenaShopInfos.myCharacterPieceList.stream()
                .filter(a -> a.slotIndex == slotIndex)
                .findAny()
                .orElse(null);
        if (myCharacterPiece != null) {
            if (myCharacterPiece.bought) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_BOUGHT.getIntegerValue(), "Fail! -> Cause: Aleady bought", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Aleady bought", ResponseErrorCode.ALREADY_BOUGHT);
            }
            myCharacterPiece.bought = true;
            BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();

            ArenaShopTable shopTable = gameDataTableService.ArenaShopTableList().stream()
                    .filter(a -> a.getId() == myCharacterPiece.shopTableId)
                    .findAny()
                    .orElse(null);
            if (shopTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find shopTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Can't Find shopTable.", ResponseErrorCode.NOT_FIND_DATA);
            }
            //가격 체크
            if (shopTable.getCurrency().equals("gold")) {
                currencyLogDto.setPreviousValue(user.getGold());
                if (!user.SpendGold(shopTable.getCost())) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail! -> Cause: Need more Gold.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more Gold.", ResponseErrorCode.NEED_MORE_GOLD);
                }
                currencyLogDto.setChangeNum(-shopTable.getCost());
                currencyLogDto.setCurrencyType("골드");
                currencyLogDto.setPresentValue(user.getGold());
            } else if (shopTable.getCurrency().equals("diamond")) {
                currencyLogDto.setPreviousValue(user.getDiamond());
                if (!user.SpendDiamond(shopTable.getCost())) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
                }
                currencyLogDto.setChangeNum(-shopTable.getCost());
                currencyLogDto.setCurrencyType("다이아");
                currencyLogDto.setPresentValue(user.getDiamond());
            } else if (shopTable.getCurrency().equals("arenaCoin")) {
                currencyLogDto.setPreviousValue(user.getArenaCoin());
                if (!user.SpendArenaCoin(shopTable.getCost())) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_ARENACOIN.getIntegerValue(), "Fail! -> Cause: Need more arenaCoin.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more arenaCoin.", ResponseErrorCode.NEED_MORE_ARENACOIN);
                }
                currencyLogDto.setChangeNum(-shopTable.getCost());
                currencyLogDto.setCurrencyType("아레나코인");
                currencyLogDto.setPresentValue(user.getArenaCoin());
            }

            List<ItemType> itemTypeList = itemTypeRepository.findAll();
            ItemType spendAbleItemType = itemTypeList.stream()
                    .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece)
                    .findAny()
                    .orElse(null);

            List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
            BelongingCharacterPieceTable characterPiece = belongingCharacterPieceTableList.stream()
                    .filter(a -> a.getCode().contains(myCharacterPiece.characterPieceCode))
                    .findAny()
                    .orElse(null);
            if (characterPiece == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find characterPiece.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Can't Find characterPiece.", ResponseErrorCode.NOT_FIND_DATA);
            }

            List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
            BelongingInventory myCharacterPieceItem = belongingInventoryList.stream()
                    .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece && a.getItemId() == characterPiece.getId())
                    .findAny()
                    .orElse(null);

            if (myCharacterPieceItem == null) {
                BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                belongingInventoryDto.setUseridUser(userId);
                belongingInventoryDto.setItemId(characterPiece.getId());
                belongingInventoryDto.setCount(shopTable.getGettingCount());
                belongingInventoryDto.setItemType(spendAbleItemType);
                myCharacterPieceItem = belongingInventoryDto.ToEntity();
                myCharacterPieceItem = belongingInventoryRepository.save(myCharacterPieceItem);
                belongingInventoryList.add(myCharacterPieceItem);
                belongingInventoryLogDto.setPreviousValue(0);
                belongingInventoryLogDto.setBelongingInventoryLogDto("상점 아레나상점 구매 -> " + myCharacterPiece.itemName + " X " + shopTable.getGettingCount(), myCharacterPieceItem.getId(),
                        myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), shopTable.getGettingCount(), myCharacterPieceItem.getCount());
            } else {
                belongingInventoryLogDto.setPreviousValue(myCharacterPieceItem.getCount());
                myCharacterPieceItem.AddItem(shopTable.getGettingCount(), characterPiece.getStackLimit());
                belongingInventoryLogDto.setBelongingInventoryLogDto("상점 아레나상점 구매 -> " + myCharacterPiece.itemName + " X " + shopTable.getGettingCount(), myCharacterPieceItem.getId(),
                        myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), shopTable.getGettingCount(), myCharacterPieceItem.getCount());
            }
            currencyLogDto.setWorkingPosition("상점 아레나상점 구매 -> " + myCharacterPiece.itemName + " X " + shopTable.getGettingCount());
            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
            loggingService.setLogging(userId, 1, log);
            String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, belongingLog);
            map.put("myCharacterPieceItem", myCharacterPieceItem);
        }
        //선물
        else {
            MyShopItemsList.MyGiftItem myGiftItem = myArenaShopInfos.myGiftItemList.stream()
                    .filter(a -> a.slotIndex == slotIndex)
                    .findAny()
                    .orElse(null);
            if (myGiftItem != null) {
                if (myGiftItem.bought) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_BOUGHT.getIntegerValue(), "Fail! -> Cause: Aleady bought", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Already bought", ResponseErrorCode.ALREADY_BOUGHT);
                }
                myGiftItem.bought = true;
                ArenaShopTable shopTable = gameDataTableService.ArenaShopTableList().stream()
                        .filter(a -> a.getId() == myGiftItem.shopTableId)
                        .findAny()
                        .orElse(null);
                if (shopTable == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find shopTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't Find shopTable.", ResponseErrorCode.NOT_FIND_DATA);
                }
                //가격 체크
                if (shopTable.getCurrency().equals("gold")) {
                    currencyLogDto.setPreviousValue(user.getGold());
                    if (!user.SpendGold(shopTable.getCost())) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail! -> Cause: Need more Gold.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Need more Gold.", ResponseErrorCode.NEED_MORE_GOLD);
                    }
                    currencyLogDto.setChangeNum(-shopTable.getCost());
                    currencyLogDto.setCurrencyType("골드");
                    currencyLogDto.setPresentValue(user.getGold());
                } else if (shopTable.getCurrency().equals("diamond")) {
                    currencyLogDto.setPreviousValue(user.getDiamond());
                    if (!user.SpendDiamond(shopTable.getCost())) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
                    }
                    currencyLogDto.setChangeNum(-shopTable.getCost());
                    currencyLogDto.setCurrencyType("다이아");
                    currencyLogDto.setPresentValue(user.getDiamond());
                } else if (shopTable.getCurrency().equals("arenaCoin")) {
                    currencyLogDto.setPreviousValue(user.getArenaCoin());
                    if (!user.SpendArenaCoin(shopTable.getCost())) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_ARENACOIN.getIntegerValue(), "Fail! -> Cause: Need more arenaCoin.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Need more arenaCoin.", ResponseErrorCode.NEED_MORE_ARENACOIN);
                    }
                    currencyLogDto.setChangeNum(-shopTable.getCost());
                    currencyLogDto.setCurrencyType("아레나코인");
                    currencyLogDto.setPresentValue(user.getArenaCoin());
                }
                List<GiftTable> giftTableList = gameDataTableService.GiftsTableList();
                GiftTable giftTableItem = giftTableList.stream()
                        .filter(a -> a.getCode().equals(myGiftItem.giftItemCode))
                        .findAny()
                        .orElse(null);
                if (giftTableItem == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: GiftTableItem not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: GiftTableItem not find.", ResponseErrorCode.NOT_FIND_DATA);
                }

                MyGiftInventory myGiftInventory = myGiftInventoryRepository.findByUseridUser(userId)
                        .orElse(null);
                if (myGiftInventory == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyGiftInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: MyGiftInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
                }
                String inventoryInfosString = myGiftInventory.getInventoryInfos();
                GiftItemDtosList giftItemInventorys = JsonStringHerlper.ReadValueFromJson(inventoryInfosString, GiftItemDtosList.class);
                GiftItemDtosList.GiftItemDto inventoryItemDto = giftItemInventorys.giftItemDtoList.stream()
                        .filter(a -> a.code.equals(myGiftItem.giftItemCode))
                        .findAny()
                        .orElse(null);
                if (inventoryItemDto == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find GiftTable", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail -> Cause: Can't Find GiftTable", ResponseErrorCode.NOT_FIND_DATA);
                }
                GiftLogDto giftLogDto = new GiftLogDto();
                giftLogDto.setPreviousValue(inventoryItemDto.count);
                inventoryItemDto.AddItem(shopTable.getGettingCount(), giftTableItem.getStackLimit());
                inventoryInfosString = JsonStringHerlper.WriteValueAsStringFromData(giftItemInventorys);
                myGiftInventory.ResetInventoryInfos(inventoryInfosString);
                currencyLogDto.setWorkingPosition("상점 아레나상점 구매 -> " + inventoryItemDto.code + " X " + shopTable.getGettingCount());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                giftLogDto.setGiftLogDto("상점 아레나상점 구매 -> " + inventoryItemDto.code + " X " + shopTable.getGettingCount(), inventoryItemDto.code, shopTable.getGettingCount(), inventoryItemDto.count);
                String giftLog = JsonStringHerlper.WriteValueAsStringFromData(giftLogDto);
                loggingService.setLogging(userId, 4, giftLog);

                map.put("myGiftInventoryInfos", giftItemInventorys.giftItemDtoList);
            }
            //강화석
            else {
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                MyShopItemsList.MyEnchantItem myEnchantItem = myArenaShopInfos.myEnchantItemList.stream()
                        .filter(a -> a.slotIndex == slotIndex)
                        .findAny()
                        .orElse(null);
                if (myEnchantItem != null) {
                    if (myEnchantItem.bought) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_BOUGHT.getIntegerValue(), "Fail! -> Cause: Aleady bought", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Aleady bought", ResponseErrorCode.ALREADY_BOUGHT);
                    }
                    myEnchantItem.bought = true;

                    ArenaShopTable shopTable = gameDataTableService.ArenaShopTableList().stream()
                            .filter(a -> a.getId() == myEnchantItem.shopTableId)
                            .findAny()
                            .orElse(null);
                    if (shopTable == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find shopTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find shopTable.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    //가격 체크
                    if (shopTable.getCurrency().equals("gold")) {
                        currencyLogDto.setPreviousValue(user.getGold());
                        if (!user.SpendGold(shopTable.getCost())) {
                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail! -> Cause: Need more Gold.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                            throw new MyCustomException("Fail! -> Cause: Need more Gold.", ResponseErrorCode.NEED_MORE_GOLD);
                        }
                        currencyLogDto.setChangeNum(-shopTable.getCost());
                        currencyLogDto.setCurrencyType("골드");
                        currencyLogDto.setPresentValue(user.getGold());
                    } else if (shopTable.getCurrency().equals("diamond")) {
                        currencyLogDto.setPreviousValue(user.getDiamond());
                        if (!user.SpendDiamond(shopTable.getCost())) {
                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                            throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
                        }
                        currencyLogDto.setChangeNum(-shopTable.getCost());
                        currencyLogDto.setCurrencyType("다이아");
                        currencyLogDto.setPresentValue(user.getDiamond());
                    } else if (shopTable.getCurrency().equals("arenaCoin")) {
                        currencyLogDto.setPreviousValue(user.getArenaCoin());
                        if (!user.SpendArenaCoin(shopTable.getCost())) {
                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_ARENACOIN.getIntegerValue(), "Fail! -> Cause: Need more arenaCoin.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                            throw new MyCustomException("Fail! -> Cause: Need more arenaCoin.", ResponseErrorCode.NEED_MORE_ARENACOIN);
                        }
                        currencyLogDto.setChangeNum(-shopTable.getCost());
                        currencyLogDto.setCurrencyType("아레나코인");
                        currencyLogDto.setPresentValue(user.getArenaCoin());
                    }

                    List<ItemType> itemTypeList = itemTypeRepository.findAll();
                    ItemType spendAbleItemType = itemTypeList.stream()
                            .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                            .findAny()
                            .orElse(null);

                    List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
                    SpendableItemInfoTable enchantStone = spendableItemInfoTableList.stream()
                            .filter(a -> a.getCode().equals(myEnchantItem.itemCode))
                            .findAny()
                            .orElse(null);

                    List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                    BelongingInventory myEnchantStone = belongingInventoryList.stream()
                            .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == enchantStone.getId())
                            .findAny()
                            .orElse(null);

                    if (myEnchantStone == null) {
                        BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                        belongingInventoryDto.setUseridUser(userId);
                        belongingInventoryDto.setItemId(enchantStone.getId());
                        belongingInventoryDto.setCount(shopTable.getGettingCount());
                        belongingInventoryDto.setItemType(spendAbleItemType);
                        myEnchantStone = belongingInventoryDto.ToEntity();
                        myEnchantStone = belongingInventoryRepository.save(myEnchantStone);
                        belongingInventoryList.add(myEnchantStone);
                        belongingInventoryLogDto.setPreviousValue(0);
                        belongingInventoryLogDto.setBelongingInventoryLogDto("상점 아레나상점 구매 -> " + shopTable.getGettingItem() + " X " + shopTable.getGettingCount(), myEnchantStone.getId(), myEnchantStone.getItemId(),
                                myEnchantStone.getItemType(), shopTable.getGettingCount(), myEnchantStone.getCount());
                    } else {
                        belongingInventoryLogDto.setPreviousValue(myEnchantStone.getCount());
                        myEnchantStone.AddItem(shopTable.getGettingCount(), enchantStone.getStackLimit());
                        belongingInventoryLogDto.setBelongingInventoryLogDto("상점 아레나상점 구매 -> " + shopTable.getGettingItem() + " X " + shopTable.getGettingCount(), myEnchantStone.getId(), myEnchantStone.getItemId(),
                                myEnchantStone.getItemType(), shopTable.getGettingCount(), myEnchantStone.getCount());
                    }
                    BelongingInventoryDto myEnchantStoneDto = new BelongingInventoryDto();
                    myEnchantStoneDto.InitFromDbData(myEnchantStone);
                    currencyLogDto.setWorkingPosition("상점 아레나상점 구매 -> " + shopTable.getGettingItem() + " X " + shopTable.getGettingCount());
                    String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                    loggingService.setLogging(userId, 1, log);
                    String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                    loggingService.setLogging(userId, 3, belongingLog);
                    map.put("enchantStone", myEnchantStoneDto);
                }
                else {
                    MyShopItemsList.MyResmeltItem myResmeltItem = myArenaShopInfos.myResmeltItemList.stream()
                            .filter(a -> a.slotIndex == slotIndex)
                            .findAny()
                            .orElse(null);
                    if (myResmeltItem != null) {
                        if (myResmeltItem.bought) {
                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_BOUGHT.getIntegerValue(), "Fail! -> Cause: Aleady bought", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                            throw new MyCustomException("Fail! -> Cause: Aleady bought", ResponseErrorCode.ALREADY_BOUGHT);
                        }
                        myResmeltItem.bought = true;

                        ArenaShopTable shopTable = gameDataTableService.ArenaShopTableList().stream()
                                .filter(a -> a.getId() == myResmeltItem.shopTableId)
                                .findAny()
                                .orElse(null);
                        if (shopTable == null) {
                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find shopTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                            throw new MyCustomException("Fail! -> Cause: Can't Find shopTable.", ResponseErrorCode.NOT_FIND_DATA);
                        }
                        //가격 체크
                        if (shopTable.getCurrency().equals("gold")) {
                            currencyLogDto.setPreviousValue(user.getGold());
                            if (!user.SpendGold(shopTable.getCost())) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail! -> Cause: Need more Gold.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Fail! -> Cause: Need more Gold.", ResponseErrorCode.NEED_MORE_GOLD);
                            }
                            currencyLogDto.setChangeNum(-shopTable.getCost());
                            currencyLogDto.setCurrencyType("골드");
                            currencyLogDto.setPresentValue(user.getGold());
                        } else if (shopTable.getCurrency().equals("diamond")) {
                            currencyLogDto.setPreviousValue(user.getDiamond());
                            if (!user.SpendDiamond(shopTable.getCost())) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
                            }
                            currencyLogDto.setChangeNum(-shopTable.getCost());
                            currencyLogDto.setCurrencyType("다이아");
                            currencyLogDto.setPresentValue(user.getDiamond());
                        } else if (shopTable.getCurrency().equals("arenaCoin")) {
                            currencyLogDto.setPreviousValue(user.getArenaCoin());
                            if (!user.SpendArenaCoin(shopTable.getCost())) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_ARENACOIN.getIntegerValue(), "Fail! -> Cause: Need more arenaCoin.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Fail! -> Cause: Need more arenaCoin.", ResponseErrorCode.NEED_MORE_ARENACOIN);
                            }
                            currencyLogDto.setChangeNum(-shopTable.getCost());
                            currencyLogDto.setCurrencyType("아레나코인");
                            currencyLogDto.setPresentValue(user.getArenaCoin());
                        }

                        List<ItemType> itemTypeList = itemTypeRepository.findAll();
                        ItemType spendAbleItemType = itemTypeList.stream()
                                .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                                .findAny()
                                .orElse(null);

                        List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
                        SpendableItemInfoTable resmeltStone = spendableItemInfoTableList.stream()
                                .filter(a -> a.getCode().equals(myResmeltItem.itemCode))
                                .findAny()
                                .orElse(null);

                        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                        BelongingInventory myResmeltStone = belongingInventoryList.stream()
                                .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == resmeltStone.getId())
                                .findAny()
                                .orElse(null);

                        if (myResmeltStone == null) {
                            BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                            belongingInventoryDto.setUseridUser(userId);
                            belongingInventoryDto.setItemId(resmeltStone.getId());
                            belongingInventoryDto.setCount(shopTable.getGettingCount());
                            belongingInventoryDto.setItemType(spendAbleItemType);
                            myResmeltStone = belongingInventoryDto.ToEntity();
                            myResmeltStone = belongingInventoryRepository.save(myResmeltStone);
                            belongingInventoryList.add(myResmeltStone);
                            belongingInventoryLogDto.setPreviousValue(0);
                            belongingInventoryLogDto.setBelongingInventoryLogDto("상점 아레나상점 구매 -> " + shopTable.getGettingItem() + " X " + shopTable.getGettingCount(), myResmeltStone.getId(), myResmeltStone.getItemId(),
                                    myResmeltStone.getItemType(), shopTable.getGettingCount(), myResmeltStone.getCount());
                        } else {
                            belongingInventoryLogDto.setPreviousValue(myResmeltStone.getCount());
                            myResmeltStone.AddItem(shopTable.getGettingCount(), resmeltStone.getStackLimit());
                            belongingInventoryLogDto.setBelongingInventoryLogDto("상점 아레나상점 구매 -> " + shopTable.getGettingItem() + " X " + shopTable.getGettingCount(), myResmeltStone.getId(), myResmeltStone.getItemId(),
                                    myResmeltStone.getItemType(), shopTable.getGettingCount(), myResmeltStone.getCount());
                        }
                        BelongingInventoryDto myResmeltStoneDto = new BelongingInventoryDto();
                        myResmeltStoneDto.InitFromDbData(myResmeltStone);
                        currencyLogDto.setWorkingPosition("상점 아레나상점 구매 -> " + shopTable.getGettingItem() + " X " + shopTable.getGettingCount());
                        String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                        loggingService.setLogging(userId, 1, log);
                        String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                        loggingService.setLogging(userId, 3, belongingLog);
                        map.put("resmeltStone", myResmeltStoneDto);
                    } else {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find ArenaShop slotIndex.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find ArenaShop slotIndex.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
            }
        }

        json_myShopMainInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsLis);
        myShopInfo.ResetMyShopInfos(json_myShopMainInfo);
        map.put("user", user);
        map.put("recycle", false);
        map.put("myShopItemsLis", myShopItemsLis);

        /*업적 : 체크 준비*/
        MyMissionsData myMissionsData = myMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if (myMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyMissionData = myMissionsData.getJson_saveDataValue();
        MissionsDataDto myMissionsDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyMissionData, MissionsDataDto.class);
        boolean changedMissionsData = false;
        /* 업적 : 아레나 상점 아이템 1회 구매 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.BUY_ARENA_SHOP.name(), "empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

        /*업적 : 미션 데이터 변경점 적용*/
        if (changedMissionsData) {
            jsonMyMissionData = JsonStringHerlper.WriteValueAsStringFromData(myMissionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMyMissionData);
            map.put("exchange_myMissionsData", true);
            map.put("myMissionsDataDto", myMissionsDataDto);
            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());
        }

        /*패스 업적 : 체크 준비*/
        MyEternalPassMissionsData myEternalPassMissionsData = myEternalPassMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if (myEternalPassMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEternalPassMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyEternalPassMissionData = myEternalPassMissionsData.getJson_saveDataValue();
        EventMissionDataDto myEternalPassMissionDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyEternalPassMissionData, EventMissionDataDto.class);
        boolean changedEternalPassMissionsData = false;
        /* 패스 업적 : 아레나 상점 아이템 1회 구매 미션 체크*/
        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.BUY_ARENA_SHOP.name(), "empty", gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;

        /* 패스 업적 : 미션 데이터 변경점 적용*/
        if (changedEternalPassMissionsData) {
            jsonMyEternalPassMissionData = JsonStringHerlper.WriteValueAsStringFromData(myEternalPassMissionDataDto);
            myEternalPassMissionsData.ResetSaveDataValue(jsonMyEternalPassMissionData);
            map.put("exchange_myEternalPassMissionsData", true);
            map.put("myEternalPassMissionsDataDto", myEternalPassMissionDataDto);
            map.put("myEternalPassLastDailyMissionClearTime", myEternalPassMissionsData.getLastDailyMissionClearTime());
            map.put("myEternalPassLastWeeklyMissionClearTime", myEternalPassMissionsData.getLastWeeklyMissionClearTime());
        }
        return map;
    }

    /*고대 상점*/
    public Map<String, Object> ancientShopBuy(Long userId, int slotIndex, Map<String, Object> map) {

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
        }

        MyShopInfo myShopInfo = myShopInfoRepository.findByUseridUser(userId).orElse(null);
        if (myShopInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find myShopMain.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find myShopMain.", ResponseErrorCode.NOT_FIND_DATA);
        }
        CurrencyLogDto currencyLogDto = new CurrencyLogDto();
        String json_myShopMainInfo = myShopInfo.getJson_myShopInfos();
        MyShopItemsList myShopItemsLis = JsonStringHerlper.ReadValueFromJson(json_myShopMainInfo, MyShopItemsList.class);

        if (myShopInfo.IsMyShopRecycleTime(86400/*총 1일에 대한 초*/)) {
            myShopItemsLis.ResetAllShop(userId, gameDataTableService.ShopTableList(), gameDataTableService.ArenaShopTableList(), gameDataTableService.GiftsTableList(), gameDataTableService.HeroEquipmentsTableList(), gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1), gameDataTableService.OptionsInfoTableList(), gameDataTableService.HerosTableList(), gameDataTableService.AncientShopTableList(), gameDataTableService.CurrencyShopTableList(), gameDataTableService.SpendableItemInfoTableList(), gameDataTableService.PieceShopTableList(), gameDataTableService.DarkObeShopTableList(), gameDataTableService.PassiveItemTableList());
            json_myShopMainInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsLis);
            myShopInfo.ResetMyShopInfos(json_myShopMainInfo);

            map.put("recycle", true);
            map.put("myShopItemsLis", myShopItemsLis);
            map.put("scheduleStartTime", myShopInfo.getScheduleStartTime());
            return map;
        }

        /*업적 : 체크 준비*/
        MyMissionsData myMissionsData = myMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if (myMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyMissionData = myMissionsData.getJson_saveDataValue();
        MissionsDataDto myMissionsDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyMissionData, MissionsDataDto.class);
        boolean changedMissionsData = false;

        MyShopItemsList.MyAncientShopInfos myAncientShopInfos = myShopItemsLis.myAncientShopInfos;
        //장비박스
        MyShopItemsList.MyShopItem myEquipmentBox = myAncientShopInfos.myEquipmentList.stream()
                .filter(a -> a.slotIndex == slotIndex)
                .findAny()
                .orElse(null);
        if (myEquipmentBox != null) {
            List<HeroEquipmentInventory> heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());
            if (heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
            }

            if (myEquipmentBox.bought) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_BOUGHT.getIntegerValue(), "Fail! -> Cause: Aleady bought", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Aleady bought", ResponseErrorCode.ALREADY_BOUGHT);
            }
            myEquipmentBox.bought = true;

            AncientShopTable shopTable = gameDataTableService.AncientShopTableList().stream()
                    .filter(a -> a.getId() == myEquipmentBox.shopTableId)
                    .findAny()
                    .orElse(null);
            if (shopTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find shopTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Can't Find shopTable.", ResponseErrorCode.NOT_FIND_DATA);
            }
            //가격 체크
            if (shopTable.getCurrency().equals("gold")) {
                currencyLogDto.setPreviousValue(user.getGold());
                if (!user.SpendGold(shopTable.getCost())) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail! -> Cause: Need more Gold.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more Gold.", ResponseErrorCode.NEED_MORE_GOLD);
                }
                currencyLogDto.setChangeNum(-shopTable.getCost());
                currencyLogDto.setCurrencyType("골드");
                currencyLogDto.setPresentValue(user.getGold());
            } else if (shopTable.getCurrency().equals("diamond")) {
                currencyLogDto.setPreviousValue(user.getDiamond());
                if (!user.SpendDiamond(shopTable.getCost())) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
                }
                currencyLogDto.setChangeNum(-shopTable.getCost());
                currencyLogDto.setCurrencyType("다이아");
                currencyLogDto.setPresentValue(user.getDiamond());
            } else if (shopTable.getCurrency().equals("arenaCoin")) {
                currencyLogDto.setPreviousValue(user.getArenaCoin());
                if (!user.SpendArenaCoin(shopTable.getCost())) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_ARENACOIN.getIntegerValue(), "Fail! -> Cause: Need more arenaCoin.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more arenaCoin.", ResponseErrorCode.NEED_MORE_ARENACOIN);
                }
                currencyLogDto.setChangeNum(-shopTable.getCost());
                currencyLogDto.setCurrencyType("아레나코인");
                currencyLogDto.setPresentValue(user.getArenaCoin());
            } else if (shopTable.getCurrency().equals("lowDragonScale")) {
                currencyLogDto.setPreviousValue(user.getLowDragonScale());
                if (!user.SpendLowDragonScale(shopTable.getCost())) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_LOWDRAGONSCALE.getIntegerValue(), "Fail! -> Cause: Need more lowDragonScale.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more lowDragonScale.", ResponseErrorCode.NEED_MORE_LOWDRAGONSCALE);
                }
                currencyLogDto.setChangeNum(-shopTable.getCost());
                currencyLogDto.setCurrencyType("용의 비늘(전설)");
                currencyLogDto.setPresentValue(user.getLowDragonScale());
            } else if (shopTable.getCurrency().equals("middleDragonScale")) {
                currencyLogDto.setPreviousValue(user.getMiddleDragonScale());
                if (!user.SpendMiddleDragonScale(shopTable.getCost())) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_MIDDLEDRAGONSCALE.getIntegerValue(), "Fail! -> Cause: Need more lowDragonScale.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more lowDragonScale.", ResponseErrorCode.NEED_MORE_MIDDLEDRAGONSCALE);
                }
                currencyLogDto.setChangeNum(-shopTable.getCost());
                currencyLogDto.setCurrencyType("용의 비늘(신성)");
                currencyLogDto.setPresentValue(user.getMiddleDragonScale());
            } else if (shopTable.getCurrency().equals("highDragonScale")) {
                currencyLogDto.setPreviousValue(user.getHighDragonScale());
                if (!user.SpendHightDragonScale(shopTable.getCost())) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_HIGHDRAGONSCALE.getIntegerValue(), "Fail! -> Cause: Need more lowDragonScale.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more lowDragonScale.", ResponseErrorCode.NEED_MORE_HIGHDRAGONSCALE);
                }
                currencyLogDto.setChangeNum(-shopTable.getCost());
                currencyLogDto.setCurrencyType("용의 비늘(고대)");
                currencyLogDto.setPresentValue(user.getHighDragonScale());
            }

            if (shopTable.getGettingItem().equals("weapon_legend")) {
                List<Double> classProbabilityList = new ArrayList<>();
                //0.9%	15.0%	40.0%	32.0%	10.0%	2.0%	0.1%
                classProbabilityList.add(0.1D);
                classProbabilityList.add(2D);
                classProbabilityList.add(10D);
                classProbabilityList.add(32D);
                classProbabilityList.add(40D);
                classProbabilityList.add(15D);
                classProbabilityList.add(0.9D);

                int classIndex = MathHelper.RandomIndexWidthProbability(classProbabilityList);

                String itemClass = EquipmentCalculate.classCategoryArray[classIndex];
                List<HeroEquipmentClassProbabilityTable> heroEquipmentClassProbabilityTables = gameDataTableService.HeroEquipmentClassProbabilityTableList();
                int classValue = (int) EquipmentCalculate.GetClassValueFromClassCategory(itemClass, heroEquipmentClassProbabilityTables.get(1));

                List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
                List<HeroEquipmentsTable> probabilityList = EquipmentCalculate.GetProbabilityList(heroEquipmentsTableList, EquipmentItemCategory.ALL, "Legend");

                int randValue = (int) MathHelper.Range(0, probabilityList.size());
                HeroEquipmentsTable selectEquipment = probabilityList.get(randValue);
                List<EquipmentOptionsInfoTable> optionsInfoTableList = gameDataTableService.OptionsInfoTableList();

                HeroEquipmentInventory generatedItem = EquipmentCalculate.CreateEquipment(userId, selectEquipment, itemClass, classValue, optionsInfoTableList);
                generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
                HeroEquipmentInventoryDto generatedItemDto = new HeroEquipmentInventoryDto();
                generatedItemDto.InitFromDbData(generatedItem);
                currencyLogDto.setWorkingPosition("상점 고대상점 구매 -> " + selectEquipment.getName() + " [" + generatedItem.getId() + "]");
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                equipmentLogDto.setEquipmentLogDto("상점 고대상점 구매 -> " + selectEquipment.getName() + " [" + generatedItem.getId() + "]", generatedItem.getId(), "추가", generatedItemDto);
                String equipmentLog = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                loggingService.setLogging(userId, 2, equipmentLog);
                map.put("item", generatedItemDto);

                /* 업적 : 장비 획득 (특정 품질) 미션 체크*/
                changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(), generatedItem.getItemClass(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
                changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), selectEquipment.getGrade(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
            }
            else if (shopTable.getGettingItem().equals("weapon_divine")) {
                List<Double> classProbabilityList = new ArrayList<>();
                //0.9%	15.0%	40.0%	32.0%	10.0%	2.0%	0.1%
                classProbabilityList.add(0.1D);
                classProbabilityList.add(2D);
                classProbabilityList.add(10D);
                classProbabilityList.add(32D);
                classProbabilityList.add(40D);
                classProbabilityList.add(15D);
                classProbabilityList.add(0.9D);

                int classIndex = MathHelper.RandomIndexWidthProbability(classProbabilityList);

                String itemClass = EquipmentCalculate.classCategoryArray[classIndex];
                List<HeroEquipmentClassProbabilityTable> heroEquipmentClassProbabilityTables = gameDataTableService.HeroEquipmentClassProbabilityTableList();
                int classValue = (int) EquipmentCalculate.GetClassValueFromClassCategory(itemClass, heroEquipmentClassProbabilityTables.get(1));

                List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
                List<HeroEquipmentsTable> probabilityList = EquipmentCalculate.GetProbabilityList(heroEquipmentsTableList, EquipmentItemCategory.ALL, "Divine");

                int randValue = (int) MathHelper.Range(0, probabilityList.size());
                HeroEquipmentsTable selectEquipment = probabilityList.get(randValue);
                List<EquipmentOptionsInfoTable> optionsInfoTableList = gameDataTableService.OptionsInfoTableList();

                HeroEquipmentInventory generatedItem = EquipmentCalculate.CreateEquipment(userId, selectEquipment, itemClass, classValue, optionsInfoTableList);
                generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
                HeroEquipmentInventoryDto generatedItemDto = new HeroEquipmentInventoryDto();
                generatedItemDto.InitFromDbData(generatedItem);
                currencyLogDto.setWorkingPosition("상점 고대상점 구매 -> " + selectEquipment.getName() + " [" + generatedItem.getId() + "]");
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                equipmentLogDto.setEquipmentLogDto("상점 고대상점 구매 -> " + selectEquipment.getName() + " [" + generatedItem.getId() + "]", generatedItem.getId(), "추가", generatedItemDto);
                String equipmentLog = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                loggingService.setLogging(userId, 2, equipmentLog);
                map.put("item", generatedItemDto);

                /* 업적 : 장비 획득 (특정 품질) 미션 체크*/
                changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(), generatedItem.getItemClass(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
                changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), selectEquipment.getGrade(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
            }
            else if (shopTable.getGettingItem().equals("weapon_ancient")) {
                List<Double> classProbabilityList = new ArrayList<>();
                //0.9%	15.0%	40.0%	32.0%	10.0%	2.0%	0.1%
                classProbabilityList.add(0.1D);
                classProbabilityList.add(2D);
                classProbabilityList.add(10D);
                classProbabilityList.add(32D);
                classProbabilityList.add(40D);
                classProbabilityList.add(15D);
                classProbabilityList.add(0.9D);

                int classIndex = MathHelper.RandomIndexWidthProbability(classProbabilityList);

                String itemClass = EquipmentCalculate.classCategoryArray[classIndex];
                List<HeroEquipmentClassProbabilityTable> heroEquipmentClassProbabilityTables = gameDataTableService.HeroEquipmentClassProbabilityTableList();
                int classValue = (int) EquipmentCalculate.GetClassValueFromClassCategory(itemClass, heroEquipmentClassProbabilityTables.get(1));

                List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
                List<HeroEquipmentsTable> probabilityList = EquipmentCalculate.GetProbabilityList(heroEquipmentsTableList, EquipmentItemCategory.ALL, "Ancient");

                int randValue = (int) MathHelper.Range(0, probabilityList.size());
                HeroEquipmentsTable selectEquipment = probabilityList.get(randValue);
                List<EquipmentOptionsInfoTable> optionsInfoTableList = gameDataTableService.OptionsInfoTableList();

                HeroEquipmentInventory generatedItem = EquipmentCalculate.CreateEquipment(userId, selectEquipment, itemClass, classValue, optionsInfoTableList);
                generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
                HeroEquipmentInventoryDto generatedItemDto = new HeroEquipmentInventoryDto();
                generatedItemDto.InitFromDbData(generatedItem);
                currencyLogDto.setWorkingPosition("상점 고대상점 구매 -> " + selectEquipment.getName() + " [" + generatedItem.getId() + "]");
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                equipmentLogDto.setEquipmentLogDto("상점 고대상점 구매 -> " + selectEquipment.getName() + " [" + generatedItem.getId() + "]", generatedItem.getId(), "추가", generatedItemDto);
                String equipmentLog = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                loggingService.setLogging(userId, 2, equipmentLog);
                map.put("item", generatedItemDto);

                /* 업적 : 장비 획득 (특정 품질) 미션 체크*/
                changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(), generatedItem.getItemClass(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
                changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), selectEquipment.getGrade(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
            }
        }
        //링크웨폰 강화 재료 키
        else {
            MyShopItemsList.MyLinkweaponKeyItem myLinkweaponKeyItem = myAncientShopInfos.myLinkweaponKeyItemList.stream()
                    .filter(a -> a.slotIndex == slotIndex)
                    .findAny()
                    .orElse(null);
            if (myLinkweaponKeyItem != null) {
                if (myLinkweaponKeyItem.bought) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_BOUGHT.getIntegerValue(), "Fail! -> Cause: Aleady bought", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Aleady bought", ResponseErrorCode.ALREADY_BOUGHT);
                }
                myLinkweaponKeyItem.bought = true;
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();

                AncientShopTable shopTable = gameDataTableService.AncientShopTableList().stream()
                        .filter(a -> a.getId() == myLinkweaponKeyItem.shopTableId)
                        .findAny()
                        .orElse(null);
                if (shopTable == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find shopTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't Find shopTable.", ResponseErrorCode.NOT_FIND_DATA);
                }

                //가격 체크
                if (shopTable.getCurrency().equals("gold")) {
                    currencyLogDto.setPreviousValue(user.getGold());
                    if (!user.SpendGold(shopTable.getCost())) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail! -> Cause: Need more Gold.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Need more Gold.", ResponseErrorCode.NEED_MORE_GOLD);
                    }
                    currencyLogDto.setChangeNum(-shopTable.getCost());
                    currencyLogDto.setCurrencyType("골드");
                    currencyLogDto.setPresentValue(user.getGold());
                } else if (shopTable.getCurrency().equals("diamond")) {
                    currencyLogDto.setPreviousValue(user.getDiamond());
                    if (!user.SpendDiamond(shopTable.getCost())) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
                    }
                    currencyLogDto.setChangeNum(-shopTable.getCost());
                    currencyLogDto.setCurrencyType("다이아");
                    currencyLogDto.setPresentValue(user.getDiamond());
                } else if (shopTable.getCurrency().equals("arenaCoin")) {
                    currencyLogDto.setPreviousValue(user.getArenaCoin());
                    if (!user.SpendArenaCoin(shopTable.getCost())) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_ARENACOIN.getIntegerValue(), "Fail! -> Cause: Need more arenaCoin.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Need more arenaCoin.", ResponseErrorCode.NEED_MORE_ARENACOIN);
                    }
                    currencyLogDto.setChangeNum(-shopTable.getCost());
                    currencyLogDto.setCurrencyType("아레나코인");
                    currencyLogDto.setPresentValue(user.getArenaCoin());
                } else if (shopTable.getCurrency().equals("lowDragonScale")) {
                    currencyLogDto.setPreviousValue(user.getLowDragonScale());
                    if (!user.SpendLowDragonScale(shopTable.getCost())) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_LOWDRAGONSCALE.getIntegerValue(), "Fail! -> Cause: Need more lowDragonScale.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Need more lowDragonScale.", ResponseErrorCode.NEED_MORE_LOWDRAGONSCALE);
                    }
                    currencyLogDto.setChangeNum(-shopTable.getCost());
                    currencyLogDto.setCurrencyType("용의 비늘(전설)");
                    currencyLogDto.setPresentValue(user.getLowDragonScale());
                } else if (shopTable.getCurrency().equals("middleDragonScale")) {
                    currencyLogDto.setPreviousValue(user.getMiddleDragonScale());
                    if (!user.SpendMiddleDragonScale(shopTable.getCost())) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_MIDDLEDRAGONSCALE.getIntegerValue(), "Fail! -> Cause: Need more lowDragonScale.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Need more lowDragonScale.", ResponseErrorCode.NEED_MORE_MIDDLEDRAGONSCALE);
                    }
                    currencyLogDto.setChangeNum(-shopTable.getCost());
                    currencyLogDto.setCurrencyType("용의 비늘(신성)");
                    currencyLogDto.setPresentValue(user.getMiddleDragonScale());
                } else if (shopTable.getCurrency().equals("highDragonScale")) {
                    currencyLogDto.setPreviousValue(user.getHighDragonScale());
                    if (!user.SpendHightDragonScale(shopTable.getCost())) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_HIGHDRAGONSCALE.getIntegerValue(), "Fail! -> Cause: Need more lowDragonScale.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Need more lowDragonScale.", ResponseErrorCode.NEED_MORE_HIGHDRAGONSCALE);
                    }
                    currencyLogDto.setChangeNum(-shopTable.getCost());
                    currencyLogDto.setCurrencyType("용의 비늘(고대)");
                    currencyLogDto.setPresentValue(user.getHighDragonScale());
                }

                if (shopTable.getGettingItem().equals("linkweapon_bronzeKey")) {
                    List<ItemType> itemTypeList = itemTypeRepository.findAll();
                    ItemType spendAbleItemType = itemTypeList.stream()
                            .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                            .findAny()
                            .orElse(null);

                    List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
                    SpendableItemInfoTable linkweaponKey = spendableItemInfoTableList.stream()
                            .filter(a -> a.getCode().equals(myLinkweaponKeyItem.itemCode))
                            .findAny()
                            .orElse(null);

                    List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                    BelongingInventory myLinkweaponKey = belongingInventoryList.stream()
                            .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == linkweaponKey.getId())
                            .findAny()
                            .orElse(null);

                    if (myLinkweaponKey == null) {
                        BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                        belongingInventoryDto.setUseridUser(userId);
                        belongingInventoryDto.setItemId(linkweaponKey.getId());
                        belongingInventoryDto.setCount(shopTable.getGettingCount());
                        belongingInventoryDto.setItemType(spendAbleItemType);
                        myLinkweaponKey = belongingInventoryDto.ToEntity();
                        myLinkweaponKey = belongingInventoryRepository.save(myLinkweaponKey);
                        belongingInventoryList.add(myLinkweaponKey);
                        belongingInventoryLogDto.setPreviousValue(0);
                        belongingInventoryLogDto.setBelongingInventoryLogDto("상점 고대상점 구매 -> " + shopTable.getGettingItem() + " X " + shopTable.getGettingCount(), myLinkweaponKey.getId(), myLinkweaponKey.getItemId(),
                                myLinkweaponKey.getItemType(), shopTable.getGettingCount(), myLinkweaponKey.getCount());
                    } else {
                        belongingInventoryLogDto.setPreviousValue(myLinkweaponKey.getCount());
                        myLinkweaponKey.AddItem(shopTable.getGettingCount(), linkweaponKey.getStackLimit());
                        belongingInventoryLogDto.setBelongingInventoryLogDto("상점 고대상점 구매 -> " + shopTable.getGettingItem() + " X " + shopTable.getGettingCount(), myLinkweaponKey.getId(), myLinkweaponKey.getItemId(),
                                myLinkweaponKey.getItemType(), shopTable.getGettingCount(), myLinkweaponKey.getCount());
                    }
                    BelongingInventoryDto myLinkweaponKeyDto = new BelongingInventoryDto();
                    myLinkweaponKeyDto.InitFromDbData(myLinkweaponKey);
                    currencyLogDto.setWorkingPosition("상점 고대상점 구매 -> " + shopTable.getGettingItem() + " X " + shopTable.getGettingCount());
                    String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                    loggingService.setLogging(userId, 1, log);
                    String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                    loggingService.setLogging(userId, 3, belongingLog);
                    map.put("linkweapon_bronzeKey", myLinkweaponKeyDto);
                }
                else if (shopTable.getGettingItem().equals("linkweapon_silverKey")) {
                    List<ItemType> itemTypeList = itemTypeRepository.findAll();
                    ItemType spendAbleItemType = itemTypeList.stream()
                            .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                            .findAny()
                            .orElse(null);

                    List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
                    SpendableItemInfoTable linkweaponKey = spendableItemInfoTableList.stream()
                            .filter(a -> a.getCode().equals(myLinkweaponKeyItem.itemCode))
                            .findAny()
                            .orElse(null);

                    List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                    BelongingInventory myLinkweaponKey = belongingInventoryList.stream()
                            .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == linkweaponKey.getId())
                            .findAny()
                            .orElse(null);

                    if (myLinkweaponKey == null) {
                        BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                        belongingInventoryDto.setUseridUser(userId);
                        belongingInventoryDto.setItemId(linkweaponKey.getId());
                        belongingInventoryDto.setCount(shopTable.getGettingCount());
                        belongingInventoryDto.setItemType(spendAbleItemType);
                        myLinkweaponKey = belongingInventoryDto.ToEntity();
                        myLinkweaponKey = belongingInventoryRepository.save(myLinkweaponKey);
                        belongingInventoryList.add(myLinkweaponKey);
                        belongingInventoryLogDto.setPreviousValue(0);
                        belongingInventoryLogDto.setBelongingInventoryLogDto("상점 고대상점 구매 -> " + shopTable.getGettingItem() + " X " + shopTable.getGettingCount(), myLinkweaponKey.getId(), myLinkweaponKey.getItemId(),
                                myLinkweaponKey.getItemType(), shopTable.getGettingCount(), myLinkweaponKey.getCount());
                    } else {
                        belongingInventoryLogDto.setPreviousValue(myLinkweaponKey.getCount());
                        myLinkweaponKey.AddItem(shopTable.getGettingCount(), linkweaponKey.getStackLimit());
                        belongingInventoryLogDto.setBelongingInventoryLogDto("상점 고대상점 구매 -> " + shopTable.getGettingItem() + " X " + shopTable.getGettingCount(), myLinkweaponKey.getId(), myLinkweaponKey.getItemId(),
                                myLinkweaponKey.getItemType(), shopTable.getGettingCount(), myLinkweaponKey.getCount());
                    }
                    BelongingInventoryDto myLinkweaponKeyDto = new BelongingInventoryDto();
                    myLinkweaponKeyDto.InitFromDbData(myLinkweaponKey);
                    currencyLogDto.setWorkingPosition("상점 고대상점 구매 -> " + shopTable.getGettingItem() + " X " + shopTable.getGettingCount());
                    String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                    loggingService.setLogging(userId, 1, log);
                    String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                    loggingService.setLogging(userId, 3, belongingLog);
                    map.put("linkweapon_silverKey", myLinkweaponKeyDto);
                }
                else if (shopTable.getGettingItem().equals("linkweapon_goldKey")) {
                    List<ItemType> itemTypeList = itemTypeRepository.findAll();
                    ItemType spendAbleItemType = itemTypeList.stream()
                            .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                            .findAny()
                            .orElse(null);

                    List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
                    SpendableItemInfoTable linkweaponKey = spendableItemInfoTableList.stream()
                            .filter(a -> a.getCode().equals(myLinkweaponKeyItem.itemCode))
                            .findAny()
                            .orElse(null);

                    List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                    BelongingInventory myLinkweaponKey = belongingInventoryList.stream()
                            .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == linkweaponKey.getId())
                            .findAny()
                            .orElse(null);

                    if (myLinkweaponKey == null) {
                        BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                        belongingInventoryDto.setUseridUser(userId);
                        belongingInventoryDto.setItemId(linkweaponKey.getId());
                        belongingInventoryDto.setCount(shopTable.getGettingCount());
                        belongingInventoryDto.setItemType(spendAbleItemType);
                        myLinkweaponKey = belongingInventoryDto.ToEntity();
                        myLinkweaponKey = belongingInventoryRepository.save(myLinkweaponKey);
                        belongingInventoryList.add(myLinkweaponKey);
                        belongingInventoryLogDto.setPreviousValue(0);
                        belongingInventoryLogDto.setBelongingInventoryLogDto("상점 고대상점 구매 -> " + shopTable.getGettingItem() + " X " + shopTable.getGettingCount(), myLinkweaponKey.getId(), myLinkweaponKey.getItemId(),
                                myLinkweaponKey.getItemType(), shopTable.getGettingCount(), myLinkweaponKey.getCount());
                    } else {
                        belongingInventoryLogDto.setPreviousValue(myLinkweaponKey.getCount());
                        myLinkweaponKey.AddItem(shopTable.getGettingCount(), linkweaponKey.getStackLimit());
                        belongingInventoryLogDto.setBelongingInventoryLogDto("상점 고대상점 구매 -> " + shopTable.getGettingItem() + " X " + shopTable.getGettingCount(), myLinkweaponKey.getId(), myLinkweaponKey.getItemId(),
                                myLinkweaponKey.getItemType(), shopTable.getGettingCount(), myLinkweaponKey.getCount());
                    }
                    BelongingInventoryDto myLinkweaponKeyDto = new BelongingInventoryDto();
                    myLinkweaponKeyDto.InitFromDbData(myLinkweaponKey);
                    currencyLogDto.setWorkingPosition("상점 고대상점 구매 -> " + shopTable.getGettingItem() + " X " + shopTable.getGettingCount());
                    String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                    loggingService.setLogging(userId, 1, log);
                    String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                    loggingService.setLogging(userId, 3, belongingLog);
                    map.put("linkweapon_goldKey", myLinkweaponKeyDto);
                }
            }
            else {
                AncientShopTable shopTable = gameDataTableService.AncientShopTableList().stream()
                        .filter(a -> a.getId() == slotIndex + 1)
                        .findAny()
                        .orElse(null);
                if (shopTable == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find shopTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't Find shopTable.", ResponseErrorCode.NOT_FIND_DATA);
                }
                if (shopTable.getCurrency().equals("gold")) {
                    currencyLogDto.setPreviousValue(user.getGold());
                    if (!user.SpendGold(shopTable.getCost())) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail! -> Cause: Need more Gold.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Need more Gold.", ResponseErrorCode.NEED_MORE_GOLD);
                    }
                    currencyLogDto.setChangeNum(-shopTable.getCost());
                    currencyLogDto.setCurrencyType("골드");
                    currencyLogDto.setPresentValue(user.getGold());
                } else if (shopTable.getCurrency().equals("diamond")) {
                    currencyLogDto.setPreviousValue(user.getDiamond());
                    if (!user.SpendDiamond(shopTable.getCost())) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
                    }
                    currencyLogDto.setChangeNum(-shopTable.getCost());
                    currencyLogDto.setCurrencyType("다이아");
                    currencyLogDto.setPresentValue(user.getDiamond());
                } else if (shopTable.getCurrency().equals("arenaCoin")) {
                    currencyLogDto.setPreviousValue(user.getArenaCoin());
                    if (!user.SpendArenaCoin(shopTable.getCost())) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_ARENACOIN.getIntegerValue(), "Fail! -> Cause: Need more arenaCoin.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Need more arenaCoin.", ResponseErrorCode.NEED_MORE_ARENACOIN);
                    }
                    currencyLogDto.setChangeNum(-shopTable.getCost());
                    currencyLogDto.setCurrencyType("아레나코인");
                    currencyLogDto.setPresentValue(user.getArenaCoin());
                } else if (shopTable.getCurrency().equals("lowDragonScale")) {
                    currencyLogDto.setPreviousValue(user.getLowDragonScale());
                    if (!user.SpendLowDragonScale(shopTable.getCost())) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_LOWDRAGONSCALE.getIntegerValue(), "Fail! -> Cause: Need more lowDragonScale.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Need more lowDragonScale.", ResponseErrorCode.NEED_MORE_LOWDRAGONSCALE);
                    }
                    currencyLogDto.setChangeNum(-shopTable.getCost());
                    currencyLogDto.setCurrencyType("용의 비늘(전설)");
                    currencyLogDto.setPresentValue(user.getLowDragonScale());
                } else if (shopTable.getCurrency().equals("middleDragonScale")) {
                    currencyLogDto.setPreviousValue(user.getMiddleDragonScale());
                    if (!user.SpendMiddleDragonScale(shopTable.getCost())) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_MIDDLEDRAGONSCALE.getIntegerValue(), "Fail! -> Cause: Need more lowDragonScale.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Need more lowDragonScale.", ResponseErrorCode.NEED_MORE_MIDDLEDRAGONSCALE);
                    }
                    currencyLogDto.setChangeNum(-shopTable.getCost());
                    currencyLogDto.setCurrencyType("용의 비늘(신성)");
                    currencyLogDto.setPresentValue(user.getMiddleDragonScale());
                } else if (shopTable.getCurrency().equals("highDragonScale")) {
                    currencyLogDto.setPreviousValue(user.getHighDragonScale());
                    if (!user.SpendHightDragonScale(shopTable.getCost())) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_HIGHDRAGONSCALE.getIntegerValue(), "Fail! -> Cause: Need more lowDragonScale.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Need more lowDragonScale.", ResponseErrorCode.NEED_MORE_HIGHDRAGONSCALE);
                    }
                    currencyLogDto.setChangeNum(-shopTable.getCost());
                    currencyLogDto.setCurrencyType("용의 비늘(고대)");
                    currencyLogDto.setPresentValue(user.getHighDragonScale());
                }
                //인장
                MyShopItemsList.MyShopItem myStempItem = myAncientShopInfos.myStempList.stream()
                        .filter(a -> a.slotIndex == slotIndex)
                        .findAny()
                        .orElse(null);
                if (myStempItem != null) {
                    /****/
                    List<HeroEquipmentInventory> heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());
                    if (heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
                    }

                    if (myStempItem.bought) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_BOUGHT.getIntegerValue(), "Fail! -> Cause: Aleady bought", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Aleady bought", ResponseErrorCode.ALREADY_BOUGHT);
                    }
                    myStempItem.bought = true;

//                    AncientShopTable shopTable = gameDataTableService.AncientShopTableList().stream()
//                            .filter(a -> a.getId() == myStempItem.shopTableId)
//                            .findAny()
//                            .orElse(null);
//                    if(shopTable == null)
//                        throw new MyCustomException("Fail! -> Cause: Can't Find shopTable.", ResponseErrorCode.NOT_FIND_DATA);


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

                    int selectedIndex = (int) MathHelper.Range(0, copyPassiveItemTables.size());
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
                    currencyLogDto.setWorkingPosition("상점 고대상점 구매 -> " + selectedPassiveItem.getName() + " [" + generatedItem.getId() + "]");
                    String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                    loggingService.setLogging(userId, 1, log);
                    HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
                    heroEquipmentInventoryDto.InitFromDbData(generatedItem);
                    EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                    equipmentLogDto.setEquipmentLogDto("상점 고대상점 구매 -> " + selectedPassiveItem.getName() + " [" + generatedItem.getId() + "]", generatedItem.getId(), "추가", heroEquipmentInventoryDto);
                    String equipmentLog = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                    loggingService.setLogging(userId, 2, equipmentLog);
                    map.put("item", generatedItem);

                } else {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find AncientShop slotIndex.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't Find AncientShop slotIndex.", ResponseErrorCode.NOT_FIND_DATA);
                }
            }

        }

        json_myShopMainInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsLis);
        myShopInfo.ResetMyShopInfos(json_myShopMainInfo);
        map.put("user", user);
        map.put("recycle", false);
        map.put("myShopItemsLis", myShopItemsLis);

        /* 업적 : 고대 상점 아이템 1회 구매 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.BUY_ANCIENT_SHOP.name(), "empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

        /*업적 : 미션 데이터 변경점 적용*/
        if (changedMissionsData) {
            jsonMyMissionData = JsonStringHerlper.WriteValueAsStringFromData(myMissionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMyMissionData);
            map.put("exchange_myMissionsData", true);
            map.put("myMissionsDataDto", myMissionsDataDto);
            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());
        }
        /*패스 업적 : 체크 준비*/
        MyEternalPassMissionsData myEternalPassMissionsData = myEternalPassMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if (myEternalPassMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEternalPassMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyEternalPassMissionData = myEternalPassMissionsData.getJson_saveDataValue();
        EventMissionDataDto myEternalPassMissionDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyEternalPassMissionData, EventMissionDataDto.class);
        boolean changedEternalPassMissionsData = false;
        /* 패스 업적 : 고대 상점 아이템 1회 구매 미션 체크*/
        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.BUY_ANCIENT_SHOP.name(), "empty", gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;

        /* 패스 업적 : 미션 데이터 변경점 적용*/
        if (changedEternalPassMissionsData) {
            jsonMyEternalPassMissionData = JsonStringHerlper.WriteValueAsStringFromData(myEternalPassMissionDataDto);
            myEternalPassMissionsData.ResetSaveDataValue(jsonMyEternalPassMissionData);
            map.put("exchange_myEternalPassMissionsData", true);
            map.put("myEternalPassMissionsDataDto", myEternalPassMissionDataDto);
            map.put("myEternalPassLastDailyMissionClearTime", myEternalPassMissionsData.getLastDailyMissionClearTime());
            map.put("myEternalPassLastWeeklyMissionClearTime", myEternalPassMissionsData.getLastWeeklyMissionClearTime());
        }
        return map;
    }

    /*화폐 상점*/
    public Map<String, Object> currencyShopBuy(Long userId, int slotIndex, Map<String, Object> map) {

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
        }

        MyShopInfo myShopInfo = myShopInfoRepository.findByUseridUser(userId).orElse(null);
        if (myShopInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find myShopMain.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find myShopMain.", ResponseErrorCode.NOT_FIND_DATA);
        }

        /*패스 업적 : 체크 준비*/
        MyEternalPassMissionsData myEternalPassMissionsData = myEternalPassMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if (myEternalPassMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEternalPassMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyEternalPassMissionData = myEternalPassMissionsData.getJson_saveDataValue();
        EventMissionDataDto myEternalPassMissionDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyEternalPassMissionData, EventMissionDataDto.class);
        boolean changedEternalPassMissionsData = false;

        CurrencyLogDto currencyLogDto = new CurrencyLogDto();
        String json_myShopMainInfo = myShopInfo.getJson_myShopInfos();
        MyShopItemsList myShopItemsLis = JsonStringHerlper.ReadValueFromJson(json_myShopMainInfo, MyShopItemsList.class);

//        if(!myShopInfo.IsRecycleTime(86400/*총 1일에 대한 초*/)){
//            myShopItemsLis.ResetAllShop(userId, gameDataTableService.ShopTableList(), gameDataTableService.ArenaShopTableList(), gameDataTableService.GiftsTableList(), gameDataTableService.HeroEquipmentsTableList(), gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1), gameDataTableService.OptionsInfoTableList(), gameDataTableService.HerosTableList(), gameDataTableService.AncientShopTableList(), gameDataTableService.CurrencyShopTableList());
//            map.put("recycle", true);
//            map.put("myShopItemsLis", myShopItemsLis);
//            map.put("scheduleStartTime", myShopInfo.getScheduleStartTime());
//            return map;
//        }
        MyShopItemsList.MyCurrencyShopInfos myCurrencyShopInfos = myShopItemsLis.myCurrencyShopInfos;

        /** 화폐 상점은 shopTableId를 클라이언트에서 보내준다.*/
        MyShopItemsList.MyShopItem myShopItem = myCurrencyShopInfos.myCurrencyList.stream()
                .filter(a -> a.shopTableId == slotIndex)
                .findAny()
                .orElse(null);
        if (myShopItem == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.SHOP_CANT_FIND_SHOPID.getIntegerValue(), "Fail! -> Cause: Can't Find myCurrencyShopInfos.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find myCurrencyShopInfos.", ResponseErrorCode.SHOP_CANT_FIND_SHOPID);
        }

        CurrencyShopTable shopTable = gameDataTableService.CurrencyShopTableList().stream()
                .filter(a -> a.getId() == myShopItem.shopTableId)
                .findAny()
                .orElse(null);
        if (shopTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find shopTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find shopTable.", ResponseErrorCode.NOT_FIND_DATA);
        }

        //가격 체크
        if (shopTable.getCurrency().equals("gold")) {
            currencyLogDto.setPreviousValue(user.getGold());
            if (!user.SpendGold(shopTable.getCost())) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail! -> Cause: Need more Gold.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Need more Gold.", ResponseErrorCode.NEED_MORE_GOLD);
            }
            currencyLogDto.setChangeNum(-shopTable.getCost());
            currencyLogDto.setCurrencyType("골드");
            currencyLogDto.setPresentValue(user.getGold());
        } else if (shopTable.getCurrency().equals("diamond")) {
            currencyLogDto.setPreviousValue(user.getDiamond());
            if (!user.SpendDiamond(shopTable.getCost())) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
            }
            currencyLogDto.setChangeNum(-shopTable.getCost());
            currencyLogDto.setCurrencyType("다이아");
            currencyLogDto.setPresentValue(user.getDiamond());
        } else if (shopTable.getCurrency().equals("arenaCoin")) {
            currencyLogDto.setPreviousValue(user.getArenaCoin());
            if (!user.SpendArenaCoin(shopTable.getCost())) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_ARENACOIN.getIntegerValue(), "Fail! -> Cause: Need more arenaCoin.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Need more arenaCoin.", ResponseErrorCode.NEED_MORE_ARENACOIN);
            }
            currencyLogDto.setChangeNum(-shopTable.getCost());
            currencyLogDto.setCurrencyType("아레나코인");
            currencyLogDto.setPresentValue(user.getArenaCoin());
        } else if (shopTable.getCurrency().equals("lowDragonScale")) {
            currencyLogDto.setPreviousValue(user.getLowDragonScale());
            if (!user.SpendLowDragonScale(shopTable.getCost())) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_LOWDRAGONSCALE.getIntegerValue(), "Fail! -> Cause: Need more lowDragonScale.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Need more lowDragonScale.", ResponseErrorCode.NEED_MORE_LOWDRAGONSCALE);
            }
            currencyLogDto.setChangeNum(-shopTable.getCost());
            currencyLogDto.setCurrencyType("용의 비늘(전설)");
            currencyLogDto.setPresentValue(user.getLowDragonScale());
        } else if (shopTable.getCurrency().equals("middleDragonScale")) {
            currencyLogDto.setPreviousValue(user.getMiddleDragonScale());
            if (!user.SpendMiddleDragonScale(shopTable.getCost())) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_MIDDLEDRAGONSCALE.getIntegerValue(), "Fail! -> Cause: Need more lowDragonScale.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Need more lowDragonScale.", ResponseErrorCode.NEED_MORE_MIDDLEDRAGONSCALE);
            }
            currencyLogDto.setChangeNum(-shopTable.getCost());
            currencyLogDto.setCurrencyType("용의 비늘(신성)");
            currencyLogDto.setPresentValue(user.getMiddleDragonScale());
        } else if (shopTable.getCurrency().equals("highDragonScale")) {
            currencyLogDto.setPreviousValue(user.getHighDragonScale());
            if (!user.SpendHightDragonScale(shopTable.getCost())) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_HIGHDRAGONSCALE.getIntegerValue(), "Fail! -> Cause: Need more lowDragonScale.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Need more lowDragonScale.", ResponseErrorCode.NEED_MORE_HIGHDRAGONSCALE);
            }
            currencyLogDto.setChangeNum(-shopTable.getCost());
            currencyLogDto.setCurrencyType("용의 비늘(고대)");
            currencyLogDto.setPresentValue(user.getHighDragonScale());
        }
        //보너스 적용된 값 적용
        int original = shopTable.getGettingCount();
        int bonus = shopTable.getBonus();
        int totalGettingCount = (original + bonus);

        if (shopTable.getGettingItem().equals("gold")) {
            currencyLogDto.setWorkingPosition("화폐 상점 구매 => 골드");
            CurrencyLogDto gettingLog = new CurrencyLogDto();
            int previousValue = user.getGold();
            user.AddGold(totalGettingCount);
            /* 패스 업적 : 골드획득*/
            changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.ERNING_GOLD.name(), "empty", totalGettingCount, gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;

            gettingLog.setCurrencyLogDto("화폐 상점 구매 => 골드", "골드", previousValue, totalGettingCount, user.getGold());
            String log = JsonStringHerlper.WriteValueAsStringFromData(gettingLog);
            loggingService.setLogging(userId, 1, log);

        } else if (shopTable.getGettingItem().equals("diamond")) {
            currencyLogDto.setWorkingPosition("화폐 상점 구매 => 다이아");
            CurrencyLogDto gettingLog = new CurrencyLogDto();
            int previousValue = user.getDiamond();
            user.AddDiamond(totalGettingCount);
            gettingLog.setCurrencyLogDto("화폐 상점 구매 => 다이아", "다이아", previousValue, totalGettingCount, user.getDiamond());
            String log = JsonStringHerlper.WriteValueAsStringFromData(gettingLog);
            loggingService.setLogging(userId, 1, log);
        }


        json_myShopMainInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsLis);
        myShopInfo.ResetMyShopInfos(json_myShopMainInfo);


        String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
        loggingService.setLogging(userId, 1, log);

        map.put("user", user);
        map.put("recycle", false);
        map.put("myShopItemsLis", myShopItemsLis);

        /* 패스 업적 : 미션 데이터 변경점 적용*/
        if (changedEternalPassMissionsData) {
            jsonMyEternalPassMissionData = JsonStringHerlper.WriteValueAsStringFromData(myEternalPassMissionDataDto);
            myEternalPassMissionsData.ResetSaveDataValue(jsonMyEternalPassMissionData);
            map.put("exchange_myEternalPassMissionsData", true);
            map.put("myEternalPassMissionsDataDto", myEternalPassMissionDataDto);
            map.put("myEternalPassLastDailyMissionClearTime", myEternalPassMissionsData.getLastDailyMissionClearTime());
            map.put("myEternalPassLastWeeklyMissionClearTime", myEternalPassMissionsData.getLastWeeklyMissionClearTime());
        }

        return map;
    }

    /*조각 교환소*/
    public Map<String, Object> pieceShopBuy(Long userId, int slotIndex, Map<String, Object> map) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
        }

        MyShopInfo myShopInfo = myShopInfoRepository.findByUseridUser(userId).orElse(null);
        if (myShopInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find myShopMain.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find myShopMain.", ResponseErrorCode.NOT_FIND_DATA);
        }
        CurrencyLogDto currencyLogDto = new CurrencyLogDto();
        String json_myShopMainInfo = myShopInfo.getJson_myShopInfos();
        MyShopItemsList myShopItemsLis = JsonStringHerlper.ReadValueFromJson(json_myShopMainInfo, MyShopItemsList.class);

        if (myShopInfo.IsMyShopRecycleTime(86400/*총 1일에 대한 초*/)) {
            myShopItemsLis.ResetAllShop(userId, gameDataTableService.ShopTableList(), gameDataTableService.ArenaShopTableList(), gameDataTableService.GiftsTableList(), gameDataTableService.HeroEquipmentsTableList(), gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1), gameDataTableService.OptionsInfoTableList(), gameDataTableService.HerosTableList(), gameDataTableService.AncientShopTableList(), gameDataTableService.CurrencyShopTableList(), gameDataTableService.SpendableItemInfoTableList(), gameDataTableService.PieceShopTableList(), gameDataTableService.DarkObeShopTableList(), gameDataTableService.PassiveItemTableList());
            json_myShopMainInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsLis);
            myShopInfo.ResetMyShopInfos(json_myShopMainInfo);
            map.put("recycle", true);
            map.put("myShopItemsLis", myShopItemsLis);
            return map;
        }
        MyShopItemsList.MyPieceShopInfos myPieceShopInfos = myShopItemsLis.myPieceShopInfos;

        //케릭터 조각 체크
        MyShopItemsList.MyCharacterPiece myCharacterPiece = myPieceShopInfos.myCharacterPieceList.stream()
                .filter(a -> a.slotIndex == slotIndex)
                .findAny()
                .orElse(null);
        if (myCharacterPiece != null) {
            if (myCharacterPiece.bought) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_BOUGHT.getIntegerValue(), "Fail! -> Cause: Aleady bought", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Aleady bought", ResponseErrorCode.ALREADY_BOUGHT);
            }
            myCharacterPiece.bought = true;
            BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
            String belongingLog = "";

            PieceShopTable shopTable = gameDataTableService.PieceShopTableList().stream()
                    .filter(a -> a.getId() == myCharacterPiece.shopTableId)
                    .findAny()
                    .orElse(null);
            if (shopTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find shopTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Can't Find shopTable.", ResponseErrorCode.NOT_FIND_DATA);
            }
            //가격 체크
            if (shopTable.getCurrency().equals("gold")) {
                currencyLogDto.setPreviousValue(user.getGold());
                if (!user.SpendGold(shopTable.getCost())) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail! -> Cause: Need more Gold.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more Gold.", ResponseErrorCode.NEED_MORE_GOLD);
                }
                currencyLogDto.setChangeNum(-shopTable.getCost());
                currencyLogDto.setCurrencyType("골드");
                currencyLogDto.setPresentValue(user.getGold());
            } else if (shopTable.getCurrency().equals("diamond")) {
                currencyLogDto.setPreviousValue(user.getDiamond());
                if (!user.SpendDiamond(shopTable.getCost())) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
                }
                currencyLogDto.setChangeNum(-shopTable.getCost());
                currencyLogDto.setCurrencyType("다이아");
                currencyLogDto.setPresentValue(user.getDiamond());
            } else if (shopTable.getCurrency().equals("arenaCoin")) {
                currencyLogDto.setPreviousValue(user.getArenaCoin());
                if (!user.SpendArenaCoin(shopTable.getCost())) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_ARENACOIN.getIntegerValue(), "Fail! -> Cause: Need more arenaCoin.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more arenaCoin.", ResponseErrorCode.NEED_MORE_ARENACOIN);
                }
                currencyLogDto.setChangeNum(-shopTable.getCost());
                currencyLogDto.setCurrencyType("아레나코인");
                currencyLogDto.setPresentValue(user.getArenaCoin());
            } else if (shopTable.getCurrency().equals("characterPiece_cr_common")) {
                List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                BelongingInventory belongingInventory = belongingInventoryList.stream().filter(i -> i.getItemType().getId()==4&&i.getItemId()==26).findAny().orElse(null);
                if(belongingInventory == null){
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: BelongingInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> BelongingInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
                }
                belongingInventoryLogDto.setPreviousValue(belongingInventory.getCount());
                if(!belongingInventory.SpendItem(shopTable.getCost())) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_CHARACTERPIECE.getIntegerValue(), "Fail! -> Cause: Need more CharacterPiece.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more CharacterPiece.", ResponseErrorCode.NEED_MORE_CHARACTERPIECE);
                }
                belongingInventoryLogDto.setBelongingInventoryLogDto("상점 조각상점 구매 -> " + myCharacterPiece.itemName + " X " + shopTable.getGettingCount(), belongingInventory.getId(),
                        belongingInventory.getItemId(), belongingInventory.getItemType(), -shopTable.getCost(), belongingInventory.getCount());
                belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                loggingService.setLogging(userId, 3, belongingLog);
            }

            List<ItemType> itemTypeList = itemTypeRepository.findAll();
            ItemType spendAbleItemType = itemTypeList.stream()
                    .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece)
                    .findAny()
                    .orElse(null);

            List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
            BelongingCharacterPieceTable characterPiece = belongingCharacterPieceTableList.stream()
                    .filter(a -> a.getCode().contains(myCharacterPiece.characterPieceCode))
                    .findAny()
                    .orElse(null);
            if (characterPiece == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find characterPiece.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Can't Find characterPiece.", ResponseErrorCode.NOT_FIND_DATA);
            }

            List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
            BelongingInventory myCharacterPieceItem = belongingInventoryList.stream()
                    .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece && a.getItemId() == characterPiece.getId())
                    .findAny()
                    .orElse(null);

            if (myCharacterPieceItem == null) {
                BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                belongingInventoryDto.setUseridUser(userId);
                belongingInventoryDto.setItemId(characterPiece.getId());
                belongingInventoryDto.setCount(shopTable.getGettingCount());
                belongingInventoryDto.setItemType(spendAbleItemType);
                myCharacterPieceItem = belongingInventoryDto.ToEntity();
                myCharacterPieceItem = belongingInventoryRepository.save(myCharacterPieceItem);
                belongingInventoryList.add(myCharacterPieceItem);
                belongingInventoryLogDto.setPreviousValue(0);
            } else {
                belongingInventoryLogDto.setPreviousValue(myCharacterPieceItem.getCount());
                myCharacterPieceItem.AddItem(shopTable.getGettingCount(), characterPiece.getStackLimit());
            }
            belongingInventoryLogDto.setBelongingInventoryLogDto("상점 조각상점 구매 -> " + myCharacterPiece.itemName + " X " + shopTable.getGettingCount(), myCharacterPieceItem.getId(),
                    myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), shopTable.getGettingCount(), myCharacterPieceItem.getCount());
            currencyLogDto.setWorkingPosition("상점 조각상점 구매 -> " + myCharacterPiece.itemName + " X " + shopTable.getGettingCount());
            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
            loggingService.setLogging(userId, 1, log);
            belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, belongingLog);

            List<BelongingInventoryDto> belongingInventoryDtoList = new ArrayList<>();
            for(BelongingInventory temp : belongingInventoryList){
                BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                belongingInventoryDto.InitFromDbData(temp);
                belongingInventoryDtoList.add(belongingInventoryDto);
            }

            map.put("myCharacterPieceItem", myCharacterPieceItem);
            map.put("belongingInventory", belongingInventoryDtoList);
        }
        else {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find PieceShop slotIndex.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find PieceShop slotIndex.", ResponseErrorCode.NOT_FIND_DATA);
        }

        json_myShopMainInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsLis);
        myShopInfo.ResetMyShopInfos(json_myShopMainInfo);
        map.put("user", user);
        map.put("recycle", false);
        map.put("myShopItemsLis", myShopItemsLis);

        return map;
    }

    /*어둠의 보주 상점*/
    public Map<String, Object> darkObeShopBuy(Long userId, int slotIndex, Map<String, Object> map) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
        }

        MyShopInfo myShopInfo = myShopInfoRepository.findByUseridUser(userId).orElse(null);
        if (myShopInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find myShopMain.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find myShopMain.", ResponseErrorCode.NOT_FIND_DATA);
        }
        CurrencyLogDto currencyLogDto = new CurrencyLogDto();
        String json_myShopMainInfo = myShopInfo.getJson_myShopInfos();
        MyShopItemsList myShopItemsLis = JsonStringHerlper.ReadValueFromJson(json_myShopMainInfo, MyShopItemsList.class);

        if (myShopInfo.IsMyShopRecycleTime(86400/*총 1일에 대한 초*/)) {
            myShopItemsLis.ResetAllShop(userId, gameDataTableService.ShopTableList(), gameDataTableService.ArenaShopTableList(), gameDataTableService.GiftsTableList(), gameDataTableService.HeroEquipmentsTableList(), gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1), gameDataTableService.OptionsInfoTableList(), gameDataTableService.HerosTableList(), gameDataTableService.AncientShopTableList(), gameDataTableService.CurrencyShopTableList(), gameDataTableService.SpendableItemInfoTableList(), gameDataTableService.PieceShopTableList(), gameDataTableService.DarkObeShopTableList(), gameDataTableService.PassiveItemTableList());
            json_myShopMainInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsLis);
            myShopInfo.ResetMyShopInfos(json_myShopMainInfo);
            map.put("recycle", true);
            map.put("myShopItemsLis", myShopItemsLis);
            return map;
        }

        /*업적 : 체크 준비*/
        MyMissionsData myMissionsData = myMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if (myMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyMissionData = myMissionsData.getJson_saveDataValue();
        MissionsDataDto myMissionsDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyMissionData, MissionsDataDto.class);
        boolean changedMissionsData = false;

        MyShopItemsList.MyDarkObeShopInfos myDarkObeShopInfos = myShopItemsLis.myDarkObeShopInfos;


        //강화석

        BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
        MyShopItemsList.MyEnchantItem myEnchantItem = myDarkObeShopInfos.myEnchantItemList.stream()
                .filter(a -> a.slotIndex == slotIndex)
                .findAny()
                .orElse(null);
        if (myEnchantItem != null){
            if (myEnchantItem.bought) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_BOUGHT.getIntegerValue(), "Fail! -> Cause: Already bought", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Already bought", ResponseErrorCode.ALREADY_BOUGHT);
            }
            myEnchantItem.bought = true;

            DarkObeShopTable shopTable = gameDataTableService.DarkObeShopTableList().stream()
                    .filter(a -> a.getId() == myEnchantItem.shopTableId)
                    .findAny()
                    .orElse(null);
            if (shopTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find shopTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Can't Find shopTable.", ResponseErrorCode.NOT_FIND_DATA);
            }
            //가격 체크
            if (shopTable.getCurrency().equals("gold")) {
                currencyLogDto.setPreviousValue(user.getGold());
                if (!user.SpendGold(shopTable.getCost())) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail! -> Cause: Need more Gold.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more Gold.", ResponseErrorCode.NEED_MORE_GOLD);
                }
                currencyLogDto.setChangeNum(-shopTable.getCost());
                currencyLogDto.setCurrencyType("골드");
                currencyLogDto.setPresentValue(user.getGold());
            } else if (shopTable.getCurrency().equals("diamond")) {
                currencyLogDto.setPreviousValue(user.getDiamond());
                if (!user.SpendDiamond(shopTable.getCost())) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
                }
                currencyLogDto.setChangeNum(-shopTable.getCost());
                currencyLogDto.setCurrencyType("다이아");
                currencyLogDto.setPresentValue(user.getDiamond());
            } else if (shopTable.getCurrency().equals("arenaCoin")) {
                currencyLogDto.setPreviousValue(user.getArenaCoin());
                if (!user.SpendArenaCoin(shopTable.getCost())) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_ARENACOIN.getIntegerValue(), "Fail! -> Cause: Need more arenaCoin.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more arenaCoin.", ResponseErrorCode.NEED_MORE_ARENACOIN);
                }
                currencyLogDto.setChangeNum(-shopTable.getCost());
                currencyLogDto.setCurrencyType("아레나코인");
                currencyLogDto.setPresentValue(user.getArenaCoin());
            } else if (shopTable.getCurrency().equals("darkobe")) {
                currencyLogDto.setPreviousValue(user.getDarkObe());
                if(!user.SpendDarkObe(shopTable.getCost())) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DARKOBE.getIntegerValue(), "Fail! -> Cause: Need more DarkObe.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more DarkObe.", ResponseErrorCode.NEED_MORE_DARKOBE);
                }
                currencyLogDto.setChangeNum(-shopTable.getCost());
                currencyLogDto.setCurrencyType("어둠의 보주");
                currencyLogDto.setPresentValue(user.getDarkObe());
            }

            List<ItemType> itemTypeList = itemTypeRepository.findAll();
            ItemType spendAbleItemType = itemTypeList.stream()
                    .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                    .findAny()
                    .orElse(null);

            List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
            SpendableItemInfoTable enchantStone = spendableItemInfoTableList.stream()
                    .filter(a -> a.getCode().equals(myEnchantItem.itemCode))
                    .findAny()
                    .orElse(null);

            List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
            BelongingInventory myEnchantStone = belongingInventoryList.stream()
                    .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == enchantStone.getId())
                    .findAny()
                    .orElse(null);

            if (myEnchantStone == null) {
                BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                belongingInventoryDto.setUseridUser(userId);
                belongingInventoryDto.setItemId(enchantStone.getId());
                belongingInventoryDto.setCount(shopTable.getGettingCount());
                belongingInventoryDto.setItemType(spendAbleItemType);
                myEnchantStone = belongingInventoryDto.ToEntity();
                myEnchantStone = belongingInventoryRepository.save(myEnchantStone);
                belongingInventoryList.add(myEnchantStone);
                belongingInventoryLogDto.setPreviousValue(0);
                belongingInventoryLogDto.setBelongingInventoryLogDto("상점 어둠의 보주 상점 구매 -> " + shopTable.getGettingItem() + " X " + shopTable.getGettingCount(), myEnchantStone.getId(), myEnchantStone.getItemId(),
                        myEnchantStone.getItemType(), shopTable.getGettingCount(), myEnchantStone.getCount());
            } else {
                belongingInventoryLogDto.setPreviousValue(myEnchantStone.getCount());
                myEnchantStone.AddItem(shopTable.getGettingCount(), enchantStone.getStackLimit());
                belongingInventoryLogDto.setBelongingInventoryLogDto("상점 어둠의 보주 상점 구매 -> " + shopTable.getGettingItem() + " X " + shopTable.getGettingCount(), myEnchantStone.getId(), myEnchantStone.getItemId(),
                        myEnchantStone.getItemType(), shopTable.getGettingCount(), myEnchantStone.getCount());
            }
            BelongingInventoryDto myEnchantStoneDto = new BelongingInventoryDto();
            myEnchantStoneDto.InitFromDbData(myEnchantStone);
            currencyLogDto.setWorkingPosition("상점 어둠의 보주 상점 구매 -> " + shopTable.getGettingItem() + " X " + shopTable.getGettingCount());
            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
            loggingService.setLogging(userId, 1, log);
            String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, belongingLog);
            map.put("enchantStone", myEnchantStoneDto);
        }
        else {
            MyShopItemsList.MyResmeltItem myResmeltItem = myDarkObeShopInfos.myResmeltItemList.stream()
                    .filter(a -> a.slotIndex == slotIndex)
                    .findAny()
                    .orElse(null);
            if (myResmeltItem != null) {
                if (myResmeltItem.bought) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_BOUGHT.getIntegerValue(), "Fail! -> Cause: Already bought", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Already bought", ResponseErrorCode.ALREADY_BOUGHT);
                }
                myResmeltItem.bought = true;

                DarkObeShopTable shopTable = gameDataTableService.DarkObeShopTableList().stream()
                        .filter(a -> a.getId() == myResmeltItem.shopTableId)
                        .findAny()
                        .orElse(null);
                if (shopTable == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find shopTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't Find shopTable.", ResponseErrorCode.NOT_FIND_DATA);
                }
                //가격 체크
                if (shopTable.getCurrency().equals("gold")) {
                    currencyLogDto.setPreviousValue(user.getGold());
                    if (!user.SpendGold(shopTable.getCost())) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail! -> Cause: Need more Gold.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Need more Gold.", ResponseErrorCode.NEED_MORE_GOLD);
                    }
                    currencyLogDto.setChangeNum(-shopTable.getCost());
                    currencyLogDto.setCurrencyType("골드");
                    currencyLogDto.setPresentValue(user.getGold());
                } else if (shopTable.getCurrency().equals("diamond")) {
                    currencyLogDto.setPreviousValue(user.getDiamond());
                    if (!user.SpendDiamond(shopTable.getCost())) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
                    }
                    currencyLogDto.setChangeNum(-shopTable.getCost());
                    currencyLogDto.setCurrencyType("다이아");
                    currencyLogDto.setPresentValue(user.getDiamond());
                } else if (shopTable.getCurrency().equals("arenaCoin")) {
                    currencyLogDto.setPreviousValue(user.getArenaCoin());
                    if (!user.SpendArenaCoin(shopTable.getCost())) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_ARENACOIN.getIntegerValue(), "Fail! -> Cause: Need more arenaCoin.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Need more arenaCoin.", ResponseErrorCode.NEED_MORE_ARENACOIN);
                    }
                    currencyLogDto.setChangeNum(-shopTable.getCost());
                    currencyLogDto.setCurrencyType("아레나코인");
                    currencyLogDto.setPresentValue(user.getArenaCoin());
                } else if (shopTable.getCurrency().equals("darkobe")) {
                    currencyLogDto.setPreviousValue(user.getDarkObe());
                    if(!user.SpendDarkObe(shopTable.getCost())) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DARKOBE.getIntegerValue(), "Fail! -> Cause: Need more DarkObe.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Need more DarkObe.", ResponseErrorCode.NEED_MORE_DARKOBE);
                    }
                    currencyLogDto.setChangeNum(-shopTable.getCost());
                    currencyLogDto.setCurrencyType("어둠의 보주");
                    currencyLogDto.setPresentValue(user.getDarkObe());
                }

                List<ItemType> itemTypeList = itemTypeRepository.findAll();
                ItemType spendAbleItemType = itemTypeList.stream()
                        .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                        .findAny()
                        .orElse(null);

                List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
                SpendableItemInfoTable resmeltStone = spendableItemInfoTableList.stream()
                        .filter(a -> a.getCode().equals(myResmeltItem.itemCode))
                        .findAny()
                        .orElse(null);

                List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                BelongingInventory myResmeltStone = belongingInventoryList.stream()
                        .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == resmeltStone.getId())
                        .findAny()
                        .orElse(null);

                if (myResmeltStone == null) {
                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.setUseridUser(userId);
                    belongingInventoryDto.setItemId(resmeltStone.getId());
                    belongingInventoryDto.setCount(shopTable.getGettingCount());
                    belongingInventoryDto.setItemType(spendAbleItemType);
                    myResmeltStone = belongingInventoryDto.ToEntity();
                    myResmeltStone = belongingInventoryRepository.save(myResmeltStone);
                    belongingInventoryList.add(myResmeltStone);
                    belongingInventoryLogDto.setPreviousValue(0);
                    belongingInventoryLogDto.setBelongingInventoryLogDto("상점 어둠의 보주 상점 구매 -> " + shopTable.getGettingItem() + " X " + shopTable.getGettingCount(), myResmeltStone.getId(), myResmeltStone.getItemId(),
                            myResmeltStone.getItemType(), shopTable.getGettingCount(), myResmeltStone.getCount());
                } else {
                    belongingInventoryLogDto.setPreviousValue(myResmeltStone.getCount());
                    myResmeltStone.AddItem(shopTable.getGettingCount(), resmeltStone.getStackLimit());
                    belongingInventoryLogDto.setBelongingInventoryLogDto("상점 어둠의 보주 상점 구매 -> " + shopTable.getGettingItem() + " X " + shopTable.getGettingCount(), myResmeltStone.getId(), myResmeltStone.getItemId(),
                            myResmeltStone.getItemType(), shopTable.getGettingCount(), myResmeltStone.getCount());
                }
                BelongingInventoryDto myResmeltStoneDto = new BelongingInventoryDto();
                myResmeltStoneDto.InitFromDbData(myResmeltStone);
                currencyLogDto.setWorkingPosition("상점 어둠의 보주 상점 구매 -> " + shopTable.getGettingItem() + " X " + shopTable.getGettingCount());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                loggingService.setLogging(userId, 3, belongingLog);
                map.put("resmeltStone", myResmeltStoneDto);
            }
            else {
                MyShopItemsList.MyCharacterPiece myCharacterPiece = myDarkObeShopInfos.myCharacterPieceList.stream()
                        .filter(a -> a.slotIndex == slotIndex)
                        .findAny()
                        .orElse(null);
                if(myCharacterPiece != null) {
                    if (myCharacterPiece.bought) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_BOUGHT.getIntegerValue(), "Fail! -> Cause: Aleady bought", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Aleady bought", ResponseErrorCode.ALREADY_BOUGHT);
                    }
                    myCharacterPiece.bought = true;
                    String belongingLog = "";

                    DarkObeShopTable shopTable = gameDataTableService.DarkObeShopTableList().stream()
                            .filter(a -> a.getId() == myCharacterPiece.shopTableId)
                            .findAny()
                            .orElse(null);
                    if (shopTable == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find shopTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find shopTable.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    //가격 체크
                    if (shopTable.getCurrency().equals("gold")) {
                        currencyLogDto.setPreviousValue(user.getGold());
                        if (!user.SpendGold(shopTable.getCost())) {
                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail! -> Cause: Need more Gold.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                            throw new MyCustomException("Fail! -> Cause: Need more Gold.", ResponseErrorCode.NEED_MORE_GOLD);
                        }
                        currencyLogDto.setChangeNum(-shopTable.getCost());
                        currencyLogDto.setCurrencyType("골드");
                        currencyLogDto.setPresentValue(user.getGold());
                    } else if (shopTable.getCurrency().equals("diamond")) {
                        currencyLogDto.setPreviousValue(user.getDiamond());
                        if (!user.SpendDiamond(shopTable.getCost())) {
                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                            throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
                        }
                        currencyLogDto.setChangeNum(-shopTable.getCost());
                        currencyLogDto.setCurrencyType("다이아");
                        currencyLogDto.setPresentValue(user.getDiamond());
                    } else if (shopTable.getCurrency().equals("arenaCoin")) {
                        currencyLogDto.setPreviousValue(user.getArenaCoin());
                        if (!user.SpendArenaCoin(shopTable.getCost())) {
                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_ARENACOIN.getIntegerValue(), "Fail! -> Cause: Need more arenaCoin.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                            throw new MyCustomException("Fail! -> Cause: Need more arenaCoin.", ResponseErrorCode.NEED_MORE_ARENACOIN);
                        }
                        currencyLogDto.setChangeNum(-shopTable.getCost());
                        currencyLogDto.setCurrencyType("아레나코인");
                        currencyLogDto.setPresentValue(user.getArenaCoin());
                    } else if (shopTable.getCurrency().equals("characterPiece_cr_common")) {
                        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                        BelongingInventory belongingInventory = belongingInventoryList.stream().filter(i -> i.getItemType().getId()==4&&i.getItemId()==26).findAny().orElse(null);
                        if(belongingInventory == null){
                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: BelongingInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                            throw new MyCustomException("Fail! -> BelongingInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
                        }
                        belongingInventoryLogDto.setPreviousValue(belongingInventory.getCount());
                        if(!belongingInventory.SpendItem(shopTable.getCost())) {
                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_CHARACTERPIECE.getIntegerValue(), "Fail! -> Cause: Need more CharacterPiece.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                            throw new MyCustomException("Fail! -> Cause: Need more CharacterPiece.", ResponseErrorCode.NEED_MORE_CHARACTERPIECE);
                        }
                        belongingInventoryLogDto.setBelongingInventoryLogDto("상점 어둠의 보주 상점 구매 -> " + myCharacterPiece.itemName + " X " + shopTable.getGettingCount(), belongingInventory.getId(),
                                belongingInventory.getItemId(), belongingInventory.getItemType(), -shopTable.getCost(), belongingInventory.getCount());
                        belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                        loggingService.setLogging(userId, 3, belongingLog);
                    }else if (shopTable.getCurrency().equals("darkobe")) {
                        currencyLogDto.setPreviousValue(user.getDarkObe());
                        if(!user.SpendDarkObe(shopTable.getCost())) {
                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DARKOBE.getIntegerValue(), "Fail! -> Cause: Need more DarkObe.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                            throw new MyCustomException("Fail! -> Cause: Need more DarkObe.", ResponseErrorCode.NEED_MORE_DARKOBE);
                        }
                        currencyLogDto.setChangeNum(-shopTable.getCost());
                        currencyLogDto.setCurrencyType("어둠의 보주");
                        currencyLogDto.setPresentValue(user.getDarkObe());
                    }

                    List<ItemType> itemTypeList = itemTypeRepository.findAll();
                    ItemType spendAbleItemType = itemTypeList.stream()
                            .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece)
                            .findAny()
                            .orElse(null);

                    List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
                    BelongingCharacterPieceTable characterPiece = belongingCharacterPieceTableList.stream()
                            .filter(a -> a.getCode().contains(myCharacterPiece.characterPieceCode))
                            .findAny()
                            .orElse(null);
                    if (characterPiece == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find characterPiece.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find characterPiece.", ResponseErrorCode.NOT_FIND_DATA);
                    }

                    List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                    BelongingInventory myCharacterPieceItem = belongingInventoryList.stream()
                            .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece && a.getItemId() == characterPiece.getId())
                            .findAny()
                            .orElse(null);

                    if (myCharacterPieceItem == null) {
                        BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                        belongingInventoryDto.setUseridUser(userId);
                        belongingInventoryDto.setItemId(characterPiece.getId());
                        belongingInventoryDto.setCount(shopTable.getGettingCount());
                        belongingInventoryDto.setItemType(spendAbleItemType);
                        myCharacterPieceItem = belongingInventoryDto.ToEntity();
                        myCharacterPieceItem = belongingInventoryRepository.save(myCharacterPieceItem);
                        belongingInventoryList.add(myCharacterPieceItem);
                        belongingInventoryLogDto.setPreviousValue(0);
                    } else {
                        belongingInventoryLogDto.setPreviousValue(myCharacterPieceItem.getCount());
                        myCharacterPieceItem.AddItem(shopTable.getGettingCount(), characterPiece.getStackLimit());
                    }

                    belongingInventoryLogDto.setBelongingInventoryLogDto("상점 균열상점 구매 -> " + myCharacterPiece.itemName + " X " + shopTable.getGettingCount(), myCharacterPieceItem.getId(),
                            myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), shopTable.getGettingCount(), myCharacterPieceItem.getCount());
                    currencyLogDto.setWorkingPosition("상점 균열상점 구매 -> " + myCharacterPiece.itemName + " X " + shopTable.getGettingCount());
                    String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                    loggingService.setLogging(userId, 1, log);
                    belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                    loggingService.setLogging(userId, 3, belongingLog);

                    map.put("myCharacterPieceItem", myCharacterPieceItem);

                }
                else {
                    MyShopItemsList.MyEquipmentShopItem myEquipmentShopItem = myDarkObeShopInfos.myEquipmentShopItemList.stream()
                            .filter(a -> a.slotIndex == slotIndex)
                            .findAny()
                            .orElse(null);
                    if(myEquipmentShopItem != null) {
                        List<HeroEquipmentInventory> heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());
                        if (heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                            throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
                        }

                        List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();


                        if (myEquipmentShopItem == null) {
                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.SHOP_CANT_FIND_SHOPID.getIntegerValue(), "Fail! -> Cause: Can't Find myEquipmentShopItem.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                            throw new MyCustomException("Fail! -> Cause: Can't Find myEquipmentShopItem.", ResponseErrorCode.SHOP_CANT_FIND_SHOPID);
                        }
                        if (myEquipmentShopItem.bought) {
                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_BOUGHT.getIntegerValue(), "Fail! -> Cause: Aleady bought", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                            throw new MyCustomException("Fail! -> Cause: Aleady bought", ResponseErrorCode.ALREADY_BOUGHT);
                        }
                        myEquipmentShopItem.bought = true;

                        DarkObeShopTable shopTable = gameDataTableService.DarkObeShopTableList().stream()
                                .filter(a -> a.getId() == myEquipmentShopItem.shopTableId)
                                .findAny()
                                .orElse(null);
                        if (shopTable == null) {
                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find shopTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                            throw new MyCustomException("Fail! -> Cause: Can't Find shopTable.", ResponseErrorCode.NOT_FIND_DATA);
                        }
                        //가격 체크
                        if (shopTable.getCurrency().equals("gold")) {
                            currencyLogDto.setPreviousValue(user.getGold());
                            if (!user.SpendGold(shopTable.getCost())) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail! -> Cause: Need more Gold.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Fail! -> Cause: Need more Gold.", ResponseErrorCode.NEED_MORE_GOLD);
                            }
                            currencyLogDto.setChangeNum(-shopTable.getCost());
                            currencyLogDto.setPresentValue(user.getGold());
                            currencyLogDto.setCurrencyType("골드");
                        } else if (shopTable.getCurrency().equals("diamond")) {
                            currencyLogDto.setPreviousValue(user.getDiamond());
                            if (!user.SpendDiamond(shopTable.getCost())) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
                            }
                            currencyLogDto.setChangeNum(-shopTable.getCost());
                            currencyLogDto.setPresentValue(user.getDiamond());
                            currencyLogDto.setCurrencyType("다이아");
                        }else if (shopTable.getCurrency().equals("darkobe")) {
                            currencyLogDto.setPreviousValue(user.getDarkObe());
                            if(!user.SpendDarkObe(shopTable.getCost())) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DARKOBE.getIntegerValue(), "Fail! -> Cause: Need more DarkObe.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Fail! -> Cause: Need more DarkObe.", ResponseErrorCode.NEED_MORE_DARKOBE);
                            }
                            currencyLogDto.setChangeNum(-shopTable.getCost());
                            currencyLogDto.setCurrencyType("어둠의 보주");
                            currencyLogDto.setPresentValue(user.getDarkObe());
                        }

                        int gradeValue = 0;
                        int maxLevel = 0;
                        int nextExp = 0;
                        String grade = "";
                        if(shopTable.getGettingItem().equals("passiveItem_00_10")){
                            maxLevel = 1;
                            nextExp = 0;
                        }
                        else{
                            HeroEquipmentsTable heroEquipmentsTable = heroEquipmentsTableList.stream()
                                    .filter(a -> a.getId() == myEquipmentShopItem.item_id)
                                    .findAny()
                                    .orElse(null);
                            grade = heroEquipmentsTable.getGrade();
                            gradeValue = EquipmentCalculate.GradeValue(grade);
                            nextExp = EquipmentCalculate.CalculateNeedExp(1, gradeValue);
                        }

                        HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
                        heroEquipmentInventoryDto.setUseridUser(userId);
                        heroEquipmentInventoryDto.setItem_Id(myEquipmentShopItem.item_id);
                        heroEquipmentInventoryDto.setItemClassValue(myEquipmentShopItem.itemClassValue);
                        heroEquipmentInventoryDto.setDecideDefaultAbilityValue(myEquipmentShopItem.decideDefaultAbilityValue);
                        heroEquipmentInventoryDto.setDecideSecondAbilityValue(myEquipmentShopItem.decideSecondAbilityValue);
                        heroEquipmentInventoryDto.setLevel(1);

                        heroEquipmentInventoryDto.setMaxLevel(maxLevel);
                        heroEquipmentInventoryDto.setExp(0);

                        heroEquipmentInventoryDto.setNextExp(nextExp);
                        heroEquipmentInventoryDto.setItemClass(myEquipmentShopItem.itemClass);
                        heroEquipmentInventoryDto.setItemClassValue(myEquipmentShopItem.itemClassValue);
                        heroEquipmentInventoryDto.setOptionIds(myEquipmentShopItem.optionIds);
                        heroEquipmentInventoryDto.setOptionValues(myEquipmentShopItem.optionValues);
                        HeroEquipmentInventory generatedItem = heroEquipmentInventoryDto.ToEntity();
                        generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
                        heroEquipmentInventoryDto.setId(generatedItem.getId());
                        currencyLogDto.setWorkingPosition("상점 어둠의 보주 상점 구매 -> " + myEquipmentShopItem.itemName + " " + generatedItem.getId());
                        String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                        loggingService.setLogging(userId, 1, log);
                        EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                        equipmentLogDto.setEquipmentLogDto("상점 어둠의 보주 상점 구매 -> " + myEquipmentShopItem.itemName + " " + generatedItem.getId(), generatedItem.getId(), "추가", heroEquipmentInventoryDto);
                        String equipmentLog = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                        loggingService.setLogging(userId, 2, equipmentLog);
                        map.put("heroEquipmentInventory", heroEquipmentInventoryDto);

                        /* 업적 : 장비 획득 (특정 품질) 미션 체크*/
                        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(), generatedItem.getItemClass(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                        /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
                        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), grade, gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                    }
                    else {
                        MyShopItemsList.MyShopItem myShopItem = myDarkObeShopInfos.myEquipmentList.stream()
                                .filter(a -> a.slotIndex == slotIndex)
                                .findAny()
                                .orElse(null);
                        if(myShopItem != null) {
                            List<HeroEquipmentInventory> heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());
                            if (heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
                            }

                            if (myShopItem.bought) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_BOUGHT.getIntegerValue(), "Fail! -> Cause: Aleady bought", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Fail! -> Cause: Aleady bought", ResponseErrorCode.ALREADY_BOUGHT);
                            }
                            myShopItem.bought = true;

                            List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
                            List<HeroEquipmentsTable> copyHeroEquipmentsTableList = new ArrayList<>();
                            int size = heroEquipmentsTableList.size();

                            DarkObeShopTable shopTable = gameDataTableService.DarkObeShopTableList().stream()
                                    .filter(a -> a.getId() == myShopItem.shopTableId)
                                    .findAny()
                                    .orElse(null);
                            if (shopTable == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find shopTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Fail! -> Cause: Can't Find shopTable.", ResponseErrorCode.NOT_FIND_DATA);
                            }
                            //가격 체크
                            if (shopTable.getCurrency().equals("gold")) {
                                currencyLogDto.setPreviousValue(user.getGold());
                                if (!user.SpendGold(shopTable.getCost())) {
                                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail! -> Cause: Need more Gold.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                    throw new MyCustomException("Fail! -> Cause: Need more Gold.", ResponseErrorCode.NEED_MORE_GOLD);
                                }
                                currencyLogDto.setChangeNum(-shopTable.getCost());
                                currencyLogDto.setPresentValue(user.getGold());
                                currencyLogDto.setCurrencyType("골드");
                            } else if (shopTable.getCurrency().equals("diamond")) {
                                currencyLogDto.setPreviousValue(user.getDiamond());
                                if (!user.SpendDiamond(shopTable.getCost())) {
                                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                    throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
                                }
                                currencyLogDto.setChangeNum(-shopTable.getCost());
                                currencyLogDto.setPresentValue(user.getDiamond());
                                currencyLogDto.setCurrencyType("다이아");
                            }else if (shopTable.getCurrency().equals("darkobe")) {
                                currencyLogDto.setPreviousValue(user.getDarkObe());
                                if(!user.SpendDarkObe(shopTable.getCost())) {
                                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DARKOBE.getIntegerValue(), "Fail! -> Cause: Need more DarkObe.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                    throw new MyCustomException("Fail! -> Cause: Need more DarkObe.", ResponseErrorCode.NEED_MORE_DARKOBE);
                                }
                                currencyLogDto.setChangeNum(-shopTable.getCost());
                                currencyLogDto.setCurrencyType("어둠의 보주");
                                currencyLogDto.setPresentValue(user.getDarkObe());
                            }
                            if(shopTable.getGettingItem().contains("Weapon")){
                                for(int i = 0; i < size; i++) {
                                    HeroEquipmentsTable heroEquipment = heroEquipmentsTableList.get(i);

                                    if(heroEquipment.getCode().contains("97") || heroEquipment.getCode().contains("99")) {
                                        if(heroEquipment.getKind().equals("Sword")
                                                || heroEquipment.getKind().equals("Spear")
                                                || heroEquipment.getKind().equals("Bow")
                                                || heroEquipment.getKind().equals("Gun")
                                                || heroEquipment.getKind().equals("Wand")){
                                            copyHeroEquipmentsTableList.add(heroEquipment);
                                        }
                                    }
                                }
                            }
                            else if(shopTable.getGettingItem().contains("Armors")){
                                for(int i = 0; i < size; i++) {
                                    HeroEquipmentsTable heroEquipment = heroEquipmentsTableList.get(i);

                                    if(heroEquipment.getCode().contains("97") || heroEquipment.getCode().contains("99")) {
                                        if(heroEquipment.getKind().equals("Armor")
                                                || heroEquipment.getKind().equals("Helmet")
                                                || heroEquipment.getKind().equals("Accessory")){
                                            copyHeroEquipmentsTableList.add(heroEquipment);
                                        }
                                    }
                                }
                            }

                            int randValue = (int) MathHelper.Range(0, copyHeroEquipmentsTableList.size());
                            HeroEquipmentsTable selectedHeroEquipment = copyHeroEquipmentsTableList.get(randValue);

                            String selectedClass = "C";
                            HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
                            int classValue = (int) EquipmentCalculate.GetClassValueFromClassCategory(selectedClass, classValues);

                            HeroEquipmentInventory generatedItem = EquipmentCalculate.CreateEquipment(userId, selectedHeroEquipment, selectedClass, classValue, gameDataTableService.OptionsInfoTableList());
                            generatedItem = heroEquipmentInventoryRepository.save(generatedItem);

                            HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
                            heroEquipmentInventoryDto.setUseridUser(userId);
                            heroEquipmentInventoryDto.setItem_Id(generatedItem.getItem_Id());
                            heroEquipmentInventoryDto.setItemClassValue(classValue);
                            heroEquipmentInventoryDto.setDecideDefaultAbilityValue(generatedItem.getDecideDefaultAbilityValue());
                            heroEquipmentInventoryDto.setDecideSecondAbilityValue(generatedItem.getDecideSecondAbilityValue());
                            heroEquipmentInventoryDto.setLevel(1);
                            String grade = selectedHeroEquipment.getGrade();
                            heroEquipmentInventoryDto.setMaxLevel(EquipmentCalculate.MaxLevel(grade));
                            heroEquipmentInventoryDto.setExp(0);
                            int gradeValue = EquipmentCalculate.GradeValue(grade);
                            heroEquipmentInventoryDto.setNextExp(EquipmentCalculate.CalculateNeedExp(1, gradeValue));
                            heroEquipmentInventoryDto.setItemClass(generatedItem.getItemClass());
                            heroEquipmentInventoryDto.setItemClassValue(generatedItem.getItemClassValue());
                            heroEquipmentInventoryDto.setOptionIds(generatedItem.getOptionIds());
                            heroEquipmentInventoryDto.setOptionValues(generatedItem.getOptionValues());
                            heroEquipmentInventoryDto.setId(generatedItem.getId());

                            currencyLogDto.setWorkingPosition("상점 어둠의 보주 상점 구매 -> " + myShopItem.itemName + " " + generatedItem.getId());
                            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                            loggingService.setLogging(userId, 1, log);
                            EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                            equipmentLogDto.setEquipmentLogDto("상점 어둠의 보주 상점 구매 -> " + myShopItem.itemName + " " + generatedItem.getId(), generatedItem.getId(), "추가", heroEquipmentInventoryDto);
                            String equipmentLog = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                            loggingService.setLogging(userId, 2, equipmentLog);
                            map.put("heroEquipmentInventory", heroEquipmentInventoryDto);

                            /* 업적 : 장비 획득 (특정 품질) 미션 체크*/
                            changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(), generatedItem.getItemClass(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                            /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
                            changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), grade, gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                        }
                        else {
                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find DarkShop slotIndex.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                            throw new MyCustomException("Fail! -> Cause: Can't Find DarkShop slotIndex.", ResponseErrorCode.NOT_FIND_DATA);
                        }
                    }
                }
            }
        }

        /*업적 : 미션 데이터 변경점 적용*/
        if (changedMissionsData) {
            jsonMyMissionData = JsonStringHerlper.WriteValueAsStringFromData(myMissionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMyMissionData);
            map.put("exchange_myMissionsData", true);
            map.put("myMissionsDataDto", myMissionsDataDto);
            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());
        }

        json_myShopMainInfo = JsonStringHerlper.WriteValueAsStringFromData(myShopItemsLis);
        myShopInfo.ResetMyShopInfos(json_myShopMainInfo);
        map.put("user", user);
        map.put("recycle", false);
        map.put("myShopItemsLis", myShopItemsLis);
        return map;
    }

    private BelongingInventoryDto AddEquipmentMaterialItem(Long userId, String gettingItemCode, int gettingCount, List<ItemType> itemTypeList, List<BelongingInventory> belongingInventoryList, List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList, BelongingInventoryLogDto belongingInventoryLogDto) {
        EquipmentMaterialInfoTable equipmentMaterial = equipmentMaterialInfoTableList.stream()
                .filter(a -> a.getCode().equals(gettingItemCode))
                .findAny()
                .orElse(null);
        if(equipmentMaterial == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: EquipmentMaterialInfoTable not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        BelongingInventory inventoryItem = belongingInventoryList.stream()
                .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Material && a.getItemId() == equipmentMaterial.getId())
                .findAny()
                .orElse(null);
        if(inventoryItem != null) {
            belongingInventoryLogDto.setPreviousValue(inventoryItem.getCount());
            inventoryItem.AddItem(gettingCount, equipmentMaterial.getStackLimit());
            BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
            belongingInventoryDto.InitFromDbData(inventoryItem);
            belongingInventoryLogDto.setItemId(inventoryItem.getItemId());
            belongingInventoryDto.setCount(gettingCount);
            belongingInventoryLogDto.setChangeNum(gettingCount);
            belongingInventoryLogDto.setPresentValue(inventoryItem.getCount());
            return belongingInventoryDto;
        }
        else {
            ItemType materialItemType = itemTypeList.stream()
                    .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Material)
                    .findAny()
                    .orElse(null);
            if(materialItemType == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ItemType Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: ItemType Can't find.", ResponseErrorCode.NOT_FIND_DATA);
            }

            BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
            belongingInventoryDto.setUseridUser(userId);
            belongingInventoryDto.setItemId(equipmentMaterial.getId());
            belongingInventoryDto.setCount(gettingCount);
            belongingInventoryDto.setItemType(materialItemType);
            BelongingInventory willAddBelongingInventoryItem = belongingInventoryDto.ToEntity();
            willAddBelongingInventoryItem = belongingInventoryRepository.save(willAddBelongingInventoryItem);
            belongingInventoryList.add(willAddBelongingInventoryItem);
            belongingInventoryDto.setId(willAddBelongingInventoryItem.getId());
            belongingInventoryLogDto.setItemId(willAddBelongingInventoryItem.getItemId());
            belongingInventoryLogDto.setPreviousValue(0);
            belongingInventoryLogDto.setChangeNum(gettingCount);
            belongingInventoryLogDto.setPresentValue(belongingInventoryDto.getCount());
            return belongingInventoryDto;
        }
    }
    private HeroEquipmentInventory generateShopHeroEquipment(Long userId, HeroEquipmentsTable heroEquipmentsTable, String itemClass, int classValue,List<EquipmentOptionsInfoTable> optionsInfoTableList) {
        HeroEquipmentInventory generatedEquipment;

        generatedEquipment = EquipmentCalculate.CreateEquipment(userId, heroEquipmentsTable, itemClass, classValue, optionsInfoTableList);
        return generatedEquipment;
    }
}
