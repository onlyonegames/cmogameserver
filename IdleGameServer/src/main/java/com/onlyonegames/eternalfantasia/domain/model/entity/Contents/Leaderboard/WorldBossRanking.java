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
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
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
}
