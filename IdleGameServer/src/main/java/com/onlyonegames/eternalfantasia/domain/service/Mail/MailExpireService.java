package com.onlyonegames.eternalfantasia.domain.service.Mail;

import com.onlyonegames.eternalfantasia.domain.model.entity.Mail.Mail;
import com.onlyonegames.eternalfantasia.domain.repository.Mail.MailRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@AllArgsConstructor
public class MailExpireService {
    private final MailRepository mailRepository;

    public Map<String, Object> ExpireMail(Map<String, Object> map) {
        List<Mail> mailList = mailRepository.findAllByExpireDateBefore(LocalDateTime.now());
        mailRepository.deleteAll(mailList);
        return map;
    }
}