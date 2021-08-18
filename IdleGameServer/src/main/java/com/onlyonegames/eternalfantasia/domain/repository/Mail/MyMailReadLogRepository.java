package com.onlyonegames.eternalfantasia.domain.repository.Mail;

import com.onlyonegames.eternalfantasia.domain.model.entity.Mail.MyMailReadLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyMailReadLogRepository extends JpaRepository<MyMailReadLog, Long> {
    List<MyMailReadLog> findAllByUseridUser(Long userIdUser);
}
