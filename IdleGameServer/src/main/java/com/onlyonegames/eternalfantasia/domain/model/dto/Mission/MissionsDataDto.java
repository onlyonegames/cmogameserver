package com.onlyonegames.eternalfantasia.domain.model.dto.Mission;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MissionsDataDto {

    public enum MISSION_TYPE {
        /*이터널패스에 의해 새로 생긴 업적 종류*/
        LOGIN_COUNT(Arrays.asList(MISSION_PARAM.STAGE,MISSION_PARAM.EMPTY)),
        SPEND_FATIGABILITY(Arrays.asList(MISSION_PARAM.STAGE,MISSION_PARAM.EMPTY)),
        ERNING_GOLD(Arrays.asList(MISSION_PARAM.STAGE,MISSION_PARAM.EMPTY)),
        SPEND_LINKFORCEPONT(Arrays.asList(MISSION_PARAM.STAGE,MISSION_PARAM.EMPTY)),
        DIVIDE_EQUIPMENT(Arrays.asList(MISSION_PARAM.STAGE,MISSION_PARAM.EMPTY)),
        SPEND_FATIGABILITY_PORTION(Arrays.asList(MISSION_PARAM.STAGE,MISSION_PARAM.EMPTY)),
        PASS_DAILY_MISSION_CLEAR(Arrays.asList(MISSION_PARAM.STAGE,MISSION_PARAM.EMPTY)),
        PASS_WEEKLY_MISSION_CLEAR(Arrays.asList(MISSION_PARAM.STAGE,MISSION_PARAM.EMPTY)),
        /**/

        STAGE_CLEAR(Arrays.asList(MISSION_PARAM.STAGE,MISSION_PARAM.EMPTY)),
        GET_FIELD_OBJECT(Arrays.asList(MISSION_PARAM.EMPTY)),
        PRODUCTION_EQUIPMENT(Arrays.asList(MISSION_PARAM.EMPTY)),
        STRENGTHEN_EQUIPMENT(Arrays.asList(MISSION_PARAM.EMPTY)),
        GIVE_PRESENTS_TO_HERO(Arrays.asList(MISSION_PARAM.EMPTY)),
        LEARN_LINKFORCE(Arrays.asList(MISSION_PARAM.EMPTY)),
        GET_EXPEDITION_RESULT(Arrays.asList(MISSION_PARAM.EMPTY)),
        PLAY_HERO_TOWER(Arrays.asList(MISSION_PARAM.EMPTY)),
        PLAY_ORDEAL_DUNGEON(Arrays.asList(MISSION_PARAM.EMPTY)),
        PLAY_ANCIENT_DRAGON(Arrays.asList(MISSION_PARAM.EMPTY)),
        PLAY_ARENA(Arrays.asList(MISSION_PARAM.EMPTY)),
        PROMOTION_EQUIPMENT(Arrays.asList(MISSION_PARAM.EMPTY)),
        QUILITY_RESMELT_EQUIPMENT(Arrays.asList(MISSION_PARAM.EQUIPMENT_QUILITY,MISSION_PARAM.EMPTY)),
        LEVELUP_LINKABILITY(Arrays.asList(MISSION_PARAM.LINKABILITY,MISSION_PARAM.EMPTY)),
        STEP_UP_GOODFEELING(Arrays.asList(MISSION_PARAM.GOODFEELING,MISSION_PARAM.EMPTY)),
        RELATION_START_LEGION_HERO(Arrays.asList(MISSION_PARAM.EMPTY)),
        BUY_MAIN_SHOP(Arrays.asList(MISSION_PARAM.EMPTY)),
        BUY_ARENA_SHOP(Arrays.asList(MISSION_PARAM.EMPTY)),
        BUY_ANCIENT_SHOP(Arrays.asList(MISSION_PARAM.EMPTY)),
        USING_GOTCHA(Arrays.asList(MISSION_PARAM.EMPTY)),
        CHAPTER_CLEAR(Arrays.asList(MISSION_PARAM.CHAPTER)),
        REVOLUTION_LINK_WEAPON(Arrays.asList(MISSION_PARAM.LINK_WEAPON)),
        GOTCHA_NEW_HERO(Arrays.asList(MISSION_PARAM.EMPTY)),
        GETTING_EQUIPMENT(Arrays.asList(MISSION_PARAM.EQUIPMENT_GRADE)),
        LEVEL_UP_MAIN_HERO(Arrays.asList(MISSION_PARAM.HERO_LEVEL)),
        CLEAR_ANCIENT_DRAGON(Arrays.asList(MISSION_PARAM.EMPTY)),
        GETTING_ARENA_POINT(Arrays.asList(MISSION_PARAM.ARENA_POINT)),
        MONSTER_KILL_COUNT(Arrays.asList(MISSION_PARAM.CHAPTER)),
        GETTING_ARENA_TIER(Arrays.asList(MISSION_PARAM.ARENA_TIER, MISSION_PARAM.EMPTY));

        private List<MISSION_PARAM> paramList;
        MISSION_TYPE(List<MISSION_PARAM> paramList){
            this.paramList = paramList;
        }

        public boolean hasParam(String paramStr) {
            return paramList.stream()
                    .anyMatch(param -> param.hasParam(paramStr));
        }
    }

    public enum MISSION_PARAM {
        CHAPTER(Arrays.asList("Chapter1",
                "Chapter2","Chapter3","Chapter4","Chapter5","Chapter6","Chapter7",
                "Chapter8","Chapter9","Chapter10","Chapter11","Chapter12","Chapter13","Chapter14")),
        STAGE(Arrays.asList("1-5","1-10","1-15","1-20",
                "2-5","2-10","2-15","2-20",
                "3-5","3-10","3-15","3-20",
                "4-5","4-10","4-15","4-20",
                "5-5","5-10","5-15","5-20",
                "6-5","6-10","6-15","6-20",
                "7-5","7-10","7-15","7-20",
                "8-5","8-10","8-15","8-20",
                "9-5","9-10","9-15","9-20",
                "10-5","10-10","10-15","10-20",
                "11-5","11-10","11-15","11-20",
                "12-5","12-10","12-15","12-20",
                "13-5","13-10","13-15","13-20",
                "14-5","14-10","14-15","14-20")),
        GOODFEELING(Arrays.asList("5레벨","10레벨","20레벨","30레벨","40레벨","50레벨","60레벨")),
        LINK_WEAPON(Arrays.asList("2단계 진화","3단계 진화","4단계 진화","5단계 진화","6단계 진화")),
        LINKABILITY(Arrays.asList("2단계","3단계","4단계","5단계","6단계")),
        //GOTCHA_COUNT_NEW_HERO(Arrays.asList("5명","7명","10명","13명","15명","20명")),
        EQUIPMENT_GRADE(Arrays.asList("Hero","Legend","Divine","Ancient")),
        EQUIPMENT_QUILITY(Arrays.asList("C","B","A","S","SS","SSS")),
        HERO_LEVEL(Arrays.asList("5 달성","10 달성","15 달성","20 달성","25 달성","30 달성","35 달성","40 달성","45 달성","50 달성","55 달성","60 달성")),
        ARENA_POINT(Arrays.asList("50pt","100pt","150pt","250pt","350pt","450pt","600pt","750pt",
                "900pt","1100pt","1300pt","1500pt","1800pt","2000pt","2300pt","2600pt","2900pt","3200pt","3400pt")),
        ARENA_TIER(Arrays.asList("bronze_5", "silver_5", "gold_5", "platinum_5", "top3")),
        EMPTY(Arrays.asList("empty"));


        private List<String> params;
        MISSION_PARAM(List<String> params){
            this.params = params;
        }

        public static MISSION_PARAM findByParamStr(String paramStr){
            return Arrays.stream(MISSION_PARAM.values())
                    .filter(mission_param -> mission_param.hasParam(paramStr))
                    .findAny()
                    .orElse(EMPTY);
        }

        public boolean hasParam(String paramStr) {
            return params.stream()
                    .anyMatch(param -> param.equals(paramStr));
        }

        public List<String> getParams() {
            return params;
        }
    }

    public static class MissionData {
        //코드
        public String code;
        //성공 여부
        public boolean success;
        //해당 미션에서 요구하는 액션을 실행한 횟수. (해당 횟수가 미션에서 요구하는 횟수에 도달하면 미션 성공)
        public int actionCount;
        //해당 미션에서 요구하는 액션의 총 횟수.
        public int goalCount;
        //해당 보상 받았는지 여부
        public boolean rewardReceived;
        //한번에 두스텝 이상의 미션을 성공 했을때 해당 변수에 성공 스텝의 스택 을 저장 해두고, 보상 받으면서 다음 스택의 값보다 해당 변수에 값이 크면
        // 다음 스택의 미션은 무조건 success = true 로 해준다.
        public int alreadyOpenStepMax;
        public void Reset(){
            success = false;
            actionCount = 0;
            rewardReceived = false;
        }
    }

    public static class RewardInfoData {
        public String code;
        public boolean rewardReceived;

        public void Reset(){
            rewardReceived = false;
        }
    }

    public List<MissionData> dailyMissionsData;
    public List<RewardInfoData> dailyRewardInfo;
    public int dailyRewardPoint;
    public List<RewardInfoData> weeklyRewardInfo;
    public int weeklyRewardPoint;
    public List<MissionData> weeklyMissionsData;
    public List<MissionData> questMissionsData;

    private MISSION_TYPE GetMissionType(String missionTypeName){
        for(MISSION_TYPE mission_type : MISSION_TYPE.values()) {
            if(mission_type.name().equalsIgnoreCase(missionTypeName))
                return mission_type;
        }
        return null;
    }

    public boolean CheckMission(String missionTypeName, String missionParamStr, int specialAddActionCount, List<DailyMissionTable> dailyMissionTableList, List<WeeklyMissionTable> weeklyMissionTableList, List<QuestMissionTable> questMissionTableList) {
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

    public boolean CheckMission(String missionTypeName, String missionParamStr, List<DailyMissionTable> dailyMissionTableList, List<WeeklyMissionTable> weeklyMissionTableList, List<QuestMissionTable> questMissionTableList) {
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

    private boolean CheckDailyMission(String missionTypeName, String missionParamStr, List<MissionData> missionDataList, List<DailyMissionTable> dailyMissionTableList, int addActionCount) {
        boolean isChanged = false;
        for(MissionData missionData : missionDataList) {
            if(missionData.success)
                continue;
            DailyMissionTable missionTable = dailyMissionTableList.stream()
                    .filter(dailyMissionTable -> dailyMissionTable.getCode().equals(missionData.code))
                    .findAny()
                    .orElse(null);
            if(!missionTypeName.equals(missionTable.getMissionTypeName()))
                continue;
            if (!missionParamStr.equals(missionTable.getMissionParamName()))
                continue;

            MISSION_TYPE mission_type = GetMissionType(missionTable.getMissionTypeName());
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

    private boolean CheckWeeklyMission(String missionTypeName, String missionParamStr, List<MissionData> missionDataList, List<WeeklyMissionTable> weeklyMissionTableList, int addActionCount) {
        boolean isChanged = false;
        for(MissionData missionData : missionDataList) {
            if(missionData.success)
                continue;
            WeeklyMissionTable missionTable = weeklyMissionTableList.stream()
                    .filter(weeklyMissionTable -> weeklyMissionTable.getCode().equals(missionData.code))
                    .findAny()
                    .orElse(null);
            if(!missionTypeName.equals(missionTable.getMissionTypeName()))
                continue;
            if (!missionParamStr.equals(missionTable.getMissionParamName()))
                continue;

            MISSION_TYPE mission_type = GetMissionType(missionTable.getMissionTypeName());
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

    private boolean CheckQuestMission(String missionTypeName, String missionParamStr, List<MissionData> missionDataList, List<QuestMissionTable> questMissionTableList, int addActionCount) {
        boolean isChanged = false;
        for(MissionData missionData : missionDataList) {
            if(missionData.success)
                continue;
            QuestMissionTable missionTable = questMissionTableList.stream()
                    .filter(questMissionTable -> questMissionTable.getCode().equals(missionData.code))
                    .findAny()
                    .orElse(null);
            if(!missionTypeName.equals(missionTable.getMissionTypeName()))
                continue;
            if (!missionParamStr.equals(missionTable.getMissionParamName()))
                continue;

            MISSION_TYPE mission_type = GetMissionType(missionTable.getMissionTypeName());
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
    public List<MissionData> ImportQuestMissionSendToClient(List<QuestMissionTable> questMissionTableList){

        List<MissionData> returnMissionDataList = new ArrayList<>();
        MissionData tempMissionData;
        String checkMissionType = "";
        String checkMissionParamName = "";
        for(MissionData missionData : questMissionsData) {
            QuestMissionTable missionTable = questMissionTableList.stream()
                    .filter(questMissionTable -> questMissionTable.getCode().equals(missionData.code))
                    .findAny()
                    .orElse(null);

            if(missionTable.getMissionTypeName().equals(checkMissionType)) {
                //장비생성의 경우 등급마다 다른퀘스트로 등록 되어있어 따로 처리한다.
                if(missionTable.getMissionTypeName().equals(MISSION_TYPE.GETTING_EQUIPMENT.name())) {
                    if(missionTable.getMissionParamName().equals(checkMissionParamName))
                        continue;
                }
                else
                    continue;
            }


            //우선순위1
            if(missionData.goalCount > missionData.actionCount) {
                returnMissionDataList.add(missionData);
                checkMissionType = missionTable.getMissionTypeName();
                checkMissionParamName = missionTable.getMissionParamName();
                continue;
            }
            //우선순위2
            if(!missionData.rewardReceived) {
                returnMissionDataList.add(missionData);
                checkMissionType = missionTable.getMissionTypeName();
                checkMissionParamName = missionTable.getMissionParamName();
                continue;
            }
            //마지막
            int nextTableId = missionTable.getId();
            int fixeNextTableId = ++nextTableId;
            QuestMissionTable nextMissionTable = questMissionTableList.stream()
                    .filter(questMissionTable -> questMissionTable.getId() == fixeNextTableId)
                    .findAny()
                    .orElse(null);
            if(nextMissionTable == null || !nextMissionTable.getMissionTypeName().equals(missionTable.getMissionTypeName())){
                returnMissionDataList.add(missionData);
                checkMissionType = missionTable.getMissionTypeName();
                checkMissionParamName = missionTable.getMissionParamName();
            }
        }

        return returnMissionDataList;
    }
    //한꺼번에 여러 스탭의 조건을 넘는 이벤트의 경우는 해당 함수로 처리.
    public boolean CheckQuestMission(String missionTypeName, String missionParamStr, int alreadyOpenStepMax, List<QuestMissionTable> questMissionTableList) {
        boolean isChanged = false;
        for(MissionData missionData : questMissionsData) {
            if(missionData.success)
                continue;
            QuestMissionTable missionTable = questMissionTableList.stream()
                    .filter(questMissionTable -> questMissionTable.getCode().equals(missionData.code))
                    .findAny()
                    .orElse(null);
            if(!missionTypeName.equals(missionTable.getMissionTypeName()))
                continue;
            if (!missionParamStr.equals(missionTable.getMissionParamName()))
                continue;

            MISSION_TYPE mission_type = GetMissionType(missionTable.getMissionTypeName());
            if(mission_type == null)
                throw new MyCustomException("Fail! -> Cause: Cant find MISSION_TYPE.", ResponseErrorCode.NOT_EXIST_CODE);

            if(!mission_type.hasParam(missionTable.getMissionParamName()))
                continue;
            missionData.actionCount++;
            if(missionData.actionCount >= missionData.goalCount) {
                missionData.success = true;
                missionData.alreadyOpenStepMax = alreadyOpenStepMax;
            }
            isChanged = true;
        }
        return isChanged;
    }

    public void DailyMissionsReset(){
        for(MissionData missionData : dailyMissionsData){
            missionData.Reset();
        }
        for(RewardInfoData rewardInfoData : dailyRewardInfo){
            rewardInfoData.Reset();
        }
        dailyRewardPoint = 0;
    }

    public void WeeklyMissionsReset(){
        for(MissionData missionData : weeklyMissionsData) {
            missionData.Reset();
        }
        for(RewardInfoData rewardInfoData : weeklyRewardInfo){
            rewardInfoData.Reset();
        }
        weeklyRewardPoint = 0;
    }
}
