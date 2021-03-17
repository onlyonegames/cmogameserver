package com.onlyonegames.eternalfantasia.domain.repository.Dungeon.Leaderboard;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.PreviousArenaSeasonRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PreviousArenaSeasonRankingRepository extends JpaRepository<PreviousArenaSeasonRanking, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    Optional<PreviousArenaSeasonRanking> findByUseridUser(Long userId);
}
