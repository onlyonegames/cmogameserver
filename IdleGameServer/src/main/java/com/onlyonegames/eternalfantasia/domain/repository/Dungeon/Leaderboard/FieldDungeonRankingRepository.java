package com.onlyonegames.eternalfantasia.domain.repository.Dungeon.Leaderboard;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.FieldDungeonRanking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FieldDungeonRankingRepository extends JpaRepository<FieldDungeonRanking, Long> {
    Optional<FieldDungeonRanking> findByUseridUser (Long userId);
}
