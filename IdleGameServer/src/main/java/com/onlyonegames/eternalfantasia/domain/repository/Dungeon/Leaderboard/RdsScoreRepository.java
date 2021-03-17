package com.onlyonegames.eternalfantasia.domain.repository.Dungeon.Leaderboard;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.RdsScore;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyChapterSaveData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface RdsScoreRepository extends JpaRepository<RdsScore, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    Optional<RdsScore> findByUseridUser(Long userId);
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    List<RdsScore> findAllByScoreAfterAndScoreBefore(Long after, Long before);
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    List<RdsScore> findTop100ByRankingtiertableId(int rankingtiertableId);
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    List<RdsScore> findBottom100ByRankingtiertableId(int rankingtiertableId);
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    @Query("SELECT p FROM RdsScore AS p ORDER BY p.score DESC")
    List<RdsScore> findAllRankQueryNative();
}
