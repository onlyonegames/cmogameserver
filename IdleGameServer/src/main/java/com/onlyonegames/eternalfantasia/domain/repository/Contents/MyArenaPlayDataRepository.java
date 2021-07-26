package com.onlyonegames.eternalfantasia.domain.repository.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.MyArenaPlayData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MyArenaPlayDataRepository extends JpaRepository<MyArenaPlayData, Long> {
    Optional<MyArenaPlayData> findByUseridUser(Long userId);
    List<MyArenaPlayData> findAllByResetAbleMatchingUserOrReMatchingAbleCountNot(boolean resetAbleMatchingUser, int reMatchingAbleCount);
}
