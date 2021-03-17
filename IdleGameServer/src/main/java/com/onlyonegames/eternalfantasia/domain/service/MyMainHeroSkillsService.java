package com.onlyonegames.eternalfantasia.domain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyMainHeroSkillBaseDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.MainHeroSkillExpandInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyMainHeroSkill;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.SkillTable;
import com.onlyonegames.eternalfantasia.domain.repository.MainHeroSkillExpandInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyMainHeroSkillRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
public class MyMainHeroSkillsService {

    @Autowired
    private MyMainHeroSkillRepository myMainHeroSkillRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GameDataTableService gameDataTableService;
    @Autowired
    private ErrorLoggingService errorLoggingService;
    public List<MyMainHeroSkill> findByUseridUser(Long userId) {
        return myMainHeroSkillRepository.findAllByUseridUser(userId);
    }

    public Map<String, Object> skillLvup(Long userId, String exclusiveType, Map<String, Object> map) {

        List<MainHeroSkillExpandInfo> mainHeroSkillExpandInfoList = gameDataTableService.MainHeroSkillExpandInfoList();

        List<String> sameTypeSkillCodes = new ArrayList<>();
        for(MainHeroSkillExpandInfo mainHeroSkillExpandInfo : mainHeroSkillExpandInfoList) {
            if(mainHeroSkillExpandInfo.getExclusiveType().equals(exclusiveType))
                sameTypeSkillCodes.add(mainHeroSkillExpandInfo.getCode());
        }

        List<SkillTable> skillTableList = gameDataTableService.SkillTableList();

        List<Integer> skillIdsList = new ArrayList<>();
        for(SkillTable skillTable : skillTableList) {
            String skillType = sameTypeSkillCodes.stream().filter( a-> a.equals(skillTable.getCode()))
                    .findAny().orElse(null);
            if(skillType != null) {
                skillIdsList.add(skillTable.getId());
            }
        }


        List<MyMainHeroSkill> myMainHeroSkillsList = myMainHeroSkillRepository.findAllByUseridUser(userId);
        if(myMainHeroSkillsList == null || myMainHeroSkillsList.size() == 0) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMainHeroSkill not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMainHeroSkill not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<MyMainHeroSkill> mySameTypeSkillList = new ArrayList<>();
        for(Integer skillId : skillIdsList) {
            MyMainHeroSkill myMainHeroSkill = myMainHeroSkillsList.stream().filter(a -> a.getActivatedSkillid() == skillId)
                    .findAny().orElse(null);
            if(myMainHeroSkill != null)
                mySameTypeSkillList.add(myMainHeroSkill);
        }

        boolean isMaxLevel = mySameTypeSkillList.get(0).getLevel() >= mainHeroSkillExpandInfoList.get(0).getMaxLevel();


        // 최대 등급 스킬의 최대 레벨은 더이상 레벨업 불가
        if (isMaxLevel) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_MORE_SKILL_LVUP.getIntegerValue(), "Fail -> Cause: Can't more level up", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Can't more level up", ResponseErrorCode.CANT_MORE_SKILL_LVUP);
        }

        int nowSkillLevel = mySameTypeSkillList.get(0).getLevel();

        //int nowLevelIndex = nowSkillLevel - 1;
        int needSp = 1;
        //스킬 레벨업 필요 골드 공식
        int needGold = (int)(100 + (100 * ((nowSkillLevel - 1) * (nowSkillLevel*0.1))));

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: user not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: user not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        int userGold = user.getGold();
        int userSp = user.getSkillPoint();
        // 골드 체크
        if (userGold < needGold) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail -> Cause: Need More Gold", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Need More Gold", ResponseErrorCode.NEED_MORE_GOLD);
        }
        // SP 체크
        if (userSp < needSp) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_SP.getIntegerValue(), "Fail -> Cause: Need More SP", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Need More SP", ResponseErrorCode.NEED_MORE_SP);
        }
//        int activatedSkillid = myMainHeroSkill.getActivatedSkillid();
//        // 맥스 레벨 체크
//        if (isMaxLevel) {
//            // activatedSkillid 를 다음 스킬아이디로 교체
//            activatedSkillid += 1;
//        }
        List<MyMainHeroSkillBaseDto> myMainHeroSkillBaseDtoList = new ArrayList<>();
        for(MyMainHeroSkill myMainHeroSkill : mySameTypeSkillList) {
            myMainHeroSkill.LevelUp();
            MyMainHeroSkillBaseDto myMainHeroSkillBaseDto = new MyMainHeroSkillBaseDto();
            myMainHeroSkillBaseDto.InitFromDbData(myMainHeroSkill);
            myMainHeroSkillBaseDtoList.add(myMainHeroSkillBaseDto);
        }

        map.put("mySameTypeSkillList", myMainHeroSkillBaseDtoList);
        user.SpendGold(needGold);
        user.SpendSkillPoint(needSp);
        map.put("user", user); /* 골드와 스킬 포인트를 사용후 반환 */
        return map;
    }
}