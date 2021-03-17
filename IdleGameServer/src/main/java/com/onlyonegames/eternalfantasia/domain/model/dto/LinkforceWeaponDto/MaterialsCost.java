package com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceWeaponDto;

import lombok.Data;

import java.util.List;

@Data
public class MaterialsCost {
    public static class NeedMaterial {
        public String code;
        public int cnt;
    }
    public static class NeedMaterialsInfo {
        public int level;
        public List<NeedMaterial> list;
    }

   public List<NeedMaterialsInfo> needMaterialList;
}
