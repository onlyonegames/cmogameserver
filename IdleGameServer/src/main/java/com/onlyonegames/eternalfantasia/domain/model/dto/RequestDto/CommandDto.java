package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

import lombok.Data;

import java.util.List;

@Data

public class CommandDto {
    public String cmd;
    public List<ContainerDto> containers;
}
