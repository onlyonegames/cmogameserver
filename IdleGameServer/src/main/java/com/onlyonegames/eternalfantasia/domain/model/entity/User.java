package com.onlyonegames.eternalfantasia.domain.model.entity;

import java.math.BigInteger;
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
    @TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    String socialId;
    String password;
    String socialProvider;
    String userGameName;
    int level;
    double exp;
    int sexType; //0 : 남자, 1 : 여자 default : 0
    @ManyToMany(fetch = FetchType.EAGER)
//    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    String gold;
    Long diamond;
    String soulStone; // 형식 변경 가능성 있음
    int skillPoint;
    int moveStone;
    Long arenaCoin;
    Long dragonCoin;
    int fieldIndex;
    Long dungeonTicket;
    String battleStatus;
    boolean dummyUser;
    boolean new_user;
    int userType; // 1 : 일반 유저, 0 : 화이트 유저 == 개발자  // 정해야함
    double battlePower;
    int mileage;
    String costumeTicket;
    boolean advertisement;
    boolean blackUser;
    String ancientCrystal;
    String redOrb;
    String greenOrb;
    String yellowOrb;
    String blueOrb;
    double fragment;
    Long eventItem;
    int advancedEventItem;
    Long classEmblem;
    int challengeTicket;
    Long crystalBall;
    int starPiece;
    int holyThingGachaCount;
//    int totalPurchase;

    @CreatedDate
    private LocalDateTime createdDate;
    LocalDateTime lastloginDate;
    LocalDateTime previousLoginDate;
    LocalDateTime lastSettingTime;
    LocalDateTime lastDayResetTime;
    LocalDateTime lastWeekResetTime;
    LocalDateTime lastMonthResetTime;

    @Builder
    public User(String socialId, String password, String socialProvider, String userGameName, Set<Role> roles) {
        this.socialId = socialId;
        this.password = password;
        this.socialProvider = socialProvider;
        this.userGameName = userGameName;
        this.roles = roles;
        InitData();
        SetLastLoginDate();
    }

    public void InitData() {
        this.gold = "0";
        this.diamond = 0L;
        this.soulStone = "0";
        this.skillPoint = 0;
        this.moveStone = 100; //TODO 라이브때 0으로 설정해야함 !!!!!
        this.arenaCoin = 0L;
        this.dragonCoin = 0L;
        this.level = 1;
        this.exp = 0;
        this.sexType = 0;
        this.fieldIndex = 0;
        this.battleStatus = "";
        this.dungeonTicket = 0L;
        this.dummyUser = false;
        this.new_user = true;
        this.userType = 1;
        this.battlePower = 0L;
        this.mileage = 0;
        this.advertisement = false;
        this.costumeTicket = "0";
        this.ancientCrystal = "0";
        this.redOrb = "0";
        this.greenOrb = "0";
        this.yellowOrb = "0";
        this.blueOrb = "0";
        this.fragment = 0;
        this.eventItem = 0L;
        this.advancedEventItem = 0;
        this.classEmblem = 0L;
        this.challengeTicket = 0;
        this.crystalBall = 0L;
        this.starPiece = 0;
        this.holyThingGachaCount = 0;
    }

