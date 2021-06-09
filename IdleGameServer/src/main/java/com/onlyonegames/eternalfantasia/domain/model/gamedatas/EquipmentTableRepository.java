package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EquipmentTableRepository extends JpaRepository<EquipmentTable, Integer> {
    Optional<EquipmentTable> findByCode(String code);
}
