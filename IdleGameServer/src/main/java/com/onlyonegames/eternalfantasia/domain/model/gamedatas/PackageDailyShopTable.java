package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "packagedailyshoptable")
public class PackageDailyShopTable {
    @Id
    int id;
    //iapTable에 있는 인앱 아이템 코드
    String iapTableCode;
}
