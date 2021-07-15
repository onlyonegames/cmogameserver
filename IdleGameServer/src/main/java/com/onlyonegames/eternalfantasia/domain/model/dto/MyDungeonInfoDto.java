package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyDungeonInfo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyDungeonInfoDto {
    Long id;
    Long useridUser;
    LocalDateTime weaponDungeonTime;
    int weaponDungeonFloor;
    LocalDateTime accessoryDungeonTime;
    int accessoryDungeonFloor;
    LocalDateTime runeDungeonTime;
    int runeDungeonFloor;

    public MyDungeonInfo ToEntity() {
        LocalDateTime now = LocalDateTime.now().minusDays(1);
        return MyDungeonInfo.builder().useridUser(useridUser).weaponDungeonTime(now).weaponDungeonFloor(0)
                .accessoryDungeonTime(now).accessoryDungeonFloor(0)
                .runeDungeonTime(now).runeDungeonFloor(0).build();
    }
}
