package com.onlyonegames.eternalfantasia.domain.service.Managementtool.HeroInfo;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.SetSkillInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.UserSkillInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyMainHeroSkill;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.SkillTable;
import com.onlyonegames.eternalfantasia.domain.repository.MyMainHeroSkillRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
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
public class UserSkillInfoService {
    private final MyMainHeroSkillRepository myMainHeroSkillRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> findByUseridUser(Long userId, Map<String, Object> map){
        List<UserSkillInfoDto> userSkillInfoDtos = new ArrayList<>();
        List<MyMainHeroSkill> myMainHeroSkills = myMainHeroSkillRepository.findAllByUseridUser(userId);
        List<SkillTable> skillTableList = gameDataTableService.SkillTableList();
        for(MyMainHeroSkill myMainHeroSkill:myMainHeroSkills){
            UserSkillInfoDto userSkillInfoDto = new UserSkillInfoDto();

            SkillTable skillTable = skillTableList.stream()
                    .filter(skill -> skill.getId() == myMainHeroSkill.getBaseSkillid())
                    .findAny()
                    .orElse(null);
            if(skillTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: Cant Find Data.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Cant Find Data.", ResponseErrorCode.NOT_FIND_DATA);
            }

            userSkillInfoDto.setId(myMainHeroSkill.getBaseSkillid());
            userSkillInfoDto.setLevel(myMainHeroSkill.getLevel());
            userSkillInfoDto.setSkillName(skillTable.getSkillName());
            userSkillInfoDtos.add(userSkillInfoDto);
        }
        map.put("Skill", userSkillInfoDtos);
        return map;
    }

    public Map<String, Object> setUserHeroSkill(SetSkillInfoDto dto, Map<String, Object> map){
        List<MyMainHeroSkill> myMainHeroSkillList = myMainHeroSkillRepository.findAllByUseridUser(dto.userId);
        List<SkillTable> skillTableList = gameDataTableService.SkillTableList();
        List<UserSkillInfoDto> userSkillInfoDtos = new ArrayList<>();
        for(int i = 0; i < myMainHeroSkillList.size(); i++){
            myMainHeroSkillList.get(i).LevelChange(dto.skillLevel.get(i));
        }
        for(MyMainHeroSkill myMainHeroSkill:myMainHeroSkillList){
            UserSkillInfoDto userSkillInfoDto = new UserSkillInfoDto();

            SkillTable skillTable = skillTableList.stream()
                    .filter(skill -> skill.getId() == myMainHeroSkill.getBaseSkillid())
                    .findAny()
                    .orElse(null);
            if(skillTable == null) {
                errorLoggingService.SetErrorLog(dto.getUserId(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: Cant Find Data.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Cant Find Data.", ResponseErrorCode.NOT_FIND_DATA);
            }

            userSkillInfoDto.setId(myMainHeroSkill.getBaseSkillid());
            userSkillInfoDto.setLevel(myMainHeroSkill.getLevel());
            userSkillInfoDto.setSkillName(skillTable.getSkillName());
            userSkillInfoDtos.add(userSkillInfoDto);
        }
        map.put("Skill", userSkillInfoDtos);
        return map;
    }
}
