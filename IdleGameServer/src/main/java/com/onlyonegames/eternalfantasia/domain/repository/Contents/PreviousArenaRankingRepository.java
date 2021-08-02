package com.onlyonegames.eternalfantasia.domain.repository.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.PreviousArenaRanking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreviousArenaRankingRepository extends JpaRepository<PreviousArenaRanking, Long> {
    Optional<PreviousArenaRanking> findByUseridUser(Long userId);
}
