package com.onlyonegames.eternalfantasia.domain.service.Inventory;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.ActiveSkillDataJsonDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Inventory.MyClassInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyClassInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyActiveSkillData;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.HeroClassInfoTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.SkillUpgradeInfoTable;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyClassInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyActiveSkillDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.util.MathHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyClassInventoryService {
    private final MyClassInventoryRepository myClassInventoryRepository;
    private final MyActiveSkillDataRepository myActiveSkillDataRepository;
    private final UserRepository userRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> GetMyClassInfo(Long userId, Map<String, Object> map) {
        List<MyClassInventory> myClassInventoryList = myClassInventoryRepository.findAllByUseridUser(userId);
        List<MyClassInventoryDto> myClassInventoryDtoList = new ArrayList<>();
        for(MyClassInventory temp : myClassInventoryList){
            MyClassInventoryDto myClassInventoryDto = new MyClassInventoryDto();
            myClassInventoryDto.InitFromDBData(temp);
            myClassInventoryDtoList.add(myClassInventoryDto);
        }
        map.put("myClassInventoryList", myClassInventoryDtoList);
        return map;
    }

    public Map<String, Object> GettingClass(Long userId, String code, int addCount, Map<String, Object> map) {
        List<MyClassInventory> myClassInventoryList = myClassInventoryRepository.findAllByUseridUser(userId);
        HeroClassInfoTable heroClassInfoTable = gameDataTableService.HeroClassInfoTable().stream().filter(i -> i.getCode().equals(code)).findAny().orElse(null);
        if(heroClassInfoTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: HeroClassInfoTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: HeroClassInfoTable not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyClassInventory myClassInventory = myClassInventoryList.stream().filter(i -> i.getCode().equals(code)).findAny().orElse(null);
        if(myClassInventory == null) {
            MyClassInventoryDto myClassInventoryDto = new MyClassInventoryDto();
            myClassInventoryDto.setCode(code);
            myClassInventoryDto.setCount(addCount);
            myClassInventoryDto.setLevel(1);
//            myClassInventoryDto.setSkillUpgradeIndex(0);
            myClassInventoryDto.setUseridUser(userId);
            myClassInventory = myClassInventoryRepository.save(myClassInventoryDto.ToEntity());
            myClassInventoryList.add(myClassInventory);
            MyActiveSkillData myActiveSkillData = myActiveSkillDataRepository.findByUseridUser(userId).orElse(null);
            if(myActiveSkillData == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyActiveSkillData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: MyActiveSkillData not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            String myActiveSkillJson = myActiveSkillData.getJson_saveDataValue();
            ActiveSkillDataJsonDto activeSkillDataJsonDto = JsonStringHerlper.ReadValueFromJson(myActiveSkillJson, ActiveSkillDataJsonDto.class);
            List<ActiveSkillDataJsonDto.SkillInfo> skillInfoList = activeSkillDataJsonDto.getSkillInfoList().stream().filter(i -> (Integer.parseInt(i.code.split("_")[1]) % 4) == (heroClassInfoTable.getGradeValue() - 1)).collect(Collectors.toList());
            ActiveSkillDataJsonDto.SkillInfo skillInfo;
            switch (heroClassInfoTable.getGrade()){
                case "High":
                    skillInfo = skillInfoList.get(1);
                    skillInfo.SkillOpen();
                    break;
                case "Rare":
                    skillInfo = skillInfoList.get(2);
                    skillInfo.SkillOpen();
                    break;
                case "Hero":
                    skillInfo = skillInfoList.get(3);
                    skillInfo.SkillOpen();
                    break;
            }
            myActiveSkillJson = JsonStringHerlper.WriteValueAsStringFromData(activeSkillDataJsonDto);
            myActiveSkillData.ResetJson_SaveDataValue(myActiveSkillJson);
            map.put("myActiveSkillInfo", activeSkillDataJsonDto);
            map.put("myClassInventoryList", myClassInventoryList);
            return map;
        }
        myClassInventory.AddCount(addCount);
        map.put("myClassInventoryList", myClassInventoryList);
        return map;

    }

    public Map<String, Object> ClassLevelUp(Long userId, Long classId, Map<String, Object> map) {
        MyClassInventory myClassInventory = myClassInventoryRepository.findById(classId).orElse(null);
        if(myClassInventory == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyClassInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyClassInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: user not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: user not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        HeroClassInfoTable heroClassInfoTable = gameDataTableService.HeroClassInfoTable().stream().filter(i -> i.getClassName().equals(myClassInventory.getCode())).findAny().orElse(null);
        if(heroClassInfoTable == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: HeroClassInfoTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: HeroClassInfoTable not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        if(user.SpendSoulStone(CalculatedCost(heroClassInfoTable.getGradeValue(), myClassInventory.getLevel()))) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_SOULSTONE.getIntegerValue(), "Fail! -> Cause: Need More SoulStone.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Need More SoulStone.", ResponseErrorCode.NEED_MORE_SOULSTONE);
        }
        myClassInventory.LevelUp();
        MyClassInventoryDto myClassInventoryDto = new MyClassInventoryDto();
        myClassInventoryDto.InitFromDBData(myClassInventory);
        map.put("user", user);
        map.put("myClassInventory", myClassInventoryDto);
        return map;
    }

//    public Map<String, Object> SkillUpgrade(Long userId, Long classId, Map<String, Object> map) {
//        MyClassInventory myClassInventory = myClassInventoryRepository.findById(classId).orElse(null);
//        if(myClassInventory == null){
//            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyClassInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
//            throw new MyCustomException("Fail! -> Cause: MyClassInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
//        }
//        MyActiveSkillData myActiveSkillData = myActiveSkillDataRepository.findByUseridUser(userId).orElse(null);
//        if(myActiveSkillData == null) {
//            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyActiveSkillData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
//            throw new MyCustomException("Fail! -> Cause: MyActiveSkillData not find.", ResponseErrorCode.NOT_FIND_DATA);
//        }
//        String myActiveSkillString = myActiveSkillData.getJson_saveDataValue();
//        ActiveSkillDataJsonDto activeSkillDataJsonDto = JsonStringHerlper.ReadValueFromJson(myActiveSkillString, ActiveSkillDataJsonDto.class);
//        List<SkillUpgradeInfoTable> skillUpgradeInfoTableList = gameDataTableService.SkillUpgradeInfoTable().stream().filter(i -> i.getOwnerClass().equals(myClassInventory.getCode())).collect(Collectors.toList());
//        SkillUpgradeInfoTable skillUpgradeInfoTable = skillUpgradeInfoTableList.get(myClassInventory.getSkillUpgradeIndex());
//
////        switch(skillUpgradeInfoTable.getUpgradeInfo()){
////            case "MaxLV_UP":
////
////        }
//        String upgradeString = skillUpgradeInfoTable.getUpgradeInfo();
//        if(upgradeString.contains("MaxLV_UP")){
//            String[] upgradeStringSplit = upgradeString.split("-");
//            ActiveSkillDataJsonDto.SkillInfo skillInfo = activeSkillDataJsonDto.getSkillInfoList().stream()
//                    .filter(i -> i.code.equals(upgradeStringSplit[1])).findAny().orElse(null);
//            if(skillInfo == null) {
//                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ActiveSkillDataJsonDto.SkillInfo not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
//                throw new MyCustomException("Fail! -> Cause: ActiveSkillDataJsonDto.SkillInfo not find.", ResponseErrorCode.NOT_FIND_DATA);
//            }
//            skillInfo.MaxLevelUp(20);
//        }
//        else if(upgradeString.contains("Option_Open")) {
//            String[] upgradeStringSplit = upgradeString.split("-");
//            ActiveSkillDataJsonDto.SkillInfo skillInfo = activeSkillDataJsonDto.getSkillInfoList().stream()
//                    .filter(i -> i.code.equals(upgradeStringSplit[1])).findAny().orElse(null);
//            if(skillInfo == null) {
//                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ActiveSkillDataJsonDto.SkillInfo not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
//                throw new MyCustomException("Fail! -> Cause: ActiveSkillDataJsonDto.SkillInfo not find.", ResponseErrorCode.NOT_FIND_DATA);
//            }
//            String[] optionSplit = upgradeStringSplit[2].split("_");
//            int optionIndex = Integer.parseInt(optionSplit[2]);
//            skillInfo.OptionOpen(optionIndex);
//        }
//        myClassInventory.SkillUpgrade();
//        MyClassInventoryDto myClassInventoryDto = new MyClassInventoryDto();
//        myClassInventoryDto.InitFromDBData(myClassInventory);
//        myActiveSkillString = JsonStringHerlper.WriteValueAsStringFromData(activeSkillDataJsonDto);
//        myActiveSkillData.ResetJson_SaveDataValue(myActiveSkillString);
//        map.put("myClassInventory", myClassInventoryDto);
//        map.put("myActiveSkill", activeSkillDataJsonDto);
//        return map;
//    }

    private BigInteger CalculatedCost(int grade, int level) {
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
                baseCost = 100;
                break;
            case 5:
                baseCost = 250;
                break;
        }
        int finalCost = (int) MathHelper.RoundUP(baseCost + (baseCost * grade * 2 * level * 0.1 * (level - 1)));
        return new BigInteger(String.valueOf(finalCost));
    }
}
