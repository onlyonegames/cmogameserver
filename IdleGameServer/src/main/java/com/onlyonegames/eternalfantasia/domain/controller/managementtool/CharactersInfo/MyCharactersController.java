package com.onlyonegames.eternalfantasia.domain.controller.managementtool.CharactersInfo;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.controller.managementtool.AddUserIdDto;
import com.onlyonegames.eternalfantasia.domain.controller.managementtool.NewFindMyCharacterDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.*;
import com.onlyonegames.eternalfantasia.domain.service.Managementtool.CharactersInfo.MyCharactersService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class MyCharactersController {
    private final MyCharactersService myCharactersService;

    @PostMapping("/api/Test/MyCharacters")
    public ResponseDTO<Map<String, Object>> getMyCharacters(@RequestBody AddUserIdDto dto) {
        Map<String, Object> map = new HashMap<>();
        long userId =dto.getUserId();
        Map<String, Object> response = myCharactersService.findUsersHero(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true, response);
    }

    @PostMapping("/api/Test/MyCharacters/SetVisitCompanion")
    public ResponseDTO<Map<String, Object>> resetVisitCompanion(@RequestBody SetVisitCompanionDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = myCharactersService.setVisitCompanion(dto, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true, response);
    }

    @PostMapping("/api/Test/MyCharacter")
    public ResponseDTO<Map<String, Object>> getMyCharacter(@RequestBody NewFindMyCharacterDto dto){
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = myCharactersService.findHeroInfo(dto.getUserId(), dto.getCharacterId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true,response);
    }

    @PostMapping("/api/Test/MyCharacterStatus")
    public ResponseDTO<Map<String, Object>> getMyCharacterStatus(@RequestBody NewFindMyCharacterDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = myCharactersService.characterStatus(dto.getUserId(), dto.getCharacterId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true, response);
    }

    @PostMapping("/api/Test/MyCharacter/SetMyCharacter")
    public ResponseDTO<Map<String, Object>> setMyCharacter(@RequestBody SetCompanionStatusDto dto){
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = myCharactersService.setCompanionInfo(dto, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true,response);
    }

    @PostMapping("/api/Test/MyCharacter/SetCompanionLinkAbilityLv")
    public ResponseDTO<Map<String, Object>> setCompanionLinkAbility(@RequestBody LinkAbilityDto dto){
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = myCharactersService.setCompanionLinkAbility(dto, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true, response);
    }

    @PostMapping("/api/Test/MyCharacter/SetCompanionCostume")
    public ResponseDTO<Map<String, Object>> setCompanionCostume(@RequestBody SetCompanionCostumeDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = myCharactersService.setCompanionCostume(dto, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true, response);
    }

    @PostMapping("/api/Test/MyCharacter/SetLinkWeapon")
    public ResponseDTO<Map<String, Object>> setLinkWeapon(@RequestBody SetLinkWeaponDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = myCharactersService.setLinkWeapon(dto, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true, response);
    }
}
