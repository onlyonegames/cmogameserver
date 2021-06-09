package com.onlyonegames.eternalfantasia.domain.repository.Inventory;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyEquipmentInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyEquipmentInventoryRepository extends JpaRepository<MyEquipmentInventory, Long> {
    List<MyEquipmentInventory> findALLByUseridUser(Long userId);
}
