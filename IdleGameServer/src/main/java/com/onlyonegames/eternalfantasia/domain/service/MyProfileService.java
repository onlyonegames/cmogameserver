package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ProfileDto.MyProfileDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ProfileDto.MyProfileMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ProfileDto.ProfileDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ProfileDto.MyProfileResponseDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyProfileData;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.ProfileFrameMissionTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.herostable;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyProfileDataRepository;
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
public class MyProfileService {
    private final MyProfileDataRepository myProfileDataRepository;
    private final MyCharactersRepository myCharactersRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> Test (Map<String, Object> map) {
        ProfileDataDto profileDataDto = new ProfileDataDto();
        List<ProfileDataDto.ProfileFrame> profileFrameList = new ArrayList<>();
        ProfileDataDto.ProfileFrame profileFrame = new ProfileDataDto.ProfileFrame();
        profileFrame.SetProfileFrame(1,"test", true, true);
        profileFrameList.add(profileFrame);
        profileDataDto.setProfileHero("hero");
        profileDataDto.setProfileFrame(1);
        profileDataDto.setProfileFrameList(profileFrameList);
        String json = JsonStringHerlper.WriteValueAsStringFromData(profileDataDto);
        map.put("test", json);
        return map;
    }

    public Map<String, Object> GetProfileInfo (Long userId, Map<String, Object> map) {
        MyProfileData myProfileData = myProfileDataRepository.findByUseridUser(userId).orElse(null);
        if(myProfileData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: UserProfileData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: UserProfileData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String json_saveDataValue = myProfileData.getJson_saveDataValue();
        ProfileDataDto profileDataDto = JsonStringHerlper.ReadValueFromJson(json_saveDataValue, ProfileDataDto.class);
        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
        List<herostable> herostableList = gameDataTableService.HerosTableList();
        List<MyProfileResponseDto.ProfileCharacter> profileCharacterList = new ArrayList<>();
        for(MyCharacters temp : myCharactersList) {
            herostable herostable = herostableList.stream().filter(i -> i.getCode().equals(temp.getCodeHerostable())).findAny().orElse(null);
            if(herostable == null){
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: herostable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: herostable not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            if(herostable.getTier()==1&&!herostable.getCode().equals("hero"))
                continue;
            MyProfileResponseDto.ProfileCharacter profileCharacter = new MyProfileResponseDto.ProfileCharacter();
            profileCharacter.SetProfileCharacter(temp.getCodeHerostable(), temp.isGotcha(), profileDataDto.profileHero.equals(temp.getCodeHerostable()));
            profileCharacterList.add(profileCharacter);
        }
        MyProfileResponseDto myProfileResponseDto = new MyProfileResponseDto();
        myProfileResponseDto.SetUserProfileResponseDto(profileDataDto.profileHero, profileDataDto.profileFrame, profileDataDto.profileFrameList, profileCharacterList);
        map.put("userProfileResponseDto", myProfileResponseDto);
        return map;
    }

    public Map<String, Object> SetProfile(Long userId, String heroCode, int frameId, Map<String, Object> map) {
        MyProfileData myProfileData = myProfileDataRepository.findByUseridUser(userId).orElse(null);
        if(myProfileData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: UserProfileData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: UserProfileData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String json_saveDataValue = myProfileData.getJson_saveDataValue();
        ProfileDataDto profileDataDto = JsonStringHerlper.ReadValueFromJson(json_saveDataValue, ProfileDataDto.class);

        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
        MyCharacters selectedCharacter = myCharactersList.stream().filter(i -> i.getCodeHerostable().equals(heroCode)&&i.isGotcha()).findAny().orElse(null);
        if(selectedCharacter == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_YET_GETTING_CHARACTER.getIntegerValue(), "Fail! -> Cause: not yet getting character.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: not yet getting character.", ResponseErrorCode.NOT_YET_GETTING_CHARACTER);
        }
        profileDataDto.setProfileHero(selectedCharacter.getCodeHerostable());

        boolean isContain = false;
        for(ProfileDataDto.ProfileFrame temp : profileDataDto.getProfileFrameList()){
            if(temp.frameId == frameId)
                isContain = true;
            if(temp.frameId == frameId&&!temp.selected){
                if(!temp.possession){
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_YET_GETTING_ProfileFRAME.getIntegerValue(), "Fail! -> Cause: not yet getting ProfileFrame.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: not yet getting ProfileFrame.", ResponseErrorCode.NOT_YET_GETTING_ProfileFRAME);
                }
                temp.OnOffSelected();
            }else if(temp.frameId != frameId&&temp.selected)
                temp.OnOffSelected();
        }
        if(!isContain){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ProfileFrame not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: ProfileFrame not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        profileDataDto.setProfileFrame(frameId);

        json_saveDataValue = JsonStringHerlper.WriteValueAsStringFromData(profileDataDto);
        myProfileData.ResetJson_saveDataValue(json_saveDataValue);

        List<herostable> herostableList = gameDataTableService.HerosTableList();
        List<MyProfileResponseDto.ProfileCharacter> profileCharacterList = new ArrayList<>();
        for(MyCharacters temp : myCharactersList) {
            herostable herostable = herostableList.stream().filter(i -> i.getCode().equals(temp.getCodeHerostable())).findAny().orElse(null);
            if(herostable == null){
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: herostable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: herostable not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            if(herostable.getTier()==1&&!herostable.getCode().equals("hero"))
                continue;
            MyProfileResponseDto.ProfileCharacter profileCharacter = new MyProfileResponseDto.ProfileCharacter();
            profileCharacter.SetProfileCharacter(temp.getCodeHerostable(), temp.isGotcha(), profileDataDto.profileHero.equals(temp.getCodeHerostable()));
            profileCharacterList.add(profileCharacter);
        }

        MyProfileResponseDto myProfileResponseDto = new MyProfileResponseDto();
        myProfileResponseDto.SetUserProfileResponseDto(profileDataDto.profileHero, profileDataDto.profileFrame, profileDataDto.profileFrameList, profileCharacterList);
        map.put("userProfileResponseDto", myProfileResponseDto);
        return map;
    }

    public void GetProfileFrame(Long userId, MyProfileMissionDataDto myProfileMissionDataDto, ProfileDataDto profileDataDto) {
        List<String> rewardList = new ArrayList<>();
        for(MissionsDataDto.MissionData temp : myProfileMissionDataDto.getProfileFrameMissionsData()){
            if(temp.rewardReceived || !temp.success)
                continue;
            rewardList.add(temp.code);
            temp.rewardReceived = true;
        }
        for(String missionCode : rewardList) {
            ProfileFrameMissionTable missionTable = gameDataTableService.ProfileFrameMissionTableList().stream().filter(i -> i.getCode().equals(missionCode)).findAny().orElse(null);
            if(missionTable == null){
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ProfileFrameMissionTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: ProfileFrameMissionTable not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            ProfileDataDto.ProfileFrame profileFrame = profileDataDto.profileFrameList.stream().filter(i -> i.frameName.equals(missionTable.getGettingFrame())).findAny().orElse(null);
            if(profileFrame == null){
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ProfileFrame not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: ProfileFrame not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            profileFrame.GetFrame();
        }
    }
}
