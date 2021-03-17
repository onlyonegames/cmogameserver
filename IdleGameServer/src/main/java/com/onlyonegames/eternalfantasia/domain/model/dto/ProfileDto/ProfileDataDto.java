package com.onlyonegames.eternalfantasia.domain.model.dto.ProfileDto;

import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import lombok.Data;

import java.util.List;

@Data
public class ProfileDataDto {
    public String profileHero;
    public int profileFrame;
    public List<ProfileFrame> profileFrameList;

    public static class ProfileFrame {
        public int frameId;
        public String frameName;
        public boolean possession;
        public boolean selected;

        public void SetProfileFrame(int frameId, String frameName, boolean possession, boolean selected) {
            this.frameId = frameId;
            this.frameName = frameName;
            this.possession = possession;
            this.selected = selected;
        }

        public boolean OnOffSelected() { return this.selected = !selected; }
        public boolean GetFrame() { return this.possession = true; }
    }

//    public void changeProfileFrame(String profileFrame, ErrorLoggingService errorLoggingService){
//        ProfileFrame selectedProfile = this.profileFrameList.stream().filter(i -> i.selected).findAny().orElse(null);
//        if(selectedProfile == null) {
//            errorLoggingService.SetErrorLog();
//        }
//    }
}
