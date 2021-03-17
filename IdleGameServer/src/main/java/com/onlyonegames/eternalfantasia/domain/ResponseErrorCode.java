package com.onlyonegames.eternalfantasia.domain;

import lombok.Getter;

@Getter
public enum ResponseErrorCode {
    NONE(0, ""),
    UNDEFINED(-1, "UNDEFINED"),
    AREADY_EXIST_USERNAME(-2, "AREADY_EXIST_USERNAME"),
    INVAILD_JSON_STRING_TO_DATA(-3, "INVAILD_JSON_STRING_TO_DATA"),
    INVAILD_DATA_TO_JSON_STRING(-4, "INVAILD_DATA_TO_JSON_STRING"),
    NOT_FIND_DATA(-100, "NOT_FOUND")/*해당 테이블에 데이터가 존재하지 않음.*/,
    NOT_EXIST_CODE(-101, "NOT_EXIST_CODE"),/*특정 프로세스에서 조건에 맞는 코드를 갖는 아이템이 존재하지 않음. 혹은 잘못된 코드*/
    INVAILD_EVENTNO(-102,"INVAILD_EVENTNO"),/*이벤트 스크립트 번호가 순서에 맞지 않음.*/
    INVAILD_CHAPTER_SAVEDATA(-150, "INVAILD_CHAPTER_SAVEDATA"),/*ChapterSaveData의 구성이 맞지 않음.*/
    INVAILD_TAVERNVISITCOMPANIONINFODATA(-151, "INVAILD_TAVERNVISITCOMPANIONINFODATA"),/*TavernVisitCompanionInfoData의 구성이 맞지 않음.*/
    INVAILD_MYGIFTINVENTORYDATA(-152, "INVAILD_MYGIFTINVENTORYDATA"),/*GIFTITEMDTOSLIST 구성이 맞지 않음.*/
    INVAILD_HEROTOWER_EXPANDDATA(-153, "INVAILD_HEROTOWER_EXPANDDATA"),/*HeroTwoerExpandData 구성이 맞지 않음.*/
    /*200 부터 유저 자원 관련 프로세스 오류*/
    NEED_MORE_GOLD(-200, "NEED_MORE_GOLD"),
    NEED_MORE_SP(-201, "NEED_MORE_SP"),
    //NEED_MORE_RESMELTINGTICKET(-202, "NEED_MORE_RESMELTINGTICKET"),
    NEED_MORE_DIAMOND(-203, "NEED_MORE_DIAMOND"),
    NEED_MORE_LINKFORCEPOINT(-204, "NEED_MORE_LINKFORCEPOINT"),
    NEED_MORE_STONOFDIMENSION(-205, "NEED_MORE_STONOFDIMENSION"),
    NEED_MORE_ARENACOIN(-206, "NEED_MORE_ARENACOIN"),
    NEED_MORE_LOWDRAGONSCALE(-207, "NEED_MORE_LOWDRAGONSCALE"),
    NEED_MORE_MIDDLEDRAGONSCALE(-208, "NEED_MORE_MIDDLEDRAGONSCALE"),
    NEED_MORE_HIGHDRAGONSCALE(-209, "NEED_MORE_HIGHDRAGONSCALE"),
    NEED_MORE_ARENATICKET(-210, "NEED_MORE_STONOFDIMENSION"),
    CANT_GETTING_ARENATICKET_MAX(-211, "CANT_GETTING_ARENATICKET_MAX"),
    NEED_MORE_GOTCHAMILEAGE(-212, "NEED_MORE_GOTCHAMILEAGE"),
    NEED_MORE_FIELDDUNGEONTICKET(-213, "NEED_MORE_FIELDDUNGEONTICKET"),
    NEED_MORE_DARKOBE(-214, "NEED_MORE_DARKOBE"),
    /*300 부터 스킬 관련 프로세스 오류*/
    CANT_MORE_SKILL_LVUP(-300, "CANT_MORE_SKILL_LVUP"),
    /*400 부터 장비 인벤토리 관련 프로세스 오류*/
    CANT_MORE_EQUIPMENT_LVUP(-400, "CANT_MORE_EQUIPMENT_LVUP"),
    CANT_USE_MATERIAL(-401, "CANT_USE_MATERIAL")/*장비 강화 재료는 동일한 종류로 제한하고 덱에 장착된 장비는 재료로 사용할수 없음, 장비 퀄리티등급 재련 재료는 동일한 등급의 아이템으로 제한하고 덱에 장착된 장비는 재료로 사용할수 없음*/ ,
    NEED_MAXLV_PROMOTION(-402, "NEED_MAXLV_PROMOTION"),
    CANT_MORE_PROMOTION(-403, "CANT_MORE_PROMOTION"),
    NEED_MORE_MATERIAL(-404, "NEED_MORE_MATERIAL"),
    NOT_YET_REFRESHTIME(-405, "NOT_YET_REFRESHTIME"),
    FULL_EQUIPMENTINVENTORY(-406, "FULL_EQUIPMENTINVENTORY"),
    CANT_OPTIONRESMELTING_NORMALGRADE(-407, "CANT_OPTIONRESMELTING_NORMALGRADE"),/*일반 장비는 옵션 제련 불가능*/
    CANT_MORE_QUILITYRESMELTING(-408, "CANT_MORE_QUILITYRESMELTING"),/*이미 최상위 퀄리티의 장비는 퀄리티 등급 제련 불가능*/
    DONT_SELECT_QUILITYRESMELTINGMATERIAL(-409, "DONT_SELECT_QUILITYRESMELTINGMATERIAL"),/*퀄리티 등급 제련에 필요한 재료를 전혀 선택하지 않아 제련 불가능*/
    CANT_USE_PRODUCTIONSLOT(-410, "CANT_USE_PRODUCTIONSLOT"),/*해당 장비 제작 슬롯은 현재 사용 할수 없음*/
    NOT_YET_PRODUCTIONTEIM(-411, "NOT_YET_PRODUCTIONTEIM"),/*해당 장비 제작 슬롯은 아직 완료시간이 되지 않았음*/
    EMPTY_SLOT(-412, "EMPTY_SLOT"),/*해당 장비 제작 슬롯은 비어있음*/
    CANT_SLOT_OPEN(-413, "CANT_SLOT_OPEN"),/*제작 슬롯 오픈 불가*/
    ANYMORE_REDUCE_PRODUCTIONTIME(-414, "ANYMORE_REDUCE_PRODUCTIONTIME"),/*장비 제작중 해당 슬롯을 위한 광고를 이미 봐서 더이상 광고보기로 시간을 단축 시킬수 없음*/
    DONT_LEVELUP_PRODUCTIONMASTERY(-415, "DONT_LEVELUP_PRODUCTIONMASTERY"),/*장비 제작 시 숙련도 달성후 해당 보상을 받지 않아서 현재는 제작이 불가한 상태*/
    NOT_YET_PRODUCTIONMASTERYLEVEL(-416, "NOT_YET_PRODUCTIONMASTERYLEVEL"),/*장비 제작 시 숙련도 달성이 되지 않아서 보상 아이템을 획득 할 수 없음*/
    CANT_ANIMORE_INCREASE_INVENTORY(-417, "CANT_ANIMORE_INCREASE_INVENTORY"),/*인벤토리 확장을 더이상 할수 없음*/
    ALREADY_RECEIVED_PRODUCTIONMASTERY_REWARD(-418, "ALREADY_RECEIVED_PRODUCTIONMASTERY_REWARD"),/*장비 제작 해당 숙련도 달성 보상 이미 받음*/
    /*500부터 동료 인연 맺기 관련 프로세스 오류*/
    NOT_YET_VISIT_RECYCLETIME(-500, "NOT_YET_VISIT_RECYCLETIME"),/*아직 방문 스케쥴 리셋 할 시간 아님. 동료 방문 스케쥴 시작후 총 5일간 해당 방문 스케쥴 유지*/
    NOT_YET_RELATIONED(-501, "NOT_YET_RELATIONED"),/*아직 아무와도 인연 맺기를 하지 않은 상태*/
    CANT_SAYHELLO(-502, "CANT_SAYHELLO"),/*이미 해당일자에 인사하기를 다한 경우나 쿨타임인 경우*/
    CANT_HUNTING(-503, "CANT_HUNTING"),/*이미 해당일자에 헌팅을 다한 경우나 쿨타임*/
    CANT_SENDGIFT(-504, "CANT_SENDGIFT"),/*이미 해당일자에 선물하기를 다한 경우나 쿨타임인 경우*/
    //ALL_GOTCHEDHEROS(-505, "ALL_GOTCHEDHEROS"),/*모든 동료 이미 다 얻어서 방문 할 동료 없음 */
    NEED_MORE_RELATION(-506, "NEED_MORE_RELATION"),/*영입시 게이지를 다채우지 못한 상태 */
    ALREADY_GOTCHEDHEROS(-507, "ALREADY_GOTCHEDHEROS"),/*이미 획득한 동료임 */
    /*600부터 동료 선물하기 관련 프로세스 오류*/
    NEED_MORE_GIFT(-600, "NEED_MORE_GIFT"),/*선물 하려는 아이템의 갯수가 모자람.(혹은 0개로 없음)*/
    CANT_MORE_LEVELUP(-601, "CANT_MORE_LEVELUP"),/*메인 영웅의 레벨보다 동료의 레벨이 높을수 없다.*/
    /*-700부터 동료 링크포스 및 링크웨폰 관련 프로세스 오류*/
    NEED_MORE_GOODFEELING(-700, "NEED_MORE_GOODFEELING"),/*해당 링크포스를 배울 호감도가 아직 아님*/
    NOT_YET_OPEN_TALENT(-701, "NOT_YET_OPEN_TALENT"),/*연결된 능력치가 아직 오픈이 안되어서 해당 링크포스는 아직 배울수 없음*/
    CANT_MORE_OPENTALENT(-702, "CANT_MORE_OPENTALENT"),/*이미 해당 링크포스를 배웠음*/
    CANT_MORE_LEVELUP_LINKWEAPON(-703, "CANT_MORE_LEVELUP_LINKWEAPON"),/*더이상 해당 링크웨폰 강화 불가*/
    NOT_YET_OPEN_LINKWEAPON(-704, "NOT_YET_OPEN_LINKWEAPON"),/*아직 진화되지 않은 장비를 강화 할수 없음*/
    DONT_MATCHING_WEAPONID(-705, "DONT_MATCHING_WEAPONID"),/*장비 강화 및 진화 요청시 현재 진행중인 장비 아이디가 잘못된 아이디가 들어왔을때*/
    NEED_MORE_CHARACTERPIECE(-706, "NEED_MORE_CHARACTERPIECE"),/*장비 강화시 필요한 케릭터 조각이 충분하지 않음.*/
    /*-800부터 링크어빌리티 레벨업 관련 프로세스 오류*/
    CANT_LEVELUP_LINKABILITY(-800, "NOT_YET_GOTCHEDHERO"),/*링크어빌리티 레벨업 불가능.*/
    NEED_MORE_MILEAGE(-801, "NEED_MORE_MILEAGE"),/*마일리지 20개 모아야 마일리지 선물 받기 가능*/
    /*-850부터 피로도 챠징 관련 프로세스 오류*/
    CANT_CHARGING_NOW(-850, "CANT_CHARGING_NOW"),/*이미 광고를 봐서 시간을 줄였거나, */
    NOT_HAVE_POSITION(-851, "NOT_HAVE_POSITION"),/*피로도 회복 포션이 없음, */
    /*-900부터 코스튬 구매 및 장착 관련 프로세스 오류*/
    AREADY_HAS_COSTUME(-900, "AREADY_HAS_COSTUME"), /* 이미 코스튬을 구매 하여 구매 불가*/
    CANT_EQUIP_COSTUME(-901, "CANT_EQUIP_COSTUME"), /*코스튬 구매가 되지 않아 장착 불가*/

