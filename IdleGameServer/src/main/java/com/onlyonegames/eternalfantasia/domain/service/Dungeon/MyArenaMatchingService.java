package com.onlyonegames.eternalfantasia.domain.service.Dungeon;

import com.google.common.base.Strings;
import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.CostumeDto.CostumeDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.MyArenaPlayDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceDto.LinkforceOpenDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceWeaponDto.LinkweaponInfoDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CurrencyLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyCharactersBaseDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RdsScoreDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.MyArenaUsersDataDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyCostumeInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyLinkforceInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyLinkweaponInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.RdsScore;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyArenaPlayData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyEquipmentDeck;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyMainHeroSkill;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyTeamInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.ArenaPlayInfoTable;
import com.onlyonegames.eternalfantasia.domain.repository.*;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyCostumeInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyLinkforceInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyLinkweaponInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.ArenaSeasonInfoDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.Leaderboard.RdsScoreRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyAreanPlayDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.HeroEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyEquipmentDeckRepository;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.Leaderboard.LeaderboardService;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.util.MathHelper;
import com.onlyonegames.util.StringMaker;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyArenaMatchingService {

    private final GameDataTableService gameDataTableService;
    private final UserRepository userRepository;
     //mysql Score table
    private final RdsScoreRepository rdsScoreRepository;
    //상대방 팀덱 케릭터들 레벨과 어빌리티 링크 레벨 얻어오기
    private final MyCharactersRepository myCharactersRepository;
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
    //매칭된 리스트들의 ID를 저장
    private final MyAreanPlayDataRepository myAreanPlayDataRepository;

    private final MyTeamInfoRepository myTeamInfoRepository;

    private final ArenaSeasonInfoDataRepository arenaSeasonInfoDataRepository;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;

    RdsScore FindNear(RdsScore myRdsScore, List<RdsScore> pointsList) {
        Long min = Long.MAX_VALUE;
        RdsScore nearData = null;
        for(RdsScore probability : pointsList) {
            if(probability.getId().equals(myRdsScore.getId()))
                continue;
            Long a = Math.abs(probability.getScore() - myRdsScore.getScore());
            if(min > a) {
                min = a;
                nearData = probability;
            }
        }
        return nearData;
    }

    List<RdsScore> GetLeadyVersus3(Long userId){

        List<RdsScore> versus3List = new ArrayList<>();
        Optional<RdsScore> myRdsScoreOptional = rdsScoreRepository.findByUseridUser(userId);
        if(myRdsScoreOptional.isPresent()) {
            RdsScore myRdsScore = myRdsScoreOptional.get();
            //티어는 숫자가 적을수록 높음.
            int nowTier = myRdsScore.getRankingtiertableId();
            int topTier = myRdsScore.getRankingtiertableId() - 1;
            int downTier = myRdsScore.getRankingtiertableId() + 1;
            //탑 티어(최고 티어는 1티어)
            if (topTier >= 1) {
                List<RdsScore> probabilityList = rdsScoreRepository.findTop100ByRankingtiertableId(topTier);
                RdsScore mine = probabilityList.stream().filter(a -> a.getId().equals(myRdsScore.getId())).findAny().orElse(null);
                if(mine != null)
                    probabilityList.remove(mine);
                int listSize = probabilityList.size();
                if (listSize > 0) {
                    int selectedIndex = (int) MathHelper.Range(0, listSize);
                    RdsScore topTierRdsScore = probabilityList.get(selectedIndex);
                    versus3List.add(topTierRdsScore);
                }
            }
            //최소 티어 20
            if (downTier <= 20) {
                List<RdsScore> probabilityList = rdsScoreRepository.findBottom100ByRankingtiertableId(downTier);
                RdsScore mine = probabilityList.stream().filter(a -> a.getId().equals(myRdsScore.getId())).findAny().orElse(null);
                if(mine != null)
                    probabilityList.remove(mine);
                int listSize = probabilityList.size();
                if (listSize > 0) {
                    int selectedIndex = (int) MathHelper.Range(0, listSize);
                    RdsScore topTierRdsScore = probabilityList.get(selectedIndex);
                    versus3List.add(topTierRdsScore);
                }
            }
            //현재 티어(탑티어와 최소 티어에서 결정된 리스트가 없다면 해당 위치에서 검색한다)
            List<RdsScore> probabilityList = rdsScoreRepository.findTop100ByRankingtiertableId(nowTier);
            RdsScore mine = probabilityList.stream().filter(a -> a.getId().equals(myRdsScore.getId())).findAny().orElse(null);
            if(mine != null)
                probabilityList.remove(mine);
            int listSize = 0;
            while (versus3List.size() < 3 && probabilityList.size() > 0) {
                listSize = probabilityList.size();
                if (listSize > 0) {
                    int randomIndex = (int) MathHelper.Range(0, probabilityList.size());
                    RdsScore nowTierRdsScore = probabilityList.get(randomIndex);
                    versus3List.add(nowTierRdsScore);
                    probabilityList.remove(randomIndex);
                }
            }
            if(versus3List.size() < 3) {
                probabilityList = rdsScoreRepository.findAllByScoreAfterAndScoreBefore(myRdsScore.getScore() - 10000, myRdsScore.getScore() + 10000);
                mine = probabilityList.stream().filter(a -> a.getId().equals(myRdsScore.getId())).findAny().orElse(null);
                if(mine != null)
                    probabilityList.remove(mine);
                while (versus3List.size() < 3 && probabilityList.size() > 0) {
                    listSize = probabilityList.size();
                    if (listSize > 0) {
                        int randomIndex = (int) MathHelper.Range(0, probabilityList.size());
                        RdsScore nowTierRdsScore = probabilityList.get(randomIndex);
                        versus3List.add(nowTierRdsScore);
                        probabilityList.remove(randomIndex);
                    }
                }
            }
        }
        //최초 유저일때
        else{
            int nowTier = 20;
            int topTier = nowTier - 1;

            //탑 티어(최고 티어는 1티어)
            List<RdsScore> topTierProbabilityList = rdsScoreRepository.findTop100ByRankingtiertableId(topTier);
            int topTierProbabilityListSize = topTierProbabilityList.size();
            if (topTierProbabilityListSize > 0) {
                int selectedIndex = (int) MathHelper.Range(0, topTierProbabilityListSize);
                RdsScore topTierRdsScore = topTierProbabilityList.get(selectedIndex);
                versus3List.add(topTierRdsScore);
            }
            //현재 티어(탑티어와 최소 티어에서 결정된 리스트가 없다면 해당 위치에서 검색한다)
            List<RdsScore> probabilityList = rdsScoreRepository.findTop100ByRankingtiertableId(nowTier);

            int listSize = 0;
            while (versus3List.size() < 3 && probabilityList.size() > 0) {
                listSize = probabilityList.size();
                if (listSize > 0) {
                    int randomIndex = (int) MathHelper.Range(0, probabilityList.size());
                    RdsScore nowTierRdsScore = probabilityList.get(randomIndex);
                    versus3List.add(nowTierRdsScore);
                    probabilityList.remove(randomIndex);
                }
            }
            if(versus3List.size() < 3) {
                probabilityList = rdsScoreRepository.findAllByScoreAfterAndScoreBefore(0L, 10000L);

                while (versus3List.size() < 3 && probabilityList.size() > 0) {
                    listSize = probabilityList.size();
                    if (listSize > 0) {
                        int randomIndex = (int) MathHelper.Range(0, probabilityList.size());
                        RdsScore nowTierRdsScore = probabilityList.get(randomIndex);
                        versus3List.add(nowTierRdsScore);
                        probabilityList.remove(randomIndex);
                    }
                }
            }
        }

        versus3List.sort((a,b) -> b.getScore().compareTo(a.getScore()));
        return versus3List;
    }

    public Map<String, Object> GetLeadyVersus3(Long userId, Map<String, Object> map) {
        List<RdsScore> versus3List;
        List<RdsScoreDto> versus3ListDto = new ArrayList<>();
        List<Long> matchedIdList = new ArrayList<>();
        MyArenaPlayData arenaPlayData = myAreanPlayDataRepository.findByUseridUser(userId);
        if(arenaPlayData == null)  {
            MyArenaPlayDataDto newPlayDataDto = new MyArenaPlayDataDto();
            newPlayDataDto.setUseridUser(userId);
            arenaPlayData = myAreanPlayDataRepository.save(newPlayDataDto.ToEntity());
        }

        String matchingRdsScoreIds = arenaPlayData.getMatchingRdsScoreIds();
        if(!Strings.isNullOrEmpty(matchingRdsScoreIds) && !arenaPlayData.isResetAbleMatchingRdsScoreIds()){
            List<MyArenaUsersDataDto.MyArenaUserData> myArenaUserDataList = new ArrayList<>();
            versus3List = new ArrayList<>();
            String[] matchingRdsScoreIdsArray = matchingRdsScoreIds.split(",");
            for(int i = 0; i < matchingRdsScoreIdsArray.length; i++){
                Long matchingRdsScoreId = Long.parseLong(matchingRdsScoreIdsArray[i]);
                RdsScore matchingUserRdsScore = rdsScoreRepository.findById(matchingRdsScoreId).orElse(null);
                if(matchingUserRdsScore == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: rdsScore not find. userId => " + matchingRdsScoreId, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: rdsScore not find. userId => " + matchingRdsScoreId, ResponseErrorCode.NOT_FIND_DATA);
                }
                versus3List.add(matchingUserRdsScore);
                matchedIdList.add(matchingUserRdsScore.getUseridUser());
            }
            List<Long> charactersIdsList = new ArrayList<>();
            List<MyTeamInfo> versurs3TeamList  = myTeamInfoRepository.findByUseridUserIn(matchedIdList);
            for(MyTeamInfo myTeamInfo : versurs3TeamList) {
                String arenaDefenceTeams = myTeamInfo.getArenaDefenceTeam();
                String[] arenaDefenceTeamsArray = arenaDefenceTeams.split(",");
                for(String characterIdStr : arenaDefenceTeamsArray){
                    if(Strings.isNullOrEmpty(characterIdStr))
                        continue;
                    long characterId = Long.parseLong(characterIdStr);
                    charactersIdsList.add(characterId);
                }
            }
            List<MyCharacters> myCharactersList = myCharactersRepository.findAllById(charactersIdsList);
            List<MyCharacters> subCharactersIdsList = new ArrayList<>();
            for(RdsScore temp : versus3List){
                RdsScoreDto rdsScoreDto = new RdsScoreDto();
                rdsScoreDto.InitFromDbData(temp);
                MyTeamInfo enemyTeam = versurs3TeamList.stream().filter(a -> a.getUseridUser().equals(temp.getUseridUser())).findAny().orElse(null);
                rdsScoreDto.setTeamCharactersIds(enemyTeam.getArenaDefenceTeam());

                for(MyCharacters selectedCharacter : myCharactersList){
                    if(selectedCharacter.getUseridUser().equals(temp.getUseridUser()))
                        subCharactersIdsList.add(selectedCharacter);
                }

                rdsScoreDto.setTeamCharacterCodes(LeaderboardService.getTeamCharacterCodes(subCharactersIdsList));
                versus3ListDto.add(rdsScoreDto);

                MyArenaUsersDataDto.MyArenaUserData myArenaUserData = getArenaUserData(temp.getUseridUser(), enemyTeam.getArenaDefenceTeam());
                myArenaUserDataList.add(myArenaUserData);
            }


            MyTeamInfo myTeamInfo = myTeamInfoRepository.findByUseridUser(userId);
            String myTeamString = myTeamInfo.getArenaPlayTeam();
            MyArenaUsersDataDto.MyArenaUserData myArenaUserData = getArenaUserData(userId, myTeamString);
            myArenaUserDataList.add(myArenaUserData);

            map.put("versus3List", versus3ListDto);
            map.put("arenaUserDataList", myArenaUserDataList);

            return map;
        }

        versus3List = GetLeadyVersus3(userId);
        StringMaker.Clear();
        for(int i = 0; i < versus3List.size(); i++) {
            RdsScore matchingRdsScore = versus3List.get(i);
            Long matchingRdsScoreId = matchingRdsScore.getId();
            matchedIdList.add(matchingRdsScore.getUseridUser());
            if (i > 0)
                StringMaker.stringBuilder.append(",");
            StringMaker.stringBuilder.append(matchingRdsScoreId);
        }
        List<MyArenaUsersDataDto.MyArenaUserData> myArenaUserDataList = new ArrayList<>();
        String newMatchingRdsScoreIds = StringMaker.stringBuilder.toString();
        arenaPlayData.SetMatchingRdsScoreIds(newMatchingRdsScoreIds);


        List<Long> charactersIdsList = new ArrayList<>();
        List<MyTeamInfo> versurs3TeamList  = myTeamInfoRepository.findByUseridUserIn(matchedIdList);
        for(MyTeamInfo myTeamInfo : versurs3TeamList) {
            String arenaDefenceTeams = myTeamInfo.getArenaDefenceTeam();
            String[] arenaDefenceTeamsArray = arenaDefenceTeams.split(",");
            for(String characterIdStr : arenaDefenceTeamsArray){
                if(Strings.isNullOrEmpty(characterIdStr))
                    continue;
                long characterId = Long.parseLong(characterIdStr);
                charactersIdsList.add(characterId);
            }
        }
        List<MyCharacters> myCharactersList = myCharactersRepository.findAllById(charactersIdsList);
        List<MyCharacters> subCharactersIdsList = new ArrayList<>();

        for(RdsScore temp : versus3List){
            RdsScoreDto rdsScoreDto = new RdsScoreDto();
            rdsScoreDto.InitFromDbData(temp);

            MyTeamInfo enemyTeam = versurs3TeamList.stream().filter(a -> a.getUseridUser().equals(temp.getUseridUser())).findAny().orElse(null);
            rdsScoreDto.setTeamCharactersIds(enemyTeam.getArenaDefenceTeam());

            for(MyCharacters selectedCharacter : myCharactersList){
                if(selectedCharacter.getUseridUser().equals(temp.getUseridUser()))
                    subCharactersIdsList.add(selectedCharacter);
            }

            rdsScoreDto.setTeamCharacterCodes(LeaderboardService.getTeamCharacterCodes(subCharactersIdsList));

            versus3ListDto.add(rdsScoreDto);

            MyArenaUsersDataDto.MyArenaUserData myArenaUserData = getArenaUserData(temp.getUseridUser(), enemyTeam.getArenaDefenceTeam());
            myArenaUserDataList.add(myArenaUserData);
        }

        MyTeamInfo myTeamInfo = myTeamInfoRepository.findByUseridUser(userId);
        String myTeamString = myTeamInfo.getArenaPlayTeam();
        MyArenaUsersDataDto.MyArenaUserData myArenaUserData = getArenaUserData(userId, myTeamString);
        myArenaUserDataList.add(myArenaUserData);

        map.put("versus3List", versus3ListDto);
        map.put("arenaUserDataList", myArenaUserDataList);

        return map;
    }

    MyArenaUsersDataDto.MyArenaUserData getArenaUserData(Long matchUserId, String userArenaTeamDeckIds){

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

        return enemyArenaUserData;
    }
    //다이아몬드를 사용하여 한번더 매칭상대들을 선택한다.
    public Map<String, Object> ForceGetLeadyVersus3ByDiamond(Long userId, Map<String, Object> map) {

        /*자원 체크*/
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        ArenaPlayInfoTable arenaPlayInfoTable = gameDataTableService.ArenaPlayInfoTableList().get(0);
        CurrencyLogDto currencyLogDto = new CurrencyLogDto();
        int previousValue = user.getDiamond();
        int cost = arenaPlayInfoTable.getRemachingNeedDiamond();
        if(!user.SpendDiamond(cost)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail -> Cause: Need More Diamond", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Need More Diamond", ResponseErrorCode.NEED_MORE_DIAMOND);
        }
        currencyLogDto.setCurrencyLogDto("아레나 재매칭", "다이아", previousValue, -cost, user.getDiamond());
        String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
        loggingService.setLogging(userId, 1, log);

        MyArenaPlayData arenaPlayData = myAreanPlayDataRepository.findByUseridUser(userId);
        List<RdsScore> versus3List = GetLeadyVersus3(userId);
        //versus3List.remove(2);
        //RdsScore special = rdsScoreRepository.findById(1760L).orElse(null);
        //versus3List.add(special);
        List<Long> matchedIdList = new ArrayList<>();

        StringMaker.Clear();
        for(int i = 0; i < versus3List.size(); i++) {
            RdsScore matchingRdsScore = versus3List.get(i);
            Long matchingRdsScoreId = matchingRdsScore.getId();
            matchedIdList.add(matchingRdsScore.getUseridUser());
            if (i > 0)
                StringMaker.stringBuilder.append(",");
            StringMaker.stringBuilder.append(matchingRdsScoreId);
        }

        List<Long> charactersIdsList = new ArrayList<>();
        List<MyTeamInfo> versurs3TeamList  = myTeamInfoRepository.findByUseridUserIn(matchedIdList);
        for(MyTeamInfo myTeamInfo : versurs3TeamList) {
            String arenaDefenceTeams = myTeamInfo.getArenaDefenceTeam();
            String[] arenaDefenceTeamsArray = arenaDefenceTeams.split(",");
            for(String characterIdStr : arenaDefenceTeamsArray){
                if(Strings.isNullOrEmpty(characterIdStr))
                    continue;
                long characterId = Long.parseLong(characterIdStr);
                charactersIdsList.add(characterId);
            }
        }
        List<MyCharacters> myCharactersList = myCharactersRepository.findAllById(charactersIdsList);
        List<MyCharacters> subCharactersIdsList = new ArrayList<>();

        List<MyArenaUsersDataDto.MyArenaUserData> myArenaUserDataList = new ArrayList<>();
        String newMatchingRdsScoreIds = StringMaker.stringBuilder.toString();
        arenaPlayData.SetMatchingRdsScoreIds(newMatchingRdsScoreIds);
        List<RdsScoreDto> versus3ListDto = new ArrayList<>();
        for(RdsScore temp : versus3List){
            RdsScoreDto rdsScoreDto = new RdsScoreDto();
            rdsScoreDto.InitFromDbData(temp);

            MyTeamInfo enemyTeam = versurs3TeamList.stream().filter(a -> a.getUseridUser().equals(temp.getUseridUser())).findAny().orElse(null);
            rdsScoreDto.setTeamCharactersIds(enemyTeam.getArenaDefenceTeam());

            for(MyCharacters selectedCharacter : myCharactersList){
                if(selectedCharacter.getUseridUser().equals(temp.getUseridUser()))
                    subCharactersIdsList.add(selectedCharacter);
            }

            rdsScoreDto.setTeamCharacterCodes(LeaderboardService.getTeamCharacterCodes(subCharactersIdsList));

            versus3ListDto.add(rdsScoreDto);

            MyArenaUsersDataDto.MyArenaUserData myArenaUserData = getArenaUserData(temp.getUseridUser(), enemyTeam.getArenaDefenceTeam());
            myArenaUserDataList.add(myArenaUserData);
        }

        MyTeamInfo myTeamInfo = myTeamInfoRepository.findByUseridUser(userId);
        String myTeamString = myTeamInfo.getArenaPlayTeam();
        MyArenaUsersDataDto.MyArenaUserData myArenaUserData = getArenaUserData(userId, myTeamString);
        myArenaUserDataList.add(myArenaUserData);

        map.put("versus3List", versus3ListDto);
        map.put("arenaUserDataList", myArenaUserDataList);
        map.put("user", user);
        return map;
    }

    //상대방 팀덱 내에 있는 케릭터들에 대한 정보 리턴
    public Map<String, Object> MatchingUserInfo(Long userId, Long matchUserId, Map<String, Object> map) {
        RdsScore userRdsScore = rdsScoreRepository.findByUseridUser(matchUserId).orElse(null);
        if(userRdsScore == null) {
            errorLoggingService.SetErrorLog(matchUserId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: rdsScore not find. userId => " + matchUserId, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: rdsScore not find. userId => " + matchUserId, ResponseErrorCode.NOT_FIND_DATA);
        }
        String userArenaTeamDeckIds = userRdsScore.getTeamCharactersIds();

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
        String myTeamString = myTeamInfo.getArenaPlayTeam();
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
    
    //아레나 유저 정보.(해당 유저의 정보중 덱은 현재 셋팅한 아레나 공격덱)
    public Map<String, Object> GetArenaUserInfo(Long userId, Long matchUserId, Map<String, Object> map) {

        MyTeamInfo enemyTeamInfo = myTeamInfoRepository.findByUseridUser(matchUserId);

        String userArenaTeamDeckIds = enemyTeamInfo.getArenaPlayTeam();

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
