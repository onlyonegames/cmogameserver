package com.onlyonegames.eternalfantasia.domain.repository.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.StageRanking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StageRankingRepository extends JpaRepository<StageRanking, Long> {
    Optional<StageRanking> findByUseridUser(Long userId);
}
