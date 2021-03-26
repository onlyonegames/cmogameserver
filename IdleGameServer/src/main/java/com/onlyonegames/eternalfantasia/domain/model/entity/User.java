package com.onlyonegames.eternalfantasia.domain.model.entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import com.onlyonegames.eternalfantasia.etc.DefineLimitValue;
import com.onlyonegames.eternalfantasia.etc.Defines;
import org.apache.tomcat.jni.Local;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 */
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    String socialId;
    String password;
    String socialProvider;
    String userGameName;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    int gold;
    int diamond;


    int linkforcePoint;
    int skillPoint;
    int lowDragonScale;//하급드래곤의 비늘
    int middleDragonScale;//중급드래곤의 비늘
    int highDragonScale;//고급드래곤의 비늘
    int arenaCoin;//아레나코인
    int arenaTicket;//아레나 티켓
    int freeArenaCountPerDay; //하루에 제공되는 무료 아레나 티켓 5개. 무조건 해당 티켓부터 소모된뒤 아레나 티켓이 소모되기 시작한다.
    LocalDateTime freeArenaTicketResetTime; //무료 아레나 티켓 하루마다 Defines.MY_ARENA_FREE_TICKET_COUNT_PER_DAY(5) 만큼 리셋.
    int fieldDungeonTicket;
    int freeFieldDungeonTicket;
    LocalDateTime freeFieldDungeonTicketResetTime;
    int darkObe;

    int freeGotchaTicketPerDay; //하루에 제공되는 무료 가차 1개.
    LocalDateTime freeGotchaTicketResetTime; //무료 가차 하루마다 Defines.MY_GOTCHA_FREE_TICKET_COUNT_PER_DAY(1) 만큼 리셋.

    int heroEquipmentInventoryMaxSlot;
    int nextEventNo; /* 다음 실행할 이벤트 번호 */
    boolean blackUser;
    boolean dummyUser;
    @CreatedDate
    private LocalDateTime createdDate;
    LocalDateTime lastloginDate;
    boolean firstBuy;//by rainful 2021-01-27 최초 결제 했는지 체크.
    int purchaseSum;//by rainful 2021-01-27 해당유저의 토탈 결제 금액

    @Builder
    public User(String socialId, String password, String socialProvider, String userGameName, Set<Role> roles) {
        this.socialId = socialId;
        this.password = password;
        this.socialProvider = socialProvider;
        this.userGameName = userGameName;
        this.roles = roles;
        this.firstBuy = false;
        this.purchaseSum = 0;
        InitData();
        SetLastLoginDate();
    }

    public void BuyFirst(){
        this.firstBuy = true;
    }

    public void PurchaseSum(int cost) {
        this.purchaseSum =  this.purchaseSum + cost;
    }

    public void InitData() {

        this.gold = 0;
        this.diamond = 0;
        this.linkforcePoint = 0;
        this.skillPoint = 0;
        this.lowDragonScale = 0;
        this.middleDragonScale = 0;
        this.highDragonScale = 0;
        this.arenaCoin = 0;
        this.heroEquipmentInventoryMaxSlot = 500;
        this.nextEventNo = 1;
        LocalDateTime now = LocalDateTime.now();
        this.freeArenaCountPerDay = Defines.MY_ARENA_FREE_TICKET_COUNT_PER_DAY;
        this.freeFieldDungeonTicket = Defines.MY_FIELD_DUNGEON_TICKET_COUNT_PER_DAY;
        LocalDateTime initTime = LocalDateTime.of(now.minusHours(5).toLocalDate(), LocalTime.of(5, 0, 0));
        this.freeArenaTicketResetTime = initTime;
        this.freeGotchaTicketPerDay = Defines.MY_GOTCHA_FREE_TICKET_COUNT_PER_DAY;
        this.freeGotchaTicketResetTime = initTime;
        this.freeFieldDungeonTicketResetTime = LocalDateTime.of(now.toLocalDate(), LocalTime.of(12,0,0));
        this.blackUser = false;
        this.firstBuy = false;
        this.purchaseSum = 0;
    }

    public void SetUserName(String userName){
        userGameName = userName;
    }
    public void SetLastLoginDate() {
        this.lastloginDate = LocalDateTime.now();
    }

    public void AddGold(int addGold) {
        long willChangeValue = gold + addGold;
        if(willChangeValue > DefineLimitValue.LIMIT_GOLD)
            willChangeValue = DefineLimitValue.LIMIT_GOLD;
        gold = (int)willChangeValue;
    }

    public boolean SpendGold(int spendGold) {
        if (gold >= spendGold) {
            gold -= spendGold;
            return true;
        }
        return false;
    }

    public void AddDiamond(int addDiamond) {
        long willChangeValue = diamond + addDiamond;
        if(willChangeValue > DefineLimitValue.LIMIT_DIAMOND)
            willChangeValue = DefineLimitValue.LIMIT_DIAMOND;
        diamond = (int)willChangeValue;
    }

    public boolean SpendDiamond(int spendDiamond) {
        if(diamond >= spendDiamond) {
            diamond -= spendDiamond;
            return true;
        }
        return false;
    }

    public void AddSkillPoint(int addSkillPoint) {
        long willChangeValue = skillPoint + addSkillPoint;
        if(willChangeValue > DefineLimitValue.LIMIT_SKILLPOINT)
            willChangeValue = DefineLimitValue.LIMIT_SKILLPOINT;
        skillPoint = (int)willChangeValue;
    }

    public boolean SpendSkillPoint(int spendSkillPoint) {
        if (skillPoint >= spendSkillPoint) {
            skillPoint -= spendSkillPoint;
            return true;
        }
        return false;
    }

    public void AddLinkforcePoint(int addLinkforcePoint) {
        int willChangeValue = linkforcePoint + addLinkforcePoint;
        if(willChangeValue > DefineLimitValue.LIMIT_LINKFORCEPOINT)
            willChangeValue = DefineLimitValue.LIMIT_LINKFORCEPOINT;
        linkforcePoint = (int)willChangeValue;
    }

    public boolean SpendLinkforcePoint(int spendLinkforcePoint) {
        if(linkforcePoint >= spendLinkforcePoint) {
            linkforcePoint -= spendLinkforcePoint;
            return true;
        }
        return false;
    }

    public void AddLowDragonScale(int addLowDragonScale) {
        int willChangeValue = lowDragonScale + addLowDragonScale;
        if(willChangeValue > DefineLimitValue.LIMIT_LOWDRAGONSCALE)
            willChangeValue = DefineLimitValue.LIMIT_LOWDRAGONSCALE;
        lowDragonScale = (int)willChangeValue;
    }

    public boolean SpendLowDragonScale(int spendLowDragonScale) {
        if(lowDragonScale >= spendLowDragonScale) {
            lowDragonScale -= spendLowDragonScale;
            return true;
        }
        return false;
    }

    public void AddMiddleDragonScale(int addMiddleDragonScale) {
        int willChangeValue = middleDragonScale + addMiddleDragonScale;
        if(willChangeValue > DefineLimitValue.LIMIT_MIDDLEDRAGONSCALE)
            willChangeValue = DefineLimitValue.LIMIT_MIDDLEDRAGONSCALE;
        middleDragonScale = (int)willChangeValue;
    }

    public boolean SpendMiddleDragonScale(int spendMiddleDragonScale) {
        if(middleDragonScale >= spendMiddleDragonScale) {
            middleDragonScale -= spendMiddleDragonScale;
            return true;
        }
        return false;
    }

    public void AddHighDragonScale(int addHighDragonScale) {
        int willChangeValue = highDragonScale + addHighDragonScale;
        if(willChangeValue > DefineLimitValue.LIMIT_HIGHTDRAGONSCALE)
            willChangeValue = DefineLimitValue.LIMIT_HIGHTDRAGONSCALE;
        highDragonScale = (int)willChangeValue;
    }

    public boolean SpendHightDragonScale(int spendHightDragonScale) {
        if(highDragonScale >= spendHightDragonScale) {
            highDragonScale -= spendHightDragonScale;
            return true;
        }
        return false;
    }

    public boolean IncreaseSlot() {
        if(heroEquipmentInventoryMaxSlot < 500) {
            heroEquipmentInventoryMaxSlot = heroEquipmentInventoryMaxSlot + 8;
            heroEquipmentInventoryMaxSlot = Math.min(500, heroEquipmentInventoryMaxSlot);
            return true;
        }
        return false;
    }

    public void SetEventNoForTest(int eventNo) {
        this.nextEventNo = eventNo;
    }

    public void EventJumped(int jumpedEvent) {
        nextEventNo = jumpedEvent;
    }

    public boolean EventComplete(int currentEventNo) {
        if(currentEventNo != nextEventNo)
            return false;
        nextEventNo++;
        return true;
    }

    public void AddArenaCoin(int addArenaCoin) {
        int willChangeValue = arenaCoin + addArenaCoin;
        if(willChangeValue > DefineLimitValue.LIMIT_ARENACOIN)
            willChangeValue = DefineLimitValue.LIMIT_ARENACOIN;
        arenaCoin = (int)willChangeValue;
    }

    public boolean SpendArenaCoin(int spendArenaCoin) {
        if(arenaCoin >= spendArenaCoin) {
            arenaCoin -= spendArenaCoin;
            return true;
        }
        return false;
    }

    public boolean AddArenaTicket(int addArenaTicket) {
        if(arenaTicket >= DefineLimitValue.LIMIT_ARENATICKET)
            return false;
        int willChangeValue = arenaTicket + addArenaTicket;
        if(willChangeValue > DefineLimitValue.LIMIT_ARENATICKET)
            willChangeValue = DefineLimitValue.LIMIT_ARENATICKET;
        arenaTicket = (int)willChangeValue;
        return true;
    }

    public boolean SpendFreeArenaCountPerDay(int spendArenaTicket) {
        if(freeArenaCountPerDay >= spendArenaTicket) {
            freeArenaCountPerDay -= spendArenaTicket;
            return true;
        }
        return false;
    }

    /**아레나 무료 티켓 하루마다 5개로 리셋*/
    public void CheckRechargingTimeFreeArenaTicket() {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(freeArenaTicketResetTime, now);
        if(duration.toDays() >= 1) {
            freeArenaCountPerDay = Defines.MY_ARENA_FREE_TICKET_COUNT_PER_DAY;
            //한국시 기준으로 오전 1시로 초기화
            freeArenaTicketResetTime = LocalDateTime.of(now.minusHours(5).toLocalDate(), LocalTime.of(5, 0, 0));
        }
    }

    public boolean SpendArenaTicket(int spendArenaTicket) {
        if(arenaTicket >= spendArenaTicket) {
            arenaTicket -= spendArenaTicket;
            return true;
        }
        return false;
    }

    public boolean AddFieldDungeonTicket(int addFieldDungeonTicket) {
        if(fieldDungeonTicket >= DefineLimitValue.LIMIT_ARENATICKET)
            return false;
        int willChangeValue = fieldDungeonTicket + addFieldDungeonTicket;
        if(willChangeValue > DefineLimitValue.LIMIT_ARENATICKET)
            willChangeValue = DefineLimitValue.LIMIT_ARENATICKET;
        fieldDungeonTicket = (int)willChangeValue;
        return true;
    }

    public boolean SpendFieldDungeonTicket(int spendFieldDungeonTicket) {
        if(fieldDungeonTicket >= spendFieldDungeonTicket) {
            fieldDungeonTicket -= spendFieldDungeonTicket;
            return true;
        }
        return false;
    }

    public boolean SpendFreeFieldDungeonTicket(int spendFieldDungeonTicket) {
        if(freeFieldDungeonTicket >= spendFieldDungeonTicket) {
            freeFieldDungeonTicket -= spendFieldDungeonTicket;
            return true;
        }
        return false;
    }

    public boolean AddDarkObe(int addDarkObe) {
        if(darkObe >= DefineLimitValue.LIMIT_DARKOBE)
            return false;
        int willChangeValue = darkObe + addDarkObe;
        if(willChangeValue > DefineLimitValue.LIMIT_DARKOBE)
            willChangeValue = DefineLimitValue.LIMIT_DARKOBE;
        darkObe = (int)willChangeValue;
        return true;
    }

    public boolean SpendDarkObe(int spendDarkObe) {
        if(darkObe >= spendDarkObe) {
            darkObe -= spendDarkObe;
            return true;
        }
        return false;
    }

    /**필드던전 무료 티켓 하루마다 5개로 리셋*/
    public boolean CheckRechargingTimeFreeFieldDungeonTicket() {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(freeFieldDungeonTicketResetTime, now); //아레나 리셋시간과 공유하던 것을 따로 추가하여 비교해야함 버그 위험성 있음.
        if(duration.toDays() >= 1) {
            freeFieldDungeonTicket = Defines.MY_FIELD_DUNGEON_TICKET_COUNT_PER_DAY;
            //한국시 기준으로 오전 1시로 초기화
            freeFieldDungeonTicketResetTime = LocalDateTime.of(now.minusHours(5).toLocalDate(), LocalTime.of(5, 0, 0));
            return true;
        }
        return false;
    }

    public boolean SpendFreeGotchaTicketCountPerDay(int spendGotchaTicket) {
        CheckRechargingTimeFreeGotchaTicket();
        if(freeGotchaTicketPerDay >= spendGotchaTicket) {
            freeGotchaTicketPerDay -= spendGotchaTicket;
            return true;
        }
        return false;
    }

    public void CheckRechargingTimeFreeGotchaTicket() {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(freeGotchaTicketResetTime, now);
        if(duration.toDays() >= 1) {
            freeGotchaTicketPerDay = Defines.MY_GOTCHA_FREE_TICKET_COUNT_PER_DAY;
            //한국시 기준으로 오전 1시로 초기화
            freeGotchaTicketResetTime = LocalDateTime.of(now.minusHours(5).toLocalDate(), LocalTime.of(5, 0, 0));
        }
    }

    public void AddFieldDungeonOnePlayable() {
        freeFieldDungeonTicket += 1;
    }

    public boolean isBlackUser() {
        return this.blackUser;
    }
}