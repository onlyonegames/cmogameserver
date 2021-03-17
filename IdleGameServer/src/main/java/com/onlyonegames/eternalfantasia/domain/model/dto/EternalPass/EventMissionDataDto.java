package com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.util.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class EventMissionDataDto {
    public List<MissionsDataDto.MissionData> dailyMissionsData;
    public List<MissionsDataDto.MissionData> weeklyMissionsData;
    public List<MissionsDataDto.MissionData> questMissionsData;

    private MissionsDataDto.MISSION_TYPE GetMissionType(String missionTypeName){
        for(MissionsDataDto.MISSION_TYPE mission_type : MissionsDataDto.MISSION_TYPE.values()) {
            if(mission_type.name().equalsIgnoreCase(missionTypeName))
                return mission_type;
        }
        return null;
    }

    public boolean CheckMission(String missionTypeName, String missionParamStr, int specialAddActionCount, List<EternalPassDailyMissionTable> dailyMissionTableList, List<EternalPassWeekMissionTable> weeklyMissionTableList, List<EternalPassQuestMissionTable> questMissionTableList) {
        boolean isChanged = false;
        //일일 업적 체크
        if(CheckDailyMission(missionTypeName, missionParamStr, dailyMissionsData, dailyMissionTableList, specialAddActionCount))
            isChanged = true;
        //주간 업적 체크
        if(CheckWeeklyMission(missionTypeName, missionParamStr, weeklyMissionsData, weeklyMissionTableList, specialAddActionCount))
            isChanged = true;
        //퀘스트 업적 체크
        if(CheckQuestMission(missionTypeName, missionParamStr, questMissionsData, questMissionTableList, specialAddActionCount))
            isChanged = true;

        return isChanged;
    }

    public boolean CheckMission(String missionTypeName, String missionParamStr, List<EternalPassDailyMissionTable> dailyMissionTableList, List<EternalPassWeekMissionTable> weeklyMissionTableList, List<EternalPassQuestMissionTable> questMissionTableList) {
        boolean isChanged = false;
        //일일 업적 체크
        if(CheckDailyMission(missionTypeName, missionParamStr, dailyMissionsData, dailyMissionTableList, 1))
            isChanged = true;
        //주간 업적 체크
        if(CheckWeeklyMission(missionTypeName, missionParamStr, weeklyMissionsData, weeklyMissionTableList, 1))
            isChanged = true;
        //퀘스트 업적 체크
        if(CheckQuestMission(missionTypeName, missionParamStr, questMissionsData, questMissionTableList, 1))
            isChanged = true;

        return isChanged;
    }

    private boolean CheckDailyMission(String missionTypeName, String missionParamStr, List<MissionsDataDto.MissionData> missionDataList, List<EternalPassDailyMissionTable> dailyMissionTableList, int addActionCount) {
        boolean isChanged = false;
        for(MissionsDataDto.MissionData missionData : missionDataList) {
            if(missionData.success)
                continue;
            EternalPassDailyMissionTable missionTable = dailyMissionTableList.stream()
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
            missionData.actionCount = MathHelper.Clamp(missionData.actionCount, 0, missionData.goalCount);

            if(missionData.actionCount >= missionData.goalCount) {
                missionData.success = true;
                //dailyRewardPoint += missionTable.getGettingPoint();
            }
            isChanged = true;
        }
        return isChanged;
    }

    private boolean CheckWeeklyMission(String missionTypeName, String missionParamStr, List<MissionsDataDto.MissionData> missionDataList, List<EternalPassWeekMissionTable> weeklyMissionTableList, int addActionCount) {
        boolean isChanged = false;
        for(MissionsDataDto.MissionData missionData : missionDataList) {
            if(missionData.success)
                continue;
            EternalPassWeekMissionTable missionTable = weeklyMissionTableList.stream()
                    .filter(weeklyMissionTable -> weeklyMissionTable.getCode().equals(missionData.code))
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
                //weeklyRewardPoint += missionTable.getGettingPoint();
            }
            isChanged = true;
        }
        return isChanged;
    }

    private boolean CheckQuestMission(String missionTypeName, String missionParamStr, List<MissionsDataDto.MissionData> missionDataList, List<EternalPassQuestMissionTable> questMissionTableList, int addActionCount) {
        boolean isChanged = false;
        for(MissionsDataDto.MissionData missionData : missionDataList) {
            if(missionData.success)
                continue;
            EternalPassQuestMissionTable missionTable = questMissionTableList.stream()
                    .filter(questMissionTable -> questMissionTable.getCode().equals(missionData.code))
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
            }
            isChanged = true;
        }
        return isChanged;
    }

    public void ResetAll() {
        DailyMissionsReset();
        WeeklyMissionsReset();
        QuestMissionsReset();
    }
    public void DailyMissionsReset(){
        for(MissionsDataDto.MissionData missionData : dailyMissionsData){
            missionData.Reset();
        }
    }

    public void WeeklyMissionsReset(){
        for(MissionsDataDto.MissionData missionData : weeklyMissionsData) {
            missionData.Reset();
        }
    }

    public void QuestMissionsReset(){
        for(MissionsDataDto.MissionData missionData : questMissionsData) {
            missionData.Reset();
        }
    }
}
