package com.onlyonegames.eternalfantasia.domain.service.Dungeon;

import com.google.common.base.Strings;
import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.CostumeDto.CostumeDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceDto.LinkforceOpenDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceWeaponDto.LinkweaponInfoDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyCharactersBaseDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.MyArenaUsersDataDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyCostumeInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyLinkforceInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyLinkweaponInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.InfiniteTowerRecords;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyEquipmentDeck;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyMainHeroSkill;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyTeamInfo;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyCostumeInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyLinkforceInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyLinkweaponInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.InfinityTowerRecordsRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.HeroEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyEquipmentDeckRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyMainHeroSkillRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyTeamInfoRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
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
public class InfiniteTowerService {
    private final InfinityTowerRecordsRepository infinityTowerRecordsRepository;
    //상대방 팀덱 케릭터들 레벨과 어빌리티 링크 레벨 얻어오기
    private final MyCharactersRepository myCharactersRepository;
    private final MyTeamInfoRepository myTeamInfoRepository;
    ///////////////////////동료 정보/////////////////////////////////////////////////////////
    //상대방 동료 케릭터들 링크 포스 정보 얻어오기
    private final MyLinkforceInfoRepository myLinkforceInfoRepository;
    //상대방 동료 케릭터들 링크 웨폰 정보 얻어오기
    private final MyLinkweaponInfoRepository myLinkweaponInfoRepository;
    //상대방 동료 케릭터들 코스튬 정보 얻어오기
    private final MyCostumeInventoryRepository myCostumeInventoryRepository;
    ///////////////////////메인 영웅 정보//////////////////////////////////////////////////////
    //상대방 메인 영웅의 장착된 장비덱 정보
    private final MyEquipmentDeckRepository myEquipmentDeckRepository;
    //상대방 메인 영웅의 덱에 장착된 실제 장비들 데이터 얻어오기
    private final HeroEquipmentInventoryRepository heroEquipmentInventoryRepository;
    //상대방 메인 영웅의 스킬 정보
    private final MyMainHeroSkillRepository myMainHeroSkillRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> GetInfinityTowerRecordsList(Map map) {
        List<InfiniteTowerRecords> infiniteTowerRecordsList = infinityTowerRecordsRepository.findAll();

        map.put("infiniteTowerRecordsList", infiniteTowerRecordsList);
        return map;
    }

