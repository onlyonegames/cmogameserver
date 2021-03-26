package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyForTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyForTestRepository extends JpaRepository<MyForTest, Long> {
    Optional<MyForTest> findByUseridUser(Long userId);
}