package com.onlyonegames.eternalfantasia.domain.controller.developer;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.UserBaseDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.developer.GiftResetDto;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RestController
public class MyCharacterTestController {
    private final MyCharacterTestService myCharacterTestService;

    @PostMapping("/api/ResetMyCharacters")
    public ResponseDTO<Map<String, Object>> Clear(@RequestBody UserBaseDto dto) {
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> response = myCharacterTestService.Clear(dto.getId(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/AddCharacterPiece")
    public ResponseDTO<Map<String, Object>> AddCharacterPiece(@RequestBody AddCharacterPieceTestDto dto) {
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> response = myCharacterTestService.AddPiece(dto.userId, dto.heroCode, dto.count, map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }
}
