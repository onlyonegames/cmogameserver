package com.onlyonegames.eternalfantasia.domain.model.entity;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class MyContentsInfo extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    int challengeTowerFloor;

    public void SetChallengeTowerFloor(int challengeTowerFloor) {
        this.challengeTowerFloor = challengeTowerFloor;
    }

    public void SetChallengeTowerFloor(String challengeTowerFloor) {
        this.challengeTowerFloor = Integer.parseInt(challengeTowerFloor);
    }
}
