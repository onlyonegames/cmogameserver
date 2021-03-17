package com.onlyonegames.eternalfantasia.domain.service.Mail;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;
import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.CostumeDto.CostumeDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.EventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.GiftItemDto.GiftItemDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.HeroEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.BelongingInventoryLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CurrencyLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.EquipmentLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.GiftLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MailDto.MailBoxDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MailDto.MailDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MailDto.MailGettingItemsResponseDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyCharactersBaseDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.MailSendRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.GotchaCharacterResponseDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.MyMailBoxResponseDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyCostumeInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyGiftInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.ItemType;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mail.Mail;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mail.MyMailBox;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyCostumeInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyGiftInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.BelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.HeroEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.ItemTypeRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mail.MailRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mail.MyMailBoxRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import com.onlyonegames.eternalfantasia.etc.*;
import com.onlyonegames.util.MathHelper;
import com.onlyonegames.util.StringMaker;
import lombok.AllArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyMailBoxService {
    private final MyMailBoxRepository myMailBoxRepository;
    private final MailRepository mailRepository;
    private final MyGiftInventoryRepository myGiftInventoryRepository;
    private final MyCharactersRepository myCharactersRepository;
    private final HeroEquipmentInventoryRepository heroEquipmentInventoryRepository;
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final UserRepository userRepository;
    private final ItemTypeRepository itemTypeRepository;
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;
    private final MyCostumeInventoryRepository myCostumeInventoryRepository;
    public Map<String, Object> GetMyMailBox(Long userId, Map<String, Object> map){

        MyMailBox myMailBox = myMailBoxRepository.findByUseridUser(userId)
                .orElse(null);
        if(myMailBox == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMailBox not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMailBox not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String json_myMailBoxInfo = myMailBox.getJson_myMailBoxInfo();
        MailBoxDto mailBoxDto = JsonStringHerlper.ReadValueFromJson(json_myMailBoxInfo, MailBoxDto.class);

        //List<Mail> mailList = mailRepository.findAllByExpireDateAfter(LocalDateTime.now());
        //List<Mail> mailList = mailRepository.findAll();
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

        List<MailBoxDto.MailBoxInfo> deleteExpireMyMailBoxList = new ArrayList<>();
        //Expire 된 메일 삭제
        for(MailBoxDto.MailBoxInfo mailBoxInfo : mailBoxDto.mailBoxInfoList) {
            Mail mailInfo = deleteExpireMailList.stream().filter(mail -> mail.getId().equals(mailBoxInfo.mailId)).findAny().orElse(null);
            if(mailInfo != null) {
                deleteExpireMyMailBoxList.add(mailBoxInfo);
            }
        }
        mailBoxDto.mailBoxInfoList.removeAll(deleteExpireMyMailBoxList);

        //메일리스트에 있는 메일이 내 메일함에 없으면 ADD
        for(Mail mail : mailList) {
            if(waitMailList.contains(mail))
                continue;
            Long toId = mail.getToId();
            if(toId != 0 && !toId.equals(userId)) {
                continue;
            }
            MailBoxDto.MailBoxInfo selectMailBoxInfo = mailBoxDto.mailBoxInfoList.stream().filter(mailBoxInfo -> mailBoxInfo.mailId.equals(mail.getId())).findAny().orElse(null);
            if(selectMailBoxInfo == null) {
                selectMailBoxInfo = new MailBoxDto.MailBoxInfo();
                selectMailBoxInfo.mailId = mail.getId();
                selectMailBoxInfo.hasRead = false;
                selectMailBoxInfo.received = false;
                mailBoxDto.mailBoxInfoList.add(selectMailBoxInfo);
            }
            //메일을 더 받을수 있는 상황인지 체크
            if(!mailBoxDto.AddAbleMail())
                break;
        }
        json_myMailBoxInfo = JsonStringHerlper.WriteValueAsStringFromData(mailBoxDto);
        myMailBox.ResetJsonMyMailBoxInfo(json_myMailBoxInfo);

        //API 응답을 위한 메일 정보 구성
        List<MyMailBoxResponseDto> myMailBoxResponseDtoList = new ArrayList<>();
        for(MailBoxDto.MailBoxInfo mailBoxInfo : mailBoxDto.mailBoxInfoList) {
            Mail mailInfo = mailList.stream().filter(mail -> mail.getId().equals(mailBoxInfo.mailId)).findAny().orElse(null);
            if(mailInfo != null) {
                MyMailBoxResponseDto myMailBoxResponseDto = new MyMailBoxResponseDto();
                myMailBoxResponseDto.setMailId(mailInfo.getId());
                /*추후 제2, 제3의 관리자가 메일을 주거나 해야할때 각각의 관리자 이름을 정하도록 하고 그전까지는 From 은 운영자로 통칭*/
                myMailBoxResponseDto.setFrom("운영자");
                myMailBoxResponseDto.setTitle(mailInfo.getTitle());
                myMailBoxResponseDto.setContent(mailInfo.getContent());
                myMailBoxResponseDto.setGettingItems(mailInfo.getGettingItems());
                myMailBoxResponseDto.setGettingItemCounts(mailInfo.getGettingItemCounts());
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

        MyMailBox myMailBox = myMailBoxRepository.findByUseridUser(userId)
                .orElse(null);
        if(myMailBox == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMailBox not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMailBox not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String json_myMailBoxInfo = myMailBox.getJson_myMailBoxInfo();
        MailBoxDto mailBoxDto = JsonStringHerlper.ReadValueFromJson(json_myMailBoxInfo, MailBoxDto.class);
        MailBoxDto.MailBoxInfo mailBoxInfo = mailBoxDto.GetMail(mailId);
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
        for(Mail mail : mailList) {
            if(now.isAfter(mail.getExpireDate())) {
                deleteExpireMailList.add(mail);
            }
        }
        mailRepository.deleteAll(deleteExpireMailList);

        Mail mailInfo = mailList.stream().filter(mail -> mail.getId().equals(mailId)).findAny().orElse(null);
        if(mailInfo == null) {
            List<MailBoxDto.MailBoxInfo> deleteExpireMyMailBoxList = new ArrayList<>();
            //Expire 된 메일 삭제
            for(MailBoxDto.MailBoxInfo mailBoxInfos : mailBoxDto.mailBoxInfoList) {
                Mail mailInfos = mailList.stream().filter(mail -> mail.getId().equals(mailBoxInfos.mailId)).findAny().orElse(null);
                if(mailInfos == null) {
                    deleteExpireMyMailBoxList.add(mailBoxInfos);
                }
            }
            mailBoxDto.mailBoxInfoList.removeAll(deleteExpireMyMailBoxList);

            //메일리스트에 있는 메일이 내 메일함에 없으면 ADD
            for(Mail mail : mailList) {
                Long toId = mail.getToId();
                if(toId != 0 && !toId.equals(userId)) {
                    continue;
                }
                MailBoxDto.MailBoxInfo selectMailBoxInfo = mailBoxDto.mailBoxInfoList.stream().filter(mailBoxInfos -> mailBoxInfos.mailId.equals(mail.getId())).findAny().orElse(null);
                if(selectMailBoxInfo == null) {
                    selectMailBoxInfo = new MailBoxDto.MailBoxInfo();
                    selectMailBoxInfo.mailId = mail.getId();
                    selectMailBoxInfo.hasRead = false;
                    selectMailBoxInfo.received = false;
                    mailBoxDto.mailBoxInfoList.add(selectMailBoxInfo);
                }
                //메일을 더 받을수 있는 상황인지 체크
                if(!mailBoxDto.AddAbleMail())
                    break;
            }
            json_myMailBoxInfo = JsonStringHerlper.WriteValueAsStringFromData(mailBoxDto);
            myMailBox.ResetJsonMyMailBoxInfo(json_myMailBoxInfo);

            //API 응답을 위한 메일 정보 구성
            List<MyMailBoxResponseDto> myMailBoxResponseDtoList = new ArrayList<>();
            for(MailBoxDto.MailBoxInfo mailBoxInfos : mailBoxDto.mailBoxInfoList) {
                Mail mailInfos = mailList.stream().filter(mail -> mail.getId().equals(mailBoxInfos.mailId)).findAny().orElse(null);
                if(mailInfos != null) {
                    MyMailBoxResponseDto myMailBoxResponseDto = new MyMailBoxResponseDto();
                    myMailBoxResponseDto.setMailId(mailInfos.getId());
                    /*추후 제2, 제3의 관리자가 메일을 주거나 해야할때 각각의 관리자 이름을 정하도록 하고 그전까지는 From 은 운영자로 통칭*/
                    myMailBoxResponseDto.setFrom("운영자");
                    myMailBoxResponseDto.setTitle(mailInfos.getTitle());
                    myMailBoxResponseDto.setContent(mailInfos.getContent());
                    myMailBoxResponseDto.setGettingItems(mailInfos.getGettingItems());
                    myMailBoxResponseDto.setGettingItemCounts(mailInfos.getGettingItemCounts());
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

        MyMissionsData myMissionsData = null;
        /*업적 : 체크 준비*/
        if(myMissionsData == null)
            myMissionsData = myMissionsDataRepository.findByUseridUser(userId).orElseThrow(() -> new MyCustomException("Fail! -> Cause: MyMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA));
        String jsonMyMissionData = myMissionsData.getJson_saveDataValue();
        MissionsDataDto myMissionsDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyMissionData, MissionsDataDto.class);
        boolean changedMissionsData = false;

        /*패스 업적 : 체크 준비*/
        MyEternalPassMissionsData myEternalPassMissionsData = myEternalPassMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myEternalPassMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEternalPassMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyEternalPassMissionData = myEternalPassMissionsData.getJson_saveDataValue();
        EventMissionDataDto myEternalPassMissionDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyEternalPassMissionData, EventMissionDataDto.class);
        boolean changedEternalPassMissionsData = false;

        //메일 읽음 처리
        //타입별 처리
        if(mailInfo.getMailType() == 1) { //아이템 선물 첨부
            MailGettingItemsResponseDto mailGettingItemsResponseDto = new MailGettingItemsResponseDto();
            changedMissionsData = receiveItem(userId, mailInfo, mailGettingItemsResponseDto, myMissionsDataDto) || changedMissionsData;


            String[] gettingItemsArray = mailInfo.getGettingItems().split(",");
            String[] itemsCountArray = mailInfo.getGettingItemCounts().split(",");
            int gettingItemsCount = gettingItemsArray.length;
            for(int i = 0; i < gettingItemsCount; i++) {
                if(gettingItemsArray[i].equals("gold")) {
                    int gettingCount = Integer.parseInt(itemsCountArray[i]);
                    /* 패스 업적 : 골드획득*/
                    changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.ERNING_GOLD.name(),"empty", gettingCount, gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
                }
            }


            mailBoxInfo.received = true;
            mailBoxInfo.hasRead = true;
            map.put("mailGettingItems", mailGettingItemsResponseDto);
        }
        else if(mailInfo.getMailType() == 0) {//일반 메시지
            mailBoxInfo.hasRead = true;
        }
        json_myMailBoxInfo = JsonStringHerlper.WriteValueAsStringFromData(mailBoxDto);
        myMailBox.ResetJsonMyMailBoxInfo(json_myMailBoxInfo);

        MyMailBoxResponseDto myMailBoxResponseDto = new MyMailBoxResponseDto();
        myMailBoxResponseDto.setMailId(mailInfo.getId());
        /*추후 제2, 제3의 관리자가 메일을 주거나 해야할때 각각의 관리자 이름을 정하도록 하고 그전까지는 From 은 운영자로 통칭*/
        myMailBoxResponseDto.setFrom("운영자");
        myMailBoxResponseDto.setTitle(mailInfo.getTitle());
        myMailBoxResponseDto.setContent(mailInfo.getContent());
        myMailBoxResponseDto.setGettingItems(mailInfo.getGettingItems());
        myMailBoxResponseDto.setGettingItemCounts(mailInfo.getGettingItemCounts());
        myMailBoxResponseDto.setSendDate(mailInfo.getSendDate());
        myMailBoxResponseDto.setExpireDate(mailInfo.getExpireDate());
        myMailBoxResponseDto.setMailType(mailInfo.getMailType());
        myMailBoxResponseDto.setHasRead(mailBoxInfo.hasRead);
        myMailBoxResponseDto.setReceived(mailBoxInfo.received);

        map.put("myMailBoxResponseDto", myMailBoxResponseDto);

        /*업적 : 미션 데이터 변경점 적용*/
        if(changedMissionsData) {
            jsonMyMissionData = JsonStringHerlper.WriteValueAsStringFromData(myMissionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMyMissionData);
            map.put("exchange_myMissionsData", true);
            map.put("myMissionsDataDto", myMissionsDataDto);
            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());
        }
        /* 패스 업적 : 미션 데이터 변경점 적용*/
        if(changedEternalPassMissionsData) {
            jsonMyEternalPassMissionData = JsonStringHerlper.WriteValueAsStringFromData(myEternalPassMissionDataDto);
            myEternalPassMissionsData.ResetSaveDataValue(jsonMyEternalPassMissionData);
            map.put("exchange_myEternalPassMissionsData", true);
            map.put("myEternalPassMissionsDataDto", myEternalPassMissionDataDto);
            map.put("myEternalPassLastDailyMissionClearTime", myEternalPassMissionsData.getLastDailyMissionClearTime());
            map.put("myEternalPassLastWeeklyMissionClearTime", myEternalPassMissionsData.getLastWeeklyMissionClearTime());
        }
        return map;
    }

    public Map<String, Object> ReadOptionMail(Long userId, Long mailId, String selectedCode, Map<String, Object> map) {
        MyMailBox myMailBox = myMailBoxRepository.findByUseridUser(userId)
                .orElse(null);
        if(myMailBox == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMailBox not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMailBox not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String json_myMailBoxInfo = myMailBox.getJson_myMailBoxInfo();
        MailBoxDto mailBoxDto = JsonStringHerlper.ReadValueFromJson(json_myMailBoxInfo, MailBoxDto.class);
        MailBoxDto.MailBoxInfo mailBoxInfo = mailBoxDto.GetMail(mailId);
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
        for(Mail mail : mailList) {
            if(now.isAfter(mail.getExpireDate())) {
                deleteExpireMailList.add(mail);
            }
        }
        mailRepository.deleteAll(deleteExpireMailList);
        Mail mailInfo = mailList.stream().filter(mail -> mail.getId().equals(mailId)).findAny().orElse(null);
        if(mailInfo == null) {
            List<MailBoxDto.MailBoxInfo> deleteExpireMyMailBoxList = new ArrayList<>();
            //Expire 된 메일 삭제
            for(MailBoxDto.MailBoxInfo mailBoxInfos : mailBoxDto.mailBoxInfoList) {
                Mail mailInfos = mailList.stream().filter(mail -> mail.getId().equals(mailBoxInfos.mailId)).findAny().orElse(null);
                if(mailInfos == null) {
                    deleteExpireMyMailBoxList.add(mailBoxInfos);
                }
            }
            mailBoxDto.mailBoxInfoList.removeAll(deleteExpireMyMailBoxList);

            //메일리스트에 있는 메일이 내 메일함에 없으면 ADD
            for(Mail mail : mailList) {
                Long toId = mail.getToId();
                if(toId != 0 && !toId.equals(userId)) {
                    continue;
                }
                MailBoxDto.MailBoxInfo selectMailBoxInfo = mailBoxDto.mailBoxInfoList.stream().filter(mailBoxInfos -> mailBoxInfos.mailId.equals(mail.getId())).findAny().orElse(null);
                if(selectMailBoxInfo == null) {
                    selectMailBoxInfo = new MailBoxDto.MailBoxInfo();
                    selectMailBoxInfo.mailId = mail.getId();
                    selectMailBoxInfo.hasRead = false;
                    selectMailBoxInfo.received = false;
                    mailBoxDto.mailBoxInfoList.add(selectMailBoxInfo);
                }
                //메일을 더 받을수 있는 상황인지 체크
                if(!mailBoxDto.AddAbleMail())
                    break;
            }
            json_myMailBoxInfo = JsonStringHerlper.WriteValueAsStringFromData(mailBoxDto);
            myMailBox.ResetJsonMyMailBoxInfo(json_myMailBoxInfo);

            //API 응답을 위한 메일 정보 구성
            List<MyMailBoxResponseDto> myMailBoxResponseDtoList = new ArrayList<>();
            for(MailBoxDto.MailBoxInfo mailBoxInfos : mailBoxDto.mailBoxInfoList) {
                Mail mailInfos = mailList.stream().filter(mail -> mail.getId().equals(mailBoxInfos.mailId)).findAny().orElse(null);
                if(mailInfos != null) {
                    MyMailBoxResponseDto myMailBoxResponseDto = new MyMailBoxResponseDto();
                    myMailBoxResponseDto.setMailId(mailInfos.getId());
                    /*추후 제2, 제3의 관리자가 메일을 주거나 해야할때 각각의 관리자 이름을 정하도록 하고 그전까지는 From 은 운영자로 통칭*/
                    myMailBoxResponseDto.setFrom("운영자");
                    myMailBoxResponseDto.setTitle(mailInfos.getTitle());
                    myMailBoxResponseDto.setContent(mailInfos.getContent());
                    myMailBoxResponseDto.setGettingItems(mailInfos.getGettingItems());
                    myMailBoxResponseDto.setGettingItemCounts(mailInfos.getGettingItemCounts());
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

        MyMissionsData myMissionsData = null;
        /*업적 : 체크 준비*/
        if(myMissionsData == null)
            myMissionsData = myMissionsDataRepository.findByUseridUser(userId).orElseThrow(() -> new MyCustomException("Fail! -> Cause: MyMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA));
        String jsonMyMissionData = myMissionsData.getJson_saveDataValue();
        MissionsDataDto myMissionsDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyMissionData, MissionsDataDto.class);
        boolean changedMissionsData = false;

        //메일 읽음 처리
        //타입별 처리
        if(mailInfo.getMailType() == 2 || mailInfo.getMailType() == 3 || mailInfo.getMailType() == 4) { // 선택권
            MailGettingItemsResponseDto mailGettingItemsResponseDto = new MailGettingItemsResponseDto();
            changedMissionsData = optionReceive(userId, mailInfo, selectedCode, mailGettingItemsResponseDto, myMissionsDataDto, map) || changedMissionsData;

            mailBoxInfo.received = true;
            mailBoxInfo.hasRead = true;
            map.put("mailGettingItems", mailGettingItemsResponseDto);
        }
        else {//일반 메시지
            mailBoxInfo.received = true;
        }
        json_myMailBoxInfo = JsonStringHerlper.WriteValueAsStringFromData(mailBoxDto);
        myMailBox.ResetJsonMyMailBoxInfo(json_myMailBoxInfo);

        MyMailBoxResponseDto myMailBoxResponseDto = new MyMailBoxResponseDto();
        myMailBoxResponseDto.setMailId(mailInfo.getId());
        /*추후 제2, 제3의 관리자가 메일을 주거나 해야할때 각각의 관리자 이름을 정하도록 하고 그전까지는 From 은 운영자로 통칭*/
        myMailBoxResponseDto.setFrom("운영자");
        myMailBoxResponseDto.setTitle(mailInfo.getTitle());
        myMailBoxResponseDto.setContent(mailInfo.getContent());
        myMailBoxResponseDto.setGettingItems(mailInfo.getGettingItems());
        myMailBoxResponseDto.setGettingItemCounts(mailInfo.getGettingItemCounts());
        myMailBoxResponseDto.setSendDate(mailInfo.getSendDate());
        myMailBoxResponseDto.setExpireDate(mailInfo.getExpireDate());
        myMailBoxResponseDto.setMailType(mailInfo.getMailType());
        myMailBoxResponseDto.setHasRead(mailBoxInfo.hasRead);
        myMailBoxResponseDto.setReceived(mailBoxInfo.received);

        map.put("myMailBoxResponseDto", myMailBoxResponseDto);

        /*업적 : 미션 데이터 변경점 적용*/
        if(changedMissionsData) {
            jsonMyMissionData = JsonStringHerlper.WriteValueAsStringFromData(myMissionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMyMissionData);
            map.put("exchange_myMissionsData", true);
            map.put("myMissionsDataDto", myMissionsDataDto);
            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());
        }
        return map;
    }

    public Map<String, Object> ReadMailAll(Long userId, Map<String, Object> map){

        MyMailBox myMailBox = myMailBoxRepository.findByUseridUser(userId)
                .orElse(null);
        if(myMailBox == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMailBox not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMailBox not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String json_myMailBoxInfo = myMailBox.getJson_myMailBoxInfo();
        MailBoxDto mailBoxDto = JsonStringHerlper.ReadValueFromJson(json_myMailBoxInfo, MailBoxDto.class);

        MyMissionsData myMissionsData = null;
        /*업적 : 체크 준비*/
        if(myMissionsData == null) {
            myMissionsData = myMissionsDataRepository.findByUseridUser(userId)
                    .orElse(null);
            if(myMissionsData == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: MyMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
        }
        String jsonMyMissionData = myMissionsData.getJson_saveDataValue();
        MissionsDataDto myMissionsDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyMissionData, MissionsDataDto.class);
        boolean changedMissionsData = false;

        /*패스 업적 : 체크 준비*/
        MyEternalPassMissionsData myEternalPassMissionsData = myEternalPassMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myEternalPassMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEternalPassMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyEternalPassMissionData = myEternalPassMissionsData.getJson_saveDataValue();
        EventMissionDataDto myEternalPassMissionDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyEternalPassMissionData, EventMissionDataDto.class);
        boolean changedEternalPassMissionsData = false;

        //API 응답을 위한 메일 정보
        List<MyMailBoxResponseDto> myMailBoxResponseDtoList = new ArrayList<>();
        //보상 아이템 정보
        MailGettingItemsResponseDto mailGettingItemsResponseDto = new MailGettingItemsResponseDto();

        List<BelongingInventory> belongingInventoryList = null;
        List<ItemType> itemTypeList = null;
        ItemType spendAbleItemType = null;
        ItemType materialItemType = null;
        User user = null;
        MyGiftInventory myGiftInventory = null;
        List<MyCharacters> myCharactersList = null;
        List<HeroEquipmentInventory> heroEquipmentInventoryList = null;

        List<BelongingCharacterPieceTable> orignalBelongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
        List<BelongingCharacterPieceTable> copyBelongingCharacterPieceTableList = new ArrayList<>();
        for(BelongingCharacterPieceTable characterPieceTable : orignalBelongingCharacterPieceTableList) {
            if(characterPieceTable.getCode().equals("characterPieceAll"))
                continue;
            copyBelongingCharacterPieceTableList.add(characterPieceTable);
        }

        List<Mail> mailList = mailRepository.findAllByToIdOrToId(0L, userId);

        LocalDateTime now = LocalDateTime.now();
        List<Mail> deleteExpireMailList = new ArrayList<>();
        for(Mail mail : mailList) {
            if(now.isAfter(mail.getExpireDate())) {
                deleteExpireMailList.add(mail);
            }
        }
        mailRepository.deleteAll(deleteExpireMailList);

        for(MailBoxDto.MailBoxInfo mailBoxInfo : mailBoxDto.mailBoxInfoList){
            if(mailBoxInfo == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMail not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: MyMail not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            if(mailBoxInfo.received)
                continue;

            Mail mailInfo = mailList.stream().filter(mail -> mail.getId().equals(mailBoxInfo.mailId)).findAny().orElse(null);
            if(mailInfo == null) {
                List<MailBoxDto.MailBoxInfo> deleteExpireMyMailBoxList = new ArrayList<>();
                //Expire 된 메일 삭제
                for(MailBoxDto.MailBoxInfo mailBoxInfos : mailBoxDto.mailBoxInfoList) {
                    Mail mailInfos = mailList.stream().filter(mail -> mail.getId().equals(mailBoxInfos.mailId)).findAny().orElse(null);
                    if(mailInfos == null) {
                        deleteExpireMyMailBoxList.add(mailBoxInfos);
                    }
                }
                mailBoxDto.mailBoxInfoList.removeAll(deleteExpireMyMailBoxList);

                //메일리스트에 있는 메일이 내 메일함에 없으면 ADD
                for(Mail mail : mailList) {
                    Long toId = mail.getToId();
                    if(toId != 0 && !toId.equals(userId)) {
                        continue;
                    }
                    MailBoxDto.MailBoxInfo selectMailBoxInfo = mailBoxDto.mailBoxInfoList.stream().filter(mailBoxInfos -> mailBoxInfos.mailId.equals(mail.getId())).findAny().orElse(null);
                    if(selectMailBoxInfo == null) {
                        selectMailBoxInfo = new MailBoxDto.MailBoxInfo();
                        selectMailBoxInfo.mailId = mail.getId();
                        selectMailBoxInfo.hasRead = false;
                        selectMailBoxInfo.received = false;
                        mailBoxDto.mailBoxInfoList.add(selectMailBoxInfo);
                    }
                    //메일을 더 받을수 있는 상황인지 체크
                    if(!mailBoxDto.AddAbleMail())
                        break;
                }
                json_myMailBoxInfo = JsonStringHerlper.WriteValueAsStringFromData(mailBoxDto);
                myMailBox.ResetJsonMyMailBoxInfo(json_myMailBoxInfo);

                //API 응답을 위한 메일 정보 구성
                List<MyMailBoxResponseDto> myNewMailBoxResponseDtoList = new ArrayList<>();
                for(MailBoxDto.MailBoxInfo mailBoxInfos : mailBoxDto.mailBoxInfoList) {
                    Mail mailInfos = mailList.stream().filter(mail -> mail.getId().equals(mailBoxInfo.mailId)).findAny().orElse(null);
                    if(mailInfos != null) {
                        MyMailBoxResponseDto myMailBoxResponseDto = new MyMailBoxResponseDto();
                        myMailBoxResponseDto.setMailId(mailInfos.getId());
                        /*추후 제2, 제3의 관리자가 메일을 주거나 해야할때 각각의 관리자 이름을 정하도록 하고 그전까지는 From 은 운영자로 통칭*/
                        myMailBoxResponseDto.setFrom("운영자");
                        myMailBoxResponseDto.setTitle(mailInfos.getTitle());
                        myMailBoxResponseDto.setContent(mailInfos.getContent());
                        myMailBoxResponseDto.setGettingItems(mailInfos.getGettingItems());
                        myMailBoxResponseDto.setGettingItemCounts(mailInfos.getGettingItemCounts());
                        myMailBoxResponseDto.setSendDate(mailInfos.getSendDate());
                        myMailBoxResponseDto.setExpireDate(mailInfos.getExpireDate());
                        myMailBoxResponseDto.setMailType(mailInfos.getMailType());
                        myMailBoxResponseDto.setHasRead(mailBoxInfos.hasRead);
                        myMailBoxResponseDto.setReceived(mailBoxInfos.received);
                        myNewMailBoxResponseDtoList.add(myMailBoxResponseDto);
                    }
                }
                map.put("exchanged_myMailBox", true);
                map.put("myMailBoxResponseDtoList", myNewMailBoxResponseDtoList);
                return map;
            }
            //메일 읽음 처리
            //타입별 처리
            if(mailInfo.getMailType() == 1) { //아이템 선물 첨부
                    String[] gettingItemsArray = mailInfo.getGettingItems().split(",");
                    String[] itemsCountArray = mailInfo.getGettingItemCounts().split(",");
                    int gettingItemsCount = gettingItemsArray.length;
                    for(int i = 0; i < gettingItemsCount; i++) {
                        String gettingItemCode = gettingItemsArray[i];
                        int gettingCount = Integer.parseInt(itemsCountArray[i]);

                        //피로도 50 회복 물약, 즉시 제작권, 차원석, 강화석, 재련석, 링크웨폰키, 코스튬 무료 티켓
                        if(gettingItemCode.equals("recovery_fatigability") || gettingItemCode.equals("ticket_direct_production_equipment")
                                || gettingItemCode.equals("dimensionStone") || gettingItemCode.contains("enchant") || gettingItemCode.contains("resmelt")
                                || gettingItemCode.equals("linkweapon_bronzeKey") || gettingItemCode.equals("linkweapon_silverKey")
                                || gettingItemCode.equals("linkweapon_goldKey") || gettingItemCode.equals("costume_ticket")) {
                            if(belongingInventoryList == null)
                                belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                            List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
                            if(itemTypeList == null)
                                itemTypeList = itemTypeRepository.findAll();
                            if(spendAbleItemType == null)
                                spendAbleItemType = itemTypeList.stream()
                                        .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                                        .findAny()
                                        .orElse(null);
                            List<BelongingInventoryDto> receivedSpendableItemList = mailGettingItemsResponseDto.getChangedBelongingInventoryList();
                            BelongingInventoryDto belongingInventoryDto = ApplySomeReward.ApplySpendableItem(userId, gettingItemCode, gettingCount, belongingInventoryRepository, belongingInventoryList, spendableItemInfoTableList, spendAbleItemType, mailBoxInfo.mailId+"번 메일 확인", loggingService, errorLoggingService);
                            int itemId = belongingInventoryDto.getItemId();
                            Long itemTypeId = belongingInventoryDto.getItemType().getId();
                            BelongingInventoryDto findBelongingInventoryDto = receivedSpendableItemList.stream().filter(a -> a.getItemId()==itemId && a.getItemType().getId().equals(itemTypeId))
                                    .findAny()
                                    .orElse(null);
                            if(findBelongingInventoryDto == null) {
                                receivedSpendableItemList.add(belongingInventoryDto);
                            }
                            else {
                                findBelongingInventoryDto.AddCount(gettingCount);
                            }
                        }
                        //골드
                        else if(gettingItemCode.equals("gold")) {
                            CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                            if(user == null) {
                                user = userRepository.findById(userId)
                                        .orElse(null);
                                if(user == null) {
                                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                                }
                            }
                            int previousCount = user.getGold();
                            user.AddGold(gettingCount);
                            /* 패스 업적 : 골드획득*/
                            changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.ERNING_GOLD.name(),"empty", gettingCount, gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
                            currencyLogDto.setCurrencyLogDto(mailBoxInfo.mailId+"번 메일 확인", "골드", previousCount, gettingCount, user.getGold());
                            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                            loggingService.setLogging(userId, 1, log);
                            mailGettingItemsResponseDto.AddGettingGold(gettingCount);
                        }
                        //다이아
                        else if(gettingItemCode.equals("diamond")) {
                            CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                            if(user == null) {
                                user = userRepository.findById(userId)
                                        .orElse(null);
                                if(user == null) {
                                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                                }
                            }
                            int previousCount = user.getDiamond();
                            user.AddDiamond(gettingCount);
                            currencyLogDto.setCurrencyLogDto(mailBoxInfo.mailId+"번 메일 확인", "다이아", previousCount, gettingCount, user.getDiamond());
                            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                            loggingService.setLogging(userId, 1, log);
                            mailGettingItemsResponseDto.AddGettingDiamond(gettingCount);
                        }
                        //링크 포인트
                        else if(gettingItemCode.equals("linkPoint")) {
                            CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                            if(user == null) {
                                user = userRepository.findById(userId)
                                        .orElse(null);
                                if(user == null) {
                                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                                }
                            }
                            int previousCount = user.getLinkforcePoint();
                            user.AddLinkforcePoint(gettingCount);
                            currencyLogDto.setCurrencyLogDto(mailBoxInfo.mailId+"번 메일 확인", "링크포인트", previousCount, gettingCount, user.getLinkforcePoint());
                            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                            loggingService.setLogging(userId, 1, log);
                            mailGettingItemsResponseDto.AddGettingLinkPoint(gettingCount);
                        }
                        //아레나 코인
                        else if(gettingItemCode.equals("arenaCoin")) {
                            CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                            if(user == null) {
                                user = userRepository.findById(userId)
                                        .orElse(null);
                                if(user == null) {
                                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                                }
                            }
                            int previousCount = user.getArenaCoin();
                            user.AddArenaCoin(gettingCount);
                            currencyLogDto.setCurrencyLogDto(mailBoxInfo.mailId+"번 메일 확인", "아레나 코인", previousCount, gettingCount, user.getArenaCoin());
                            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                            loggingService.setLogging(userId, 1, log);
                            mailGettingItemsResponseDto.AddGettingArenaCoin(gettingCount);
                        }
                        //아레나 티켓
                        else if(gettingItemCode.equals("arenaTicket")) {
                            CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                            if(user == null) {
                                user = userRepository.findById(userId)
                                        .orElse(null);
                                if(user == null) {
                                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                                }
                            }
                            int previousCount = user.getArenaTicket();
                            user.AddArenaTicket(gettingCount);
                            currencyLogDto.setCurrencyLogDto(mailBoxInfo.mailId+"번 메일 확인", "아레나 티켓", previousCount, gettingCount, user.getArenaTicket());
                            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                            loggingService.setLogging(userId, 1, log);
                            mailGettingItemsResponseDto.AddGettingArenaTicket(gettingCount);
                        }
                        else if(gettingItemCode.equals("lowDragonScale")) {
                            CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                            if(user == null) {
                                user = userRepository.findById(userId)
                                        .orElse(null);
                                if(user == null) {
                                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                                }
                            }
                            int previousCount = user.getLowDragonScale();
                            user.AddLowDragonScale(gettingCount);
                            currencyLogDto.setCurrencyLogDto(mailBoxInfo.mailId+"번 메일 확인", "용의 비늘(전설)", previousCount, gettingCount, user.getLowDragonScale());
                            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                            loggingService.setLogging(userId, 1, log);
                            mailGettingItemsResponseDto.AddGettingLowDragonScale(gettingCount);
                        }
                        else if(gettingItemCode.equals("middleDragonScale")) {
                            CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                            if(user == null) {
                                user = userRepository.findById(userId)
                                        .orElse(null);
                                if(user == null) {
                                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                                }
                            }
                            int previousCount = user.getMiddleDragonScale();
                            user.AddMiddleDragonScale(gettingCount);
                            currencyLogDto.setCurrencyLogDto(mailBoxInfo.mailId+"번 메일 확인", "용의 비늘(신성)", previousCount, gettingCount, user.getMiddleDragonScale());
                            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                            loggingService.setLogging(userId, 1, log);
                            mailGettingItemsResponseDto.AddGettingMiddleDragonScale(gettingCount);
                        }
                        else if(gettingItemCode.equals("highDragonScale")) {
                            CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                            if(user == null) {
                                user = userRepository.findById(userId)
                                        .orElse(null);
                                if(user == null) {
                                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                                }
                            }
                            int previousCount = user.getHighDragonScale();
                            user.AddHighDragonScale(gettingCount);
                            currencyLogDto.setCurrencyLogDto(mailBoxInfo.mailId+"번 메일 확인", "용의 비늘(고대)", previousCount, gettingCount, user.getHighDragonScale());
                            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                            loggingService.setLogging(userId, 1, log);
                            mailGettingItemsResponseDto.AddGettingHighDragonScale(gettingCount);
                        }
                        else if(gettingItemCode.equals("darkobe")) {
                            CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                            if(user == null) {
                                user = userRepository.findById(userId)
                                        .orElse(null);
                                if(user == null) {
                                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                                }
                            }
                            int previousCount = user.getDarkObe();
                            user.AddDarkObe(gettingCount);
                            currencyLogDto.setCurrencyLogDto(mailBoxInfo.mailId+"번 메일 확인", "어둠의 보주", previousCount, gettingCount, user.getDarkObe());
                            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                            loggingService.setLogging(userId, 1, log);
                            mailGettingItemsResponseDto.AddGettingDarkObe(gettingCount);
                        }
                        /*3종, 5종, 8종 재료 상자*/
                        else if(gettingItemCode.equals("reward_material_low") || gettingItemCode.equals("reward_material_middle") || gettingItemCode.equals("reward_material_high")) {
                            int kindCount = 0;
                            if (gettingItemCode.contains("low")) {
                                //3종
                                kindCount = 3;
                            } else if (gettingItemCode.contains("middle")) {
                                //5종
                                kindCount = 5;
                            }
                            else if (gettingItemCode.contains("high")) {
                                //8종
                                kindCount = 8;
                            }
                            List<BelongingInventoryDto> receivedSpendableItemList = mailGettingItemsResponseDto.getChangedBelongingInventoryList();
                            if(belongingInventoryList == null)
                                belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                            List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList = gameDataTableService.EquipmentMaterialInfoTableList();
                            if(itemTypeList == null)
                                itemTypeList = itemTypeRepository.findAll();
                            if(materialItemType == null) {
                                materialItemType = itemTypeList.stream()
                                        .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Material)
                                        .findAny()
                                        .orElse(null);
                            }
                            List<EquipmentMaterialInfoTable> copyEquipmentMaterialInfoTableList = new ArrayList<>();
                            copyEquipmentMaterialInfoTableList.addAll(equipmentMaterialInfoTableList);
                            int addIndex = 0;
                            while (addIndex < kindCount) {
                                int selectedRandomIndex = (int) MathHelper.Range(0, copyEquipmentMaterialInfoTableList.size());
                                EquipmentMaterialInfoTable equipmentMaterialInfoTable = copyEquipmentMaterialInfoTableList.get(selectedRandomIndex);
                                BelongingInventoryDto belongingInventoryDto = ApplySomeReward.AddEquipmentMaterialItem(equipmentMaterialInfoTable.getCode(), gettingCount, belongingInventoryRepository, itemTypeList, belongingInventoryList, equipmentMaterialInfoTableList, materialItemType, mailBoxInfo.mailId+"번 메일 확인", userId, loggingService, errorLoggingService);
                                int itemId = belongingInventoryDto.getItemId();
                                Long itemTypeId = belongingInventoryDto.getItemType().getId();
                                BelongingInventoryDto findBelongingInventoryDto = receivedSpendableItemList.stream().filter(a -> a.getItemId()==itemId && a.getItemType().getId().equals(itemTypeId))
                                        .findAny()
                                        .orElse(null);
                                if(findBelongingInventoryDto == null) {
                                    receivedSpendableItemList.add(belongingInventoryDto);
                                }
                                else {
                                    findBelongingInventoryDto.AddCount(gettingCount);
                                }
                                copyEquipmentMaterialInfoTableList.remove(selectedRandomIndex);
                                addIndex++;
                            }
                        }
                        /*특정 재료*/
                        else if(gettingItemCode.contains("material")){
                            List<BelongingInventoryDto> receivedSpendableItemList = mailGettingItemsResponseDto.getChangedBelongingInventoryList();
                            if(belongingInventoryList == null)
                                belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                            List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList = gameDataTableService.EquipmentMaterialInfoTableList();
                            if(itemTypeList == null)
                                itemTypeList = itemTypeRepository.findAll();
                            if(materialItemType == null) {
                                materialItemType = itemTypeList.stream()
                                        .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Material)
                                        .findAny()
                                        .orElse(null);
                            }
                            BelongingInventoryDto belongingInventoryDto = ApplySomeReward.AddEquipmentMaterialItem(gettingItemCode, gettingCount, belongingInventoryRepository, itemTypeList, belongingInventoryList, equipmentMaterialInfoTableList, materialItemType, mailBoxInfo.mailId+"번 메일 확인", userId, loggingService, errorLoggingService);
                            int itemId = belongingInventoryDto.getItemId();
                            Long itemTypeId = belongingInventoryDto.getItemType().getId();
                            BelongingInventoryDto findBelongingInventoryDto = receivedSpendableItemList.stream().filter(a -> a.getItemId()==itemId && a.getItemType().getId().equals(itemTypeId))
                                    .findAny()
                                    .orElse(null);
                            if(findBelongingInventoryDto == null) {
                                receivedSpendableItemList.add(belongingInventoryDto);
                            }
                            else {
                                findBelongingInventoryDto.AddCount(gettingCount);
                            }
                        }
                        /*모든 선물중 하나*/
                        else if(gettingItemCode.equals("giftAll")) {
                            List<GiftTable> giftTableList = gameDataTableService.GiftsTableList();
                            List<GiftTable> copyGiftTableList = new ArrayList<>(giftTableList);
                            copyGiftTableList.remove(25);
                            int randIndex = (int) MathHelper.Range(0, copyGiftTableList.size());
                            GiftTable giftTable = copyGiftTableList.get(randIndex);
                            if(myGiftInventory == null) {
                                myGiftInventory = myGiftInventoryRepository.findByUseridUser(userId)
                                        .orElse(null);
                                if(myGiftInventory == null) {
                                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyGiftInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                    throw new MyCustomException("Fail! -> Cause: MyGiftInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
                                }
                            }
                            List<GiftItemDtosList.GiftItemDto> changedMyGiftInventoryList = mailGettingItemsResponseDto.getChangedMyGiftInventoryList();
                            String inventoryInfosString = myGiftInventory.getInventoryInfos();
                            GiftItemDtosList giftItemInventorys = JsonStringHerlper.ReadValueFromJson(inventoryInfosString, GiftItemDtosList.class);

                            GiftItemDtosList.GiftItemDto inventoryItemDto = giftItemInventorys.giftItemDtoList.stream()
                                    .filter(a -> a.code.equals(giftTable.getCode()))
                                    .findAny()
                                    .orElse(null);
                            if(inventoryItemDto == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find GiftTable", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Fail -> Cause: Can't Find GiftTable", ResponseErrorCode.NOT_FIND_DATA);
                            }
                            GiftLogDto giftLogDto = new GiftLogDto();
                            giftLogDto.setPreviousValue(inventoryItemDto.count);
                            inventoryItemDto.AddItem(gettingCount ,giftTable.getStackLimit());
                            giftLogDto.setGiftLogDto(mailBoxInfo.mailId+"번 메일 확인", inventoryItemDto.code,gettingCount, inventoryItemDto.count);
                            String log = JsonStringHerlper.WriteValueAsStringFromData(giftLogDto);
                            loggingService.setLogging(userId, 4, log);
                            inventoryInfosString = JsonStringHerlper.WriteValueAsStringFromData(giftItemInventorys);
                            myGiftInventory.ResetInventoryInfos(inventoryInfosString);
                            GiftItemDtosList.GiftItemDto responseItemDto = new GiftItemDtosList.GiftItemDto();
                            responseItemDto.code = inventoryItemDto.code;
                            responseItemDto.count = gettingCount;
                            changedMyGiftInventoryList.add(responseItemDto);
                        }
                        /*특정 선물*/
                        else if(gettingItemCode.contains("gift_")) {
                            List<GiftTable> giftTableList = gameDataTableService.GiftsTableList();
                            GiftTable giftTable = giftTableList.stream().filter(a -> a.getCode().equals(gettingItemCode)).findAny().orElse(null);
                            if(giftTable == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: GiftTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Fail! -> Cause: GiftTable not find.", ResponseErrorCode.NOT_FIND_DATA);
                            }
                            if(myGiftInventory == null) {
                                myGiftInventory = myGiftInventoryRepository.findByUseridUser(userId)
                                        .orElse(null);
                                if(myGiftInventory == null) {
                                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyGiftInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                    throw new MyCustomException("Fail! -> Cause: MyGiftInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
                                }
                            }
                            List<GiftItemDtosList.GiftItemDto> changedMyGiftInventoryList = mailGettingItemsResponseDto.getChangedMyGiftInventoryList();
                            String inventoryInfosString = myGiftInventory.getInventoryInfos();
                            GiftItemDtosList giftItemInventorys = JsonStringHerlper.ReadValueFromJson(inventoryInfosString, GiftItemDtosList.class);

                            GiftItemDtosList.GiftItemDto inventoryItemDto = giftItemInventorys.giftItemDtoList.stream()
                                    .filter(a -> a.code.equals(giftTable.getCode()))
                                    .findAny()
                                    .orElse(null);
                            if(inventoryItemDto == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find GiftTable", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Fail -> Cause: Can't Find GiftTable", ResponseErrorCode.NOT_FIND_DATA);
                            }
                            GiftLogDto giftLogDto = new GiftLogDto();
                            giftLogDto.setPreviousValue(inventoryItemDto.count);

                            inventoryItemDto.AddItem(gettingCount ,giftTable.getStackLimit());
                            giftLogDto.setGiftLogDto(mailBoxInfo.mailId+"번 메일 확인", inventoryItemDto.code,gettingCount, inventoryItemDto.count);
                            String log = JsonStringHerlper.WriteValueAsStringFromData(giftLogDto);
                            loggingService.setLogging(userId, 4, log);
                            inventoryInfosString = JsonStringHerlper.WriteValueAsStringFromData(giftItemInventorys);
                            myGiftInventory.ResetInventoryInfos(inventoryInfosString);

                            GiftItemDtosList.GiftItemDto responseItemDto = new GiftItemDtosList.GiftItemDto();
                            responseItemDto.code = inventoryItemDto.code;
                            responseItemDto.count = gettingCount;
                            changedMyGiftInventoryList.add(responseItemDto);
                        }
                        /*모든 케릭터 조각중 하나*/
                        else if(gettingItemCode.equals("characterPieceAll")) {
                            BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                            //List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
                            int randIndex = (int) MathHelper.Range(0, copyBelongingCharacterPieceTableList.size());
                            BelongingCharacterPieceTable selectedCharacterPiece = copyBelongingCharacterPieceTableList.get(randIndex);
                            List<BelongingInventoryDto> changedCharacterPieceList = mailGettingItemsResponseDto.getChangedBelongingInventoryList();
                            if(itemTypeList == null)
                                itemTypeList = itemTypeRepository.findAll();
                            ItemType belongingCharacterPieceItemType = itemTypeList.stream()
                                    .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece)
                                    .findAny()
                                    .orElse(null);
                            if(belongingInventoryList == null)
                                belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                            BelongingInventory myCharacterPieceItem = belongingInventoryList.stream()
                                    .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece && a.getItemId() == selectedCharacterPiece.getId())
                                    .findAny()
                                    .orElse(null);

                            if(myCharacterPieceItem == null) {
                                belongingInventoryLogDto.setPreviousValue(0);
                                BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                                belongingInventoryDto.setUseridUser(userId);
                                belongingInventoryDto.setItemId(selectedCharacterPiece.getId());
                                belongingInventoryDto.setCount(gettingCount);
                                belongingInventoryDto.setItemType(belongingCharacterPieceItemType);
                                myCharacterPieceItem = belongingInventoryDto.ToEntity();
                                myCharacterPieceItem = belongingInventoryRepository.save(myCharacterPieceItem);
                                belongingInventoryLogDto.setBelongingInventoryLogDto(mailBoxInfo.mailId+"번 메일 확인", myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
                                String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                                loggingService.setLogging(userId, 3, log);
                                belongingInventoryList.add(myCharacterPieceItem);
                                belongingInventoryDto.setId(myCharacterPieceItem.getId());
                                changedCharacterPieceList.add(belongingInventoryDto);
                            }
                            else {
                                belongingInventoryLogDto.setPreviousValue(myCharacterPieceItem.getCount());
                                myCharacterPieceItem.AddItem(gettingCount, selectedCharacterPiece.getStackLimit());
                                BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                                belongingInventoryDto.setUseridUser(userId);
                                belongingInventoryDto.setItemId(selectedCharacterPiece.getId());
                                belongingInventoryDto.setCount(gettingCount);
                                belongingInventoryDto.setItemType(belongingCharacterPieceItemType);
                                belongingInventoryLogDto.setBelongingInventoryLogDto(mailBoxInfo.mailId+"번 메일 확인", myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
                                String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                                loggingService.setLogging(userId, 3, log);
                                belongingInventoryDto.setId(myCharacterPieceItem.getId());
                                changedCharacterPieceList.add(belongingInventoryDto);
                            }
                        }
                        /*특정 케릭터 조각*/
                        else if(gettingItemCode.contains("characterPiece")) {
                            if(itemTypeList == null)
                                itemTypeList = itemTypeRepository.findAll();
                            ItemType belongingCharacterPieceItemType = itemTypeList.stream()
                                    .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece)
                                    .findAny()
                                    .orElse(null);

                            if(belongingInventoryList == null)
                                belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                            List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
                            if(itemTypeList == null)
                                itemTypeList = itemTypeRepository.findAll();
                            List<BelongingInventoryDto> receivedSpendableItemList = mailGettingItemsResponseDto.getChangedBelongingInventoryList();
                            BelongingInventoryDto belongingInventoryDto = ApplySomeReward.ApplyCharacterPiece(userId, gettingItemCode, gettingCount, belongingInventoryRepository, belongingInventoryList, belongingCharacterPieceTableList, belongingCharacterPieceItemType, mailInfo.getId()+"번 메일 확인", loggingService, errorLoggingService);
                            int itemId = belongingInventoryDto.getItemId();
                            Long itemTypeId = belongingInventoryDto.getItemType().getId();
                            BelongingInventoryDto findBelongingInventoryDto = receivedSpendableItemList.stream().filter(a -> a.getItemId()==itemId && a.getItemType().getId().equals(itemTypeId))
                                    .findAny()
                                    .orElse(null);
                            if(findBelongingInventoryDto == null) {
                                receivedSpendableItemList.add(belongingInventoryDto);
                            }
                            else {
                                findBelongingInventoryDto.AddCount(gettingCount);
                            }
                        }
                        /*모든 장비 중 하나*/
                        else if(gettingItemCode.equals("equipmentAll")){
                            if(user == null) {
                                user = userRepository.findById(userId)
                                        .orElse(null);
                                if(user == null) {
                                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                                }
                            }
                            //장비
                            if(heroEquipmentInventoryList == null)
                                heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());

                            if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
                            }
                            //등급 확률	영웅 70%	 전설 20%	신성 9%	고대 1%
                            //품질	D	C	B	A	S	SS	SSS
                            //확률	15%	25%	45%	9%	5%	1%	0%
                            List<Double> gradeProbabilityList = new ArrayList<>();
                            gradeProbabilityList.add(70D);
                            gradeProbabilityList.add(20D);
                            gradeProbabilityList.add(9D);
                            gradeProbabilityList.add(1D);
                            String selectedGrade = "";
                            int selectedIndex = MathHelper.RandomIndexWidthProbability(gradeProbabilityList);
                            switch (selectedIndex) {
                                case 0:
                                    selectedGrade = "Hero";
                                    break;
                                case 1:
                                    selectedGrade = "Legend";
                                    break;
                                case 2:
                                    selectedGrade = "Divine";
                                    break;
                                case 3:
                                    selectedGrade = "Ancient";
                                    break;
                            }

                            List<Double> classProbabilityList = new ArrayList<>();
                            classProbabilityList.add(15D);
                            classProbabilityList.add(25D);
                            classProbabilityList.add(45D);
                            classProbabilityList.add(9D);
                            classProbabilityList.add(5D);
                            classProbabilityList.add(1D);
                            classProbabilityList.add(0D);
                            String selectedClass = "";
                            selectedIndex = MathHelper.RandomIndexWidthProbability(classProbabilityList);
                            switch (selectedIndex) {
                                case 0:
                                    selectedClass = "D";
                                    break;
                                case 1:
                                    selectedClass = "C";
                                    break;
                                case 2:
                                    selectedClass = "B";
                                    break;
                                case 3:
                                    selectedClass = "A";
                                    break;
                                case 4:
                                    selectedClass = "S";
                                    break;
                                case 5:
                                    selectedClass = "SS";
                                    break;
                                case 6:
                                    selectedClass = "SSS";
                                    break;
                            }
                            List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
                            HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
                            int classValue = (int) EquipmentCalculate.GetClassValueFromClassCategory(selectedClass, classValues);
                            List<HeroEquipmentsTable> probabilityList = EquipmentCalculate.GetProbabilityList(heroEquipmentsTableList, EquipmentItemCategory.ALL, selectedGrade);
                            int randValue = (int)MathHelper.Range(0, probabilityList.size());
                            HeroEquipmentsTable selectEquipment = probabilityList.get(randValue);
                            List<EquipmentOptionsInfoTable> optionsInfoTableList = gameDataTableService.OptionsInfoTableList();
                            HeroEquipmentInventory generatedItem = EquipmentCalculate.CreateEquipment(user.getId(), selectEquipment, selectedClass, classValue, optionsInfoTableList);
                            generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
                            HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
                            heroEquipmentInventoryDto.InitFromDbData(generatedItem);
                            EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                            equipmentLogDto.setEquipmentLogDto(mailBoxInfo.mailId+"번 메일 확인", generatedItem.getId(), "추가", heroEquipmentInventoryDto);
                            String log = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                            loggingService.setLogging(userId, 2, log);
                            List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = mailGettingItemsResponseDto.getChangedHeroEquipmentInventoryList();
                            changedHeroEquipmentInventoryList.add(heroEquipmentInventoryDto);


                            /* 업적 : 장비 획득 (특정 품질) 미션 체크*/
                            changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(), generatedItem.getItemClass(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                            /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
                            changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), selectedGrade, gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

                        }
                        /*특정 등급, 특정 클래스, 특정 종류의 장비중 하나*/
                        else if(gettingItemCode.contains("equipment")) {
                            if(user == null) {
                                user = userRepository.findById(userId)
                                        .orElse(null);
                                if(user == null) {
                                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                                }
                            }
                            if(heroEquipmentInventoryList == null)
                                heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());

                            if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot())
                                throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);

                            List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
                            HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
                            List<EquipmentOptionsInfoTable> optionsInfoTableList = gameDataTableService.OptionsInfoTableList();
                            HeroEquipmentInventoryDto heroEquipmentInventoryDto = ApplySomeReward.AddEquipmentItem(user, gettingItemCode, heroEquipmentInventoryList, heroEquipmentInventoryRepository, heroEquipmentsTableList, classValues, optionsInfoTableList, mailBoxInfo.mailId+"번 메일 확인", loggingService, errorLoggingService);
                            List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = mailGettingItemsResponseDto.getChangedHeroEquipmentInventoryList();
                            changedHeroEquipmentInventoryList.add(heroEquipmentInventoryDto);

                            HeroEquipmentsTable selectedEquipmentItemTable = heroEquipmentsTableList.stream().filter(a -> a.getId() == heroEquipmentInventoryDto.getItem_Id()).findAny().orElse(null);

                            /* 업적 : 장비 획득 (특정 품질) 미션 체크*/
                            changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(), heroEquipmentInventoryDto.getItemClass(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                            /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
                            changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), selectedEquipmentItemTable.getGrade(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

                        }
                        /*인장 중 하나*/
                        else if(gettingItemCode.equals("stampAll")){
                            if(user == null) {
                                user = userRepository.findById(userId)
                                        .orElse(null);
                                if(user == null) {
                                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                                }
                            }
                            if(heroEquipmentInventoryList == null)
                                heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());

                            if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot())
                                throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);

                            List<PassiveItemTable> passiveItemTables = gameDataTableService.PassiveItemTableList();
                            List<PassiveItemTable> copyPassiveItemTables = new ArrayList<>();
                            copyPassiveItemTables.addAll(passiveItemTables);
                            List<PassiveItemTable> deleteList = new ArrayList<>();
                            boolean deleted = false;
                            for(PassiveItemTable passiveItemTable : copyPassiveItemTables){
                                String code = passiveItemTable.getCode();
                                if(code.equals("passiveItem_00_10")) {
                                    deleteList.add(passiveItemTable);
                                    deleted = true;
                                    break;
                                }
                            }
                            if(deleted)
                                copyPassiveItemTables.removeAll(deleteList);

                            int selectedIndex  = (int)MathHelper.Range(0, copyPassiveItemTables.size());
                            PassiveItemTable selectedPassiveItem = copyPassiveItemTables.get(selectedIndex);
                            List<Double> classProbabilityList = new ArrayList<>();
                            classProbabilityList.add(15D);
                            classProbabilityList.add(25D);
                            classProbabilityList.add(45D);
                            classProbabilityList.add(9D);
                            classProbabilityList.add(5D);
                            classProbabilityList.add(1D);
                            classProbabilityList.add(0D);
                            String selectedClass = "";
                            selectedIndex = MathHelper.RandomIndexWidthProbability(classProbabilityList);
                            switch (selectedIndex) {
                                case 0:
                                    selectedClass = "D";
                                    break;
                                case 1:
                                    selectedClass = "C";
                                    break;
                                case 2:
                                    selectedClass = "B";
                                    break;
                                case 3:
                                    selectedClass = "A";
                                    break;
                                case 4:
                                    selectedClass = "S";
                                    break;
                                case 5:
                                    selectedClass = "SS";
                                    break;
                                case 6:
                                    selectedClass = "SSS";
                                    break;
                            }
                            HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
                            int classValue = (int) EquipmentCalculate.GetClassValueFromClassCategory(selectedClass, classValues);

                            HeroEquipmentInventoryDto dto = new HeroEquipmentInventoryDto();
                            dto.setUseridUser(userId);
                            dto.setItem_Id(selectedPassiveItem.getId());
                            dto.setItemClassValue(classValue);
                            dto.setDecideDefaultAbilityValue(0);
                            dto.setDecideSecondAbilityValue(0);
                            dto.setLevel(1);
                            dto.setMaxLevel(1);
                            dto.setExp(0);
                            dto.setNextExp(0);
                            dto.setItemClass(selectedClass);

                            HeroEquipmentInventory generatedItem = dto.ToEntity();
                            generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
                            HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
                            heroEquipmentInventoryDto.InitFromDbData(generatedItem);
                            EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                            equipmentLogDto.setEquipmentLogDto(mailBoxInfo.mailId+"번 메일 확인", generatedItem.getId(), "추가", heroEquipmentInventoryDto);
                            String log = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                            loggingService.setLogging(userId, 2, log);
                            List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = mailGettingItemsResponseDto.getChangedHeroEquipmentInventoryList();
                            changedHeroEquipmentInventoryList.add(heroEquipmentInventoryDto);
                        }
                        /*특정 품질의 인장*/
                        else if(gettingItemCode.contains("stamp")){

                            if(user == null) {
                                user = userRepository.findById(userId)
                                        .orElse(null);
                                if(user == null) {
                                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                                }
                            }
                            if(heroEquipmentInventoryList == null)
                                heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());

                            if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
                            }

                            String itemClass = "D";
                            if(gettingItemCode.contains("ClassD")) {
                                itemClass = "D";
                            }
                            else if(gettingItemCode.contains("ClassC")){
                                itemClass = "C";
                            }
                            else if(gettingItemCode.contains("ClassB")){
                                itemClass = "B";
                            }
                            else if(gettingItemCode.contains("ClassA")){
                                itemClass = "A";
                            }
                            else if(gettingItemCode.contains("ClassSSS")){
                                itemClass = "SSS";
                            }
                            else if(gettingItemCode.contains("ClassSS")){
                                itemClass = "SS";
                            }
                            else if(gettingItemCode.contains("ClassS")){
                                itemClass = "S";
                            }

                            List<PassiveItemTable> passiveItemTables = gameDataTableService.PassiveItemTableList();
                            List<PassiveItemTable> copyPassiveItemTables = new ArrayList<>();
                            copyPassiveItemTables.addAll(passiveItemTables);
                            List<PassiveItemTable> deleteList = new ArrayList<>();
                            boolean deleted = false;
                            for(PassiveItemTable passiveItemTable : copyPassiveItemTables){
                                String code = passiveItemTable.getCode();
                                if(code.equals("passiveItem_00_10")) {
                                    deleteList.add(passiveItemTable);
                                    deleted = true;
                                    break;
                                }
                            }
                            if(deleted)
                                copyPassiveItemTables.removeAll(deleteList);

                            int selectedIndex  = (int)MathHelper.Range(0, copyPassiveItemTables.size());
                            PassiveItemTable selectedPassiveItem = copyPassiveItemTables.get(selectedIndex);

                            HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
                            int classValue = (int) EquipmentCalculate.GetClassValueFromClassCategory(itemClass, classValues);

                            HeroEquipmentInventoryDto dto = new HeroEquipmentInventoryDto();
                            dto.setUseridUser(userId);
                            dto.setItem_Id(selectedPassiveItem.getId());
                            dto.setItemClassValue(classValue);
                            dto.setDecideDefaultAbilityValue(0);
                            dto.setDecideSecondAbilityValue(0);
                            dto.setLevel(1);
                            dto.setMaxLevel(1);
                            dto.setExp(0);
                            dto.setNextExp(0);
                            dto.setItemClass(itemClass);

                            HeroEquipmentInventory generatedItem = dto.ToEntity();
                            generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
                            HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
                            heroEquipmentInventoryDto.InitFromDbData(generatedItem);
                            EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                            equipmentLogDto.setEquipmentLogDto(mailBoxInfo.mailId+"번 메일 확인", generatedItem.getId(), "추가", heroEquipmentInventoryDto);
                            String log = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                            loggingService.setLogging(userId, 2, log);
                            List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = mailGettingItemsResponseDto.getChangedHeroEquipmentInventoryList();
                            changedHeroEquipmentInventoryList.add(heroEquipmentInventoryDto);
                    }
                        /*특정 인장 중 하나*/
                        else if(gettingItemCode.contains("passiveItem")) {
                            if(user == null) {
                                user = userRepository.findById(userId)
                                        .orElse(null);
                                if(user == null) {
                                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                                }
                            }

                            if(heroEquipmentInventoryList == null)
                                heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());

                            if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
                            }

                            List<PassiveItemTable> passiveItemTables = gameDataTableService.PassiveItemTableList();

                            int selectedIndex  = (int)MathHelper.Range(0, passiveItemTables.size());
                            PassiveItemTable selectedPassiveItem = passiveItemTables.stream().filter(a -> a.getCode().equals(gettingItemCode)).findAny().orElse(null);
                            if(selectedPassiveItem == null) {
                                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find PassiveItemTable", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                                throw new MyCustomException("Fail -> Cause: Can't Find PassiveItemTable", ResponseErrorCode.NOT_FIND_DATA);
                            }
                            List<Double> classProbabilityList = new ArrayList<>();
                            classProbabilityList.add(15D);
                            classProbabilityList.add(25D);
                            classProbabilityList.add(45D);
                            classProbabilityList.add(9D);
                            classProbabilityList.add(5D);
                            classProbabilityList.add(1D);
                            classProbabilityList.add(0D);
                            String selectedClass = "";
                            selectedIndex = MathHelper.RandomIndexWidthProbability(classProbabilityList);
                            switch (selectedIndex) {
                                case 0:
                                    selectedClass = "D";
                                    break;
                                case 1:
                                    selectedClass = "C";
                                    break;
                                case 2:
                                    selectedClass = "B";
                                    break;
                                case 3:
                                    selectedClass = "A";
                                    break;
                                case 4:
                                    selectedClass = "S";
                                    break;
                                case 5:
                                    selectedClass = "SS";
                                    break;
                                case 6:
                                    selectedClass = "SSS";
                                    break;
                            }
                            HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
                            int classValue = (int) EquipmentCalculate.GetClassValueFromClassCategory(selectedClass, classValues);

                            HeroEquipmentInventoryDto dto = new HeroEquipmentInventoryDto();
                            dto.setUseridUser(userId);
                            dto.setItem_Id(selectedPassiveItem.getId());
                            dto.setItemClassValue(classValue);
                            dto.setDecideDefaultAbilityValue(0);
                            dto.setDecideSecondAbilityValue(0);
                            dto.setLevel(1);
                            dto.setMaxLevel(1);
                            dto.setExp(0);
                            dto.setNextExp(0);
                            dto.setItemClass(selectedClass);

                            HeroEquipmentInventory generatedItem = dto.ToEntity();
                            generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
                            HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
                            heroEquipmentInventoryDto.InitFromDbData(generatedItem);
                            EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                            equipmentLogDto.setEquipmentLogDto(mailInfo.getId()+"번 메일 확인", generatedItem.getId(), "추가", heroEquipmentInventoryDto);
                            String log = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                            loggingService.setLogging(userId, 2, log);

                            List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = mailGettingItemsResponseDto.getChangedHeroEquipmentInventoryList();
                            changedHeroEquipmentInventoryList.add(heroEquipmentInventoryDto);
                        }
                }

                mailBoxInfo.received = true;
                mailBoxInfo.hasRead = true;
                map.put("mailGettingItems", mailGettingItemsResponseDto);
            }
            else if (mailInfo.getMailType() == 0) //선택권은 패스
                mailBoxInfo.hasRead = true;

            MyMailBoxResponseDto myMailBoxResponseDto = new MyMailBoxResponseDto();
            myMailBoxResponseDto.setMailId(mailInfo.getId());
            /*추후 제2, 제3의 관리자가 메일을 주거나 해야할때 각각의 관리자 이름을 정하도록 하고 그전까지는 From 은 운영자로 통칭*/
            myMailBoxResponseDto.setFrom("운영자");
            myMailBoxResponseDto.setTitle(mailInfo.getTitle());
            myMailBoxResponseDto.setContent(mailInfo.getContent());
            myMailBoxResponseDto.setGettingItems(mailInfo.getGettingItems());
            myMailBoxResponseDto.setGettingItemCounts(mailInfo.getGettingItemCounts());
            myMailBoxResponseDto.setSendDate(mailInfo.getSendDate());
            myMailBoxResponseDto.setExpireDate(mailInfo.getExpireDate());
            myMailBoxResponseDto.setMailType(mailInfo.getMailType());
            myMailBoxResponseDto.setHasRead(mailBoxInfo.hasRead);
            myMailBoxResponseDto.setReceived(mailBoxInfo.received);
            myMailBoxResponseDtoList.add(myMailBoxResponseDto);
        }
        json_myMailBoxInfo = JsonStringHerlper.WriteValueAsStringFromData(mailBoxDto);
        myMailBox.ResetJsonMyMailBoxInfo(json_myMailBoxInfo);

        map.put("myMailBoxResponseDtoList", myMailBoxResponseDtoList);

        /*업적 : 미션 데이터 변경점 적용*/
        if(changedMissionsData) {
            jsonMyMissionData = JsonStringHerlper.WriteValueAsStringFromData(myMissionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMyMissionData);
            map.put("exchange_myMissionsData", true);
            map.put("myMissionsDataDto", myMissionsDataDto);
            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());
        }

        /* 패스 업적 : 미션 데이터 변경점 적용*/
        if(changedEternalPassMissionsData) {
            jsonMyEternalPassMissionData = JsonStringHerlper.WriteValueAsStringFromData(myEternalPassMissionDataDto);
            myEternalPassMissionsData.ResetSaveDataValue(jsonMyEternalPassMissionData);
            map.put("exchange_myEternalPassMissionsData", true);
            map.put("myEternalPassMissionsDataDto", myEternalPassMissionDataDto);
            map.put("myEternalPassLastDailyMissionClearTime", myEternalPassMissionsData.getLastDailyMissionClearTime());
            map.put("myEternalPassLastWeeklyMissionClearTime", myEternalPassMissionsData.getLastWeeklyMissionClearTime());
        }
        return map;
    }

    private boolean receiveItem(Long userId, Mail mailInfo, MailGettingItemsResponseDto mailGettingItemsResponseDto,  MissionsDataDto myMissionsDataDto) {

        List<BelongingInventory> belongingInventoryList = null;
        List<ItemType> itemTypeList = null;
        ItemType spendAbleItemType = null;
        ItemType materialItemType = null;
        User user = null;
        MyGiftInventory myGiftInventory = null;
        List<MyCharacters> myCharactersList = null;
        List<HeroEquipmentInventory> heroEquipmentInventoryList = null;

        List<BelongingCharacterPieceTable> orignalBelongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
        List<BelongingCharacterPieceTable> copyBelongingCharacterPieceTableList = new ArrayList<>();
        for(BelongingCharacterPieceTable characterPieceTable : orignalBelongingCharacterPieceTableList) {
            if(characterPieceTable.getCode().equals("characterPieceAll"))
                continue;
            copyBelongingCharacterPieceTableList.add(characterPieceTable);
        }

        boolean changedMissionsData = false;

        String[] gettingItemsArray = mailInfo.getGettingItems().split(",");
        String[] itemsCountArray = mailInfo.getGettingItemCounts().split(",");
        int gettingItemsCount = gettingItemsArray.length;
        for(int i = 0; i < gettingItemsCount; i++) {
            String gettingItemCode = gettingItemsArray[i];
            int gettingCount = Integer.parseInt(itemsCountArray[i]);

            //피로도 50 회복 물약, 즉시 제작권, 차원석, 강화석, 재련석, 링크웨폰키, 코스튬 무료 티켓
            if(gettingItemCode.equals("recovery_fatigability") || gettingItemCode.equals("ticket_direct_production_equipment")
                    || gettingItemCode.equals("dimensionStone") || gettingItemCode.contains("enchant") || gettingItemCode.contains("resmelt")
                    || gettingItemCode.equals("linkweapon_bronzeKey") || gettingItemCode.equals("linkweapon_silverKey")
                    || gettingItemCode.equals("linkweapon_goldKey") || gettingItemCode.equals("costume_ticket")) {
                if(belongingInventoryList == null)
                    belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
                if(itemTypeList == null)
                    itemTypeList = itemTypeRepository.findAll();
                if(spendAbleItemType == null)
                    spendAbleItemType = itemTypeList.stream()
                            .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                            .findAny()
                            .orElse(null);
                List<BelongingInventoryDto> receivedSpendableItemList = mailGettingItemsResponseDto.getChangedBelongingInventoryList();
                BelongingInventoryDto belongingInventoryDto = ApplySomeReward.ApplySpendableItem(userId, gettingItemCode, gettingCount, belongingInventoryRepository, belongingInventoryList, spendableItemInfoTableList, spendAbleItemType, mailInfo.getId()+"번 메일 확인", loggingService, errorLoggingService);
                int itemId = belongingInventoryDto.getItemId();
                Long itemTypeId = belongingInventoryDto.getItemType().getId();
                BelongingInventoryDto findBelongingInventoryDto = receivedSpendableItemList.stream().filter(a -> a.getItemId()==itemId && a.getItemType().getId().equals(itemTypeId))
                        .findAny()
                        .orElse(null);
                if(findBelongingInventoryDto == null) {
                    receivedSpendableItemList.add(belongingInventoryDto);
                }
                else {
                    findBelongingInventoryDto.AddCount(gettingCount);
                }
            }
            //골드
            else if(gettingItemCode.equals("gold")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousCount = user.getGold();
                user.AddGold(gettingCount);
                currencyLogDto.setCurrencyLogDto(mailInfo.getId()+"번 메일 확인", "골드", previousCount, gettingCount, user.getGold());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                mailGettingItemsResponseDto.AddGettingGold(gettingCount);
            }
            //다이아
            else if(gettingItemCode.equals("diamond")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousCount = user.getDiamond();
                user.AddDiamond(gettingCount);
                currencyLogDto.setCurrencyLogDto(mailInfo.getId()+"번 메일 확인", "다이아", previousCount, gettingCount, user.getDiamond());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                mailGettingItemsResponseDto.AddGettingDiamond(gettingCount);
            }
            //링크 포인트
            else if(gettingItemCode.equals("linkPoint")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousCount = user.getLinkforcePoint();
                user.AddLinkforcePoint(gettingCount);
                currencyLogDto.setCurrencyLogDto(mailInfo.getId()+"번 메일 확인", "링크포인트", previousCount, gettingCount, user.getLinkforcePoint());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                mailGettingItemsResponseDto.AddGettingLinkPoint(gettingCount);
            }
            //아레나 코인
            else if(gettingItemCode.equals("arenaCoin")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousCount = user.getArenaCoin();
                user.AddArenaCoin(gettingCount);
                currencyLogDto.setCurrencyLogDto(mailInfo.getId()+"번 메일 확인", "아레나 코인", previousCount, gettingCount, user.getArenaCoin());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                mailGettingItemsResponseDto.AddGettingArenaCoin(gettingCount);
            }
            //아레나 티켓
            else if(gettingItemCode.equals("arenaTicket")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousCount = user.getArenaTicket();
                user.AddArenaTicket(gettingCount);
                currencyLogDto.setCurrencyLogDto(mailInfo.getId()+"번 메일 확인", "아레나 티켓", previousCount, gettingCount, user.getArenaTicket());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                mailGettingItemsResponseDto.AddGettingArenaTicket(gettingCount);
            }
            else if(gettingItemCode.equals("lowDragonScale")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousCount = user.getLowDragonScale();
                user.AddLowDragonScale(gettingCount);
                currencyLogDto.setCurrencyLogDto(mailInfo.getId()+"번 메일 확인", "용의 비늘(전설)", previousCount, gettingCount, user.getLowDragonScale());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                mailGettingItemsResponseDto.AddGettingLowDragonScale(gettingCount);
            }
            else if(gettingItemCode.equals("middleDragonScale")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousCount = user.getMiddleDragonScale();
                user.AddMiddleDragonScale(gettingCount);
                currencyLogDto.setCurrencyLogDto(mailInfo.getId()+"번 메일 확인", "용의 비늘(신성)", previousCount, gettingCount, user.getMiddleDragonScale());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                mailGettingItemsResponseDto.AddGettingMiddleDragonScale(gettingCount);
            }
            else if(gettingItemCode.equals("highDragonScale")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousCount = user.getHighDragonScale();
                user.AddHighDragonScale(gettingCount);
                currencyLogDto.setCurrencyLogDto(mailInfo.getId()+"번 메일 확인", "용의 비늘(고대)", previousCount, gettingCount, user.getHighDragonScale());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                mailGettingItemsResponseDto.AddGettingHighDragonScale(gettingCount);
            }
            else if(gettingItemCode.equals("darkobe")) {
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                int previousCount = user.getDarkObe();
                user.AddDarkObe(gettingCount);
                currencyLogDto.setCurrencyLogDto(mailInfo.getId()+"번 메일 확인", "어둠의 보주", previousCount, gettingCount, user.getDarkObe());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                mailGettingItemsResponseDto.AddGettingDarkObe(gettingCount);
            }
            /*3종, 5종, 8종 재료 상자*/
            else if(gettingItemCode.equals("reward_material_low") || gettingItemCode.equals("reward_material_middle") || gettingItemCode.equals("reward_material_high")) {
                int kindCount = 0;
                if (gettingItemCode.contains("low")) {
                    //3종
                    kindCount = 3;
                } else if (gettingItemCode.contains("middle")) {
                    //5종
                    kindCount = 5;
                }
                else if (gettingItemCode.contains("high")) {
                    //8종
                    kindCount = 8;
                }
                List<BelongingInventoryDto> receivedSpendableItemList = mailGettingItemsResponseDto.getChangedBelongingInventoryList();
                if(belongingInventoryList == null)
                    belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList = gameDataTableService.EquipmentMaterialInfoTableList();
                if(itemTypeList == null)
                    itemTypeList = itemTypeRepository.findAll();
                if(materialItemType == null) {
                    materialItemType = itemTypeList.stream()
                            .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Material)
                            .findAny()
                            .orElse(null);
                }
                List<EquipmentMaterialInfoTable> copyEquipmentMaterialInfoTableList = new ArrayList<>();
                copyEquipmentMaterialInfoTableList.addAll(equipmentMaterialInfoTableList);
                int addIndex = 0;
                while (addIndex < kindCount) {
                    int selectedRandomIndex = (int) MathHelper.Range(0, copyEquipmentMaterialInfoTableList.size());
                    EquipmentMaterialInfoTable equipmentMaterialInfoTable = copyEquipmentMaterialInfoTableList.get(selectedRandomIndex);
                    BelongingInventoryDto belongingInventoryDto = ApplySomeReward.AddEquipmentMaterialItem(equipmentMaterialInfoTable.getCode(), gettingCount, belongingInventoryRepository, itemTypeList, belongingInventoryList, equipmentMaterialInfoTableList, materialItemType, mailInfo.getId()+"번 메일 확인", userId, loggingService, errorLoggingService);
                    int itemId = belongingInventoryDto.getItemId();
                    Long itemTypeId = belongingInventoryDto.getItemType().getId();
                    BelongingInventoryDto findBelongingInventoryDto = receivedSpendableItemList.stream().filter(a -> a.getItemId()==itemId && a.getItemType().getId().equals(itemTypeId))
                            .findAny()
                            .orElse(null);
                    if(findBelongingInventoryDto == null) {
                        receivedSpendableItemList.add(belongingInventoryDto);
                    }
                    else {
                        findBelongingInventoryDto.AddCount(gettingCount);
                    }
                    copyEquipmentMaterialInfoTableList.remove(selectedRandomIndex);
                    addIndex++;
                }
            }
            /*특정 재료*/
            else if(gettingItemCode.contains("material")){
                List<BelongingInventoryDto> receivedSpendableItemList = mailGettingItemsResponseDto.getChangedBelongingInventoryList();
                if(belongingInventoryList == null)
                    belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList = gameDataTableService.EquipmentMaterialInfoTableList();
                if(itemTypeList == null)
                    itemTypeList = itemTypeRepository.findAll();
                if(materialItemType == null) {
                    materialItemType = itemTypeList.stream()
                            .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Material)
                            .findAny()
                            .orElse(null);
                }
                BelongingInventoryDto belongingInventoryDto = ApplySomeReward.AddEquipmentMaterialItem(gettingItemCode, gettingCount, belongingInventoryRepository, itemTypeList, belongingInventoryList, equipmentMaterialInfoTableList, materialItemType, mailInfo.getId()+"번 메일 확인", userId, loggingService, errorLoggingService);
                int itemId = belongingInventoryDto.getItemId();
                Long itemTypeId = belongingInventoryDto.getItemType().getId();
                BelongingInventoryDto findBelongingInventoryDto = receivedSpendableItemList.stream().filter(a -> a.getItemId()==itemId && a.getItemType().getId().equals(itemTypeId))
                        .findAny()
                        .orElse(null);
                if(findBelongingInventoryDto == null) {
                    receivedSpendableItemList.add(belongingInventoryDto);
                }
                else {
                    findBelongingInventoryDto.AddCount(gettingCount);
                }
            }
            /*모든 선물중 하나*/
            else if(gettingItemCode.equals("giftAll")) {
                List<GiftTable> giftTableList = gameDataTableService.GiftsTableList();
                List<GiftTable> copyGiftTableList = new ArrayList<>(giftTableList);
                copyGiftTableList.remove(25);
                int randIndex = (int) MathHelper.Range(0, copyGiftTableList.size());
                GiftTable giftTable = copyGiftTableList.get(randIndex);
                if(myGiftInventory == null) {
                    myGiftInventory = myGiftInventoryRepository.findByUseridUser(userId)
                            .orElse(null);
                    if(myGiftInventory == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyGiftInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: MyGiftInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                List<GiftItemDtosList.GiftItemDto> changedMyGiftInventoryList = mailGettingItemsResponseDto.getChangedMyGiftInventoryList();
                String inventoryInfosString = myGiftInventory.getInventoryInfos();
                GiftItemDtosList giftItemInventorys = JsonStringHerlper.ReadValueFromJson(inventoryInfosString, GiftItemDtosList.class);

                GiftItemDtosList.GiftItemDto inventoryItemDto = giftItemInventorys.giftItemDtoList.stream()
                        .filter(a -> a.code.equals(giftTable.getCode()))
                        .findAny()
                        .orElse(null);
                if(inventoryItemDto == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find GiftTable", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail -> Cause: Can't Find GiftTable", ResponseErrorCode.NOT_FIND_DATA);
                }
                GiftLogDto giftLogDto = new GiftLogDto();
                giftLogDto.setPreviousValue(inventoryItemDto.count);
                inventoryItemDto.AddItem(gettingCount ,giftTable.getStackLimit());
                giftLogDto.setGiftLogDto(mailInfo.getId()+"번 메일 확인", inventoryItemDto.code,gettingCount, inventoryItemDto.count);
                String log = JsonStringHerlper.WriteValueAsStringFromData(giftLogDto);
                loggingService.setLogging(userId, 4, log);
                inventoryInfosString = JsonStringHerlper.WriteValueAsStringFromData(giftItemInventorys);
                myGiftInventory.ResetInventoryInfos(inventoryInfosString);

                GiftItemDtosList.GiftItemDto responseItemDto = new GiftItemDtosList.GiftItemDto();
                responseItemDto.code = inventoryItemDto.code;
                responseItemDto.count = gettingCount;
                changedMyGiftInventoryList.add(responseItemDto);
            }
            /*특정 선물*/
            else if(gettingItemCode.contains("gift_")) {
                List<GiftTable> giftTableList = gameDataTableService.GiftsTableList();
                GiftTable giftTable = giftTableList.stream().filter(a -> a.getCode().equals(gettingItemCode)).findAny().orElse(null);
                if(giftTable == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: GiftTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: GiftTable not find.", ResponseErrorCode.NOT_FIND_DATA);
                }
                if(myGiftInventory == null) {
                    myGiftInventory = myGiftInventoryRepository.findByUseridUser(userId)
                            .orElse(null);
                    if(myGiftInventory == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyGiftInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: MyGiftInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                List<GiftItemDtosList.GiftItemDto> changedMyGiftInventoryList = mailGettingItemsResponseDto.getChangedMyGiftInventoryList();
                String inventoryInfosString = myGiftInventory.getInventoryInfos();
                GiftItemDtosList giftItemInventorys = JsonStringHerlper.ReadValueFromJson(inventoryInfosString, GiftItemDtosList.class);

                GiftItemDtosList.GiftItemDto inventoryItemDto = giftItemInventorys.giftItemDtoList.stream()
                        .filter(a -> a.code.equals(giftTable.getCode()))
                        .findAny()
                        .orElse(null);
                if(inventoryItemDto == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find GiftTable", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail -> Cause: Can't Find GiftTable", ResponseErrorCode.NOT_FIND_DATA);
                }
                GiftLogDto giftLogDto = new GiftLogDto();
                giftLogDto.setPreviousValue(inventoryItemDto.count);

                inventoryItemDto.AddItem(gettingCount ,giftTable.getStackLimit());
                giftLogDto.setGiftLogDto(mailInfo.getId()+"번 메일 확인", inventoryItemDto.code,gettingCount, inventoryItemDto.count);
                String log = JsonStringHerlper.WriteValueAsStringFromData(giftLogDto);
                loggingService.setLogging(userId, 4, log);
                inventoryInfosString = JsonStringHerlper.WriteValueAsStringFromData(giftItemInventorys);
                myGiftInventory.ResetInventoryInfos(inventoryInfosString);

                GiftItemDtosList.GiftItemDto responseItemDto = new GiftItemDtosList.GiftItemDto();
                responseItemDto.code = inventoryItemDto.code;
                responseItemDto.count = gettingCount;
                changedMyGiftInventoryList.add(responseItemDto);
            }
            /*모든 케릭터 조각중 하나*/
            else if(gettingItemCode.equals("characterPieceAll")) {
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                //List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
                int randIndex = (int) MathHelper.Range(0, copyBelongingCharacterPieceTableList.size());
                BelongingCharacterPieceTable selectedCharacterPiece = copyBelongingCharacterPieceTableList.get(randIndex);
                List<BelongingInventoryDto> changedCharacterPieceList = mailGettingItemsResponseDto.getChangedBelongingInventoryList();
                if(itemTypeList == null)
                    itemTypeList = itemTypeRepository.findAll();
                ItemType belongingCharacterPieceItemType = itemTypeList.stream()
                        .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece)
                        .findAny()
                        .orElse(null);
                if(belongingInventoryList == null)
                    belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                BelongingInventory myCharacterPieceItem = belongingInventoryList.stream()
                        .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece && a.getItemId() == selectedCharacterPiece.getId())
                        .findAny()
                        .orElse(null);

                if(myCharacterPieceItem == null) {
                    belongingInventoryLogDto.setPreviousValue(0);
                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.setUseridUser(userId);
                    belongingInventoryDto.setItemId(selectedCharacterPiece.getId());
                    belongingInventoryDto.setCount(gettingCount);
                    belongingInventoryDto.setItemType(belongingCharacterPieceItemType);
                    myCharacterPieceItem = belongingInventoryDto.ToEntity();
                    myCharacterPieceItem = belongingInventoryRepository.save(myCharacterPieceItem);
                    belongingInventoryLogDto.setBelongingInventoryLogDto(mailInfo.getId()+"번 메일 확인", myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
                    String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                    loggingService.setLogging(userId, 3, log);
                    belongingInventoryList.add(myCharacterPieceItem);
                    belongingInventoryDto.setId(myCharacterPieceItem.getId());
                    changedCharacterPieceList.add(belongingInventoryDto);
                }
                else {
                    belongingInventoryLogDto.setPreviousValue(myCharacterPieceItem.getCount());
                    myCharacterPieceItem.AddItem(gettingCount, selectedCharacterPiece.getStackLimit());
                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.setUseridUser(userId);
                    belongingInventoryDto.setItemId(selectedCharacterPiece.getId());
                    belongingInventoryDto.setCount(gettingCount);
                    belongingInventoryDto.setItemType(belongingCharacterPieceItemType);
                    belongingInventoryLogDto.setBelongingInventoryLogDto(mailInfo.getId()+"번 메일 확인", myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
                    String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                    loggingService.setLogging(userId, 3, log);
                    belongingInventoryDto.setId(myCharacterPieceItem.getId());
                    changedCharacterPieceList.add(belongingInventoryDto);
                }
            }
            /*특정 케릭터 조각*/
            else if(gettingItemCode.contains("characterPiece")) {
                if(itemTypeList == null)
                    itemTypeList = itemTypeRepository.findAll();
                ItemType belongingCharacterPieceItemType = itemTypeList.stream()
                        .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece)
                        .findAny()
                        .orElse(null);

                if(belongingInventoryList == null)
                    belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
                if(itemTypeList == null)
                    itemTypeList = itemTypeRepository.findAll();
                List<BelongingInventoryDto> receivedSpendableItemList = mailGettingItemsResponseDto.getChangedBelongingInventoryList();
                BelongingInventoryDto belongingInventoryDto = ApplySomeReward.ApplyCharacterPiece(userId, gettingItemCode, gettingCount, belongingInventoryRepository, belongingInventoryList, belongingCharacterPieceTableList, belongingCharacterPieceItemType, mailInfo.getId()+"번 메일 확인", loggingService, errorLoggingService);
                int itemId = belongingInventoryDto.getItemId();
                Long itemTypeId = belongingInventoryDto.getItemType().getId();
                BelongingInventoryDto findBelongingInventoryDto = receivedSpendableItemList.stream().filter(a -> a.getItemId()==itemId && a.getItemType().getId().equals(itemTypeId))
                        .findAny()
                        .orElse(null);
                if(findBelongingInventoryDto == null) {
                    receivedSpendableItemList.add(belongingInventoryDto);
                }
                else {
                    findBelongingInventoryDto.AddCount(gettingCount);
                }
            }
            /*모든 장비 중 하나*/
            else if(gettingItemCode.equals("equipmentAll")){
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                //장비
                if(heroEquipmentInventoryList == null)
                    heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());

                if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot())
                    throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
                //등급 확률	영웅 70%	 전설 20%	신성 9%	고대 1%
                //품질	D	C	B	A	S	SS	SSS
                //확률	15%	25%	45%	9%	5%	1%	0%
                List<Double> gradeProbabilityList = new ArrayList<>();
                gradeProbabilityList.add(70D);
                gradeProbabilityList.add(20D);
                gradeProbabilityList.add(9D);
                gradeProbabilityList.add(1D);
                String selectedGrade = "";
                int selectedIndex = MathHelper.RandomIndexWidthProbability(gradeProbabilityList);
                switch (selectedIndex) {
                    case 0:
                        selectedGrade = "Hero";
                        break;
                    case 1:
                        selectedGrade = "Legend";
                        break;
                    case 2:
                        selectedGrade = "Divine";
                        break;
                    case 3:
                        selectedGrade = "Ancient";
                        break;
                }

                List<Double> classProbabilityList = new ArrayList<>();
                classProbabilityList.add(15D);
                classProbabilityList.add(25D);
                classProbabilityList.add(45D);
                classProbabilityList.add(9D);
                classProbabilityList.add(5D);
                classProbabilityList.add(1D);
                classProbabilityList.add(0D);
                String selectedClass = "";
                selectedIndex = MathHelper.RandomIndexWidthProbability(classProbabilityList);
                switch (selectedIndex) {
                    case 0:
                        selectedClass = "D";
                        break;
                    case 1:
                        selectedClass = "C";
                        break;
                    case 2:
                        selectedClass = "B";
                        break;
                    case 3:
                        selectedClass = "A";
                        break;
                    case 4:
                        selectedClass = "S";
                        break;
                    case 5:
                        selectedClass = "SS";
                        break;
                    case 6:
                        selectedClass = "SSS";
                        break;
                }
                List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
                HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
                int classValue = (int) EquipmentCalculate.GetClassValueFromClassCategory(selectedClass, classValues);
                List<HeroEquipmentsTable> probabilityList = EquipmentCalculate.GetProbabilityList(heroEquipmentsTableList, EquipmentItemCategory.ALL, selectedGrade);
                int randValue = (int)MathHelper.Range(0, probabilityList.size());
                HeroEquipmentsTable selectEquipment = probabilityList.get(randValue);
                List<EquipmentOptionsInfoTable> optionsInfoTableList = gameDataTableService.OptionsInfoTableList();
                HeroEquipmentInventory generatedItem = EquipmentCalculate.CreateEquipment(user.getId(), selectEquipment, selectedClass, classValue, optionsInfoTableList);
                generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
                HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
                heroEquipmentInventoryDto.InitFromDbData(generatedItem);
                List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = mailGettingItemsResponseDto.getChangedHeroEquipmentInventoryList();
                changedHeroEquipmentInventoryList.add(heroEquipmentInventoryDto);
                EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                equipmentLogDto.setEquipmentLogDto(mailInfo.getId()+"번 메일 확인", generatedItem.getId(), "추가", heroEquipmentInventoryDto);
                String log = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                loggingService.setLogging(userId, 2, log);

                /* 업적 : 장비 획득 (특정 품질) 미션 체크*/
                changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(), generatedItem.getItemClass(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
                changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), selectedGrade, gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

            }
            /*특정 등급, 특정 클래스, 특정 종류의 장비중 하나*/
            else if(gettingItemCode.contains("equipment")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                if(heroEquipmentInventoryList == null)
                    heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());

                if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
                }

                List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
                HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
                List<EquipmentOptionsInfoTable> optionsInfoTableList = gameDataTableService.OptionsInfoTableList();
                HeroEquipmentInventoryDto heroEquipmentInventoryDto = ApplySomeReward.AddEquipmentItem(user, gettingItemCode, heroEquipmentInventoryList, heroEquipmentInventoryRepository, heroEquipmentsTableList, classValues, optionsInfoTableList, mailInfo.getId()+"번 메일 확인", loggingService, errorLoggingService);
                List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = mailGettingItemsResponseDto.getChangedHeroEquipmentInventoryList();
                changedHeroEquipmentInventoryList.add(heroEquipmentInventoryDto);

                HeroEquipmentsTable selectedEquipmentItemTable = heroEquipmentsTableList.stream().filter(a -> a.getId() == heroEquipmentInventoryDto.getItem_Id()).findAny().orElse(null);

                /* 업적 : 장비 획득 (특정 품질) 미션 체크*/
                changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(), heroEquipmentInventoryDto.getItemClass(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
                changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), selectedEquipmentItemTable.getGrade(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

            }
            /*인장 중 하나*/
            else if(gettingItemCode.equals("stampAll")){
                if(user == null) {
                    user = userRepository.findById(userId).orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }

                if(heroEquipmentInventoryList == null)
                    heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());

                if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
                }

                List<PassiveItemTable> passiveItemTables = gameDataTableService.PassiveItemTableList();
                List<PassiveItemTable> copyPassiveItemTables = new ArrayList<>();
                copyPassiveItemTables.addAll(passiveItemTables);
                List<PassiveItemTable> deleteList = new ArrayList<>();
                boolean deleted = false;
                for(PassiveItemTable passiveItemTable : copyPassiveItemTables){
                    String code = passiveItemTable.getCode();
                    if(code.equals("passiveItem_00_10")) {
                        deleteList.add(passiveItemTable);
                        deleted = true;
                        break;
                    }
                }
                if(deleted)
                    copyPassiveItemTables.removeAll(deleteList);

                int selectedIndex  = (int)MathHelper.Range(0, copyPassiveItemTables.size());
                PassiveItemTable selectedPassiveItem = copyPassiveItemTables.get(selectedIndex);
                List<Double> classProbabilityList = new ArrayList<>();
                classProbabilityList.add(15D);
                classProbabilityList.add(25D);
                classProbabilityList.add(45D);
                classProbabilityList.add(9D);
                classProbabilityList.add(5D);
                classProbabilityList.add(1D);
                classProbabilityList.add(0D);
                String selectedClass = "";
                selectedIndex = MathHelper.RandomIndexWidthProbability(classProbabilityList);
                switch (selectedIndex) {
                    case 0:
                        selectedClass = "D";
                        break;
                    case 1:
                        selectedClass = "C";
                        break;
                    case 2:
                        selectedClass = "B";
                        break;
                    case 3:
                        selectedClass = "A";
                        break;
                    case 4:
                        selectedClass = "S";
                        break;
                    case 5:
                        selectedClass = "SS";
                        break;
                    case 6:
                        selectedClass = "SSS";
                        break;
                }
                HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
                int classValue = (int) EquipmentCalculate.GetClassValueFromClassCategory(selectedClass, classValues);

                HeroEquipmentInventoryDto dto = new HeroEquipmentInventoryDto();
                dto.setUseridUser(userId);
                dto.setItem_Id(selectedPassiveItem.getId());
                dto.setItemClassValue(classValue);
                dto.setDecideDefaultAbilityValue(0);
                dto.setDecideSecondAbilityValue(0);
                dto.setLevel(1);
                dto.setMaxLevel(1);
                dto.setExp(0);
                dto.setNextExp(0);
                dto.setItemClass(selectedClass);

                HeroEquipmentInventory generatedItem = dto.ToEntity();
                generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
                HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
                heroEquipmentInventoryDto.InitFromDbData(generatedItem);
                EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                equipmentLogDto.setEquipmentLogDto(mailInfo.getId()+"번 메일 확인", generatedItem.getId(), "추가", heroEquipmentInventoryDto);
                String log = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                loggingService.setLogging(userId, 2, log);

                List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = mailGettingItemsResponseDto.getChangedHeroEquipmentInventoryList();
                changedHeroEquipmentInventoryList.add(heroEquipmentInventoryDto);
            }
            /*특정 품질의 인장*/
            else if(gettingItemCode.contains("stamp")){

                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }
                
                if(heroEquipmentInventoryList == null)
                    heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());

                if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
                }

                String itemClass = "D";
                if(gettingItemCode.contains("ClassD")) {
                    itemClass = "D";
                }
                else if(gettingItemCode.contains("ClassC")){
                    itemClass = "C";
                }
                else if(gettingItemCode.contains("ClassB")){
                    itemClass = "B";
                }
                else if(gettingItemCode.contains("ClassA")){
                    itemClass = "A";
                }
                else if(gettingItemCode.contains("ClassSSS")){
                    itemClass = "SSS";
                }
                else if(gettingItemCode.contains("ClassSS")){
                    itemClass = "SS";
                }
                else if(gettingItemCode.contains("ClassS")){
                    itemClass = "S";
                }

                List<PassiveItemTable> passiveItemTables = gameDataTableService.PassiveItemTableList();
                List<PassiveItemTable> copyPassiveItemTables = new ArrayList<>();
                copyPassiveItemTables.addAll(passiveItemTables);
                List<PassiveItemTable> deleteList = new ArrayList<>();
                boolean deleted = false;
                for(PassiveItemTable passiveItemTable : copyPassiveItemTables){
                    String code = passiveItemTable.getCode();
                    if(code.equals("passiveItem_00_10")) {
                        deleteList.add(passiveItemTable);
                        deleted = true;
                        break;
                    }
                }
                if(deleted)
                    copyPassiveItemTables.removeAll(deleteList);

                int selectedIndex  = (int)MathHelper.Range(0, copyPassiveItemTables.size());
                PassiveItemTable selectedPassiveItem = copyPassiveItemTables.get(selectedIndex);

                HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
                int classValue = (int) EquipmentCalculate.GetClassValueFromClassCategory(itemClass, classValues);

                HeroEquipmentInventoryDto dto = new HeroEquipmentInventoryDto();
                dto.setUseridUser(userId);
                dto.setItem_Id(selectedPassiveItem.getId());
                dto.setItemClassValue(classValue);
                dto.setDecideDefaultAbilityValue(0);
                dto.setDecideSecondAbilityValue(0);
                dto.setLevel(1);
                dto.setMaxLevel(1);
                dto.setExp(0);
                dto.setNextExp(0);
                dto.setItemClass(itemClass);

                HeroEquipmentInventory generatedItem = dto.ToEntity();
                generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
                HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
                heroEquipmentInventoryDto.InitFromDbData(generatedItem);
                EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                equipmentLogDto.setEquipmentLogDto(mailInfo.getId()+"번 메일 확인", generatedItem.getId(), "추가", heroEquipmentInventoryDto);
                String log = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                loggingService.setLogging(userId, 2, log);

                List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = mailGettingItemsResponseDto.getChangedHeroEquipmentInventoryList();
                changedHeroEquipmentInventoryList.add(heroEquipmentInventoryDto);
            }
            /*특정 인장 중 하나*/
            else if(gettingItemCode.contains("passiveItem")) {
                if(user == null) {
                    user = userRepository.findById(userId)
                            .orElse(null);
                    if(user == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                }

                if(heroEquipmentInventoryList == null)
                    heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());

                if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
                }

                List<PassiveItemTable> passiveItemTables = gameDataTableService.PassiveItemTableList();

                int selectedIndex  = (int)MathHelper.Range(0, passiveItemTables.size());
                PassiveItemTable selectedPassiveItem = passiveItemTables.stream().filter(a -> a.getCode().equals(gettingItemCode)).findAny().orElse(null);
                if(selectedPassiveItem == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find PassiveItemTable", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail -> Cause: Can't Find PassiveItemTable", ResponseErrorCode.NOT_FIND_DATA);
                }
                List<Double> classProbabilityList = new ArrayList<>();
                classProbabilityList.add(15D);
                classProbabilityList.add(25D);
                classProbabilityList.add(45D);
                classProbabilityList.add(9D);
                classProbabilityList.add(5D);
                classProbabilityList.add(1D);
                classProbabilityList.add(0D);
                String selectedClass = "";
                selectedIndex = MathHelper.RandomIndexWidthProbability(classProbabilityList);
                switch (selectedIndex) {
                    case 0:
                        selectedClass = "D";
                        break;
                    case 1:
                        selectedClass = "C";
                        break;
                    case 2:
                        selectedClass = "B";
                        break;
                    case 3:
                        selectedClass = "A";
                        break;
                    case 4:
                        selectedClass = "S";
                        break;
                    case 5:
                        selectedClass = "SS";
                        break;
                    case 6:
                        selectedClass = "SSS";
                        break;
                }
                HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
                int classValue = (int) EquipmentCalculate.GetClassValueFromClassCategory(selectedClass, classValues);

                HeroEquipmentInventoryDto dto = new HeroEquipmentInventoryDto();
                dto.setUseridUser(userId);
                dto.setItem_Id(selectedPassiveItem.getId());
                dto.setItemClassValue(classValue);
                dto.setDecideDefaultAbilityValue(0);
                dto.setDecideSecondAbilityValue(0);
                dto.setLevel(1);
                dto.setMaxLevel(1);
                dto.setExp(0);
                dto.setNextExp(0);
                dto.setItemClass(selectedClass);

                HeroEquipmentInventory generatedItem = dto.ToEntity();
                generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
                HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
                heroEquipmentInventoryDto.InitFromDbData(generatedItem);
                EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                equipmentLogDto.setEquipmentLogDto(mailInfo.getId()+"번 메일 확인", generatedItem.getId(), "추가", heroEquipmentInventoryDto);
                String log = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                loggingService.setLogging(userId, 2, log);

                List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = mailGettingItemsResponseDto.getChangedHeroEquipmentInventoryList();
                changedHeroEquipmentInventoryList.add(heroEquipmentInventoryDto);
            }
        }
        return changedMissionsData;
    }

    private boolean optionReceive(Long userId, Mail mailInfo, String selectedCode, MailGettingItemsResponseDto mailGettingItemsResponseDto, MissionsDataDto myMissionsDataDto, Map<String, Object> map) {
        String[] gettingCharacters = mailInfo.getGettingItems().split(",");
        String[] selectedCodeSplit = selectedCode.split("_");

        boolean changedMissionsData = false;

        if(mailInfo.getMailType() == 2){
            String check = Arrays.stream(gettingCharacters).filter(i -> i.equals(selectedCode)).findAny().orElse(null);
            if(check == null){//TODO ErrorCode 찾기 혹은 추가
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.WRONG_SELECTED_CODE.getIntegerValue(), "Fail! -> Cause: Wrong Selected Code.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Wrong Selected Code.", ResponseErrorCode.WRONG_SELECTED_CODE);
            }
            List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
            MyCharacters selectedCharacter = myCharactersList.stream().filter(a -> a.getCodeHerostable().equals(selectedCode))
                    .findAny()
                    .orElse(null);
            if (selectedCharacter == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myCharactersList not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: myCharactersList not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            List<herostable> herostables = gameDataTableService.HerosTableList();
            herostable herostable = herostables.stream()
                    .filter(e -> e.getCode().equals(selectedCharacter.getCodeHerostable()))
                    .findAny()
                    .orElse(null);
            if (herostable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: not find userId.", ResponseErrorCode.NOT_FIND_DATA);
            }

            if (selectedCharacter.isGotcha()) {
                List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);

                List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
                List<CharacterToPieceTable> characterToPieceTableList = gameDataTableService.CharacterToPieceTableList();

                String willFindCharacterCode = selectedCharacter.getCodeHerostable();
                if (herostable.getTier() == 1) {
                    willFindCharacterCode = "characterPiece_cr_common";
                }
                String finalWillFindCharacterCode = willFindCharacterCode;
                BelongingCharacterPieceTable finalCharacterPieceTable = belongingCharacterPieceTableList.stream()
                        .filter(a -> a.getCode().contains(finalWillFindCharacterCode))
                        .findAny()
                        .orElse(null);
                if (finalCharacterPieceTable == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: characterPieceTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: characterPieceTable not find.", ResponseErrorCode.NOT_FIND_DATA);
                }
                CharacterToPieceTable characterToPieceTable = characterToPieceTableList.stream().filter(a -> a.getId() == herostable.getTier()).findAny().orElse(null);
                if (characterToPieceTable == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: characterToPieceTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: characterToPieceTable not find.", ResponseErrorCode.NOT_FIND_DATA);
                }
                List<ItemType> itemTypeList = itemTypeRepository.findAll();
                BelongingInventoryDto belongingInventoryDto = AddSelectedCharacterPiece(userId, characterToPieceTable.getPieceCount(), itemTypeList, finalCharacterPieceTable, belongingInventoryList, "선택권");

                GotchaCharacterResponseDto gotchaCharacterResponseDto = mailGettingItemsResponseDto.getGotchaCharacterResponseDto();
                GotchaCharacterResponseDto.GotchaCharacterInfo gotchaCharacterInfo = new GotchaCharacterResponseDto.GotchaCharacterInfo(selectedCharacter.getCodeHerostable(), belongingInventoryDto, null);
                gotchaCharacterResponseDto.getCharacterInfoList().add(gotchaCharacterInfo);

            } else {
                selectedCharacter.Gotcha();
                GotchaCharacterResponseDto gotchaCharacterResponseDto = mailGettingItemsResponseDto.getGotchaCharacterResponseDto();
                GotchaCharacterResponseDto.GotchaCharacterInfo gotchaCharacterInfo = new GotchaCharacterResponseDto.GotchaCharacterInfo(selectedCharacter.getCodeHerostable(), null, selectedCharacter);
                gotchaCharacterResponseDto.getCharacterInfoList().add(gotchaCharacterInfo);

                /* 업적 : 새로운 동료 획득 미션 체크*/
                changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GOTCHA_NEW_HERO.name(), "empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
            }
        }
        else if(mailInfo.getMailType() == 3) {
            StringMaker.Clear();
            StringMaker.stringBuilder.append(selectedCodeSplit[0]);
            StringMaker.stringBuilder.append("_");
            StringMaker.stringBuilder.append(selectedCodeSplit[1]);
            String characterCode = StringMaker.stringBuilder.toString();
            String check = Arrays.stream(gettingCharacters).filter(i -> i.equals(characterCode)).findAny().orElse(null);
            if(check == null){//TODO ErrorCode 찾기 혹은 추가
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.WRONG_SELECTED_CODE.getIntegerValue(), "Fail! -> Cause: Wrong Selected Code.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Wrong Selected Code.", ResponseErrorCode.WRONG_SELECTED_CODE);
            }
            MyCostumeInventory myCostumeInventory = myCostumeInventoryRepository.findByUseridUser(userId).orElse(null);
            if(myCostumeInventory == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyCostumeInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: MyCostumeInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            String json_costume = myCostumeInventory.getJson_CostumeInventory();
            CostumeDtosList costumeDtosList = JsonStringHerlper.ReadValueFromJson(json_costume, CostumeDtosList.class);
            List<LegionCostumeTable> legionCostumeTableList = gameDataTableService.CostumeTableList();
            LegionCostumeTable legionCostumeTable = legionCostumeTableList.stream().filter(i -> i.getCostumeCode().equals(selectedCode)).findAny().orElse(null);
            if(legionCostumeTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: LegionCostumeTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: LegionCostumeTable not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            CostumeDtosList.CostumeDto costumeDto = costumeDtosList.hasCostumeIdList.stream().filter(i -> i.costumeId == legionCostumeTable.getId()).findAny().orElse(null);
            if(costumeDto == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: HasCostumeIdList not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: HasCostumeIdList not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            costumeDto.Buy();
            json_costume = JsonStringHerlper.WriteValueAsStringFromData(costumeDtosList);
            myCostumeInventory.ResetCostumeInventory(json_costume);
            map.put("exchange_Costume", true);
            map.put("hasCostumeIdList", costumeDtosList.hasCostumeIdList);
        }
        else if(mailInfo.getMailType() == 4) {
//            StringMaker.Clear();
//            StringMaker.stringBuilder.append(selectedCodeSplit[0]);
//            StringMaker.stringBuilder.append("_");
//            StringMaker.stringBuilder.append(selectedCodeSplit[1]);
//            String characterCode = StringMaker.stringBuilder.toString();
//            String check = Arrays.stream(gettingCharacters).filter(i -> i.equals(characterCode)).findAny().orElse(null);
//            if(check == null){//TODO ErrorCode 찾기 혹은 추가
//                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.WRONG_SELECTED_CODE.getIntegerValue(), "Fail! -> Cause: Wrong Selected Code.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
//                throw new MyCustomException("Fail! -> Cause: Wrong Selected Code.", ResponseErrorCode.WRONG_SELECTED_CODE);
//            }
            MyCostumeInventory myCostumeInventory = myCostumeInventoryRepository.findByUseridUser(userId).orElse(null);
            if(myCostumeInventory == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyCostumeInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: MyCostumeInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            String json_costume = myCostumeInventory.getJson_CostumeInventory();
            CostumeDtosList costumeDtosList = JsonStringHerlper.ReadValueFromJson(json_costume, CostumeDtosList.class);
            List<LegionCostumeTable> legionCostumeTableList = gameDataTableService.CostumeTableList();
            LegionCostumeTable legionCostumeTable = legionCostumeTableList.stream().filter(i -> i.getCostumeCode().equals(mailInfo.getGettingItems())).findAny().orElse(null);
            if(legionCostumeTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: LegionCostumeTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: LegionCostumeTable not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            CostumeDtosList.CostumeDto costumeDto = costumeDtosList.hasCostumeIdList.stream().filter(i -> i.costumeId == legionCostumeTable.getId()).findAny().orElse(null);
            if(costumeDto == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: HasCostumeIdList not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: HasCostumeIdList not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            costumeDto.Buy();
            json_costume = JsonStringHerlper.WriteValueAsStringFromData(costumeDtosList);
            myCostumeInventory.ResetCostumeInventory(json_costume);
            map.put("exchange_Costume", true);
            map.put("hasCostumeIdList", costumeDtosList.hasCostumeIdList);
        }
        return changedMissionsData;
    }

    BelongingInventoryDto AddSelectedCharacterPiece(Long userId, int gettingCount, List<ItemType> itemTypeList, BelongingCharacterPieceTable characterPieceTable, List<BelongingInventory> belongingInventoryList, String workingPosition) {
        ItemType belongingCharacterPieceItem = itemTypeList.stream()
                .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece)
                .findAny()
                .orElse(null);

        BelongingCharacterPieceTable finalCharacterPieceTable = characterPieceTable;
        BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
        BelongingInventory myCharacterPieceItem = belongingInventoryList.stream()
                .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece && a.getItemId() == finalCharacterPieceTable.getId())
                .findAny()
                .orElse(null);

        if(myCharacterPieceItem == null) {
            BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
            belongingInventoryLogDto.setPreviousValue(0);
            belongingInventoryDto.setUseridUser(userId);
            belongingInventoryDto.setItemId(characterPieceTable.getId());
            belongingInventoryDto.setCount(gettingCount);
            belongingInventoryDto.setItemType(belongingCharacterPieceItem);
            myCharacterPieceItem = belongingInventoryDto.ToEntity();
            myCharacterPieceItem = belongingInventoryRepository.save(myCharacterPieceItem);
            belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
            String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, belongingLog);
            belongingInventoryDto.setId(myCharacterPieceItem.getId());
            belongingInventoryList.add(myCharacterPieceItem);
        }
        else {
            BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
            belongingInventoryLogDto.setPreviousValue(myCharacterPieceItem.getCount());
            myCharacterPieceItem.AddItem(gettingCount, characterPieceTable.getStackLimit());
            belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
            String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, belongingLog);
            belongingInventoryDto.setId(myCharacterPieceItem.getId());
            belongingInventoryDto.setUseridUser(userId);
            belongingInventoryDto.setItemId(characterPieceTable.getId());
            belongingInventoryDto.setCount(gettingCount);
            belongingInventoryDto.setItemType(belongingCharacterPieceItem);
        }
        return belongingInventoryDto;
    }

    //운영측
    public Map<String, Object> SendMail(MailSendRequestDto mailSendRequestDto, Map<String, Object> map) {

        MailDto mailDto = new MailDto();
        mailDto.setFromId(mailSendRequestDto.fromId);
        mailDto.setToId(mailSendRequestDto.toId);
        mailDto.setTitle(mailSendRequestDto.title);
        mailDto.setContent(mailSendRequestDto.content);
        StringMaker.Clear();
        for(MailSendRequestDto.Item item : mailSendRequestDto.itemList) {
            String itemCode = item.gettingItem;
            if(StringMaker.stringBuilder.length() > 0) {
                StringMaker.stringBuilder.append(",");
            }
            StringMaker.stringBuilder.append(itemCode);
        }
        String gettingItems = StringMaker.stringBuilder.toString();
        mailDto.setGettingItems(gettingItems);

        StringMaker.Clear();
        for(MailSendRequestDto.Item item : mailSendRequestDto.itemList) {
            int itemCount = item.gettingItemCount;
            if(StringMaker.stringBuilder.length() > 0) {
                StringMaker.stringBuilder.append(",");
            }
            StringMaker.stringBuilder.append(itemCount);
        }
        String gettingItemCount = StringMaker.stringBuilder.toString();
        mailDto.setGettingItemCounts(gettingItemCount);
        mailDto.setMailType(mailSendRequestDto.mailType);
        mailDto.setExpireDate(mailSendRequestDto.expireDate);
        LocalDateTime sendDate = LocalDateTime.now();
        if(mailSendRequestDto.getSendDate() != null){
            sendDate = mailSendRequestDto.getSendDate();
        }
        mailDto.setSendDate(sendDate);
        Mail mail = mailDto.ToEntity();
        mail = mailRepository.save(mail);
        mailDto.InitFromDbData(mail);
        map.put("mail", mailDto);
        return map;
    }
}
