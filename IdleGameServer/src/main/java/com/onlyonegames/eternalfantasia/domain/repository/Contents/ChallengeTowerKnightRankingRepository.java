package com.onlyonegames.eternalfantasia.domain.repository.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ChallengeTowerKnightRanking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChallengeTowerKnightRankingRepository extends JpaRepository<ChallengeTowerKnightRanking, Long> {
    Optional<ChallengeTowerKnightRanking> findByUseridUser(Long userId);
}
