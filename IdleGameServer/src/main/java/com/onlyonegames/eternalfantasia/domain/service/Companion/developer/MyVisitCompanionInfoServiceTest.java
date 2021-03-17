package com.onlyonegames.eternalfantasia.domain.service.Companion.developer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.TavernVisitCompanionInfoData.TavernVisitCompanionInfoDataList;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyVisitCompanionInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.herostable;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyVisitCompanionInfoRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.util.MathHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
public class MyVisitCompanionInfoServiceTest {
    private final MyVisitCompanionInfoRepository myVisitCompanionInfoRepository;
    private final MyCharactersRepository myCharactersRepository;
    private final UserRepository userRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;

    @Autowired
    public MyVisitCompanionInfoServiceTest(MyVisitCompanionInfoRepository myVisitCompanionInfoRepository, MyCharactersRepository myCharactersRepository, UserRepository userRepository, GameDataTableService gameDataTableService, ErrorLoggingService errorLoggingService) {
        this.myVisitCompanionInfoRepository = myVisitCompanionInfoRepository;
        this.myCharactersRepository = myCharactersRepository;
        this.userRepository = userRepository;
        this.gameDataTableService = gameDataTableService;
        this.errorLoggingService = errorLoggingService;
    }

    public Map<String, Object> GetMyVisitCompanionInfo(Long userId,  Map<String, Object> map){
        MyVisitCompanionInfo myVisitCompanionInfo = myVisitCompanionInfoRepository.findByUseridUser(userId)
                .orElse(null);
        if(myVisitCompanionInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[Developer] Fail! -> Cause: MyVisitCompanionInfo not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyVisitCompanionInfo not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);

        ResetVisitCompanionInfo(myVisitCompanionInfo, myCharactersList, gameDataTableService.HerosTableList());

        String visitCompanionInfo = myVisitCompanionInfo.getVisitCompanionInfo();

        map.put("myVisitCompanionInfo", myVisitCompanionInfo);
        map.put("recycled", false);
        return map;
    }

    public static boolean ResetVisitCompanionInfo(MyVisitCompanionInfo myVisitCompanionInfo, List<MyCharacters> myCharactersList, List<herostable> herosTableList) {
       // if(!myVisitCompanionInfo.IsRecycleTime(432000/*총 5일에 대한 초*/)){


        TavernVisitCompanionInfoDataList tavernVisitCompanionInfoDataList = new TavernVisitCompanionInfoDataList();
        tavernVisitCompanionInfoDataList.visitList = new ArrayList<>();
        List<herostable> probabilityList = new ArrayList<>();

        for(herostable hero : herosTableList){
            boolean gotchaed = false;
            for(MyCharacters myCharacter : myCharactersList) {
                if(hero.getCode().equals(myCharacter.getCodeHerostable()) && myCharacter.isGotcha()){
                    gotchaed = true;
                    break;
                }
            }
            if(!gotchaed) {
                probabilityList.add(hero);
            }
        }

        int probabilitySize = probabilityList.size();
        if(probabilitySize == 0)
            return false;

        int minGenerateCount = 1;
        int maxGenerateCount = probabilitySize > 6 ? 6 : probabilitySize;

        //int willGenerateCount = (int) MathHelper.Range(minGenerateCount, maxGenerateCount);
        int willGenerateCount = maxGenerateCount;//(int) MathHelper.Range(minGenerateCount, maxGenerateCount);

        for(int i = 0; i < willGenerateCount; i++) {
            int randValue = (int) MathHelper.Range(0, probabilityList.size());
            herostable hero = probabilityList.get(randValue);
            TavernVisitCompanionInfoDataList.TavernVisitCompanionInfoData tavernVisitCompanionInfoData = new TavernVisitCompanionInfoDataList.TavernVisitCompanionInfoData(hero);
            tavernVisitCompanionInfoDataList.visitList.add(tavernVisitCompanionInfoData);
            probabilityList.remove(randValue);
        }
        tavernVisitCompanionInfoDataList.relationHeroIndex = -1;
        String tavernVisitCompanionInfoData = JsonStringHerlper.WriteValueAsStringFromData(tavernVisitCompanionInfoDataList);

        myVisitCompanionInfo.ResetVisitCompanionInfo(tavernVisitCompanionInfoData);
        return true;
    }

    public Map<String, Object> ClearInfo(Long userId, Map<String, Object> map){
        MyVisitCompanionInfo myVisitCompanionInfo = myVisitCompanionInfoRepository.findByUseridUser(userId)
                .orElse(null);
        if(myVisitCompanionInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[Developer] Fail! -> Cause: MyVisitCompanionInfo not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyVisitCompanionInfo not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);

        for(MyCharacters myCharacters : myCharactersList) {
            if(myCharacters.getCodeHerostable().equals("cr_000") || (myCharacters.getCodeHerostable().equals("hero"))){
                continue;
            }
            myCharacters.NonGotcha();
        }

        ResetVisitCompanionInfo(myVisitCompanionInfo, myCharactersList, gameDataTableService.HerosTableList());

        map.put("myVisitCompanionInfo", myVisitCompanionInfo);
        map.put("recycled", false);
        return map;
    }
}
