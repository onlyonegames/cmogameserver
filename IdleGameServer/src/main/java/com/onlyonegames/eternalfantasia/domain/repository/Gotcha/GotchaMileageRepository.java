package com.onlyonegames.eternalfantasia.domain.repository.Gotcha;

import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyGiftInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Gotcha.GotchaMileage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface GotchaMileageRepository extends JpaRepository<GotchaMileage, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    Optional<GotchaMileage> findByUseridUser(Long userId);
}
