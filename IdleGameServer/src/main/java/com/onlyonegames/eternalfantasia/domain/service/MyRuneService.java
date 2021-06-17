package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Inventory.MyRuneInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyRuneLevelInfoDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.GettingRuneRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyRuneInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyRuneLevelInfoData;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyRuneInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyRuneLevelInfoDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyRuneService {
    private final MyRuneInventoryRepository myRuneInventoryRepository;
    private final MyRuneLevelInfoDataRepository myRuneLevelInfoDataRepository;
    private final UserRepository userRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> GetRuneInfo(Long userId, Map<String, Object> map) {
        MyRuneLevelInfoData myRuneLevelInfoData = myRuneLevelInfoDataRepository.findByUseridUser(userId).orElse(null);
        if(myRuneLevelInfoData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyRuneLevelInfoData", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Not Found MyRuneLevelInfoData", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<MyRuneInventory> myRuneInventoryList = myRuneInventoryRepository.findAllByUseridUser(userId);
        List<MyRuneInventoryDto> myRuneInventoryDtoList = new ArrayList<>();

        MyRuneLevelInfoDataDto myRuneLevelInfoDataDto = new MyRuneLevelInfoDataDto();
        myRuneLevelInfoDataDto.InitFromDBData(myRuneLevelInfoData);

        for(MyRuneInventory temp : myRuneInventoryList) {
            MyRuneInventoryDto myRuneInventoryDto = new MyRuneInventoryDto();
            myRuneInventoryDto.InitFromDBData(temp);
            myRuneInventoryDtoList.add(myRuneInventoryDto);
        }
        map.put("myRuneLevelInfo", myRuneLevelInfoDataDto);
        map.put("myRuneInventory", myRuneInventoryDtoList);
        return map;
    }

    public Map<String, Object> GettingRune(Long userId, List<GettingRuneRequestDto.GettingRuneInfo> gettingRuneInfoList, Map<String, Object> map) { //TODO
//        User user = userRepository.findById(userId).orElse(null);
//        if(user == null) {
//            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found IdleUser", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
//            throw new MyCustomException("Not Found IdleUser", ResponseErrorCode.NOT_FIND_DATA);
//        }
//        MyRuneLevelInfoData myRuneLevelInfoData = myRuneLevelInfoDataRepository.findByUseridUser(userId).orElse(null);
//        if(myRuneLevelInfoData == null) {
//            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyRuneLevelInfoData", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
//            throw new MyCustomException("Not Found MyRuneLevelInfoData", ResponseErrorCode.NOT_FIND_DATA);
//        }
//        List<MyRuneInventory> myRuneInventoryList = myRuneInventoryRepository.findAllByUseridUser(userId);
//        int addLevel = 0;
//        for(GettingRuneRequestDto.GettingRuneInfo temp : gettingRuneInfoList) {
//            MyRuneInventory selectedRune = myRuneInventoryList.stream().filter(i -> i.getRune_Id() == temp.rune_id && i.getGrade() == temp.grade && i.getItemClassValue() == temp.itemClassValue).findAny().orElse(null);
//            if(selectedRune == null) {
//                MyRuneInventoryDto myRuneInventoryDto = new MyRuneInventoryDto();
//                myRuneInventoryDto.SetMyRuneInventoryDto(userId, temp.rune_id, temp.itemClassValue, temp.grade, temp.count);
//                selectedRune = myRuneInventoryRepository.save(myRuneInventoryDto.ToEntity());
//                myRuneInventoryList.add(selectedRune);
//                addLevel += temp.count;
//                continue;
//            }
//            selectedRune.AddCount(temp.count);
//            addLevel += temp.count;
//        }
//        myRuneLevelInfoData.LevelUp(addLevel);
//        MyRuneLevelInfoDataDto myRuneLevelInfoDataDto = new MyRuneLevelInfoDataDto();
//        myRuneLevelInfoDataDto.InitFromDBData(myRuneLevelInfoData);
//        List<MyRuneInventoryDto> myRuneInventoryDtoList = new ArrayList<>();
//        for(MyRuneInventory temp : myRuneInventoryList) {
//            MyRuneInventoryDto myRuneInventoryDto = new MyRuneInventoryDto();
//            myRuneInventoryDto.InitFromDBData(temp);
//            myRuneInventoryDtoList.add(myRuneInventoryDto);
//        }
//        map.put("myRuneLevelInfo", myRuneLevelInfoDataDto);
//        map.put("myRuneInventory", myRuneInventoryDtoList);
        return map;
    }

    public Map<String, Object> RuneUpgrade(Long userId, Map<String, Object> map) { //TODO 클라이언트에서 결과값을 넘겨주면 세팅만 함 (SETTER)
        List<MyRuneInventory> myRuneInventoryList = myRuneInventoryRepository.findAllByUseridUser(userId);

        return map;
    }

//    public BigInteger calculationPrice(int level, int count) {// (250/3)*n(n+2)(3n^2-4n+5)
//        int finalValue = level + count;
//        BigInteger finalLevel = new BigInteger(Integer.toString(finalValue));
//        BigInteger first = new BigInteger("250");
//        return first;
//    }
}
