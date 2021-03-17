package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

import lombok.Data;

@Data
public class QuilityResmeltingRequestDto {
    ItemRequestDto originalItem;/*품질 제련을 적용할 아이템*/
    ItemRequestDto equipmentMaterial;/*재료로 사용될 장비 아이템*/
    ItemRequestDto resmeltingMaterial;/*재료로 사용될 재련석*/
}
