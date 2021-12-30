package com.onlyonegames.eternalfantasia.domain.repository.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ChallengeTowerWarriorRanking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChallengeTowerWarriorRankingRepository extends JpaRepository<ChallengeTowerWarriorRanking, Long> {
    Optional<ChallengeTowerWarriorRanking> findByUseridUser(Long userId);
}
