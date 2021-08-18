package com.onlyonegames.eternalfantasia.domain.repository.Mail;

import com.onlyonegames.eternalfantasia.domain.model.entity.Mail.Mail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MailRepository extends JpaRepository<Mail, Long> {
    List<Mail> findAllByExpireDateAfter(LocalDateTime baseTime);
    List<Mail> findAllByToIdOrToId(Long toIdZeroForAllUser, Long toIdForMine);
    List<Mail> findAllByExpireDateBefore(LocalDateTime baseTime);
}
