package com.onlyonegames.eternalfantasia.domain.service.Managementtool.CharactersInfo;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.controller.managementtool.CostumeDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ChapterSaveDto.ChapterSaveData;
import com.onlyonegames.eternalfantasia.domain.model.dto.CostumeDto.CostumeDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.Event.AchieveEventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceDto.LinkforceOpenDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceDto.LinkforceOpenDtosList.CompanionLinkforceOpenInfo;
import com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceWeaponDto.LinkweaponInfoDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceWeaponDto.LinkweaponInfoDtosList.CompanionLinkweaponInfo;
import com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceWeaponDto.LinkweaponInfoDtosList.LinkweaponInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.*;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.MyCharactersDto.OpenInfo;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.TavernVisitCompanionInfoData.TavernVisitCompanionInfoDataList;
import com.onlyonegames.eternalfantasia.domain.model.dto.TavernVisitCompanionInfoData.TavernVisitCompanionInfoDataList.TavernVisitCompanionInfoData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyCostumeInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyLinkforceInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyLinkweaponInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyVisitCompanionInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyAncientDragonExpandSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyHeroTowerExpandSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyOrdealDungeonExpandSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Event.MyAchieveEventMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyCostumeInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyLinkforceInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyLinkweaponInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyVisitCompanionInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyAncientDragonExpandSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyHeroTowerExpandSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyOrdealDungeonExpandSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Event.MyAchieveEventMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.Managementtool.HeroInfo.HeroCalculate;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.util.MathHelper;
import com.onlyonegames.util.StringMaker;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyCharactersService {
    private final MyCharactersRepository myCharactersRepository;
    private final GameDataTableService gameDataTableService;
    private final MyCostumeInventoryRepository myCostumeInventoryRepository;
    private final MyLinkweaponInfoRepository myLinkweaponInfoRepository;
    private final MyLinkforceInfoRepository myLinkforceInfoRepository;
    private final MyVisitCompanionInfoRepository myVisitCompanionInfoRepository;
    private final LegionCostumeTableRepository legionCostumeTableRepository;
    private final ErrorLoggingService errorLoggingService;
    private final MyHeroTowerExpandSaveDataRepository myHeroTowerExpandSaveDataRepository;
    private final MyOrdealDungeonExpandSaveDataRepository myOrdealDungeonExpandSaveDataRepository;
    private final MyAncientDragonExpandSaveDataRepository myAncientDragonExpandSaveDataRepository;
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final MyAchieveEventMissionsDataRepository myAchieveEventMissionsDataRepository;

    public Map<String, Object> findUsersHero(Long userId, Map<String, Object> map) {
        List<MyCharacters> myCharacters = myCharactersRepository.findAllByuseridUser(userId);
        if(myCharacters.size() == 0){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyCharacters not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyCharacters not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<herostable> herostables = gameDataTableService.HerosTableList();

        MyVisitCompanionInfo myVisitCompanionInfo = myVisitCompanionInfoRepository.findByUseridUser(userId)
                .orElse(null);
        if(myVisitCompanionInfo == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyVisitCompanionInfo not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyVisitCompanionInfo not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        TavernVisitCompanionInfoDataList tavernVisitCompanionInfoDataList = JsonStringHerlper.ReadValueFromJson(myVisitCompanionInfo.getVisitCompanionInfo(), TavernVisitCompanionInfoDataList.class);

        List<MyCharactersDto> myCharactersDtos = new ArrayList<>();
        List<MyCharactersDto.VisitCompanionInfo> visitCompanionInfoList = new ArrayList<>();

        for (MyCharacters myCharacter : myCharacters) {

            MyCharactersDto myCharactersDto = new MyCharactersDto();

            herostable herostable = herostables.stream()
                    .filter(e -> e.getCode().equals(myCharacter.getCodeHerostable()))
                    .findAny()
                    .orElse(null);
            if(herostable == null){
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: not find userId.", ResponseErrorCode.NOT_FIND_DATA);
            }
            String name = herostable.getName();


            myCharactersDto.setMyCharacters(name, myCharacter.getId(), myCharacter.getFatigability(),
                    myCharacter.getLevel(), myCharacter.getExp(), myCharacter.getLinkAbilityLevel(),
                    myCharacter.isGotcha(), null, null, null, null);
            if(myCharactersDto.getName().equals("영웅"))
                continue;
            myCharactersDtos.add(myCharactersDto);
        }


        MyCharactersDto.TavernVisitCompanionInfo tavernVisitCompanionInfo = new MyCharactersDto.TavernVisitCompanionInfo();
        for (TavernVisitCompanionInfoData tavernVisitCompanionInfoData : tavernVisitCompanionInfoDataList.visitList) {
            MyCharactersDto.VisitCompanionInfo visitCompanionInfo = new MyCharactersDto.VisitCompanionInfo();
            visitCompanionInfo.setVisitCompanionInfo(tavernVisitCompanionInfoData.characterName, tavernVisitCompanionInfoData.linkGaugePercent, tavernVisitCompanionInfoData.recruited);
            visitCompanionInfoList.add(visitCompanionInfo);
        }
        tavernVisitCompanionInfo.setTavernVisitCompanionInfo(visitCompanionInfoList, myVisitCompanionInfo.getVisitScheduleStartTime());

        map.put("MyCharacters", myCharactersDtos);
        map.put("VisitCompanions", tavernVisitCompanionInfo);
        return map;
    }

    public Map<String, Object> findHeroInfo(Long userId, Long characterId, Map<String, Object> map){
        MyCharactersDto myCharactersDto = new MyCharactersDto();
        MyCharacters myCharacter = myCharactersRepository.findById(characterId)
                .orElse(null);
        if(myCharacter == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyCharacters not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyCharacters not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<herostable> herostables = gameDataTableService.HerosTableList();
        List<LinkweaponTalentsTable> linkweaponTalentsTableList = gameDataTableService.LinkweaponTalentsTableList().stream()
                .filter(i -> i.getOwner().equals(myCharacter.getCodeHerostable()))
                .collect(Collectors.toList());
        MyCostumeInventory myCostumeInventory = myCostumeInventoryRepository.findByUseridUser(userId)
                .orElse(null);
        if(myCostumeInventory == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: myCostumeInventory not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myCostumeInventory not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyLinkweaponInfo myLinkweaponInfo = myLinkweaponInfoRepository.findByUseridUser(userId)
                .orElse(null);
        if(myLinkweaponInfo == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: myLinkweaponInfo not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myLinkweaponInfo not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyLinkforceInfo myLinkforceInfo = myLinkforceInfoRepository.findByUseridUser(userId)
                .orElse(null);
        if(myLinkforceInfo == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: myLinkforceInfo not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myLinkforceInfo not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<LinkforceTalentsTable> linkforceTalentsTables = gameDataTableService.LinkforceTalentsTableList();
        List<LegionCostumeTable> legionCostumeTableList = gameDataTableService.CostumeTableList().stream()
                .filter(i -> i.getOwnerCode().equals(myCharacter.getCodeHerostable()))
                .collect(Collectors.toList());
        CostumeDtosList costumeDtosList = JsonStringHerlper.ReadValueFromJson(myCostumeInventory.getJson_CostumeInventory(), CostumeDtosList.class);
        LinkweaponInfoDtosList linkweaponInfoDtosList = JsonStringHerlper.ReadValueFromJson(myLinkweaponInfo.getJson_LinkweaponRevolution(), LinkweaponInfoDtosList.class);
        LinkforceOpenDtosList myLinkforceDtosList = JsonStringHerlper.ReadValueFromJson(myLinkforceInfo.getJson_LinkforceInfos(), LinkforceOpenDtosList.class);

        List<CompanionLinkforceOpenInfo> test = myLinkforceDtosList.openInfoList;
        List<CostumeDto> costumeDtoList = new ArrayList<>();
        List<LinkforceTalentsTable> linkforceTemp = new ArrayList<>();
        List<String> linkforceMaxNumber = Arrays.asList("/17","/28","/30","/28","/28","/34");
        List<OpenInfo> openInfos = new ArrayList<>();
        List<MyCharactersDto.CompanionWeaponInfo> companionWeaponInfoList = new ArrayList<>();

        herostable herostable = herostables.stream()
                .filter(e -> e.getCode().equals(myCharacter.getCodeHerostable()))
                .findAny()
                .orElse(null);
        if(herostable == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String name = herostable.getName();
        //동료 코스튬
        int a = Integer.parseInt(myCharacter.getCodeHerostable().substring(4,6));
        for(CostumeDtosList.CostumeDto temp:costumeDtosList.hasCostumeIdList) { // int a HeorCode String indexing 해서 구하기
            for(LegionCostumeTable legionCostumeTable: legionCostumeTableList) {
                if(temp.costumeId==legionCostumeTable.getId()) {
                    CostumeDto costumeDto = new CostumeDto();
                    costumeDto.setCostumeDto(temp.costumeId, legionCostumeTable.getName(), temp.isEquip, temp.hasBuy);
                    costumeDtoList.add(costumeDto);
                }
            }
        }

        CompanionLinkforceOpenInfo companionLinkforceOpenInfo = test.stream().filter(i -> i.code.equals(myCharacter.getCodeHerostable()))
                .findAny()
                .orElse(null);
        if(companionLinkforceOpenInfo == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: CompanionLinkforceOpenInfo not find Code.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: CompanionLinkforceOpenInfo not find Code.", ResponseErrorCode.NOT_FIND_DATA);
        }
        CompanionLinkweaponInfo companionLinkweaponInfo = linkweaponInfoDtosList.companionLinkweaponInfoList
                .stream()
                .filter(i -> i.ownerCode.equals(myCharacter.getCodeHerostable()))
                .findAny()
                .orElse(null);
        if(companionLinkweaponInfo == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: CompanionLinkweaponInfo not find CompanionCode.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: CompanionLinkweaponInfo not find CompanionCode.", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<LinkforceTalentsTable> linkforceTalentsTableList = linkforceTalentsTables.stream()
                .filter(i -> i.getOwner().equals(myCharacter.getCodeHerostable()))
                .collect(Collectors.toList());
        //동료 링크웨폰
        for(LinkweaponInfoDto linkweaponInfoDto:companionLinkweaponInfo.linkweaponInfoDtoList){
            MyCharactersDto.CompanionWeaponInfo companionWeaponInfo = new MyCharactersDto.CompanionWeaponInfo();
            LinkweaponTalentsTable linkweaponTalentsTable = linkweaponTalentsTableList.stream()
                    .filter(i-> i.getID() == linkweaponInfoDto.id)
                    .findAny()
                    .orElse(null);
            if(linkweaponTalentsTable == null){
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: LinkweaponInfoDtosList not find CompanionCode.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: LinkweaponInfoDtosList not find CompanionCode.", ResponseErrorCode.NOT_FIND_DATA);
            }
            String weaponName = linkweaponTalentsTable.getName();
            companionWeaponInfo.setCompanionWeaponInfo(linkweaponTalentsTable.getName(), linkweaponInfoDto.open, linkweaponInfoDto.upgrade);
            companionWeaponInfoList.add(companionWeaponInfo);
        }
        List<MyCharactersDto.CompanionWeaponInfo> companionWeaponSelectedInfoList = new ArrayList<>();

        for(Integer temp: companionLinkweaponInfo.selectedIds){
            MyCharactersDto.CompanionWeaponInfo companionWeaponInfo = new MyCharactersDto.CompanionWeaponInfo();
            LinkweaponInfoDto linkweaponInfoDto = companionLinkweaponInfo.linkweaponInfoDtoList
                    .stream()
                    .filter(i -> i.id==temp)
                    .findAny()
                    .orElse(null);
            if(linkweaponInfoDto == null){
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: LinkweaponInfoDtosList not find LinkweaponId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: LinkweaponInfoDtosList not find LinkweaponId.", ResponseErrorCode.NOT_FIND_DATA);
            }
            LinkweaponTalentsTable linkweaponTalentsTable = linkweaponTalentsTableList.stream()
                    .filter(i-> i.getID() == temp)
                    .findAny()
                    .orElse(null);
            if(linkweaponTalentsTable == null){
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: LinkweaponInfoDtosList not find CompanionCode.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: LinkweaponInfoDtosList not find CompanionCode.", ResponseErrorCode.NOT_FIND_DATA);
            }
            String weaponName = linkweaponTalentsTable.getName();
            companionWeaponInfo.setCompanionWeaponInfo(linkweaponTalentsTable.getName(), linkweaponInfoDto.open, linkweaponInfoDto.upgrade);
            companionWeaponSelectedInfoList.add(companionWeaponInfo);
        }
        //동료 링크포스
        for(int i = 0; i<companionLinkforceOpenInfo.linkforceOpenInfoList.size() ; i++){
            if(companionLinkforceOpenInfo.linkforceOpenInfoList.get(i) == 1){
                linkforceTemp.add(linkforceTalentsTableList.get(i));
            }
        }
        for(int i = 1; i<7; i++) {
            OpenInfo openinfo = new OpenInfo();
            openinfo.setOpenInfo(Integer.toString(getOpenLinkforceListSize(i, linkforceTemp))+linkforceMaxNumber.get(i-1));
            openInfos.add(openinfo);
        }
        myCharactersDto.setMyCharacters(name, myCharacter.getId(), myCharacter.getFatigability(),
                myCharacter.getLevel(), myCharacter.getExp(), myCharacter.getLinkAbilityLevel(),
                myCharacter.isGotcha(),costumeDtoList, companionWeaponSelectedInfoList,companionWeaponInfoList, openInfos);

        map.put("MyCharacter", myCharactersDto);
        return map;
    }

    public Map<String, Object> characterStatus(Long userId, Long characterId, Map<String, Object> map){
        //기본 성장 능력치를 위한 데이터
        MyCharacters myCharacter = myCharactersRepository.findById(characterId)
                .orElse(null);
        if(myCharacter == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyCharacters not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyCharacters not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        herostable herostable = gameDataTableService.HerosTableList()
                .stream()
                .filter(i -> i.getCode().equals(myCharacter.getCodeHerostable()))
                .findAny()
                .orElse(null);
        if(herostable == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: herostable not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: herostable not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        //Option 정보
        List<EquipmentOptionsInfoTable> equipmentOptionsInfoTables = gameDataTableService.OptionsInfoTableList();

        //Linkforce 능력치를 위한 데이터
        List<LinkforceTalentsTable> linkforceTalentsTableList = gameDataTableService.LinkforceTalentsTableList().stream()
                .filter(i -> i.getOwner().equals(myCharacter.getCodeHerostable()))
                .collect(Collectors.toList());
        MyLinkforceInfo myLinkforceInfo = myLinkforceInfoRepository.findByUseridUser(userId)
                .orElse(null);
        if(myLinkforceInfo == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyLinkforceInfo not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyLinkforceInfo not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        LinkforceOpenDtosList linkforceOpenDtosList = JsonStringHerlper.ReadValueFromJson(myLinkforceInfo.getJson_LinkforceInfos(), LinkforceOpenDtosList.class);
        CompanionLinkforceOpenInfo companionLinkforceOpenInfo = linkforceOpenDtosList.openInfoList.stream().filter(i -> i.code.equals(myCharacter.getCodeHerostable()))
                .findAny()
                .orElse(null);
        if(companionLinkforceOpenInfo == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: LinkforceOpenDtos not find HeroCode.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: LinkforceOpenDtos not find HeroCode.", ResponseErrorCode.NOT_FIND_DATA);
        }

        //Costume 정보
        List<LegionCostumeTable> legionCostumeTableList = gameDataTableService.CostumeTableList().stream()
                .filter(i -> i.getOwnerCode().equals(myCharacter.getCodeHerostable()))
                .collect(Collectors.toList());
        MyCostumeInventory myCostumeInventory = myCostumeInventoryRepository.findByUseridUser(userId)
                .orElse(null);
        if(myCostumeInventory == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: myCostumeInventory not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myCostumeInventory not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        CostumeDtosList costumeDtosList = JsonStringHerlper.ReadValueFromJson(myCostumeInventory.getJson_CostumeInventory(), CostumeDtosList.class);
        CostumeDtosList.CostumeDto costumeDto = new CostumeDtosList.CostumeDto();
        int a = Integer.parseInt(myCharacter.getCodeHerostable().substring(4,6));
        for(CostumeDtosList.CostumeDto costumeDtos:costumeDtosList.hasCostumeIdList) { // int a HeorCode String indexing 해서 구하기
            for(LegionCostumeTable legionCostumeTable: legionCostumeTableList) {
                if(costumeDtos.costumeId==legionCostumeTable.getId() && costumeDtos.isEquip) {
                    costumeDto = costumeDtos;
                }
            }
        }
        LegionCostumeTable legionCostumeTable = legionCostumeTableRepository.findById(costumeDto.costumeId)
                .orElse(null);
        if(legionCostumeTable == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: LegionCostumeTable not find costumeId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: LegionCostumeTable not find costumeId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        //LinkWeapon 정보
        List<LinkweaponTalentsTable> linkweaponTalentsTableList = gameDataTableService.LinkweaponTalentsTableList()
                .stream()
                .filter(i -> i.getOwner().equals(myCharacter.getCodeHerostable()))
                .collect(Collectors.toList());
        MyLinkweaponInfo myLinkweaponInfo = myLinkweaponInfoRepository.findByUseridUser(userId)
                .orElse(null);
        if(myLinkweaponInfo == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyLinkweaponInfo not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyLinkweaponInfo not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        LinkweaponInfoDtosList linkweaponInfoDtosList = JsonStringHerlper.ReadValueFromJson(myLinkweaponInfo.getJson_LinkweaponRevolution(), LinkweaponInfoDtosList.class);
        CompanionLinkweaponInfo companionLinkweaponInfo = linkweaponInfoDtosList.companionLinkweaponInfoList.stream().filter(i -> i.ownerCode.equals(myCharacter.getCodeHerostable()))
                .findAny()
                .orElse(null);
        if(companionLinkweaponInfo == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: LinkweaponInfoDtosList not find HeroCode.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: LinkweaponInfoDtosList not find HeroCode.", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<LinkweaponInfoDto> linkweaponInfoDtoList = new ArrayList<>();
        List<LinkweaponTalentsTable> linkweaponTalentsTables = new ArrayList<>();
        for(Integer temp:companionLinkweaponInfo.selectedIds){
            linkweaponInfoDtoList.add(companionLinkweaponInfo.linkweaponInfoDtoList.get(temp));
            LinkweaponTalentsTable linkweaponTalentsTable = linkweaponTalentsTableList.stream().filter(i -> temp.equals(i.getID()))
                    .findAny()
                    .orElse(null);
            if(linkweaponTalentsTable == null){
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: LinkweaponTalentsTableList not find EquipmentID.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: LinkweaponTalentsTableList not find EquipmentID.", ResponseErrorCode.NOT_FIND_DATA);
            }
            linkweaponTalentsTables.add(linkweaponTalentsTable);
        }

        HeroStatusDto calculatedStatus = new HeroStatusDto();
        HeroStatusDto growthStatus = new HeroStatusDto();
        HeroCalculate.CalculateStatusFromCharacter(myCharacter.getLevel(),myCharacter.getFatigability(),growthStatus,calculatedStatus,herostable,equipmentOptionsInfoTables,legionCostumeTable,linkforceTalentsTableList,companionLinkforceOpenInfo, linkweaponTalentsTables, linkweaponInfoDtoList);
        calculatedStatus.setLevel(myCharacter.getLevel(), herostable);
        calculatedStatus.setDPS();
        map.put("CharacterStatus", calculatedStatus);
        return map;
    }

    public Map<String, Object> setVisitCompanion(SetVisitCompanionDto dto, Map<String, Object> map){
        MyVisitCompanionInfo myVisitCompanionInfo = myVisitCompanionInfoRepository.findByUseridUser(dto.getUserId())
                .orElse(null);
        if(myVisitCompanionInfo == null){
            errorLoggingService.SetErrorLog(dto.getUserId(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyVisitCompanionInfo not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyVisitCompanionInfo not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        TavernVisitCompanionInfoDataList tavernVisitCompanionInfoDataList = JsonStringHerlper.ReadValueFromJson(myVisitCompanionInfo.getVisitCompanionInfo(), TavernVisitCompanionInfoDataList.class);
        for(int i = 0; i < tavernVisitCompanionInfoDataList.visitList.size(); i++){
            if(!tavernVisitCompanionInfoDataList.visitList.get(i).code.equals(dto.getCompanionList().get(i).code) || (tavernVisitCompanionInfoDataList.visitList.get(i).linkGaugePercent != dto.getCompanionList().get(i).linkGaugePercent)){
                tavernVisitCompanionInfoDataList.visitList.get(i).ChangeCompanion(dto.getCompanionList().get(i).code, dto.getCompanionList().get(i).characterName, dto.getCompanionList().get(i).linkGaugePercent);
            }
        }
        String tavernVisitCompanionInfoData = JsonStringHerlper.WriteValueAsStringFromData(tavernVisitCompanionInfoDataList);
        myVisitCompanionInfo.ResetVisitCompanionInfo(tavernVisitCompanionInfoData);
        map.put("MyVisitCompanionInfo",myVisitCompanionInfo);
        return map;

    }

    public Map<String, Object> setCompanionInfo(SetCompanionStatusDto dto, Map<String, Object> map) {
        MyCharacters myCharacters = null;
        boolean changedMissionsData = false;
        boolean changedAchieveEventMissionsData = false;
        /*업적 : 체크 준비*/
        MyMissionsData myMissionsData = myMissionsDataRepository.findByUseridUser(dto.getUserId())
                .orElse(null);
        if(myMissionsData == null) {
            errorLoggingService.SetErrorLog(dto.getUserId(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyMissionData = myMissionsData.getJson_saveDataValue();
        MissionsDataDto myMissionsDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyMissionData, MissionsDataDto.class);
        /*누적 업적 : 체크 준비*/
        MyAchieveEventMissionsData myAchieveEventMissionsData = myAchieveEventMissionsDataRepository.findByUseridUser(dto.getUserId())
                .orElse(null);
        if(myAchieveEventMissionsData == null) {
            errorLoggingService.SetErrorLog(dto.getUserId(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyAchieveEventMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyAchieveEventMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyAchieveEventMissionData = myAchieveEventMissionsData.getJson_saveDataValue();
        AchieveEventMissionDataDto myAchieveEventMissionDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyAchieveEventMissionData, AchieveEventMissionDataDto.class);
        if(dto.getCharacterCode()!=null){
            myCharacters = myCharactersRepository.findByUseridUserAndCodeHerostable(dto.getUserId(),"hero")
                    .orElse(null);
            if (myCharacters == null) {
                errorLoggingService.SetErrorLog(dto.getUserId(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyCharacters not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: MyCharacters not find userId.", ResponseErrorCode.NOT_FIND_DATA);
            }
        } else {
            myCharacters = myCharactersRepository.findById(dto.getCharacterId())
                    .orElse(null);
            if(myCharacters == null){
                errorLoggingService.SetErrorLog(dto.getUserId(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyCharacters not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: MyCharacters not find userId.", ResponseErrorCode.NOT_FIND_DATA);
            }
        }
//        if(myCharacters.getExp() != dto.getExp() && myCharacters.getLevel() == dto.getLevel()){
//            int tempExp = dto.getExp() - myCharacters.getExp();
//            if(!myCharacters.AddExp(tempExp,myCharacters.getMaxLevel())) {
//                errorLoggingService.SetErrorLog(dto.getUserId(), ResponseErrorCode.CANT_MORE_LEVELUP.getIntegerValue(), "[ManagementTool] Fail! -> Cause: Can't more levelup", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
//                throw new MyCustomException("Fail! -> Cause: Can't more levelup", ResponseErrorCode.CANT_MORE_LEVELUP);
//            }
//            if(myCharacters.getFatigability() != dto.getFatigability()) {
//                myCharacters.SetFatigability(dto.getFatigability());
//            }
//        } else
        if(myCharacters.getLevel() != dto.getLevel() && dto.getLevel() != 0){
            if(myCharacters.getMaxLevel() < dto.getLevel()){
                errorLoggingService.SetErrorLog(dto.getUserId(), ResponseErrorCode.CANT_MORE_LEVELUP.getIntegerValue(), "[ManagementTool] Fail! -> Cause: Can't more levelup", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Can't more levelup", ResponseErrorCode.CANT_MORE_LEVELUP);
            }
            int tempExp = GetNextLevelUpExpForLv(dto.getLevel() - 1) - myCharacters.getExp();
            int priveusLevel = myCharacters.getLevel();
            if(!myCharacters.AddExp(tempExp,myCharacters.getMaxLevel())) {
                errorLoggingService.SetErrorLog(dto.getUserId(), ResponseErrorCode.CANT_MORE_LEVELUP.getIntegerValue(), "[ManagementTool] Fail! -> Cause: Can't more levelup", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Can't more levelup", ResponseErrorCode.CANT_MORE_LEVELUP);
            }
            if(priveusLevel < myCharacters.getLevel() && myCharacters.getCodeHerostable().equals("hero")) {
                OpenDungeon(dto.getUserId(), myCharacters.getLevel());
                for(int i = priveusLevel+1; i <=myCharacters.getLevel(); i++){
                    StringMaker.Clear();
                    StringMaker.stringBuilder.append(i);
                    StringMaker.stringBuilder.append(" 달성");
                    String levelParameter = StringMaker.stringBuilder.toString();
                    changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.LEVEL_UP_MAIN_HERO.name(), levelParameter, gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                    changedAchieveEventMissionsData = myAchieveEventMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.LEVEL_UP_MAIN_HERO.name(), levelParameter, gameDataTableService.AchieveEventTableList()) || changedAchieveEventMissionsData;
                }
            }
        }
        if(myCharacters.getFatigability() != dto.getFatigability() && dto.getFatigability() != 0) {//TODO 운영툴에서 피로도를 0으로 변경할 수 있도록 수정 필요
            myCharacters.SetFatigability(dto.getFatigability());
        }
        if(dto.getCharacterCode() != null){
            List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(dto.getUserId());
            for(MyCharacters temp: myCharactersList){
                if(temp.getLevel()>dto.getLevel()){
                    int tempExp = GetNextLevelUpExpForLv(dto.getLevel() - 1) - temp.getExp();
                    if(!temp.AddExp(tempExp,temp.getMaxLevel())) {
                        errorLoggingService.SetErrorLog(dto.getUserId(), ResponseErrorCode.CANT_MORE_LEVELUP.getIntegerValue(), "[ManagementTool] Fail! -> Cause: Can't more levelup", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Can't more levelup", ResponseErrorCode.CANT_MORE_LEVELUP);
                    }
                }
                if(!temp.getCodeHerostable().equals("hero"))
                    temp.setMaxLevel(dto.getLevel());
            }
        }
        /*업적 : 미션 데이터 변경점 적용*/
        if(changedMissionsData) {
            jsonMyMissionData = JsonStringHerlper.WriteValueAsStringFromData(myMissionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMyMissionData);
        }
        /* 누적 업적 : 미션 데이터 변경점 적용*/
        if(changedAchieveEventMissionsData) {
            jsonMyAchieveEventMissionData = JsonStringHerlper.WriteValueAsStringFromData(myAchieveEventMissionDataDto);
            myAchieveEventMissionsData.ResetSaveDataValue(jsonMyAchieveEventMissionData);
        }
        map.put("MyCharacter", myCharacters);
        return map;
    }

    public Map<String, Object> setCompanionLinkAbility(LinkAbilityDto dto, Map<String, Object> map) {
        MyCharacters myCharacters = myCharactersRepository.findByUseridUserAndCodeHerostable(dto.getUserId(), dto.getHeroCode())
                .orElse(null);
        if(myCharacters == null){
            errorLoggingService.SetErrorLog(dto.getUserId(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyCharacters not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyCharacters not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        if(dto.getLinkAbilityLevel() != myCharacters.getLinkAbilityLevel()) {
            myCharacters.SetLinkAbilityLv(dto.getLinkAbilityLevel());
        }
        map.put("MyCharater", myCharacters);
        return map;
    }

    public Map<String, Object> setCompanionCostume(SetCompanionCostumeDto dto, Map<String, Object> map) {
        MyCharacters myCharacter = myCharactersRepository.findByUseridUserAndCodeHerostable(dto.getUserId(), dto.getHeroCode())
                .orElse(null);
        if(myCharacter == null){
            errorLoggingService.SetErrorLog(dto.getUserId(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyCharacters not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyCharacters not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyCostumeInventory myCostumeInventory = myCostumeInventoryRepository.findByUseridUser(dto.getUserId())
                .orElse(null);
        if(myCostumeInventory == null){
            errorLoggingService.SetErrorLog(dto.getUserId(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: myCostumeInventory not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myCostumeInventory not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        CostumeDtosList costumeDtosList = JsonStringHerlper.ReadValueFromJson(myCostumeInventory.getJson_CostumeInventory(), CostumeDtosList.class);
        List<CostumeDtosList.CostumeDto> costumeDtoList = new ArrayList<>();

        int a = Integer.parseInt(myCharacter.getCodeHerostable().substring(4,6));
        for(CostumeDtosList.CostumeDto costumeDto:costumeDtosList.hasCostumeIdList) { // int a HeorCode String indexing 해서 구하기
            if (a == ((costumeDto.costumeId - 1) / 4)) {
                costumeDtoList.add(costumeDto);
            }
        }
        int flag = 0;
        for(int i = 0; i < 3; i++){
            if(!dto.getCostumeHasBuy().get(i).equals(costumeDtosList.hasCostumeIdList.get((a*4+1)+i).hasBuy)) {
                costumeDtosList.hasCostumeIdList.get((a * 4 + 1) + i).ChangeHasBuy(dto.getCostumeHasBuy().get(i));
                flag = 1;
            }
        }
        if(flag == 1) {
            String costumeDto = JsonStringHerlper.WriteValueAsStringFromData(costumeDtosList);
            myCostumeInventory.ResetCostumeInventory(costumeDto);
        }
        map.put("CostumeInfo", costumeDtoList);
        return map;
    }

    public Map<String, Object> setLinkWeapon(SetLinkWeaponDto dto, Map<String, Object> map) {
        MyLinkweaponInfo myLinkweaponInfo = myLinkweaponInfoRepository.findByUseridUser(dto.getUserId())
                .orElse(null);
        if(myLinkweaponInfo == null){
            errorLoggingService.SetErrorLog(dto.getUserId(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: myLinkweaponInfo not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myLinkweaponInfo not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        LinkweaponInfoDtosList linkweaponInfoDtosList = JsonStringHerlper.ReadValueFromJson(myLinkweaponInfo.getJson_LinkweaponRevolution(), LinkweaponInfoDtosList.class);
        CompanionLinkweaponInfo companionLinkweaponInfo = linkweaponInfoDtosList.companionLinkweaponInfoList
                .stream()
                .filter(i -> i.ownerCode.equals(dto.getHeroCode()))
                .findAny()
                .orElse(null);
        if(companionLinkweaponInfo == null){
            errorLoggingService.SetErrorLog(dto.getUserId(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: not find CompanionCode.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: not find CompanionCode.", ResponseErrorCode.NOT_FIND_DATA);
        }
        if(!companionLinkweaponInfo.selectedIds.equals(dto.getSelectedIds())) {
            for(int i = 1; i < companionLinkweaponInfo.selectedIds.size(); i++){
                companionLinkweaponInfo.linkweaponInfoDtoList.get(companionLinkweaponInfo.selectedIds.get(i)).open = false;
            }
            for(int i = 0; i < dto.getSelectedIds().size(); i++){
                companionLinkweaponInfo.linkweaponInfoDtoList.get(dto.getSelectedIds().get(i)).open = true;
                if(i != dto.getSelectedIds().size() - 1)
                    companionLinkweaponInfo.linkweaponInfoDtoList.get(dto.getSelectedIds().get(i)).upgrade = 5;
                else
                    companionLinkweaponInfo.linkweaponInfoDtoList.get(dto.getSelectedIds().get(i)).upgrade = dto.getUpgrade();
            }
            companionLinkweaponInfo.selectedIds = dto.getSelectedIds();
            companionLinkweaponInfo.strengthenId = dto.getSelectedIds().get(dto.getSelectedIds().size()-1);
        }
        String jsonData = JsonStringHerlper.WriteValueAsStringFromData(linkweaponInfoDtosList);
        myLinkweaponInfo.ResetLinkweaponRevolution(jsonData);
        map.put("Test", companionLinkweaponInfo);
        return map;
    }

    int getOpenLinkforceListSize(int GFCondition, List<LinkforceTalentsTable> linkforceTemp){
        List<LinkforceTalentsTable> temp = linkforceTemp.stream()
                .filter(i -> i.getGFCondition() == GFCondition)
                .collect(Collectors.toList());
        return temp.size();
    }

    int GetNextLevelUpExpForLv(int nowLv) {
        if (nowLv == 100)
            nowLv--;
        int tempLv = nowLv;
        int resultExp = 0;
        while (tempLv >= 1)
        {
            resultExp += CalculateExp(tempLv);
            tempLv--;
        }

        return nowLv >= 1 ? resultExp : 0;
    }

    int CalculateExp(int nowLv) {
        if (nowLv <= 0)
            return 0;
        float result = (float) MathHelper.RoundUPMinus1((100 + ((nowLv - 1) * 10) +(nowLv * (nowLv - 1)) * 2));
        return (int)result;
    }

    void OpenDungeon(Long userId, int afterLevel){
        MyHeroTowerExpandSaveData myHeroTowerExpandSaveData = myHeroTowerExpandSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myHeroTowerExpandSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyHeroTowerExpandSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyHeroTowerExpandSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        MyOrdealDungeonExpandSaveData myOrdealDungeonExpandSaveData = myOrdealDungeonExpandSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myOrdealDungeonExpandSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyOrdealDungeonExpandSaveDataRepository not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyOrdealDungeonExpandSaveDataRepository not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        MyAncientDragonExpandSaveData myAncientDragonExpandSaveData = myAncientDragonExpandSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myAncientDragonExpandSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyOrdealDungeonExpandSaveDataRepository not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyOrdealDungeonExpandSaveDataRepository not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        int checkOpenLevel = 1;
        while(true) {
            int finalCheckOpenLevel = checkOpenLevel;
            int previousCheckOpenLevel = finalCheckOpenLevel - 1;
            HeroTowerStageTable openableHeroTowerStageInfo = gameDataTableService.HeroTowerStageTableList().stream()
                    .filter(a -> a.getOpenLevel() <= finalCheckOpenLevel && a.getOpenLevel() > previousCheckOpenLevel)
                    .findAny()
                    .orElse(null);
            if(openableHeroTowerStageInfo != null) {
                String heroTowerChapterAndStageNoString = openableHeroTowerStageInfo.getStage();
                String[] heroTowerChapterAndStageNoStringArray =  heroTowerChapterAndStageNoString.split("-");
                int heroTowerStageNo = Integer.parseInt(heroTowerChapterAndStageNoStringArray[1]);


                String json_heroTowerSaveData = myHeroTowerExpandSaveData.getJson_saveDataValue();
                ChapterSaveData heroTowerChapterSaveData = JsonStringHerlper.ReadValueFromJson(json_heroTowerSaveData, ChapterSaveData.class);
                ChapterSaveData.ChapterPlayInfo heroTowerExpandChapterPlayInfo = heroTowerChapterSaveData.chapterData.chapterPlayInfosList.get(0);

                for(ChapterSaveData.StagePlayInfo heroTowerExpandStagePlayInfo : heroTowerExpandChapterPlayInfo.stagePlayInfosList) {
                    if(heroTowerExpandStagePlayInfo.stageNo <= heroTowerStageNo) {
                        heroTowerExpandStagePlayInfo.isOpend = true;
                    }
                }
                json_heroTowerSaveData = JsonStringHerlper.WriteValueAsStringFromData(heroTowerChapterSaveData);
                myHeroTowerExpandSaveData.ResetSaveDataValue(json_heroTowerSaveData);
            }
            //시련의 탑 각 스테이지 오픈 조건 레벨 체크
            OrdealStageTable openableOrdealStageInfo = gameDataTableService.OrdealStageTableList().stream()
                    .filter(a -> a.getOpenLevel() <= finalCheckOpenLevel && a.getOpenLevel() > previousCheckOpenLevel)
                    .findAny()
                    .orElse(null);
            if(openableOrdealStageInfo != null) {
                String ordealChapterAndStageNoString = openableOrdealStageInfo.getStage();
                String[] ordealChapterAndStageNoStringArray =  ordealChapterAndStageNoString.split("-");
                int ordealStageNo = Integer.parseInt(ordealChapterAndStageNoStringArray[1]);


                String json_ordealSaveData = myOrdealDungeonExpandSaveData.getJson_saveDataValue();
                ChapterSaveData ordealChapterSaveData = JsonStringHerlper.ReadValueFromJson(json_ordealSaveData, ChapterSaveData.class);
                ChapterSaveData.ChapterPlayInfo ordealExpandChapterPlayInfo = ordealChapterSaveData.chapterData.chapterPlayInfosList.get(0);

                for(ChapterSaveData.StagePlayInfo ordealExpandStagePlayInfo : ordealExpandChapterPlayInfo.stagePlayInfosList) {
                    if(ordealExpandStagePlayInfo.stageNo <= ordealStageNo) {
                        ordealExpandStagePlayInfo.isOpend = true;
                    }
                }
                json_ordealSaveData = JsonStringHerlper.WriteValueAsStringFromData(ordealChapterSaveData);
                myOrdealDungeonExpandSaveData.ResetSaveDataValue(json_ordealSaveData);
            }
            //고대 던전 각 스테이지 오픈 조건 레벨 체크
            AncientDragonStageTable openableAncientStageInfo = gameDataTableService.AncientDragonStageTableList().stream()
                    .filter(a -> a.getOpenLevel() <= finalCheckOpenLevel && a.getOpenLevel() > previousCheckOpenLevel)
                    .findAny()
                    .orElse(null);
            if(openableAncientStageInfo != null) {
                String ancientChapterAndStageNoString = openableAncientStageInfo.getStage();
                String[] ancientChapterAndStageNoStringArray =  ancientChapterAndStageNoString.split("-");
                int ancientStageNo = Integer.parseInt(ancientChapterAndStageNoStringArray[1]);


                String json_ancientSaveData = myAncientDragonExpandSaveData.getJson_saveDataValue();
                ChapterSaveData ancientChapterSaveData = JsonStringHerlper.ReadValueFromJson(json_ancientSaveData, ChapterSaveData.class);
                ChapterSaveData.ChapterPlayInfo ancientExpandChapterPlayInfo = ancientChapterSaveData.chapterData.chapterPlayInfosList.get(0);

                for(ChapterSaveData.StagePlayInfo ancientExpandStagePlayInfo : ancientExpandChapterPlayInfo.stagePlayInfosList) {
                    if(ancientExpandStagePlayInfo.stageNo <= ancientStageNo) {
                        ancientExpandStagePlayInfo.isOpend = true;
                    }
                }
                json_ancientSaveData = JsonStringHerlper.WriteValueAsStringFromData(ancientChapterSaveData);
                myAncientDragonExpandSaveData.ResetSaveDataValue(json_ancientSaveData);
            }
            checkOpenLevel++;
            if(checkOpenLevel > afterLevel)
                break;
        }
    }
}
