package com.onlyonegames.eternalfantasia.domain.repository.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.BattlePowerRanking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BattlePowerRankingRepository extends JpaRepository<BattlePowerRanking, Long> {
    Optional<BattlePowerRanking> findByUseridUser(Long userId);
}
