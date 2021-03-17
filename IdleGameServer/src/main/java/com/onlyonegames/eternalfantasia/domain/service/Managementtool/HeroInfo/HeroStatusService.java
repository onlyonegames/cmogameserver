package com.onlyonegames.eternalfantasia.domain.service.Managementtool.HeroInfo;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.EquipmentCalculatedDto.EquipmentInfo;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.EquipmentCalculatedDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.HeroStatusDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyEquipmentDeck;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.EquipmentOptionsInfoTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.HeroEquipmentsTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.HerostableRepository;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.herostable;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.HeroEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyEquipmentDeckRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.util.MathHelper;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class HeroStatusService {
    private final GameDataTableService gameDataTableService;
    private final MyCharactersRepository myCharactersRepository;
    private final MyEquipmentDeckRepository myEquipmentDeckRepository;
    private final HeroEquipmentInventoryRepository heroEquipmentInventoryRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> getHeroStatus(Long userId, Map<String, Object> map){
        herostable herostable = gameDataTableService.HerosTableList()
                .stream()
                .filter(i -> i.getCode().equals("hero"))
                .findAny()
                .orElse(null);
        if(herostable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: Cant Find Data.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Cant Find Data.", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
        List<EquipmentOptionsInfoTable> equipmentOptionsInfoTables = gameDataTableService.OptionsInfoTableList();
        MyCharacters myCharacters = myCharactersRepository.findByUseridUserAndCodeHerostable(userId, "hero")
                .orElse(null);
        if(myCharacters == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: Can't Find Character.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find Character.", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyEquipmentDeck myEquipmentDeck = myEquipmentDeckRepository.findByUserIdUser(userId)
                .orElse(null);
        if(myEquipmentDeck == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: Can't Find EquipmentDeckInfo.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find EquipmentDeckInfo.", ResponseErrorCode.NOT_FIND_DATA);
        }
        HeroStatusDto calculatedStatus = new HeroStatusDto();
        HeroStatusDto growthStatus = new HeroStatusDto();
        List<Long> deckList = new ArrayList<>();

        switch(myEquipmentDeck.getCurrentUseDeckNo()){
            case 1:
                if(!myEquipmentDeck.getFirstDeckAccessoryInventoryId().equals(0L))
                    deckList.add(myEquipmentDeck.getFirstDeckAccessoryInventoryId());
                if(!myEquipmentDeck.getFirstDeckArmorInventoryId().equals(0L))
                    deckList.add(myEquipmentDeck.getFirstDeckArmorInventoryId());
                if(!myEquipmentDeck.getFirstDeckHelmetInventoryId().equals(0L))
                    deckList.add(myEquipmentDeck.getFirstDeckHelmetInventoryId());
                if(!myEquipmentDeck.getFirstDeckWeaponInventoryId().equals(0L))
                    deckList.add(myEquipmentDeck.getFirstDeckWeaponInventoryId());
                break;
            case 2:
                if(!myEquipmentDeck.getSecondDeckAccessoryInventoryId().equals(0L))
                    deckList.add(myEquipmentDeck.getSecondDeckAccessoryInventoryId());
                if(!myEquipmentDeck.getSecondDeckArmorInventoryId().equals(0L))
                    deckList.add(myEquipmentDeck.getSecondDeckArmorInventoryId());
                if(!myEquipmentDeck.getSecondDeckHelmetInventoryId().equals(0L))
                    deckList.add(myEquipmentDeck.getSecondDeckHelmetInventoryId());
                if(!myEquipmentDeck.getSecondDeckWeaponInventoryId().equals(0L))
                    deckList.add(myEquipmentDeck.getSecondDeckWeaponInventoryId());
                break;
            case 3:
                if(!myEquipmentDeck.getThirdDeckAccessoryInventoryId().equals(0L))
                    deckList.add(myEquipmentDeck.getThirdDeckAccessoryInventoryId());
                if(!myEquipmentDeck.getThirdDeckArmorInventoryId().equals(0L))
                    deckList.add(myEquipmentDeck.getThirdDeckArmorInventoryId());
                if(!myEquipmentDeck.getThirdDeckHelmetInventoryId().equals(0L))
                    deckList.add(myEquipmentDeck.getThirdDeckHelmetInventoryId());
                if(!myEquipmentDeck.getThirdDeckWeaponInventoryId().equals(0L))
                    deckList.add(myEquipmentDeck.getThirdDeckWeaponInventoryId());
                break;
        }
        EquipmentCalculatedDto equipmentCalculatedDto = new EquipmentCalculatedDto();
        List<HeroEquipmentInventory> heroEquipmentInventoryList = heroEquipmentInventoryRepository.findAllById(deckList);
        List<EquipmentInfo> temp = new ArrayList<>();
        for(HeroEquipmentInventory heroEquipmentInventory: heroEquipmentInventoryList){
            HeroEquipmentsTable heroEquipmentsTable = heroEquipmentsTableList
                    .stream()
                    .filter(i -> i.getId() == heroEquipmentInventory.getItem_Id())
                    .findAny()
                    .orElse(null);
            if(heroEquipmentsTable == null){
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: Can't Find HeroEquipmentsTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Can't Find HeroEquipmentsTable.", ResponseErrorCode.NOT_FIND_DATA);
            }
            EquipmentInfo equipmentInfo = new EquipmentInfo();
            equipmentInfo.setEquipmentInfo(heroEquipmentInventory, heroEquipmentsTable);
            temp.add(equipmentInfo);
        }
        equipmentCalculatedDto.setEquipmentInfoList(temp);


        HeroCalculate.CalculateStatusFromMainHero(userId, myCharacters.getLevel(),growthStatus,calculatedStatus,herostable,equipmentCalculatedDto, equipmentOptionsInfoTables);
        calculatedStatus.setLevel(myCharacters.getLevel(),herostable);
        calculatedStatus.setDPS();

        map.put("HeroStatus", calculatedStatus);
        return map;
    }
}
