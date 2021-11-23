package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.entity.NoticeInfo;
import com.onlyonegames.eternalfantasia.domain.repository.NoticeInfoRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class NoticeService {
    @Autowired
    private NoticeInfoRepository noticeInfoRepository;

    private NoticeInfo noticeInfo = null;

    public Map<String, Object> GetNotice(Map<String, Object> map) {
        LocalDateTime now = LocalDateTime.now();
        List<NoticeInfo> noticeInfoList = null;
        if (noticeInfo == null) {
            noticeInfo = noticeInfoRepository.findById(1).get();//.findAllByBeginDateBeforeAndExpireDateAfter(now, now);
            if (noticeInfo.getExpireDate().isBefore(now)) {
                noticeInfo.ChangeContents("");
                noticeInfo.changeExpireDate(now.plusYears(1));
            }
        }
        else {
            if (noticeInfo.getExpireDate().isBefore(now)) {
                noticeInfo.ChangeContents("");
                noticeInfo.changeExpireDate(now.plusYears(1));
            }
        }
        map.put("notice", noticeInfo);
        return map;
    }

    public Map<String, Object> SetNotice(String contents, LocalDateTime expireDate, boolean force, Map<String, Object> map) {
        if (noticeInfo == null) {
            noticeInfo = noticeInfoRepository.findById(1).get();
            noticeInfo.ChangeContents(contents);
            noticeInfo.changeExpireDate(expireDate);
            noticeInfoRepository.save(noticeInfo);
        }
        else if (noticeInfo.getContents().equals("")){
            noticeInfo.ChangeContents(contents);
            noticeInfo.changeExpireDate(expireDate);
            noticeInfoRepository.save(noticeInfo);
        }
        else if (force){
            noticeInfo.ChangeContents(contents);
            noticeInfo.changeExpireDate(expireDate);
            noticeInfoRepository.save(noticeInfo);
        }
        else {
            throw new MyCustomException("만료되지 않은 공지가 있습니다. 변경을 원하시면 강제변경 옵션을 추가해 주시기 바랍니다.", ResponseErrorCode.UNDEFINED);
        }
        return map;
    }
}
