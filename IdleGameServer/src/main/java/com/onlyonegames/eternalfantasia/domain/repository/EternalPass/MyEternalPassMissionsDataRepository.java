package com.onlyonegames.eternalfantasia.domain.repository.EternalPass;

import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPass;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyEternalPassMissionsDataRepository extends JpaRepository<MyEternalPassMissionsData, Long> {
    Optional<MyEternalPassMissionsData> findByUseridUser(Long useridUser);
}
