package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyForTest;
import com.onlyonegames.eternalfantasia.domain.model.entity.UpgradeStatus;
import com.onlyonegames.eternalfantasia.domain.repository.MyForTestRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UpgradeStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
@AllArgsConstructor
public class UpgradeStatusService
{
    private final UpgradeStatusRepository upgradeStatusRepository;

    public Map<String, Object> GetUpgradeStatus (Long userId, Map<String, Object> map)
    {
        UpgradeStatus upgradeStatus = upgradeStatusRepository.findByUseridUser(userId).orElse(null);
        //String json = JsonStringHerlper.WriteValueAsStringFromData(myForTest);
        //map.put("myForTest", json);
        map.put("upgradeStatus", upgradeStatus);
        return map;
    }
}