    /*-1000부터 던전 관련 프로세스 오류*/
    HERO_TOWER_TIME_OUT(-1000, "AREADY_HAS_COSTUME"), /* 최소 클리어 타임 시간안에 스테이지 클리어가 안됬는데 성공 메시지 보낼때*/
    DONT_PLAY_ANYMORE(-1001, "DONT_PLAY_ANIMORE"), /* 고대 드래곤 하루 플레이 가능 횟수 초과, 시련의 던전 하루 플레이 가능 횟수 초과*/
    CANT_FIND_RANKING(-1002, "CANT_FIND_RANKING"), /* 아직 아레나 플레이를 하지 않아서 전체 랭킹 이외의 랭킹을 산출할수 없음. */
    NEED_MORE_REPETITIONPOINT(-1003, "NEED_MORE_REPETITIONPOINT"), /* 아레나 반복 보상을 얻기 위한 포인트가 모자람. */
    AREADY_SETTING_HALLOFHONOR(-1004, "AREADY_SETTING_HALLOFHONOR"), /* 이미 명예의 전당 꾸미기를 진행해서 더이상 할수 없음.*/
    ALREADY_RECEIVED_HALLOFHONORREWARD(-1005, "ALREADY_RECEIVED_HALLOFHONORREWARD"), /* 오늘 이미 명예의 전당 보상을 받았음.*/
    DONT_SET_MYHALLOFHONOR(-1006, "DONT_SET_MYHALLOFHONOR"), /* 명예의 전당 셋팅할 조건이 안됨*/
    HERO_CANNOT_EMPTY(-1007, "HERO_CANNOT_EMPTY"), /*영웅은 제거될 수 없음*/
    HERO_CANNOT_DUPLICATE(-1008, "HERO_CANNOT_DUPLICATE"), /*영웅은 중복될 수 없음*/
    LEGION_CANNOT_DUPLICATE(-1009, "LEGION_CANNOT_DUPLICATE"), /*동료는 중복될 수 없음*/
    /*-1100부터 필드 관련 프로세스 오류*/
    WRONG_FIELDNO(-1100, "WRONG_FIELDNO"), /* 필드 프로세싱 요청 인자중 필드 번호가 잘못되었음.*/
    CANT_MORE_GENERATE_FIELD_OBJECT(-1101, "CANT_MORE_GENERATE_FIELD_OBJECT"), /* 더이상 해당 오브젝트 생성 불가.*/
    NOT_YET_GENERATE_TIME_FIELD_OBJECT(-1102, "NOT_YET_GENERATE_TIME_FIELD_OBJECT"), /* 아직 해당 오브젝트 생성할 시간이 되지 않음.*/
    CANT_FINISH_EXPEDITION_TIME(-1103, "CANT_FINISH_TIME"),/*탐험시간이 너무 짧아 보상받기 불가.*/
    NOT_YET_USE_BOOST(-1104, "NOT_YET_BOOST"),/*이미 부스트가 동작중.*/
    CANT_USE_BOOST_ANYMORE(-1105, "CANT_USE_BOOST_ANIMORE"),/*부스트 사용갯수에 막혀 오늘은 부스트 사용 불가.*/
    NOT_YET_USE_SKYWALKER(-1106, "NOT_YET_USE_SKYWALKER"),/*챕터를 모두 깨지 못해 비공정을 탑승하지 못함.*/
    ALREADY_GETTING(-1107, "ALREADY_GETTING"),/* 필드 오브젝트를 이미 얻음 */
    /*-1200부터 상점 관련 프로세스 오류*/
    SHOP_CANT_FIND_SHOPID(-1200, "SHOP_CANT_FIND_SHOPID"),
    ALREADY_BOUGHT(-1201, "ALREADY_BOUGHT"), //이미 구매함.
    /*-1300부터 미션 관련 프로세스 오류*/
    MISSION_NOT_YET_COMPLETE(-1300, "MISSION_NOT_YET_COMPLETE"),//아직 해당 미션이 완료되지 않아서 보상 받을수 없음.
    ALREADY_RECEIVED_MISSION_REWARD(-1301, "ALREADY_RECEIVED_MISSION_REWARD"),//이미 해당 업적에 대한 보상을 받음.
    NOT_YET_GETTING_BONUS_REWARD(-1302, "NOT_YET_GETTING_BONUS_REWARD"),//아직 포인트가 모자라 보너스 리워드를 받을 수 없음.
    /*-1400부터 메일 관련 프로세스 오류*/
    ALREADY_READ_MAIL(-1400, "ALREADY_READ_MAIL"),//이미 해당 아이디의 메일은 읽었음.
    WRONG_SELECTED_CODE(-1401, "WRONG_SELECTED_CODE"), //잘못된 선택 코드 (선택권에 없는 캐릭터나 캐릭터 코스튬을 선택했을때)
    /*-1500부터 광고 관련 오류*/
    CANT_MORE_VIEW(-1500,"CANT_MORE_VIEW"),/* 오늘 최대 광고 시청수를 채움 */
    /*-1600부터 천공의 계단 관련 오류*/
    ALREADY_RECEIVED_INFINITETOWERREWARD(-1600, "ALREADY_RECEIVED_INFINITETOWERREWARD"),/* 이미 천공의 계단 해당 층의 보상 을 받았음*/
    NOT_YET_PLAY_INFINITETOWER(-1601, "NOT_YET_PLAY_INFINITETOWER"),/*해당 층을 이미 플레이 했거나 아직 플레이 할수 없는 층을 플레이 하려고 했을때*/
    /*-1700부터 가챠 관련 오류*/
    ALREADY_FREE_GOTCHA(-1700, "ALREADY_FREE_GOTCHA"),/*무료 가챠는 하루에 한번만~*/
    //CLOSE_ALL_PICKUP_GOTCHA(-1701, "CLOSE_ALL_PICKUP_GOTCHA");/*현재 픽업 가챠는 모두 끝났음*/
    /*-1800부터 이터널 패스 관련 오류*/
    NOT_YET_RECEIVE_PASS_REWARD(-1800, "NOT_YET_RECEIVE_PASS_REWARD"),/*아직 해당 보상 받을 레밸이 되지 않음.*/
    AREADY_RECEIVE_PASS_ITEM(-1801, "AREADY_RECEIVE_PASS_ITEM"),/*이미 해당 보상 받음*/
    AREADY_BUY_ROYALPASS(-1802, "AREADY_BUY_ROYALPASS"),/*이미 로얄 패스 구매함*/
    AREADY_ARCHIVED_PASSLEVEL(-1803, "AREADY_ARCHIVED_PASSLEVEL"),/*이미 해당 패스 레벨을 달성 함.*/
    MAX_PASSLEVEL(-1804, "MAX_PASSLEVEL"),/*더이상 패스 레벨을 올릴수 없음.*/
    NOT_YET_RECEIVE_ROYAL_PASS_REWARD(-1805, "NOT_YET_RECEIVE_ROYAL_PASS_REWARD"),/*로얄 패스 구매 하지 않아서 해당 보상 받을수 없음.*/
    /*-1900부터 이벤트 관련 오류*/
    Duplicate_Scheduler_Found(-1900, "Duplicate_Scheduler_Found"),/*같은 종류의 이벤트가 중복으로 발생함*/
    ALREADY_RECEIVED_REWARD(-1901, "ALREADY_RECEIVED_REWARD"),/*이미 당일 출석보상을 받음*/
    /*-2000부터 프로필 관련 오류*/
    NOT_YET_GETTING_CHARACTER(-2000, "NOT_YET_GETTING_CHARACTER"),/*아직 획득하지 않은 케릭터 선택*/
    NOT_YET_GETTING_ProfileFRAME(-2001, "NOT_YET_GETTING_FRAME"),/*아직 획득하지 않은 프레임 선택*/

    ALREADY_COMPLETE_TRANSACTION(-3000, "ALREADY_COMPLETE_TRANSACTION"),/*인앱 결제 이후 트랜잭션이 끝났는데 다시 해당 트랜잭션으로 요청 올때*/
    WRONG_TRANSACTIONID(-3001, "WRONG_TRANSACTIONID"),/*인앱 결제 이후 트랜잭션 아이디가 없음.*/
    /**/
    EXPIRED_SESSION(-9996, "EXPIRED_SESSION"),
    EMPTY_SESSION(-9997, "EMPTY_SESSION"),
    DUPLICATED_SESSION(-9998, "DUPLICATED_SESSION"),
    BLACK_USER(-9999, "BLACK_USER"),

    ALREADY_USED_COUPON(-10000, "ALREADY_USED_COUPON"),
    COUPON_NOT_EXIST(-10001, "COUPON_NOT_EXIST");
    private final int integerValue;
    private final String stringValue;

    ResponseErrorCode(int table1Value, String table2Value) {
        this.integerValue = table1Value;
        this.stringValue = table2Value;
    }
}