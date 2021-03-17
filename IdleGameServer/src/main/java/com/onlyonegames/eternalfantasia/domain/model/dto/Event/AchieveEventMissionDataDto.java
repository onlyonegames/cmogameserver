package com.onlyonegames.eternalfantasia.domain.model.dto.Event;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.AchieveEventTable;

import java.util.List;

public class AchieveEventMissionDataDto {
    public List<MissionsDataDto.MissionData> levelMissionsData;

    private MissionsDataDto.MISSION_TYPE GetMissionType(String missionTypeName){
        for(MissionsDataDto.MISSION_TYPE mission_type : MissionsDataDto.MISSION_TYPE.values()) {
            if(mission_type.name().equalsIgnoreCase(missionTypeName))
                return mission_type;
        }
        return null;
    }

    public boolean CheckPossibleMission(){
        for(MissionsDataDto.MissionData temp: this.levelMissionsData){
            if(!temp.rewardReceived)
                return true;
        }
        return false;
    }

    public boolean CheckMission(String missionTypeName, String missionParamStr, List<AchieveEventTable> levelMissionTableList) {
        boolean isChanged = false;
        //누적 레벨 업적 체크
        if(CheckLevelMission(missionTypeName, missionParamStr, levelMissionsData, levelMissionTableList, 1))
            isChanged = true;

        return isChanged;
    }

    private boolean CheckLevelMission(String missionTypeName, String missionParamStr, List<MissionsDataDto.MissionData> missionDataList, List<AchieveEventTable> levelMissionTableList, int addActionCount) {
        boolean isChanged = false;
        for(MissionsDataDto.MissionData missionData : missionDataList) {
            if(missionData.success)
                continue;
            AchieveEventTable missionTable = levelMissionTableList.stream()
                    .filter(dailyMissionTable -> dailyMissionTable.getCode().equals(missionData.code))
                    .findAny()
                    .orElse(null);
            if(!missionTypeName.equals(missionTable.getMissionTypeName()))
                continue;
            if (!missionParamStr.equals(missionTable.getMissionParamName()))
                continue;

            MissionsDataDto.MISSION_TYPE mission_type = GetMissionType(missionTable.getMissionTypeName());
            if(mission_type == null)
                throw new MyCustomException("Fail! -> Cause: Cant find MISSION_TYPE.", ResponseErrorCode.NOT_EXIST_CODE);

            if(!mission_type.hasParam(missionTable.getMissionParamName()))
                continue;
            missionData.actionCount += addActionCount;
            if(missionData.actionCount >= missionData.goalCount) {
                missionData.success = true;
                //dailyRewardPoint += missionTable.getGettingPoint();
            }
            isChanged = true;
        }
        return isChanged;
    }
}
