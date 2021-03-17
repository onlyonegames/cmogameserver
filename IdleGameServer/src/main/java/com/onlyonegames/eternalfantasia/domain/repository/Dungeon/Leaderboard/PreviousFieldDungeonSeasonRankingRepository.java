package com.onlyonegames.eternalfantasia.domain.repository.Dungeon.Leaderboard;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.PreviousFieldDungeonSeasonRanking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PreviousFieldDungeonSeasonRankingRepository extends JpaRepository<PreviousFieldDungeonSeasonRanking, Long> {
    List<PreviousFieldDungeonSeasonRanking> findByUseridUser(Long userId);
}
