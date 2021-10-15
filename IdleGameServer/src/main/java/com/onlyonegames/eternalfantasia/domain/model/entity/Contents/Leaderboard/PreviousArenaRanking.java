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
public class PreviousArenaRanking extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence", allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String userGameName;
    int point;
    int ranking;
    boolean receivable;

    public void ReceiveReward() {
        this.receivable = false;
    }

    public void ResetUserGameName(String userGameName) {
        this.userGameName = userGameName;
    }
}
