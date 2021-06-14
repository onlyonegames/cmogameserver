package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import com.onlyonegames.eternalfantasia.domain.model.dto.ActiveSkillDataJsonDto;
import lombok.Data;

import java.util.List;

@Data
public class UserInfoDto {
    public String userGameName;
    public int level;
    public int exp;
    public int sexType;

    //액티브 스킬 정보
    public List<ActiveSkillDataJsonDto.SkillInfo> skillInfoList;
    //정령 정보
    public int pixieLevel;

    public void SetUserInfoDto(){

    }
}
