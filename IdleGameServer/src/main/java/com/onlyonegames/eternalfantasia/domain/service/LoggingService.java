package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.HeroEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.*;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.EquipmentCalculatedDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.EquipmentOptionDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.MyInventoryInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.*;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.Logging.*;
import com.onlyonegames.eternalfantasia.domain.service.Managementtool.HeroInfo.AnalysisTalentOption;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
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
public class LoggingService {
    private final CurrencyLogRepository currencyLogRepository;
    private final EquipmentLogRepository equipmentLogRepository;
    private final BelongingInventoryLogRepository belongingInventoryLogRepository;
    private final GiftLogRepository giftLogRepository;
    private final CharacterFatigabilityLogRepository characterFatigabilityLogRepository;
//    private final TeamBuildingLogRepository teamBuildingLogRepository;
//    private final MainHeroExpLogRepository mainHeroExpLogRepository;
//    private final CharacterExpLogRepository characterExpLogRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;
    @Transactional(readOnly = true)
    public Map<String, Object> getLog(Long userId, Map<String, Object> map) {
        List<CurrencyLog> currencyLogList = currencyLogRepository.findAllByUseridUser(userId);
        List<BelongingInventoryLog> belongingInventoryLogList = belongingInventoryLogRepository.findAllByUseridUser(userId);
        List<EquipmentLog> equipmentLogList = equipmentLogRepository.findAllByUseridUser(userId);
        List<GiftLog> giftLogList = giftLogRepository.findAllByUseridUser(userId);
        List<EquipmentOptionsInfoTable> equipmentOptionsInfoTableList = gameDataTableService.OptionsInfoTableList();
        List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
        List<GiftTable> giftTableList = gameDataTableService.GiftsTableList();
        List<PassiveItemTable> passiveItemTableList = gameDataTableService.PassiveItemTableList();
        List<GiftBoxItemInfoTable> giftBoxItemInfoTableList = gameDataTableService.GiftBoxItemInfoTableList();
        List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
        List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList = gameDataTableService.EquipmentMaterialInfoTableList();
        List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
        List<ResponseLogDto.CurrencyResponseDto> responseLogDtoList = new ArrayList<>();
        List<ResponseLogDto.BelongingResponseDto> belongingResponseDtoList = new ArrayList<>();
        List<ResponseLogDto.EquipmentResponseDto> equipmentResponseDtoList = new ArrayList<>();
        List<ResponseLogDto.GiftResponseDto> giftResponseDtoList = new ArrayList<>();

        for(CurrencyLog temp:currencyLogList){
            ResponseLogDto.CurrencyResponseDto currencyResponseDto = new ResponseLogDto.CurrencyResponseDto();
            CurrencyLogDto currencyLogDto = JsonStringHerlper.ReadValueFromJson(temp.getJson_LogDetail(), CurrencyLogDto.class);
            currencyResponseDto.setCurrencyResponseDto(temp,currencyLogDto);
            responseLogDtoList.add(currencyResponseDto);
        }
        for(BelongingInventoryLog temp:belongingInventoryLogList){
            ResponseLogDto.BelongingResponseDto belongingResponseDto = new ResponseLogDto.BelongingResponseDto();
            BelongingInventoryLogDto belongingInventoryLogDto = JsonStringHerlper.ReadValueFromJson(temp.getJson_LogDetail(), BelongingInventoryLogDto.class);
            String itemName = "";
            if(belongingInventoryLogDto.getItemId() == 0) {
                itemName = "ErrorLog => 개발측 확인필요";
                belongingResponseDto.setBelongingResponseDto(temp,belongingInventoryLogDto, itemName);
                belongingResponseDtoList.add(belongingResponseDto);
            }
            switch (belongingInventoryLogDto.getItemType_id().getItemTypeName()){
                case BelongingItem_Material:
                    EquipmentMaterialInfoTable equipmentMaterialInfoTable = equipmentMaterialInfoTableList.get(belongingInventoryLogDto.getItemId()-1);//.stream().filter(i -> i.getId() == belongingInventoryLogDto.getItemId()).findAny().orElse(null);
                    if(equipmentMaterialInfoTable == null){
//                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: EquipmentMaterialInfoTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
//                        throw new MyCustomException("Fail! -> Cause: EquipmentMaterialInfoTable not find.", ResponseErrorCode.NOT_FIND_DATA);
                        itemName = "ErrorLog => 개발측 확인필요";
                        break;
                    }
                    itemName = equipmentMaterialInfoTable.getName();
                    break;
                case BelongingItem_GiftBox:
                    GiftBoxItemInfoTable giftBoxItemInfoTable = giftBoxItemInfoTableList.get(belongingInventoryLogDto.getItemId()-1);//.stream().filter(i -> i.getId() == belongingInventoryLogDto.getItemId()).findAny().orElse(null);
                    if(giftBoxItemInfoTable == null){
//                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: GiftBoxItemInfoTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
//                        throw new MyCustomException("Fail! -> Cause: GiftBoxItemInfoTable not find.", ResponseErrorCode.NOT_FIND_DATA);
                        itemName = "ErrorLog => 개발측 확인필요";
                        break;
                    }
                    itemName = giftBoxItemInfoTable.getName();
                    break;
                case BelongingItem_Spendables:
                    SpendableItemInfoTable spendableItemInfoTable = spendableItemInfoTableList.get(belongingInventoryLogDto.getItemId()-1);//.stream().filter(i -> i.getId() == belongingInventoryLogDto.getItemId()).findAny().orElse(null);
                    if(spendableItemInfoTable == null){
//                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: SpendableItemInfoTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
//                        throw new MyCustomException("Fail! -> Cause: SpendableItemInfoTable not find.", ResponseErrorCode.NOT_FIND_DATA);
                        itemName = "ErrorLog => 개발측 확인필요";
                        break;
                    }
                    itemName = spendableItemInfoTable.getName();
                    break;
                case BelongingItem_CharacterPiece:
                    BelongingCharacterPieceTable belongingCharacterPieceTable = belongingCharacterPieceTableList.get(belongingInventoryLogDto.getItemId()-1);//.stream().filter(i -> i.getId() == belongingInventoryLogDto.getItemId()).findAny().orElse(null);
                    if(belongingCharacterPieceTable == null){
//                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: BelongingCharacterPieceTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
//                        throw new MyCustomException("Fail! -> Cause: BelongingCharacterPieceTable not find.", ResponseErrorCode.NOT_FIND_DATA);
                        itemName = "ErrorLog => 개발측 확인필요";
                        break;
                    }
                    itemName = belongingCharacterPieceTable.getName();
                    break;
            }
            belongingResponseDto.setBelongingResponseDto(temp,belongingInventoryLogDto, itemName);
            belongingResponseDtoList.add(belongingResponseDto);
        }
        for(EquipmentLog temp:equipmentLogList){
            ResponseLogDto.EquipmentResponseDto equipmentResponseDto = new ResponseLogDto.EquipmentResponseDto();
            EquipmentLogDto equipmentLogDto = JsonStringHerlper.ReadValueFromJson(temp.getJson_LogDetail(), EquipmentLogDto.class);
            MyInventoryInfoDto.EquipmentItem equipmentItem = new MyInventoryInfoDto.EquipmentItem();
            if(equipmentLogDto.getHeroEquipmentInventoryDto().getItem_Id() >= 10000){//인장
                PassiveItemTable passiveItemTable = passiveItemTableList.get(equipmentLogDto.getHeroEquipmentInventoryDto().getItem_Id()-10000);//.stream().filter(i -> i.getId() == equipmentLogDto.getHeroEquipmentInventoryDto().getItem_Id())
                        //.findAny()
                        //.orElse(null);
                if (heroEquipmentsTableList == null) {
                    errorLoggingService.SetErrorLog(equipmentLogDto.getHeroEquipmentInventoryDto().getUseridUser(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: not find HeroEquipmentsTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: not find HeroEquipmentsTable.", ResponseErrorCode.NOT_FIND_DATA);
                }
                equipmentItem.SetEquipmentItem(passiveItemTable.getName(),
                        "",
                        "",
                        equipmentLogDto.getHeroEquipmentInventoryDto().getItemClass(),
                        equipmentLogDto.getHeroEquipmentInventoryDto().getLevel(),
                        equipmentLogDto.getHeroEquipmentInventoryDto().getExp(),
                        equipmentLogDto.getHeroEquipmentInventoryDto().getItem_Id(),
                        0,
                        equipmentLogDto.getHeroEquipmentInventoryDto().getId(),
                        temp.getCreateddate(),
                        null, null);
            }else{
                HeroEquipmentsTable heroEquipmentsTable = heroEquipmentsTableList.get(equipmentLogDto.getHeroEquipmentInventoryDto().getItem_Id()-1);//.stream().filter(i -> i.getId() == equipmentLogDto.getHeroEquipmentInventoryDto().getItem_Id())
                        //.findAny()
                        //.orElse(null);
                if (heroEquipmentsTable == null) {
                    errorLoggingService.SetErrorLog(equipmentLogDto.getHeroEquipmentInventoryDto().getUseridUser(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: not find HeroEquipmentsTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: not find HeroEquipmentsTable.", ResponseErrorCode.NOT_FIND_DATA);
                }
                equipmentItem.SetEquipmentItem(heroEquipmentsTable.getName(), heroEquipmentsTable.getKind(),
                        heroEquipmentsTable.getGrade(), equipmentLogDto.getHeroEquipmentInventoryDto().getItemClass(),
                        equipmentLogDto.getHeroEquipmentInventoryDto().getLevel(), equipmentLogDto.getHeroEquipmentInventoryDto().getExp(),
                        equipmentLogDto.getHeroEquipmentInventoryDto().getItem_Id(), heroEquipmentsTable.getSetInfo(), equipmentLogDto.getHeroEquipmentInventoryDto().getId(), temp.getCreateddate(),
                        getEquipmentOptions(userId, equipmentOptionsInfoTableList, heroEquipmentsTableList, equipmentLogDto.getHeroEquipmentInventoryDto(), true),
                        getEquipmentOptions(userId, equipmentOptionsInfoTableList, heroEquipmentsTableList, equipmentLogDto.getHeroEquipmentInventoryDto(), false));
            }
            equipmentResponseDto.setEquipmentResponseDto(temp, equipmentLogDto, equipmentItem);
            equipmentResponseDtoList.add(equipmentResponseDto);
        }
        for(GiftLog temp:giftLogList){
            ResponseLogDto.GiftResponseDto giftResponseDto = new ResponseLogDto.GiftResponseDto();
            GiftLogDto giftLogDto = JsonStringHerlper.ReadValueFromJson(temp.getJson_LogDetail(), GiftLogDto.class);
            GiftTable giftTable = giftTableList.stream().filter(i -> i.getCode().equals(giftLogDto.getCode())).findAny().orElse(null);
            if (giftTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: not find GiftTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: not find GiftTable.", ResponseErrorCode.NOT_FIND_DATA);
            }
            String name = giftTable.getGiftName();
            giftResponseDto.setGiftResponseDto(temp, giftLogDto, name);
            giftResponseDtoList.add(giftResponseDto);
        }

        ResponseLogDto responseLogDto = new ResponseLogDto();
        responseLogDto.setCurrencyResponseDtoList(responseLogDtoList);
        responseLogDto.setBelongingResponseDtoList(belongingResponseDtoList);
        responseLogDto.setEquipmentResponseDtoList(equipmentResponseDtoList);
        responseLogDto.setGiftResponseDtoList(giftResponseDtoList);
        map.put("ResponseLog", responseLogDto);
        return map;
    }

    public void setLogging(Long userId, int logType, String json_LogDetail){

        switch(logType){
            case 1:
                CurrencyLog currencyLog = new CurrencyLog(userId, logType, json_LogDetail);
                currencyLogRepository.save(currencyLog);
                break;
            case 2:
                EquipmentLog equipmentLog = new EquipmentLog(userId, logType, json_LogDetail);
                equipmentLogRepository.save(equipmentLog);
                break;
            case 3:
                BelongingInventoryLog belongingInventoryLog = new BelongingInventoryLog(userId, logType, json_LogDetail);
                belongingInventoryLogRepository.save(belongingInventoryLog);
                break;
            case 4:
                GiftLog giftLog = new GiftLog(userId, logType, json_LogDetail);
                giftLogRepository.save(giftLog);
                break;
            case 5:
                CharacterFatigabilityLog characterFatigabilityLog = new CharacterFatigabilityLog(userId, logType, json_LogDetail);
                characterFatigabilityLogRepository.save(characterFatigabilityLog);
                break;
//            case 6:
//                TeamBuildingLog teamBuildingLog = new TeamBuildingLog(userId, logType, json_LogDetail);
//                teamBuildingLogRepository.save(teamBuildingLog);
//                break;
//            case 7:
//                MainHeroExpLog mainHeroExpLog = new MainHeroExpLog(userId, logType, json_LogDetail);
//                mainHeroExpLogRepository.save(mainHeroExpLog);
//                break;
//            case 8:
//                CharacterExpLog characterExpLog = new CharacterExpLog(userId, logType, json_LogDetail);
//                characterExpLogRepository.save(characterExpLog);
        }
    }

    List<EquipmentOptionDto> getEquipmentOptions(Long userId,
                                                 List<EquipmentOptionsInfoTable> equipmentOptionsInfoTableList,
                                                 List<HeroEquipmentsTable> heroEquipmentsTableList,
                                                 HeroEquipmentInventoryDto heroEquipmentInventory, boolean isDefaultOptions) {
        List<EquipmentOptionDto> equipmentOptionDtoList = new ArrayList<>();
        HeroEquipmentsTable heroEquipmentsTable = heroEquipmentsTableList.stream()
                .filter(i -> i.getId() == heroEquipmentInventory.getItem_Id())
                .findAny()
                .orElse(null);
        if (heroEquipmentsTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: HeroEquipmentsTable not find Equipment.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: HeroEquipmentsTable not find Equipment.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String[] optionIdList = heroEquipmentInventory.getOptionIds().split(",");
        String[] optionValueList = heroEquipmentInventory.getOptionValues().split(",");
        EquipmentCalculatedDto.EquipmentInfo equipmentInfo = new EquipmentCalculatedDto.EquipmentInfo();
        equipmentInfo.setEquipmentInfo(heroEquipmentInventory, heroEquipmentsTable);
        if (isDefaultOptions) {
            EquipmentOptionDto equipmentOptionDto1 = new EquipmentOptionDto();
            equipmentOptionDto1.setOption(AnalysisTalentOption.GetMainHeroEquipmentDefaultAbilityKind(equipmentInfo).toString(), equipmentInfo.decideDefaultAbilityValue, false);
            equipmentOptionDtoList.add(equipmentOptionDto1);
            EquipmentOptionDto equipmentOptionDto2 = new EquipmentOptionDto();
            equipmentOptionDto2.setOption(AnalysisTalentOption.GetMainHeroEquipmentSecondAbilityKind(equipmentInfo).toString(), equipmentInfo.decideSecondAbilityValue, false);
            equipmentOptionDtoList.add(equipmentOptionDto2);
        } else {
            for (int i = 0; i < optionIdList.length; i++) {
                if (!optionIdList[i].equals("")) {
                    int index = i;
                    EquipmentOptionsInfoTable equipmentOptionsInfoTable = equipmentOptionsInfoTableList.stream().filter(j -> j.getID() == Integer.parseInt(optionIdList[index]))
                            .findAny()
                            .orElse(null);
                    if (equipmentOptionsInfoTable == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: EquipmentOptionsInfoTable not find Id.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: EquipmentOptionsInfoTable not find Id.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    String optionName = equipmentOptionsInfoTable.getOption();
                    EquipmentOptionDto equipmentOptionDto = new EquipmentOptionDto();
                    equipmentOptionDto.setOption(optionName, Float.parseFloat(optionValueList[index]), true);
                    equipmentOptionDtoList.add(equipmentOptionDto);
                }
            }
        }
        return equipmentOptionDtoList;
    }
}
