package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyForTest;
import com.onlyonegames.eternalfantasia.domain.repository.MyForTestRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Map<String, Object> SetMyForTest (Long userId, String key, String value, Map<String, Object> map)
    {
        MyForTest myForTest =  myForTestRepository.findByUseridUser(userId).orElse(null);
        //String json = JsonStringHerlper.WriteValueAsStringFromData(myForTest);
        //map.put("myForTest", json);
        //myForTest.SetMyStringValue(value);
        myForTest.Set(key, value);
        map.put("myForTest", myForTest);
        return map;
    }
}
