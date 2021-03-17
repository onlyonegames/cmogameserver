package com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool;

import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import lombok.Data;

@Data
public class CurrencyDto {
    public Long userId;
    public int addDiamond;
    public int addGold;
    public int addLinkforcePoint;
    public int addLowDragonScale;
    public int addMiddleDragonScale;
    public int addHighDragonScale;
    public int addArenaCoin;
    public int addArenaTicket;
    public int addFreeArenaCountPerDay;

    public void InitFromDbData(User user) {
        userId = user.getId();
        addDiamond = user.getDiamond();
        addGold = user.getGold();
        addLinkforcePoint = user.getLinkforcePoint();
        addLowDragonScale = user.getLowDragonScale();
        addMiddleDragonScale = user.getMiddleDragonScale();
        addHighDragonScale = user.getHighDragonScale();
        addArenaCoin = user.getArenaCoin();
        addArenaTicket = user.getArenaTicket();
        addFreeArenaCountPerDay = user.getFreeArenaCountPerDay();
    }
}
