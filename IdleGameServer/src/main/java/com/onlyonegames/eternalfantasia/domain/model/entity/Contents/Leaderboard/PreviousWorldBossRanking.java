package com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class PreviousWorldBossRanking extends BaseTimeEntity {
    @Id
    //@TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long useridUser;
    String userGameName;
    Long totalDamage;
    Long bestDamage;
    int ranking;
    boolean receivable;

    public void ReceiveReward() {
        this.receivable = false;
    }

    public void ResetUserGameName(String userGameName) {
        this.userGameName = userGameName;
    }
}
