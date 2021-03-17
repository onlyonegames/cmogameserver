package com.onlyonegames.eternalfantasia.domain.service.Inventory;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.HeroEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.BelongingInventoryLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CurrencyLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.EquipmentLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ItemRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.EquipmentOptionsInfoTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.HeroEquipmentsTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.SpendableItemInfoTable;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.BelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.HeroEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import com.onlyonegames.eternalfantasia.etc.EquipmentCalculate;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.util.StringMaker;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;
import static com.onlyonegames.util.MathHelper.Range;

@Service
@Transactional
@AllArgsConstructor
public class HeroEquipmentOptionResmeltingService {

    private final UserRepository userRepository;
    private final HeroEquipmentInventoryRepository heroEquipmentInventoryRepository;
    private final GameDataTableService gameDataTableService;
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;

    //옵션재련 필요 다이아 수량 공식
    int getNeedOptionResmeltingDiamond(String gradeString) {
        int gradeValue = EquipmentCalculate.GradeValue(gradeString);
        return  50 * gradeValue;
    }
    //옵션재련 재련석 필요수량 공식
    int getNeedOptionResmeltingStone(String gradeString) {
        int gradeValue = EquipmentCalculate.GradeValue(gradeString);
        return  20 * gradeValue;
    }

    public Map<String, Object> OptionResmelting(Long userId, ItemRequestDto originalItem, Map<String, Object> map) {
        //해당 userId의 정보
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
        }
        //해당 userId의 EquipmentInventory 에 OriginalItem 이 있는지 확인.
        List<HeroEquipmentInventory> userHeroEquipmentInventory = heroEquipmentInventoryRepository.findByUseridUser(userId);
        HeroEquipmentInventory heroEquipment = userHeroEquipmentInventory.stream()
                .filter(a -> a.getId().equals(originalItem.getId()))
                .findAny()
                .orElse(null);
        if(heroEquipment == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Original Item Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Original Item Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        //해당 장비가 재련 가능한지 체크(노멀 등급은 재련불가)
        List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
        int itemId = heroEquipment.getItem_Id();
        HeroEquipmentsTable equipmentsTable = heroEquipmentsTableList.stream()
                .filter(a -> a.getId() == itemId)
                .findAny()
                .orElse(null);
        if(equipmentsTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Original Item Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Original Item Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        if(equipmentsTable.getGrade().equals("Normal")) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_OPTIONRESMELTING_NORMALGRADE.getIntegerValue(), "Fail! -> Cause: Can't OptionResmelting for NormalGradeItem.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't OptionResmelting for NormalGradeItem.", ResponseErrorCode.CANT_OPTIONRESMELTING_NORMALGRADE);
        }

        //재련에 필요한 골드가 충분한지 체크
        String equipmentGrade = equipmentsTable.getGrade();
        int needResmeltingDiamond = getNeedOptionResmeltingDiamond(equipmentGrade);
        CurrencyLogDto currencyLogDto = new CurrencyLogDto();
        int previousValue = user.getDiamond();
        if(!user.SpendDiamond(needResmeltingDiamond)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more resmeltingGold.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Need more resmeltingGold.", ResponseErrorCode.NEED_MORE_DIAMOND);
        }
        currencyLogDto.setCurrencyLogDto("옵션재련 - "+equipmentsTable.getName()+" ["+heroEquipment.getId()+"]","다이아", previousValue, -needResmeltingDiamond, user.getDiamond());
        String currencyLog = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
        loggingService.setLogging(userId, 1, currencyLog);

