package com.onlyonegames.eternalfantasia.domain.model.dto.Gotcha;

import com.onlyonegames.eternalfantasia.domain.model.entity.Gotcha.GotchaMileage;
import lombok.Data;

@Data
public class GotchaMileageDto {
    Long id;
    Long useridUser;
    int mileage;
    long totalCountGetMileage;

    public GotchaMileage ToEntity() {
        return GotchaMileage.builder().useridUser(useridUser).build();
    }

    public void InitFromDbData(GotchaMileage dbData) {
        this.id = dbData.getId();
        this.useridUser = dbData.getUseridUser();
        this.mileage = dbData.getMileage();
        this.totalCountGetMileage = dbData.getTotalCountGetMileage();
    }
}
