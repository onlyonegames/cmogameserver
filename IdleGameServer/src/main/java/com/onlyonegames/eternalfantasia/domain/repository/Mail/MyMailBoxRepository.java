package com.onlyonegames.eternalfantasia.domain.repository.Mail;

import com.onlyonegames.eternalfantasia.domain.model.entity.Mail.MyMailBox;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyChapterSaveData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MyMailBoxRepository extends JpaRepository<MyMailBox, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    Optional<MyMailBox> findByUseridUser(Long userId);
}
