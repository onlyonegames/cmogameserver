package com.onlyonegames.eternalfantasia.domain.repository.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.StageRedisRanking;
import org.springframework.data.repository.CrudRepository;

public interface StageRedisRankingRepository extends CrudRepository<StageRedisRanking, Long> {
}
