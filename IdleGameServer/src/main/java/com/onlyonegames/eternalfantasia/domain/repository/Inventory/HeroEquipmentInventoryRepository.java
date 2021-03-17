package com.onlyonegames.eternalfantasia.domain.repository.Inventory;

import java.util.List;
import java.util.Optional;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface HeroEquipmentInventoryRepository extends JpaRepository<HeroEquipmentInventory, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    List<HeroEquipmentInventory> findByUseridUser(Long userId);
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    Optional<HeroEquipmentInventory> findByIdAndUseridUser(Long Id, Long userId);
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    List<HeroEquipmentInventory> findByUseridUser(Long userId, Pageable pageable);
}