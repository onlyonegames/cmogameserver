package com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ManagementToolMailDto {
    public List<ManagementToolMails> managementToolMails;
    public ManagementToolMail managementToolMail;

    public static class ManagementToolMails{
        public String title;
        public boolean hasRead;
        public boolean received;
        public LocalDateTime sendDate;
        public Long mailId;

        public void setManagementToolMails(String title, boolean hasRead, boolean received, LocalDateTime sendDate, Long mailId){
            this.title = title;
            this.hasRead = hasRead;
            this.received = received;
            this. sendDate = sendDate;
            this.mailId = mailId;
        }
    }

    public static class ManagementToolMail{
        public String sendUser;
        public LocalDateTime sendDate;
        public List<String> reward;
        public String title;
        public String contents;

        public void setManagementToolMail(String sendUser, LocalDateTime sendDate, List<String> reward, String title, String contents){
            this.sendUser = sendUser;
            this.sendDate = sendDate;
            this.reward = reward;
            this.title = title;
            this.contents = contents;
        }

    }

    public void setManagementToolMailDto(List<ManagementToolMails> managementToolMails, ManagementToolMail managementToolMail){
        this.managementToolMails = managementToolMails;
        this.managementToolMail = managementToolMail;
    }
}
