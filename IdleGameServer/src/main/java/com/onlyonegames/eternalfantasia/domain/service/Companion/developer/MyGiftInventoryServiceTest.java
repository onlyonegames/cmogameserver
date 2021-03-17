package com.onlyonegames.eternalfantasia.domain.service.Companion.developer;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.GiftItemDto.GiftItemDtosList;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyGiftInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.GiftTable;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyGiftInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
public class MyGiftInventoryServiceTest {
    private final MyGiftInventoryRepository myGiftInventoryRepository;
    private final MyCharactersRepository myCharactersRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;
    @Autowired
    public MyGiftInventoryServiceTest(MyGiftInventoryRepository myGiftInventoryRepository, MyCharactersRepository myCharactersRepository, GameDataTableService gameDataTableService, ErrorLoggingService errorLoggingService) {
        this.myGiftInventoryRepository = myGiftInventoryRepository;
        this.myCharactersRepository = myCharactersRepository;
        this.gameDataTableService = gameDataTableService;
        this.errorLoggingService = errorLoggingService;
    }

    public Map<String, Object> ResetGifts(Long userId, int initCount, List<String> giftCodeList, Map<String, Object> map){

        MyGiftInventory myGiftInventory = myGiftInventoryRepository.findByUseridUser(userId)
                .orElse(null);
        if(myGiftInventory == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[Developer] Fail! -> Cause: myGiftInventory not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myGiftInventory not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String inventoryInfosString = myGiftInventory.getInventoryInfos();
        GiftItemDtosList giftItemInventorys = JsonStringHerlper.ReadValueFromJson(inventoryInfosString, GiftItemDtosList.class);

        if(giftCodeList == null) {
            for(GiftItemDtosList.GiftItemDto inventoryItemDto : giftItemInventorys.giftItemDtoList) {
                inventoryItemDto.count = initCount;
            }
        }
        else
        {
            for(GiftItemDtosList.GiftItemDto inventoryItemDto : giftItemInventorys.giftItemDtoList) {
                String giftCode = giftCodeList.stream()
                        .filter(a -> a.equals(inventoryItemDto.code))
                        .findAny()
                        .orElse(null);
                if(giftCode != null)
                {
                    inventoryItemDto.count = initCount;
                }
            }
        }


        inventoryInfosString = JsonStringHerlper.WriteValueAsStringFromData(giftItemInventorys);
        myGiftInventory.ResetInventoryInfos(inventoryInfosString);
        map.put("myGiftInventoryInfos", giftItemInventorys.giftItemDtoList);
        return map;
    }

    public Map<String, Object> ResetGoodfeelingAllHero(Long userId, Map<String, Object> map){

        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);

        for(MyCharacters myCharacter : myCharactersList) {
            //myCharacter.ResetGoodFeeling();
        }
        map.put("myCharactersList",myCharactersList);
        return map;
    }

    public Map<String, Object> ResetGoodfeelingByHero(Long userId, String heroCode, Map<String, Object> map){

        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
        MyCharacters myCharacter = myCharactersList.stream()
                .filter(a -> a.getCodeHerostable().equals(heroCode))
                .findAny()
                .orElse(null);
        if(myCharacter == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[Developer] Fail! -> Cause: CharacterCode not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: CharacterCode not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        //myCharacter.ResetGoodFeeling();

        map.put("myCharacter",myCharacter);
        return map;
    }

}
