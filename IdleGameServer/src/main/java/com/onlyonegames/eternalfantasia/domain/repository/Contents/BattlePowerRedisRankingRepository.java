package com.onlyonegames.eternalfantasia.domain.repository.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.BattlePowerRedisRanking;
import org.springframework.data.repository.CrudRepository;

public interface BattlePowerRedisRankingRepository extends CrudRepository<BattlePowerRedisRanking, Long> {
}
