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
    @TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String userGameName;
    double totalDamage;
    double bestDamage;
    int ranking;
    boolean isBlack;

    public void refresh(double totalDamage) {
        this.totalDamage = totalDamage;
    }

    public void ResetBestDamage(double bestDamage) {
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

    public void SetBlack() {
        this.isBlack = true;
    }
}
