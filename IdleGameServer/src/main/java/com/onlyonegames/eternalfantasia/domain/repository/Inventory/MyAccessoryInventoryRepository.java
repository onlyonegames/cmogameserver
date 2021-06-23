package com.onlyonegames.eternalfantasia.domain.repository.Inventory;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyAccessoryInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyAccessoryInventoryRepository extends JpaRepository<MyAccessoryInventory, Long> {
    List<MyAccessoryInventory> findAllByUseridUser(Long userId);
}
