package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Inventory.MyRuneInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyPixieInfoDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.PixieLevelUpRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyRuneInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyPixieInfoData;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyRuneInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyPixieInfoDataRepository;
import com.onlyonegames.util.MathHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyPixieInfoService {
    private final MyPixieInfoDataRepository myPixieInfoDataRepository;
    private final MyRuneInventoryRepository myRuneInventoryRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> GetMyPixieInfo(Long userId, Map<String, Object> map) {
        MyPixieInfoData myPixieInfoData = myPixieInfoDataRepository.findByUseridUser(userId).orElse(null);
        if(myPixieInfoData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyPixieInfoData", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Not Found MyPixieInfoData", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyPixieInfoDataDto myPixieInfoDataDto = new MyPixieInfoDataDto();
        myPixieInfoDataDto.InitFromDBData(myPixieInfoData);
        map.put("myPixieInfoData", myPixieInfoDataDto);
        return map;
    }

    public Map<String, Object> PixieLevelUp(Long userId, List<PixieLevelUpRequestDto.PixieGettingRuneInfo> pixieGettingRuneInfoList, Map<String, Object> map) {
//        MyPixieInfoData myPixieInfoData = myPixieInfoDataRepository.findByUseridUser(userId).orElse(null);
//        if(myPixieInfoData == null) {
//            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyPixieInfoData", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
//            throw new MyCustomException("Not Found MyPixieInfoData", ResponseErrorCode.NOT_FIND_DATA);
//        }
//        List<MyRuneInventory> myRuneInventoryList = myRuneInventoryRepository.findAllByUseridUser(userId);
//        for(PixieLevelUpRequestDto.PixieGettingRuneInfo temp : pixieGettingRuneInfoList) {
//            MyRuneInventory myRuneInventory = myRuneInventoryList.stream().filter(i -> i.getRune_Id() == temp.rune_id && i.getItemClassValue() == temp.classValue && i.getGrade() == temp.grade).findAny().orElse(null);
//            if(myRuneInventory == null) {
//                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyRuneInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
//                throw new MyCustomException("Not Found MyRuneInventory", ResponseErrorCode.NOT_FIND_DATA);
//            }
//            if(!myRuneInventory.SpendRune(temp.count)) {
//                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_RUNE.getIntegerValue(), "Need More Rune", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
//                throw new MyCustomException("Need More Rune", ResponseErrorCode.NEED_MORE_RUNE);
//            }
//        }
//        myPixieInfoData.GetExp(GetExpCalculate(pixieGettingRuneInfoList));
//        MyPixieInfoDataDto myPixieInfoDataDto = new MyPixieInfoDataDto();
//        myPixieInfoDataDto.InitFromDBData(myPixieInfoData);
//        map.put("myPixieInfoData", myPixieInfoDataDto);
        return map;
    }

    public Map<String, Object> EquipmentRune(Long userId, Long runeInventoryId, int slotNo, Map<String, Object> map) {
//        MyPixieInfoData myPixieInfoData = myPixieInfoDataRepository.findByUseridUser(userId).orElse(null);
//        if(myPixieInfoData == null) {
//            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyPixieInfoData", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
//            throw new MyCustomException("Not Found MyPixieInfoData", ResponseErrorCode.NOT_FIND_DATA);
//        }
//        MyRuneInventory myRuneInventory = myRuneInventoryRepository.findById(runeInventoryId).orElse(null);
//        if(myRuneInventory == null) {
//            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyRuneInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
//            throw new MyCustomException("Not Found MyRuneInventory", ResponseErrorCode.NOT_FIND_DATA);
//        }
//        if(!myRuneInventory.SpendRune(1)){
//            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_RUNE.getIntegerValue(), "Need More Rune", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
//            throw new MyCustomException("Need More Rune", ResponseErrorCode.NEED_MORE_RUNE);
//        }
//        int levelCheck = 0;
//        switch(slotNo){
//            case 2:
//                levelCheck = 50;
//                break;
//            case 3:
//                levelCheck = 100;
//                break;
//            case 4:
//                levelCheck = 300;
//                break;
//            case 5:
//                levelCheck = 500;
//                break;
//            case 6:
//                levelCheck = 1000;
//                break;
//        }
//        if(myPixieInfoData.getLevel()<levelCheck){
//            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_LEVEL.getIntegerValue(), "Need More Level", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
//            throw new MyCustomException("Need More Level", ResponseErrorCode.NEED_MORE_LEVEL);
//        }
//        Long unEquipmentRuneId = myPixieInfoData.EquipmentRune(runeInventoryId, slotNo);
//        if(!unEquipmentRuneId.equals(0L)) {
//            MyRuneInventory unEquipmentRune = myRuneInventoryRepository.findById(unEquipmentRuneId).orElse(null);
//            if(unEquipmentRune == null) {
//                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyRuneInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
//                throw new MyCustomException("Not Found MyRuneInventory", ResponseErrorCode.NOT_FIND_DATA);
//            }
//            unEquipmentRune.AddCount(1);
//            MyRuneInventoryDto unEquipmentRuneDto = new MyRuneInventoryDto();
//            unEquipmentRuneDto.InitFromDBData(unEquipmentRune);
//            map.put("unEquipmentRune", unEquipmentRuneDto);
//        }
//
//        MyPixieInfoDataDto myPixieInfoDataDto = new MyPixieInfoDataDto();
//        myPixieInfoDataDto.InitFromDBData(myPixieInfoData);
//        MyRuneInventoryDto equipmentRuneDto = new MyRuneInventoryDto();
//        equipmentRuneDto.InitFromDBData(myRuneInventory);
//        map.put("myPixieInfoData", myPixieInfoDataDto);
//        map.put("unEquipmentRune", equipmentRuneDto);
        return map;
    }

    public Map<String, Object> UnEquipmentRune(Long userId, int slotNo, Map<String, Object> map) {
//        MyPixieInfoData myPixieInfoData = myPixieInfoDataRepository.findByUseridUser(userId).orElse(null);
//        if(myPixieInfoData == null) {
//            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyPixieInfoData", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
//            throw new MyCustomException("Not Found MyPixieInfoData", ResponseErrorCode.NOT_FIND_DATA);
//        }
//        Long unEquipmentRuneId = myPixieInfoData.UnEquipmentRune(slotNo);
//        if(!unEquipmentRuneId.equals(0L)){
//            MyRuneInventory unEquipmentRune = myRuneInventoryRepository.findById(unEquipmentRuneId).orElse(null);
//            if(unEquipmentRune == null) {
//                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyRuneInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
//                throw new MyCustomException("Not Found MyRuneInventory", ResponseErrorCode.NOT_FIND_DATA);
//            }
//            unEquipmentRune.AddCount(1);
//            MyRuneInventoryDto unEquipmentRuneDto = new MyRuneInventoryDto();
//            unEquipmentRuneDto.InitFromDBData(unEquipmentRune);
//            map.put("unEquipmentRune", unEquipmentRuneDto);
//        }
//        MyPixieInfoDataDto myPixieInfoDataDto = new MyPixieInfoDataDto();
//        myPixieInfoDataDto.InitFromDBData(myPixieInfoData);
//        map.put("myPixieInfoData", myPixieInfoDataDto);
        return map;
    }

    private Long GetExpCalculate(List<PixieLevelUpRequestDto.PixieGettingRuneInfo> pixieGettingRuneInfoList) {
        long total = 0L;
        for(PixieLevelUpRequestDto.PixieGettingRuneInfo temp : pixieGettingRuneInfoList) {
            int grade = temp.grade;
            int quality = temp.classValue;
            double gradeSquared = Math.pow(grade, grade);
            double qualitySquared = Math.pow(quality, 2);
            double squared = Math.pow(5, grade);
            double finalExpD = gradeSquared * qualitySquared * squared * 0.1D;
            long finalExp = (long) MathHelper.RoundUP(finalExpD);
            total += finalExp * temp.count;
        }
        return total;
    }
}
