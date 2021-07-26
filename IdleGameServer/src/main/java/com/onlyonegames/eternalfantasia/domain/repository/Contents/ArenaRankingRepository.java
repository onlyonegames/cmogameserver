package com.onlyonegames.eternalfantasia.domain.repository.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ArenaRanking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArenaRankingRepository extends JpaRepository<ArenaRanking, Long> {
    Optional<ArenaRanking> findByUseridUser (Long userId);
    List<ArenaRanking> findAllByRankingGreaterThanAndRankingLessThan(int low, int high);
    List<ArenaRanking> findAllByUseridUserIn (List<Long> userId);
}
