package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.*;
import com.onlyonegames.eternalfantasia.domain.model.dto.Inventory.*;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.*;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.*;
import com.onlyonegames.eternalfantasia.domain.model.entity.*;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.MyArenaPlayData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.MyWorldBossPlayData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.*;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.*;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.MyArenaPlayDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.MyWorldBossPlayDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.*;
import com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard.BattlePowerLeaderboardService;
import com.onlyonegames.eternalfantasia.domain.service.Mail.MyMailBoxService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class GetterService {
    private final MyPixieInfoDataRepository myPixieInfoDataRepository;
    private final MyRuneLevelInfoDataRepository myRuneLevelInfoDataRepository;
    private final MyStatusInfoRepository myStatusInfoRepository;
    private final UserRepository userRepository;
    private final MyActiveSkillDataRepository myActiveSkillDataRepository;
    private final MyClassInventoryRepository myClassInventoryRepository;
    private final MyEquipmentInventoryRepository myEquipmentInventoryRepository;
    private final MyRuneInventoryRepository myRuneInventoryRepository;
    private final ErrorLoggingService errorLoggingService;
    private final MyBelongingInventoryRepository myBelongingInventoryRepository;
    private final MyEquipmentInfoRepository myEquipmentInfoRepository;
    private final GameDataTableService gameDataTableService;
    private final MyAccessoryInventoryRepository myAccessoryInventoryRepository;
    private final MyRelicInventoryRepository myRelicInventoryRepository;
    private final MyPassiveSkillDataRepository myPassiveSkillDataRepository;
    private final MyContentsInfoRepository myContentsInfoRepository;
    private final MyDungeonInfoRepository myDungeonInfoRepository;
    private final MyGachaInfoRepository myGachaInfoRepository;
    private final MyCollectionInfoRepository myCollectionInfoRepository;
    private final MyQuickMissionDataRepository myQuickMissionDataRepository;
    private final ServerStatusInfoRepository serverStatusInfoRepository;
    private final BattlePowerLeaderboardService battlePowerLeaderboardService;
    private final MyPassDataRepository myPassDataRepository;
    private final MyMissionInfoRepository myMissionInfoRepository;
    private final MyChatBlockInfoRepository myChatBlockInfoRepository;
    private final MyShopInfoRepository myShopInfoRepository;
    private final StandardTimeRepository standardTimeRepository;
    private final MyArenaPlayDataRepository myArenaPlayDataRepository;
    private final MyMailBoxService myMailBoxService;
    private final MyCostumeInventoryRepository myCostumeInventoryRepository;
    private final MyBoosterInfoRepository myBoosterInfoRepository;
    private final MyWorldBossPlayDataRepository myWorldBossPlayDataRepository;

    public Map<String, Object> Getter(Long userId, RequestDto requestList, Map<String, Object> map) throws IllegalAccessException, NoSuchFieldException {
        ServerStatusInfo serverStatusInfo = serverStatusInfoRepository.getOne(1);
        StandardTime standardTime = standardTimeRepository.findById(1).orElse(null);
        MyPixieInfoData myPixieInfoData = null;
        MyRuneLevelInfoData myRuneLevelInfoData = null;
        List<MyRuneInventory> myRuneInventoryList = null;
        MyActiveSkillData myActiveSkillData = null;
        List<MyClassInventory> myClassInventoryList = null;
        List<MyEquipmentInventory> myEquipmentInventoryList = null;
        List<MyBelongingInventory> myBelongingInventoryList = null;
        MyEquipmentInfo myEquipmentInfo = null;
        List<MyAccessoryInventory> myAccessoryInventoryList = null;
        List<MyRelicInventory> myRelicInventoryList = null;
        MyStatusInfo myStatusInfo = null;
        MyPassiveSkillData myPassiveSkillData = null;
        MyContentsInfo myContentsInfo = null;
        MyDungeonInfo myDungeonInfo = null;
        MyGachaInfo myGachaInfo = null;
        MyCollectionInfo myCollectionInfo = null;
        MyQuickMissionData myQuickMissionData = null;
        MyPassData myPassData = null;
        MyMissionInfo myMissionInfo = null;
        MyChatBlockInfo myChatBlockInfo = null;
        MyShopInfo myShopInfo = null;
        List<MyCostumeInventory> myCostumeInventoryList = null;
        MyBoosterInfo myBoosterInfo = null;

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found User", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Not Found User", ResponseErrorCode.NOT_FIND_DATA);
        }

        if (serverStatusInfo.getServerStatus() == 1 && user.getUserType() == 1) {
            throw new MyCustomException("Server Check", ResponseErrorCode.SERVER_CHECK);
        }

        if(user.isBlackUser()) {
            errorLoggingService.SetErrorLog(user.getId(), ResponseErrorCode.BLACK_USER.getIntegerValue(), "Black User", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Black User", ResponseErrorCode.BLACK_USER);
        }

        //Request에 따라 entity를 불러옴
        for (CommandDto cmd : requestList.cmds) {
            for(ContainerDto containerDto : cmd.containers) {
                switch (containerDto.container) {
                    case "MyPixieInfoData":
                        if (myPixieInfoData == null)
                            myPixieInfoData = myPixieInfoDataRepository.findByUseridUser(userId).orElse(null);

                        break;
                    case "PlayerInfo":
                        for (ElementDto i : containerDto.elements) {
                            switch (i.getElement()) {
                                case "gold":
                                case "diamond":
                                case "soulStone":
                                case "skillPoint":
                                case "moveStone":
                                case "fieldIndex":
                                case "battleStatus":
                                case "dungeonTicket":
                                case "arenaCoin":
                                case "mileage":
                                case "ancientCrystal":
                                case "redOrb":
                                case "greenOrb":
                                case "yellowOrb":
                                case "blueOrb":
                                    break;
                                case "runeLevel":
                                    if (myRuneLevelInfoData == null) {
                                        myRuneLevelInfoData = myRuneLevelInfoDataRepository.findByUseridUser(userId).orElse(null);
                                        if (myRuneLevelInfoData == null) {
                                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyRuneLevelInfoData", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                            throw new MyCustomException("Not Found MyRuneLevelInfoData", ResponseErrorCode.NOT_FIND_DATA);
                                        }
                                    }
                                    break;
                                case "runeInventory":
                                    if (myRuneInventoryList == null) {
                                        myRuneInventoryList = myRuneInventoryRepository.findAllByUseridUser(userId);
                                        if (myRuneInventoryList == null) {
                                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyRuneInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                            throw new MyCustomException("Not Found MyRuneInventory", ResponseErrorCode.NOT_FIND_DATA);
                                        }
                                    }
                                    break;
                            }
                        }
                        break;
                    case "UserInfo":
                        for (ElementDto i : containerDto.elements) {
                            switch (i.getElement()) {
                                case "userGameName":
                                case "level":
                                case "exp":
                                case "sexType":
                                    break;
                            }
                        }
                        break;
                    case "runeInventory":
                        if (myRuneInventoryList == null) {
                            myRuneInventoryList = myRuneInventoryRepository.findAllByUseridUser(userId);
                            if (myRuneInventoryList == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyRuneInventoryList", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Not Found MyRuneInventoryList", ResponseErrorCode.NOT_FIND_DATA);
                            }
                        }
                        break;
                    case "useUpItemInventory":
                        if (myBelongingInventoryList == null) {
                            myBelongingInventoryList = myBelongingInventoryRepository.findAllByUseridUser(userId);
                            if(myBelongingInventoryList == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyBelongingInventoryList", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Not Found MyBelongingInventoryList", ResponseErrorCode.NOT_FIND_DATA);
                            }
                        }
                        break;
                    case "equipmentInfo":
                        if (myEquipmentInfo == null) {
                            myEquipmentInfo = myEquipmentInfoRepository.findByUseridUser(userId).orElse(null);
                            if(myEquipmentInfo == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyEquipmentInfo", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Not Found MyEquipmentInfo", ResponseErrorCode.NOT_FIND_DATA);
                            }
                        }
                        break;
                    case "skillUserDataTable":
                        if (myActiveSkillData == null) {
                            myActiveSkillData = myActiveSkillDataRepository.findByUseridUser(userId).orElse(null);
                            if(myActiveSkillData == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found mMActiveSkillData", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Not Found MyActiveSkillData", ResponseErrorCode.NOT_FIND_DATA);
                            }
                        }
                    case "weaponInventory":
                        if (myEquipmentInventoryList == null) {
                            myEquipmentInventoryList = myEquipmentInventoryRepository.findAllByUseridUser(userId);
                            if (myEquipmentInventoryList == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyEquipmentInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Not Found MyEquipmentInventory", ResponseErrorCode.NOT_FIND_DATA);
                            }
                        }
                        break;
                    case "accessoryInventory":
                        if (myAccessoryInventoryList == null) {
                            myAccessoryInventoryList = myAccessoryInventoryRepository.findAllByUseridUser(userId);
                            if (myAccessoryInventoryList == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyAccessoryInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Not Found MyAccessoryInventory", ResponseErrorCode.NOT_FIND_DATA);
                            }
                        }
                        break;
                    case "artifactUserDataTable":
                        if (myRelicInventoryList == null) {
                            myRelicInventoryList = myRelicInventoryRepository.findAllByUseridUser(userId);
                            if(myRelicInventoryList == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyRelicInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Not Found MyRelicInventory", ResponseErrorCode.NOT_FIND_DATA);
                            }
                        }
                        break;
                    case "pixieUserData":
                    case "carvingRuneUserData":
                        if (myPixieInfoData == null) {
                            myPixieInfoData = myPixieInfoDataRepository.findByUseridUser(userId).orElse(null);
                            if (myPixieInfoData == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyPixieInfoData", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Not Found MyPixieInfoData", ResponseErrorCode.NOT_FIND_DATA);
                            }
                        }
                        break;
                    case "heroClassInventory":
                        if (myClassInventoryList == null) {
                            myClassInventoryList = myClassInventoryRepository.findAllByUseridUser(userId);
                            if (myClassInventoryList == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyClassInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Not Found MyClassInventory", ResponseErrorCode.NOT_FIND_DATA);
                            }
                        }
                        break;
                    case "upgradeStatusUserData":
                        if (myStatusInfo == null) {
                            myStatusInfo = myStatusInfoRepository.findByUseridUser(userId).orElse(null);
                            if (myStatusInfo == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyClassInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Not Found MyClassInventory", ResponseErrorCode.NOT_FIND_DATA);
                            }
                        }
                        break;
                    case "passiveSkillUserDataTable":
                        if (myPassiveSkillData == null) {
                            myPassiveSkillData = myPassiveSkillDataRepository.findByUseridUser(userId).orElse(null);
                            if(myPassiveSkillData == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyPassiveSkillData", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Not Found MyPassiveSkillData", ResponseErrorCode.NOT_FIND_DATA);
                            }
                        }
                        break;
                    case "contentsInfo":
                        if (myContentsInfo == null) {
                            myContentsInfo = myContentsInfoRepository.findByUseridUser(userId).orElse(null);
                            if(myContentsInfo == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyContentsInfo", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Not Found MyContentsInfo", ResponseErrorCode.NOT_FIND_DATA);
                            }
                        }
                        break;
                    case "dungeonInfo":
                        if (myDungeonInfo == null) {
                            myDungeonInfo = myDungeonInfoRepository.findByUseridUser(userId).orElse(null);
                            if(myDungeonInfo == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyContentsInfo", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Not Found MyContentsInfo", ResponseErrorCode.NOT_FIND_DATA);
                            }
                        }
                        break;
                    case "gachaInfo":
                        if (myGachaInfo == null) {
                            myGachaInfo = myGachaInfoRepository.findByUseridUser(userId).orElse(null);
                            if(myGachaInfo == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyGachaInfo", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Not Found MyGachaInfo", ResponseErrorCode.NOT_FIND_DATA);
                            }
                        }
                        break;
                    case "collectionInfo":
                        if (myCollectionInfo == null) {
                            myCollectionInfo = myCollectionInfoRepository.findByUseridUser(userId).orElse(null);
                            if(myCollectionInfo == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyCollectionInfo", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Not Found MyCollectionInfo", ResponseErrorCode.NOT_FIND_DATA);
                            }
                        }
                        break;
                    case "quickMissionInfo":
                        if(myQuickMissionData == null) {
                            myQuickMissionData = myQuickMissionDataRepository.findByUseridUser(userId).orElse(null);
                            if(myQuickMissionData == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyQuickMissionData", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Not Found MyQuickMissionData", ResponseErrorCode.NOT_FIND_DATA);
                            }
                        }
                        break;
                    case "passInfo":
                        if (myPassData == null) {
                            myPassData = myPassDataRepository.findByUseridUser(userId).orElse(null);
                            if(myPassData == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyPassData", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Not Found MyPassData", ResponseErrorCode.NOT_FIND_DATA);
                            }
                        }
                        break;
                    case "missionInfo":
                        if (myMissionInfo == null) {
                            myMissionInfo = myMissionInfoRepository.findByUseridUser(userId).orElse(null);
                            if (myMissionInfo == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyMissionInfo", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Not Found MyMissionInfo", ResponseErrorCode.NOT_FIND_DATA);
                            }
                        }
                        break;
                    case "chatBlockInfo":
                        if (myChatBlockInfo == null) {
                            myChatBlockInfo = myChatBlockInfoRepository.findByUseridUser(userId).orElse(null);
                            if (myChatBlockInfo == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyChatBlockInfo", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Not Found MyChatBlockInfo", ResponseErrorCode.NOT_FIND_DATA);
                            }
                        }
                        break;
                    case "myShopInfo":
                        if (myShopInfo == null) {
                            myShopInfo = myShopInfoRepository.findByUseridUser(userId).orElse(null);
                            if (myShopInfo == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyShopInfo", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Not Found MyShopInfo", ResponseErrorCode.NOT_FIND_DATA);
                            }
                        }
                        break;
                    case "costumeInventory":
                        if (myCostumeInventoryList == null) {
                            myCostumeInventoryList = myCostumeInventoryRepository.findAllByUseridUser(userId);
                            if (myCostumeInventoryList == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyCostumeInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Not Found MyCostumeInventory", ResponseErrorCode.NOT_FIND_DATA);
                            }
                        }
                        break;
                    case "booster":
                        if (myBoosterInfo == null) {
                            myBoosterInfo = myBoosterInfoRepository.findByUseridUser(userId).orElse(null);
                            if (myBoosterInfo == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyBoosterInfo", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Not Found MyBoosterInfo", ResponseErrorCode.NOT_FIND_DATA);
                            }
                        }
                        break;
                }
            }
        }

        for (CommandDto cmd : requestList.cmds) {
            switch (cmd.cmd) {
                case "get":
                    for (ContainerDto container : cmd.containers) {
                        switch (container.getContainer()) {
                            case "PlayerInfo":
                                for (ElementDto element : container.elements) {
                                    switch (element.getElement()) {
                                        case "gold":
                                            element.SetValue(user.getGold());
                                            break;
                                        case "diamond":
                                            element.SetValue(user.getDiamond());
                                            break;
                                        case "soulStone":
                                            element.SetValue(user.getSoulStone());
                                            break;
                                        case "skillPoint":
                                            element.SetValue(user.getSkillPoint());
                                            break;
                                        case "moveStone":
                                            element.SetValue(user.getMoveStone());
                                            break;
                                        case "runeLevel":
                                            element.SetValue(myRuneLevelInfoData.getLevel());
                                            break;
                                        case "fieldIndex":
                                            element.SetValue(user.getFieldIndex());
                                            break;
                                        case "battleStatus":
                                            element.SetValue(user.getBattleStatus());
                                            break;
                                        case "dungeonTicket":
                                            element.SetValue(user.getDungeonTicket());
                                            break;
                                        case "arenaCoin":
                                            element.SetValue(user.getArenaCoin());
                                            break;
                                        case "dragonCoin":
                                            element.SetValue(user.getDragonCoin());
                                            break;
                                        case "mileage":
                                            element.SetValue(user.getMileage());
                                            break;
                                        case "lastDayResetTime":
                                            element.SetValue(user.getLastDayResetTime().toString());
                                            break;
                                        case "lastWeekResetTime":
                                            element.SetValue(user.getLastWeekResetTime().toString());
                                            break;
                                        case "lastMonthResetTime":
                                            element.SetValue(user.getLastMonthResetTime().toString());
                                            break;
                                        case "costumeTicket":
                                            element.SetValue(user.getCostumeTicket());
                                            break;
                                        case "ancientCrystal":
                                            element.SetValue(user.getAncientCrystal());
                                            break;
                                        case "redOrb":
                                            element.SetValue(user.getRedOrb());
                                            break;
                                        case "greenOrb":
                                            element.SetValue(user.getGreenOrb());
                                            break;
                                        case "yellowOrb":
                                            element.SetValue(user.getYellowOrb());
                                            break;
                                        case "blueOrb":
                                            element.SetValue(user.getBlueOrb());
                                            break;
                                    }
                                }
                                break;
                            case "UserInfo":
                                for (ElementDto element : container.elements) {
                                    switch (element.getElement()) {
                                        case "userGameName":
                                            element.SetValue(user.getUserGameName());
                                            break;
                                        case "level":
                                            element.SetValue(user.getLevel());
                                            break;
                                        case "exp":
                                            element.SetValue(user.getExp());
                                            break;
                                        case "sexType":
                                            element.SetValue(user.getSexType());
                                            break;
                                        case "adRemove":
                                            element.SetValue(user.isAdRemove());
                                    }
                                }
                                break;
                            case "runeInventory":
                                List<ElementDto> runeInventoryElementDtoList = new ArrayList<>();
                                boolean flag = false;
                                for (ElementDto element : container.elements) {
                                    if(element.getElement().equals("all")) {
                                        for(MyRuneInventory myRuneInventory : myRuneInventoryList){
                                            RuneInventoryResponseDto runeInventoryResponseDto = new RuneInventoryResponseDto();
                                            runeInventoryResponseDto.SetRuneInventoryResponseDto(myRuneInventory);
                                            String rune_Json = JsonStringHerlper.WriteValueAsStringFromData(runeInventoryResponseDto);
                                            ElementDto elementDto = new ElementDto();
                                            elementDto.SetElement(myRuneInventory.getCode(), rune_Json);
                                            runeInventoryElementDtoList.add(elementDto);
                                        }
                                        flag = true;
                                        break;
                                    }
                                    MyRuneInventory myRuneInventory = myRuneInventoryList.stream().filter(j -> j.getCode().equals(element.getElement())).findAny().orElse(null);
                                    if (myRuneInventory == null) {
                                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyRuneInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                        throw new MyCustomException("Not Found MyRuneInventory", ResponseErrorCode.NOT_FIND_DATA);
                                    }
                                    RuneInventoryResponseDto runeInventoryResponseDto = new RuneInventoryResponseDto();
                                    runeInventoryResponseDto.SetRuneInventoryResponseDto(myRuneInventory);
                                    String rune_Json = JsonStringHerlper.WriteValueAsStringFromData(runeInventoryResponseDto);
                                    element.SetValue(rune_Json);
                                }
                                if(flag){
                                    container.elements = runeInventoryElementDtoList;
                                }
                                break;
                            case "useUpItemInventory":
                                List<ElementDto> elementDtoList = new ArrayList<>();
                                boolean itemFlag = false;
                                for (ElementDto temp : container.elements) {
                                    if(temp.getElement().equals("all")) {
                                        for(MyBelongingInventory j : myBelongingInventoryList) {
                                            ElementDto inventory = new ElementDto();
                                            BelongingInventoryJsonData belongingInventoryJsonData = new BelongingInventoryJsonData();
                                            belongingInventoryJsonData.SetBelongingInventoryJsonData(j.getCount(), j.getSlotNo(), j.getSlotPercent());
                                            String jsonData = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryJsonData);
                                            inventory.SetElement(j.getCode(), jsonData);
                                            elementDtoList.add(inventory);
                                        }
                                        itemFlag = true;
                                        break;
                                    }
                                    MyBelongingInventory myBelongingInventory = myBelongingInventoryList.stream().filter(j -> j.getCode().equals(temp.getElement())).findAny().orElse(null);
                                    if(myBelongingInventory == null) {
                                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyBelongingInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                        throw new MyCustomException("Not Found MyBelongingInventory", ResponseErrorCode.NOT_FIND_DATA);
                                    }
                                    BelongingInventoryJsonData belongingInventoryJsonData = new BelongingInventoryJsonData();
                                    belongingInventoryJsonData.SetBelongingInventoryJsonData(myBelongingInventory.getCount(), myBelongingInventory.getSlotNo(), myBelongingInventory.getSlotPercent());
                                    String jsonData = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryJsonData);
                                    temp.SetValue(jsonData);
                                }
                                if(itemFlag) {
                                    container.elements = elementDtoList;
                                }
                                break;
                            case "pixieUserData":
                                List<ElementDto> pixieElementDtoList = new ArrayList<>();
                                boolean pixieFlag = false;
                                for(ElementDto element : container.elements) {
                                    if(element.getElement().equals("all")) {
                                        for(Field field : myPixieInfoData.getClass().getDeclaredFields()) {
                                            String name = field.getName();
                                            if(name.equals("id") || name.equals("useridUser") || name.equals("runeSlot1")
                                                    || name.equals("runeSlot2") || name.equals("runeSlot3")
                                                    || name.equals("runeSlot4") || name.equals("runeSlot5")
                                                    || name.equals("runeSlot6") || name.equals("createddate") || name.equals("modifieddate"))
                                                continue;
                                            ElementDto elementDto = new ElementDto();
                                            elementDto.SetElement(field.getName(), field.get(myPixieInfoData).toString());
                                            pixieElementDtoList.add(elementDto);
                                        }
                                        pixieFlag = true;
                                        break;
                                    }
                                    Field field = myPixieInfoData.getClass().getDeclaredField(element.getElement());
                                    element.SetValue(field.get(myPixieInfoData).toString());
                                }
                                if(pixieFlag)
                                    container.elements = pixieElementDtoList;
                                break;
                            case "carvingRuneUserData":
                                List<ElementDto> carvingRuneElementDtoList = new ArrayList<>();
                                boolean carvingRuneFlag = false;
                                for(ElementDto element : container.elements) {
                                    if(element.getElement().equals("all")) {
                                        for(Field field : myPixieInfoData.getClass().getDeclaredFields()) {
                                            String name = field.getName();
                                            if(name.equals("id") || name.equals("useridUser") || name.equals("level") || name.equals("exp"))
                                                continue;
                                            ElementDto elementDto = new ElementDto();
                                            elementDto.SetElement(field.getName(), field.get(myPixieInfoData).toString());
                                            carvingRuneElementDtoList.add(elementDto);
                                        }
                                        carvingRuneFlag = true;
                                        break;
                                    }
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.delete(0, stringBuilder.length()-1);
                                    stringBuilder.append("runeSlot");
                                    stringBuilder.append(element.getElement());
                                    Field field = myPixieInfoData.getClass().getDeclaredField(stringBuilder.toString());
                                    element.SetValue(field.get(myPixieInfoData).toString());
                                }
                                if(carvingRuneFlag)
                                    container.elements = carvingRuneElementDtoList;
                                break;
                            case "heroClassInventory":
                                List<ElementDto> heroClassElementDtoList = new ArrayList<>();
                                boolean heroClassFlag = false;
                                for(ElementDto element : container.elements) {
                                    if(element.getElement().equals("all")) {
                                        for(MyClassInventory myClassInventory : myClassInventoryList) {
                                            ClassInventoryResponseDto classInventoryResponseDto = new ClassInventoryResponseDto();
                                            classInventoryResponseDto.InitFromDB(myClassInventory);
                                            String class_Json = JsonStringHerlper.WriteValueAsStringFromData(classInventoryResponseDto);
                                            ElementDto elementDto = new ElementDto();
                                            elementDto.SetElement(myClassInventory.getCode(), class_Json);
                                            heroClassElementDtoList.add(elementDto);
                                        }
                                        heroClassFlag = true;
                                        break;
                                    }
                                    MyClassInventory myClassInventory = myClassInventoryList.stream().filter(i -> i.getCode().equals(element.getElement())).findAny().orElse(null);
                                    if(myClassInventory == null) {
                                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyClassInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                        throw new MyCustomException("Not Found MyClassInventory", ResponseErrorCode.NOT_FIND_DATA);
                                    }
                                    ClassInventoryResponseDto classInventoryResponseDto = new ClassInventoryResponseDto();
                                    classInventoryResponseDto.InitFromDB(myClassInventory);
                                    String class_Json = JsonStringHerlper.WriteValueAsStringFromData(classInventoryResponseDto);
                                    element.SetValue(class_Json);
                                }
                                if(heroClassFlag)
                                    container.elements = heroClassElementDtoList;
                                break;
                            case "upgradeStatusUserData":
                                List<ElementDto> statusElementDtoList = new ArrayList<>();
                                boolean statusFlag = false;
                                for(ElementDto element : container.elements) {
                                    if(element.getElement().equals("all")) {
                                        for(Field field : myStatusInfo.getClass().getDeclaredFields()) {
                                            String name = field.getName();
                                            if(name.equals("id") || name.equals("useridUser"))
                                                continue;
                                            ElementDto elementDto = new ElementDto();
                                            elementDto.SetElement(name, field.get(myStatusInfo).toString());
                                            statusElementDtoList.add(elementDto);
                                        }
                                        statusFlag = true;
                                        break;
                                    }
                                    Field field = myStatusInfo.getClass().getDeclaredField(element.getElement());
                                    element.SetValue(field.get(myStatusInfo).toString());
                                }
                                if(statusFlag)
                                    container.elements = statusElementDtoList;
                                break;
                            case "skillUserDataTable":
                                List<ElementDto> activeSkillElementDtoList = new ArrayList<>();
                                boolean activeSkillFlag = false;
                                ActiveSkillDataJsonDto activeSkillDataJsonDto = JsonStringHerlper.ReadValueFromJson(myActiveSkillData.getJson_saveDataValue(), ActiveSkillDataJsonDto.class);
                                for(ElementDto element : container.elements) {
                                    if(element.getElement().equals("all")) {
                                        for(ActiveSkillDataJsonDto.SkillInfo skillInfo : activeSkillDataJsonDto.getSkillInfoList()) {
                                            ElementDto elementDto = new ElementDto();
                                            String skill_Json = JsonStringHerlper.WriteValueAsStringFromData(skillInfo);
                                            elementDto.SetElement(Integer.toString(skillInfo.id), skill_Json);
                                            activeSkillElementDtoList.add(elementDto);
                                        }
                                        activeSkillFlag = true;
                                        break;
                                    }
                                    ActiveSkillDataJsonDto.SkillInfo skillInfo = activeSkillDataJsonDto.skillInfoList.stream().filter(i -> i.id == Integer.parseInt(element.getElement())).findAny().orElse(null);
                                    if(skillInfo == null) {
                                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found SkillInfo", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                        throw new MyCustomException("Not Found SkillInfo", ResponseErrorCode.NOT_FIND_DATA);
                                    }
                                    String skill_Json = JsonStringHerlper.WriteValueAsStringFromData(skillInfo);
                                    element.SetValue(skill_Json);
                                }
                                if(activeSkillFlag)
                                    container.elements = activeSkillElementDtoList;
                                break;
                            case "equipmentInfo":
                                List<ElementDto> equipmentElementDtoList = new ArrayList<>();
                                boolean equipmentFlag = false;
                                for(ElementDto element : container.elements) {
                                    if(element.getElement().equals("all")) {
                                        for(Field j : myEquipmentInfo.getClass().getDeclaredFields()) {
                                            String name = j.getName();
                                            if(name.equals("id") || name.equals("useridUser") || name.equals("createdate") || name.equals("modifieddate"))
                                                continue;
                                            ElementDto inventory = new ElementDto();
                                            inventory.SetElement(j.getName(), j.get(myEquipmentInfo).toString());
                                            equipmentElementDtoList.add(inventory);
                                        }
                                        equipmentFlag = true;
                                        break;
                                    }
                                    Field j = myEquipmentInfo.getClass().getDeclaredField(element.getElement());
                                    element.SetValue(j.get(myEquipmentInfo).toString());
                                }
                                if(equipmentFlag) {
                                    container.elements = equipmentElementDtoList;
                                }
                                break;
                            case "weaponInventory":
                                List<ElementDto> weaponElementDtoList = new ArrayList<>();
                                boolean weaponFlag = false;
                                for(ElementDto temp : container.elements) {
                                    if(temp.getElement().equals("all")) {
                                        for (MyEquipmentInventory j : myEquipmentInventoryList) {
                                            ElementDto inventory = new ElementDto();
                                            WeaponInventoryResponseDto weaponInventoryResponseDto = new WeaponInventoryResponseDto();
                                            weaponInventoryResponseDto.InitFromDB(j);
                                            String jsonData = JsonStringHerlper.WriteValueAsStringFromData(weaponInventoryResponseDto);
                                            inventory.SetElement(j.getCode(), jsonData);
                                            weaponElementDtoList.add(inventory);
                                        }
                                        weaponFlag = true;
                                        break;
                                    }
                                    MyEquipmentInventory myEquipmentInventory = myEquipmentInventoryList.stream().filter(i -> i.getCode().equals(temp.getElement())).findAny().orElse(null);
                                    if(myEquipmentInventory == null) {
                                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyEquipmentInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                        throw new MyCustomException("Not Found MyEquipmentInventory", ResponseErrorCode.NOT_FIND_DATA);
                                    }
                                    WeaponInventoryResponseDto weaponInventoryResponseDto = new WeaponInventoryResponseDto();
                                    weaponInventoryResponseDto.InitFromDB(myEquipmentInventory);
                                    String jsonData = JsonStringHerlper.WriteValueAsStringFromData(weaponInventoryResponseDto);
                                    temp.SetValue(jsonData);
                                }
                                if (weaponFlag)
                                    container.elements = weaponElementDtoList;
                                break;
                            case "accessoryInventory":
                                List<ElementDto> accessoryElementDtoList = new ArrayList<>();
                                boolean accessoryFlag = false;
                                for(ElementDto element : container.elements) {
                                    if(element.getElement().equals("all")) {
                                        for(MyAccessoryInventory item : myAccessoryInventoryList) {
                                            ElementDto inventory = new ElementDto();
                                            AccessoryInventoryResponseDto accessoryInventoryResponseDto = new AccessoryInventoryResponseDto();
                                            accessoryInventoryResponseDto.InitFromDB(item);
                                            String jsonData = JsonStringHerlper.WriteValueAsStringFromData(accessoryInventoryResponseDto);
                                            inventory.SetElement(item.getCode(), jsonData);
                                            accessoryElementDtoList.add(inventory);
                                        }
                                        accessoryFlag = true;
                                        break;
                                    }
                                    MyAccessoryInventory myAccessoryInventory = myAccessoryInventoryList.stream().filter(i -> i.getCode().equals(element.getElement())).findAny().orElse(null);
                                    if(myAccessoryInventory == null) {
                                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyAccessoryInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                        throw new MyCustomException("Not Found MyAccessoryInventory", ResponseErrorCode.NOT_FIND_DATA);
                                    }
                                    AccessoryInventoryResponseDto accessoryInventoryResponseDto = new AccessoryInventoryResponseDto();
                                    accessoryInventoryResponseDto.InitFromDB(myAccessoryInventory);
                                    String jsonData = JsonStringHerlper.WriteValueAsStringFromData(accessoryInventoryResponseDto);
                                    element.SetValue(jsonData);
                                }
                                if (accessoryFlag)
                                    container.elements = accessoryElementDtoList;
                                break;
                            case "artifactUserDataTable":
                                List<ElementDto> relicElementDtoList = new ArrayList<>();
                                boolean relicFlag = false;
                                for(ElementDto element : container.elements) {
                                    if(element.getElement().equals("all")) {
                                        for(MyRelicInventory item : myRelicInventoryList) {
                                            ElementDto inventory = new ElementDto();
                                            RelicInventoryResponseDto relicInventoryResponseDto = new RelicInventoryResponseDto();
                                            relicInventoryResponseDto.InitFromDB(item);
                                            String jsonData = JsonStringHerlper.WriteValueAsStringFromData(relicInventoryResponseDto);
                                            inventory.SetElement(Integer.toString(item.getTable_id()), jsonData);
                                            relicElementDtoList.add(inventory);
                                        }
                                        relicFlag = true;
                                        break;
                                    }
                                    MyRelicInventory myRelicInventory = myRelicInventoryList.stream().filter(i -> i.getTable_id() == Integer.parseInt(element.getElement())).findAny().orElse(null);
                                    if(myRelicInventory == null) {
                                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyRelicInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                        throw new MyCustomException("Not Found MyRelicInventory", ResponseErrorCode.NOT_FIND_DATA);
                                    }
                                    RelicInventoryResponseDto relicInventoryResponseDto = new RelicInventoryResponseDto();
                                    relicInventoryResponseDto.InitFromDB(myRelicInventory);
                                    String jsonData = JsonStringHerlper.WriteValueAsStringFromData(relicInventoryResponseDto);
                                    element.SetValue(jsonData);
                                }
                                if (relicFlag)
                                    container.elements = relicElementDtoList;
                                break;
                            case "passiveSkillUserDataTable":
                                List<ElementDto> passiveSkillElementDtoList = new ArrayList<>();
                                boolean passiveSkillFlag = false;
                                PassiveSkillDataJsonDto passiveSkillDataJsonDto = JsonStringHerlper.ReadValueFromJson(myPassiveSkillData.getJson_saveDataValue(), PassiveSkillDataJsonDto.class);
                                for(ElementDto element : container.elements) {
                                    if(element.getElement().equals("all")) {
                                        for(PassiveSkillDataJsonDto.PassiveSkillInfo skillInfo : passiveSkillDataJsonDto.passiveSkillInfoList) {
                                            ElementDto elementDto = new ElementDto();
                                            String skill_Json = JsonStringHerlper.WriteValueAsStringFromData(skillInfo);
                                            elementDto.SetElement(Integer.toString(skillInfo.id), skill_Json);
                                            passiveSkillElementDtoList.add(elementDto);
                                        }
                                        passiveSkillFlag = true;
                                        break;
                                    }
                                    PassiveSkillDataJsonDto.PassiveSkillInfo skillInfo = passiveSkillDataJsonDto.passiveSkillInfoList.stream().filter(i -> i.id == Integer.parseInt(element.getElement())).findAny().orElse(null);
                                    if(skillInfo == null) {
                                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found SkillInfo", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                        throw new MyCustomException("Not Found SkillInfo", ResponseErrorCode.NOT_FIND_DATA);
                                    }
                                    String skill_Json = JsonStringHerlper.WriteValueAsStringFromData(skillInfo);
                                    element.SetValue(skill_Json);
                                }
                                if(passiveSkillFlag)
                                    container.elements = passiveSkillElementDtoList;
                                break;
                            case "contentsInfo":
                                for(ElementDto element : container.elements) {
                                    Field j = myContentsInfo.getClass().getDeclaredField(element.getElement());
                                    element.SetValue(j.get(myContentsInfo).toString());
                                }
                                break;
                            case "dungeonInfo":
                                for(ElementDto element : container.elements) {
                                    Field field = myDungeonInfo.getClass().getDeclaredField(element.getElement());
                                    element.SetValue((field.get(myDungeonInfo).toString()));
                                }
                                break;
                            case "gachaInfo":
                                for(ElementDto element : container.elements) {
                                    Field field = myGachaInfo.getClass().getDeclaredField(element.getElement());
                                    element.SetValue((field.get(myGachaInfo).toString()));
                                }
                                break;
                            case "collectionInfo":
                                for(ElementDto element : container.elements) {
                                    Field field = myCollectionInfo.getClass().getDeclaredField(element.getElement());
                                    element.SetValue(field.get(myCollectionInfo).toString());
                                }
                                break;
                            case "quickMissionInfo":
                                for (ElementDto element : container.elements) {
                                    Field field = myQuickMissionData.getClass().getDeclaredField(element.getElement());
                                    element.SetValue(field.get(myQuickMissionData).toString());
                                }
                                break;
                            case "serverStatus":
                                for(ElementDto element : container.elements) {
                                    element.SetValue(serverStatusInfo.getServerStatus());
                                }
                                break;
                            case "battlePower":
                                for (ElementDto element : container.elements) {
                                    if (element.getElement().equals("battlePower"))
                                        element.SetValue(user.getBattlePower());
                                }
                            case "passInfo":
                                for (ElementDto element : container.elements) {
                                    Field field = myPassData.getClass().getDeclaredField(element.getElement());
                                    element.SetValue(field.get(myPassData).toString());
                                }
                                break;
                            case "missionInfo":
                                for (ElementDto element : container.elements) {
                                    Field field = myMissionInfo.getClass().getDeclaredField(element.getElement());
                                    element.SetValue(field.get(myMissionInfo).toString());
                                }
                                break;
                            case "chatBlockInfo":
                                for (ElementDto element : container.elements) {
                                    Field field = myChatBlockInfo.getClass().getDeclaredField(element.getElement());
                                    element.SetValue(field.get(myChatBlockInfo).toString());
                                }
                                break;
                            case "myShopInfo":
                                for (ElementDto element : container.elements) {
                                    Field field = myShopInfo.getClass().getDeclaredField(element.getElement());
                                    element.SetValue(field.get(myShopInfo).toString());
                                }
                                break;
                            case "standardTime":
                                for (ElementDto element : container.elements) {
                                    Field field = standardTime.getClass().getDeclaredField(element.getElement());
                                    element.SetValue(field.get(standardTime).toString());
                                }
                                break;
                            case "costumeInventory":
                                List<ElementDto> costumeElementDtoList = new ArrayList<>();
                                boolean costumeFlag = false;
                                for (ElementDto element : container.elements) {
                                    if (element.getElement().equals("all")) {
                                        for (MyCostumeInventory myCostumeInventory : myCostumeInventoryList) {
                                            CostumeInventoryResponseDto costumeInventoryResponseDto = new CostumeInventoryResponseDto();
                                            costumeInventoryResponseDto.setCode(myCostumeInventory.getCode());
                                            String costume_Json = JsonStringHerlper.WriteValueAsStringFromData(costumeInventoryResponseDto);
                                            ElementDto elementDto = new ElementDto();
                                            elementDto.SetElement(myCostumeInventory.getCode(), costume_Json);
                                            costumeElementDtoList.add(elementDto);
                                        }
                                        costumeFlag = true;
                                        break;
                                    }
                                    MyCostumeInventory myCostumeInventory = myCostumeInventoryList.stream().filter(i -> i.getCode().equals(element.getElement())).findAny().orElse(null);
                                    if(myCostumeInventory == null) {
                                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyCostumeInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                        throw new MyCustomException("Not Found MyCostumeInventory", ResponseErrorCode.NOT_FIND_DATA);
                                    }
                                    CostumeInventoryResponseDto costumeInventoryResponseDto = new CostumeInventoryResponseDto();
                                    costumeInventoryResponseDto.setCode(myCostumeInventory.getCode());
                                    String costume_Json = JsonStringHerlper.WriteValueAsStringFromData(costumeInventoryResponseDto);
                                    element.SetValue(costume_Json);
                                }
                                if (costumeFlag)
                                    container.elements = costumeElementDtoList;
                                break;
                            case "booster":
                                for (ElementDto element : container.elements) {
                                    Field field = myBoosterInfo.getClass().getDeclaredField(element.getElement());
                                    element.SetValue(field.get(myBoosterInfo).toString());
                                }
                                break;
                        }
                    }
                    break;
                case "set":
                    for (ContainerDto container : cmd.containers) {
                        switch (container.container) {
                            case "PlayerInfo":
                                for (ElementDto element : container.elements) {
                                    switch (element.getElement()) {
                                        case "gold":
                                            user.SetGold(element.getValue());
                                            break;
                                        case "diamond":
                                            user.SetDiamond(element.getValue());
                                            break;
                                        case "soulStone":
                                            user.SetSoulStone(element.getValue());
                                            break;
                                        case "skillPoint":
                                            user.SetSkillPoint(element.getValue());
                                            break;
                                        case "moveStone":
                                            user.SetMoveStone(element.getValue());
                                            break;
                                        case "runeLevel":
                                            myRuneLevelInfoData.SetLevel(element.getValue());
                                            break;
                                        case "fieldIndex":
                                            user.SetFieldIndex(element.getValue());
                                            break;
                                        case "battleStatus":
                                            user.SetBattleStatus(element.getValue());
                                            break;
                                        case "dungeonTicket":
                                            user.SetDungeonTicket(element.getValue());
                                            break;
                                        case "arenaCoin":
                                            user.SetArenaCoin(element.getValue());
                                            break;
                                        case "dragonCoin":
                                            user.SetDragonCoin(element.getValue());
                                            break;
                                        case "mileage":
                                            user.SetMileage(element.getValue());
                                            break;
                                        case "costumeTicket":
                                            user.SetCostumeTicket(element.getValue());
                                            break;
                                        case "ancientCrystal":
                                            user.SetAncientCrystal(element.getValue());
                                            break;
                                        case "redOrb":
                                            user.SetRedOrb(element.getValue());
                                            break;
                                        case "greenOrb":
                                            user.SetGreenOrb(element.getValue());
                                            break;
                                        case "yellowOrb":
                                            user.SetYellowOrb(element.getValue());
                                            break;
                                        case "blueOrb":
                                            user.SetBlueOrb(element.getValue());
                                            break;
                                    }
                                }
                                break;
                            case "UserInfo":
                                for (ElementDto element : container.elements) {
                                    switch (element.getElement()) {
                                        case "level":
                                            user.SetLevel(element.getValue());
                                            break;
                                        case "exp":
                                            user.SetExp(element.getValue());
                                            break;
                                        case "sexType":
                                            user.SetSexType(Integer.parseInt(element.getValue()));
                                            break;
                                    }
                                }
                                break;
                            case "runeInventory":
                                for (ElementDto element : container.elements) {
                                    {
                                        RuneInventoryResponseDto runeInventoryResponseDto = JsonStringHerlper.ReadValueFromJson(element.getValue(), RuneInventoryResponseDto.class);
                                        MyRuneInventory myRuneInventory = myRuneInventoryList.stream().filter(j -> j.getCode().equals(runeInventoryResponseDto.getCode())).findAny().orElse(null);
                                        if (myRuneInventory == null) {
                                            MyRuneInventoryDto myRuneInventoryDto = new MyRuneInventoryDto();
                                            myRuneInventoryDto.SetMyRuneInventoryDto(userId, runeInventoryResponseDto.getCode(), runeInventoryResponseDto.getCount(), runeInventoryResponseDto.getLevel());
                                            myRuneInventory = myRuneInventoryRepository.save(myRuneInventoryDto.ToEntity());
                                            myRuneInventoryList.add(myRuneInventory);
                                        } else {
                                            myRuneInventory.SetRuneInventory(runeInventoryResponseDto);
                                        }
                                    }
                                }
                                break;
                            case "useUpItemInventory":
                                for (ElementDto element : container.elements) {
                                    MyBelongingInventory myBelongingInventory = myBelongingInventoryList.stream().filter(j -> j.getCode().equals(element.getElement())).findAny().orElse(null);
                                    BelongingInventoryJsonData belongingInventoryJsonData = JsonStringHerlper.ReadValueFromJson(element.getValue(), BelongingInventoryJsonData.class);
                                    if (myBelongingInventory == null) {
                                        MyBelongingInventoryDto myBelongingInventoryDto = new MyBelongingInventoryDto();
                                        myBelongingInventoryDto.SetFirstMyBelongingInventoryDto(userId, element.getElement(), belongingInventoryJsonData.getCount());
                                        myBelongingInventory = myBelongingInventoryRepository.save(myBelongingInventoryDto.ToEntity());
                                        myBelongingInventoryList.add(myBelongingInventory);
                                    } else {
                                        myBelongingInventory.SetCountAndSlotNoAndSlotPercent(belongingInventoryJsonData.getCount(), belongingInventoryJsonData.getSlot(), belongingInventoryJsonData.getSlotPercent());
                                    }
                                }
                                break;
                            case "pixieUserData":
                            case "carvingRuneUserData":
                                for (ElementDto element : container.elements) {
                                    Field field = myPixieInfoData.getClass().getDeclaredField(element.getElement());
                                    Class<?> elementType = field.getType();
                                    if(elementType.getTypeName().equals("java.lang.Long"))
                                        field.set(myPixieInfoData, Long.parseLong(element.getValue()));
                                    else if(elementType.getTypeName().equals("int"))
                                        field.set(myPixieInfoData, Integer.parseInt(element.getValue()));
                                }
                                break;
                            case "heroClassInventory":
                                for(ElementDto element : container.elements) {
                                    ClassInventoryResponseDto classInventoryResponseDto = JsonStringHerlper.ReadValueFromJson(element.getValue(), ClassInventoryResponseDto.class);
                                    MyClassInventory myClassInventory = myClassInventoryList.stream().filter(i -> i.getCode().equals(element.getElement())).findAny().orElse(null);
                                    if( myClassInventory == null) {
                                        MyClassInventoryDto myClassInventoryDto = new MyClassInventoryDto();
                                        myClassInventoryDto.SetMyClassInventoryDto(userId, classInventoryResponseDto.getCode(), classInventoryResponseDto.getLevel(), classInventoryResponseDto.getCount(), classInventoryResponseDto.getPromotionPercent(), classInventoryResponseDto.getIsPromotionLock());
                                        myClassInventory = myClassInventoryRepository.save(myClassInventoryDto.ToEntity());
                                        myClassInventoryList.add(myClassInventory);
                                    }else {
                                        myClassInventory.SetMyClassInventory(classInventoryResponseDto);
                                    }
                                }
                                break;
                            case "upgradeStatusUserData":
                                for(ElementDto element : container.elements) {
                                    Field field = myStatusInfo.getClass().getDeclaredField(element.getElement());
                                    field.set(myStatusInfo, Integer.parseInt(element.getValue()));
                                }
                                break;
                            case "equipmentInfo":
                                for (ElementDto element : container.elements) {
                                    Field j = myEquipmentInfo.getClass().getDeclaredField(element.getElement());
                                    Class<?> elementType = j.getType();
                                    if(elementType.getTypeName().equals("java.lang.Long"))
                                        j.set(myEquipmentInfo, Long.parseLong(element.getValue()));
                                    else if(elementType.getTypeName().equals("int"))
                                        j.set(myEquipmentInfo, Integer.parseInt(element.getValue()));

                                }
                                break;
                            case "skillUserDataTable":
                                ActiveSkillDataJsonDto activeSkillDataJsonDto = JsonStringHerlper.ReadValueFromJson(myActiveSkillData.getJson_saveDataValue(), ActiveSkillDataJsonDto.class);
                                for (ElementDto element : container.elements) {
                                    ActiveSkillDataJsonDto.SkillInfo skillInfo = JsonStringHerlper.ReadValueFromJson(element.getValue(), ActiveSkillDataJsonDto.SkillInfo.class);
                                    activeSkillDataJsonDto.skillInfoList.set(Integer.parseInt(element.getElement())-1, skillInfo);
                                }
                                String jsonData = JsonStringHerlper.WriteValueAsStringFromData(activeSkillDataJsonDto);
                                myActiveSkillData.ResetJson_SaveDataValue(jsonData);
                                break;
                            case "weaponInventory":
                                for(ElementDto element : container.elements) {
                                    WeaponInventoryResponseDto weaponInventoryResponseDto = JsonStringHerlper.ReadValueFromJson(element.getValue(), WeaponInventoryResponseDto.class);
                                    MyEquipmentInventory myEquipmentInventory = myEquipmentInventoryList.stream().filter(i -> i.getCode().equals(element.getElement())).findAny().orElse(null);
                                    if (myEquipmentInventory == null) {
                                        EquipmentTable equipmentTable = gameDataTableService.EquipmentTable().stream().filter(i -> i.getCode().equals(element.getElement())).findAny().orElse(null);
                                        if(equipmentTable == null) {
                                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found EquipmentTable", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                            throw new MyCustomException("Not Found EquipmentTable", ResponseErrorCode.NOT_FIND_DATA);
                                        }
                                        MyEquipmentInventoryDto myEquipmentInventoryDto = new MyEquipmentInventoryDto();
                                        myEquipmentInventoryDto.SetMyEquipmentInventoryDto(userId, element.getElement(), equipmentTable.getGrade(), weaponInventoryResponseDto.getCount(), weaponInventoryResponseDto.getLevel(), weaponInventoryResponseDto.getIsPromotionLock(), weaponInventoryResponseDto.getCarveData());
                                        myEquipmentInventory = myEquipmentInventoryRepository.save(myEquipmentInventoryDto.ToEntity());
                                        myEquipmentInventoryList.add(myEquipmentInventory);
                                    }else {
                                        myEquipmentInventory.SetMyEquipmentInventory(weaponInventoryResponseDto);
                                    }
                                }
                                break;
                            case "accessoryInventory":
                                for(ElementDto element : container.elements) {
                                    AccessoryInventoryResponseDto accessoryInventoryResponseDto = JsonStringHerlper.ReadValueFromJson(element.getValue(), AccessoryInventoryResponseDto.class);
                                    MyAccessoryInventory myAccessoryInventory = myAccessoryInventoryList.stream().filter(i -> i.getCode().equals(element.getElement())).findAny().orElse(null);
                                    if (myAccessoryInventory == null) {
                                        AccessoryTable accessoryTable = gameDataTableService.AccessoryTable().stream().filter(i -> i.getCode().equals(element.getElement())).findAny().orElse(null);
                                        if(accessoryTable == null) {
                                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found AccessoryTable", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                            throw new MyCustomException("Not Found AccessoryTable", ResponseErrorCode.NOT_FIND_DATA);
                                        }
                                        MyAccessoryInventoryDto myAccessoryInventoryDto = new MyAccessoryInventoryDto();
                                        myAccessoryInventoryDto.SetMyAccessoryInventoryDto(userId, accessoryTable.getCode(), accessoryInventoryResponseDto.getCount(), 1, accessoryInventoryResponseDto.getOptionLockList(), accessoryInventoryResponseDto.getOptions());
                                        myAccessoryInventory = myAccessoryInventoryRepository.save(myAccessoryInventoryDto.ToEntity());
                                        myAccessoryInventoryList.add(myAccessoryInventory);
                                    }else {
                                        myAccessoryInventory.SetterMyAccessoryInventory(accessoryInventoryResponseDto);
                                    }
                                }
                                break;
                            case "artifactUserDataTable":
                                for(ElementDto elementDto : container.elements) {
                                    RelicInventoryResponseDto relicInventoryResponseDto = JsonStringHerlper.ReadValueFromJson(elementDto.getValue(), RelicInventoryResponseDto.class);
                                    MyRelicInventory myRelicInventory = myRelicInventoryList.stream().filter(i -> i.getTable_id() == Integer.parseInt(elementDto.getElement())).findAny().orElse(null);
                                    if (myRelicInventory == null) {
                                        MyRelicInventoryDto myRelicInventoryDto = new MyRelicInventoryDto();
                                        myRelicInventoryDto.SetMyRelicInventoryDto(userId, Integer.parseInt(elementDto.getElement()), relicInventoryResponseDto.getCount(), relicInventoryResponseDto.getLevel());
                                        myRelicInventory = myRelicInventoryRepository.save(myRelicInventoryDto.ToEntity());
                                        myRelicInventoryList.add(myRelicInventory);
                                    }else {
                                        myRelicInventory.SetMyRelicInventoryForResponse(relicInventoryResponseDto);
                                    }
                                }
                                break;
                            case "passiveSkillUserDataTable":
                                PassiveSkillDataJsonDto passiveSkillDataJsonDto = JsonStringHerlper.ReadValueFromJson(myPassiveSkillData.getJson_saveDataValue(), PassiveSkillDataJsonDto.class);
                                for(ElementDto element : container.elements) {
                                    PassiveSkillDataJsonDto.PassiveSkillInfo passiveSkillInfo = JsonStringHerlper.ReadValueFromJson(element.getValue(), PassiveSkillDataJsonDto.PassiveSkillInfo.class);
                                    passiveSkillDataJsonDto.passiveSkillInfoList.set(Integer.parseInt(element.getElement())-1, passiveSkillInfo);
                                }
                                String passiveSkill_Json = JsonStringHerlper.WriteValueAsStringFromData(passiveSkillDataJsonDto);
                                myPassiveSkillData.ResetJson_SaveDataValue(passiveSkill_Json);
                                break;
                            case "contentsInfo":
                                for(ElementDto element : container.elements) {
                                    Field j = myContentsInfo.getClass().getDeclaredField(element.getElement());
                                    Class<?> elementType = j.getType();
                                    if(elementType.getTypeName().equals("java.lang.Long"))
                                        j.set(myContentsInfo, Long.parseLong(element.getValue()));
                                    else if(elementType.getTypeName().equals("int"))
                                        j.set(myContentsInfo, Integer.parseInt(element.getValue()));
                                }
                                break;
                            case "dungeonInfo":
                                for(ElementDto element : container.elements) {
                                    Field field = myDungeonInfo.getClass().getDeclaredField(element.getElement());
                                    Class<?> elementType = field.getType();
                                    if(elementType.getTypeName().equals("int"))
                                        field.set(myDungeonInfo, Integer.parseInt(element.getValue()));
                                    else if(elementType.getTypeName().equals("java.time.LocalDateTime"))
                                        field.set(myDungeonInfo, LocalDateTime.parse(element.getValue()));
                                }
                                break;
                            case "gachaInfo":
                                for(ElementDto element : container.elements) {
                                    Field field = myGachaInfo.getClass().getDeclaredField(element.getElement());
                                    Class<?> elementType = field.getType();
                                    if(elementType.getTypeName().equals("int"))
                                        field.set(myGachaInfo, Integer.parseInt(element.getValue()));
                                }
                                break;
                            case "collectionInfo":
                                for(ElementDto element : container.elements) {
                                    Field field = myCollectionInfo.getClass().getDeclaredField(element.getElement());
                                    field.set(myCollectionInfo, element.getValue());
                                }
                                break;
                            case "quickMissionInfo":
                                for(ElementDto element : container.elements) {
                                    Field field = myQuickMissionData.getClass().getDeclaredField(element.getElement());
                                    field.set(myQuickMissionData, element.getValue());
                                }
                                break;
                            case "battlePower":
                                for (ElementDto element : container.elements) {
                                    if (element.getElement().equals("battlePower")){
                                        user.SetBattlePower(element.getValue());
                                        LocalDateTime now = LocalDateTime.now();
                                        LocalTime start = LocalTime.of(0,0,0);
                                        LocalTime end = LocalTime.of(0, 10, 0);
                                        int dayOfWeekNumber = now.getDayOfWeek().getValue();
                                        if (!((now.toLocalTime().isAfter(start) && now.toLocalTime().isBefore(end)) && dayOfWeekNumber == 1)) {
                                            battlePowerLeaderboardService.setScore(userId, Double.parseDouble(element.getValue()));
                                        }
                                    }
                                }
                                break;
                            case "passInfo":
                                for (ElementDto element : container.elements) {
                                    if (element.getElement().contains("json"))
                                        continue;
                                    Field field = myPassData.getClass().getDeclaredField(element.getElement());
                                    Class<?> elementType = field.getType();
                                    if (elementType.getTypeName().equals("java.time.LocalDateTime"))
                                        field.set(myPassData, LocalDateTime.parse(element.getValue()));
                                    else if (elementType.getTypeName().equals("java.lang.Long"))
                                        field.set(myPassData, Long.parseLong(element.getValue()));
                                    else
                                        field.set(myPassData, element.getValue());
                                }
                                break;
                            case "missionInfo":
                                for (ElementDto element : container.elements) {
                                    Field field = myMissionInfo.getClass().getDeclaredField(element.getElement());
                                    field.set(myMissionInfo, element.getValue());
                                }
                                break;
                            case "chatBlockInfo":
                                for (ElementDto element : container.elements) {
                                    Field field = myChatBlockInfo.getClass().getDeclaredField(element.getElement());
                                    field.set(myChatBlockInfo, element.getValue());
                                }
                                break;
                            case "costumeInventory":
                                for (ElementDto element : container.elements) {
                                    CostumeInventoryResponseDto costumeInventoryResponseDto = JsonStringHerlper.ReadValueFromJson(element.getValue(), CostumeInventoryResponseDto.class);
                                    MyCostumeInventory myCostumeInventory = myCostumeInventoryList.stream().filter(i -> i.getCode().equals(element.getElement())).findAny().orElse(null);
                                    if (myCostumeInventory == null) {
                                        MyCostumeInventoryDto myCostumeInventoryDto = new MyCostumeInventoryDto();
                                        myCostumeInventoryDto.SetMyCostumeInventoryDto(userId, costumeInventoryResponseDto.getCode());
                                        myCostumeInventory = myCostumeInventoryRepository.save(myCostumeInventoryDto.ToEntity());
                                        myCostumeInventoryList.add(myCostumeInventory);
                                    }
                                }
                                break;
                            case "booster":
                                for (ElementDto element : container.elements) {
                                    Field field = myBoosterInfo.getClass().getDeclaredField(element.getElement());
                                    field.set(myBoosterInfo, element.getValue());
                                }
                                break;
                        }
                    }
                    user.SetLastSettingTime();
                    break;
            }
        }

        boolean day = false;
        boolean week = false;
        boolean month = false;
        if (!standardTime.getBaseDayTime().isEqual(user.getLastDayResetTime())) {
            ResetArenaForDay(userId);//유저
            ResetDayPass(userId);//유저
            ResetMyGachaInfo(userId);//유저
            ResetWorldBossPlayable(userId);
            day = true;
            user.SetLastDayResetTime(standardTime.getBaseDayTime());
        }
        if (!standardTime.getBaseWeekTime().isEqual(user.getLastWeekResetTime())) {
            week = true;
            ResetChallengeTower(userId);
            user.SetLastWeekResetTime(standardTime.getBaseWeekTime());
        }
        if (!standardTime.getBaseMonthTime().isEqual(user.getLastMonthResetTime())) {
            month = true;
            user.SetLastMonthResetTime(standardTime.getBaseMonthTime());
        }

        if (day || week || month)
            ResetShopPurchaseCount(userId, day, week, month);
        if (myPassData == null) {
            myPassData = myPassDataRepository.findByUseridUser(userId).orElse(null);
            if(myPassData == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyPassData", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Not Found MyPassData", ResponseErrorCode.NOT_FIND_DATA);
            }
        }
        String json_saveData = myPassData.getJson_attendanceSaveData();
        MyAttendanceDataJsonDto myAttendanceDataJsonDto = JsonStringHerlper.ReadValueFromJson(json_saveData, MyAttendanceDataJsonDto.class);
        if (CheckAttendance(myPassData, myAttendanceDataJsonDto)) {
            //TODO 남은 보상 메일로 보상처리 보상 테이블 확인 필요
            Map<String, Object> tempMap = new HashMap<>();
            LocalDateTime now = LocalDateTime.now();
            List<AttendanceFreePassTable> attendanceFreePassTableList = gameDataTableService.AttendanceFreePassTable();
            List<AttendanceBuyPassTable> attendanceBuyPassTableList = gameDataTableService.AttendanceBuyPassTable();
            for (int i = 0; i <31; i++) {
                if (!myAttendanceDataJsonDto.getRewardList().get(i)) {
                    AttendanceFreePassTable attendanceFreePassTable = attendanceFreePassTableList.get(i);
                    MailSendRequestDto mailSendRequestDto = new MailSendRequestDto();
                    mailSendRequestDto.setToId(userId);
                    mailSendRequestDto.setSendDate(now);
                    mailSendRequestDto.setMailType(0);
                    mailSendRequestDto.setExpireDate(now.plusDays(30));
                    mailSendRequestDto.setTitle("미획득 출석보상 지급");
                    mailSendRequestDto.setGettingItem(attendanceFreePassTable.getRewardType()); //TODO 보상 테이블에 있는 보상으로 지급
                    mailSendRequestDto.setGettingItemCount(attendanceFreePassTable.getRewardCount());
                    myMailBoxService.SendMail(mailSendRequestDto, tempMap);
                }
                if (myAttendanceDataJsonDto.isPassPurchase()) {
                    if (!myAttendanceDataJsonDto.getPassRewardList().get(i)) {
                        AttendanceBuyPassTable attendanceBuyPassTable = attendanceBuyPassTableList.get(i);
                        MailSendRequestDto mailSendRequestDto = new MailSendRequestDto();
                        mailSendRequestDto.setToId(userId);
                        mailSendRequestDto.setSendDate(now);
                        mailSendRequestDto.setMailType(0);
                        mailSendRequestDto.setExpireDate(now.plusDays(30));
                        mailSendRequestDto.setTitle("미획득 구매 출석보상 지급");
                        mailSendRequestDto.setGettingItem(attendanceBuyPassTable.getRewardType()); //TODO 보상 테이블에 있는 보상으로 지급
                        mailSendRequestDto.setGettingItemCount(attendanceBuyPassTable.getRewardCount());
                        myMailBoxService.SendMail(mailSendRequestDto, tempMap);
                    }
                }
            }
            myAttendanceDataJsonDto.Init();
        }
        json_saveData = JsonStringHerlper.WriteValueAsStringFromData(myAttendanceDataJsonDto);
        myPassData.ResetAttendanceJsonData(json_saveData);

        map.put("cmdRequest", requestList.cmds);
        map.put("lastSettingTime", user.getLastSettingTime());
        return map;
    }

    public Map<String, Object> GetLastResetTime(Long userId, Map<String, Object> map) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found User", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Not Found User", ResponseErrorCode.NOT_FIND_DATA);
        }
        map.put("lastDayResetTime",user.getLastDayResetTime());
        map.put("lastWeekResetTime",user.getLastWeekResetTime());
        map.put("lastMonthResetTime",user.getLastMonthResetTime());
        return map;
    }

    private void ResetArenaForDay(Long userId) {
        MyArenaPlayData myArenaPlayData = myArenaPlayDataRepository.findByUseridUser(userId).orElse(null);
        if (myArenaPlayData == null)
            return;
        myArenaPlayData.ResetReMatchingAbleCount();
        myArenaPlayData.ResetPlayableCount();

    }

    private void ResetDayPass(Long userId) {
        MyPassData myPassData = myPassDataRepository.findByUseridUser(userId).orElse(null);
        if (myPassData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyPassData Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyPassData Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyDayRewardDataJsonDto myDayRewardDataJsonDto = JsonStringHerlper.ReadValueFromJson(myPassData.getJson_daySaveData(), MyDayRewardDataJsonDto.class);
        myDayRewardDataJsonDto.Init();
        String json_day = JsonStringHerlper.WriteValueAsStringFromData(myDayRewardDataJsonDto);
        myPassData.ResetDayJsonData(json_day);
        myPassData.SetGettingCount();

    }

    private void ResetMyGachaInfo(Long userId) {
        MyGachaInfo myGachaInfo = myGachaInfoRepository.findByUseridUser(userId).orElse(null);
        if (myGachaInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyGachaInfo Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyGachaInfo Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        myGachaInfo.ResetADCount();

    }

    private void ResetChallengeTower(Long userId) {
        MyContentsInfo myContentsInfo = myContentsInfoRepository.findByUseridUser(userId).orElse(null);
        if (myContentsInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyContentsInfo Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyContentsInfo Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        if (myContentsInfo.getChallengeTowerFloor() != 0)
            myContentsInfo.SetChallengeTowerFloor(0);
    }

    private void ResetShopPurchaseCount(Long userId, boolean day, boolean week, boolean month) {
        MyShopInfo myShopInfo = myShopInfoRepository.findByUseridUser(userId).orElse(null);

        if (day)
            myShopInfo.RechargeDay();
        if (week)
            myShopInfo.RechargeWeek();
        if (month)
            myShopInfo.RechargeMonth();

    }

    private void ResetWorldBossPlayable(Long userId) {
        MyWorldBossPlayData myWorldBossPlayData = myWorldBossPlayDataRepository.findByUseridUser(userId).orElse(null);
        if (myWorldBossPlayData == null)
            return;
        myWorldBossPlayData.ResetPlayableCount();
    }

    /**
     * @return  boolean 값을 return true : 모든 아이템을 획득하도록 획득 서비스를 실행해야함
     */
    private boolean CheckAttendance(MyPassData myPassData, MyAttendanceDataJsonDto dto) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(myPassData.getLastAttendanceDate(), now);
        if(duration.toDays()>=1) {
            myPassData.ResetLastAttendanceDate(LocalDateTime.of(now.toLocalDate(), LocalTime.of(0, 0, 0)));
            return PlusCount(dto);
        }
        return false;
    }

    private boolean PlusCount(MyAttendanceDataJsonDto dto) {
        if(dto.gettingCount == 31) {
            dto.gettingCount = 1;
            return true;
        }
        else {
            dto.gettingCount += 1;
            return false;
        }
    }
}
