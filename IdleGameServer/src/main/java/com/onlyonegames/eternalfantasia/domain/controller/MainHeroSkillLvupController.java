package com.onlyonegames.eternalfantasia.domain.controller;

import java.util.HashMap;
import java.util.Map;
import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyMainHeroSkillBaseDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.MyMainHeroSkillLevelUpDto;
import com.onlyonegames.eternalfantasia.domain.service.MyMainHeroSkillsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ "/api/MainHeroSkillLvup" })
public class MainHeroSkillLvupController {

    @Autowired
    MyMainHeroSkillsService myMainHeroSkillsService;

    @PostMapping
    public ResponseDTO<Map<String, Object>> skillLvup(@RequestBody MyMainHeroSkillLevelUpDto myMainHeroSkillLevelUpDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myMainHeroSkillsService.skillLvup(userId,
                myMainHeroSkillLevelUpDto.getExclusiveType(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK,
                ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }
}