package com.onlyonegames.eternalfantasia.domain.model.dto;

import java.util.HashSet;
import java.util.Set;

import com.onlyonegames.eternalfantasia.domain.model.entity.Role;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;

import lombok.Data;

@Data
public class UserBaseDto {
    Long id;
    String socialId;
    String password;
    String socialProvider;
    String userGameName;
    String version;
    Set<Role> roles = new HashSet<>();

    public User ToEntity() {
        return User.builder().socialId(socialId).password(password).socialProvider(socialProvider)
                .userGameName(userGameName).roles(roles).build();
    }
}