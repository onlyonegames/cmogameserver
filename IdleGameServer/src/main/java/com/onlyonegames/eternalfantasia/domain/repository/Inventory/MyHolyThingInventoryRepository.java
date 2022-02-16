package com.onlyonegames.eternalfantasia.domain.repository.Inventory;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyHolyThingInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyHolyThingInventoryRepository extends JpaRepository<MyHolyThingInventory, Long> {
    List<MyHolyThingInventory> findAllByUseridUser(Long userId);
}
