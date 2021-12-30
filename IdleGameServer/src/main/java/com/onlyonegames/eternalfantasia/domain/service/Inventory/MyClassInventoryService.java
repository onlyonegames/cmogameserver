package com.onlyonegames.eternalfantasia.domain.service.Inventory;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Inventory.ClassOptionJsonDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Inventory.OptionLockListJsonDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.ClassInventoryResponseDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyClassInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyClassInventoryRepository;
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
@AllArgsConstructor
@Transactional
public class MyClassInventoryService {
    private final MyClassInventoryRepository myClassInventoryRepository;
    private final UserRepository userRepository;
    private final ErrorLoggingService errorLoggingService;
    private final List<List<Integer>> optionList = Arrays.asList(Arrays.asList(0,1,2), Arrays.asList(0,1,3), Arrays.asList(4,5,6), Arrays.asList(7,8,4), Arrays.asList(7,8,5));

    public Map<String, Object> ClassTranscendence(Long userId, String code, Map<String, Object> map) {
        MyClassInventory myClassInventory = myClassInventoryRepository.findByUseridUserAndCode(userId, code).orElse(null);
        if (myClassInventory == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyClassInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyClassInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        int price;
        int level = myClassInventory.getSuperiorLevel();
        if (level >= 5) {
            ClassInventoryResponseDto classInventoryResponseDto = new ClassInventoryResponseDto();
            classInventoryResponseDto.InitFromDB(myClassInventory);
            map.put("myClassInventory", classInventoryResponseDto);
            return map;
        }
        switch (level) {
            case 0:
            case 1:
                price = 1;
                break;
            case 2:
                price = 2;
                break;
            case 3:
            case 4:
                price = 3;
                break;
            default:
                price = 0;
                break;
        }
        if (price == 0) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_MORE_UPGRADE.getIntegerValue(), "Fail! -> Cause: Can't More Upgrade.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't More Upgrade.", ResponseErrorCode.CANT_MORE_UPGRADE);
        }
        if (!myClassInventory.SpendCount(price)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_CLASS.getIntegerValue(), "Fail! -> Cause: Need More Class.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Need More Class.", ResponseErrorCode.NEED_MORE_CLASS);
        }
        myClassInventory.UpgradeClass();
        List<Integer> options = optionList.get(level);
        ClassOptionJsonDto classOptionJsonDto = JsonStringHerlper.ReadValueFromJson(myClassInventory.getSuperiorOptions(), ClassOptionJsonDto.class);
        ClassOptionJsonDto.OptionInfo optionInfo = new ClassOptionJsonDto.OptionInfo();
        optionInfo.SetOptionInfo(0, options.get((int)(Math.random() * 3)));
        classOptionJsonDto.options.add(optionInfo);
        String json_Option = JsonStringHerlper.WriteValueAsStringFromData(classOptionJsonDto);
        myClassInventory.ResetOptions(json_Option);
        OptionLockListJsonDto optionLockListJsonDto = JsonStringHerlper.ReadValueFromJson(myClassInventory.getSuperiorOptionLock(), OptionLockListJsonDto.class);
        optionLockListJsonDto.optionLockList.add(0);
        String optionLockListString = JsonStringHerlper.WriteValueAsStringFromData(optionLockListJsonDto);
        myClassInventory.ResetOptionLock(optionLockListString);
        ClassInventoryResponseDto classInventoryResponseDto = new ClassInventoryResponseDto();
        classInventoryResponseDto.InitFromDB(myClassInventory);
        map.put("myClassInventory", classInventoryResponseDto);
        return map;
    }

    public Map<String, Object> ChangeOption(Long userId, ClassInventoryResponseDto dto, Map<String, Object> map) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found User", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Not Found User", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyClassInventory myClassInventory = myClassInventoryRepository.findByUseridUserAndCode(userId, dto.getCode()).orElse(null);
        if (myClassInventory == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyClassInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyClassInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        myClassInventory.SetMyClassInventory(dto);
        OptionLockListJsonDto optionLockListJsonDto = JsonStringHerlper.ReadValueFromJson(myClassInventory.getSuperiorOptionLock(), OptionLockListJsonDto.class);
        int lockCount = 0;
        for(int i : optionLockListJsonDto.optionLockList){
            if(i == 1)
                lockCount++;
        }

        int spendClassEmblem = 50 * ((int)Math.pow(lockCount, 2.5) + 1);
        if (!user.SpendClassEmblem((long) spendClassEmblem)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_CLASS_EMBLEM.getIntegerValue(), "Need More ClassEmblem", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Need More ClassEmblem", ResponseErrorCode.NEED_MORE_CLASS_EMBLEM);
        }

        List<Double> probabilityList = new ArrayList<>();
        probabilityList.add(0.15D);
        probabilityList.add(0.23D);
        probabilityList.add(0.25D);
        probabilityList.add(0.20D);
        probabilityList.add(0.10D);
        probabilityList.add(0.05D);
        probabilityList.add(0.02D);

        ClassOptionJsonDto classOptionJsonDto = JsonStringHerlper.ReadValueFromJson(myClassInventory.getSuperiorOptions(), ClassOptionJsonDto.class);
        for (int i = 0; i < classOptionJsonDto.options.size(); i++) { //TODO 옵션 변경 시 잠겨있는 옵션도 변경되는 현상이 있었음 확인 필요
            if (optionLockListJsonDto.optionLockList.get(i) == 1)
                continue;
            List<Integer> indexList = optionList.get(i);
            int option_Index = (int)(Math.random() * 3);
            int option_Grade_Index = MathHelper.RandomIndexWidthProbability(probabilityList);
            ClassOptionJsonDto.OptionInfo optionInfo = classOptionJsonDto.options.get(i);
            optionInfo.SetOptionInfo(option_Grade_Index, indexList.get(option_Index));
        }
        String options = JsonStringHerlper.WriteValueAsStringFromData(classOptionJsonDto);
        myClassInventory.ResetOptions(options);
        ClassInventoryResponseDto classInventoryResponseDto = new ClassInventoryResponseDto();
        classInventoryResponseDto.InitFromDB(myClassInventory);
        map.put("myClassInventory", classInventoryResponseDto);
        map.put("classEmblem", user.getClassEmblem());
        return map;
    }
}
