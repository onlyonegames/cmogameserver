package com.onlyonegames.eternalfantasia.domain.repository.Inventory;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyCostumeInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyCostumeInventoryRepository extends JpaRepository<MyCostumeInventory, Long> {
    List<MyCostumeInventory> findAllByUseridUser(Long userId);
}
