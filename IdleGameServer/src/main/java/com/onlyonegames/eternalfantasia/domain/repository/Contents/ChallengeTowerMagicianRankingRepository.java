package com.onlyonegames.eternalfantasia.domain.repository.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ChallengeTowerMagicianRanking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChallengeTowerMagicianRankingRepository extends JpaRepository<ChallengeTowerMagicianRanking, Long> {
    Optional<ChallengeTowerMagicianRanking> findByUseridUser(Long userId);
}
