package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.model.dto.ProfileDto.ProfileDataDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyForTest;
import com.onlyonegames.eternalfantasia.domain.repository.MyForTestRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyProfileDataRepository;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@AllArgsConstructor
public class MyForTestService
{
    private final MyForTestRepository myForTestRepository;

    public Map<String, Object> GetMyForTest (Long userId, Map<String, Object> map)
    {
        MyForTest myForTest =  myForTestRepository.findByUseridUser(userId).orElse(null);
        //String json = JsonStringHerlper.WriteValueAsStringFromData(myForTest);
        //map.put("myForTest", json);
        map.put("myForTest", myForTest);
        return map;
    }
}
