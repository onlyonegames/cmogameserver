package com.onlyonegames.eternalfantasia.domain.repository.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.PreviousStageRanking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreviousStageRankingRepository extends JpaRepository<PreviousStageRanking, Long> {
    Optional<PreviousStageRanking> findByUseridUser (Long userId);
}
