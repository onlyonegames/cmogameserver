package com.onlyonegames.eternalfantasia.domain.service.Companion;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;
import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.CostumeDto.CostumeDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.BelongingInventoryLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CurrencyLogDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyCostumeInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.LegionCostumeTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.SpendableItemInfoTable;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyCostumeInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.BelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@AllArgsConstructor
@Service
@Transactional
public class MyCostumeInventoryService {
    private final MyCostumeInventoryRepository myCostumeInventoryRepository;
    private final UserRepository userRepository;
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;

    public Map<String, Object> EquipCostume(Long userId, int costumeId, String ownerCode, Map<String, Object> map) {

        List<LegionCostumeTable> legionCostumeTableList = gameDataTableService.CostumeTableList();
        List<LegionCostumeTable> legionCostumeTableListByOwner = legionCostumeTableList.stream()
                .filter(a -> a.getOwnerCode().equals(ownerCode))
                .collect(Collectors.toList());
        if(legionCostumeTableListByOwner == null || legionCostumeTableListByOwner.size() == 0) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: legionCostumeTableListByOwner not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: legionCostumeTableListByOwner not find", ResponseErrorCode.NOT_FIND_DATA);
        }

        MyCostumeInventory myCostumeInventory = myCostumeInventoryRepository.findByUseridUser(userId)
                .orElse(null);
        if(myCostumeInventory == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myCostumeInventory not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myCostumeInventory not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        CostumeDtosList costumeDtosList = JsonStringHerlper.ReadValueFromJson(myCostumeInventory.getJson_CostumeInventory(), CostumeDtosList.class);
        CostumeDtosList.CostumeDto willEquipCostume = costumeDtosList.hasCostumeIdList.stream()
                .filter(a-> a.costumeId == costumeId)
                .findAny()
                .orElse(null);
        if(willEquipCostume == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_EQUIP_COSTUME.getIntegerValue(), "Fail -> Cause: Can't Equip the costume", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Can't Equip the costume", ResponseErrorCode.CANT_EQUIP_COSTUME);
        }

        for(LegionCostumeTable legionCostumeTable : legionCostumeTableListByOwner) {
            //해당 코스튬 장착
            if(willEquipCostume.costumeId == legionCostumeTable.getId()) {
                willEquipCostume.Equip();
                continue;
            }
            //나머지 코스튬은 비장착 셋팅
            for(CostumeDtosList.CostumeDto costumeDto : costumeDtosList.hasCostumeIdList) {
                if(legionCostumeTable.getId() == costumeDto.costumeId) {
                    costumeDto.UnEquip();
                    break;
                }
            }
        }
        String jsonCostumeInventory = JsonStringHerlper.WriteValueAsStringFromData(costumeDtosList);
        myCostumeInventory.ResetCostumeInventory(jsonCostumeInventory);

        map.put("hasCostumeIdList", costumeDtosList.hasCostumeIdList);
        return map;
    }

    public Map<String, Object> BuyCostume(Long userId, int costumeId, Map<String, Object> map) {

        //코스튬 티켓 확인.
        List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
        SpendableItemInfoTable spendableItemInfoTable = spendableItemInfoTableList.stream()
                .filter(a -> a.getCode().equals("costume_ticket"))
                .findAny()
                .orElse(null);
        if(spendableItemInfoTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: spendableItemInfoTable Can't Find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: spendableItemInfoTable Can't Find", ResponseErrorCode.NOT_EXIST_CODE);
        }

        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
        BelongingInventory belongingInventoryItem = belongingInventoryList.stream()
                .filter(a -> a.getItemId() == spendableItemInfoTable.getId() && a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                .findAny()
                .orElse(null);
        List<LegionCostumeTable> legionCostumeTableList = gameDataTableService.CostumeTableList();
        LegionCostumeTable legionCostumeTable = legionCostumeTableList.stream()
                .filter(a -> a.getId() == costumeId)
                .findAny()
                .orElse(null);
        if(legionCostumeTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: LegionCostumeTable not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: LegionCostumeTable not find", ResponseErrorCode.NOT_FIND_DATA);
        }
        if(belongingInventoryItem != null && belongingInventoryItem.getCount() > 0){
            //코스튬 티켓 소모해서 코스튬 구매
            BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
            belongingInventoryLogDto.setPreviousValue(belongingInventoryItem.getCount());
            belongingInventoryItem.SpendItem(1);
            belongingInventoryLogDto.setBelongingInventoryLogDto(legionCostumeTable.getName()+" 코스튬 구매", belongingInventoryItem.getId(), belongingInventoryItem.getItemId(), belongingInventoryItem.getItemType(), -1, belongingInventoryItem.getCount());
            String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, log);
            BelongingInventoryDto belongingInventoryItemDto = new BelongingInventoryDto();
            belongingInventoryItemDto.InitFromDbData(belongingInventoryItem);
            map.put("costume_ticket", belongingInventoryItemDto);
        }
        else {
            //다이아 소모해서 코스튬 구매

            User user = userRepository.findById(userId).orElse(null);
            if(user == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
            }

            int cost = legionCostumeTable.getPrice();
            CurrencyLogDto currencyLogDto = new CurrencyLogDto();
            int previousValue = user.getDiamond();
            if(!user.SpendDiamond(cost)) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail -> Cause: Need More Diamond", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail -> Cause: Need More Diamond", ResponseErrorCode.NEED_MORE_DIAMOND);
            }
            currencyLogDto.setCurrencyLogDto(legionCostumeTable.getName()+" 코스튬 구매", "다이아", previousValue, -cost, user.getDiamond());
            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
            loggingService.setLogging(userId, 1, log);
            map.put("user", user);
        }


        MyCostumeInventory myCostumeInventory = myCostumeInventoryRepository.findByUseridUser(userId)
                .orElse(null);
        if(myCostumeInventory == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myCostumeInventory not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myCostumeInventory not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        CostumeDtosList costumeDtosList = JsonStringHerlper.ReadValueFromJson(myCostumeInventory.getJson_CostumeInventory(), CostumeDtosList.class);
        CostumeDtosList.CostumeDto costumeDto = costumeDtosList.hasCostumeIdList.stream()
                .filter(a -> a.costumeId == costumeId)
                .findAny()
                .orElse(null);
        if(costumeDto == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: costumeDto Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: costumeDto Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        if(!costumeDto.Buy()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.AREADY_HAS_COSTUME.getIntegerValue(), "Fail -> Cause: Aready has a costume", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Aready has a costume", ResponseErrorCode.AREADY_HAS_COSTUME);
        }

        String jsonCostumeInventory = JsonStringHerlper.WriteValueAsStringFromData(costumeDtosList);
        myCostumeInventory.ResetCostumeInventory(jsonCostumeInventory);

        map.put("hasCostumeIdList", costumeDtosList.hasCostumeIdList);
        return map;
    }
}
