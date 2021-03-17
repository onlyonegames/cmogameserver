package com.onlyonegames.eternalfantasia.domain.repository.Dungeon.Leaderboard;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.RedisScore;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RedisScoreRepository extends CrudRepository<RedisScore, Long> {

    List<RedisScore> findAllByRankingtiertable_id(int rankingtiertable_id);
}
