package com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class MyInfiniteTowerSaveData extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    int arrivedTopFloor;
    /**
     * 무한의 탑 보상 받은 정보 json
     * */
    String receivedRewardInfoJson;

    public void ResetReceivedRewardInfoJson(String receivedRewardInfoJson) {
        this.receivedRewardInfoJson = receivedRewardInfoJson;
    }

    public void SetArrivedTopFloor(int arrivedTopFloor) {
        this.arrivedTopFloor = arrivedTopFloor;
    }
}
