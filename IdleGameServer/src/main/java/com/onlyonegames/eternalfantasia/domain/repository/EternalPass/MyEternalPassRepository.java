package com.onlyonegames.eternalfantasia.domain.repository.EternalPass;

import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPass;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MyEternalPassRepository extends JpaRepository<MyEternalPass, Long> {
    Optional<MyEternalPass> findByUseridUser(Long useridUser);
}