//    public void AddPurchase(int price) {
//        this.totalPurchase += price;
//    }

    public void AddGold(BigInteger _addGold) {
        BigInteger gold = new BigInteger(this.gold);
        gold = gold.add(_addGold);
        this.gold = gold.toString();
    }

    public boolean SpendGold(BigInteger spendGold) {
        BigInteger gold = new BigInteger(this.gold);
        if(gold.compareTo(spendGold)>=0){
            gold = gold.subtract(spendGold);
            this.gold = gold.toString();
            return true;
        }
        return false;
    }

    public void AddSoulStone(Long _addSoulStone) {
        BigInteger soulStone = new BigInteger(this.soulStone);
        soulStone = soulStone.add(BigInteger.valueOf(_addSoulStone));
        this.soulStone = soulStone.toString();
    }

    public boolean SpendSoulStone(BigInteger spendSoulStone) {
        BigInteger soulStone = new BigInteger(this.soulStone);
        if(soulStone.compareTo(spendSoulStone)>=0){
            soulStone = soulStone.subtract(spendSoulStone);
            this.soulStone = soulStone.toString();
            return true;
        }
        return false;
    }

    public boolean SpendDiamond(int spendDiamond) {
        if(spendDiamond>this.diamond)
            return false;
        this.diamond -= spendDiamond;
        return true;
    }

    public boolean SpendArenaCoin(Long spendArenaCoin) {
        if (spendArenaCoin > this.arenaCoin)
            return false;
        this.arenaCoin -= spendArenaCoin;
        return true;
    }

    public boolean SpendDragonCoin(Long spendDragonCoin) {
        if (spendDragonCoin > this.dragonCoin)
            return false;
        this.dragonCoin -= spendDragonCoin;
        return true;
    }

    public boolean SpendMileage(int spendMileage) {
        if (spendMileage > this.mileage)
            return false;
        this.mileage -= spendMileage;
        return true;
    }

    public boolean SpendEventItem(Long spendEventItem) {
        if (spendEventItem > this.eventItem)
            return false;
        this.eventItem -= spendEventItem;
        return true;
    }

    public boolean SpendAdvancedEventItem(int spendAdvancedEventItem) {
        if (spendAdvancedEventItem > this.advancedEventItem)
            return false;
        this.advancedEventItem -= spendAdvancedEventItem;
        return true;
    }

    public boolean SpendClassEmblem(Long spendClassEmblem) {
        if (spendClassEmblem > this.classEmblem)
            return false;
        this.classEmblem -= spendClassEmblem;
        return true;
    }

    public boolean SpendChallengeTicket(int spendChallengeTicket) {
        if (spendChallengeTicket > this.challengeTicket)
            return false;
        this.challengeTicket -= spendChallengeTicket;
        return true;
    }

    public void SetLastLoginDate() {
        if (this.lastloginDate == null)
            this.previousLoginDate = LocalDateTime.now();
        else
            this.previousLoginDate = this.lastloginDate;
        this.lastloginDate = LocalDateTime.now();
    }

    public void SetUserName(String userGameName) {
        this.userGameName = userGameName;
    }

    public void SetNew_User() {
        this.new_user = false;
    }

    public void AddSkillPoint(int addSkillPoint) {
        this.skillPoint += addSkillPoint;
    }

    public boolean SpendSkillPoint(int spendSkillPoint) {
        if(this.skillPoint >= spendSkillPoint) {
            this.skillPoint -= spendSkillPoint;
            return true;
        }
        return false;
    }

    public void AddArenaCoin(Long addArenaCoin) {
        this.arenaCoin += addArenaCoin;
    }

    public void AddAdvancedEventItem(int addAdvancedEventItem) {
        this.advancedEventItem += addAdvancedEventItem;
    }

    public void SetLastSettingTime() {
        this.lastSettingTime = LocalDateTime.now().withNano(0);
    }

    public void SetSexType(int sexType) {
        this.sexType = sexType;
    }

    public void SetGold(String element) {
        this.gold = element;
    }

    public void SetDiamond(String element) {
        this.diamond = Long.parseLong(element);
    }

    public void SetSoulStone(String element) {
        this.soulStone = element;
    }

    public void SetSkillPoint(String element) {
        this.skillPoint = Integer.parseInt(element);
    }

    public void SetMoveStone(String element) {
        this.moveStone = Integer.parseInt(element);
    }

    public void SetLevel(String element) {
        this.level = Integer.parseInt(element);
    }

    public void SetExp(String element) {
        this.exp = Double.parseDouble(element);
    }

    public void SetFieldIndex(String element) {
        this.fieldIndex = Integer.parseInt(element);
    }

    public void SetBattleStatus(String element) {
        this.battleStatus = element;
    }

    public void SetDungeonTicket(String element) {
        this.dungeonTicket = Long.parseLong(element);
    }

    public void SetArenaCoin(String element) {
        this.arenaCoin = Long.parseLong(element);
    }

    public void SetDragonCoin(String element) {
        this.dragonCoin = Long.parseLong(element);
    }

    public void SetBattlePower(String element) {
        this.battlePower = Double.parseDouble(element);
    }

    public void SetMileage(String element) {
        this.mileage = Integer.parseInt(element);
    }

    public void SetCostumeTicket(String element) {
        this.costumeTicket = element;
    }

    public void SetAncientCrystal(String element) {
        this.ancientCrystal = element;
    }

    public void SetRedOrb(String element) {
        this.redOrb = element;
    }

    public void SetGreenOrb(String element) {
        this.greenOrb = element;
    }

    public void SetYellowOrb(String element) {
        this.yellowOrb = element;
    }

    public void SetBlueOrb(String element) {
        this.blueOrb = element;
    }

    public void SetFragment(String element) {
        this.fragment = Double.parseDouble(element);
    }

    public void SetEventItem(String element) {
        this.eventItem = Long.parseLong(element);
    }


    public void SetClassEmblem(String element) {
        this.classEmblem = Long.parseLong(element);
    }
    public void SetChallengeTicket(String element) {
        this.challengeTicket = Integer.parseInt(element);
    }

    public void SetCrystalBall(String element) {
        this.crystalBall = Long.parseLong(element);
    }

    public void SetStarPiece(String element) {
        this.starPiece = Integer.parseInt(element);
    }

    public void SetHolyThingGachaCount(String element) {
        this.holyThingGachaCount = Integer.parseInt(element);
    }

    public void ADRemove() {
        this.advertisement = true;
    }

    public int isAdRemove() {
        return this.advertisement?1:0;
    }

    public void ExchangeSocial(String socialId, String socialProvider) {
        this.socialId = socialId;
        this.socialProvider = socialProvider;
    }

    public void SetLastDayResetTime(LocalDateTime lastDayResetTime) {
        this.lastDayResetTime = lastDayResetTime;
    }

    public void SetLastWeekResetTime(LocalDateTime lastWeekResetTime) {
        this.lastWeekResetTime = lastWeekResetTime;
    }

    public void SetLastMonthResetTime(LocalDateTime lastMonthResetTime) {
        this.lastMonthResetTime = lastMonthResetTime;
    }

    public void SetBlackUser() {
        this.blackUser = true;
    }
}