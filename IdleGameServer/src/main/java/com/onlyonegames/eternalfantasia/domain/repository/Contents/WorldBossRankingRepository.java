package com.onlyonegames.eternalfantasia.domain.repository.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.WorldBossRanking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorldBossRankingRepository extends JpaRepository<WorldBossRanking, Long> {
    Optional<WorldBossRanking> findByUseridUser(Long userId);
}
