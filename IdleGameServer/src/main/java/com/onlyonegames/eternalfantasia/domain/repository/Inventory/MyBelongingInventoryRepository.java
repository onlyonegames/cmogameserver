package com.onlyonegames.eternalfantasia.domain.repository.Inventory;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyBelongingInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MyBelongingInventoryRepository extends JpaRepository<MyBelongingInventory, Long> {
    List<MyBelongingInventory> findAllByUseridUser(Long userId);
    Optional<MyBelongingInventory> findByUseridUserAndCode(Long userId, String code);
}
