package com.onlyonegames.eternalfantasia.domain.controller.managementtool.Arena;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.controller.managementtool.AddUserIdDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ArenaSeasonEndTimeSetRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Managementtool.Arena.ArenaInfoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class ArenaInfoController {
    private final ArenaInfoService arenaInfoService;

    @PostMapping("/api/Test/ArenaInfo")
    public ResponseDTO<Map<String, Object>> GetArenaInfo(@RequestBody AddUserIdDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = arenaInfoService.getMyArenaSeasonInfo(dto.getUserId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true,response);
    }

    @GetMapping("/api/Test/GetArenaSeasonInfo")
    public ResponseDTO<Map<String, Object>> GetArenaSeasonInfo() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = arenaInfoService.getArenaSeasonInfo(map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true,response);
    }

    @PostMapping("/api/Test/SetArenaSeasonEndTime")
    public ResponseDTO<Map<String, Object>> SetArenaSeasonEndTime(@RequestBody ArenaSeasonEndTimeSetRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = arenaInfoService.SetArenaEndTime(dto.getSetTime(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true,response);
    }

    @PostMapping("/api/Test/SetArenaScore")
    public ResponseDTO<Map<String, Object>> SetArenaScore(@RequestBody AddUserIdDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = arenaInfoService.SetNextTier(dto.getUserId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true,response);
    }

    @PostMapping("/api/Test/ArenaWin")
    public ResponseDTO<Map<String, Object>> PlayWin(@RequestBody AddUserIdDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = arenaInfoService.PlayArenaWin(dto.getUserId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true,response);
    }
}
