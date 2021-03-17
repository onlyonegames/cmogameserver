package com.onlyonegames.eternalfantasia.domain.service.Event;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;
import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.EventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Event.EventResponseDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Event.MonsterKillEventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.GiftItemDto.GiftItemDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.HeroEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.BelongingInventoryLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CurrencyLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.EquipmentLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.GiftLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ReceiveItemCommonDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.GotchaCharacterResponseDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyGiftInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Event.CommonEventScheduler;
import com.onlyonegames.eternalfantasia.domain.model.entity.Event.MyMonsterKillEventMissionData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.ItemType;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyGiftInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Event.CommonEventSchedulerRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Event.MyMonsterKillEventMissionDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.BelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.HeroEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.ItemTypeRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import com.onlyonegames.eternalfantasia.etc.ApplySomeReward;
import com.onlyonegames.eternalfantasia.etc.EquipmentCalculate;
import com.onlyonegames.eternalfantasia.etc.EquipmentItemCategory;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.util.MathHelper;
import com.onlyonegames.util.StringMaker;
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
public class MonsterKillEventService {
    private final UserRepository userRepository;
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final MyMonsterKillEventMissionDataRepository myMonsterKillEventMissionDataRepository;
    private final CommonEventSchedulerRepository commonEventSchedulerRepository;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final ItemTypeRepository itemTypeRepository;
    private final MyGiftInventoryRepository myGiftInventoryRepository;
    private final HeroEquipmentInventoryRepository heroEquipmentInventoryRepository;
    private final MyCharactersRepository myCharactersRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;

    public Map<String, Object> GetMissionReward(Long userId, String missionCode, Map<String, Object> map){
        LocalDateTime now = LocalDateTime.now();
        List<CommonEventScheduler> monsterKillEventSchedulerList = commonEventSchedulerRepository.findByEventContentsTable_idAndStartTimeBeforeAndEndTimeAfter(3, now, now);
        if(1<monsterKillEventSchedulerList.size()){//TODO ErrorCode 추가 필요 EventScheduler가 중복 기간으로 설정됨.
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Wrong EventScheduler find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Wrong EventScheduler find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        CommonEventScheduler monsterKillEventScheduler = null;
        if(!monsterKillEventSchedulerList.isEmpty()){
            if(monsterKillEventSchedulerList.get(0).getEventContentsTable().getId()==3) {
                monsterKillEventScheduler = monsterKillEventSchedulerList.get(0);
            }
        }
        MyMonsterKillEventMissionData myMonsterKillEventMissionData = myMonsterKillEventMissionDataRepository.findByUseridUser(userId).orElse(null);
        if(myMonsterKillEventMissionData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMonsterKillEventMissionData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMonsterKillEventMissionData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String json_saveDataValue = myMonsterKillEventMissionData.getJson_saveDataValue();
        MonsterKillEventMissionDataDto monsterKillEventMissionDataDto = JsonStringHerlper.ReadValueFromJson(json_saveDataValue, MonsterKillEventMissionDataDto.class);

        MissionsDataDto.MissionData missionData = monsterKillEventMissionDataDto.monsterKillEventMissionsData.stream()
                .filter(data -> data.code.equals(missionCode))
                .findAny()
                .orElse(null);
        if(missionData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: monsterKillEventMissionData not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: monsterKillEventMissionData not find", ResponseErrorCode.NOT_FIND_DATA);
        }

        if(!missionData.success) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.MISSION_NOT_YET_COMPLETE.getIntegerValue(), "Fail! -> Cause: MonsterKillEventMission not Yet Complete", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MonsterKillEventMission not Yet Complete", ResponseErrorCode.MISSION_NOT_YET_COMPLETE);
        }
        if(missionData.rewardReceived) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_RECEIVED_MISSION_REWARD.getIntegerValue(), "Fail! -> Cause: MonsterKillEventMission already received reward", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MonsterKillEventMission already received reward", ResponseErrorCode.ALREADY_RECEIVED_MISSION_REWARD);
        }

        MonsterKillEventTable missionTable = gameDataTableService.MonsterKillEventTableList().stream()
                .filter(mission -> mission.getCode().equals(missionCode))
                .findAny()
                .orElse(null);
        if(missionTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MonsterKillEventTable not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MonsterKillEventTable not find", ResponseErrorCode.NOT_FIND_DATA);
        }

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

        ReceiveItemCommonDto receiveItemCommonDto = new ReceiveItemCommonDto();
        MyMissionsData myMissionsData = null;
        /*업적 : 체크 준비*/
        if(myMissionsData == null)
            myMissionsData = myMissionsDataRepository.findByUseridUser(userId).orElseThrow(() -> new MyCustomException("Fail! -> Cause: MyMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA));
        String jsonMyMissionData = myMissionsData.getJson_saveDataValue();
        MissionsDataDto myMissionsDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyMissionData, MissionsDataDto.class);
        boolean changedMissionsData = false;

        String gettingItemCode = missionTable.getGettingItem();
        String gettingCountString = Integer.toString(missionTable.getGettingCount());
        int gettingCountInt = missionTable.getGettingCount();
        StringMaker.Clear();
        StringMaker.stringBuilder.append("이터널패스 시즌 ");
        StringMaker.stringBuilder.append("프리패스 달성 보상 레밸: ");
        String logWorkingPosition = StringMaker.stringBuilder.toString();

        changedMissionsData = receiveItem(userId, gettingItemCode, gettingCountString, receiveItemCommonDto, myMissionsDataDto, belongingInventoryRepository,
                gameDataTableService, itemTypeRepository, loggingService, myGiftInventoryRepository, heroEquipmentInventoryRepository, myCharactersRepository, logWorkingPosition) || changedMissionsData;
        if(gettingItemCode.equals("gold")) {
            /* 패스 업적 : 골드획득*/
            changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.ERNING_GOLD.name(),"empty", gettingCountInt, gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
        }

        map.put("receiveItemCommonDto", receiveItemCommonDto);

        missionData.rewardReceived = true;
        String[] split = missionCode.split("-");
        List<MissionsDataDto.MissionData> clientData = monsterKillEventMissionDataDto.ImportQuestMissionSendToClient(gameDataTableService.MonsterKillEventTableList());
        String json = JsonStringHerlper.WriteValueAsStringFromData(monsterKillEventMissionDataDto);
        myMonsterKillEventMissionData.ResetSaveDataValue(json);
        EventResponseDto.MonsterKillEventDataDto monsterKillEventDataDto = new EventResponseDto.MonsterKillEventDataDto();
        monsterKillEventDataDto.SetMonsterKillEventDataDto(clientData, monsterKillEventScheduler.getEndTime());
        map.put("monsterKillEventDataDto", monsterKillEventDataDto);

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
                    GotchaCharacterResponseDto.GotchaCharacterInfo gotchaCharacterInfo = new GotchaCharacterResponseDto.GotchaCharacterInfo(selectedCharacter.getCodeHerostable(), belongingInventoryDto, null);
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
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                //장비
                if(heroEquipmentInventoryList == null)
                    heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());

                if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot())
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
                int randValue = (int)MathHelper.Range(0, probabilityList.size());
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
            /*특정 등급, 특정 클래스, 특정 종류의 장비중 하나*/
            else if(gettingItemCode.contains("equipment")) {
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
