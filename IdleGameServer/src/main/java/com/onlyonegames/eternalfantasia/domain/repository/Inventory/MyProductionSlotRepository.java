package com.onlyonegames.eternalfantasia.domain.repository.Inventory;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyProductionSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MyProductionSlotRepository extends JpaRepository<MyProductionSlot, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    List<MyProductionSlot>  findByUserIdUser(Long userId);
}
