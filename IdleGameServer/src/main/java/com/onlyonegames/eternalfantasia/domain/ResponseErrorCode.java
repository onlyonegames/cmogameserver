package com.onlyonegames.eternalfantasia.domain;

import lombok.Getter;

@Getter
public enum ResponseErrorCode {
    NONE(0, ""),
    UNDEFINED(-1, "UNDEFINED"),
    AREADY_EXIST_USERNAME(-2, "AREADY_EXIST_USERNAME"),
    INVAILD_JSON_STRING_TO_DATA(-3, "INVAILD_JSON_STRING_TO_DATA"),
    INVAILD_DATA_TO_JSON_STRING(-4, "INVAILD_DATA_TO_JSON_STRING"),
    SERVER_CHECK(-5, "SERVER_CHECK"),
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
    NEED_MORE_SOULSTONE(-202, "NEED_MORE_SOULSTONE"),
    NEED_MORE_DIAMOND(-203, "NEED_MORE_DIAMOND"),
    NEED_MORE_ARENACOIN(-204, "NEED_MORE_ARENACOIN"),
    NEED_MORE_DRAGONCOIN(-205, "NEED_MORE_DRAGONCOIN"),
    /*300 부터 인벤토리 관련 프로세스 오류*/
    NEED_MORE_RUNE(-300, "NEED_MORE_RUNE"),
    NEED_MORE_LEVEL(-301, "NEED_MORE_LEVEL"),
    NEED_MORE_RELIC(-302, "NEED_MORE_RELIC"),
    /*400 부터 장비 인벤토리 관련 프로세스 오류*/
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
    /*-1700부터 가챠 관련 오류*/
    ALREADY_FREE_GOTCHA(-1700, "ALREADY_FREE_GOTCHA"),/*무료 가챠는 하루에 한번만~*/
    //CLOSE_ALL_PICKUP_GOTCHA(-1701, "CLOSE_ALL_PICKUP_GOTCHA");/*현재 픽업 가챠는 모두 끝났음*/
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