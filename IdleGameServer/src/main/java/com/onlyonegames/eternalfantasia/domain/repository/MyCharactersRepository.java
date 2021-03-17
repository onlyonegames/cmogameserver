package com.onlyonegames.eternalfantasia.domain.repository;

import java.util.List;
import java.util.Optional;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface MyCharactersRepository extends JpaRepository<MyCharacters, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    List<MyCharacters> findAllByuseridUser(Long useridUser);
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    Optional<MyCharacters> findByUseridUserAndCodeHerostable(Long useridUser, String codeHerosTable);
}