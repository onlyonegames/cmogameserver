package com.onlyonegames.eternalfantasia.domain.repository.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.PreviousWorldBossRanking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreviousWorldBossRankingRepository extends JpaRepository<PreviousWorldBossRanking, Long> {
    Optional<PreviousWorldBossRanking> findByUseridUser (Long userId);
}
