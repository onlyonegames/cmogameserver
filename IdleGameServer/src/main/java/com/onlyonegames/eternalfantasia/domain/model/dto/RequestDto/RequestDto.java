package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

//import java.util.List;
import lombok.Data;

import java.util.List;

@Data
public class RequestDto {
    public List<CommandDto> cmds;
    //public List<ElementDto> Element;
    //String key;
    //String value;
}