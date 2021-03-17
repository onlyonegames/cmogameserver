package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.CompanionUserEvaluationTable;
import com.onlyonegames.eternalfantasia.domain.model.entity.MainHeroSkillExpandInfo;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.CompanionUserEvaluationRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MainHeroSkillExpandInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class GameDataTableService {
    private List<HeroEquipmentsTable> heroEquipmentsTableList = null;
    private List<EquipmentOptionsInfoTable> optionsInfoTableList = null;
    private List<MainHeroSkillExpandInfo> mainHeroSkillExpandInfoList = null;
    private List<SpendableItemInfoTable> spendableItemInfoTableList = null;
    private List<HeroEquipmentsPromotionTable> heroEquipmentsPromotionTableList = null;
    private List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList = null;
    private List<HeroEquipmentProductionTable> heroEquipmentProductionTableList = null;
    private List<HeroEquipmentProductionExpandTable> heroEquipmentProductionExpandTableList = null;
    private List<ProductionSlotOpenCostTable> productionSlotOpenCostTableList = null;
    private List<ProductionMasteryInfoTable> productionMasteryInfoTableList = null;
    private List<GiftBoxItemInfoTable> giftBoxItemInfoTableList = null;
    private List<StageTable> stageTableList = null;
    private List<HeroTowerStageTable> heroTowerStageTableList = null;
    private List<OrdealStageTable> ordealStageTableList = null;
    private List<herostable> herosTableList = null;
    private List<GiftTable> giftTableList = null;
    private List<LinkforceTalentsTable> linkforceTalentsTableList = null;
    private List<LinkweaponTalentsTable> linkweaponTalentsTableList = null;
    private List<InitJsonDatasForFirstUser> initJsonDatasForFIrstUserList = null;
    private List<LegionCostumeTable> legionCostumeTableList = null;
    private List<GotchaStep1Table> gotchaStep1TablesList = null;
    private List<GotchaStep2Table> gotchaStep2TableList = null;
    private List<AncientDragonStageTable> ancientDragonStageTableList = null;
    private List<ArenaRewardsTable> arenaRewardsTableList = null;
    private List<RankingTierTable> rankingTierTableList = null;
    private List<ArenaPlayInfoTable> arenaPlayInfoTableList = null;
    private List<FieldObjectInfoTable> fieldObjectInfoTableList = null;
    private List<ExpeditionProcessTable> expeditionProcessTableList = null;
    private List<MainQuestInfoTable> mainQuestInfoTableList = null;
    private List<ChapterOpenConditionTable> chapterOpenConditionTableList = null;
    private List<StageOpenCondtionTable> stageOpenCondtionTableList = null;
    private List<HeroEquipmentClassProbabilityTable> heroEquipmentClassProbabilityTableList = null;
    private List<QuilityResmeltingInfoTable> quilityResmeltingInfoTableList = null;
    private List<ShopTable> shopTableList = null;
    private List<ArenaShopTable> arenaShopTableList = null;
    private List<AncientShopTable> ancientShopTableList = null;
    private List<CurrencyShopTable> currencyShopTableList = null;
    private List<PieceShopTable> pieceShopTableList = null;
    private List<DarkObeShopTable> darkObeShopTableList = null;
    private List<GotchaTable> gotchaTableList = null;
    private List<DailyMissionTable> dailyMissionTableList = null;
    private List<WeeklyMissionTable> weeklyMissionTableList = null;
    private List<QuestMissionTable> questMissionTableList = null;
    private List<DailyMissionRewardTable> dailyMissionRewardTableList = null;
    private List<WeeklyMissionRewardTable> weeklyMissionRewardTableList = null;
    private List<PassiveItemTable> passiveItemTableList = null;
    private List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = null;
    private List<ArenaRankingRewardTable> arenaRankingRewardTableList = null;
    private List<SkillTable> skillTableList = null;
    private List<ADLimitInfoTable> adLimitInfoTable = null;
    private List<CommonVariableTable> commonVariableTable = null;
    //private List<CompanionUserEvaluationTable> companionUserEvaluationTable = null;
    private List<InfiniteTowerRewardsTable> infiniteTowerRewardsTableList = null;
    private List<InfiniteTowerStageTable> infiniteTowerStageTableList = null;
    private List<GotchaCharacterTable> gotchaCharacterTableList = null;
    private List<CharacterToPieceTable> characterToPieceTableList = null;
    private List<GotchaEtcTable> gotchaEtcTableList = null;
    private List<ExchangeItemEventTable> exchangeItemEventTableList = null;
    private List<EternalPassRewardTable> eternalPassRewardTableList = null;

    private List<EternalPassDailyMissionTable> eternalPassDailyMissionTableList = null;
    private List<EternalPassWeekMissionTable> eternalPassWeekMissionTablesList = null;
    private List<EternalPassQuestMissionTable> eternalPassQuestMissionTableList = null;

    private List<AchieveEventTable> achieveEventTableList = null;
    private List<MonsterKillEventTable> monsterKillEventTableList = null;

    private List<ProfileFrameMissionTable> profileFrameMissionTableList = null;
    private List<ProfileFrameTable> profileFrameTableList = null;

    private List<AttendanceRewardTable> attendanceRewardTableList = null;

    private List<IapTable> iapTableList = null;
    private List<PackageSpecialShopTable> packageSpecialShopTableList = null;
    private List<PackageDailyShopTable> packageDailyShopTableList = null;
    private List<PackageWeeklyShopTable> packageWeeklyShopTableList = null;
    private List<PackageMonthlyShopTable> packageMonthlyShopTableList = null;

    private List<FieldDungeonMonsterWaveInfoTable> fieldDungeonMonsterWaveInfoTableList = null;
    private List<FieldDungeonRewardItemTable> fieldDungeonRewardItemTableList = null;
    private List<FieldDungeonRewardTable> fieldDungeonRewardTableList = null;
    private List<FieldMonsterInfoTable> fieldMonsterInfoTableList = null;
    private List<FieldDungeonRewardProbabilityTable> fieldDungeonRewardProbabilityTableList = null;
    private List<FieldDungeonBuyCountTable> fieldDungeonBuyCountTableList = null;

    @Autowired
    private HeroEquipmentsTableRepository heroEquipmentsTableRepository;
    @Autowired
    private EquipmentOptionsInfoTableRepository equipmentOptionsInfoTableRepository;
    @Autowired
    private MainHeroSkillExpandInfoRepository mainHeroSkillExpandInfoRepository;
    @Autowired
    private SpendableItemInfoTableRepository spendableItemInfoTableRepository;
    @Autowired
    private HeroEquipmentsPromotionTableRepository heroEquipmentsPromotionTableRepository;
    @Autowired
    private EquipmentMaterialInfoTableRepository equipmentMaterialInfoTableRepository;
    @Autowired
    private HeroEquipmentProductionTableRepository heroEquipmentProductionTableRepository;
    @Autowired
    private HeroEquipmentProductionExpandTableRepository heroEquipmentProductionExpandTableRepository;
    @Autowired
    private ProductionSlotOpenCostTableRepository productionSlotOpenCostTableRepository;
    @Autowired
    private ProductionMasteryInfoTableRepository productionMasteryInfoTableRepository;
    @Autowired
    private GiftBoxItemInfoTableRepository giftboxItemInfoTableRepository;
    @Autowired
    private StageTableRepository stageTableRepository;
    @Autowired
    private HeroTowerStageTableRepository heroTowerStageTableRepository;
    @Autowired
    private OrdealStageTableRepository ordealStageTableRepository;
    @Autowired
    private AncientDragonStageTableRepository ancientDragonStageTableRepository;
    @Autowired
    private HerostableRepository herostableRepository;
    @Autowired
    private GiftTableRepository giftTableRepository;
    @Autowired
    private LinkforceTalentsTableRepository linkforceTalentsTableRepository;
    @Autowired
    private LinkweaponTalentsTableRepository linkweaponTalentsTableRepository;
    @Autowired
    private InitJsonDatasForFirstUserRepository initJsonDatasForFirstUserRepository;
    @Autowired
    private LegionCostumeTableRepository legionCostumeTableRepository;
    @Autowired
    private GotchaStep1TableRepository gotchaStep1TableRepository;
    @Autowired
    private GotchaStep2TableRepository gotchaStep2TableRepository;
    @Autowired
    private ArenaRewardsTableRepository arenaRewardsTableRepository;
    @Autowired
    private RankingTierTableRepository rankingTierTableRepository;
    @Autowired
    private ArenaPlayInfoTableRepository arenaPlayInfoTableRepository;
    @Autowired
    private FieldObjectInfoTableRepository fieldObjectInfoTableRepository;
    @Autowired
    private ExpeditionProcessTableRepository expeditionProcessTableRepository;
    @Autowired
    private MainQuestInfoTableRepository mainQuestInfoTableRepository;
    @Autowired
    private ChapterOpenConditionTableRepository chapterOpenConditionTableRepository;
    @Autowired
    private StageOpenCondtionTableRepository stageOpenCondtionTableRepository;
    @Autowired
    private HeroEquipmentClassProbabilityTableRepository heroEquipmentClassProbabilityTableRepository;
    @Autowired
    private QuilityResmeltingInfoTableRepository quilityResmeltingInfoTableRepository;
    @Autowired
    private ShopTableRepository shopTableRepository;
    @Autowired
    private ArenaShopTableRepository arenaShopTableRepository;
    @Autowired
    private AncientShopTableRepository ancientShopTableRepository;
    @Autowired
    private CurrencyShopTableRepository currencyShopTableRepository;
    @Autowired
    private PieceShopTablerepository pieceShopTablerepository;
    @Autowired
    private DarkObeShopTableRepository darkObeShopTableRepository;
    @Autowired
    private GotchaTableRepository gotchaTableRepository;
    @Autowired
    private DailyMissionTableRepository dailyMissionTableRepository;
    @Autowired
    private WeeklyMissionTableRepository weeklyMissionTableRepository;
    @Autowired
    private QuestMissionTableRepository questMissionTableRepository;
    @Autowired
    private DailyMissionRewardTableRepository dailyMissionRewardTableRepository;
    @Autowired
    private WeeklyMissionRewardTableRepository weeklyMissionRewardTableRepository;
    @Autowired
    private PassiveItemTableRepository passiveItemTableRepository;
    @Autowired
    private BelongingCharacterPieceTableRepository belongingCharacterPieceTableRepository;
    @Autowired
    private ArenaRankingRewardTableRepository arenaRankingRewardTableRepository;
    @Autowired
    private SkillTableRepository skillTableRepository;
    @Autowired
    private ADLimitInfoTableRepository adLimitInfoTableRepository;
    @Autowired
    private CommonVariableTableRepository commonvariableTableRepository;
    //@Autowired
    //private CompanionUserEvaluationRepository companionUserEvaluationRepository;
    @Autowired
    private InfiniteTowerRewardsTableRepository infiniteTowerRewardsTableRepository;
    @Autowired
    private InfiniteTowerStageInfoTableRepository infiniteTowerStageInfoTableRepository;
    @Autowired
    private GotchaCharacterTableRepository gotchaCharacterTableRepository;
    @Autowired
    private CharacterToPieceTableRepository characterToPieceTableRepository;
    @Autowired
    private GotchaEtcTableRepository gotchaEtcTableRepository;
    @Autowired
    private ExchangeItemEventTableRepository exchangeItemEventTableRepository;
    @Autowired
    private EternalPassRewardTableRepository eternalPassRewardTableRepository;
    @Autowired
    private EternalPassDailyMissionTableRepository eternalPassDailyMissionTableRepository;
    @Autowired
    private EternalPassWeekMissionTAbleRepository eternalPassWeekMissionTAbleRepository;
    @Autowired
    private EternalPassQuestMissionTableRepository eternalPassQuestMissionTableRepository;
    @Autowired
    private AchieveEventTableRepository achieveEventTableRepository;
    @Autowired
    private MonsterKillEventTableRepository monsterKillEventTableRepository;
    @Autowired
    private ProfileFrameMissionTableRepository profileFrameMissionTableRepository;
    @Autowired
    private ProfileFrameTableRepository profileFrameTableRepository;
    @Autowired
    private AttendanceRewardTableRepository attendanceRewardTableRepository;

    @Autowired
    private IapTableRepository iapTableRepository;
    @Autowired
    private PackageSpecialShopTableRepository packageSpecialShopTableRepository;
    @Autowired
    private PackageDailyShopTableRepository packageDailyShopTableRepository;
    @Autowired
    private PackageWeeklyShopTableRepository packageWeeklyShopTableRepository;
    @Autowired
    private PackageMonthlyShopTableRepository packageMonthlyShopTableRepository;

    @Autowired
    private FieldDungeonMonsterWaveInfoTableRepository fieldDungeonMonsterWaveInfoTableRepository;
    @Autowired
    private FieldDungeonRewardItemTableRepository fieldDungeonRewardItemTableRepository;
    @Autowired
    private FieldDungeonRewardTableRepository fieldDungeonRewardTableRepository;
    @Autowired
    private FieldMonsterInfoTableRepository fieldMonsterInfoTableRepository;
    @Autowired
    private FieldDungeonRewardProbabilityTableRepository fieldDungeonRewardProbabilityTableRepository;
    @Autowired
    private FieldDungeonBuyCountTableRepository fieldDungeonBuyCountTableRepository;

    public List<HeroEquipmentsTable> HeroEquipmentsTableList() {
        heroEquipmentsTableList = heroEquipmentsTableList == null ? heroEquipmentsTableRepository.findAll() : heroEquipmentsTableList;
        return heroEquipmentsTableList;
    }
    public List<EquipmentOptionsInfoTable> OptionsInfoTableList() {
        optionsInfoTableList = optionsInfoTableList == null ? equipmentOptionsInfoTableRepository.findAll() : optionsInfoTableList;
        return optionsInfoTableList;
    }
    public List<MainHeroSkillExpandInfo> MainHeroSkillExpandInfoList() {
        mainHeroSkillExpandInfoList = mainHeroSkillExpandInfoList == null ? mainHeroSkillExpandInfoRepository.findAll() : mainHeroSkillExpandInfoList;
        return mainHeroSkillExpandInfoList;
    }
    public List<SpendableItemInfoTable> SpendableItemInfoTableList() {
        spendableItemInfoTableList = spendableItemInfoTableList == null ? spendableItemInfoTableRepository.findAll() : spendableItemInfoTableList;
        return spendableItemInfoTableList;
    }
    public List<HeroEquipmentsPromotionTable> HeroEquipmentsPromotionTableList() {
        heroEquipmentsPromotionTableList = heroEquipmentsPromotionTableList == null ? heroEquipmentsPromotionTableRepository.findAll() : heroEquipmentsPromotionTableList;
        return heroEquipmentsPromotionTableList;
    }
    public List<EquipmentMaterialInfoTable> EquipmentMaterialInfoTableList() {
        equipmentMaterialInfoTableList = equipmentMaterialInfoTableList == null ? equipmentMaterialInfoTableRepository.findAll() : equipmentMaterialInfoTableList;
        return equipmentMaterialInfoTableList;
    }
    public List<HeroEquipmentProductionTable> HeroEquipmentProductionTableList() {
        heroEquipmentProductionTableList = heroEquipmentProductionTableList == null ? heroEquipmentProductionTableRepository.findAll() : heroEquipmentProductionTableList;
        return heroEquipmentProductionTableList;
    }
    public List<HeroEquipmentProductionExpandTable> HeroEquipmentProductionExpandTableList() {
        heroEquipmentProductionExpandTableList = heroEquipmentProductionExpandTableList == null ? heroEquipmentProductionExpandTableRepository.findAll() : heroEquipmentProductionExpandTableList;
        return heroEquipmentProductionExpandTableList;
    }
    public List<ProductionSlotOpenCostTable> ProductionSlotOpenCostTableList() {
        productionSlotOpenCostTableList = productionSlotOpenCostTableList == null ? productionSlotOpenCostTableRepository.findAll() : productionSlotOpenCostTableList;
        return productionSlotOpenCostTableList;
    }
    public  List<ProductionMasteryInfoTable> ProductionMasteryInfoTableList() {
        productionMasteryInfoTableList = productionMasteryInfoTableList == null ? productionMasteryInfoTableRepository.findAll() : productionMasteryInfoTableList;
        return productionMasteryInfoTableList;
    }
    public  List<GiftBoxItemInfoTable> GiftBoxItemInfoTableList() {
        giftBoxItemInfoTableList = giftBoxItemInfoTableList == null ? giftboxItemInfoTableRepository.findAll() : giftBoxItemInfoTableList;
        return giftBoxItemInfoTableList;
    }
    public List<StageTable> StageTableList() {
        stageTableList = stageTableList == null ? stageTableRepository.findAll() : stageTableList;
        return stageTableList;
    }
    public List<HeroTowerStageTable> HeroTowerStageTableList() {
        heroTowerStageTableList = heroTowerStageTableList == null ? heroTowerStageTableRepository.findAll() : heroTowerStageTableList;
        return heroTowerStageTableList;
    }
    public List<OrdealStageTable> OrdealStageTableList() {
        ordealStageTableList = ordealStageTableList == null ? ordealStageTableRepository.findAll() : ordealStageTableList;
        return ordealStageTableList;
    }
    public List<AncientDragonStageTable> AncientDragonStageTableList() {
        ancientDragonStageTableList = ancientDragonStageTableList == null ? ancientDragonStageTableRepository.findAll() : ancientDragonStageTableList;
        return ancientDragonStageTableList;
    }
    public List<herostable> HerosTableList() {
        herosTableList = herosTableList == null ? herostableRepository.findAll() : herosTableList;
        return herosTableList;
    }
    public List<GiftTable> GiftsTableList() {
        giftTableList = giftTableList == null ? giftTableRepository.findAll() : giftTableList;
        return giftTableList;
    }
    public List<LinkforceTalentsTable> LinkforceTalentsTableList() {
        linkforceTalentsTableList = linkforceTalentsTableList == null ? linkforceTalentsTableRepository.findAll() : linkforceTalentsTableList;
        return linkforceTalentsTableList;
    }
    public List<LinkweaponTalentsTable> LinkweaponTalentsTableList() {
        linkweaponTalentsTableList = linkweaponTalentsTableList == null ? linkweaponTalentsTableRepository.findAll() : linkweaponTalentsTableList;
        return linkweaponTalentsTableList;
    }
    public List<InitJsonDatasForFirstUser> InitJsonDatasForFirstUserList() {
        initJsonDatasForFIrstUserList = initJsonDatasForFIrstUserList == null ? initJsonDatasForFirstUserRepository.findAll() : initJsonDatasForFIrstUserList;
        return initJsonDatasForFIrstUserList;
    }
    public List<LegionCostumeTable> CostumeTableList() {
        legionCostumeTableList = legionCostumeTableList == null ? legionCostumeTableRepository.findAll() : legionCostumeTableList;
        return legionCostumeTableList;
    }
    public List<GotchaStep1Table> GotchaStep1TableList() {
        gotchaStep1TablesList = gotchaStep1TablesList == null ? gotchaStep1TableRepository.findAll() : gotchaStep1TablesList;
        return gotchaStep1TablesList;
    }
    public List<GotchaStep2Table> GotchaStep2TableList() {
        gotchaStep2TableList = gotchaStep2TableList == null ? gotchaStep2TableRepository.findAll() : gotchaStep2TableList;
        return gotchaStep2TableList;
    }
    public List<ArenaRewardsTable> ArenaRewardsTableList() {
        arenaRewardsTableList = arenaRewardsTableList == null ? arenaRewardsTableRepository.findAll() : arenaRewardsTableList;
        return arenaRewardsTableList;
    }
    public List<RankingTierTable> RankingTierTableList() {
        rankingTierTableList = rankingTierTableList == null ? rankingTierTableRepository.findAll() : rankingTierTableList;
        return rankingTierTableList;
    }
    public List<ArenaPlayInfoTable> ArenaPlayInfoTableList() {
        arenaPlayInfoTableList = arenaPlayInfoTableList == null ? arenaPlayInfoTableRepository.findAll() : arenaPlayInfoTableList;
        return arenaPlayInfoTableList;
    }
    public List<FieldObjectInfoTable> FieldObjectInfoTableList() {
        fieldObjectInfoTableList = fieldObjectInfoTableList == null ? fieldObjectInfoTableRepository.findAll() : fieldObjectInfoTableList;
        return fieldObjectInfoTableList;
    }
    public List<ExpeditionProcessTable> ExpeditionProcessTableList() {
        expeditionProcessTableList = expeditionProcessTableList == null ? expeditionProcessTableRepository.findAll() : expeditionProcessTableList;
        return expeditionProcessTableList;
    }

    public List<StageOpenCondtionTable> StageOpenConditionTableList() {
        stageOpenCondtionTableList = stageOpenCondtionTableList == null ? stageOpenCondtionTableRepository.findAll() : stageOpenCondtionTableList;
        return stageOpenCondtionTableList;
    }

    public List<ChapterOpenConditionTable> ChapterOpenConditionTableList() {
        chapterOpenConditionTableList = chapterOpenConditionTableList == null ? chapterOpenConditionTableRepository.findAll() : chapterOpenConditionTableList;
        return chapterOpenConditionTableList;
    }

    public List<MainQuestInfoTable> MainQuestInfoTableList() {
        mainQuestInfoTableList = mainQuestInfoTableList == null ? mainQuestInfoTableRepository.findAll() : mainQuestInfoTableList;
        return mainQuestInfoTableList;
    }

    public List<HeroEquipmentClassProbabilityTable> HeroEquipmentClassProbabilityTableList() {
        heroEquipmentClassProbabilityTableList = heroEquipmentClassProbabilityTableList == null ? heroEquipmentClassProbabilityTableRepository.findAll() : heroEquipmentClassProbabilityTableList;
        return  heroEquipmentClassProbabilityTableList;
    }

    public List<QuilityResmeltingInfoTable> QuilityResmeltingInfoTableList() {
        quilityResmeltingInfoTableList = quilityResmeltingInfoTableList == null ? quilityResmeltingInfoTableRepository.findAll() : quilityResmeltingInfoTableList;
        return  quilityResmeltingInfoTableList;
    }

    public List<ShopTable> ShopTableList(){
        shopTableList = shopTableList == null ? shopTableRepository.findAll() : shopTableList;
        return shopTableList;
    }

    public List<ArenaShopTable> ArenaShopTableList(){
        arenaShopTableList = arenaShopTableList == null ? arenaShopTableRepository.findAll() : arenaShopTableList;
        return arenaShopTableList;
    }

    public List<AncientShopTable> AncientShopTableList(){
        ancientShopTableList = ancientShopTableList == null ? ancientShopTableRepository.findAll() : ancientShopTableList;
        return ancientShopTableList;
    }

    public List<CurrencyShopTable> CurrencyShopTableList(){
        currencyShopTableList = currencyShopTableList == null ? currencyShopTableRepository.findAll() : currencyShopTableList;
        return currencyShopTableList;
    }

    public List<PieceShopTable> PieceShopTableList(){
        pieceShopTableList = pieceShopTableList == null ? pieceShopTablerepository.findAll() : pieceShopTableList;
        return pieceShopTableList;
    }

    public List<DarkObeShopTable> DarkObeShopTableList(){
        darkObeShopTableList = darkObeShopTableList == null ? darkObeShopTableRepository.findAll() : darkObeShopTableList;
        return darkObeShopTableList;
    }

    public List<GotchaTable> GotchaTableList(){
        gotchaTableList = gotchaTableList == null ? gotchaTableRepository.findAll() : gotchaTableList;
        return gotchaTableList;
    }

    public List<DailyMissionTable> DailyMissionTableList() {
        dailyMissionTableList = dailyMissionTableList == null ? dailyMissionTableRepository.findAll() : dailyMissionTableList;
        return dailyMissionTableList;
    }

    public List<WeeklyMissionTable> WeeklyMissionTableList() {
        weeklyMissionTableList = weeklyMissionTableList == null ? weeklyMissionTableRepository.findAll() : weeklyMissionTableList;
        return weeklyMissionTableList;
    }

    public List<QuestMissionTable> QuestMissionTableList() {
        questMissionTableList = questMissionTableList == null ? questMissionTableRepository.findAll() : questMissionTableList;
        return questMissionTableList;
    }

    public List<DailyMissionRewardTable> DailyMissionRewardTableList() {
        dailyMissionRewardTableList = dailyMissionRewardTableList == null ? dailyMissionRewardTableRepository.findAll() : dailyMissionRewardTableList;
        return dailyMissionRewardTableList;
    }

    public List<WeeklyMissionRewardTable> WeeklyMissionRewardTableList() {
        weeklyMissionRewardTableList = weeklyMissionRewardTableList == null ? weeklyMissionRewardTableRepository.findAll() : weeklyMissionRewardTableList;
        return weeklyMissionRewardTableList;
    }

    public List<PassiveItemTable> PassiveItemTableList() {
        passiveItemTableList = passiveItemTableList == null ? passiveItemTableRepository.findAll() : passiveItemTableList;
        return passiveItemTableList;
    }

    public List<BelongingCharacterPieceTable> BelongingCharacterPieceTableList() {
        belongingCharacterPieceTableList = belongingCharacterPieceTableList == null ? belongingCharacterPieceTableRepository.findAll() : belongingCharacterPieceTableList;
        return belongingCharacterPieceTableList;
    }

    public List<ArenaRankingRewardTable> ArenaRankingRewardTableList() {
        arenaRankingRewardTableList = arenaRankingRewardTableList == null ? arenaRankingRewardTableRepository.findAll() : arenaRankingRewardTableList;
        return arenaRankingRewardTableList;
    }

    public List<SkillTable> SkillTableList() {
        skillTableList = skillTableList == null ? skillTableRepository.findAll() : skillTableList;
        return skillTableList;
    }

    public List<ADLimitInfoTable> ADLimitInfoTable() {
        adLimitInfoTable = adLimitInfoTable == null ? adLimitInfoTableRepository.findAll() : adLimitInfoTable;
        return adLimitInfoTable;
    }

    public List<CommonVariableTable> CommonVariableTable() {
        commonVariableTable = commonVariableTable == null ? commonvariableTableRepository.findAll() : commonVariableTable;
        return commonVariableTable;
    }

//    public List<CompanionUserEvaluationTable> CompanionUserEvaluationTable() {
//        companionUserEvaluationTable = companionUserEvaluationTable == null ? companionUserEvaluationRepository.findAll() : companionUserEvaluationTable;
//        return companionUserEvaluationTable;
//    }

    public List<InfiniteTowerRewardsTable> InfiniteTowerRewardsTableList() {
        infiniteTowerRewardsTableList = infiniteTowerRewardsTableList == null ? infiniteTowerRewardsTableRepository.findAll() : infiniteTowerRewardsTableList;
        return infiniteTowerRewardsTableList;
    }

    public List<InfiniteTowerStageTable> InfiniteTowerStageTableList() {
        infiniteTowerStageTableList = infiniteTowerStageTableList == null ? infiniteTowerStageInfoTableRepository.findAll() : infiniteTowerStageTableList;
        return infiniteTowerStageTableList;
    }

    public List<GotchaCharacterTable> GotchaCharacterTableList() {
        gotchaCharacterTableList = gotchaCharacterTableList == null ? gotchaCharacterTableRepository.findAll() : gotchaCharacterTableList;
        return gotchaCharacterTableList;
    }

    public List<CharacterToPieceTable> CharacterToPieceTableList() {
        characterToPieceTableList = characterToPieceTableList == null ?  characterToPieceTableRepository.findAll() : characterToPieceTableList;
        return characterToPieceTableList;
    }

    public List<GotchaEtcTable> GotchaEtcTableList() {
        gotchaEtcTableList = gotchaEtcTableList == null ?  gotchaEtcTableRepository.findAll() : gotchaEtcTableList;
        return gotchaEtcTableList;
    }

    public List<ExchangeItemEventTable> ExchangeItemEventTableList() {
        exchangeItemEventTableList = exchangeItemEventTableList == null ? exchangeItemEventTableRepository.findAll() : exchangeItemEventTableList;
        return exchangeItemEventTableList;
    }

    public List<EternalPassRewardTable> EternalPassRewardTableList() {
        eternalPassRewardTableList = eternalPassRewardTableList == null ?  eternalPassRewardTableRepository.findAll() : eternalPassRewardTableList;
        return eternalPassRewardTableList;
    }

    public List<EternalPassDailyMissionTable> EternalPassDailyMissionTableList() {
        eternalPassDailyMissionTableList = eternalPassDailyMissionTableList == null ? eternalPassDailyMissionTableRepository.findAll() : eternalPassDailyMissionTableList;
        return eternalPassDailyMissionTableList;
    }

    public List<EternalPassWeekMissionTable> EternalPassWeekMissionTableList() {
        eternalPassWeekMissionTablesList = eternalPassWeekMissionTablesList == null ? eternalPassWeekMissionTAbleRepository.findAll() : eternalPassWeekMissionTablesList;
        return eternalPassWeekMissionTablesList;
    }

    public List<EternalPassQuestMissionTable> EternalPassQuestMissionTableList() {
        eternalPassQuestMissionTableList = eternalPassQuestMissionTableList == null ? eternalPassQuestMissionTableRepository.findAll() : eternalPassQuestMissionTableList;
        return eternalPassQuestMissionTableList;
    }

    public List<AchieveEventTable> AchieveEventTableList() {
        achieveEventTableList = achieveEventTableList == null ? achieveEventTableRepository.findAll() : achieveEventTableList;
        return achieveEventTableList;
    }

    public List<MonsterKillEventTable> MonsterKillEventTableList() {
        monsterKillEventTableList = monsterKillEventTableList == null ? monsterKillEventTableRepository.findAll() : monsterKillEventTableList;
        return monsterKillEventTableList;
    }

    public List<ProfileFrameMissionTable> ProfileFrameMissionTableList() {
        profileFrameMissionTableList = profileFrameMissionTableList == null ? profileFrameMissionTableRepository.findAll() : profileFrameMissionTableList;
        return profileFrameMissionTableList;
    }

    public List<ProfileFrameTable> ProfileFrameTableList() {
        profileFrameTableList = profileFrameTableList == null ? profileFrameTableRepository.findAll() : profileFrameTableList;
        return profileFrameTableList;
    }

    public List<AttendanceRewardTable> AttendanceRewardTableList() {
        attendanceRewardTableList = attendanceRewardTableList == null ? attendanceRewardTableRepository.findAll() : attendanceRewardTableList;
        return attendanceRewardTableList;
    }

    public List<IapTable> IapTableList() {
        iapTableList = iapTableList == null ? iapTableRepository.findAll() : iapTableList;
        return iapTableList;
    }

    public List<PackageSpecialShopTable> PackageSpecialShopTableList() {
        packageSpecialShopTableList = packageSpecialShopTableList == null ? packageSpecialShopTableRepository.findAll() : packageSpecialShopTableList;
        return packageSpecialShopTableList;
    }

    public List<PackageDailyShopTable> PackageDailyShopTableList() {
        packageDailyShopTableList = packageDailyShopTableList == null ? packageDailyShopTableRepository.findAll() : packageDailyShopTableList;
        return packageDailyShopTableList;
    }

    public List<PackageWeeklyShopTable> PackageWeeklyShopTableList() {
        packageWeeklyShopTableList = packageWeeklyShopTableList == null ? packageWeeklyShopTableRepository.findAll() : packageWeeklyShopTableList;
        return packageWeeklyShopTableList;
    }

    public List<PackageMonthlyShopTable> PackageMonthlyShopTableList() {
        packageMonthlyShopTableList = packageMonthlyShopTableList == null ? packageMonthlyShopTableRepository.findAll() : packageMonthlyShopTableList;
        return packageMonthlyShopTableList;
    }

    public List<FieldDungeonMonsterWaveInfoTable> FieldDungeonMonsterWaveInfoTableList() {
        fieldDungeonMonsterWaveInfoTableList = fieldDungeonMonsterWaveInfoTableList == null ? fieldDungeonMonsterWaveInfoTableRepository.findAll() : fieldDungeonMonsterWaveInfoTableList;
        return fieldDungeonMonsterWaveInfoTableList;
    }

    public List<FieldDungeonRewardItemTable> FieldDungeonRewardItemTableList() {
        fieldDungeonRewardItemTableList = fieldDungeonRewardItemTableList == null ? fieldDungeonRewardItemTableRepository.findAll() : fieldDungeonRewardItemTableList;
        return fieldDungeonRewardItemTableList;
    }

    public List<FieldDungeonRewardTable> FieldDungeonRewardTableList() {
        fieldDungeonRewardTableList = fieldDungeonRewardTableList == null ? fieldDungeonRewardTableRepository.findAll() : fieldDungeonRewardTableList;
        return fieldDungeonRewardTableList;
    }

    public List<FieldMonsterInfoTable> FieldMonsterInfoTable() {
        fieldMonsterInfoTableList = fieldMonsterInfoTableList == null ? fieldMonsterInfoTableRepository.findAll() : fieldMonsterInfoTableList;
        return fieldMonsterInfoTableList;
    }

    public List<FieldDungeonRewardProbabilityTable> FieldDungeonRewardProbabilityTable() {
        fieldDungeonRewardProbabilityTableList = fieldDungeonRewardProbabilityTableList == null ? fieldDungeonRewardProbabilityTableRepository.findAll() : fieldDungeonRewardProbabilityTableList;
        return fieldDungeonRewardProbabilityTableList;
    }

    public List<FieldDungeonBuyCountTable> FieldDungeonBuyCountTable() {
        fieldDungeonBuyCountTableList = fieldDungeonBuyCountTableList == null ? fieldDungeonBuyCountTableRepository.findAll() : fieldDungeonBuyCountTableList;
        return fieldDungeonBuyCountTableList;
    }

    public Map<String, Object> ResetGameDataTable(Map<String, Object> map) {
        heroEquipmentsTableList = null;
        optionsInfoTableList = null;
        mainHeroSkillExpandInfoList = null;
        spendableItemInfoTableList = null;
        heroEquipmentsPromotionTableList = null;
        equipmentMaterialInfoTableList = null;
        heroEquipmentProductionTableList = null;
        heroEquipmentProductionExpandTableList = null;
        productionSlotOpenCostTableList = null;
        productionMasteryInfoTableList = null;
        giftBoxItemInfoTableList = null;
        stageTableList = null;
        heroTowerStageTableList = null;
        ordealStageTableList = null;
        herosTableList = null;
        giftTableList = null;
        linkforceTalentsTableList = null;
        linkweaponTalentsTableList = null;
        initJsonDatasForFIrstUserList = null;
        legionCostumeTableList = null;
        gotchaStep1TablesList = null;
        gotchaStep2TableList = null;
        ancientDragonStageTableList = null;
        arenaRewardsTableList = null;
        rankingTierTableList = null;
        arenaPlayInfoTableList = null;
        fieldObjectInfoTableList = null;
        expeditionProcessTableList = null;
        mainQuestInfoTableList = null;
        chapterOpenConditionTableList = null;
        stageOpenCondtionTableList = null;
        heroEquipmentClassProbabilityTableList = null;
        quilityResmeltingInfoTableList = null;
        shopTableList = null;
        arenaShopTableList = null;
        ancientShopTableList = null;
        currencyShopTableList = null;
        pieceShopTableList = null;
        darkObeShopTableList = null;
        gotchaTableList = null;
        dailyMissionTableList = null;
        weeklyMissionTableList = null;
        questMissionTableList = null;
        dailyMissionRewardTableList = null;
        weeklyMissionRewardTableList = null;
        passiveItemTableList = null;
        belongingCharacterPieceTableList = null;
        arenaRankingRewardTableList = null;
        skillTableList = null;
        adLimitInfoTable = null;
        commonVariableTable = null;
        //companionUserEvaluationTable = null;
        infiniteTowerRewardsTableList = null;
        infiniteTowerStageTableList = null;
        gotchaCharacterTableList = null;
        characterToPieceTableList = null;
        gotchaEtcTableList = null;
        exchangeItemEventTableList = null;
        eternalPassRewardTableList = null;
        eternalPassDailyMissionTableList = null;
        eternalPassWeekMissionTablesList = null;
        eternalPassQuestMissionTableList = null;
        achieveEventTableList = null;
        monsterKillEventTableList = null;
        profileFrameMissionTableList = null;
        profileFrameTableList = null;
        attendanceRewardTableList = null;

        iapTableList = null;
        packageSpecialShopTableList = null;
        packageDailyShopTableList = null;
        packageWeeklyShopTableList = null;
        packageMonthlyShopTableList = null;
        fieldDungeonMonsterWaveInfoTableList = null;
        fieldDungeonRewardItemTableList = null;
        fieldDungeonRewardTableList = null;
        fieldMonsterInfoTableList = null;
        fieldDungeonRewardProbabilityTableList = null;
        fieldDungeonBuyCountTableList = null;

        HeroEquipmentsTableList();
        OptionsInfoTableList();
        MainHeroSkillExpandInfoList();
        SpendableItemInfoTableList();
        HeroEquipmentsPromotionTableList();
        EquipmentMaterialInfoTableList();
        HeroEquipmentProductionTableList();
        HeroEquipmentProductionExpandTableList();
        ProductionSlotOpenCostTableList();
        ProductionMasteryInfoTableList();
        GiftBoxItemInfoTableList();
        StageTableList();
        HeroTowerStageTableList();
        OrdealStageTableList();
        AncientDragonStageTableList();
        HerosTableList();
        GiftsTableList();
        LinkforceTalentsTableList();
        LinkweaponTalentsTableList();
        InitJsonDatasForFirstUserList();
        CostumeTableList();
        GotchaStep1TableList();
        GotchaStep2TableList();
        ArenaRewardsTableList();
        RankingTierTableList();
        ArenaPlayInfoTableList();
        FieldObjectInfoTableList();
        ExpeditionProcessTableList();
        StageOpenConditionTableList();
        ChapterOpenConditionTableList();
        MainQuestInfoTableList();
        HeroEquipmentClassProbabilityTableList();
        QuilityResmeltingInfoTableList();
        ShopTableList();
        ArenaShopTableList();
        AncientShopTableList();
        CurrencyShopTableList();
        PieceShopTableList();
        DarkObeShopTableList();
        GotchaTableList();
        DailyMissionTableList();
        WeeklyMissionTableList();
        QuestMissionTableList();
        DailyMissionRewardTableList();
        WeeklyMissionRewardTableList();
        PassiveItemTableList();
        BelongingCharacterPieceTableList();
        ArenaRankingRewardTableList();
        SkillTableList();
        ADLimitInfoTable();
        CommonVariableTable();
        //CompanionUserEvaluationTable();
        InfiniteTowerRewardsTableList();
        InfiniteTowerStageTableList();
        GotchaCharacterTableList();
        CharacterToPieceTableList();
        GotchaEtcTableList();
        ExchangeItemEventTableList();
        EternalPassRewardTableList();
        EternalPassDailyMissionTableList();
        EternalPassWeekMissionTableList();
        EternalPassQuestMissionTableList();
        AchieveEventTableList();
        MonsterKillEventTableList();
        ProfileFrameMissionTableList();
        ProfileFrameTableList();
        AttendanceRewardTableList();

        IapTableList();
        PackageSpecialShopTableList();
        PackageDailyShopTableList();
        PackageWeeklyShopTableList();
        PackageMonthlyShopTableList();

        FieldDungeonMonsterWaveInfoTableList();
        FieldDungeonRewardItemTableList();
        FieldDungeonRewardTableList();
        FieldMonsterInfoTable();
        FieldDungeonRewardProbabilityTable();
        FieldDungeonBuyCountTable();

        map.put("heroEquipmentsTableList", heroEquipmentsTableList);
        map.put("optionsInfoTableList", optionsInfoTableList);
        map.put("mainHeroSkillExpandInfoList", mainHeroSkillExpandInfoList);
        map.put("spendableItemInfoTableList", spendableItemInfoTableList);
        map.put("heroEquipmentsPromotionTableList", heroEquipmentsPromotionTableList);
        map.put("equipmentMaterialInfoTableList", equipmentMaterialInfoTableList);
        map.put("heroEquipmentProductionTableList", heroEquipmentProductionTableList);
        map.put("heroEquipmentProductionExpandTableList", heroEquipmentProductionExpandTableList);
        map.put("productionSlotOpenCostTableList", productionSlotOpenCostTableList);
        map.put("productionMasteryInfoTableList", productionMasteryInfoTableList);
        map.put("giftBoxItemInfoTableList", giftBoxItemInfoTableList);
        map.put("stageTableList", stageTableList);
        map.put("heroTowerStageTableList", heroTowerStageTableList);
        map.put("ordealStageTableList", ordealStageTableList);
        map.put("herosTableList", herosTableList);
        map.put("giftTableList", giftTableList);
        map.put("linkforceTalentsTableList", linkforceTalentsTableList);
        map.put("linkweaponTalentsTableList", linkweaponTalentsTableList);
        map.put("initJsonDatasForFIrstUserList", initJsonDatasForFIrstUserList);
        map.put("legionCostumeTableList", legionCostumeTableList);
        map.put("gotchaStep1TablesList", gotchaStep1TablesList);
        map.put("gotchaStep2TableList", gotchaStep2TableList);
        map.put("ancientDragonStageTableList", ancientDragonStageTableList);
        map.put("arenaRewardsTableList", arenaRewardsTableList);
        map.put("rankingTierTableList", rankingTierTableList);
        map.put("arenaPlayInfoTableList", arenaPlayInfoTableList);
        map.put("fieldObjectInfoTableList", fieldObjectInfoTableList);
        map.put("expeditionProcessTableList", expeditionProcessTableList);
        map.put("mainQuestInfoTableList", mainQuestInfoTableList);
        map.put("chapterOpenConditionTableList", chapterOpenConditionTableList);
        map.put("stageOpenCondtionTableList", stageOpenCondtionTableList);
        map.put("heroEquipmentClassProbabilityTableList", heroEquipmentClassProbabilityTableList);
        map.put("quilityResmeltingInfoTableList", quilityResmeltingInfoTableList);
        map.put("shopTableList", shopTableList);
        map.put("arenaShopTableList", arenaShopTableList);
        map.put("ancientShopTableList", ancientShopTableList);
        map.put("currencyShopTableList", currencyShopTableList);
        map.put("pieceShopTableList", pieceShopTableList);
        map.put("darkObeShopTableList", darkObeShopTableList);
        map.put("gotchaTableList", gotchaTableList);
        map.put("dailyMissionTableList", dailyMissionTableList);
        map.put("weeklyMissionTableList", weeklyMissionTableList);
        map.put("questMissionTableList", questMissionTableList);
        map.put("dailyMissionRewardTableList", dailyMissionRewardTableList);
        map.put("weeklyMissionRewardTableList", weeklyMissionRewardTableList);
        map.put("passiveItemTableList", passiveItemTableList);
        map.put("belongingCharacterPieceTableList", belongingCharacterPieceTableList);
        map.put("arenaRankingRewardTableList", arenaRankingRewardTableList);
        map.put("skillTableList", skillTableList);
        map.put("adLimitInfoTable", adLimitInfoTable);
        map.put("commonVariableTable", commonVariableTable);
        //map.put("companionUserEvaluationTable", companionUserEvaluationTable);
        map.put("infiniteTowerRewardsTableList", infiniteTowerRewardsTableList);
        map.put("infiniteTowerStageTableList", infiniteTowerStageTableList);
        map.put("gotchaCharacterTableList", gotchaCharacterTableList);
        map.put("characterToPieceTableList", characterToPieceTableList);
        map.put("gotchaEtcTableList", gotchaEtcTableList);
        map.put("exchangeItemEventTableList", exchangeItemEventTableList);
        map.put("eternalPassRewardTableList", eternalPassRewardTableList);
        map.put("eternalPassDailyMissionTableList", eternalPassDailyMissionTableList);
        map.put("eternalPassWeekMissionTablesList", eternalPassWeekMissionTablesList);
        map.put("eternalPassQuestMissionTableList", eternalPassQuestMissionTableList);
        map.put("achieveEventTableList", achieveEventTableList);
        map.put("monsterKillEventTableList", monsterKillEventTableList);
        map.put("profileFrameMissionTableList", profileFrameMissionTableList);
        map.put("profileFrameTableList", profileFrameTableList);
        map.put("attendanceRewardTableList", attendanceRewardTableList);

        map.put("IapTableList", iapTableList);
        map.put("PackageSpecialShopTableList", packageSpecialShopTableList);
        map.put("PackageDailyShopTableList", packageDailyShopTableList);
        map.put("PackageWeeklyShopTableList", packageWeeklyShopTableList);
        map.put("PackageMonthlyShopTableList", packageMonthlyShopTableList);

        map.put("fieldDungeonMonsterWaveInfoTableList", fieldDungeonMonsterWaveInfoTableList);
        map.put("fieldDungeonRewardItemTableList", fieldDungeonRewardItemTableList);
        map.put("fieldDungeonRewardTableList", fieldDungeonRewardTableList);
        map.put("fieldMonsterInfoTableList", fieldMonsterInfoTableList);
        map.put("fieldDungeonRewardProbabilityTableList", fieldDungeonRewardProbabilityTableList);
        map.put("fieldDungeonBuyCount", fieldDungeonBuyCountTableList);
        return map;
    }
}
