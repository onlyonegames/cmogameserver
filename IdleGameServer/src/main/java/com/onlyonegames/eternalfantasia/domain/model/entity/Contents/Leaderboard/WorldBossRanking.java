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
public class WorldBossRanking extends BaseTimeEntity {
    @Id
    //@TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long useridUser;
    String userGameName;
    Long totalDamage;
    Long bestDamage;
    int ranking;

    public void refresh(Long totalDamage) {
        this.totalDamage = totalDamage;
    }

    public void ResetBestDamage(Long bestDamage) {
        if(this.bestDamage < bestDamage)
            this.bestDamage = bestDamage;
    }

    public void ResetRanking(Long ranking) {
        this.ranking = ranking.intValue();
    }


    public void ResetZero(){
        this.totalDamage = 0L;
        this.bestDamage = 0L;
        this.ranking = 0;
    }

    public void ResetUserGameName(String userGameName) {
        this.userGameName = userGameName;
    }
}
