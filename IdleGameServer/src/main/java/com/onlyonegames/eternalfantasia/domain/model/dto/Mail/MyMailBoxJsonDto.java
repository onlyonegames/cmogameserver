package com.onlyonegames.eternalfantasia.domain.model.dto.Mail;

import com.onlyonegames.eternalfantasia.etc.DefineLimitValue;

import java.util.List;

public class MyMailBoxJsonDto {
    public static class MailBoxInfo {
        public Long mailId;
        public boolean hasRead;/*읽었는지*/
        public boolean received;/*아이템 받았는지*/
    }

    public List<MailBoxInfo> mailBoxInfoList;

    public MailBoxInfo GetMail(Long mailId) {
        MailBoxInfo mailBoxInfo = mailBoxInfoList.stream().filter(a -> a.mailId.equals(mailId)).findAny().orElse(null);
        return  mailBoxInfo;
    }
    //현재 메일을 받을수 있는 상태인지 체크
    public boolean AddAbleMail() {
        //한번에 받을수 있는 최대 매일 갯수 100개
        int notYetReadMailCount = 0;
        for(MailBoxInfo mailBoxInfo : mailBoxInfoList) {
            if(!mailBoxInfo.hasRead) {
                notYetReadMailCount++;
            }
        }

        if(notYetReadMailCount >= DefineLimitValue.LIMIT_MY_MAIL_BOX)
            return false;

        return true;
    }
}