    //천공의 계단 레코드에 등록된 유저들을 눌렀을때 나오는 유저 정보.
    public Map<String, Object> GetUserInfo(Long userId, Long matchUserId, Map<String, Object> map) {

        MyTeamInfo enemyTeamInfo = myTeamInfoRepository.findByUseridUser(matchUserId);

        String userArenaTeamDeckIds = enemyTeamInfo.getInfiniteTowerTeam();

        String[] userTeamArray = userArenaTeamDeckIds.split(",");

        List<Long> characterIdsList = new ArrayList<>();
        for(String characterIdStr : userTeamArray) {
            if(Strings.isNullOrEmpty(characterIdStr))
                continue;
            Long characterId = Long.parseLong(characterIdStr);
            characterIdsList.add(characterId);
        }
        //상대방 아레나 팀덱
        List<MyCharacters> tempEnemyTeamDeck = myCharactersRepository.findAllById(characterIdsList);
        List<MyCharacters> enemyTeamDeck = new ArrayList<>();
        for(Long characterId : characterIdsList) {
            if(characterId == 0L){
                MyCharactersBaseDto myCharactersBaseDto = new MyCharactersBaseDto();
                myCharactersBaseDto.setId(0L);
                myCharactersBaseDto.setCodeHerostable("none");
                enemyTeamDeck.add(myCharactersBaseDto.ToEntity(0));
            }
            else{
                MyCharacters myCharacter = tempEnemyTeamDeck.stream().filter(a -> a.getId().equals(characterId)).findAny().orElse(null);
                enemyTeamDeck.add(myCharacter);
            }
        }
        //상대방 동료 케릭터의 링크포스 정보
        MyLinkforceInfo enemyLinkforceInfoList = myLinkforceInfoRepository.findByUseridUser(matchUserId)
                .orElse(null);
        if(enemyLinkforceInfoList == null) {
            errorLoggingService.SetErrorLog(matchUserId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myLinkforceInfoList not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myLinkforceInfoList not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        LinkforceOpenDtosList enemyLinkforceOpenDtosList = JsonStringHerlper.ReadValueFromJson(enemyLinkforceInfoList.getJson_LinkforceInfos(), LinkforceOpenDtosList.class);

        //상대방 동료 케릭터의 링크웨폰 정보
        MyLinkweaponInfo enemyLinkweaponInfoList = myLinkweaponInfoRepository.findByUseridUser(matchUserId)
                .orElse(null);
        if(enemyLinkweaponInfoList == null) {
            errorLoggingService.SetErrorLog(matchUserId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myLinkweaponInfoList not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myLinkweaponInfoList not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        LinkweaponInfoDtosList enemyLinkweaponInfoDtosList = JsonStringHerlper.ReadValueFromJson(enemyLinkweaponInfoList.getJson_LinkweaponRevolution(), LinkweaponInfoDtosList.class);

        //상대방 동료 케릭터의 코스튬 정보
        MyCostumeInventory enemyCostumeInventory = myCostumeInventoryRepository.findByUseridUser(matchUserId)
                .orElse(null);
        if(enemyCostumeInventory == null) {
            errorLoggingService.SetErrorLog(matchUserId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myCostumeInventory not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myCostumeInventory not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        CostumeDtosList enemyCostumeDtosList = JsonStringHerlper.ReadValueFromJson(enemyCostumeInventory.getJson_CostumeInventory(), CostumeDtosList.class);


        //상대방 메인 영웅의 장착된 장비덱 정보
        MyEquipmentDeck enemyEquipmentDeck = myEquipmentDeckRepository.findByUserIdUser(matchUserId)
                .orElse(null);
        if(enemyEquipmentDeck == null) {
            errorLoggingService.SetErrorLog(matchUserId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyEquipmentDeck not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEquipmentDeck not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

//        List<Long> enemyEquipmentsList = new ArrayList<>();
//        int currentUseDeckNo = enemyEquipmentDeck.getCurrentUseDeckNo();
//        switch (currentUseDeckNo) {
//            case 1:
//                enemyEquipmentsList.add(enemyEquipmentDeck.getFirstDeckWeaponInventoryId());
//                enemyEquipmentsList.add(enemyEquipmentDeck.getFirstDeckArmorInventoryId());
//                enemyEquipmentsList.add(enemyEquipmentDeck.getFirstDeckHelmetInventoryId());
//                enemyEquipmentsList.add(enemyEquipmentDeck.getFirstDeckAccessoryInventoryId());
//                break;
//            case 2:
//                enemyEquipmentsList.add(enemyEquipmentDeck.getSecondDeckWeaponInventoryId());
//                enemyEquipmentsList.add(enemyEquipmentDeck.getSecondDeckAccessoryInventoryId());
//                enemyEquipmentsList.add(enemyEquipmentDeck.getSecondDeckHelmetInventoryId());
//                enemyEquipmentsList.add(enemyEquipmentDeck.getSecondDeckAccessoryInventoryId());
//                break;
//            case 3:
//                enemyEquipmentsList.add(enemyEquipmentDeck.getThirdDeckWeaponInventoryId());
//                enemyEquipmentsList.add(enemyEquipmentDeck.getThirdDeckArmorInventoryId());
//                enemyEquipmentsList.add(enemyEquipmentDeck.getThirdDeckHelmetInventoryId());
//                enemyEquipmentsList.add(enemyEquipmentDeck.getThirdDeckAccessoryInventoryId());
//                break;
//        }
        //상대방 메인 영웅의 덱에 장착된 실제 장비들 데이터
        List<HeroEquipmentInventory> enemyHeroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(matchUserId);
        //상대방 메인 영웅의 스킬 정보(스킬 레벨)
        List<MyMainHeroSkill> enemyMainHeroSkillList = myMainHeroSkillRepository.findAllByUseridUser(matchUserId);


        MyArenaUsersDataDto.MyArenaUserData enemyArenaUserData =
                new MyArenaUsersDataDto.MyArenaUserData(enemyTeamDeck, enemyLinkforceOpenDtosList.openInfoList, enemyLinkweaponInfoDtosList.companionLinkweaponInfoList, enemyCostumeDtosList.hasCostumeIdList, enemyHeroEquipmentInventoryList, enemyEquipmentDeck, enemyMainHeroSkillList);


        MyTeamInfo myTeamInfo = myTeamInfoRepository.findByUseridUser(userId);
        String myTeamString = myTeamInfo.getArenaDefenceTeam();
        String[] myTeamArray = myTeamString.split(",");

        List<Long> myTeamCharactersList = new ArrayList<>();
        for(String characterIdStr : myTeamArray) {
            if(Strings.isNullOrEmpty(characterIdStr))
                continue;
            Long characterId = Long.parseLong(characterIdStr);
            myTeamCharactersList.add(characterId);
        }
        //내 아레나 팀덱
        List<MyCharacters> tempMyTeamDeck = myCharactersRepository.findAllById(myTeamCharactersList);
        List<MyCharacters> myTeamDeck = new ArrayList<>();
        for(Long characterId : myTeamCharactersList) {
            if(characterId == 0L){
                MyCharactersBaseDto myCharactersBaseDto = new MyCharactersBaseDto();
                myCharactersBaseDto.setId(0L);
                myCharactersBaseDto.setCodeHerostable("none");
                myTeamDeck.add(myCharactersBaseDto.ToEntity(0));
            }
            else{
                MyCharacters myCharacter = tempMyTeamDeck.stream().filter(a -> a.getId().equals(characterId)).findAny().orElse(null);
                myTeamDeck.add(myCharacter);
            }
        }

        //내 동료 케릭터의 링크포스 정보
        MyLinkforceInfo myLinkforceInfoList = myLinkforceInfoRepository.findByUseridUser(userId)
                .orElse(null);
        if(myLinkforceInfoList == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myLinkforceInfoList not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myLinkforceInfoList not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        LinkforceOpenDtosList myLinkforceOpenDtosList = JsonStringHerlper.ReadValueFromJson(myLinkforceInfoList.getJson_LinkforceInfos(), LinkforceOpenDtosList.class);

        //내 동료 케릭터의 링크웨폰 정보
        MyLinkweaponInfo myLinkweaponInfoList = myLinkweaponInfoRepository.findByUseridUser(userId)
                .orElse(null);
        if(myLinkweaponInfoList == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myLinkweaponInfoList not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myLinkweaponInfoList not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        LinkweaponInfoDtosList myLinkweaponInfoDtosList = JsonStringHerlper.ReadValueFromJson(myLinkweaponInfoList.getJson_LinkweaponRevolution(), LinkweaponInfoDtosList.class);

        //내 동료 케릭터의 코스튬 정보
        MyCostumeInventory myCostumeInventory = myCostumeInventoryRepository.findByUseridUser(userId)
                .orElse(null);
        if(myCostumeInventory == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myCostumeInventory not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myCostumeInventory not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        CostumeDtosList myCostumeDtosList = JsonStringHerlper.ReadValueFromJson(myCostumeInventory.getJson_CostumeInventory(), CostumeDtosList.class);


        //내 메인 영웅의 장착된 장비덱 정보
        MyEquipmentDeck myEquipmentDeck = myEquipmentDeckRepository.findByUserIdUser(userId)
                .orElse(null);
        if(myEquipmentDeck == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyEquipmentDeck not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEquipmentDeck not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

//        List<Long> myEquipmentsList = new ArrayList<>();
//        currentUseDeckNo = myEquipmentDeck.getCurrentUseDeckNo();
//        switch (currentUseDeckNo) {
//            case 1:
//                myEquipmentsList.add(myEquipmentDeck.getFirstDeckWeaponInventoryId());
//                myEquipmentsList.add(myEquipmentDeck.getFirstDeckArmorInventoryId());
//                myEquipmentsList.add(myEquipmentDeck.getFirstDeckHelmetInventoryId());
//                myEquipmentsList.add(myEquipmentDeck.getFirstDeckAccessoryInventoryId());
//                break;
//            case 2:
//                myEquipmentsList.add(myEquipmentDeck.getSecondDeckWeaponInventoryId());
//                myEquipmentsList.add(myEquipmentDeck.getSecondDeckAccessoryInventoryId());
//                myEquipmentsList.add(myEquipmentDeck.getSecondDeckHelmetInventoryId());
//                myEquipmentsList.add(myEquipmentDeck.getSecondDeckAccessoryInventoryId());
//                break;
//            case 3:
//                myEquipmentsList.add(myEquipmentDeck.getThirdDeckWeaponInventoryId());
//                myEquipmentsList.add(myEquipmentDeck.getThirdDeckArmorInventoryId());
//                myEquipmentsList.add(myEquipmentDeck.getThirdDeckHelmetInventoryId());
//                myEquipmentsList.add(myEquipmentDeck.getThirdDeckAccessoryInventoryId());
//                break;
//        }
        //상대방 메인 영웅의 덱에 장착된 실제 장비들 데이터
        List<HeroEquipmentInventory> myHeroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(userId);
        //상대방 메인 영웅의 스킬 정보(스킬 레벨)
        List<MyMainHeroSkill> myMainHeroSkillList = myMainHeroSkillRepository.findAllByUseridUser(userId);

        MyArenaUsersDataDto.MyArenaUserData myArenaUserData =
                new MyArenaUsersDataDto.MyArenaUserData(myTeamDeck, myLinkforceOpenDtosList.openInfoList, myLinkweaponInfoDtosList.companionLinkweaponInfoList, myCostumeDtosList.hasCostumeIdList, myHeroEquipmentInventoryList, myEquipmentDeck, myMainHeroSkillList);


        map.put("myArenaUserData", myArenaUserData);
        map.put("enemyArenaUserData", enemyArenaUserData);
        return map;
    }
}