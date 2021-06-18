package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.*;
import com.onlyonegames.eternalfantasia.domain.model.dto.Inventory.MyBelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Inventory.MyClassInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Inventory.MyEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Inventory.MyRuneInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.CommandDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ContainerDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ElementDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.RequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.BelongingInventoryJsonData;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.CarvingRuneUserData;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.PixieUserDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.RuneInventoryResponseDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.*;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyBelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyClassInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyRuneInventory;
import com.onlyonegames.eternalfantasia.domain.repository.*;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyBelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyClassInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyRuneInventoryRepository;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
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

    public Map<String, Object> Getter(Long userId, RequestDto requestList, Map<String, Object> map) throws IllegalAccessException, NoSuchFieldException {
        MyPixieInfoData myPixieInfoData = null;
        MyRuneLevelInfoData myRuneLevelInfoData = null;
        List<MyRuneInventory> myRuneInventoryList = null;
        MyActiveSkillData myActiveSkillData = null;
        List<MyClassInventory> myClassInventoryList = null;
        List<MyEquipmentInventory> myEquipmentInventoryList = null;
        User user = null;
        List<MyBelongingInventory> myBelongingInventoryList = null;
        MyEquipmentInfo myEquipmentInfo = null;

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
                                    if (user == null) {
                                        user = userRepository.findById(userId).orElse(null);
                                        if (user == null) {
                                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found User", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                            throw new MyCustomException("Not Found User", ResponseErrorCode.NOT_FIND_DATA);
                                        }
                                    }
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
                                case "pixieUserData":
                                    if (myPixieInfoData == null) {
                                        myPixieInfoData = myPixieInfoDataRepository.findByUseridUser(userId).orElse(null);
                                        if (myPixieInfoData == null) {
                                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyPixieInfoData", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                            throw new MyCustomException("Not Found MyPixieInfoData", ResponseErrorCode.NOT_FIND_DATA);
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
                                    if (user == null) {
                                        user = userRepository.findById(userId).orElse(null);
                                        if (user == null) {
                                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found User", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                            throw new MyCustomException("Not Found User", ResponseErrorCode.NOT_FIND_DATA);
                                        }
                                    }
                                    break;
                                case "skillUserDataTable":
                                    if (myActiveSkillData == null) {
                                        myActiveSkillData = myActiveSkillDataRepository.findByUseridUser(userId).orElse(null);
                                        if (myActiveSkillData == null) {
                                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyActiveSkillData", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                            throw new MyCustomException("Not Found MyActiveSkillData", ResponseErrorCode.NOT_FIND_DATA);
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
                                        if (myPixieInfoData == null) {
                                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyClassInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                            throw new MyCustomException("Not Found MyClassInventory", ResponseErrorCode.NOT_FIND_DATA);
                                        }
                                    }
                                    break;
                                case "weaponInventory":
                                    if (myEquipmentInventoryList == null) {
                                        myEquipmentInventoryList = myEquipmentInventoryRepository.findALLByUseridUser(userId);
                                        if (myEquipmentInventoryList == null) {
                                            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyEquipmentInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                            throw new MyCustomException("Not Found MyEquipmentInventory", ResponseErrorCode.NOT_FIND_DATA);
                                        }
                                    }
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
                }
            }
        }

        for (CommandDto cmd : requestList.cmds) {
            switch (cmd.cmd) {
                case "get":
                    for (ContainerDto i : cmd.containers) {
                        switch (i.getContainer()) {
                            case "PlayerInfo":
                                for (ElementDto temp : i.elements) {
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
                                for (ElementDto temp : i.elements) {
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
                            case "runeInventory":
                                List<ElementDto> runeInventoryElementDtoList = new ArrayList<>();
                                boolean flag = false;
                                for (ElementDto temp : i.elements) {
                                    if(temp.getElement().equals("all")) {
                                        for(MyRuneInventory j : myRuneInventoryList){
                                            ElementDto inventory = new ElementDto();
                                            inventory.SetElement(Integer.toString(j.getType_Id()), Integer.toString(j.getCount()));
                                            runeInventoryElementDtoList.add(inventory);
                                        }
                                        flag = true;
                                        break;
                                    }
                                    MyRuneInventory myRuneInventory = myRuneInventoryList.stream().filter(j -> j.getType_Id() == Integer.parseInt(temp.getElement())).findAny().orElse(null);
                                    if (myRuneInventory == null) {
                                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyRuneInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                                        throw new MyCustomException("Not Found MyRuneInventory", ResponseErrorCode.NOT_FIND_DATA);
                                    }
                                    temp.SetValue(myRuneInventory.getCount());
                                }
                                if(flag){
                                    i.elements = runeInventoryElementDtoList;
                                }
                                break;
                            case "useUpItemInventory":
                                List<ElementDto> elementDtoList = new ArrayList<>();
                                boolean itemFlag = false;
                                for (ElementDto temp : i.elements) {
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

                                    }
                                    BelongingInventoryJsonData belongingInventoryJsonData = new BelongingInventoryJsonData();
                                    belongingInventoryJsonData.SetBelongingInventoryJsonData(myBelongingInventory.getCount(), myBelongingInventory.getSlotNo(), myBelongingInventory.getSlotPercent());
                                    String jsonData = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryJsonData);
                                    temp.SetValue(jsonData);
                                }
                                if(itemFlag) {
                                    i.elements = elementDtoList;
                                }
                                break;
                            case "equipmentInfo":
                                List<ElementDto> equipmentElementDtoList = new ArrayList<>();
                                boolean equipmentFlag = false;
                                for(ElementDto temp : i.elements) {
                                    if(temp.getElement().equals("all")) {
                                        for(Field j : myEquipmentInfo.getClass().getDeclaredFields()) {
                                            ElementDto inventory = new ElementDto();
                                            String name = j.getName();
                                            if(name.equals("id") || name.equals("useridUser") || name.equals("createdate") || name.equals("modifieddate"))
                                                continue;
                                            inventory.SetElement(j.getName(), j.get(myEquipmentInfo).toString());
                                            equipmentElementDtoList.add(inventory);
                                        }
                                        equipmentFlag = true;
                                        break;
                                    }
                                    Field j = myEquipmentInfo.getClass().getDeclaredField(temp.getElement());
                                    temp.SetValue(j.get(myEquipmentInfo).toString());
                                }
                                if(equipmentFlag) {
                                    i.elements = equipmentElementDtoList;
                                }
                                break;
                        }
                    }
                    break;
                case "set":
                    for (ContainerDto i : cmd.containers) {
                        switch (i.container) {
                            case "PlayerInfo":
                                for (ElementDto temp : i.elements) {
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
                                for (ElementDto temp : i.elements) {
                                    switch (temp.getElement()) {
                                        case "level":
                                            user.SetLevel(temp.getValue());
                                            break;
                                        case "exp":
                                            user.SetExp(temp.getValue());
                                            break;
                                    }
                                }
                                break;
                            case "runeInventory":
                                for (ElementDto temp : i.elements) {
                                    MyRuneInventory myRuneInventory = myRuneInventoryList.stream().filter(j -> j.getType_Id() == Integer.parseInt(temp.getElement())).findAny().orElse(null);
                                    if (myRuneInventory == null) {
                                        MyRuneInventoryDto myRuneInventoryDto = new MyRuneInventoryDto();
                                        myRuneInventoryDto.SetMyRuneInventoryDto(userId, temp.getElement(), temp.getValue());
                                        myRuneInventory = myRuneInventoryRepository.save(myRuneInventoryDto.ToEntity());
                                        myRuneInventoryList.add(myRuneInventory);
                                    } else {
                                        myRuneInventory.SetCount(temp.getValue());
                                    }
                                }
                                break;
                            case "useUpItemInventory":
                                for (ElementDto temp : i.elements) {
                                    MyBelongingInventory myBelongingInventory = myBelongingInventoryList.stream().filter(j -> j.getCode().equals(temp.getElement())).findAny().orElse(null);
                                    BelongingInventoryJsonData belongingInventoryJsonData = JsonStringHerlper.ReadValueFromJson(temp.getValue(), BelongingInventoryJsonData.class);
                                    if (myBelongingInventory == null) {
                                        MyBelongingInventoryDto myBelongingInventoryDto = new MyBelongingInventoryDto();
                                        myBelongingInventoryDto.SetFirstMyBelongingInventoryDto(userId, temp.getElement(), belongingInventoryJsonData.getCount());
                                        myBelongingInventory = myBelongingInventoryRepository.save(myBelongingInventoryDto.ToEntity());
                                        myBelongingInventoryList.add(myBelongingInventory);
                                    } else {
                                        myBelongingInventory.SetCountAndSlotNoAndSlotPercent(belongingInventoryJsonData.getCount(), belongingInventoryJsonData.getSlot(), belongingInventoryJsonData.getSlotPercent());
                                    }
                                }
                                break;
                            case "equipmentInfo":
                                for (ElementDto temp : i.elements) {
                                    Field j = myEquipmentInfo.getClass().getDeclaredField(temp.getElement());
                                    Class<?> elementType = j.getType();
                                    if(elementType.getTypeName().equals("java.long.Long"))
                                        j.set(myEquipmentInfo, Long.parseLong(temp.getValue()));
                                    else if(elementType.getTypeName().equals("int"))
                                        j.set(myEquipmentInfo, Integer.parseInt(temp.getValue()));

                                }
                                break;
                        }
                    }
                    break;
            }
        }
        map.put("cmdRequest", requestList.cmds);
        return map;
    }
}
