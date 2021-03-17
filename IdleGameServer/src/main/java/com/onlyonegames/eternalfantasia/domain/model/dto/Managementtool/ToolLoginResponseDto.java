package com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool;

import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import lombok.Data;

@Data
public class ToolLoginResponseDto {
    Long userId;
    Long role;
    String userGameName;

    public void SetToolLoginResponseDto(User user){
        this.userId = user.getId();
        this.role = user.getRoles().stream().findAny().get().getId();
        this.userGameName = user.getUserGameName();
    }
}
