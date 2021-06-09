package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

import lombok.Data;
import springfox.documentation.spring.web.json.Json;

import java.util.List;

@Data
public class IntegrationRequestDto {
    /**
     * <p>path : API URL or API Function</p>
     * <p>type : Method Type</p>
     * <p>body : Request Body</p>
     */
    public static class RequestContent{
        public String path;
        public String type;
        public String body;
    }

    List<RequestContent> requestContentList;
}
