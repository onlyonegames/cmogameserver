package com.onlyonegames.eternalfantasia.domain.model.entity.Logging;

import com.onlyonegames.eternalfantasia.domain.CommonLogEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@Getter
@NoArgsConstructor
public class MainHeroExpLog extends CommonLogEntity {
    public MainHeroExpLog(Long useridUser, int logType, String json_LogDetail) {
        super(useridUser, logType, json_LogDetail);
    }
}
