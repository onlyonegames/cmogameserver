package com.onlyonegames.eternalfantasia.domain.controller.developer;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RestController
public class AddLinkPointTestController {
    private final AddLinkPointTestService addLinkPointTestService;
    @PostMapping("/api/Test/AddLinkPoint")
    public ResponseDTO<Map<String, Object>> AddLinkPoint(@RequestBody AddLinkPointTestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response =  addLinkPointTestService.AddLinkPointTest(dto.userId, dto.count, map);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }
}
