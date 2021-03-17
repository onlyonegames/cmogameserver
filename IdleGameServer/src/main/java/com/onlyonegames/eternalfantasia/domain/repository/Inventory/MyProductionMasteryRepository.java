package com.onlyonegames.eternalfantasia.domain.repository.Inventory;

import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyProductionMastery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface MyProductionMasteryRepository extends JpaRepository<MyProductionMastery, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    MyProductionMastery findByUserIdUser(Long userId);
}
