package com.onlyonegames.eternalfantasia.domain.model.dto.Gotcha;

import com.onlyonegames.eternalfantasia.domain.model.entity.Gotcha.GotchaMileage;
import com.onlyonegames.eternalfantasia.domain.model.entity.Gotcha.PickupTable;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PickupTableDto {
    Long id;
    String pickupCharacterCode;
    LocalDateTime startDate;
    LocalDateTime endDate;

    public PickupTable ToEntity() {
        return PickupTable.builder().pickupCharacterCode(pickupCharacterCode).startDate(startDate).endDate(endDate).build();
    }

    public void InitFromDbData(PickupTable pickupTable) {
        this.id = pickupTable.getId();
        this.pickupCharacterCode = pickupTable.getPickupCharacterCode();
        this.startDate = pickupTable.getStartDate();
        this.endDate = pickupTable.getEndDate();
    }
}
