package com.onlyonegames.eternalfantasia.domain.repository;

import java.util.Optional;

import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
public interface UserRepository extends JpaRepository<User, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    Optional<User> findByuserGameName(String userGameName);
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    Optional<User> findBysocialId(String socialId);
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    boolean existsBySocialId(String socialId);
}
