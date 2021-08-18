package com.onlyonegames.eternalfantasia.domain.repository.Mail;

import com.onlyonegames.eternalfantasia.domain.model.entity.Mail.MyMailBox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyMailBoxRepository extends JpaRepository<MyMailBox, Long> {
    Optional<MyMailBox> findByUseridUser(Long userId);
}
