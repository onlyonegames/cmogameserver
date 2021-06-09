package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.*;
import com.onlyonegames.eternalfantasia.domain.model.dto.Inventory.MyClassInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Inventory.MyEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Inventory.MyRuneInventoryDto;
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

    public Map<String, Object> Getter(Long userId, Map<String,List<String>> requestList, Map<String, Object> map){
        MyPixieInfoData myPixieInfoData = null;
        MyRuneLevelInfoData myRuneLevelInfoData = null;
        User user = null;
        Iterator keys = requestList.keySet().iterator();
        while(keys.hasNext()) {
            String key = (String) keys.next();
            switch (key){
                case "MyPixieInfoData":
                    if(myPixieInfoData == null)
                        myPixieInfoData = myPixieInfoDataRepository.findByUseridUser(userId).orElse(null);

                    break;
                case "PlayerInfo":
                    for(String i : requestList.get(key)){
                        switch(i){
                            case "gold":
                            case "diamond":
                            case "soulStone":
                            case "skillPoint":
                            case "moveStone":
                                if(user == null) {
                                    user = userRepository.findById(userId).orElse(null);
                                    if(user == null){

                                    }
                                }
                                break;

                        }
                    }
                    break;
                case "UserInfo":
                    for(String i : requestList.get(key)) {
                        switch (i) {
                            case "userGameName":
                            case "level":
                            case "exp":
                            case "sexType":
                        }
                    }

            }
        }
        keys = requestList.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            switch(key) {
                case "PlayerInfo":
                    Map<String, Object> playerInfoContainer = new HashMap<>();
                    for(String temp : requestList.get(key)){
                        switch (temp){
                            case "gold":
                                String gold = user.getGold();
                                playerInfoContainer.put("gold", gold);
                                break;
                            case "diamond":
                                int diamond = user.getDiamond();
                                playerInfoContainer.put("diamond", diamond);
                                break;
                            case "soulStone":
                                String soulStone = user.getSoulStone();
                                playerInfoContainer.put("soulStone", soulStone);
                                break;
                        }
                    }
                    map.put("PlayerInfo", playerInfoContainer);
                    break;
                case "UserInfo":
                    Map<String, Object> userInfoContainer = new HashMap<>();
                    for(String temp : requestList.get(key)) {
                        switch (temp) {
                            case "userGameName":
                                String userGameName = user.getUserGameName();
                        }
                    }
                    break;
//                case "MyPixieInfoData":
//                    switch ()
//                    MyPixieInfoData myPixieInfoData = myPixieInfoDataRepository.findByUseridUser(userId).orElse(null);
//                    if(myPixieInfoData == null) {
//                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyPixieInfoData", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
//                        throw new MyCustomException("Not Found MyPixieInfoData", ResponseErrorCode.NOT_FIND_DATA);
//                    }
//                    MyPixieInfoDataDto myPixieInfoDataDto = new MyPixieInfoDataDto();
//                    myPixieInfoDataDto.InitFromDBData(myPixieInfoData);
//                    map.put("myPixieInfoData", myPixieInfoDataDto);
//                    break;
//                case "MyRuneLevelInfoData":
//                    MyRuneLevelInfoData myRuneLevelInfoData = myRuneLevelInfoDataRepository.findByUseridUser(userId).orElse(null);
//                    if(myRuneLevelInfoData == null) {
//                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyRuneLevelInfoData", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
//                        throw new MyCustomException("Not Found MyRuneLevelInfoData", ResponseErrorCode.NOT_FIND_DATA);
//                    }
//                    MyRuneLevelInfoDataDto myRuneLevelInfoDataDto = new MyRuneLevelInfoDataDto();
//                    myRuneLevelInfoDataDto.InitFromDBData(myRuneLevelInfoData);
//                    map.put("myRuneLevelInfoData", myRuneLevelInfoDataDto);
//                    break;
//                case "MyStatusInfo":
//                    MyStatusInfo myStatusInfo = myStatusInfoRepository.findByUseridUser(userId).orElse(null);
//                    if(myStatusInfo == null) {
//                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyStatusInfo", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
//                        throw new MyCustomException("Not Found MyStatusInfo", ResponseErrorCode.NOT_FIND_DATA);
//                    }
//                    MyStatusInfoDto myStatusInfoDto = new MyStatusInfoDto();
//                    myStatusInfoDto.InitFormDBData(myStatusInfo);
//                    map.put("myStatusInfo", myStatusInfoDto);
//                    break;
//                case "User":
//                    User user = userRepository.findById(userId).orElse(null);
//                    if(user == null) {
//                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found User", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
//                        throw new MyCustomException("Not Found User", ResponseErrorCode.NOT_FIND_DATA);
//                    }
//                    map.put("user", user);
//                    break;
//                case "myActiveSkillData":
//                    MyActiveSkillData myActiveSkillData = myActiveSkillDataRepository.findByUseridUser(userId).orElse(null);
//                    if(myActiveSkillData == null) {
//                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyActiveSkillData", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
//                        throw new MyCustomException("Not Found MyActiveSkillData", ResponseErrorCode.NOT_FIND_DATA);
//                    }
//                    String json_saveData = myActiveSkillData.getJson_saveDataValue();
//                    ActiveSkillDataJsonDto activeSkillDataJsonDto = JsonStringHerlper.ReadValueFromJson(json_saveData, ActiveSkillDataJsonDto.class);
//                    map.put("myActiveSkillData", activeSkillDataJsonDto);
//                    break;
//                case "myClassInventory":
//                    List<MyClassInventory> myClassInventoryList = myClassInventoryRepository.findAllByUseridUser(userId);
//                    List<MyClassInventoryDto> myClassInventoryDtoList = new ArrayList<>();
//                    for(MyClassInventory temp : myClassInventoryList) {
//                        MyClassInventoryDto myClassInventoryDto = new MyClassInventoryDto();
//                        myClassInventoryDto.InitFromDBData(temp);
//                        myClassInventoryDtoList.add(myClassInventoryDto);
//                    }
//                    map.put("myClassInventory", myClassInventoryDtoList);
//                    break;
//                case "myEquipmentInventory":
//                    List<MyEquipmentInventory> myEquipmentInventoryList = myEquipmentInventoryRepository.findALLByUseridUser(userId);
//                    List<MyEquipmentInventoryDto> myEquipmentInventoryDtoList = new ArrayList<>();
//                    for(MyEquipmentInventory temp : myEquipmentInventoryList) {
//                        MyEquipmentInventoryDto myEquipmentInventoryDto = new MyEquipmentInventoryDto();
//                        myEquipmentInventoryDto.InitFromDBData(temp);
//                        myEquipmentInventoryDtoList.add(myEquipmentInventoryDto);
//                    }
//                    map.put("myEquipmentInventory", myEquipmentInventoryDtoList);
//                    break;
//                case "myRuneInventory":
//                    List<MyRuneInventory> myRuneInventoryList = myRuneInventoryRepository.findAllByUseridUser(userId);
//                    List<MyRuneInventoryDto> myRuneInventoryDtoList = new ArrayList<>();
//                    for(MyRuneInventory temp : myRuneInventoryList) {
//                        MyRuneInventoryDto myRuneInventoryDto = new MyRuneInventoryDto();
//                        myRuneInventoryDto.InitFromDBData(temp);
//                        myRuneInventoryDtoList.add(myRuneInventoryDto);
//                    }
//                    map.put("myRuneInventory", myRuneInventoryDtoList);
//                    break;
            }
        }
        return map;
    }
}
