package com.onlyonegames.eternalfantasia.domain.repository.Companion;

import com.onlyonegames.eternalfantasia.domain.model.dto.CostumeDto.MyCostumeInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyCostumeInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyGiftInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface MyCostumeInventoryRepository extends JpaRepository<MyCostumeInventory, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    Optional<MyCostumeInventory> findByUseridUser(Long userId);
}
