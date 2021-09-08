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
    private List<AccessoryTable> accessoryTableList = null;
    private List<PassiveSkillTable> passiveSkillTableList = null;
    private List<DayFreePassTable> dayFreePassTableList = null;
    private List<DayADPassTable> dayADPassTableList = null;
    private List<AttendanceFreePassTable> attendanceFreePassTableList = null;
    private List<AttendanceBuyPassTable> attendanceBuyPassTableList = null;
    private List<LevelBuyPassTable> levelBuyPassTableList = null;
    private List<LevelFreePassTable> levelFreePassTableList = null;
    private List<AdventureStageBuyPassTable> adventureStageBuyPassTableList = null;
    private List<AdventureStageFreePassTable> adventureStageFreePassTableList = null;
    private List<ShopRewardTable> shopRewardTableList = null;

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
    @Autowired
    private AccessoryTableRepository accessoryTableRepository;
    @Autowired
    private PassiveSkillTableRepository passiveSkillTableRepository;
    @Autowired
    private DayFreePassTableRepository dayFreePassTableRepository;
    @Autowired
    private DayADPassTableRepository dayADPassTableRepository;
    @Autowired
    private AttendanceFreePassTableRepository attendanceFreePassTableRepository;
    @Autowired
    private AttendanceBuyPassTableRepository attendanceBuyPassTableRepository;
    @Autowired
    private LevelBuyPassTableRepository levelBuyPassTableRepository;
    @Autowired
    private LevelFreePassTableRepository levelFreePassTableRepository;
    @Autowired
    private AdventureStageBuyPassTableRepository adventureStageBuyPassTableRepository;
    @Autowired
    private AdventureStageFreePassTableRepository adventureStageFreePassTableRepository;
    @Autowired
    private ShopRewardTableRepository shopRewardTableRepository;

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

    public List<AccessoryTable> AccessoryTable() {
        accessoryTableList = accessoryTableList == null ? accessoryTableRepository.findAll() : accessoryTableList;
        return accessoryTableList;
    }

    public List<PassiveSkillTable> PassiveSkillTable() {
        passiveSkillTableList = passiveSkillTableList == null ? passiveSkillTableRepository.findAll() : passiveSkillTableList;
        return passiveSkillTableList;
    }

    public List<DayFreePassTable> DayFreePassTable() {
        dayFreePassTableList = dayFreePassTableList == null ? dayFreePassTableRepository.findAll() : dayFreePassTableList;
        return dayFreePassTableList;
    }

    public List<DayADPassTable> DayADPassTable() {
        dayADPassTableList = dayADPassTableList == null ? dayADPassTableRepository.findAll() : dayADPassTableList;
        return dayADPassTableList;
    }

    public List<AttendanceFreePassTable> AttendanceFreePassTable() {
        attendanceFreePassTableList = attendanceFreePassTableList == null ? attendanceFreePassTableRepository.findAll() : attendanceFreePassTableList;
        return attendanceFreePassTableList;
    }

    public List<AttendanceBuyPassTable> AttendanceBuyPassTable() {
        attendanceBuyPassTableList = attendanceBuyPassTableList == null ? attendanceBuyPassTableRepository.findAll() : attendanceBuyPassTableList;
        return attendanceBuyPassTableList;
    }

    public List<LevelFreePassTable> LevelFreePassTable() {
        levelFreePassTableList = levelFreePassTableList == null ? levelFreePassTableRepository.findAll() : levelFreePassTableList;
        return levelFreePassTableList;
    }

    public List<LevelBuyPassTable> LevelBuyPassTable() {
        levelBuyPassTableList = levelBuyPassTableList == null ? levelBuyPassTableRepository.findAll() : levelBuyPassTableList;
        return levelBuyPassTableList;
    }

    public List<AdventureStageBuyPassTable> AdventureStageBuyPassTable() {
        adventureStageBuyPassTableList = adventureStageBuyPassTableList == null ? adventureStageBuyPassTableRepository.findAll() : adventureStageBuyPassTableList;
        return adventureStageBuyPassTableList;
    }

    public List<AdventureStageFreePassTable> AdventureStageFreePassTable() {
        adventureStageFreePassTableList = adventureStageFreePassTableList == null ? adventureStageFreePassTableRepository.findAll() : adventureStageFreePassTableList;
        return adventureStageFreePassTableList;
    }

    public List<ShopRewardTable> ShopRewardTable() {
        shopRewardTableList = shopRewardTableList == null ? shopRewardTableRepository.findAll() : shopRewardTableList;
        return shopRewardTableList;
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
        accessoryTableList = null;
        passiveSkillTableList = null;
        dayFreePassTableList = null;
        dayADPassTableList = null;
        attendanceFreePassTableList = null;
        attendanceBuyPassTableList = null;
        levelFreePassTableList = null;
        levelBuyPassTableList = null;
        adventureStageFreePassTableList = null;
        adventureStageBuyPassTableList = null;
        shopRewardTableList = null;

        UpgradeStatusBuyInfoTableList();

        /*idle game*/
        HeroClassInfoTable();
        ActiveSkillTable();
        EquipmentTable();
        InitJsonDatasForFirstUser();
        RuneInfoTable();
        SkillUpgradeInfoTable();
        AccessoryTable();
        PassiveSkillTable();
        DayFreePassTable();
        DayADPassTable();
        AttendanceFreePassTable();
        AttendanceBuyPassTable();
        LevelFreePassTable();
        LevelBuyPassTable();
        AdventureStageFreePassTable();
        AdventureStageBuyPassTable();
        ShopRewardTable();

        /*idle game*/
        map.put("heroClassInfoTable", heroClassInfoTableList);
        map.put("activeSkillTable", activeSkillTableList);
        map.put("equipmentTable", equipmentTableList);
        map.put("initJsonDatasForFirstUser", initJsonDatasForFirstUserList);
        map.put("runeInfoTable", runeInfoTableList);
        map.put("skillUpgradeInfoTable", skillUpgradeInfoTableList);
        map.put("accessoryTable", accessoryTableList);
        map.put("passiveSkillTable", passiveSkillTableList);
        map.put("dayFreePassTable", dayFreePassTableList);
        map.put("dayADPassTable", dayADPassTableList);
        map.put("attendanceFreePassTable", attendanceFreePassTableList);
        map.put("attendanceBuyPassTable", attendanceBuyPassTableList);
        map.put("levelFreePassTable", levelFreePassTableList);
        map.put("levelBuyPassTable", levelBuyPassTableList);
        map.put("adventureStageFreePassTable", adventureStageFreePassTableList);
        map.put("adventureStageBuyPassTable", adventureStageBuyPassTableList);
        map.put("shopRewardTable", shopRewardTableList);
        return map;
    }
}
