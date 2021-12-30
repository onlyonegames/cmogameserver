package com.onlyonegames.eternalfantasia.domain.repository.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ChallengeTowerArcherRanking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChallengeTowerArcherRankingRepository extends JpaRepository<ChallengeTowerArcherRanking, Long> {
    Optional<ChallengeTowerArcherRanking> findByUseridUser(Long userId);
}