package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.model.gamedatas.ServerStatusInfo;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.ServerStatusInfoRepository;
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
    @Autowired
    private ServerStatusInfoRepository serverStatusInfoRepository;

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

    public Map<String, Object> PreVersionCheck(String version, Map<String, Object> map) {
        ServerStatusInfo serverStatusInfo = serverStatusInfoRepository.getOne(1);
        if (serverStatusInfo.getServerStatus() == 1)
            map.put("serverStatus", serverStatusInfo.getServerStatus());

        VersionCheckTable versionCheckTable = VersionCheckTable();
        if (version.equals(versionCheckTable.getVersion())) {
            map.put("server", "https://api.classmasteronline.com:8080/");
            return map;
        }
        String[] clientVersionSplit = version.split("\\.");
        String[] dBVersionSplit = versionCheckTable.getVersion().split("\\.");

        for (int i = 0; i < clientVersionSplit.length; i++) {
            int clientInt = Integer.parseInt(clientVersionSplit[i]);
            int dBInt = Integer.parseInt(dBVersionSplit[i]);
            if (clientInt > dBInt) {
                map.put("server", "http://3.38.152.93:8080/");
                return map;
            }
            else if (clientInt < dBInt) {
                map.put("server", "https://api.classmasteronline.com:8080/");
                return map;
            }
        }
        map.put("server", "https://api.classmasteronline.com:8080/");
        return map;
    }
}
