package com.onlyonegames.eternalfantasia.domain.repository.Companion;

import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyGiftInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MyGiftInventoryRepository extends JpaRepository<MyGiftInventory, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    Optional<MyGiftInventory> findByUseridUser(Long userId);
}
