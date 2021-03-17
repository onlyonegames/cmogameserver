package com.onlyonegames.eternalfantasia.domain.model.entity;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import com.onlyonegames.eternalfantasia.etc.Defines;
import com.onlyonegames.util.StringMaker;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MyTeamInfo extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String stageTeam;
    String heroTowerDungeonPlayTeam;
    String ordealDungeonPlayTeam;
    String arenaPlayTeam;
    String arenaDefenceTeam;
    String ancientDragonDungeonPlayTeam;
    String infiniteTowerTeam;
    String fieldDungeonPlayTeam;
    @Builder
    public MyTeamInfo(Long useridUser) {
        this.useridUser = useridUser;
        this.stageTeam = "0,0,0,0";
        this.heroTowerDungeonPlayTeam = "0,0,0,0";
        this.ordealDungeonPlayTeam = "0,0,0,0";
        this.arenaPlayTeam = "0,0,0,0";
        this.arenaDefenceTeam = "0,0,0,0";
        this.ancientDragonDungeonPlayTeam = "0,0,0,0";
        this.infiniteTowerTeam = "0,0,0,0";
        this.fieldDungeonPlayTeam = "0,0,0,0";
    }

    public void InitData(Long heroId) {
        this.stageTeam = heroId+",0,0,0";
        this.heroTowerDungeonPlayTeam = heroId+",0,0,0";
        this.ordealDungeonPlayTeam = heroId+",0,0,0";
        this.arenaPlayTeam = heroId+",0,0,0";
        this.arenaDefenceTeam = heroId+",0,0,0";
        this.ancientDragonDungeonPlayTeam = heroId+",0,0,0";
        this.infiniteTowerTeam = heroId+",0,0,0";
        this.fieldDungeonPlayTeam = heroId+",0,0,0";
    }

    public void initHeroForStage(Long heroId) {
        StringMaker.Clear();
        StringMaker.stringBuilder.append(heroId);
        StringMaker.stringBuilder.append(",0,0,0");
        this.heroTowerDungeonPlayTeam = StringMaker.stringBuilder.toString();
    }

    public void initHeroForHeroTowerDungeonPlayTeam(Long heroId) {
        StringMaker.Clear();
        StringMaker.stringBuilder.append(heroId);
        StringMaker.stringBuilder.append(",0,0,0");
        this.heroTowerDungeonPlayTeam = StringMaker.stringBuilder.toString();
    }

    public void initHeroForOrdealDungeonPlayTeam(Long heroId) {
        StringMaker.Clear();
        StringMaker.stringBuilder.append(heroId);
        StringMaker.stringBuilder.append(",0,0,0");
        this.ordealDungeonPlayTeam = StringMaker.stringBuilder.toString();
    }

    public void initHeroForArenaPlayTeam(Long heroId) {
        StringMaker.Clear();
        StringMaker.stringBuilder.append(heroId);
        StringMaker.stringBuilder.append(",0,0,0");
        this.arenaPlayTeam = StringMaker.stringBuilder.toString();
    }

    public void initHeroForArenaDefenceTeam(Long heroId) {
        StringMaker.Clear();
        StringMaker.stringBuilder.append(heroId);
        StringMaker.stringBuilder.append(",0,0,0");
        this.arenaDefenceTeam = StringMaker.stringBuilder.toString();
    }

    public void initHeroForAncientDragonDungeonPlayTeam(Long heroId) {
        StringMaker.Clear();
        StringMaker.stringBuilder.append(heroId);
        StringMaker.stringBuilder.append(",0,0,0");
        this.ancientDragonDungeonPlayTeam = StringMaker.stringBuilder.toString();
    }

    public void initHeroForInfiniteTowerTeam(Long heroId) {
        StringMaker.Clear();
        StringMaker.stringBuilder.append(heroId);
        StringMaker.stringBuilder.append(",0,0,0");
        this.infiniteTowerTeam = StringMaker.stringBuilder.toString();
    }

    public void initHeroForFieldDungeonPlayTeam(Long heroId) {
        StringMaker.Clear();
        StringMaker.stringBuilder.append(heroId);
        StringMaker.stringBuilder.append(",0,0,0");
        this.fieldDungeonPlayTeam = StringMaker.stringBuilder.toString();
    }

    public void AddTeam(Long addMyHeroId, int index, Defines.TEAM_BUILDING_KIND team_building_kind) {

        String[] myTeams;
        switch (team_building_kind) {
            case STAGE_PLAY_TEAM:
                myTeams = stageTeam.split(",");
                stageTeam = TeamSetting(index, myTeams, addMyHeroId.toString());
                break;
            case HEROTOWER_DUNGEON_TEAM:
                myTeams = heroTowerDungeonPlayTeam.split(",");
                heroTowerDungeonPlayTeam = TeamSetting(index, myTeams, addMyHeroId.toString());
                break;
            case ORDEAL_DUNGEON_TEAM:
                myTeams = ordealDungeonPlayTeam.split(",");
                ordealDungeonPlayTeam = TeamSetting(index, myTeams, addMyHeroId.toString());
                break;
            case ARENA_TEAM:
                myTeams = arenaPlayTeam.split(",");
                arenaPlayTeam = TeamSetting(index, myTeams, addMyHeroId.toString());
                break;
            case ARENA_DEFEND_TEAM:
                myTeams = arenaDefenceTeam.split(",");
                arenaDefenceTeam = TeamSetting(index, myTeams, addMyHeroId.toString());
                break;
            case ANCIENT_DRAGON_DUNGEON_TEAM:
                myTeams = ancientDragonDungeonPlayTeam.split(",");
                ancientDragonDungeonPlayTeam = TeamSetting(index, myTeams, addMyHeroId.toString());
                break;
            case INFINITE_TOWER_TEAM:
                myTeams = infiniteTowerTeam.split(",");
                infiniteTowerTeam = TeamSetting(index, myTeams, addMyHeroId.toString());
                break;
            case FIELD_DUNGEON_TEAM:
                myTeams = fieldDungeonPlayTeam.split(",");
                fieldDungeonPlayTeam = TeamSetting(index, myTeams, addMyHeroId.toString());
        }
    }

    private String TeamSetting(int index, String[] myTeams, String heroIdString) {
        myTeams[index] = heroIdString;
        return GetTeamString(myTeams);
    }

    private String GetTeamString(String[] myTeams) {
        StringMaker.Clear();
        for (int i = 0; i < myTeams.length; i++) {
            if (i > 0)
                StringMaker.stringBuilder.append(",");
            StringMaker.stringBuilder.append(myTeams[i]);
        }
        return StringMaker.stringBuilder.toString();
    }

    public void RemoveTeam(int index, Defines.TEAM_BUILDING_KIND team_building_kind) {

        String[] myTeams;
        switch (team_building_kind) {
            case STAGE_PLAY_TEAM:
                myTeams = stageTeam.split(",");
                stageTeam = TeamSetting(index, myTeams, "0");
                break;
            case HEROTOWER_DUNGEON_TEAM:
                myTeams = heroTowerDungeonPlayTeam.split(",");
                heroTowerDungeonPlayTeam = TeamSetting(index, myTeams, "0");
                break;
            case ORDEAL_DUNGEON_TEAM:
                myTeams = ordealDungeonPlayTeam.split(",");
                ordealDungeonPlayTeam = TeamSetting(index, myTeams, "0");
                break;
            case ARENA_TEAM:
                myTeams = arenaPlayTeam.split(",");
                arenaPlayTeam = TeamSetting(index, myTeams, "0");
                break;
            case ARENA_DEFEND_TEAM:
                myTeams = arenaDefenceTeam.split(",");
                arenaDefenceTeam = TeamSetting(index, myTeams, "0");
                break;
            case ANCIENT_DRAGON_DUNGEON_TEAM:
                myTeams = ancientDragonDungeonPlayTeam.split(",");
                ancientDragonDungeonPlayTeam = TeamSetting(index, myTeams, "0");
                break;
            case INFINITE_TOWER_TEAM:
                myTeams = infiniteTowerTeam.split(",");
                infiniteTowerTeam = TeamSetting(index, myTeams, "0");
                break;
            case FIELD_DUNGEON_TEAM:
                myTeams = fieldDungeonPlayTeam.split(",");
                fieldDungeonPlayTeam = TeamSetting(index, myTeams, "0");
                break;

        }
    }

    public void SwitchTeam(int indexA, int indexB, Defines.TEAM_BUILDING_KIND team_building_kind)
    {
        String[] myTeams;
        switch (team_building_kind) {

            case STAGE_PLAY_TEAM:
                myTeams = stageTeam.split(",");
                ExchangeTeamInfo(indexA, indexB, myTeams);
                stageTeam = GetTeamString(myTeams);
                break;
            case HEROTOWER_DUNGEON_TEAM:
                myTeams = heroTowerDungeonPlayTeam.split(",");
                ExchangeTeamInfo(indexA, indexB, myTeams);
                heroTowerDungeonPlayTeam = GetTeamString(myTeams);
                break;
            case ORDEAL_DUNGEON_TEAM:
                myTeams = ordealDungeonPlayTeam.split(",");
                ExchangeTeamInfo(indexA, indexB, myTeams);
                ordealDungeonPlayTeam = GetTeamString(myTeams);
                break;
            case ARENA_TEAM:
                myTeams = arenaPlayTeam.split(",");
                ExchangeTeamInfo(indexA, indexB, myTeams);
                arenaPlayTeam = GetTeamString(myTeams);
                break;
            case ARENA_DEFEND_TEAM:
                myTeams = arenaDefenceTeam.split(",");
                ExchangeTeamInfo(indexA, indexB, myTeams);
                arenaDefenceTeam = GetTeamString(myTeams);
                break;
            case ANCIENT_DRAGON_DUNGEON_TEAM:
                myTeams = ancientDragonDungeonPlayTeam.split(",");
                ExchangeTeamInfo(indexA, indexB, myTeams);
                ancientDragonDungeonPlayTeam = GetTeamString(myTeams);
                break;
            case INFINITE_TOWER_TEAM:
                myTeams = infiniteTowerTeam.split(",");
                ExchangeTeamInfo(indexA, indexB, myTeams);
                infiniteTowerTeam = GetTeamString(myTeams);
                break;
            case FIELD_DUNGEON_TEAM:
                myTeams = fieldDungeonPlayTeam.split(",");
                ExchangeTeamInfo(indexA, indexB, myTeams);
                fieldDungeonPlayTeam = GetTeamString(myTeams);
                break;
        }
    }

    private void ExchangeTeamInfo(int indexA, int indexB, String[] myTeams) {
        String myTeamA = myTeams[indexA];
        myTeams[indexA] = myTeams[indexB];
        myTeams[indexB] = myTeamA;
    }

    public void AutoTeamSet(String teamIds, Defines.TEAM_BUILDING_KIND team_building_kind){

        switch (team_building_kind) {

            case STAGE_PLAY_TEAM:
                this.stageTeam = teamIds;
                break;
            case HEROTOWER_DUNGEON_TEAM:
                this.heroTowerDungeonPlayTeam = teamIds;
                break;
            case ORDEAL_DUNGEON_TEAM:
                this.ordealDungeonPlayTeam = teamIds;
                break;
            case ARENA_TEAM:
                this.arenaPlayTeam = teamIds;
                break;
            case ARENA_DEFEND_TEAM:
                this.arenaDefenceTeam = teamIds;
                break;
            case ANCIENT_DRAGON_DUNGEON_TEAM:
                this.ancientDragonDungeonPlayTeam = teamIds;
                break;
            case INFINITE_TOWER_TEAM:
                this.infiniteTowerTeam = teamIds;
                break;
            case FIELD_DUNGEON_TEAM:
                this.fieldDungeonPlayTeam = teamIds;
                break;
        }

    }
}