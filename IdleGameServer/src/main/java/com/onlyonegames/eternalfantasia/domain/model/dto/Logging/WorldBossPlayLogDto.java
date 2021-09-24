package com.onlyonegames.eternalfantasia.domain.model.dto.Logging;

import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.WorldBossPlayLog;
import lombok.Data;

@Data
public class WorldBossPlayLogDto {
    Long id;
    Long useridUser;
    Long damage;
    Long totalDamage;

    public void SetWorldBossPlayLogDto(Long useridUser) {
        this.useridUser = useridUser;
        this.damage = 0L;
        this.totalDamage = 0L;
    }

    public void SetDamageAndTotalDamage(Long damage, Long totalDamage) {
        this.damage = damage;
        this.totalDamage = totalDamage;
    }

    public WorldBossPlayLog ToEntity() {
        return WorldBossPlayLog.builder().useridUser(useridUser).damage(damage).totalDamage(totalDamage).build();
    }
}
