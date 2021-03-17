package com.onlyonegames.eternalfantasia.domain.repository.Inventory;

import java.util.List;
import java.util.Optional;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface BelongingInventoryRepository extends JpaRepository<BelongingInventory, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    List<BelongingInventory> findByUseridUser(Long userId);
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    List<BelongingInventory> findByUseridUser(Long userId, Pageable pageable);
}