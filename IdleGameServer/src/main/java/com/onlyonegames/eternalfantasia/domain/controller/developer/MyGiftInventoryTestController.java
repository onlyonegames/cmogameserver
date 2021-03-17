package com.onlyonegames.eternalfantasia.domain.controller.developer;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.UserBaseDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.developer.GiftResetDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.developer.HeroRestterDto;
import com.onlyonegames.eternalfantasia.domain.service.Companion.developer.MyGiftInventoryServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MyGiftInventoryTestController {
    private final MyGiftInventoryServiceTest myGiftInventoryServiceTest;

    @Autowired
    public MyGiftInventoryTestController(MyGiftInventoryServiceTest myGiftInventoryServiceTest) {
        this.myGiftInventoryServiceTest = myGiftInventoryServiceTest;
    }

    @PostMapping("/api/GiftReset")
    public ResponseDTO<Map<String, Object>> GiftReset(@RequestBody GiftResetDto dto) {
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> response = myGiftInventoryServiceTest.ResetGifts(dto.getId(), dto.getResetCount(), dto.getResetGiftCodeList(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/ResetGoodfeelingAllHero")
    public ResponseDTO<Map<String, Object>> ResetGoodfeelingAllHero(@RequestBody GiftResetDto dto) {
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> response = myGiftInventoryServiceTest.ResetGoodfeelingAllHero(dto.getId(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/ResetGoodfeelingByHero")
    public ResponseDTO<Map<String, Object>> ResetGoodfeelingByHero(@RequestBody GiftResetDto dto) {
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> response = myGiftInventoryServiceTest.ResetGoodfeelingByHero(dto.getId(), dto.getHeroCode(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }
}