        //재련에 필요한 재련석이 충분한지 체크(재련할 장비의 등급과 동일한 재련석만 재료로 사용 가능)
        List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
        SpendableItemInfoTable spendableItemInfoTable = spendableItemInfoTableList.stream()
                .filter(a -> a.getGrade().equals(equipmentsTable.getGrade()) && a.getSpendableType().equals("MATERIAL_FOR_RESMELTING"))
                .findAny()
                .orElse(null);
        if(spendableItemInfoTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: SpendableItem Item Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: SpendableItem Item Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        int needResmeltStoneCount = getNeedOptionResmeltingStone(equipmentGrade);
        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
        BelongingInventory secondMaterialItem = belongingInventoryList.stream()
                .filter(a -> a.getItemId() == spendableItemInfoTable.getId()&&a.getItemType().getId() == 3)
                .findAny()
                .orElse(null);
        if(secondMaterialItem == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_MATERIAL.getIntegerValue(), "Fail! -> Cause: Need more ResmeltStoneCount count", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Need more ResmeltStoneCount count", ResponseErrorCode.NEED_MORE_MATERIAL);
        }
        BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
        belongingInventoryLogDto.setPreviousValue(secondMaterialItem.getCount());
        if(!secondMaterialItem.SpendItem(needResmeltStoneCount)){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_MATERIAL.getIntegerValue(), "Fail! -> Cause: Need more ResmeltStoneCount count", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw  new MyCustomException("Fail! -> Cause: Need more ResmeltStoneCount count", ResponseErrorCode.NEED_MORE_MATERIAL);
        }
        belongingInventoryLogDto.setBelongingInventoryLogDto("옵션재련 - "+equipmentsTable.getName()+" ["+heroEquipment.getId()+"]", secondMaterialItem.getId(), secondMaterialItem.getItemId(), secondMaterialItem.getItemType(), -needResmeltStoneCount, secondMaterialItem.getCount());
        String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
        loggingService.setLogging(userId, 3, belongingLog);

        //해당 userId의 OptionResmeltingCandidate 서치.
//        OptionResmeltingCandidate optionResmeltingCandidate = optionResmeltingCandidateRepository.findByUserIdUser(userId).orElseThrow(
//                () -> new MyCustomException("Fail! -> Cause: OptionResmeltingCandidate Can not find.", ResponseErrorCode.NOT_FIND_DATA));
        String gradeString = equipmentsTable.getGrade();
        int haveOptionCount = EquipmentCalculate.GetOptionCount(gradeString);
        //String[] optionsCandidate = optionResmeltingCandidate.getCandidateOptionIds().split(",");
        List<Integer> optionsCandidateList = new ArrayList<>();
        //optionsCandidateList.addAll(Arrays.asList(optionsCandidate));
        List<EquipmentOptionsInfoTable> optionsInfoTableList = gameDataTableService.OptionsInfoTableList();
        for(EquipmentOptionsInfoTable optionInfo : optionsInfoTableList)
        {
            optionsCandidateList.add((optionInfo.getID()));
        }
        String optionsIds = "";
        String optionsValues = "";
        int level = heroEquipment.getLevel();
        int classValue = heroEquipment.getItemClassValue();
        int gradeValue = EquipmentCalculate.GradeValue(gradeString);
        for(int i = 0; i < haveOptionCount; i++) {
            int randValue = (int) Range(0, optionsCandidateList.size());
            int selectedOptionId = optionsCandidateList.get(randValue);
            EquipmentOptionsInfoTable equipmentOptionsInfoTable = optionsInfoTableList.stream()
                    .filter(a -> a.getID() == selectedOptionId)
                    .findAny()
                    .orElse(null);
            if(equipmentOptionsInfoTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: SelectedOptionId Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: SelectedOptionId Can't find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            StringMaker.Clear();
            StringMaker.stringBuilder.append(optionsIds);
            if (optionsIds.length() > 0)
                StringMaker.stringBuilder.append(",");
            StringMaker.stringBuilder.append(selectedOptionId);
            optionsIds = StringMaker.stringBuilder.toString();

            StringMaker.Clear();
            StringMaker.stringBuilder.append(optionsValues);
            if (optionsValues.length() > 0)
                StringMaker.stringBuilder.append(",");
            double decideOptionValue = EquipmentCalculate.CalculateOptionValue(equipmentOptionsInfoTable.getBaseValue(), level, gradeValue, classValue, equipmentOptionsInfoTable.getPERCENT_OR_FIXEDVALUE());
            StringMaker.stringBuilder.append(decideOptionValue);
            optionsValues = StringMaker.stringBuilder.toString();
            optionsCandidateList.remove(randValue);
        }
        heroEquipment.OptionResmelting(optionsIds, optionsValues);
        HeroEquipmentInventoryDto heroEquipmentDto = new HeroEquipmentInventoryDto();
        heroEquipmentDto.InitFromDbData(heroEquipment);
        EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
        equipmentLogDto.setEquipmentLogDto("옵션재련 - "+equipmentsTable.getName()+" ["+heroEquipment.getId()+"]",heroEquipment.getId(), "변경", heroEquipmentDto);
        String log = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
        loggingService.setLogging(userId, 2, log);
        BelongingInventoryDto secondMaterialItemDto = new BelongingInventoryDto();
        secondMaterialItemDto.InitFromDbData(secondMaterialItem);
        map.put("secondMaterialItem", secondMaterialItemDto);
        map.put("item",heroEquipmentDto);
        map.put("user", user);

        return map;
    }
}
