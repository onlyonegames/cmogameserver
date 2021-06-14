package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.*;
import com.onlyonegames.eternalfantasia.domain.model.dto.Inventory.MyClassInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Inventory.MyEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Inventory.MyRuneInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.CommandDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ContainerDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ElementDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.RequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.CarvingRuneUserData;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.PixieUserDataDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.*;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyClassInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyRuneInventory;
import com.onlyonegames.eternalfantasia.domain.repository.*;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyClassInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyRuneInventoryRepository;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Map<String, Object> Getter(Long userId, RequestDto requestList, Map<String, Object> map){
        MyPixieInfoData myPixieInfoData = null;
        MyRuneLevelInfoData myRuneLevelInfoData = null;
        List<MyRuneInventory> myRuneInventoryList = null;
        MyActiveSkillData myActiveSkillData = null;
        List<MyClassInventory> myClassInventoryList = null;
        List<MyEquipmentInventory> myEquipmentInventoryList = null;
        User user = null;
        for (CommandDto cmd : requestList.cmds) {
            for(ContainerDto containerDto : cmd.containers) {
                switch (containerDto.container) {
                    case "MyPixieInfoData":
                        if (myPixieInfoData == null)
                            myPixieInfoData = myPixieInfoDataRepository.findByUseridUser(userId).orElse(null);

                        break;
                    case "PlayerInfo":
                        for (ElementDto i : containerDto.Element) {
                            switch (i.getElement()) {
                                case "gold":
                                case "diamond":
                                case "soulStone":
                                case "skillPoint":
                                case "moveStone":
                                    if (user == null) {
                                        user = userRepository.findById(userId).orElse(null);
                                        if (user == null) {

                                        }
                                    }
                                    break;
                                case "runeLevel":
                                    if (myRuneLevelInfoData == null) {
                                        myRuneLevelInfoData = myRuneLevelInfoDataRepository.findByUseridUser(userId).orElse(null);
                                        if (myRuneLevelInfoData == null) {

                                        }
                                    }
                                    break;
                                case "runeInventory":
                                    if (myRuneInventoryList == null) {
                                        myRuneInventoryList = myRuneInventoryRepository.findAllByUseridUser(userId);
                                        if (myRuneInventoryList == null) {

                                        }
                                    }
                                    break;
                                case "pixieUserData":
                                    if (myPixieInfoData == null) {
                                        myPixieInfoData = myPixieInfoDataRepository.findByUseridUser(userId).orElse(null);
                                        if (myPixieInfoData == null) {

                                        }
                                    }
                                    break;
                            }
                        }
                        break;
                    case "UserInfo":
                        for (ElementDto i : containerDto.Element) {
                            switch (i.getElement()) {
                                case "userGameName":
                                case "level":
                                case "exp":
                                case "sexType":
                                    if (user == null) {
                                        user = userRepository.findById(userId).orElse(null);
                                        if (user == null) {

                                        }
                                    }
                                    break;
                                case "skillUserDataTable":
                                    if (myActiveSkillData == null) {
                                        myActiveSkillData = myActiveSkillDataRepository.findByUseridUser(userId).orElse(null);
                                        if (myActiveSkillData == null) {

                                        }
                                    }
                                    break;
                                case "pixieUserData":
                                case "carvingRuneUserData":
                                    if (myPixieInfoData == null) {
                                        myPixieInfoData = myPixieInfoDataRepository.findByUseridUser(userId).orElse(null);
                                        if (myPixieInfoData == null) {

                                        }
                                    }
                                    break;
                                case "heroClassInventory":
                                    if (myClassInventoryList == null) {
                                        myClassInventoryList = myClassInventoryRepository.findAllByUseridUser(userId);
                                        if (myPixieInfoData == null) {

                                        }
                                    }
                                    break;
                                case "weaponInventory":
                                    if (myEquipmentInventoryList == null) {
                                        myEquipmentInventoryList = myEquipmentInventoryRepository.findALLByUseridUser(userId);
                                        if (myEquipmentInventoryList == null) {

                                        }
                                    }
                                    break;
                            }
                        }
                        break;
                }
            }
        }
        for (CommandDto cmd : requestList.cmds) {
            switch (cmd.cmd) {
                case "get":
                    for (ContainerDto i : cmd.containers) {
                        switch (i.getContainer()) {
                            case "PlayerInfo":
                                for (ElementDto temp : i.Element) {
                                    switch (temp.getElement()) {
                                        case "gold":
                                            temp.SetValue(user.getGold());
                                            break;
                                        case "diamond":
                                            temp.SetValue(user.getDiamond());
                                            break;
                                        case "soulStone":
                                            temp.SetValue(user.getSoulStone());
                                            break;
                                        case "skillPoint":
                                            temp.SetValue(user.getSkillPoint());
                                            break;
                                        case "moveStone":
                                            temp.SetValue(user.getMoveStone());
                                            break;
                                        case "runeLevel":
                                            temp.SetValue(myRuneLevelInfoData.getLevel());
                                            break;
                                    }
                                }
                                break;
                            case "UserInfo":
                                for (ElementDto temp : i.Element) {
                                    switch (temp.getElement()) {
                                        case "userGameName":
                                            temp.SetValue(user.getUserGameName());
                                            break;
                                        case "level":
                                            temp.SetValue(user.getLevel());
                                            break;
                                        case "exp":
                                            temp.SetValue(user.getExp());
                                            break;
                                        case "sexType":
                                            temp.SetValue(user.getSexType());
                                            break;
                                        case "skillUserDataTable":
                                            temp.setElement(myActiveSkillData.getJson_saveDataValue());
                                            break;
                                        case "pixieUserData":
                                            PixieUserDataDto pixieUserDataDto = new PixieUserDataDto();
                                            pixieUserDataDto.SetPixieUserDataDto(myPixieInfoData);
                                            break;
                                        case "carvingRuneUserData":
                                            CarvingRuneUserData carvingRuneUserData = new CarvingRuneUserData();
                                            carvingRuneUserData.SetCarvingRuneUserData(myPixieInfoData);
                                            break;
                                        case "heroClassInventory":
                                            List<MyClassInventoryDto> myClassInventoryDtoList = new ArrayList<>();
                                            for (MyClassInventory j : myClassInventoryList) {
                                                MyClassInventoryDto myClassInventoryDto = new MyClassInventoryDto();
                                                myClassInventoryDto.InitFromDBData(j);
                                                myClassInventoryDtoList.add(myClassInventoryDto);
                                            }
                                            String listJson = JsonStringHerlper.WriteValueAsStringFromData(myClassInventoryDtoList);
                                            temp.SetValue(listJson);
                                            break;
                                        case "weaponInventory":
                                            List<MyEquipmentInventoryDto> myEquipmentInventoryDtoList = new ArrayList<>();
                                            for (MyEquipmentInventory j : myEquipmentInventoryList) {
                                                MyEquipmentInventoryDto myEquipmentInventoryDto = new MyEquipmentInventoryDto();
                                                myEquipmentInventoryDto.InitFromDBData(j);
                                                myEquipmentInventoryDtoList.add(myEquipmentInventoryDto);
                                            }
                                            break;
                                    }
                                }
                                break;
                        }
                    }
                    break;
                case "set":
                    for (ContainerDto i : cmd.containers) {
                        switch (i.container) {
                            case "PlayerInfo":
                                for (ElementDto temp : i.Element) {
                                    switch (temp.getElement()) {
                                        case "gold":
                                            user.SetGold(temp.getValue());
                                            break;
                                        case "diamond":
                                            user.SetDiamond(temp.getValue());
                                            break;
                                        case "soulStone":
                                            user.SetSoulStone(temp.getValue());
                                            break;
                                        case "skillPoint":
                                            user.SetSkillPoint(temp.getValue());
                                            break;
                                        case "moveStone":
                                            user.SetMoveStone(temp.getValue());
                                            break;
                                        case "runeLevel":
                                            myRuneLevelInfoData.SetLevel(temp.getValue());
                                            break;
                                    }
                                }
                                break;
                            case "UserInfo":

                        }
                    }
                    break;
            }
        }
        map.put("cmdRequest", requestList.cmds);
        return map;
    }
}
