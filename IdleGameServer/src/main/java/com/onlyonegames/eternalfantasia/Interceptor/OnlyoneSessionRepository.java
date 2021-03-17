package com.onlyonegames.eternalfantasia.Interceptor;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.RedisScore;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

@Transactional
public interface OnlyoneSessionRepository extends CrudRepository<OnlyoneSession, Long> {
}
