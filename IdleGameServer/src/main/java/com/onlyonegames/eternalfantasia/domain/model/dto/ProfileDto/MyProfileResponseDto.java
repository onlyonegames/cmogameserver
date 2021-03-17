package com.onlyonegames.eternalfantasia.domain.model.dto.ProfileDto;

import lombok.Data;

import java.util.List;

@Data
public class MyProfileResponseDto {
    public String profileHero;
    public int profileFrame;
    public List<ProfileDataDto.ProfileFrame> frameList;
    public List<ProfileCharacter> characterList;

    public void SetUserProfileResponseDto(String profileHero, int profileFrame, List<ProfileDataDto.ProfileFrame> frameList, List<ProfileCharacter> characterList) {
        this.profileHero = profileHero;
        this.profileFrame = profileFrame;
        this.frameList = frameList;
        this.characterList = characterList;
    }

    public static class ProfileCharacter {
        public String heroCode;
        public boolean gotcha;
        public boolean selected;

        public void SetProfileCharacter(String heroCode, boolean gotcha, boolean selected) {
            this.heroCode = heroCode;
            this.gotcha = gotcha;
            this.selected = selected;
        }

    }
}
