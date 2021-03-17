package com.onlyonegames.eternalfantasia.domain.controller.developer;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.UserBaseDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyAncientDragonExpandSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RestController
@Transactional
public class EventNoSetterTestController {
    private final UserRepository userRepository;

    @PostMapping("/api/Test/SetEventNo")
    public ResponseDTO<Map<String, Object>> SetEventNo(@RequestBody SetEventNoDto dto) {
        Map<String, Object> map = new HashMap<>();

        User user = userRepository.findById(dto.getUserId()).orElseThrow(
                ()->new MyCustomException("Fail! -> Cause: user not find userId.", ResponseErrorCode.NOT_FIND_DATA));
        user.SetEventNoForTest(dto.getNextEventNo());
        user = userRepository.save(user);
        map.put("user", user);
        Map<String, Object> response = map;

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }
}
