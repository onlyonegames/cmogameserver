package com.onlyonegames.eternalfantasia.domain.service.Inventory;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyEquipmentDeckDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyEquipmentDeck;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.HeroEquipmentsTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.PassiveItemTable;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.HeroEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyEquipmentDeckRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.etc.EquipmentCalculate;
import com.onlyonegames.eternalfantasia.etc.EquipmentItemCategory;
import com.onlyonegames.util.StringMaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
public class MyEquipmentDeckSetterService {

    private final MyEquipmentDeckRepository myEquipmentDeckRepository;
    private final HeroEquipmentInventoryRepository heroEquipmentInventoryRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;
    @Autowired
    public MyEquipmentDeckSetterService(MyEquipmentDeckRepository myEquipmentDeckRepository, HeroEquipmentInventoryRepository heroEquipmentInventoryRepository, GameDataTableService gameDataTableService, ErrorLoggingService errorLoggingService) {
        this.myEquipmentDeckRepository = myEquipmentDeckRepository;
        this.heroEquipmentInventoryRepository = heroEquipmentInventoryRepository;
        this.gameDataTableService = gameDataTableService;
        this.errorLoggingService = errorLoggingService;
    }
    //특정 장비 장착
    public Map<String, Object> Equip(Long userId, Long itemInventoryId, int deckNo, Map<String, Object> map) {

        MyEquipmentDeck myEquipmentDeck = myEquipmentDeckRepository.findByUserIdUser(userId)
                .orElse(null);
        if(myEquipmentDeck == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyEquipmentDeck not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEquipmentDeck not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        HeroEquipmentInventory equipmentItem = heroEquipmentInventoryRepository.findByIdAndUseridUser(itemInventoryId, userId)
                .orElse(null);
        if(equipmentItem == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: HeroEquipmentInventory not find itemInventoryId Or userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: HeroEquipmentInventory not find itemInventoryId Or userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        HeroEquipmentsTable selectedItem = null;
        int itemId = equipmentItem.getItem_Id();
        List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
        for(HeroEquipmentsTable heroEquipment : heroEquipmentsTableList) {
            if(heroEquipment.getId() == itemId) {
                selectedItem = heroEquipment;
                break;
            }
        }
        if(selectedItem == null) {
            StringMaker.Clear();
            StringMaker.stringBuilder.append("Fail! -> Cause: Can't Find EquipmentItem(");
            StringMaker.stringBuilder.append(itemId);
            StringMaker.stringBuilder.append(")");
            throw new MyCustomException(StringMaker.stringBuilder.toString(), ResponseErrorCode.NOT_FIND_DATA);
        }

        String selectedItemKind = selectedItem.getKind();
        myEquipmentDeck.Equip(deckNo, equipmentItem.getId(), selectedItemKind);
        MyEquipmentDeckDto myEquipmentDeckDto = new MyEquipmentDeckDto();
        myEquipmentDeckDto.InitFromDbData(myEquipmentDeck);
        map.put("equipmentDeck", myEquipmentDeckDto);
        return map;
    }
    //특정 장비 해제
    public Map<String, Object> UnEquip(Long userId, Long itemInventoryId, int deckNo, Map<String, Object> map) {
        MyEquipmentDeck myEquipmentDeck = myEquipmentDeckRepository.findByUserIdUser(userId)
                .orElse(null);
        if(myEquipmentDeck == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyEquipmentDeck not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEquipmentDeck not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        HeroEquipmentInventory equipmentItem = heroEquipmentInventoryRepository.findByIdAndUseridUser(itemInventoryId, userId)
                .orElse(null);
        if(equipmentItem == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: HeroEquipmentInventory not find itemInventoryId Or userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: HeroEquipmentInventory not find itemInventoryId Or userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        HeroEquipmentsTable selectedItem = null;
        int itemId = equipmentItem.getItem_Id();
        List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
        for(HeroEquipmentsTable heroEquipment : heroEquipmentsTableList) {
            if(heroEquipment.getId() == itemId) {
                selectedItem = heroEquipment;
                break;
            }
        }
        if(selectedItem == null) {
            StringMaker.Clear();
            StringMaker.stringBuilder.append("Fail! -> Cause: Can't Find EquipmentItem(");
            StringMaker.stringBuilder.append(itemId);
            StringMaker.stringBuilder.append(")");
            throw new MyCustomException(StringMaker.stringBuilder.toString(), ResponseErrorCode.NOT_FIND_DATA);
        }

        String selectedItemKind = selectedItem.getKind();
        myEquipmentDeck.UnEquip(deckNo, selectedItemKind);
        MyEquipmentDeckDto myEquipmentDeckDto = new MyEquipmentDeckDto();
        myEquipmentDeckDto.InitFromDbData(myEquipmentDeck);
        map.put("equipmentDeck", myEquipmentDeckDto);
        return map;
    }
    //자동 장착
    public Map<String, Object> AutoEquip(Long userId, int deckNo, Map<String, Object> map) {
        MyEquipmentDeck myEquipmentDeck = myEquipmentDeckRepository.findByUserIdUser(userId)
                .orElse(null);
        if(myEquipmentDeck == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyEquipmentDeck not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEquipmentDeck not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<HeroEquipmentInventory> userHeroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(userId);
        HeroEquipmentInventory equipmentInventory = MaxValueEquipmentSelect(userHeroEquipmentInventoryList, EquipmentItemCategory.WEAPON);
        if(equipmentInventory != null)
            myEquipmentDeck.Equip(deckNo, equipmentInventory.getId(), EquipmentItemCategory.WEAPON);

        equipmentInventory = MaxValueEquipmentSelect(userHeroEquipmentInventoryList, EquipmentItemCategory.ARMOR);
        if(equipmentInventory != null)
            myEquipmentDeck.Equip(deckNo, equipmentInventory.getId(), EquipmentItemCategory.ARMOR);

        equipmentInventory = MaxValueEquipmentSelect(userHeroEquipmentInventoryList, EquipmentItemCategory.HELMET);
        if(equipmentInventory != null)
            myEquipmentDeck.Equip(deckNo, equipmentInventory.getId(), EquipmentItemCategory.HELMET);

        equipmentInventory = MaxValueEquipmentSelect(userHeroEquipmentInventoryList, EquipmentItemCategory.ACCESSORY);
        if(equipmentInventory != null)
            myEquipmentDeck.Equip(deckNo, equipmentInventory.getId(), EquipmentItemCategory.ACCESSORY);

        //인장 아이템
        List<PassiveItemTable> passiveItemTableList = gameDataTableService.PassiveItemTableList();
        List<PassiveItemTable> passiveItemTableCopyList = new ArrayList<>();
        passiveItemTableCopyList.addAll(passiveItemTableList);
        List<HeroEquipmentInventory> userHeroEquipmentInventoryListCopyList = new ArrayList<>(userHeroEquipmentInventoryList);
        userHeroEquipmentInventoryListCopyList.removeIf(i -> i.getItem_Id()<10000);

        if(deckNo == 1) {
            Long passiveItemId1 = myEquipmentDeck.getFirstDeckPassiveInventoryId1();
            Long passiveItemId2 = myEquipmentDeck.getFirstDeckPassiveInventoryId2();

            userHeroEquipmentInventoryListCopyList.removeIf(i -> i.getId().equals(passiveItemId1));
            userHeroEquipmentInventoryListCopyList.removeIf(i -> i.getId().equals(passiveItemId2));
            if(myEquipmentDeck.getFirstDeckPassiveInventoryId1() == 0L) {

                for(PassiveItemTable passiveItemTable : passiveItemTableCopyList) {
                    HeroEquipmentInventory heroEquipmentInventory = userHeroEquipmentInventoryListCopyList.stream()
                            .filter( a -> a.getItem_Id() == passiveItemTable.getId())
                            .findAny()
                            .orElse(null);
                    if(heroEquipmentInventory != null) {
                        myEquipmentDeck.PassiveItemEquip(deckNo, heroEquipmentInventory.getId(), 0);
                        passiveItemTableCopyList.remove(passiveItemTable);
                        break;
                    }
                }
            }
            if(myEquipmentDeck.getFirstDeckPassiveInventoryId2() == 0L) {
                for(PassiveItemTable passiveItemTable : passiveItemTableCopyList) {
                    HeroEquipmentInventory heroEquipmentInventory = userHeroEquipmentInventoryListCopyList.stream()
                            .filter( a -> a.getItem_Id() == passiveItemTable.getId())
                            .findAny()
                            .orElse(null);
                    if(heroEquipmentInventory != null) {
                        myEquipmentDeck.PassiveItemEquip(deckNo, heroEquipmentInventory.getId(), 1);
                        passiveItemTableCopyList.remove(passiveItemTable);
                        break;
                    }
                }
            }
        }
        else if(deckNo == 2) {
            Long passiveItemId1 = myEquipmentDeck.getSecondDeckPassiveInventoryId1();
            Long passiveItemId2 = myEquipmentDeck.getSecondDeckPassiveInventoryId2();
            userHeroEquipmentInventoryListCopyList.removeIf(i -> i.getId().equals(passiveItemId1));
            userHeroEquipmentInventoryListCopyList.removeIf(i -> i.getId().equals(passiveItemId2));
            if(myEquipmentDeck.getSecondDeckPassiveInventoryId1() == 0L) {
                for(PassiveItemTable passiveItemTable : passiveItemTableCopyList) {
                    HeroEquipmentInventory heroEquipmentInventory = userHeroEquipmentInventoryListCopyList.stream()
                            .filter( a -> a.getItem_Id() == passiveItemTable.getId())
                            .findAny()
                            .orElse(null);
                    if(heroEquipmentInventory != null) {
                        myEquipmentDeck.PassiveItemEquip(deckNo, heroEquipmentInventory.getId(), 0);
                        passiveItemTableCopyList.remove(passiveItemTable);
                        break;
                    }
                }
            }
            if(myEquipmentDeck.getSecondDeckPassiveInventoryId2() == 0L) {
                for(PassiveItemTable passiveItemTable : passiveItemTableCopyList) {
                    HeroEquipmentInventory heroEquipmentInventory = userHeroEquipmentInventoryListCopyList.stream()
                            .filter( a -> a.getItem_Id() == passiveItemTable.getId())
                            .findAny()
                            .orElse(null);
                    if(heroEquipmentInventory != null) {
                        myEquipmentDeck.PassiveItemEquip(deckNo, heroEquipmentInventory.getId(), 1);
                        passiveItemTableCopyList.remove(passiveItemTable);
                        break;
                    }
                }
            }
        }
        else if(deckNo == 3) {
            Long passiveItemId1 = myEquipmentDeck.getThirdDeckPassiveInventoryId1();
            Long passiveItemId2 = myEquipmentDeck.getThirdDeckPassiveInventoryId2();
            userHeroEquipmentInventoryListCopyList.removeIf(i -> i.getId().equals(passiveItemId1));
            userHeroEquipmentInventoryListCopyList.removeIf(i -> i.getId().equals(passiveItemId2));
            if(myEquipmentDeck.getThirdDeckPassiveInventoryId1() == 0L) {
                for(PassiveItemTable passiveItemTable : passiveItemTableCopyList) {
                    HeroEquipmentInventory heroEquipmentInventory = userHeroEquipmentInventoryListCopyList.stream()
                            .filter( a -> a.getItem_Id() == passiveItemTable.getId())
                            .findAny()
                            .orElse(null);
                    if(heroEquipmentInventory != null) {
                        myEquipmentDeck.PassiveItemEquip(deckNo, heroEquipmentInventory.getId(), 0);
                        passiveItemTableCopyList.remove(passiveItemTable);
                        break;
                    }
                }
            }
            if(myEquipmentDeck.getThirdDeckPassiveInventoryId2() == 0L) {
                for(PassiveItemTable passiveItemTable : passiveItemTableCopyList) {
                    HeroEquipmentInventory heroEquipmentInventory = userHeroEquipmentInventoryListCopyList.stream()
                            .filter( a -> a.getItem_Id() == passiveItemTable.getId())
                            .findAny()
                            .orElse(null);
                    if(heroEquipmentInventory != null) {
                        myEquipmentDeck.PassiveItemEquip(deckNo, heroEquipmentInventory.getId(), 1);
                        passiveItemTableCopyList.remove(passiveItemTable);
                        break;
                    }
                }
            }
        }
        MyEquipmentDeckDto myEquipmentDeckDto = new MyEquipmentDeckDto();
        myEquipmentDeckDto.InitFromDbData(myEquipmentDeck);
        map.put("equipmentDeck", myEquipmentDeckDto);
        return map;
    }

    //인장 장착
    public Map<String, Object> PassiveItemEquip(Long userId, Long itemInventoryId, int deckNo, int passiveSlotIndex, Map<String, Object> map) {
        MyEquipmentDeck myEquipmentDeck = myEquipmentDeckRepository.findByUserIdUser(userId)
                .orElse(null);
        if(myEquipmentDeck == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyEquipmentDeck not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEquipmentDeck not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        HeroEquipmentInventory equipmentItem = heroEquipmentInventoryRepository.findByIdAndUseridUser(itemInventoryId, userId)
                .orElse(null);
        if(equipmentItem == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: HeroEquipmentInventory not find itemInventoryId Or userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: HeroEquipmentInventory not find itemInventoryId Or userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        PassiveItemTable selectedItem = null;
        int itemId = equipmentItem.getItem_Id();
        List<PassiveItemTable> passiveItemTableList = gameDataTableService.PassiveItemTableList();
        for(PassiveItemTable passiveItem : passiveItemTableList) {
            if(passiveItem.getId() == itemId) {
                selectedItem = passiveItem;
                break;
            }
        }
        if(selectedItem == null) {
            StringMaker.Clear();
            StringMaker.stringBuilder.append("Fail! -> Cause: Can't Find PassiveItemTable(");
            StringMaker.stringBuilder.append(itemId);
            StringMaker.stringBuilder.append(")");
            throw new MyCustomException(StringMaker.stringBuilder.toString(), ResponseErrorCode.NOT_FIND_DATA);
        }

        myEquipmentDeck.PassiveItemEquip(deckNo, equipmentItem.getId(), passiveSlotIndex);
        MyEquipmentDeckDto myEquipmentDeckDto = new MyEquipmentDeckDto();
        myEquipmentDeckDto.InitFromDbData(myEquipmentDeck);
        map.put("equipmentDeck", myEquipmentDeckDto);
        return map;
    }
    //인장 해제
    public Map<String, Object> PassiveItemUnEquip(Long userId, Long itemInventoryId, int deckNo, Map<String, Object> map) {
        MyEquipmentDeck myEquipmentDeck = myEquipmentDeckRepository.findByUserIdUser(userId)
                .orElse(null);
        if(myEquipmentDeck == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyEquipmentDeck not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEquipmentDeck not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        HeroEquipmentInventory equipmentItem = heroEquipmentInventoryRepository.findByIdAndUseridUser(itemInventoryId, userId)
                .orElse(null);
        if(equipmentItem == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: HeroEquipmentInventory not find itemInventoryId Or userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: HeroEquipmentInventory not find itemInventoryId Or userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        PassiveItemTable selectedItem = null;
        int itemId = equipmentItem.getItem_Id();
        List<PassiveItemTable> passiveItemTableList = gameDataTableService.PassiveItemTableList();
        for(PassiveItemTable passiveItem : passiveItemTableList) {
            if(passiveItem.getId() == itemId) {
                selectedItem = passiveItem;
                break;
            }
        }
        if(selectedItem == null) {
            StringMaker.Clear();
            StringMaker.stringBuilder.append("Fail! -> Cause: Can't Find EquipmentItem(");
            StringMaker.stringBuilder.append(itemId);
            StringMaker.stringBuilder.append(")");
            throw new MyCustomException(StringMaker.stringBuilder.toString(), ResponseErrorCode.NOT_FIND_DATA);
        }

        myEquipmentDeck.PassiveItemUnEquip(deckNo, itemInventoryId);
        MyEquipmentDeckDto myEquipmentDeckDto = new MyEquipmentDeckDto();
        myEquipmentDeckDto.InitFromDbData(myEquipmentDeck);
        map.put("equipmentDeck", myEquipmentDeckDto);
        return map;
    }

    HeroEquipmentInventory MaxValueEquipmentSelect(List<HeroEquipmentInventory> userEquipmentInventoryList, EquipmentItemCategory equipmentItemCategory) {
        HeroEquipmentInventory returnItem = null;
        List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
        List<HeroEquipmentsTable> equipmentsTableListByCategory = GetEquipmentsTableListByCategory(heroEquipmentsTableList, equipmentItemCategory);
        float previousValue = 0;
        for( HeroEquipmentInventory heroEquipmentInventory : userEquipmentInventoryList) {
            for( HeroEquipmentsTable heroEquipmentsTable : equipmentsTableListByCategory) {
                if(heroEquipmentsTable.getId() == heroEquipmentInventory.getItem_Id()) {
                    if (previousValue < heroEquipmentInventory.getDecideDefaultAbilityValue())
                    {
                        previousValue = heroEquipmentInventory.getDecideDefaultAbilityValue();
                        returnItem = heroEquipmentInventory;
                    }
                    break;
                }
            }
        }
        return returnItem;
    }

    //모두 해제
    public Map<String, Object> AllUnEquip(Long userId, int deckNo, Map<String, Object> map) {
        MyEquipmentDeck myEquipmentDeck = myEquipmentDeckRepository.findByUserIdUser(userId)
                .orElse(null);
        if(myEquipmentDeck == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyEquipmentDeck not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEquipmentDeck not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        myEquipmentDeck.AllUnEquip(deckNo);
        MyEquipmentDeckDto myEquipmentDeckDto = new MyEquipmentDeckDto();
        myEquipmentDeckDto.InitFromDbData(myEquipmentDeck);
        map.put("equipmentDeck", myEquipmentDeckDto);
        return map;
    }
    //사용중인 덱 번호 수정 요청
    public Map<String, Object> ChangeCurrentDeckNo(Long userId, int willUsingDeckNo, Map<String, Object> map) {
        MyEquipmentDeck myEquipmentDeck = myEquipmentDeckRepository.findByUserIdUser(userId)
                .orElse(null);
        if(myEquipmentDeck == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyEquipmentDeck not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEquipmentDeck not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        myEquipmentDeck.SetCurrentUseDeckNo(willUsingDeckNo);
        MyEquipmentDeckDto myEquipmentDeckDto = new MyEquipmentDeckDto();
        myEquipmentDeckDto.InitFromDbData(myEquipmentDeck);
        map.put("equipmentDeck", myEquipmentDeckDto);
        return map;
    }

    private List<HeroEquipmentsTable> GetEquipmentsTableListByCategory(List<HeroEquipmentsTable> heroEquipmentsTableList, EquipmentItemCategory equipmentItemCategory) {
        List<HeroEquipmentsTable> probabilityList = null;
        switch (equipmentItemCategory) {
            case WEAPON:
                probabilityList = heroEquipmentsTableList.stream()
                        .filter(a -> a.getKind().equals("Sword")
                                || a.getKind().equals("Spear")
                                || a.getKind().equals("Bow")
                                || a.getKind().equals("Gun")
                                || a.getKind().equals("Wand"))
                        .collect(Collectors.toList());
                break;
            case ARMOR:
                probabilityList = heroEquipmentsTableList.stream()
                        .filter(a -> a.getKind().equals("Armor"))
                        .collect(Collectors.toList());
                break;
            case HELMET:
                probabilityList = heroEquipmentsTableList.stream()
                        .filter(a -> a.getKind().equals("Helmet"))
                        .collect(Collectors.toList());
                break;
            case ACCESSORY:
                probabilityList = heroEquipmentsTableList.stream()
                        .filter(a -> a.getKind().equals("Accessory"))
                        .collect(Collectors.toList());
                break;
            case ALL:
                probabilityList = heroEquipmentsTableList;
                break;
        }
        return probabilityList;
    }
}

