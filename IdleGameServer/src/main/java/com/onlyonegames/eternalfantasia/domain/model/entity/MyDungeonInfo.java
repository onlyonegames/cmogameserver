package com.onlyonegames.eternalfantasia.domain.model.entity;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class MyDungeonInfo extends BaseTimeEntity {
    @Id
    //@TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long useridUser;
    public LocalDateTime weaponDungeonTime;
    public int weaponDungeonFloor;
    public LocalDateTime accessoryDungeonTime;
    public int accessoryDungeonFloor;
    public LocalDateTime runeDungeonTime;
    public int runeDungeonFloor;


}
