package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

import lombok.Data;

@Data
public class TeamBuildingRequestDto {
    /*MyCharacters에서의 id*/
    Long characterId;
    /*해당 Character가 덱내에서 위치할 인덱스*/
    int index;
}
