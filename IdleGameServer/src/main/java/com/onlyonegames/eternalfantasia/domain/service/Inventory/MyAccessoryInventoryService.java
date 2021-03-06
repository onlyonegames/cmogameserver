package com.onlyonegames.eternalfantasia.domain.service.Inventory;

import com.mysql.cj.xdevapi.JsonString;
import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.AccessoryOptionJsonDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Inventory.OptionLockListJsonDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.AccessoryInventoryResponseDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyAccessoryInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyAccessoryInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.util.MathHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyAccessoryInventoryService {
    private final MyAccessoryInventoryRepository myAccessoryInventoryRepository;
    private final UserRepository userRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> UpgradeAccessory(Long userId, String code, Map<String, Object> map){
        List<MyAccessoryInventory> myAccessoryInventoryList = myAccessoryInventoryRepository.findAllByUseridUser(userId);
        MyAccessoryInventory myAccessoryInventory = myAccessoryInventoryList.stream().filter(i -> i.getCode().equals(code)).findAny().orElse(null);
        if(myAccessoryInventory == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyAccessoryInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Not Found MyAccessoryInventory", ResponseErrorCode.NOT_FIND_DATA);
        }
        AccessoryInventoryResponseDto accessoryInventoryResponseDto = new AccessoryInventoryResponseDto();
        if(myAccessoryInventory.getLevel() == 0) {
            myAccessoryInventory.SpendAccessory();
            myAccessoryInventory.AccessoryLevelUp();
            accessoryInventoryResponseDto.InitFromDB(myAccessoryInventory);
            map.put("myAccessoryInventory", accessoryInventoryResponseDto);
            return map;
        }
        double rng = MathHelper.Range(0, 1);
        double baseRNG = 1-(0.02 * (myAccessoryInventory.getLevel() - 1));
        baseRNG = Math.max(baseRNG, 0.05D);
        if(rng > baseRNG) {
            myAccessoryInventory.SpendAccessory();
            accessoryInventoryResponseDto.InitFromDB(myAccessoryInventory);
            map.put("myAccessoryInventory", accessoryInventoryResponseDto);
            return map;
        }

        myAccessoryInventory.SpendAccessory();
        myAccessoryInventory.AccessoryLevelUp();
        int level = myAccessoryInventory.getLevel();
        if (level == 2 || level == 5 || level == 10){
            AccessoryOptionJsonDto accessoryOptionJsonDto = JsonStringHerlper.ReadValueFromJson(myAccessoryInventory.getOptions(), AccessoryOptionJsonDto.class);
            AccessoryOptionJsonDto.OptionInfo optionInfo = new AccessoryOptionJsonDto.OptionInfo();
            optionInfo.SetOptionInfo(0, (int)(Math.random() *6));
            accessoryOptionJsonDto.options.add(optionInfo);
            String json_Option = JsonStringHerlper.WriteValueAsStringFromData(accessoryOptionJsonDto);
            myAccessoryInventory.Reset_Options(json_Option);
            OptionLockListJsonDto optionLockListJsonDto = JsonStringHerlper.ReadValueFromJson(myAccessoryInventory.getOptionLockList(), OptionLockListJsonDto.class);
            optionLockListJsonDto.optionLockList.add(0);
            String optionLockListString = JsonStringHerlper.WriteValueAsStringFromData(optionLockListJsonDto);
            myAccessoryInventory.SetOptionLockList(optionLockListString);
        }
        accessoryInventoryResponseDto.InitFromDB(myAccessoryInventory);
        map.put("myAccessoryInventory", accessoryInventoryResponseDto);
        return map;
    }

    public Map<String, Object> UpgradeAllAccessory(Long userId, String code, Map<String, Object> map) {
        List<MyAccessoryInventory> myAccessoryInventoryList = myAccessoryInventoryRepository.findAllByUseridUser(userId);
        MyAccessoryInventory myAccessoryInventory = myAccessoryInventoryList.stream().filter(i -> i.getCode().equals(code)).findAny().orElse(null);
        if(myAccessoryInventory == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyAccessoryInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Not Found MyAccessoryInventory", ResponseErrorCode.NOT_FIND_DATA);
        }
        AccessoryInventoryResponseDto accessoryInventoryResponseDto = new AccessoryInventoryResponseDto();
        if(myAccessoryInventory.getLevel() == 0) {
            myAccessoryInventory.SpendAccessory();
            myAccessoryInventory.AccessoryLevelUp();
            accessoryInventoryResponseDto.InitFromDB(myAccessoryInventory);
            map.put("myAccessoryInventory", accessoryInventoryResponseDto);
            return map;
        }
        List<Boolean> successList = new ArrayList<>();
        while (myAccessoryInventory.getCount() != 0 && myAccessoryInventory.getLevel() != 100) {
            double rng = MathHelper.Range(0, 1);
            double baseRNG = 1 - (0.02 * (myAccessoryInventory.getLevel() - 1));
            baseRNG = Math.max(baseRNG, 0.05D);
            myAccessoryInventory.SpendAccessory();
            if (rng > baseRNG) {
                successList.add(false);
                continue;
            }
            myAccessoryInventory.AccessoryLevelUp();
            int level = myAccessoryInventory.getLevel();
            if (level == 2 || level == 5 || level == 10) {
                AccessoryOptionJsonDto accessoryOptionJsonDto = JsonStringHerlper.ReadValueFromJson(myAccessoryInventory.getOptions(), AccessoryOptionJsonDto.class);
                AccessoryOptionJsonDto.OptionInfo optionInfo = new AccessoryOptionJsonDto.OptionInfo();
                optionInfo.SetOptionInfo(0, (int) (Math.random() * 6));
                accessoryOptionJsonDto.options.add(optionInfo);
                String json_Option = JsonStringHerlper.WriteValueAsStringFromData(accessoryOptionJsonDto);
                myAccessoryInventory.Reset_Options(json_Option);
                OptionLockListJsonDto optionLockListJsonDto = JsonStringHerlper.ReadValueFromJson(myAccessoryInventory.getOptionLockList(), OptionLockListJsonDto.class);
                optionLockListJsonDto.optionLockList.add(0);
                String optionLockListString = JsonStringHerlper.WriteValueAsStringFromData(optionLockListJsonDto);
                myAccessoryInventory.SetOptionLockList(optionLockListString);
            }
            successList.add(true);
        }
        accessoryInventoryResponseDto.InitFromDB(myAccessoryInventory);
        map.put("myAccessoryInventory", accessoryInventoryResponseDto);
        map.put("successList", successList);
        return map;
    }

    public Map<String, Object> ChangeOption(Long userId, AccessoryInventoryResponseDto dto, Map<String, Object> map) {
        List<MyAccessoryInventory> myAccessoryInventoryList = myAccessoryInventoryRepository.findAllByUseridUser(userId);
        MyAccessoryInventory myAccessoryInventory = myAccessoryInventoryList.stream().filter(i -> i.getCode().equals(dto.getCode())).findAny().orElse(null);
        if(myAccessoryInventory == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyAccessoryInventory", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Not Found MyAccessoryInventory", ResponseErrorCode.NOT_FIND_DATA);
        }
        myAccessoryInventory.SetMyAccessoryInventory(dto);
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found User", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Not Found User", ResponseErrorCode.NOT_FIND_DATA);
        }
        OptionLockListJsonDto optionLockListJsonDto = JsonStringHerlper.ReadValueFromJson(myAccessoryInventory.getOptionLockList(), OptionLockListJsonDto.class);
        int lockCount = 0;
        for(int i : optionLockListJsonDto.optionLockList){
            if(i == 1)
                lockCount++;
        }

        int spendDiamond = (100 * ((int)(Math.pow(lockCount, 2)) + 1));
        if (!user.SpendDiamond(spendDiamond)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Need More Diamond", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Need More Diamond", ResponseErrorCode.NEED_MORE_DIAMOND);
        }

        List<Double> probabilityList = new ArrayList<>();
        probabilityList.add(0.4);
        probabilityList.add(0.25);
        probabilityList.add(0.15);
        probabilityList.add(0.10);
        probabilityList.add(0.08);
        probabilityList.add(0.02);

        AccessoryOptionJsonDto accessoryOptionJsonDto = JsonStringHerlper.ReadValueFromJson(myAccessoryInventory.getOptions(), AccessoryOptionJsonDto.class);
        for(int i = 0; i < accessoryOptionJsonDto.options.size(); i++) {
            if(optionLockListJsonDto.optionLockList.get(i) == 1)
                continue;
            int option_Index = (int)(Math.random() * 9);
            int option_Grade_Index = MathHelper.RandomIndexWidthProbability(probabilityList);
            AccessoryOptionJsonDto.OptionInfo optionInfo = accessoryOptionJsonDto.options.get(i);
            optionInfo.SetOptionInfo(option_Grade_Index, option_Index);
        }
        String options = JsonStringHerlper.WriteValueAsStringFromData(accessoryOptionJsonDto);
        myAccessoryInventory.Reset_Options(options);
        AccessoryInventoryResponseDto accessoryInventoryResponseDto = new AccessoryInventoryResponseDto();
        accessoryInventoryResponseDto.InitFromDB(myAccessoryInventory);
        map.put("myAccessoryInventory", accessoryInventoryResponseDto);
        map.put("diamond", user.getDiamond());
        return map;
    }
}
