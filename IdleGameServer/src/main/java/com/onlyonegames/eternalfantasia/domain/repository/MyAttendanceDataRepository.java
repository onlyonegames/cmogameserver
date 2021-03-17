package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyAttendanceData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyAttendanceDataRepository extends JpaRepository<MyAttendanceData, Long> {
    Optional<MyAttendanceData> findByUseridUser(Long userId);
}
