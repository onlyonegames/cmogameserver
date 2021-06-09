package com.onlyonegames.eternalfantasia.domain.repository.Inventory;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyClassInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyClassInventoryRepository extends JpaRepository<MyClassInventory, Long> {
    List<MyClassInventory> findAllByUseridUser(Long userId);
}
