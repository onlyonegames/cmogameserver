package com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool;

import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserInfoDto {
    Long Id;
    String socialId;
    String userGameName;
    LocalDateTime createdDate;
    LocalDateTime lastloginDate;

    public void setUserInfoDto (User user) {
        Id = user.getId();
        socialId = user.getSocialId();
        userGameName = user.getUserGameName();
        createdDate = user.getCreatedDate();
        lastloginDate = user.getLastloginDate();
    }
}
