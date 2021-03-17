package com.onlyonegames.eternalfantasia.domain.service.Dungeon;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyTeamInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyTeamInfo;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyTeamInfoRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.etc.Defines;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class TeamBuildingAncientDragonDungeonService {
    private final MyCharactersRepository myCharactersRepository;
    private final MyTeamInfoRepository myTeamInfoRepository;
    private final ErrorLoggingService errorLoggingService;

    // 덱슬롯중 해당 인덱스를 해당 동료 나 영웅으로 설정
    public Map<String, Object> AddTeam(Long useridUser, int index, Long characterId, Map<String, Object> map) {

        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(useridUser);
        MyCharacters myCharacter = myCharactersList.stream()
                .filter(a -> a.getId().equals(characterId))
                .findAny()
                .orElse(null);
        if(myCharacter == null) {
            errorLoggingService.SetErrorLog(useridUser, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find Character", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Can't Find Character", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyTeamInfo myTeamInfo = myTeamInfoRepository.findByUseridUser(useridUser);
        myTeamInfo.AddTeam(myCharacter.getId(), index, Defines.TEAM_BUILDING_KIND.ANCIENT_DRAGON_DUNGEON_TEAM);
        MyTeamInfoDto myTeamInfoDto = new MyTeamInfoDto();
        myTeamInfoDto.InitFromDBData(myTeamInfo);
        map.put("myTeamInfo", myTeamInfoDto);
        return map;
    }
    // 덱 내에 있는 영웅 및 동료중 해당 인덱스에 있는 것 빼주기
    public Map<String, Object> RemoveTeam(Long useridUser, int index, Long characterId, Map<String, Object> map) {

        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(useridUser);
        MyCharacters myCharacter = myCharactersList.stream()
                .filter(a -> a.getId().equals(characterId))
                .findAny()
                .orElse(null);
        if(myCharacter == null) {
            errorLoggingService.SetErrorLog(useridUser, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find Character", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Can't Find Character", ResponseErrorCode.NOT_FIND_DATA);
        }
        if(myCharacter.getCodeHerostable().equals("hero")){
            errorLoggingService.SetErrorLog(useridUser, ResponseErrorCode.HERO_CANNOT_EMPTY.getIntegerValue(), "Fail -> Cause: Heroes Cannot Be Removed", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Heroes Cannot Be Removed", ResponseErrorCode.HERO_CANNOT_EMPTY);
        }
        MyTeamInfo myTeamInfo = myTeamInfoRepository.findByUseridUser(useridUser);
        myTeamInfo.RemoveTeam(index, Defines.TEAM_BUILDING_KIND.ANCIENT_DRAGON_DUNGEON_TEAM);
        MyTeamInfoDto myTeamInfoDto = new MyTeamInfoDto();
        myTeamInfoDto.InitFromDBData(myTeamInfo);
        map.put("myTeamInfo", myTeamInfoDto);
        return map;
    }

    //덱 내에 있는 영웅들 끼리 인덱스 교체
    public Map<String, Object> SwitchTeam(Long useridUser, int indexA, Long characterIdA, int indexB, Long characterIdB, Map<String, Object> map) {

        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(useridUser);
        MyCharacters myCharacter = myCharactersList.stream()
                .filter(a -> a.getId().equals(characterIdA))
                .findAny()
                .orElse(null);
        if(myCharacter == null) {
            errorLoggingService.SetErrorLog(useridUser, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find CharacterA", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Can't Find CharacterA", ResponseErrorCode.NOT_FIND_DATA);
        }

        //characterIdB 가 0이면 다른 슬롯에 있는 케릭터를 드래그하여 빈 슬롯으로 옮겼을때 이다.
        if(characterIdB > 0){
            myCharacter = myCharactersList.stream()
                    .filter(a -> a.getId().equals(characterIdB))
                    .findAny()
                    .orElse(null);
            if(myCharacter == null) {
                errorLoggingService.SetErrorLog(useridUser, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find CharacterB", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail -> Cause: Can't Find CharacterB", ResponseErrorCode.NOT_FIND_DATA);
            }
        }

        MyTeamInfo myTeamInfo = myTeamInfoRepository.findByUseridUser(useridUser);
        myTeamInfo.SwitchTeam(indexA, indexB, Defines.TEAM_BUILDING_KIND.ANCIENT_DRAGON_DUNGEON_TEAM);
        MyTeamInfoDto myTeamInfoDto = new MyTeamInfoDto();
        myTeamInfoDto.InitFromDBData(myTeamInfo);
        map.put("myTeamInfo", myTeamInfoDto);
        return map;
    }

    //덱 내에 있는 영웅들 중 전투력이 가장 높은 영웅 순으로 덱에 채워 넣는다.
    public Map<String, Object> TeamAutoSet(Long useridUser, String teamIds, Map<String, Object> map) {
        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(useridUser);
        CheckTeamDeck(useridUser, teamIds, myCharactersList);
        MyTeamInfo myTeamInfo = myTeamInfoRepository.findByUseridUser(useridUser);
        myTeamInfo.AutoTeamSet(teamIds, Defines.TEAM_BUILDING_KIND.ANCIENT_DRAGON_DUNGEON_TEAM);
        MyTeamInfoDto myTeamInfoDto = new MyTeamInfoDto();
        myTeamInfoDto.InitFromDBData(myTeamInfo);
        map.put("myTeamInfo", myTeamInfoDto);
        return map;
    }

    private void CheckTeamDeck(Long userId, String teamDeck, List<MyCharacters> myCharactersList){
        int heroCount = 0;
        String[] characterIdList = teamDeck.split(",");
        Set<Long> duplicateCheck = new HashSet<>();
        for(String temp:characterIdList){
            long characterId = Long.parseLong(temp);
            if(characterId == 0)
                continue;
            MyCharacters myCharacters = myCharactersList.stream().filter(i -> i.getId().equals(characterId)).findAny().orElse(null);
            if(myCharacters == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find MyCharacters", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail -> Cause: Can't Find MyCharacters", ResponseErrorCode.NOT_FIND_DATA);
            }
            if(myCharacters.getCodeHerostable().equals("hero")) {
                heroCount += 1;
                continue;
            }
            if(!duplicateCheck.add(characterId)){
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.LEGION_CANNOT_DUPLICATE.getIntegerValue(), "Fail -> Cause: Legions Cannot Be Duplicate", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail -> Cause: Legions Cannot Be Duplicate", ResponseErrorCode.LEGION_CANNOT_DUPLICATE);
            }
        }
        if(heroCount == 0){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.HERO_CANNOT_EMPTY.getIntegerValue(), "Fail -> Cause: Heroes Cannot Be Removed", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Heroes Cannot Be Removed", ResponseErrorCode.HERO_CANNOT_EMPTY);
        }
        if(heroCount >=2){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.HERO_CANNOT_DUPLICATE.getIntegerValue(), "Fail -> Cause: Heroes Cannot Be Duplicated", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Heroes Cannot Be Duplicated", ResponseErrorCode.HERO_CANNOT_DUPLICATE);
        }
    }
}
