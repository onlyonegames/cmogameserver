package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.model.gamedatas.VersionCheckTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.VersionCheckTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;

@Service
@Transactional
public class VersionCheckService {
    @Autowired
    private VersionCheckTableRepository versionCheckTableRepository;

    private VersionCheckTable versionCheckTable = null;

    public VersionCheckTable VersionCheckTable() {
        versionCheckTable = versionCheckTable == null ? versionCheckTableRepository.getOne(1) : versionCheckTable;
        return versionCheckTable;
    }

    public Map<String, Object> ResetVersion(Map<String, Object> map) {
        versionCheckTable = null;
        VersionCheckTable();
        map.put("version", versionCheckTable.getVersion());
        return map;
    }

    public void VersionCheck(Map<String, Object> map) {
        map.put("version", VersionCheckTable().getVersion());
    }
}
