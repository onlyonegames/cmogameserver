package com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceWeaponDto;

import java.util.List;

public class LinkweaponInfoDtosList  {

    public static class LinkweaponInfoDto {
        public int id;
        public boolean open;
        public int upgrade; /*1은 최초 해당 무기가 오픈 되었을때, 2부터 강화1단계로 봄(1level은 강화 0단계) 최종 level 5(강화레벨 4)*/
        public List<Integer> dependenciesList;
    }

    public static class CompanionLinkweaponInfo {
        public String ownerCode;
        public int strengthenId;/*현재 강화를 진행중인 무기 Id*/
        public List<Integer> selectedIds;
        public List<LinkweaponInfoDto> linkweaponInfoDtoList;

        public int GetRevolutionStep(int weaponID) {
            int previousDependencieNo = -1;
            int i = 0;
            int step = 0;
            while (true) {
                if(linkweaponInfoDtoList.size() <= i)
                    break;
                if(weaponID < i)
                    break;
                LinkweaponInfoDto linkweaponInfoDto = linkweaponInfoDtoList.get(i++);
                if(linkweaponInfoDto.dependenciesList.size() > 0) {
                    int dependencieNo = linkweaponInfoDto.dependenciesList.get(0);
                    if(dependencieNo != previousDependencieNo) {
                        previousDependencieNo = dependencieNo;
                        step++;
                    }
                }
            }
            return step;
        }
    }

    public List<CompanionLinkweaponInfo> companionLinkweaponInfoList;
}
