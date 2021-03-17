package com.onlyonegames.eternalfantasia.domain.repository.Dungeon.Leaderboard;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.InfiniteTowerRedisScore;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.RedisScore;
import org.springframework.data.repository.CrudRepository;

public interface InfiniteTowerRedisScoreRepository extends CrudRepository<InfiniteTowerRedisScore, Long> {
}
