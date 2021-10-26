package com.onlyonegames.eternalfantasia.domain.model.entity.Logging;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WorldBossPlayLog extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    double damage;
    double totalDamage;

    public void SetDamageAndTotalDamage(double damage, double totalDamage) {
        this.damage = damage;
        this.totalDamage = totalDamage;
    }
}
