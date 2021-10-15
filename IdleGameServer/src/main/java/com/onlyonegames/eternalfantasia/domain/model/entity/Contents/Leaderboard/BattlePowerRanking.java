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
public class BattlePowerRanking extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence", allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String userGameName;
    Long battlePower;
    int ranking;

    public void refresh(Long battlePower) {
        this.battlePower = battlePower;
    }
    public void SetRanking(int ranking) {
        this.ranking = ranking;
    }

    public void ResetUserGameName(String userGameName) {
        this.userGameName = userGameName;
    }
}
