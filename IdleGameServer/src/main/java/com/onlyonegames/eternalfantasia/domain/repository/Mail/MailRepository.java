package com.onlyonegames.eternalfantasia.domain.repository.Mail;

import com.onlyonegames.eternalfantasia.domain.model.entity.Mail.Mail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface MailRepository extends JpaRepository<Mail, Long> {
   //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
   List<Mail> findAllByExpireDateAfter(LocalDateTime baseTime);
   List<Mail> findAllByToIdOrToId(Long toIdZeroForAllUser, Long toIdForMine);
   //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
   List<Mail> findAllByExpireDateBefore(LocalDateTime baseTime);
}
