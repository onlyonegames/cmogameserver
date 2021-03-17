package com.onlyonegames.eternalfantasia.etc;

public class Defines {
    /**팀 빌딩 종류 : 일반 플레이팀, 영웅의 타워 팀, 시련의 던전 팀, 아레나 공격팀, 아레나 방어팀, 고대 드래곤 던전 팀, 2021-02-03 by 김현호 추가 필드 던전 팀*/
    public enum TEAM_BUILDING_KIND{
        STAGE_PLAY_TEAM, HEROTOWER_DUNGEON_TEAM, ORDEAL_DUNGEON_TEAM, ARENA_TEAM, ARENA_DEFEND_TEAM, ANCIENT_DRAGON_DUNGEON_TEAM, INFINITE_TOWER_TEAM, FIELD_DUNGEON_TEAM
    };
    /**아레나 전투 기록실중 공격인지 방어인지에 대한 종류 정의*/
    public static final int MY_ARENA_PLAY_LOG_FOR_BATTLE_RECORD_ATTACK = 1;
    public static final int MY_ARENA_PLAY_LOG_FOR_BATTLE_RECORD_DEFENCE = 2;
    /**아레나 전투 기록실중 승리인지 패배인지 에 대한 종류 정의*/
    public static final int MY_ARENA_PLAY_LOG_FOR_BATTLE_RECORD_WIN = 1;
    public static final int MY_ARENA_PLAY_LOG_FOR_BATTLE_RECORD_DEFEAT = 2;
    /**아레나 하루당 무료 티켓 갯수 정의*/
    public static final int MY_ARENA_FREE_TICKET_COUNT_PER_DAY = 5;
    /**어둠의 균열 하루당 무료 티켓 갯수 정의*/
    public static final int MY_FIELD_DUNGEON_TICKET_COUNT_PER_DAY = 3;
    /**가챠 하루당 무료 티켓 갯수 정의*/
    public static final int MY_GOTCHA_FREE_TICKET_COUNT_PER_DAY = 1;
    /**월정액 패키지 받을수 있는 날짜*/
    public static final int PACKAGE_MONTH_RECEIVABLE_COUNT = 30;
}
