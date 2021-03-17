package com.onlyonegames.eternalfantasia.domain.repository.Inventory;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.ItemType;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemTypeRepository extends JpaRepository<ItemType, Long> {

}