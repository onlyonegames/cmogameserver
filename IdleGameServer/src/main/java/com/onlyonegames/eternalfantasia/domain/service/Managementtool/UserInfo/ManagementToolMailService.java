package com.onlyonegames.eternalfantasia.domain.service.Managementtool.UserInfo;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.MailDto.MailBoxDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.ManagementToolMailDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mail.Mail;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mail.MyMailBox;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.Mail.MailRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mail.MyMailBoxRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@AllArgsConstructor
public class ManagementToolMailService {
    private final MyMailBoxRepository myMailBoxRepository;
    private final MailRepository mailRepository;
    private final UserRepository userRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> DeleteMail(Long mailId, Map<String, Object> map) {
        mailRepository.deleteById(mailId);
        return map;
    }

    public Map<String, Object> GetAllMail(Map<String, Object> map){
        List<Mail> mail = mailRepository.findAllByExpireDateAfter(LocalDateTime.now());
        map.put("Mail", mail);
        return map;
    }

    public Map<String, Object> GetMailsByUserId(Long userId, Map<String, Object> map){
        MyMailBox myMailBox = myMailBoxRepository.findByUseridUser(userId)
                .orElse(null);
        if(myMailBox == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyMailBox not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMailBox not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        MailBoxDto mailBoxDto = JsonStringHerlper.ReadValueFromJson(myMailBox.getJson_myMailBoxInfo(), MailBoxDto.class);
        List<Long> mailId = new ArrayList<>();
        for(MailBoxDto.MailBoxInfo mailBoxInfo:mailBoxDto.mailBoxInfoList){
            mailId.add(mailBoxInfo.mailId);
        }
        List<Mail> mailList = mailRepository.findAllById(mailId);
        List<ManagementToolMailDto.ManagementToolMails> managementToolMailDtoList = new ArrayList<>();
        for(Long id:mailId){
            ManagementToolMailDto.ManagementToolMails managementToolMails = new ManagementToolMailDto.ManagementToolMails();
            Mail mail = mailList.stream().filter(i -> i.getId().equals(id))
                    .findAny()
                    .orElse(null);
            if(mail == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: Mail not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Mail not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            MailBoxDto.MailBoxInfo mailBoxInfo = mailBoxDto.mailBoxInfoList.stream().filter(i -> i.mailId.equals(id))
                    .findAny()
                    .orElse(null);
            if(mailBoxInfo == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyMailBox not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: MyMailBox not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            String title = mail.getTitle();
            boolean hasRead = mailBoxInfo.hasRead;
            boolean received = mailBoxInfo.received;
            LocalDateTime sendData = mail.getSendDate();
            managementToolMails.setManagementToolMails(title,hasRead,received,sendData,id);
            managementToolMailDtoList.add(managementToolMails);
        }
        map.put("ManagementToolMailDtoList", managementToolMailDtoList);
        return map;
    }

    public Map<String, Object> GetMailByMailId(Long mailId, Map<String, Object> map){
        Mail mail = mailRepository.findById(mailId)
                .orElse(null);
        if (mail == null) {
            errorLoggingService.SetErrorLog(mailId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: Mail not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Mail not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        ManagementToolMailDto.ManagementToolMail managementToolMail = new ManagementToolMailDto.ManagementToolMail();
        String[] gettingItemsArray = mail.getGettingItems().split(",");
        String[] itemsCountArray = mail.getGettingItemCounts().split(",");
        List<String> reward = new ArrayList<>();
        int gettingItemsCount = gettingItemsArray.length;
        for(int i = 0; i < gettingItemsCount; i++){
            reward.add(gettingItemsArray[i]+" X "+itemsCountArray[i]);
        }

        User user = userRepository.findById(mail.getFromId())
                .orElseThrow(() -> new MyCustomException("Fail! -> Cause: user not find", ResponseErrorCode.NOT_FIND_DATA));
        if(user == null) {
            errorLoggingService.SetErrorLog(mailId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: Mail not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Mail not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String userName = user.getUserGameName();
        managementToolMail.setManagementToolMail(userName,mail.getSendDate(), reward, mail.getTitle(), mail.getContent());
        map.put("Mail", managementToolMail);
        return map;
    }
}
