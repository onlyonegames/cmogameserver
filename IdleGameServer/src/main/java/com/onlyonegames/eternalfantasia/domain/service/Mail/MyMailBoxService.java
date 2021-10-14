package com.onlyonegames.eternalfantasia.domain.service.Mail;

import com.mysql.cj.xdevapi.JsonString;
import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mail.MailDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mail.MyMailBoxJsonDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mail.MyMailReadLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.MailSendRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.MyMailBoxResponseDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mail.Mail;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mail.MyMailBox;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mail.MyMailReadLog;
import com.onlyonegames.eternalfantasia.domain.repository.Mail.MailRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mail.MyMailBoxRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mail.MyMailReadLogRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyMailBoxService {
    private final MailRepository mailRepository;
    private final MyMailBoxRepository myMailBoxRepository;
    private final MyMailReadLogRepository myMailReadLogRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> GetMyMailBox(Long userId, Map<String, Object> map) {
        MyMailBox myMailBox = myMailBoxRepository.findByUseridUser(userId).orElse(null);
        if(myMailBox == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMailBox not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMailBox not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String json_myMailBoxInfo = myMailBox.getJson_myMailBoxInfo();
        MyMailBoxJsonDto myMailBoxJsonDto = JsonStringHerlper.ReadValueFromJson(json_myMailBoxInfo, MyMailBoxJsonDto.class);

        List<Mail> mailList = mailRepository.findAllByToIdOrToId(0L, userId);

        LocalDateTime now = LocalDateTime.now();
        List<Mail> deleteExpireMailList = new ArrayList<>();
        List<Mail> waitMailList = new ArrayList<>();
        for(Mail mail : mailList) {
            if (now.isAfter(mail.getExpireDate()))
                deleteExpireMailList.add(mail);
            if (now.isBefore(mail.getSendDate()))
                waitMailList.add(mail);
        }

        mailRepository.deleteAll(deleteExpireMailList);
        mailList.removeAll(deleteExpireMailList);

        List<MyMailBoxJsonDto.MailBoxInfo> deleteExpireMyMailBoxList = new ArrayList<>();
//        for (MyMailBoxJsonDto.MailBoxInfo mailBoxInfo : myMailBoxJsonDto.mailBoxInfoList) {
//            Mail mailInfo = mailList.stream().filter(i -> i.getId().equals(mailBoxInfo.mailId)).findAny().orElse(null); //MailReadLog 위치 변경
//            if(mailInfo == null) {
//                deleteExpireMyMailBoxList.add(mailBoxInfo);
//                MyMailReadLogDto myMailReadLogDto = new MyMailReadLogDto();
//                myMailReadLogDto.setUseridUser(userId);
//                myMailReadLogDto.setReadMailId(mailBoxInfo.mailId);
////                myMailReadLogDto.setMailTitle(mailInfo.getTitle());
//                myMailReadLogRepository.save(myMailReadLogDto.ToEntity());
//                continue;
//            }
//            if (mailBoxInfo.hasRead) {
//                deleteExpireMyMailBoxList.add(mailBoxInfo);
//                MyMailReadLogDto myMailReadLogDto = new MyMailReadLogDto();
//                myMailReadLogDto.setUseridUser(userId);
//                myMailReadLogDto.setReadMailId(mailBoxInfo.mailId);
//                myMailReadLogDto.setMailTitle(mailInfo.getTitle());
//                myMailReadLogDto.setGettingItem(mailInfo.getGettingItems());
//                myMailReadLogDto.setGettingItemCount(mailInfo.getGettingItemCounts());
//                myMailReadLogRepository.save(myMailReadLogDto.ToEntity());
//            }
//        }

        for (MyMailBoxJsonDto.MailBoxInfo mailBoxInfo : myMailBoxJsonDto.mailBoxInfoList) {
            Mail mail = mailList.stream().filter(i -> i.getId().equals(mailBoxInfo.mailId)).findAny().orElse(null);
            if(mail == null)
                deleteExpireMyMailBoxList.add(mailBoxInfo);
        }

        List<MyMailReadLog> myMailReadLogList = myMailReadLogRepository.findAllByUseridUser(userId);

        myMailBoxJsonDto.mailBoxInfoList.removeAll(deleteExpireMyMailBoxList);

        for (Mail mail : mailList) {
            MyMailReadLog myMailReadLog = myMailReadLogList.stream().filter(i -> i.getReadMailId().equals(mail.getId())).findAny().orElse(null);
            Long toId = mail.getToId();
            if(myMailReadLog != null || waitMailList.contains(mail) || (toId !=0 && !toId.equals(userId))) {
                continue;
            }
            MyMailBoxJsonDto.MailBoxInfo selectMailBoxInfo = myMailBoxJsonDto.mailBoxInfoList.stream().filter(mailBoxInfo -> mailBoxInfo.mailId.equals(mail.getId())).findAny().orElse(null);
            if(selectMailBoxInfo == null) {
                selectMailBoxInfo = new MyMailBoxJsonDto.MailBoxInfo();
                selectMailBoxInfo.mailId = mail.getId();
                selectMailBoxInfo.hasRead = false;
                selectMailBoxInfo.received = false;
                myMailBoxJsonDto.mailBoxInfoList.add(selectMailBoxInfo);
            }

            if(!myMailBoxJsonDto.AddAbleMail())
                break;
        }
        json_myMailBoxInfo = JsonStringHerlper.WriteValueAsStringFromData(myMailBoxJsonDto);
        myMailBox.ResetJsonMyMailBoxInfo(json_myMailBoxInfo);

        List<MyMailBoxResponseDto> myMailBoxResponseDtoList = new ArrayList<>();
        for(MyMailBoxJsonDto.MailBoxInfo mailBoxInfo : myMailBoxJsonDto.mailBoxInfoList) {
            Mail mailInfo = mailList.stream().filter(i -> i.getId().equals(mailBoxInfo.mailId)).findAny().orElse(null);
            if(mailInfo != null) {
                if (mailBoxInfo.hasRead && mailBoxInfo.received)
                    continue;
                MyMailBoxResponseDto myMailBoxResponseDto = new MyMailBoxResponseDto();
                myMailBoxResponseDto.setMailId(mailInfo.getId());
                myMailBoxResponseDto.setTitle(mailInfo.getTitle());
                myMailBoxResponseDto.setGettingItem(mailInfo.getGettingItems());
                myMailBoxResponseDto.setGettingItemCount(mailInfo.getGettingItemCounts());
                myMailBoxResponseDto.setSendDate(mailInfo.getSendDate());
                myMailBoxResponseDto.setExpireDate(mailInfo.getExpireDate());
                myMailBoxResponseDto.setMailType(mailInfo.getMailType());
                myMailBoxResponseDto.setHasRead(mailBoxInfo.hasRead);
                myMailBoxResponseDto.setReceived(mailBoxInfo.received);
                myMailBoxResponseDtoList.add(myMailBoxResponseDto);
            }
        }
        map.put("myMailBoxResponseDtoList", myMailBoxResponseDtoList);
        return map;
    }

    public Map<String, Object> ReadMail(Long userId, Long mailId, Map<String, Object> map) {
        MyMailBox myMailBox = myMailBoxRepository.findByUseridUser(userId).orElse(null);
        if(myMailBox == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMailBox not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMailBox not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String json_myMailBoxInfo = myMailBox.getJson_myMailBoxInfo();
        MyMailBoxJsonDto myMailBoxJsonDto = JsonStringHerlper.ReadValueFromJson(json_myMailBoxInfo, MyMailBoxJsonDto.class);
        MyMailBoxJsonDto.MailBoxInfo mailBoxInfo = myMailBoxJsonDto.GetMail(mailId);
        if(mailBoxInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMail not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMail not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        if(mailBoxInfo.received) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_READ_MAIL.getIntegerValue(), "Fail! -> Cause: Already read mail.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Already read mail.", ResponseErrorCode.ALREADY_READ_MAIL);
        }

        List<Mail> mailList = mailRepository.findAllByToIdOrToId(0L, userId);

        LocalDateTime now = LocalDateTime.now();
        List<Mail> deleteExpireMailList = new ArrayList<>();
        List<Mail> waitMailList = new ArrayList<>();
        for(Mail mail : mailList) {
            if(now.isAfter(mail.getExpireDate())) {
                deleteExpireMailList.add(mail);
            }
            if(now.isBefore(mail.getSendDate())){
                waitMailList.add(mail);
            }
        }
        mailRepository.deleteAll(deleteExpireMailList);
        mailList.removeAll(deleteExpireMailList);

        List<MyMailBoxResponseDto> myMailBoxResponseDtoList = new ArrayList<>();

        Mail mailInfo = mailList.stream().filter(i -> i.getId().equals(mailId)).findAny().orElse(null);
        if(mailInfo == null) {
            List<MyMailBoxJsonDto.MailBoxInfo> deleteExpireMyMailBoxList = new ArrayList<>();
            for(MyMailBoxJsonDto.MailBoxInfo mailBoxInfos : myMailBoxJsonDto.mailBoxInfoList) {
                Mail mailInfos = mailList.stream().filter(i -> i.getId().equals(mailBoxInfos.mailId)).findAny().orElse(null);
                if(mailInfos == null)
                    deleteExpireMyMailBoxList.add(mailBoxInfos);
            }
            myMailBoxJsonDto.mailBoxInfoList.removeAll(deleteExpireMyMailBoxList);
            List<MyMailReadLog> myMailReadLogList = myMailReadLogRepository.findAllByUseridUser(userId);

            for(Mail mail : mailList) {
                MyMailReadLog myMailReadLog = myMailReadLogList.stream().filter(i -> i.getReadMailId().equals(mail.getId())).findAny().orElse(null);
                Long toId = mail.getToId();
                if(myMailReadLog != null || waitMailList.contains(mail) || (toId != 0 && !toId.equals(userId)))
                    continue;
                MyMailBoxJsonDto.MailBoxInfo selectMailBoxInfo = myMailBoxJsonDto.mailBoxInfoList.stream().filter(i -> i.mailId.equals(mail.getId())).findAny().orElse(null);
                if(selectMailBoxInfo == null) {
                    selectMailBoxInfo = new MyMailBoxJsonDto.MailBoxInfo();
                    selectMailBoxInfo.mailId = mail.getId();
                    selectMailBoxInfo.hasRead = false;
                    selectMailBoxInfo.received = false;
                    myMailBoxJsonDto.mailBoxInfoList.add(selectMailBoxInfo);
                }
                if(!myMailBoxJsonDto.AddAbleMail())
                    break;
            }
            json_myMailBoxInfo = JsonStringHerlper.WriteValueAsStringFromData(myMailBoxJsonDto);
            myMailBox.ResetJsonMyMailBoxInfo(json_myMailBoxInfo);

            for(MyMailBoxJsonDto.MailBoxInfo mailBoxInfos : myMailBoxJsonDto.mailBoxInfoList) {
                Mail mailInfos = mailList.stream().filter(i -> i.getId().equals(mailBoxInfos.mailId)).findAny().orElse(null);
                if(mailInfos != null) {
                    MyMailBoxResponseDto myMailBoxResponseDto = new MyMailBoxResponseDto();
                    myMailBoxResponseDto.setMailId(mailInfos.getId());
                    myMailBoxResponseDto.setTitle(mailInfos.getTitle());
                    myMailBoxResponseDto.setGettingItem(mailInfos.getGettingItems());
                    myMailBoxResponseDto.setGettingItemCount(mailInfos.getGettingItemCounts());
                    myMailBoxResponseDto.setSendDate(mailInfos.getSendDate());
                    myMailBoxResponseDto.setExpireDate(mailInfos.getExpireDate());
                    myMailBoxResponseDto.setMailType(mailInfos.getMailType());
                    myMailBoxResponseDto.setHasRead(mailBoxInfos.hasRead);
                    myMailBoxResponseDto.setReceived(mailBoxInfos.received);
                    myMailBoxResponseDtoList.add(myMailBoxResponseDto);
                }
            }
            map.put("exchanged_myMailBox", true);
            map.put("myMailBoxResponseDtoList", myMailBoxResponseDtoList);
            return map;
        }

        mailBoxInfo.received = true;
        mailBoxInfo.hasRead = true;

        json_myMailBoxInfo = JsonStringHerlper.WriteValueAsStringFromData(myMailBoxJsonDto);
        myMailBox.ResetJsonMyMailBoxInfo(json_myMailBoxInfo);

        MyMailBoxResponseDto myMailBoxResponseDto = new MyMailBoxResponseDto();
        myMailBoxResponseDto.setMailId(mailInfo.getId());
        myMailBoxResponseDto.setTitle(mailInfo.getTitle());
        myMailBoxResponseDto.setGettingItem(mailInfo.getGettingItems());
        myMailBoxResponseDto.setGettingItemCount(mailInfo.getGettingItemCounts());
        myMailBoxResponseDto.setSendDate(mailInfo.getSendDate());
        myMailBoxResponseDto.setExpireDate(mailInfo.getExpireDate());
        myMailBoxResponseDto.setMailType(mailInfo.getMailType());
        myMailBoxResponseDto.setHasRead(mailBoxInfo.hasRead);
        myMailBoxResponseDto.setReceived(mailBoxInfo.received);
        myMailBoxResponseDtoList.add(myMailBoxResponseDto);

        map.put("myMailBoxResponseDtoList", myMailBoxResponseDtoList);

        MyMailReadLogDto myMailReadLogDto = new MyMailReadLogDto();
        myMailReadLogDto.setUseridUser(userId);
        myMailReadLogDto.setReadMailId(mailBoxInfo.mailId);
        myMailReadLogDto.setMailTitle(mailInfo.getTitle());
        myMailReadLogDto.setGettingItem(mailInfo.getGettingItems());
        myMailReadLogDto.setGettingItemCount(mailInfo.getGettingItemCounts());
        myMailReadLogRepository.save(myMailReadLogDto.ToEntity());

        if(mailInfo.getToId().equals(userId))
            mailRepository.delete(mailInfo);
        return map;
    }

    public Map<String, Object> ReadMailAll(Long userId, int type, Map<String, Object> map) {
        MyMailBox myMailBox = myMailBoxRepository.findByUseridUser(userId).orElse(null);
        if(myMailBox == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMailBox not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMailBox not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String json_myMailBoxInfo = myMailBox.getJson_myMailBoxInfo();
        MyMailBoxJsonDto mailBoxDto = JsonStringHerlper.ReadValueFromJson(json_myMailBoxInfo, MyMailBoxJsonDto.class);

        //API 응답을 위한 메일 정보
        List<MyMailBoxResponseDto> myMailBoxResponseDtoList = new ArrayList<>();
        List<Mail> mailList = mailRepository.findAllByToIdOrToId(0L, userId);

        LocalDateTime now = LocalDateTime.now();
        List<Mail> deleteExpireMailList = new ArrayList<>();
        List<Mail> waitMailList = new ArrayList<>();
        for(Mail mail : mailList) {
            if(now.isAfter(mail.getExpireDate()))
                deleteExpireMailList.add(mail);
            if(now.isBefore(mail.getSendDate()))
                waitMailList.add(mail);
        }
        mailRepository.deleteAll(deleteExpireMailList);
        mailList.removeAll(deleteExpireMailList);
        deleteExpireMailList.clear();

        List<MyMailBoxJsonDto.MailBoxInfo> deleteExpireMyMailBoxList = new ArrayList<>();
        for (MyMailBoxJsonDto.MailBoxInfo mailBoxInfo : mailBoxDto.mailBoxInfoList) {
            if(mailBoxInfo == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMail not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: MyMail not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            if(mailBoxInfo.received)
                continue;

            Mail mailInfo = mailList.stream().filter(mail -> mail.getId().equals(mailBoxInfo.mailId)).findAny().orElse(null);
            if(mailInfo == null) {

                //Expire 된 메일 삭제
                for(MyMailBoxJsonDto.MailBoxInfo mailBoxInfos : mailBoxDto.mailBoxInfoList) {
                    Mail mailInfos = mailList.stream().filter(mail -> mail.getId().equals(mailBoxInfos.mailId)).findAny().orElse(null);
                    if(mailInfos == null) {
                        deleteExpireMyMailBoxList.add(mailBoxInfos);
                    }
                }
                continue;
            }
            if(mailInfo.getMailType() != type)
                continue;
            mailBoxInfo.received = true;
            mailBoxInfo.hasRead = true;

            MyMailBoxResponseDto myMailBoxResponseDto = new MyMailBoxResponseDto();
            myMailBoxResponseDto.setMailId(mailInfo.getId());
            myMailBoxResponseDto.setTitle(mailInfo.getTitle());
            myMailBoxResponseDto.setGettingItem(mailInfo.getGettingItems());
            myMailBoxResponseDto.setGettingItemCount(mailInfo.getGettingItemCounts());
            myMailBoxResponseDto.setSendDate(mailInfo.getSendDate());
            myMailBoxResponseDto.setExpireDate(mailInfo.getExpireDate());
            myMailBoxResponseDto.setMailType(mailInfo.getMailType());
            myMailBoxResponseDto.setHasRead(mailBoxInfo.hasRead);
            myMailBoxResponseDto.setReceived(mailBoxInfo.received);
            myMailBoxResponseDtoList.add(myMailBoxResponseDto);

            MyMailReadLogDto myMailReadLogDto = new MyMailReadLogDto();
            myMailReadLogDto.setUseridUser(userId);
            myMailReadLogDto.setReadMailId(mailBoxInfo.mailId);
            myMailReadLogDto.setMailTitle(mailInfo.getTitle());
            myMailReadLogDto.setGettingItem(mailInfo.getGettingItems());
            myMailReadLogDto.setGettingItemCount(mailInfo.getGettingItemCounts());
            myMailReadLogRepository.save(myMailReadLogDto.ToEntity());

            if(mailInfo.getToId().equals(userId))
                deleteExpireMailList.add(mailInfo);
        }
        mailRepository.deleteAll(deleteExpireMailList);

        if(deleteExpireMyMailBoxList.size() > 0)//옮긴 이유가 받을 수 있는 메일은 받을 수 있도록 하기위해
            mailBoxDto.mailBoxInfoList.removeAll(deleteExpireMyMailBoxList);

        json_myMailBoxInfo = JsonStringHerlper.WriteValueAsStringFromData(mailBoxDto);
        myMailBox.ResetJsonMyMailBoxInfo(json_myMailBoxInfo);


        map.put("myMailBoxResponseDtoList", myMailBoxResponseDtoList);
        return map;
    }

    //운영측
    public Map<String, Object> SendMail(MailSendRequestDto dto, Map<String, Object> map) {
        MailDto mailDto = new MailDto();
        mailDto.setToId(dto.getToId());
        mailDto.setTitle(dto.getTitle());
        mailDto.setGettingItems(dto.getGettingItem());
        mailDto.setGettingItemCounts(dto.getGettingItemCount());
        mailDto.setMailType(dto.getMailType());
        mailDto.setExpireDate(dto.getExpireDate());
        mailDto.setSendDate(dto.getSendDate());
        Mail mail = mailDto.ToEntity();
        mail = mailRepository.save(mail);
        mailDto.InitFromDbData(mail);
        map.put("mail", mailDto);
        return map;
    }

    //하루마다 보내는 보상 메일
    public Map<String, Object> DailySendMail12(Map<String, Object> map) {
        String gettingItem = "diamond:5000,item_011:2,item_012:2,item_013:2,item_014:2,item_015:2";
        LocalDateTime now = LocalDateTime.now();
        String[] gettingItemSplit = gettingItem.split(",");
        for (String i : gettingItemSplit){
            String[] temp = i.split(":");
            MailDto mailDto = new MailDto();
            mailDto.setToId(0L);
            mailDto.setTitle("당신의 성장을 응원합니다.");
            mailDto.setGettingItems(temp[0]);
            mailDto.setGettingItemCounts(temp[1]);
            mailDto.setMailType(0);
            mailDto.setExpireDate(now.plusHours(12));
            mailDto.setSendDate(now);
            Mail mail = mailDto.ToEntity();
            mail = mailRepository.save(mail);
            mailDto.InitFromDbData(mail);
            map.put("mail", mailDto);
        }
        return map;
    }

    public Map<String, Object> DailySendMail6(Map<String, Object> map) {
        String gettingItem = "diamond:5000,dungeonTicket:20";
        LocalDateTime now = LocalDateTime.now();
        String[] gettingItemSplit = gettingItem.split(",");
        for (String i : gettingItemSplit){
            String[] temp = i.split(":");
            MailDto mailDto = new MailDto();
            mailDto.setToId(0L);
            mailDto.setTitle("당신의 성장을 응원합니다.");
            mailDto.setGettingItems(temp[0]);
            mailDto.setGettingItemCounts(temp[1]);
            mailDto.setMailType(0);
            mailDto.setExpireDate(now.plusHours(12));
            mailDto.setSendDate(now);
            Mail mail = mailDto.ToEntity();
            mail = mailRepository.save(mail);
            mailDto.InitFromDbData(mail);
            map.put("mail", mailDto);
        }
        return map;
    }
}
