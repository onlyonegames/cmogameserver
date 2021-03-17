package com.onlyonegames.eternalfantasia.domain.service.Companion;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.EventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceDto.LinkforceOpenDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceDto.MyLinkforceInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CurrencyLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.initJsonDataDto.InitJsonForLinkforceDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyLinkforceInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.InitJsonDatasForFirstUser;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.LinkforceTalentsTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.herostable;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyLinkforceInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.util.StringMaker;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyLinkforceInfoService {
    private final MyLinkforceInfoRepository myLinkforceInfoRepository;
    private final MyCharactersRepository myCharactersRepository;
    private final UserRepository userRepository;
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;

    public Map<String, Object> GetLinkforceInfo(Long userId, Map<String, Object> map){

        MyLinkforceInfo myLinkforceInfo = myLinkforceInfoRepository.findByUseridUser(userId)
                .orElse(null);
        if(myLinkforceInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myLinkforceInfo not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myLinkforceInfo not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        LinkforceOpenDtosList myLinkforceDtosList = JsonStringHerlper.ReadValueFromJson(myLinkforceInfo.getJson_LinkforceInfos(), LinkforceOpenDtosList.class);
        map.put("myLinkforceInfoList", myLinkforceDtosList.openInfoList);
        return map;
    }

    public Map<String, Object> LearnLinkforce(Long userId, Long characterId, int linkforceId, Map<String, Object> map){

        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
        MyCharacters myCharacters = myCharactersList.stream()
                .filter(a -> a.getId().equals(characterId))
                .findAny()
                .orElse(null);
        if(myCharacters == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find Character", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Can't Find Character", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<herostable> herostableList = gameDataTableService.HerosTableList();
        herostable myhero = herostableList.stream().filter(i -> i.getCode().equals(myCharacters.getCodeHerostable())).findAny().orElse(null);
        if(myhero == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find herostable", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Can't Find herostable", ResponseErrorCode.NOT_FIND_DATA);
        }

        //해당 유저의 MyLinkforceInfo 검색
        MyLinkforceInfo myLinkforceInfo = myLinkforceInfoRepository.findByUseridUser(userId)
                .orElse(null);
        if(myLinkforceInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myLinkforceInfo not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myLinkforceInfo not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        //json_LinkforceInfos을 활용 LinkforceInfoDtosList를 만들고 linkforceId에 해당하는 linkforceInfoDto Get
        LinkforceOpenDtosList linkforceOpenDtosList = JsonStringHerlper.ReadValueFromJson(myLinkforceInfo.getJson_LinkforceInfos(), LinkforceOpenDtosList.class);
        LinkforceOpenDtosList.CompanionLinkforceOpenInfo companionLinkforceInfo = linkforceOpenDtosList.openInfoList.stream()
                .filter(a -> a.code.equals(myCharacters.getCodeHerostable()))
                .findAny()
                .orElse(null);
        if(companionLinkforceInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Not find companionLinkforceInfo.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Not find companionLinkforceInfo.", ResponseErrorCode.NOT_FIND_DATA);
        }

        int isOpen = companionLinkforceInfo.linkforceOpenInfoList.get(linkforceId);

        if(isOpen > 0) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_MORE_OPENTALENT.getIntegerValue(), "Fail! -> Cause: Cant more open talent", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Cant more open talent", ResponseErrorCode.CANT_MORE_OPENTALENT);
        }
        //모든 동료의 TalentTree 형태는 동일하며 해당 트리 정보(링크포스트리정보)는 InitJsonDatasForFirstUser에 8번째 row에 있음. 각 Linkforce의 id 는 리스트 내에서의 index와 같다.
        List<InitJsonDatasForFirstUser> initJsonDatasForFirstUserList = gameDataTableService.InitJsonDatasForFirstUserList();
        InitJsonDatasForFirstUser initJsonDatasForFirstUser = initJsonDatasForFirstUserList.get(8);
        String linkforceSkillTreeJson = initJsonDatasForFirstUser.getInitJson();
        InitJsonForLinkforceDto initJsonForLinkforceDto = JsonStringHerlper.ReadValueFromJson(linkforceSkillTreeJson, InitJsonForLinkforceDto.class);
        InitJsonForLinkforceDto.SkillTree skillTree = initJsonForLinkforceDto.skilltree.get(linkforceId);
        for(int dependencieId : skillTree.skill_Dependencies)
        {
            int isDependencieOpen = companionLinkforceInfo.linkforceOpenInfoList.get(dependencieId);
            if(isDependencieOpen < 1) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_YET_OPEN_TALENT.getIntegerValue(), "Fail! -> Cause: Not yet Open talent.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Not yet Open talent.", ResponseErrorCode.NOT_YET_OPEN_TALENT);
            }
        }
        // Linkforce table통해서 GFCondition 체크
        List<LinkforceTalentsTable> linkforceTalentsTableList = gameDataTableService.LinkforceTalentsTableList();
        LinkforceTalentsTable linkforceTalentsTable = linkforceTalentsTableList.stream()
                .filter(a -> a.getOwner().equals(myCharacters.getCodeHerostable()) && a.getTalentID() == linkforceId)
                .findAny()
                .orElse(null);
        if(linkforceTalentsTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Not find talentsTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Not find talentsTable.", ResponseErrorCode.NOT_FIND_DATA);
        }
        if(myCharacters.getLinkAbilityLevel() < linkforceTalentsTable.getGFCondition()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOODFEELING.getIntegerValue(), "Fail! -> Cause: Need more goodfeeling.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Need more goodfeeling.", ResponseErrorCode.NEED_MORE_GOODFEELING);
        }

        //코스트 감소 가능 체크
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }

        int spendCost = linkforceTalentsTable.getHaremCost();
        CurrencyLogDto currencyLogDto = new CurrencyLogDto();
        int previousValue = user.getLinkforcePoint();
        if(!user.SpendLinkforcePoint(spendCost)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_LINKFORCEPOINT.getIntegerValue(), "Fail -> Cause: Need More LinkforcePoint", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Need More LinkforcePoint", ResponseErrorCode.NEED_MORE_LINKFORCEPOINT);
        }
        currencyLogDto.setCurrencyLogDto("링크포스 오픈 - "+myhero.getName()+" "+linkforceId+"번", "링크포인트", previousValue, -spendCost, user.getLinkforcePoint());
        String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
        loggingService.setLogging(userId, 1, log);

        //링크포스 업데이트
        companionLinkforceInfo.linkforceOpenInfoList.set(linkforceId, 1);
        String linkforceInfoDtosListStr = JsonStringHerlper.WriteValueAsStringFromData(linkforceOpenDtosList);
        myLinkforceInfo.ResetLinkforceInfos(linkforceInfoDtosListStr);

        map.put("companionLinkforceInfo", companionLinkforceInfo);
        map.put("user", user);

        /*업적 : 체크 준비*/
        MyMissionsData myMissionsData = myMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyMissionData = myMissionsData.getJson_saveDataValue();
        MissionsDataDto myMissionsDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyMissionData, MissionsDataDto.class);
        boolean changedMissionsData = false;
        /* 업적 : 링크 포스 개방 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.LEARN_LINKFORCE.name(),"empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
        /*업적 : 미션 데이터 변경점 적용*/
        if(changedMissionsData) {
            jsonMyMissionData = JsonStringHerlper.WriteValueAsStringFromData(myMissionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMyMissionData);
            map.put("exchange_myMissionsData", true);
            map.put("myMissionsDataDto", myMissionsDataDto);
            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());
        }
        /*패스 업적 : 체크 준비*/
        MyEternalPassMissionsData myEternalPassMissionsData = myEternalPassMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myEternalPassMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEternalPassMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyEternalPassMissionData = myEternalPassMissionsData.getJson_saveDataValue();
        EventMissionDataDto myEternalPassMissionDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyEternalPassMissionData, EventMissionDataDto.class);
        boolean changedEternalPassMissionsData = false;
        /* 패스 업적 : 링크 포스 포인트 소모 미션 체크*/
        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.SPEND_LINKFORCEPONT.name(), "empty", spendCost, gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;

        /* 패스 업적 : 링크 포스 개방 미션 체크*/
        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.LEARN_LINKFORCE.name(),"empty", gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;

        /* 패스 업적 : 미션 데이터 변경점 적용*/
        if(changedEternalPassMissionsData) {
            jsonMyEternalPassMissionData = JsonStringHerlper.WriteValueAsStringFromData(myEternalPassMissionDataDto);
            myEternalPassMissionsData.ResetSaveDataValue(jsonMyEternalPassMissionData);
            map.put("exchange_myEternalPassMissionsData", true);
            map.put("myEternalPassMissionsDataDto", myEternalPassMissionDataDto);
            map.put("myEternalPassLastDailyMissionClearTime", myEternalPassMissionsData.getLastDailyMissionClearTime());
            map.put("myEternalPassLastWeeklyMissionClearTime", myEternalPassMissionsData.getLastWeeklyMissionClearTime());
        }
        return map;
   }

   //테스트 용
    public Map<String, Object> ResetLinkforce(Long userId, Map<String, Object> map){

        List<InitJsonDatasForFirstUser> initJsonDatasForFirstUserList = gameDataTableService.InitJsonDatasForFirstUserList();
        InitJsonDatasForFirstUser initJsonDatasForFirstUser = initJsonDatasForFirstUserList.get(8);
        String linkforceSkillTreeJson = initJsonDatasForFirstUser.getInitJson();
        InitJsonForLinkforceDto initJsonForLinkforceDto = JsonStringHerlper.ReadValueFromJson(linkforceSkillTreeJson, InitJsonForLinkforceDto.class);

        LinkforceOpenDtosList linkforceInfoDtosList = new LinkforceOpenDtosList();
        linkforceInfoDtosList.openInfoList = new ArrayList<>();
        for(int i = 0; i < 20; i++) {
            StringMaker.Clear();
            LinkforceOpenDtosList.CompanionLinkforceOpenInfo companionLinkforceInfo = new LinkforceOpenDtosList.CompanionLinkforceOpenInfo();
            StringMaker.stringBuilder.append("cr_");
            StringMaker.stringBuilder.append(String.format("%03d",i));
            companionLinkforceInfo.code = StringMaker.stringBuilder.toString();
            List<Integer> linkforceOpenInfoList = new ArrayList();
            companionLinkforceInfo.linkforceOpenInfoList = linkforceOpenInfoList;

            for(int j = 0; j < 165; j++) {
                linkforceOpenInfoList.add(0);
            }

            linkforceInfoDtosList.openInfoList.add(companionLinkforceInfo);
        }
        String result = JsonStringHerlper.WriteValueAsStringFromData(linkforceInfoDtosList);

        MyLinkforceInfo myLinkforceInfo = myLinkforceInfoRepository.findByUseridUser(userId)
                .orElse(null);
        if(myLinkforceInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myLinkforceInfo not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myLinkforceInfo not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        myLinkforceInfo.ResetLinkforceInfos( result );
        MyLinkforceInfoDto myLinkforceInfoDto = new MyLinkforceInfoDto();
        myLinkforceInfoDto.InitFromDbData(myLinkforceInfo);
        map.put("myLinkforceInfoList", myLinkforceInfoDto.getJson_LinkforceInfos());
        return map;
    }

}
