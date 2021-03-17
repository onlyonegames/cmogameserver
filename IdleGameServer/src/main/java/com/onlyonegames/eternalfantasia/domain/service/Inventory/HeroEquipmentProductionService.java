package com.onlyonegames.eternalfantasia.domain.service.Inventory;
import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;
import com.onlyonegames.eternalfantasia.domain.model.dto.*;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.EventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.BelongingInventoryLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CurrencyLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.EquipmentLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.*;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.*;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import com.onlyonegames.eternalfantasia.etc.EquipmentCalculate;
import com.onlyonegames.eternalfantasia.etc.EquipmentItemCategory;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.util.MathHelper;
import com.onlyonegames.util.StringMaker;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class HeroEquipmentProductionService {

    private final HeroEquipmentInventoryRepository heroEquipmentInventoryRepository;
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final UserRepository userRepository;
    private final MyProductionSlotRepository myProductionSlotRepository;
    private final MyProductionMasteryRepository myProductionMasteryRepository;
    private final MyProductionMaterialSettedInfoRepository myProductionMaterialSettedInfoRepository;
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;

    //장비 제작 슬롯 리스트 반환
    public Map<String, Object> RequestEquipmentProductionList(Long userId, Map<String, Object> map) {
        List<MyProductionSlot> myProductionSlotList = myProductionSlotRepository.findByUserIdUser(userId);
        List<MyProductionSlotDto> myProductionSlotDtoList = new ArrayList<>();
        for(MyProductionSlot temp : myProductionSlotList){
            MyProductionSlotDto myProductionSlotDto = new MyProductionSlotDto();
            myProductionSlotDto.InitFromDbData(temp);
            myProductionSlotDtoList.add(myProductionSlotDto);
        }
        map.put("myProductionSlotList", myProductionSlotDtoList);
        return map;
    }

    //숙련도 레벨업
    private MyProductionMastery LevelUpMyProductionMastery(Long userId, HeroEquipmentProductionExpandTable heroEquipmentProductionExpandTable) {
        MyProductionMastery myProductionMastery = myProductionMasteryRepository.findByUserIdUser(userId);
        List<ProductionMasteryInfoTable> productionMasteryInfoTableList = gameDataTableService.ProductionMasteryInfoTableList();
       // int opendGiftIndex = myProductionMastery.getOpenedGiftIndex();
      //  ProductionMasteryInfoTable productionMasteryInfoTable = productionMasteryInfoTableList.get(opendGiftIndex);
//        //현재 획득 해야 할 보상이 있는데 받지 않고 제작을 하였다면 해당 프로세스 중단
//        if(productionMasteryInfoTable.getLevelCondition() <= myProductionMastery.getMasteryLevel()) {
//            throw new MyCustomException("Fail! -> Cause: Don't LevelUp Mastery", ResponseErrorCode.DONT_LEVELUP_PRODUCTIONMASTERY);
//        }
        //숙련도가 최대인 상황에서 보상을 받지 않는 상태는 해당 프로세스 중단
        int plusLevel = heroEquipmentProductionExpandTable.getMasteryUpValue();
        if(!myProductionMastery.LevelUp(plusLevel)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.DONT_LEVELUP_PRODUCTIONMASTERY.getIntegerValue(), "Fail! -> Cause: Don't LevelUp Mastery", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Don't LevelUp Mastery", ResponseErrorCode.DONT_LEVELUP_PRODUCTIONMASTERY);
        }
        return myProductionMastery;
    }
    //장비 제작 슬롯 오픈
    public Map<String, Object> OpenSlot(Long userId, int slotNo, Map<String, Object> map) {
        if(slotNo < 1 || slotNo > 4) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_SLOT_OPEN.getIntegerValue(), "Fail! -> Cause: slotNo don't exists.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: slotNo don't exists.", ResponseErrorCode.CANT_SLOT_OPEN);
        }
        List<MyProductionSlot> myProductionSlotList = myProductionSlotRepository.findByUserIdUser(userId);
        int slotCount = myProductionSlotList.size();
        if(slotCount == 4) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_SLOT_OPEN.getIntegerValue(), "Fail! -> Cause: Can't increase slot.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't increase slot.", ResponseErrorCode.CANT_SLOT_OPEN);
        }
        MyProductionSlot myProductionSlot = myProductionSlotList.stream()
                .filter(a -> a.getSlotNo() == slotNo)
                .findAny()
                .orElse(null);
        if(myProductionSlot != null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_SLOT_OPEN.getIntegerValue(), "Fail! -> Cause: slot's already opened.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: slot's already opened.", ResponseErrorCode.CANT_SLOT_OPEN);
        }

        StringMaker.Clear();
        StringMaker.Clear();
        StringMaker.stringBuilder.append("slot");
        StringMaker.stringBuilder.append(slotCount + 1);
        String slotOpenCostCode = StringMaker.stringBuilder.toString();
        List<ProductionSlotOpenCostTable> productionSlotOpenCostTableList = gameDataTableService.ProductionSlotOpenCostTableList();
        ProductionSlotOpenCostTable productionSlotOpenCostTable = productionSlotOpenCostTableList.stream()
                .filter(a -> a.getCode().equals(slotOpenCostCode))
                .findAny()
                .orElse(null);
        if(productionSlotOpenCostTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: productionSlotOpenCostTable code Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: productionSlotOpenCostTable code Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
        }
        CurrencyLogDto currencyLogDto = new CurrencyLogDto();
        int previousValue = user.getDiamond();
        if(!user.SpendDiamond(productionSlotOpenCostTable.getNeedDiamond())) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
        }
        currencyLogDto.setCurrencyLogDto("제작 슬롯 추가 - "+slotNo+"번", "다이아", previousValue, -productionSlotOpenCostTable.getNeedDiamond(),user.getDiamond());
        String currencyLog = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
        loggingService.setLogging(userId, 1, currencyLog);

        MyProductionSlotDto newMyProductionSlotDto = new MyProductionSlotDto();
        newMyProductionSlotDto.setUserIdUser(userId);
        newMyProductionSlotDto.setSlotNo(slotNo);
        newMyProductionSlotDto.setItemId(0);
        newMyProductionSlotDto.setState(0);
        newMyProductionSlotDto.setReduceSecondFromAD(0);
        newMyProductionSlotDto.setProductionStartTime(LocalDateTime.now());
        MyProductionSlot newMyProductionSlot = newMyProductionSlotDto.ToEntity();
        newMyProductionSlot = myProductionSlotRepository.save(newMyProductionSlot);
        newMyProductionSlotDto.InitFromDbData(newMyProductionSlot);
        map.put("productionSlot", newMyProductionSlotDto);
        map.put("user", user);
        return map;
    }

    //장비 재료 셋팅
    public Map<String, Object> ProductionMaterialSet(Long userId, EquipmentItemCategory itemCategory, int materialIndex, int setCount, Map<String, Object> map) {
        MyProductionMaterialSettedInfo myProductionMaterialSettedInfo = myProductionMaterialSettedInfoRepository.findByUserIdUser(userId).orElse(null);
        if(myProductionMaterialSettedInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't find myProductionMaterialSettedInfo.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't find myProductionMaterialSettedInfo.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String materialCounts = "";
        switch (itemCategory) {

            case WEAPON:
                materialCounts = myProductionMaterialSettedInfo.getWeaponMaterialCounts();
                break;
            case ARMOR:
                materialCounts = myProductionMaterialSettedInfo.getArmorMaterialCounts();
                break;
            case HELMET:
                materialCounts = myProductionMaterialSettedInfo.getHelmetMaterialCounts();
                break;
            case ACCESSORY:
                materialCounts = myProductionMaterialSettedInfo.getAccessoryMaterialCounts();
                break;
        }
        String[] materialCountsArray = materialCounts.split(",");
        materialCountsArray[materialIndex] = Integer.toString(setCount);

        StringMaker.Clear();
        for (int i = 0; i < materialCountsArray.length; i++) {
            if (i > 0)
                StringMaker.stringBuilder.append(",");
            StringMaker.stringBuilder.append(materialCountsArray[i]);
        }
        materialCounts = StringMaker.stringBuilder.toString();

        switch (itemCategory) {

            case WEAPON:
                myProductionMaterialSettedInfo.SetWeaponMaterialCounts(materialCounts);
                break;
            case ARMOR:
                myProductionMaterialSettedInfo.SetArmorMaterialCounts(materialCounts);
                break;
            case HELMET:
                myProductionMaterialSettedInfo.SetHelmetMaterialCounts(materialCounts);
                break;
            case ACCESSORY:
                myProductionMaterialSettedInfo.SetAccessoryMaterialCounts(materialCounts);
                break;
        }
        MyProductionMaterialSettedInfoDto myProductionMaterialSettedInfoDto = new MyProductionMaterialSettedInfoDto();
        myProductionMaterialSettedInfoDto.InitFromDbData(myProductionMaterialSettedInfo);
        map.put("myProductionMaterialSettedInfo", myProductionMaterialSettedInfoDto);
        return map;
    }

    //장비 제작 시작
    public Map<String, Object> EquipmentProduction(Long userId, int slotNo, EquipmentItemCategory itemCategory, Map<String, Object> map) {

        List<MyProductionSlot> myProductionSlotList = myProductionSlotRepository.findByUserIdUser(userId);
        MyProductionSlot myProductionSlot = myProductionSlotList.stream()
                .filter(a -> a.getSlotNo() == slotNo)
                .findAny()
                .orElse(null);
        if(myProductionSlot == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: slotNo Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: slotNo Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        //장비 제작에 필요한 골드와 재료들 체크
        String heroEquipmentProductionCode = itemCategory.toString().toLowerCase();
        HeroEquipmentProductionTable heroEquipmentProductionTable = gameDataTableService.HeroEquipmentProductionTableList().stream()
                .filter(a -> a.getCode().equals(heroEquipmentProductionCode))
                .findAny()
                .orElse(null);
        if(heroEquipmentProductionTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: herEquipmentProductionTable Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: herEquipmentProductionTable Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        //골드가 충분한지 체크
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        CurrencyLogDto currencyLogDto = new CurrencyLogDto();
        int previousValue = user.getGold();
        int cost = heroEquipmentProductionTable.getNeedGold();
        if(!user.SpendGold(cost)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail -> Cause: Need More Gold", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Need More Gold", ResponseErrorCode.NEED_MORE_GOLD);
        }
        currencyLogDto.setCurrencyLogDto("제작 진행 - "+slotNo+"번 슬롯 "+itemCategory.name(), "골드", previousValue, -cost, user.getGold());
        String currencyLog = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
        loggingService.setLogging(userId, 1, currencyLog);
        //각 재료들의 갯수가 충분한지 체크
        String[] materials = heroEquipmentProductionTable.getMaterials().split(",");
        String[] needCounts = heroEquipmentProductionTable.getNeedCounts().split(",");
        int totalMaterialsCount = 0;//제작될 장비의 등급 및 품질 확률 계산에 재료수량이 들어감.
        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
        List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList = gameDataTableService.EquipmentMaterialInfoTableList();
        List<BelongingInventory> returnInventoryList = new ArrayList<>();

        MyProductionMaterialSettedInfo myProductionMaterialSettedInfo = myProductionMaterialSettedInfoRepository.findByUserIdUser(userId).orElse(null);
        if(myProductionMaterialSettedInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't find myProductionMaterialSettedInfo.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't find myProductionMaterialSettedInfo.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String materialCounts = "";
        switch (itemCategory) {

            case WEAPON:
                materialCounts = myProductionMaterialSettedInfo.getWeaponMaterialCounts();
                break;
            case ARMOR:
                materialCounts = myProductionMaterialSettedInfo.getArmorMaterialCounts();
                break;
            case HELMET:
                materialCounts = myProductionMaterialSettedInfo.getHelmetMaterialCounts();
                break;
            case ACCESSORY:
                materialCounts = myProductionMaterialSettedInfo.getAccessoryMaterialCounts();
                break;
        }
        String[] materialCountsArray = materialCounts.split(",");
        for(int i = 0; i < materials.length; i++) {
            String materialCode = materials[i];
            EquipmentMaterialInfoTable equipmentMaterialInfo = equipmentMaterialInfoTableList.stream()
                    .filter(a -> a.getCode().equals(materialCode))
                    .findAny()
                    .orElse(null);
            if(equipmentMaterialInfo == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: EquipmentmaterialInfo Can't Find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: EquipmentmaterialInfo Can't Find", ResponseErrorCode.NOT_EXIST_CODE);
            }
            BelongingInventory belongingInventoryItem = belongingInventoryList.stream()
                    .filter(a -> a.getItemId() == equipmentMaterialInfo.getId() && a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Material)
                    .findAny()
                    .orElse(null);
            if(belongingInventoryItem == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_MATERIAL.getIntegerValue(), "Fail! -> Cause: BelongingInventory Can't Find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: BelongingInventory Can't Find", ResponseErrorCode.NEED_MORE_MATERIAL);
            }
            int needCount = Integer.parseInt(needCounts[i]);
            int usingMaterialCount = Integer.parseInt(materialCountsArray[i]);
            if(needCount > usingMaterialCount) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_MATERIAL.getIntegerValue(), "Fail! -> Cause: Need more BelongingInventoryItem count", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Need more BelongingInventoryItem count", ResponseErrorCode.NEED_MORE_MATERIAL);
            }


            totalMaterialsCount += usingMaterialCount;
            BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
            belongingInventoryLogDto.setPreviousValue(belongingInventoryItem.getCount());
            if(!belongingInventoryItem.SpendItem(usingMaterialCount)) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_MATERIAL.getIntegerValue(), "Fail! -> Cause: Need more BelongingInventoryItem count", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Need more BelongingInventoryItem count", ResponseErrorCode.NEED_MORE_MATERIAL);
            }
            belongingInventoryLogDto.setBelongingInventoryLogDto("제작 진행 - "+slotNo+"번 슬롯 "+itemCategory.name(), belongingInventoryItem.getId(), belongingInventoryItem.getItemId(), belongingInventoryItem.getItemType(), -usingMaterialCount, belongingInventoryItem.getCount());
            String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, belongingLog);
            returnInventoryList.add(belongingInventoryItem);
        }

        //제작될 장비의 등급 결정
        String decideGrade = EquipmentCalculate.DecideGrade(gameDataTableService.HeroEquipmentProductionExpandTableList(), totalMaterialsCount);
        List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
        //등급과 아이템 종류에 맞는 제작 아이템 후보 리스트 추출
        List<HeroEquipmentsTable> probabilityList = EquipmentCalculate.GetProbabilityList(heroEquipmentsTableList, itemCategory, decideGrade);
        if(probabilityList.size() == 0) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find HeroEquipment", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find HeroEquipment", ResponseErrorCode.NOT_FIND_DATA);
        }

        int randValue = (int)MathHelper.Range(0, probabilityList.size());
        //최종 제작될 장비 선택
        HeroEquipmentsTable selectEquipment = probabilityList.get(randValue);
        if(!myProductionSlot.StartProduction(selectEquipment.getId())) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_USE_PRODUCTIONSLOT.getIntegerValue(), "Fail! -> Cause: this slot Can't use.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: this slot Can't use.", ResponseErrorCode.CANT_USE_PRODUCTIONSLOT);
        }
        MyProductionSlotDto myProductionSlotDto = new MyProductionSlotDto();
        myProductionSlotDto.InitFromDbData(myProductionSlot);
        List<BelongingInventoryDto> returnInventoryDtoList = new ArrayList<>();
        for(BelongingInventory temp : returnInventoryList){
            BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
            belongingInventoryDto.InitFromDbData(temp);
            returnInventoryDtoList.add(belongingInventoryDto);
        }
        map.put("productionSlot", myProductionSlotDto);
        map.put("user", user);
        map.put("changeInventoryList", returnInventoryDtoList);
        return map;
    }

    private int getTotalUsingMaterialsCount(HeroEquipmentsTable willMakeItemInfo, MyProductionMaterialSettedInfo myProductionMaterialSettedInfo) {
        int totalUsingMaterialsCount = 0;
        String equipmentItemKind = willMakeItemInfo.getKind();
        if(equipmentItemKind.equals("Sword")
                || equipmentItemKind.equals("Spear")
                || equipmentItemKind.equals("Bow")
                || equipmentItemKind.equals("Gun")
                || equipmentItemKind.equals("Wand")) {
            String materialCountsString = myProductionMaterialSettedInfo.getWeaponMaterialCounts();
            String[] materialCountsStringArray = materialCountsString.split(",");
            for(int i = 0; i < materialCountsStringArray.length; i++){
                totalUsingMaterialsCount += Integer.parseInt(materialCountsStringArray[i]);
            }
        }
        else if(equipmentItemKind.equals("Armor")) {
            String materialCountsString = myProductionMaterialSettedInfo.getArmorMaterialCounts();
            String[] materialCountsStringArray = materialCountsString.split(",");
            for(int i = 0; i < materialCountsStringArray.length; i++){
                totalUsingMaterialsCount += Integer.parseInt(materialCountsStringArray[i]);
            }
        }
        else if(equipmentItemKind.equals("Helmet")) {
            String materialCountsString = myProductionMaterialSettedInfo.getHelmetMaterialCounts();
            String[] materialCountsStringArray = materialCountsString.split(",");
            for(int i = 0; i < materialCountsStringArray.length; i++){
                totalUsingMaterialsCount += Integer.parseInt(materialCountsStringArray[i]);
            }
        }
        else if(equipmentItemKind.equals("Accessory")) {
            String materialCountsString = myProductionMaterialSettedInfo.getAccessoryMaterialCounts();
            String[] materialCountsStringArray = materialCountsString.split(",");
            for(int i = 0; i < materialCountsStringArray.length; i++){
                totalUsingMaterialsCount += Integer.parseInt(materialCountsStringArray[i]);
            }
        }
        return totalUsingMaterialsCount;
    }

    //제작 완료된 아이템 획득(인벤토리로 추가)
    public Map<String, Object> RequestProductedEquipment(Long userId, int slotNo, Map<String, Object> map) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<HeroEquipmentInventory> heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(userId);
        if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
        }

        List<MyProductionSlot> myProductionSlotList = myProductionSlotRepository.findByUserIdUser(userId);
        MyProductionSlot myProductionSlot = myProductionSlotList.stream()
                .filter(a -> a.getSlotNo() == slotNo)
                .findAny()
                .orElse(null);
        if(myProductionSlot == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: slotNo Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: slotNo Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        //슬롯 사용 중인지 체크
        if(myProductionSlot.getState() == 0) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.EMPTY_SLOT.getIntegerValue(), "Fail! -> Cause: slot is empty", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: slot is empty", ResponseErrorCode.EMPTY_SLOT);
        }

        List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
        int productionItemId = myProductionSlot.getItemId();
        HeroEquipmentsTable willMakeItemInfo = heroEquipmentsTableList.stream()
                .filter(a -> a.getId() == productionItemId)
                .findAny()
                .orElse(null);
        if(willMakeItemInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: HeroEquipmentsTable Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: HeroEquipmentsTable Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<HeroEquipmentProductionExpandTable> heroEquipmentProductionExpandTableList = gameDataTableService.HeroEquipmentProductionExpandTableList();
        HeroEquipmentProductionExpandTable heroEquipmentProductionExpandTable = heroEquipmentProductionExpandTableList.stream()
                .filter(a -> a.getGrade().equals(willMakeItemInfo.getGrade()))
                .findAny()
                .orElse(null);
        if(heroEquipmentProductionExpandTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: heroEquipmentProductionExpandTable Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: heroEquipmentProductionExpandTable Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        int productionSecond = heroEquipmentProductionExpandTable.getProductionSecond();
        if(!myProductionSlot.ReturnProduction(productionSecond)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_YET_PRODUCTIONTEIM.getIntegerValue(), "Fail! -> Cause: Not yet Production Time", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Not yet Production Time", ResponseErrorCode.NOT_YET_PRODUCTIONTEIM);
        }
        //숙련도 레벨업 체크
        MyProductionMastery myProductionMastery = LevelUpMyProductionMastery(userId, heroEquipmentProductionExpandTable);

        MyProductionMaterialSettedInfo myProductionMaterialSettedInfo = myProductionMaterialSettedInfoRepository.findByUserIdUser(userId).orElse(null);
        if(myProductionMaterialSettedInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't find myProductionMaterialSettedInfo.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't find myProductionMaterialSettedInfo.", ResponseErrorCode.NOT_FIND_DATA);
        }

        //사용한 재료 체크
        int totalUsingMaterialsCount = getTotalUsingMaterialsCount(willMakeItemInfo, myProductionMaterialSettedInfo);

        //실제 장비 생성
        HeroEquipmentInventory generatedItem = EquipmentCalculate.ProductEquipment(userId, willMakeItemInfo, totalUsingMaterialsCount, gameDataTableService.OptionsInfoTableList(), gameDataTableService.HeroEquipmentClassProbabilityTableList());
        generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
        myProductionSlot = myProductionSlotRepository.save(myProductionSlot);
        MyProductionSlotDto myProductionSlotDto = new MyProductionSlotDto();
        myProductionSlotDto.InitFromDbData(myProductionSlot);
        HeroEquipmentInventoryDto generatedItemDto = new HeroEquipmentInventoryDto();
        generatedItemDto.InitFromDbData(generatedItem);
        EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
        equipmentLogDto.setEquipmentLogDto("제작완료 - "+slotNo+"번 ["+generatedItem.getId()+"]", generatedItem.getId(), "추가", generatedItemDto);
        String equipmentLog = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
        loggingService.setLogging(userId, 2, equipmentLog);
        MyProductionMasteryDto myProductionMasteryDto = new MyProductionMasteryDto();
        myProductionMasteryDto.InitFromDbData(myProductionMastery);
        map.put("productionSlot", myProductionSlotDto);
        map.put("generatedItem", generatedItemDto);
        map.put("myProductionMastery", myProductionMasteryDto);

        /*업적 : 체크 준비*/
        MyMissionsData myMissionsData = myMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyMissionData = myMissionsData.getJson_saveDataValue();
        MissionsDataDto myMissionsDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyMissionData, MissionsDataDto.class);
        boolean changedMissionsData = false;
        /* 업적 : 장비 제작하기 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.PRODUCTION_EQUIPMENT.name(),"empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
        /* 업적 : 장비 획득 (특정 품질) 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(), generatedItem.getItemClass(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
        /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), willMakeItemInfo.getGrade(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
        /*업적 : 미션 데이터 변경점 적용*/
        if(changedMissionsData) {
            jsonMyMissionData = JsonStringHerlper.WriteValueAsStringFromData(myMissionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMyMissionData);
            map.put("exchange_myMissionsData", true);
            map.put("myMissionsDataDto", myMissionsDataDto);
            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());
        }

        /*패스 업적 : 체크 준비*/
        MyEternalPassMissionsData myEternalPassMissionsData = myEternalPassMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myEternalPassMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEternalPassMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyEternalPassMissionData = myEternalPassMissionsData.getJson_saveDataValue();
        EventMissionDataDto myEternalPassMissionDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyEternalPassMissionData, EventMissionDataDto.class);
        boolean changedEternalPassMissionsData = false;
        /* 패스 업적 : 장비 제작하기 미션 체크*/
        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.PRODUCTION_EQUIPMENT.name(),"empty", gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
        /* 패스 업적 : 미션 데이터 변경점 적용*/
        if(changedEternalPassMissionsData) {
            jsonMyEternalPassMissionData = JsonStringHerlper.WriteValueAsStringFromData(myEternalPassMissionDataDto);
            myEternalPassMissionsData.ResetSaveDataValue(jsonMyEternalPassMissionData);
            map.put("exchange_myEternalPassMissionsData", true);
            map.put("myEternalPassMissionsDataDto", myEternalPassMissionDataDto);
            map.put("myEternalPassLastDailyMissionClearTime", myEternalPassMissionsData.getLastDailyMissionClearTime());
            map.put("myEternalPassLastWeeklyMissionClearTime", myEternalPassMissionsData.getLastWeeklyMissionClearTime());
        }
        return map;
    }
    //제작 중인 장비 즉시 완료
    public Map<String, Object> RequestProductedEquipmentImmediately(Long userId, int slotNo, Map<String, Object> map) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<HeroEquipmentInventory> heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(userId);
        if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
        }

        List<MyProductionSlot> myProductionSlotList = myProductionSlotRepository.findByUserIdUser(userId);
        MyProductionSlot myProductionSlot = myProductionSlotList.stream()
                .filter(a -> a.getSlotNo() == slotNo)
                .findAny()
                .orElse(null);
        if(myProductionSlot == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: slotNo Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: slotNo Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        //슬롯 사용 중인지 체크
        if(myProductionSlot.getState() == 0) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.EMPTY_SLOT.getIntegerValue(), "Fail! -> Cause: slot is empty", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: slot is empty", ResponseErrorCode.EMPTY_SLOT);
        }

        List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
        int productionItemId = myProductionSlot.getItemId();
        HeroEquipmentsTable willMakeItemInfo = heroEquipmentsTableList.stream()
                .filter(a -> a.getId() == productionItemId)
                .findAny()
                .orElse(null);
        if(willMakeItemInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: HeroEquipmentsTable Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: HeroEquipmentsTable Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<HeroEquipmentProductionExpandTable> heroEquipmentProductionExpandTableList = gameDataTableService.HeroEquipmentProductionExpandTableList();
        HeroEquipmentProductionExpandTable heroEquipmentProductionExpandTable = heroEquipmentProductionExpandTableList.stream()
                .filter(a -> a.getGrade().equals(willMakeItemInfo.getGrade()))
                .findAny()
                .orElse(null);
        if(heroEquipmentProductionExpandTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: heroEquipmentProductionExpandTable Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: heroEquipmentProductionExpandTable Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();

        //즉시 제작권이 있다면 즉시 제작권을 소모 시킨다.
        SpendableItemInfoTable ticketOfDirectProductionEquipment = spendableItemInfoTableList.stream()
                .filter(a -> a.getSpendableType().equals("TICKET_DIRECT_PRODUCTION_EQUIPMENT"))
                .findAny()
                .orElse(null);
        if(ticketOfDirectProductionEquipment == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ticketOfDirectProductionEquipment Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: ticketOfDirectProductionEquipment Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
        BelongingInventory myTicketOfDirectProductionEquipment = belongingInventoryList.stream()
                .filter(a -> a.getItemId() == ticketOfDirectProductionEquipment.getId() && a.getItemType().getId() == 3L)
                .findAny()
                .orElse(null);
        if(myTicketOfDirectProductionEquipment != null && myTicketOfDirectProductionEquipment.getCount() > 0) {
            BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
            belongingInventoryLogDto.setPreviousValue(myTicketOfDirectProductionEquipment.getCount());
            myTicketOfDirectProductionEquipment.SpendItem(1);
            belongingInventoryLogDto.setBelongingInventoryLogDto("제작 즉시 완료 - "+slotNo+"번 슬롯", myTicketOfDirectProductionEquipment.getId(), myTicketOfDirectProductionEquipment.getItemId(), myTicketOfDirectProductionEquipment.getItemType(), -1, myTicketOfDirectProductionEquipment.getCount());
            String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, belongingLog);
            BelongingInventoryDto myTicketOfDirectProductionEquipmentDto = new BelongingInventoryDto();
            myTicketOfDirectProductionEquipmentDto.InitFromDbData(myTicketOfDirectProductionEquipment);
            map.put("ticketSpend", true);
            map.put("myTicketOfDirectProductionEquipment", myTicketOfDirectProductionEquipmentDto);
        }
        else {
            int productionSecond = heroEquipmentProductionExpandTable.getProductionSecond();
            long remainProductionSecond = myProductionSlot.RemainProductionSecond(productionSecond);
            int costDiamond = 0;
            if(remainProductionSecond > 0) {
                /*1분당 다이아 소비 10개*/
                costDiamond = (int) (remainProductionSecond / 60); // * diamond;
                /*나머지값이 있으면 나머지에 상관없이 1분으로 환산해서 소비 다이아 개수 10개 추가*/
                int remainSecond = (int)(remainProductionSecond % 60);
                if(remainSecond > 0)
                    costDiamond += 1;
            }
            CurrencyLogDto currencyLogDto = new CurrencyLogDto();
            int previousValue = user.getDiamond();
            if(!user.SpendDiamond(costDiamond)) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
            }
            currencyLogDto.setCurrencyLogDto("제작 즉시 완료 - "+slotNo+"번 슬롯", "다이아", previousValue, -costDiamond, user.getDiamond());
            String currencyLog = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
            loggingService.setLogging(userId, 1, currencyLog);
        }

        //숙련도 레벨업 체크
        MyProductionMastery myProductionMastery = LevelUpMyProductionMastery(userId, heroEquipmentProductionExpandTable);
        //즉시 슬롯 오픈 및 인벤토리 아이템 추가
        myProductionSlot.ReturnProductionImmediately();

        MyProductionMaterialSettedInfo myProductionMaterialSettedInfo = myProductionMaterialSettedInfoRepository.findByUserIdUser(userId).orElse(null);
        if(myProductionMaterialSettedInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't find myProductionMaterialSettedInfo.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't find myProductionMaterialSettedInfo.", ResponseErrorCode.NOT_FIND_DATA);
        }

        //사용한 재료 체크
        int totalUsingMaterialsCount = getTotalUsingMaterialsCount(willMakeItemInfo, myProductionMaterialSettedInfo);

        HeroEquipmentInventory generatedItem = EquipmentCalculate.ProductEquipment(userId, willMakeItemInfo, totalUsingMaterialsCount, gameDataTableService.OptionsInfoTableList(), gameDataTableService.HeroEquipmentClassProbabilityTableList());
        generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
        MyProductionSlotDto  myProductionSlotDto = new MyProductionSlotDto();
        myProductionSlotDto.InitFromDbData(myProductionSlot);
        HeroEquipmentInventoryDto generatedItemDto = new HeroEquipmentInventoryDto();
        generatedItemDto.InitFromDbData(generatedItem);
        EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
        equipmentLogDto.setEquipmentLogDto("제작완료 - "+slotNo+"번 슬롯 ["+generatedItem.getId()+"]", generatedItem.getId(), "추가", generatedItemDto);
        String equipmentLog = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
        loggingService.setLogging(userId, 2, equipmentLog);
        MyProductionMasteryDto myProductionMasteryDto = new MyProductionMasteryDto();
        myProductionMasteryDto.InitFromDbData(myProductionMastery);
        map.put("productionSlot", myProductionSlotDto);
        map.put("generatedItem", generatedItemDto);
        map.put("myProductionMastery", myProductionMasteryDto);
        map.put("user", user);

        /*업적 : 체크 준비*/
        MyMissionsData myMissionsData = myMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyMissionData = myMissionsData.getJson_saveDataValue();
        MissionsDataDto myMissionsDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyMissionData, MissionsDataDto.class);
        boolean changedMissionsData = false;
        /* 업적 : 장비 제작하기 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.PRODUCTION_EQUIPMENT.name(),"empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
        /* 업적 : 장비 획득 (특정 품질) 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(), generatedItem.getItemClass(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
        /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), willMakeItemInfo.getGrade(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
        /*업적 : 미션 데이터 변경점 적용*/
        if(changedMissionsData) {
            jsonMyMissionData = JsonStringHerlper.WriteValueAsStringFromData(myMissionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMyMissionData);
            map.put("exchange_myMissionsData", true);
            map.put("myMissionsDataDto", myMissionsDataDto);
            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());
        }
        /*패스 업적 : 체크 준비*/
        MyEternalPassMissionsData myEternalPassMissionsData = myEternalPassMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myEternalPassMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEternalPassMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyEternalPassMissionData = myEternalPassMissionsData.getJson_saveDataValue();
        EventMissionDataDto myEternalPassMissionDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyEternalPassMissionData, EventMissionDataDto.class);
        boolean changedEternalPassMissionsData = false;
        /* 패스 업적 : 장비 제작하기 미션 체크*/
        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.PRODUCTION_EQUIPMENT.name(),"empty", gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
        /* 패스 업적 : 미션 데이터 변경점 적용*/
        if(changedEternalPassMissionsData) {
            jsonMyEternalPassMissionData = JsonStringHerlper.WriteValueAsStringFromData(myEternalPassMissionDataDto);
            myEternalPassMissionsData.ResetSaveDataValue(jsonMyEternalPassMissionData);
            map.put("exchange_myEternalPassMissionsData", true);
            map.put("myEternalPassMissionsDataDto", myEternalPassMissionDataDto);
            map.put("myEternalPassLastDailyMissionClearTime", myEternalPassMissionsData.getLastDailyMissionClearTime());
            map.put("myEternalPassLastWeeklyMissionClearTime", myEternalPassMissionsData.getLastWeeklyMissionClearTime());
        }
        return map;
    }
    //제작 중인 장비 광고 보기후 제작시간 줄이기
    public Map<String, Object> DecreaseProductTimeFromAD(Long userId, int slotNo, Map<String, Object> map) {
        List<MyProductionSlot> myProductionSlotList = myProductionSlotRepository.findByUserIdUser(userId);
        MyProductionSlot myProductionSlot = myProductionSlotList.stream()
                .filter(a -> a.getSlotNo() == slotNo)
                .findAny()
                .orElse(null);
        if(myProductionSlot == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: slotNo Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: slotNo Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        //슬롯 사용 중인지 체크
        if(myProductionSlot.getState() == 0) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.EMPTY_SLOT.getIntegerValue(), "Fail! -> Cause: slot is empty", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: slot is empty", ResponseErrorCode.EMPTY_SLOT);
        }
        List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
        int productionItemId = myProductionSlot.getItemId();
        HeroEquipmentsTable willMakeItemInfo = heroEquipmentsTableList.stream()
                .filter(a -> a.getId() == productionItemId)
                .findAny()
                .orElse(null);
        if(willMakeItemInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: HeroEquipmentsTable Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: HeroEquipmentsTable Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<HeroEquipmentProductionExpandTable> heroEquipmentProductionExpandTableList = gameDataTableService.HeroEquipmentProductionExpandTableList();
        HeroEquipmentProductionExpandTable heroEquipmentProductionExpandTable = heroEquipmentProductionExpandTableList.stream()
                .filter(a -> a.getGrade().equals(willMakeItemInfo.getGrade()))
                .findAny()
                .orElse(null);
        if(heroEquipmentProductionExpandTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: heroEquipmentProductionExpandTable Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: heroEquipmentProductionExpandTable Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        int productionSecond = heroEquipmentProductionExpandTable.getProductionSecond();
        long remainProductionSecond = myProductionSlot.RemainProductionSecond(productionSecond);
        int reduceSecond = (int)Math.round(remainProductionSecond * 0.5);
        if(!myProductionSlot.ReduceProductionTime(reduceSecond)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ANYMORE_REDUCE_PRODUCTIONTIME.getIntegerValue(), "Fail! -> Cause: Anymore reduce productionTime.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Anymore reduce productionTime.", ResponseErrorCode.ANYMORE_REDUCE_PRODUCTIONTIME);
        }
        MyProductionSlotDto myProductionSlotDto = new MyProductionSlotDto();
        myProductionSlotDto.InitFromDbData(myProductionSlot);
        map.put("myProductionSlot", myProductionSlotDto);
        return map;
    }
    //제작 숙련도에 의해 획득할 보상이 생겼을때 해당 보상 획득 요청
    public Map<String, Object> RequestMasteryReward(Long userId, int receiveIndex, Map<String, Object> map) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<HeroEquipmentInventory> heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(userId);
        if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
        }

        MyProductionMastery myProductionMastery = myProductionMasteryRepository.findByUserIdUser(userId);
        List<ProductionMasteryInfoTable> productionMasteryInfoTableList = gameDataTableService.ProductionMasteryInfoTableList();

        ProductionMasteryInfoTable productionMasteryInfoTable = productionMasteryInfoTableList.get(receiveIndex);

        //현재 숙련도 레벨이 다음 보상 아이템 장비를 받기위한 숙련도 레벨보다 작다면 해당 프로세스 중단
        if(productionMasteryInfoTable.getLevelCondition() > myProductionMastery.getMasteryLevel()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_YET_PRODUCTIONMASTERYLEVEL.getIntegerValue(), "Fail! -> Cause: Not yet productionMasteryLevel.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Not yet productionMasteryLevel.", ResponseErrorCode.NOT_YET_PRODUCTIONMASTERYLEVEL);
        }
        int giftBoxItemId = productionMasteryInfoTable.getGiftBoxItemId();
        List<GiftBoxItemInfoTable> giftBoxItemInfoTableList= gameDataTableService.GiftBoxItemInfoTableList();
        GiftBoxItemInfoTable giftBoxItemInfoTable = giftBoxItemInfoTableList.stream()
                .filter(a -> a.getId() == giftBoxItemId)
                .findAny()
                .orElse(null);
        if(giftBoxItemInfoTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find GiftBoxItemInfoTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find GiftBoxItemInfoTable.", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
        //등급과 아이템 종류에 맞는 제작 아이템 후보 리스트 추출
        List<HeroEquipmentsTable> probabilityList = EquipmentCalculate.GetProbabilityList(heroEquipmentsTableList, EquipmentItemCategory.ALL, giftBoxItemInfoTable.getGrade());
        int randValue = (int)MathHelper.Range(0, probabilityList.size());
        //최종 제작될 장비 선택
        HeroEquipmentsTable selectEquipment = probabilityList.get(randValue);
        HeroEquipmentInventory generatedItem = EquipmentCalculate.CreateEquipment(userId, selectEquipment, gameDataTableService.OptionsInfoTableList(), gameDataTableService.HeroEquipmentClassProbabilityTableList());
        if(!myProductionMastery.ReceiveReward(receiveIndex)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_RECEIVED_PRODUCTIONMASTERY_REWARD.getIntegerValue(), "Fail! -> Cause: Already received reward.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Already received reward.", ResponseErrorCode.ALREADY_RECEIVED_PRODUCTIONMASTERY_REWARD);
        }

        generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
        HeroEquipmentInventoryDto generatedItemDto = new HeroEquipmentInventoryDto();
        generatedItemDto.InitFromDbData(generatedItem);
        EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
        equipmentLogDto.setEquipmentLogDto("제작 숙련도 보상 획득 ["+generatedItem.getId()+"]", generatedItem.getId(), "추가", generatedItemDto);
        String equipmentLog = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
        loggingService.setLogging(userId, 2, equipmentLog);
        MyProductionMasteryDto myProductionMasteryDto = new MyProductionMasteryDto();
        myProductionMasteryDto.InitFromDbData(myProductionMastery);
        map.put("generatedItem", generatedItemDto);
        map.put("myProductionMastery", myProductionMasteryDto);

        /*업적 : 체크 준비*/
        MyMissionsData myMissionsData = myMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyMissionData = myMissionsData.getJson_saveDataValue();
        MissionsDataDto myMissionsDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyMissionData, MissionsDataDto.class);
        boolean changedMissionsData = false;

        /* 업적 : 장비 획득 (특정 품질) 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(), generatedItem.getItemClass(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
        /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), selectEquipment.getGrade(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
        /*업적 : 미션 데이터 변경점 적용*/
        if(changedMissionsData) {
            jsonMyMissionData = JsonStringHerlper.WriteValueAsStringFromData(myMissionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMyMissionData);
            map.put("exchange_myMissionsData", true);
            map.put("myMissionsDataDto", myMissionsDataDto);
            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());
        }
        return map;
    }
}
