package com.onlyonegames.eternalfantasia.domain.repository.Inventory;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyRelicInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyRelicInventoryRepository extends JpaRepository<MyRelicInventory, Long> {
    List<MyRelicInventory> findAllByUseridUser(Long userId);
}
