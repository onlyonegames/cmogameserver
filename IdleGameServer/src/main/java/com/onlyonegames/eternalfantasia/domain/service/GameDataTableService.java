package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class GameDataTableService
{
    private List<UpgradeStatusBuyInfoTable> upgradeStatusBuyInfoTableList = null; // 2021-03-23 재형: 능력치 상승 초기 구매 가격
    /*idle game*/
    private List<HeroClassInfoTable> heroClassInfoTableList = null;
    private List<ActiveSkillTable> activeSkillTableList = null;
    private List<EquipmentTable> equipmentTableList = null;
    private List<InitJsonDatasForFirstUser> initJsonDatasForFirstUserList = null;
    private List<RuneInfoTable> runeInfoTableList = null;
    private List<SkillUpgradeInfoTable> skillUpgradeInfoTableList = null;

    @Autowired
    private UpgradeStatusBuyInfoTableRepository upgradeStatusBuyInfoTableRepository;  // 2021-03-23 재형: 능력치 상승 초기 구매 가격
    /*idle game*/
    @Autowired
    private HeroClassInfoTableRepository heroClassInfoTableRepository;
    @Autowired
    private ActiveSkillTableRepository activeSkillTableRepository;
    @Autowired
    private EquipmentTableRepository equipmentTableRepository;
    @Autowired
    private InitJsonDatasForFirstUserRepository initJsonDatasForFirstUserRepository;
    @Autowired
    private RuneInfoTableRepository runeInfoTableRepository;
    @Autowired
    private SkillUpgradeInfoTableRepository skillUpgradeInfoTableRepository;

    /**/
    // 2021-03-23 재형: 능력치 상승 초기 구매 가격
    public List<UpgradeStatusBuyInfoTable> UpgradeStatusBuyInfoTableList() {
        upgradeStatusBuyInfoTableList = upgradeStatusBuyInfoTableList == null ? upgradeStatusBuyInfoTableRepository.findAll() : upgradeStatusBuyInfoTableList;
        return upgradeStatusBuyInfoTableList;
    }

    /*idle game*/
    public List<HeroClassInfoTable> HeroClassInfoTable() {
        heroClassInfoTableList = heroClassInfoTableList == null ? heroClassInfoTableRepository.findAll() : heroClassInfoTableList;
        return heroClassInfoTableList;
    }

    public List<ActiveSkillTable> ActiveSkillTable() {
        activeSkillTableList = activeSkillTableList == null ? activeSkillTableRepository.findAll() : activeSkillTableList;
        return activeSkillTableList;
    }

    public List<EquipmentTable> EquipmentTable() {
        equipmentTableList = equipmentTableList == null ? equipmentTableRepository.findAll() : equipmentTableList;
        return equipmentTableList;
    }

    public List<InitJsonDatasForFirstUser> InitJsonDatasForFirstUser() {
        initJsonDatasForFirstUserList = initJsonDatasForFirstUserList == null ? initJsonDatasForFirstUserRepository.findAll() : initJsonDatasForFirstUserList;
        return initJsonDatasForFirstUserList;
    }

    public List<RuneInfoTable> RuneInfoTable() {
        runeInfoTableList = runeInfoTableList == null ? runeInfoTableRepository.findAll() : runeInfoTableList;
        return runeInfoTableList;
    }

    public List<SkillUpgradeInfoTable> SkillUpgradeInfoTable() {
        skillUpgradeInfoTableList = skillUpgradeInfoTableList == null ? skillUpgradeInfoTableRepository.findAll() : skillUpgradeInfoTableList;
        return skillUpgradeInfoTableList;
    }

    public Map<String, Object> ResetGameDataTable(Map<String, Object> map)
    {
        upgradeStatusBuyInfoTableList = null;

        /*idle game*/
        heroClassInfoTableList = null;
        activeSkillTableList = null;
        equipmentTableList = null;
        initJsonDatasForFirstUserList = null;
        runeInfoTableList = null;
        skillUpgradeInfoTableList = null;

        UpgradeStatusBuyInfoTableList();

        /*idle game*/
        HeroClassInfoTable();
        ActiveSkillTable();
        EquipmentTable();
        InitJsonDatasForFirstUser();
        RuneInfoTable();
        SkillUpgradeInfoTable();

        /*idle game*/
        map.put("heroClassInfoTable", heroClassInfoTableList);
        map.put("activeSkillTable", activeSkillTableList);
        map.put("equipmentTable", equipmentTableList);
        map.put("initJsonDatasForFirstUser", initJsonDatasForFirstUserList);
        map.put("runeInfoTable", runeInfoTableList);
        map.put("skillUpgradeInfoTable", skillUpgradeInfoTableList);
        return map;
    }
}
