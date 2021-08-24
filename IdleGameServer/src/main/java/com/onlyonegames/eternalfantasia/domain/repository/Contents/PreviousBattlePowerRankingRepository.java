package com.onlyonegames.eternalfantasia.domain.repository.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.PreviousBattlePowerRanking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreviousBattlePowerRankingRepository extends JpaRepository<PreviousBattlePowerRanking, Long> {
    Optional<PreviousBattlePowerRanking> findByUseridUser(Long userId);
}
