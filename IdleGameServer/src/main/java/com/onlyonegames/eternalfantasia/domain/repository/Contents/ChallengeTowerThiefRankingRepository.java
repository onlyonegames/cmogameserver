package com.onlyonegames.eternalfantasia.domain.repository.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ChallengeTowerThiefRanking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChallengeTowerThiefRankingRepository extends JpaRepository<ChallengeTowerThiefRanking, Long> {
    Optional<ChallengeTowerThiefRanking> findByUseridUser(Long userId);
}