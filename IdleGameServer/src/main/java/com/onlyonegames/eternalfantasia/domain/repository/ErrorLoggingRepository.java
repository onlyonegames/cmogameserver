package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.ErrorLogging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ErrorLoggingRepository extends JpaRepository<ErrorLogging, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    List<ErrorLogging> findAllByUseridUser(Long userid);
}