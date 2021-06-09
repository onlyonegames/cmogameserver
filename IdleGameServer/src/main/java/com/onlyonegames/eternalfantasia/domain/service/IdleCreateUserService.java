package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.model.dto.MyStatusInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.UserBaseDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyStatusInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.MyStatusInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
@AllArgsConstructor
public class IdleCreateUserService {
    private final UserRepository userRepository;
    private final MyStatusInfoRepository myStatusInfoRepository;
    private final GameDataTableService gameDataTableService;

    public Map<String, Object> createUser(UserBaseDto userBaseDto, Map<String, Object> map) {
        User previousUser = userBaseDto.ToEntity();
        User createdUser = userRepository.save(previousUser);

        map.put("createdUser", createdUser);
        Long userid = createdUser.getId();

        MyStatusInfoDto myStatusInfoDto = new MyStatusInfoDto(userid);
        MyStatusInfo myStatusInfo = myStatusInfoDto.ToEntity();
        myStatusInfo = myStatusInfoRepository.save(myStatusInfo);
        map.put("myStatusInfo", myStatusInfo);
        return map;
    }
}
