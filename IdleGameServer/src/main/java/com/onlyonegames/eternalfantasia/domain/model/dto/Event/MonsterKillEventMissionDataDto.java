package com.onlyonegames.eternalfantasia.domain.model.dto.Event;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.MonsterKillEventTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.QuestMissionTable;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MonsterKillEventMissionDataDto {
    public Long eventId;
    public List<MissionsDataDto.MissionData> monsterKillEventMissionsData;

    private MissionsDataDto.MISSION_TYPE GetMissionType(String missionTypeName){
        for(MissionsDataDto.MISSION_TYPE mission_type : MissionsDataDto.MISSION_TYPE.values()) {
            if(mission_type.name().equalsIgnoreCase(missionTypeName))
                return mission_type;
        }
        return null;
    }

    public boolean CheckPossibleMission(){
        for(MissionsDataDto.MissionData temp: this.monsterKillEventMissionsData){
            if(!temp.rewardReceived)
                return true;
        }
        return false;
    }

    public boolean CheckMission(String missionTypeName, String missionParamStr, List<MonsterKillEventTable> monsterMissionTableList) {
        boolean isChanged = false;
        //누적 레벨 업적 체크
        if(CheckMonsterMission(missionTypeName, missionParamStr, monsterKillEventMissionsData, monsterMissionTableList))
            isChanged = true;

        return isChanged;
    }

    private boolean CheckMonsterMission(String missionTypeName, String missionParamStr, List<MissionsDataDto.MissionData> missionDataList, List<MonsterKillEventTable> monsterMissionTableList) {
        boolean isChanged = false;
        for(MissionsDataDto.MissionData missionData : missionDataList) {
            if(missionData.success)
                continue;
            MonsterKillEventTable missionTable = monsterMissionTableList.stream()
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
            missionData.actionCount += 1;
            if(missionData.actionCount >= missionData.goalCount) {
                missionData.success = true;
                //dailyRewardPoint += missionTable.getGettingPoint();
            }
            isChanged = true;
        }
        return isChanged;
    }

    public List<MissionsDataDto.MissionData> ImportQuestMissionSendToClient(List<MonsterKillEventTable> monsterKillEventTableList){

        List<MissionsDataDto.MissionData> returnMissionDataList = new ArrayList<>();
        MissionsDataDto.MissionData tempMissionData;
        String checkMissionType = "";
        String checkMissionParamName = "";
        for(MissionsDataDto.MissionData missionData : monsterKillEventMissionsData) {
            MonsterKillEventTable monsterKillEventTable = monsterKillEventTableList.stream()
                    .filter(questMissionTable -> questMissionTable.getCode().equals(missionData.code))
                    .findAny()
                    .orElse(null);

            if(monsterKillEventTable.getMissionParamName().equals(checkMissionParamName)) {
                //장비생성의 경우 등급마다 다른퀘스트로 등록 되어있어 따로 처리한다.
//                if(monsterKillEventTable.getMissionTypeName().equals(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name())) {
//                    if(monsterKillEventTable.getMissionParamName().equals(checkMissionParamName))
//                        continue;
//                }
//                else
                continue;
            }


            //우선순위1
            if(missionData.goalCount > missionData.actionCount) {
                returnMissionDataList.add(missionData);
                //checkMissionType = monsterKillEventTable.getMissionTypeName();
                checkMissionParamName = monsterKillEventTable.getMissionParamName();
                continue;
            }
            //우선순위2
            if(!missionData.rewardReceived) {
                returnMissionDataList.add(missionData);
                //checkMissionType = monsterKillEventTable.getMissionTypeName();
                checkMissionParamName = monsterKillEventTable.getMissionParamName();
                continue;
            }
            //마지막
            int nextTableId = monsterKillEventTable.getId();
            int fixeNextTableId = ++nextTableId;
            MonsterKillEventTable nextMissionTable = monsterKillEventTableList.stream()
                    .filter(questMissionTable -> questMissionTable.getId() == fixeNextTableId)
                    .findAny()
                    .orElse(null);
            if(nextMissionTable == null || !nextMissionTable.getMissionParamName().equals(monsterKillEventTable.getMissionParamName())){
                returnMissionDataList.add(missionData);
                //checkMissionType = monsterKillEventTable.getMissionTypeName();
                checkMissionParamName = monsterKillEventTable.getMissionParamName();
            }
        }

        return returnMissionDataList;
    }
}
