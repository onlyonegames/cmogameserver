package com.onlyonegames.eternalfantasia.domain.repository;

import java.util.Optional;

import com.onlyonegames.eternalfantasia.domain.model.RoleName;
import com.onlyonegames.eternalfantasia.domain.model.entity.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    Optional<Role> findByName(RoleName name);
}