package com.onlyonegames.eternalfantasia.domain.service.Inventory;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Inventory.MyEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.GettingEquipmentItemRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.EquipmentTable;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.util.MathHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyEquipmentService {
    private final MyEquipmentInventoryRepository myEquipmentInventoryRepository;
    private final UserRepository userRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> GetInventoryInfo(Long userId, Map<String, Object> map) {
        List<MyEquipmentInventory> myEquipmentInventoryList = myEquipmentInventoryRepository.findAllByUseridUser(userId);
        List<MyEquipmentInventoryDto> myEquipmentInventoryDtoList = new ArrayList<>();
        for(MyEquipmentInventory temp : myEquipmentInventoryList){
            MyEquipmentInventoryDto myEquipmentInventoryDto = new MyEquipmentInventoryDto();
            myEquipmentInventoryDto.InitFromDBData(temp);
            myEquipmentInventoryDtoList.add(myEquipmentInventoryDto);
        }
        map.put("myEquipmentInventoryList", myEquipmentInventoryDtoList);
        return map;
    }

    public Map<String, Object> GettingEquipmentItem(Long userId, GettingEquipmentItemRequestDto dto, Map<String, Object> map) {
        List<MyEquipmentInventory> myEquipmentInventoryList = myEquipmentInventoryRepository.findAllByUseridUser(userId);
        List<EquipmentTable> equipmentTableList = gameDataTableService.EquipmentTable();
        for(GettingEquipmentItemRequestDto.GettingEquipmentItemInfo temp : dto.gettingEquipmentItemInfoList){
            MyEquipmentInventory myEquipmentInventory = myEquipmentInventoryList.stream().filter(i -> i.getCode().equals(temp.code)).findAny().orElse(null);
            if(myEquipmentInventory == null) {
                EquipmentTable equipmentTable = equipmentTableList.stream().filter(i -> i.getCode().equals(temp.code)).findAny().orElse(null);
                if(equipmentTable == null){
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: EquipmentTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: EquipmentTable not find.", ResponseErrorCode.NOT_FIND_DATA);
                }
                MyEquipmentInventoryDto myEquipmentInventoryDto = new MyEquipmentInventoryDto();
                myEquipmentInventoryDto.SetMyEquipmentInventoryDto(userId, temp.code, equipmentTable.getGrade(), temp.count, 1);
                myEquipmentInventoryList.add(myEquipmentInventoryRepository.save(myEquipmentInventoryDto.ToEntity()));
                continue;
            }
            myEquipmentInventory.AddCount(temp.count);
        }
        map.put("myEquipmentInventoryList", myEquipmentInventoryList);
        return map;
    }

    public Map<String, Object> EquipmentLevelUp(Long userId, Long id, Map<String, Object> map) {
        MyEquipmentInventory myEquipmentInventory = myEquipmentInventoryRepository.findById(id).orElse(null);
        if(myEquipmentInventory == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyEquipmentInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEquipmentInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: User not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: User not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        //BigInteger finalCost = GetLevelUpCost(myEquipmentInventory.getGradeValue(), myEquipmentInventory.getLevel());
//        if(!user.SpendSoulStone(finalCost)){
//            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_SOULSTONE.getIntegerValue(), "Fail! -> Cause: Need More SoulStone.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
//            throw new MyCustomException("Fail! -> Cause: Need More SoulStone.", ResponseErrorCode.NEED_MORE_SOULSTONE);
//        }
        myEquipmentInventory.UpgradeLevel();
        map.put("user", user);
        MyEquipmentInventoryDto myEquipmentInventoryDto = new MyEquipmentInventoryDto();
        myEquipmentInventoryDto.InitFromDBData(myEquipmentInventory);
        map.put("myEquipmentInventory", myEquipmentInventoryDto);
        return map;
    }

    private BigInteger GetLevelUpCost(int grade, int level) {
        int baseCost = 0;
        switch(grade){
            case 1:
                baseCost = 5;
                break;
            case 2:
                baseCost = 10;
                break;
            case 3:
                baseCost = 50;
                break;
            case 4:
                baseCost = 150;
                break;
            case 5:
                baseCost = 500;
                break;
            case 6:
                baseCost = 1000;
                break;
        }
        int cost = (int) MathHelper.RoundUP(baseCost + (baseCost*(grade+4)*(level*0.1)*(level-1)));
        return new BigInteger(String.valueOf(cost));
    }
}
