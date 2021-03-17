package com.onlyonegames.eternalfantasia.domain.controller.managementtool.ChapterInfo;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.controller.managementtool.AddUserIdDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.InfiniteTowerSetRequestArrivedFloorDto;
import com.onlyonegames.eternalfantasia.domain.service.Managementtool.ChapterInfo.MyChapterDataService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class MyChapterDataController {
    private final MyChapterDataService myChapterDataService;

    @PostMapping("/api/Test/MyChapterData")
    public ResponseDTO<Map<String, Object>> GetMyChapterData(@RequestBody AddUserIdDto dto){
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = myChapterDataService.findUserChapterSaveData(dto.getUserId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true,response);
    }

    @PostMapping("/api/Test/ResetOrdealDungeon")
    public ResponseDTO<Map<String, Object>> ResetOrdealDungeon(@RequestBody AddUserIdDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = myChapterDataService.ResetDungeonBonus(dto.getUserId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "",true, response);
    }

    @PostMapping("/api/Test/ResetPlayable")
    public ResponseDTO<Map<String, Object>> ResetPlayable(@RequestBody AddUserIdDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = myChapterDataService.ResetPlayable(dto.getUserId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "",true, response);
    }

    @PostMapping("/api/Test/SetArrivedFloor")
    public ResponseDTO<Map<String, Object>> SetArrivedFloor(@RequestBody InfiniteTowerSetRequestArrivedFloorDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = myChapterDataService.SetInfiniteTower(dto.getUserid(), dto.getSetArrivedFloor(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
