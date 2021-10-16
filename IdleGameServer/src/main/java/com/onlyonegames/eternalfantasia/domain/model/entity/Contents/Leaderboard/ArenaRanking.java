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
public class ArenaRanking extends BaseTimeEntity {
    @Id
    //@TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long useridUser;
    String userGameName;
    int point;
    int ranking;

    public void refresh(int point) {
        this.point = point;
    }

    public void SetRanking(int ranking) {
        this.ranking = ranking;
    }

    public void ResetUserGameName(String userGameName) {
        this.userGameName = userGameName;
    }

    public void SetFirstUser() {
        this.point = 0;
        this.ranking = 0;
    }
}
