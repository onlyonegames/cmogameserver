package com.onlyonegames.eternalfantasia.domain.repository.Inventory;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyRuneInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyRuneInventoryRepository extends JpaRepository<MyRuneInventory, Long> {
    List<MyRuneInventory> findAllByUseridUser(Long userId);
}
