package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.*;
import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.*;
import com.onlyonegames.eternalfantasia.domain.repository.Logging.*;
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
        List<ResponseLogDto.CurrencyResponseDto> responseLogDtoList = new ArrayList<>();
        List<ResponseLogDto.GiftResponseDto> giftResponseDtoList = new ArrayList<>();

        for(CurrencyLog temp:currencyLogList){
            ResponseLogDto.CurrencyResponseDto currencyResponseDto = new ResponseLogDto.CurrencyResponseDto();
            CurrencyLogDto currencyLogDto = JsonStringHerlper.ReadValueFromJson(temp.getJson_LogDetail(), CurrencyLogDto.class);
            currencyResponseDto.setCurrencyResponseDto(temp,currencyLogDto);
            responseLogDtoList.add(currencyResponseDto);
        }

        ResponseLogDto responseLogDto = new ResponseLogDto();
        responseLogDto.setCurrencyResponseDtoList(responseLogDtoList);
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
}
