package com.onlyonegames.eternalfantasia.domain.repository.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ArenaRedisRanking;
import org.springframework.data.repository.CrudRepository;

public interface ArenaRedisRankingRepository extends CrudRepository<ArenaRedisRanking, Long> {
}
