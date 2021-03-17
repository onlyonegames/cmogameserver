package com.onlyonegames.eternalfantasia.domain.model.dto.ShopDto;

import com.google.common.base.Strings;
import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.entity.Iap.SeasonLimitPackageSchedule;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.etc.Defines;
import com.onlyonegames.eternalfantasia.etc.EquipmentCalculate;
import com.onlyonegames.eternalfantasia.etc.EquipmentItemCategory;
import com.onlyonegames.util.MathHelper;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class MyShopItemsList {
    public static class MyShopItem {
        public int slotIndex;/**유저 각 상점별 고유 인덱스*/
        public int shopTableId;
        public String itemName;
        public boolean bought;
    }

    public static class MyGiftItem {
        public int slotIndex;/**유저 각 상점별 고유 인덱스*/
        public int shopTableId;
        public String itemName;
        public boolean bought;
        public String giftItemCode;
    }

    public static class MyEquipmentShopItem {
        public int slotIndex;/**유저 각 상점별 고유 인덱스*/
        public int shopTableId;
        public String itemName;
        public boolean bought;
        public int item_id;
        public int itemClassValue;
        public float decideDefaultAbilityValue;
        public float decideSecondAbilityValue;
        public String itemClass;
        public String optionIds;
        public String optionValues;
    }

    public static class MyCharacterPiece {
        public int slotIndex;/**유저 각 상점별 고유 인덱스*/
        public int shopTableId;/**케릭터조각샵테이블 아이디*/
        public String itemName;
        public boolean bought;
        public String characterPieceCode;
    }

    public static class MyEnchantItem {
        public int slotIndex;/**유저 각 상점별 고유 인덱스*/
        public int shopTableId;
        public String itemName;
        public boolean bought;
        public String itemCode;
    }

    public static class MyResmeltItem {
        public int slotIndex;/**유저 각 상점별 고유 인덱스*/
        public int shopTableId;
        public String itemName;
        public boolean bought;
        public String itemCode;
    }

    public static class MyLinkweaponKeyItem {
        public int slotIndex;/**유저 각 상점별 고유 인덱스*/
        public int shopTableId;
        public String itemName;
        public boolean bought;
        public String itemCode;
    }

    //결제 아이템중 월정액
    public static class PackagePerMonth {
        public int slotIndex;/**유저 각 상점별 고유 인덱스*/
        public int iapTableId;
        public boolean bought;
        public String iapCode;
        public String scheduleStartTime;//최초 패키지 구매 했을때 날짜
        public String lastGettingTime;//마지막 보상 받았던 날짜(일단위로 받을수 있게)
        public int remainCount;//남은 갯수, 다른 패키지와는 달리 구매부터 매일 1씩 깍는 프로세싱 진행.

    }

    //계정당 정해진 갯수만큼만 구매 가능.
    public static class LimitPackage {
        public int slotIndex;/**유저 각 상점별 고유 인덱스*/
        public int iapTableId;
        public boolean bought;
        public int remainCount;
        public String iapCode;
    }

    //시즌 한정 상품
    public static class SeasonLimitPackage {
        public int slotIndex;/**유저 각 상점별 고유 인덱스*/
        public Long iapTableId;
        public boolean bought;
        public int remainCount;
        public String iapCode;
        public String seasonStartTime;
        public String seasonEndTime;
    }

    //메인샵
    public class MyShopMainInfos{
        public List<MyShopItem> myShopItemList; //링크포인트, 재료상자
        public List<MyGiftItem> myGiftItemList; //정해진 선물
        public List<MyEquipmentShopItem> myEquipmentShopItemList;//정해진 장비

        public void ResetShop(Long userId, List<ShopTable> shopTableList, List<GiftTable> giftTableList, List<HeroEquipmentsTable> heroEquipmentsTableList, HeroEquipmentClassProbabilityTable classValues, List<EquipmentOptionsInfoTable> optionsInfoTableList){

            List<ShopTable> probabilityLinkpoint_gold_list = new ArrayList<>();
            List<Double> probabilityLinkpoint_gold_chanceList = new ArrayList<>();

            List<ShopTable> probabilityLinkpoint_dia_list = new ArrayList<>();
            List<Double> probabilityLinkpoint_dia_chanceList = new ArrayList<>();

            List<ShopTable> probabilityMaterail_list = new ArrayList<>();
            List<Double> probabilityMaterail_chanceList = new ArrayList<>();

            List<ShopTable> probabilityGift_list = new ArrayList<>();
            List<Double> probabilityGift_chanceList = new ArrayList<>();

            List<ShopTable> probabilityEquipment_gold_list = new ArrayList<>();
            List<Double> probabilityEquipment_gold_chanceList = new ArrayList<>();

            List<ShopTable> probabilityEquipment_dia_list = new ArrayList<>();
            List<Double> probabilityEquipment_dia_chanceList = new ArrayList<>();

            for(ShopTable shopTable : shopTableList){
                String gettingItem = shopTable.getGettingItem();
                String currency = shopTable.getCurrency();
                double gettingChance = shopTable.getGettingChance();

                if(gettingItem.contains("linkPoint")){
                    if(currency.equals("gold")) {
                        probabilityLinkpoint_gold_list.add(shopTable);
                        probabilityLinkpoint_gold_chanceList.add(gettingChance);
                    }
                    else if(currency.equals("diamond")) {
                        probabilityLinkpoint_dia_list.add(shopTable);
                        probabilityLinkpoint_dia_chanceList.add(gettingChance);
                    }
                }
                else if(gettingItem.contains("material")) {
                    probabilityMaterail_list.add(shopTable);
                    probabilityMaterail_chanceList.add(gettingChance);
                }
                else if(gettingItem.contains("giftAll")) {
                    probabilityGift_list.add(shopTable);
                    probabilityGift_chanceList.add(gettingChance);
                }
                else if(gettingItem.contains("equipment")){
                    if(currency.equals("gold")){
                        probabilityEquipment_gold_list.add(shopTable);
                        probabilityEquipment_gold_chanceList.add(gettingChance);
                    }
                    else if(currency.equals("diamond")){
                        probabilityEquipment_dia_list.add(shopTable);
                        probabilityEquipment_dia_chanceList.add(gettingChance);
                    }
                }
            }
            //이전 상품 모두 삭제
            myShopItemList.clear();
            myEquipmentShopItemList.clear();
            myGiftItemList.clear();
            int slotIndex = 0;
            //1번 링크포인트(골드)
            int selectedIndex = MathHelper.RandomIndexWidthProbability(probabilityLinkpoint_gold_chanceList);
            ShopTable selectedTable = probabilityLinkpoint_gold_list.get(selectedIndex);
            MyShopItem myShopItem = new MyShopItem();
            myShopItem.shopTableId = selectedTable.getId();
            myShopItem.bought = false;
            myShopItem.itemName = selectedTable.getName();
            myShopItem.slotIndex = slotIndex++;
            myShopItemList.add(myShopItem);

            //2번 링크포인트(다이아)
            selectedIndex = MathHelper.RandomIndexWidthProbability(probabilityLinkpoint_dia_chanceList);
            selectedTable = probabilityLinkpoint_dia_list.get(selectedIndex);
            myShopItem = new MyShopItem();
            myShopItem.shopTableId = selectedTable.getId();
            myShopItem.itemName = selectedTable.getName();
            myShopItem.slotIndex = slotIndex++;
            myShopItem.bought = false;
            myShopItemList.add(myShopItem);

            //3번 재료상자
            selectedIndex = MathHelper.RandomIndexWidthProbability(probabilityMaterail_chanceList);
            selectedTable = probabilityMaterail_list.get(selectedIndex);
            myShopItem = new MyShopItem();
            myShopItem.shopTableId = selectedTable.getId();
            myShopItem.itemName = selectedTable.getName();
            myShopItem.slotIndex = slotIndex++;
            myShopItem.bought = false;
            myShopItemList.add(myShopItem);

            //4~6번 선물
            List<GiftTable> giftTableListCopy = new ArrayList<>();
            giftTableListCopy.addAll(giftTableList);
            giftTableListCopy.remove(25);
            for(int i = 0; i < 3; i++){
                selectedIndex = MathHelper.RandomIndexWidthProbability(probabilityGift_chanceList);
                selectedTable = probabilityGift_list.get(selectedIndex);
                int selectedRandomIndex = (int)MathHelper.Range(0, giftTableListCopy.size());
                GiftTable giftTable = giftTableListCopy.get(selectedRandomIndex);
                giftTableListCopy.remove(selectedRandomIndex);
                MyGiftItem myGiftItem = new MyGiftItem();
                myGiftItem.shopTableId = selectedTable.getId();
                myGiftItem.itemName = giftTable.getGiftName();//selectedTable.getName();
                myGiftItem.bought = false;
                myGiftItem.giftItemCode = giftTable.getCode();
                myGiftItem.slotIndex = slotIndex++;
                myGiftItemList.add(myGiftItem);
            }

            //7~9번 장비(다이아)
            for(int i = 0; i < 3; i++) {
                selectedIndex = MathHelper.RandomIndexWidthProbability(probabilityEquipment_dia_chanceList);
                selectedTable = probabilityEquipment_dia_list.get(selectedIndex);

                String gettingItem = selectedTable.getGettingItem();

                HeroEquipmentInventory generatedEquipment = null;
                if(gettingItem.contains("Ancient")) {
                    List<HeroEquipmentsTable> probabilityEquipmentsList = EquipmentCalculate.GetProbabilityList(heroEquipmentsTableList, EquipmentItemCategory.ALL,"Ancient");
                    generatedEquipment = generateShopHeroEquipment(userId, classValues, optionsInfoTableList, gettingItem, probabilityEquipmentsList);
                }
                else if(gettingItem.contains("Divine")) {
                    List<HeroEquipmentsTable> probabilityEquipmentsList = EquipmentCalculate.GetProbabilityList(heroEquipmentsTableList, EquipmentItemCategory.ALL,"Divine");
                    generatedEquipment = generateShopHeroEquipment(userId, classValues, optionsInfoTableList, gettingItem, probabilityEquipmentsList);
                }
                else if(gettingItem.contains("Legend")||gettingItem.contains("legend")) {
                    List<HeroEquipmentsTable> probabilityEquipmentsList = EquipmentCalculate.GetProbabilityList(heroEquipmentsTableList, EquipmentItemCategory.ALL,"Legend");
                    generatedEquipment = generateShopHeroEquipment(userId, classValues, optionsInfoTableList, gettingItem, probabilityEquipmentsList);
                }
                else if(gettingItem.contains("Hero")) {
                    List<HeroEquipmentsTable> probabilityEquipmentsList = EquipmentCalculate.GetProbabilityList(heroEquipmentsTableList, EquipmentItemCategory.ALL,"Hero");
                    generatedEquipment = generateShopHeroEquipment(userId, classValues, optionsInfoTableList, gettingItem, probabilityEquipmentsList);
                }
                else if(gettingItem.contains("Rare")) {
                    List<HeroEquipmentsTable> probabilityEquipmentsList = EquipmentCalculate.GetProbabilityList(heroEquipmentsTableList, EquipmentItemCategory.ALL,"Rare");
                    generatedEquipment = generateShopHeroEquipment(userId, classValues, optionsInfoTableList, gettingItem, probabilityEquipmentsList);
                }
                int item_id = generatedEquipment.getItem_Id();
                MyEquipmentShopItem myEquipmentShopItem = new MyEquipmentShopItem();
                myEquipmentShopItem.shopTableId = selectedTable.getId();
                HeroEquipmentsTable heroEquipmentsTable = heroEquipmentsTableList.stream().filter(a -> a.getId() == item_id).findAny().orElse(null);
                myEquipmentShopItem.itemName = heroEquipmentsTable.getName();
                myEquipmentShopItem.bought = false;
                myEquipmentShopItem.item_id = generatedEquipment.getItem_Id();
                myEquipmentShopItem.itemClassValue = generatedEquipment.getItemClassValue();
                myEquipmentShopItem.decideDefaultAbilityValue = generatedEquipment.getDecideDefaultAbilityValue();
                myEquipmentShopItem.decideSecondAbilityValue = generatedEquipment.getDecideSecondAbilityValue();
                myEquipmentShopItem.itemClass = generatedEquipment.getItemClass();
                myEquipmentShopItem.optionIds = generatedEquipment.getOptionIds();
                myEquipmentShopItem.optionValues = generatedEquipment.getOptionValues();
                myEquipmentShopItem.slotIndex = slotIndex++;
                myEquipmentShopItemList.add(myEquipmentShopItem);
            }
            //10~12번 장비(골드)
            for(int i = 0; i < 3; i++) {
                selectedIndex = MathHelper.RandomIndexWidthProbability(probabilityEquipment_gold_chanceList);
                selectedTable = probabilityEquipment_gold_list.get(selectedIndex);

                String gettingItem = selectedTable.getGettingItem();

                HeroEquipmentInventory generatedEquipment = null;
                if(gettingItem.contains("Ancient")) {
                    List<HeroEquipmentsTable> probabilityEquipmentsList = EquipmentCalculate.GetProbabilityList(heroEquipmentsTableList, EquipmentItemCategory.ALL,"Ancient");
                    generatedEquipment = generateShopHeroEquipment(userId, classValues, optionsInfoTableList, gettingItem, probabilityEquipmentsList);

                }
                else if(gettingItem.contains("Divine")) {
                    List<HeroEquipmentsTable> probabilityEquipmentsList = EquipmentCalculate.GetProbabilityList(heroEquipmentsTableList, EquipmentItemCategory.ALL,"Divine");
                    generatedEquipment = generateShopHeroEquipment(userId, classValues, optionsInfoTableList, gettingItem, probabilityEquipmentsList);
                }
                else if(gettingItem.contains("Legend")||gettingItem.contains("legend")) {
                    List<HeroEquipmentsTable> probabilityEquipmentsList = EquipmentCalculate.GetProbabilityList(heroEquipmentsTableList, EquipmentItemCategory.ALL,"Legend");
                    generatedEquipment = generateShopHeroEquipment(userId, classValues, optionsInfoTableList, gettingItem, probabilityEquipmentsList);
                }
                else if(gettingItem.contains("Hero")) {
                    List<HeroEquipmentsTable> probabilityEquipmentsList = EquipmentCalculate.GetProbabilityList(heroEquipmentsTableList, EquipmentItemCategory.ALL,"Hero");
                    generatedEquipment = generateShopHeroEquipment(userId, classValues, optionsInfoTableList, gettingItem, probabilityEquipmentsList);
                }
                else if(gettingItem.contains("Rare")) {
                    List<HeroEquipmentsTable> probabilityEquipmentsList = EquipmentCalculate.GetProbabilityList(heroEquipmentsTableList, EquipmentItemCategory.ALL,"Rare");
                    generatedEquipment = generateShopHeroEquipment(userId, classValues, optionsInfoTableList, gettingItem, probabilityEquipmentsList);
                }
                int item_id = generatedEquipment.getItem_Id();
                MyEquipmentShopItem myEquipmentShopItem = new MyEquipmentShopItem();
                myEquipmentShopItem.shopTableId = selectedTable.getId();
                HeroEquipmentsTable heroEquipmentsTable = heroEquipmentsTableList.stream().filter(a -> a.getId() == item_id).findAny().orElse(null);
                myEquipmentShopItem.itemName = heroEquipmentsTable.getName();
                myEquipmentShopItem.bought = false;
                myEquipmentShopItem.item_id = generatedEquipment.getItem_Id();
                myEquipmentShopItem.itemClassValue = generatedEquipment.getItemClassValue();
                myEquipmentShopItem.decideDefaultAbilityValue = generatedEquipment.getDecideDefaultAbilityValue();
                myEquipmentShopItem.decideSecondAbilityValue = generatedEquipment.getDecideSecondAbilityValue();
                myEquipmentShopItem.itemClass = generatedEquipment.getItemClass();
                myEquipmentShopItem.optionIds = generatedEquipment.getOptionIds();
                myEquipmentShopItem.optionValues = generatedEquipment.getOptionValues();
                myEquipmentShopItem.slotIndex = slotIndex++;
                myEquipmentShopItemList.add(myEquipmentShopItem);
            }
        }
    }
    //아레나 샵
    public class MyArenaShopInfos{
        public List<MyCharacterPiece> myCharacterPieceList; //정해진 케릭터 조각,
        public List<MyGiftItem> myGiftItemList; //정해진 선물
        public List<MyEnchantItem> myEnchantItemList; //정해진 강화석
        public List<MyResmeltItem> myResmeltItemList; //정해진 제련석

        public void ResetShop(List<ArenaShopTable> arenaShopTableList, List<herostable> herosTablesList,  List<GiftTable> giftTableList, List<SpendableItemInfoTable> spendableItemInfoTableList) {

            //이전 상품 모두 삭제
            myCharacterPieceList.clear();
            myGiftItemList.clear();
            myEnchantItemList.clear();
            myResmeltItemList.clear();
            int slotIndex = 0;
            List<herostable> herosTablesListCopy = new ArrayList<>();
            for(herostable herostable : herosTablesList) {
                if(herostable.getTier() > 1 && !herostable.getCode().equals("hero"))
                    herosTablesListCopy.add(herostable);
            }

            List<GiftTable> giftTableListCopy = new ArrayList<>();
            giftTableListCopy.addAll(giftTableList);

            for(ArenaShopTable arenaShopTable : arenaShopTableList){
                String gettingItem = arenaShopTable.getGettingItem();
                if(gettingItem.equals("characterPieceAll")) {
                    int randIndex = (int)MathHelper.Range(0, herosTablesListCopy.size());
                    herostable heroInfo = herosTablesListCopy.get(randIndex);
                    MyCharacterPiece myCharacterPiece = new MyCharacterPiece();
                    myCharacterPiece.shopTableId = arenaShopTable.getId();
                    myCharacterPiece.itemName = heroInfo.getName();
                    myCharacterPiece.bought = false;
                    myCharacterPiece.characterPieceCode = heroInfo.getCode();
                    myCharacterPiece.slotIndex = slotIndex++;
                    myCharacterPieceList.add(myCharacterPiece);
                    herosTablesListCopy.remove(heroInfo);
                }
                else if(gettingItem.equals("giftAll")) {
                    int randIndex = (int)MathHelper.Range(0,   giftTableListCopy.size());
                    GiftTable giftTable = giftTableListCopy.get(randIndex);
                    MyGiftItem myGiftItem = new MyGiftItem();
                    myGiftItem.shopTableId = arenaShopTable.getId();
                    myGiftItem.itemName = giftTable.getGiftName();
                    myGiftItem.bought = false;
                    myGiftItem.giftItemCode = giftTable.getCode();
                    myGiftItem.slotIndex = slotIndex++;
                    myGiftItemList.add(myGiftItem);
                    giftTableListCopy.remove(giftTable);
                }
                else if(gettingItem.contains("enchant")) {
                   SpendableItemInfoTable spendableItemInfoTable = spendableItemInfoTableList.stream().filter(a -> a.getCode().equals(gettingItem)).findAny().orElse(null);
                   MyEnchantItem myEnchantItem = new MyEnchantItem();
                   myEnchantItem.shopTableId = arenaShopTable.getId();
                   myEnchantItem.itemName = spendableItemInfoTable.getName();
                   myEnchantItem.bought = false;
                   myEnchantItem.itemCode = gettingItem;
                   myEnchantItem.slotIndex = slotIndex++;
                   myEnchantItemList.add(myEnchantItem);
                }
                else if(gettingItem.contains("resmelt")) {
                    SpendableItemInfoTable spendableItemInfoTable = spendableItemInfoTableList.stream().filter(a -> a.getCode().equals(gettingItem)).findAny().orElse(null);
                    MyResmeltItem myResmeltItem = new MyResmeltItem();
                    myResmeltItem.shopTableId = arenaShopTable.getId();
                    myResmeltItem.itemName = spendableItemInfoTable.getName();
                    myResmeltItem.bought = false;
                    myResmeltItem.itemCode = gettingItem;
                    myResmeltItem.slotIndex = slotIndex++;
                    myResmeltItemList.add(myResmeltItem);
                }
            }
        }
    }
    //고대상점
    public class MyAncientShopInfos{
        public List<MyShopItem> myEquipmentList; //영웅장비 상자, 신성장비 상자, 고대장비 상자
        public List<MyLinkweaponKeyItem> myLinkweaponKeyItemList; //링크웨폰 강화 키
        public List<MyShopItem> myStempList;  //영웅 인장 상자, 신성 인장 상자, 고대 인장 상자

        public void ResetShop(List<AncientShopTable> ancientShopTableList) {
            //이전 상품 모두 삭제
            myEquipmentList.clear();
            myLinkweaponKeyItemList.clear();
            myStempList.clear();
            int slotIndex = 0;
            for(AncientShopTable ancientShopTable : ancientShopTableList){
                String code = ancientShopTable.getCode();
                if(code.contains("equipmentBox")){

                    MyShopItem myShopItem = new MyShopItem();
                    myShopItem.shopTableId = ancientShopTable.getId();
                    myShopItem.itemName = ancientShopTable.getName();
                    myShopItem.bought = false;
                    myShopItem.slotIndex = slotIndex++;
                    myEquipmentList.add(myShopItem);
                }
                else if(code.contains("linkweapon")){
                    //LinkweaponTalentsTable linkweaponTalent = linkweaponTalentsTableList.stream().filter(a -> a.get().equals(ancientShopTable.getGettingItem())).findAny().orElse(null);
                    MyLinkweaponKeyItem myLinkweaponKeyItem = new MyLinkweaponKeyItem();
                    myLinkweaponKeyItem.shopTableId = ancientShopTable.getId();
                    myLinkweaponKeyItem.itemName = ancientShopTable.getName();
                    myLinkweaponKeyItem.bought = false;
                    myLinkweaponKeyItem.itemCode = ancientShopTable.getGettingItem();
                    myLinkweaponKeyItem.slotIndex = slotIndex++;
                    myLinkweaponKeyItemList.add(myLinkweaponKeyItem);
                }
                else if(code.contains("stamp")){
                    MyShopItem myShopItem = new MyShopItem();
                    myShopItem.shopTableId = ancientShopTable.getId();
                    myShopItem.itemName = ancientShopTable.getName();
                    myShopItem.bought = false;
                    myShopItem.slotIndex = slotIndex++;
                    myStempList.add(myShopItem);
                }
            }
        }
    }
    //화폐상점
    public class MyCurrencyShopInfos{
        public List<MyShopItem> myCurrencyList;

        public void ResetShop(List<CurrencyShopTable> currencyShopTableList) {
            //이전 상품 모두 삭제
            myCurrencyList.clear();
            int slotIndex = 0;
            for(CurrencyShopTable currencyShopTable : currencyShopTableList) {
                MyShopItem myShopItem = new MyShopItem();
                myShopItem.shopTableId = currencyShopTable.getId();
                myShopItem.itemName = currencyShopTable.getName();
                myShopItem.bought = false;
                myShopItem.slotIndex = slotIndex++;
                myCurrencyList.add(myShopItem);
            }
        }
    }
    //조각교환소
    public class MyPieceShopInfos{
        public List<MyCharacterPiece> myCharacterPieceList;

        public void ResetShop(List<PieceShopTable> pieceShopTableList, List<herostable> herostableList) {
            //이전 상품 모두 삭제
            myCharacterPieceList.clear();
            int slotIndex = 0;

            for(PieceShopTable pieceShopTable : pieceShopTableList){
                String gettingItem = pieceShopTable.getGettingItem();
                herostable heroInfo = herostableList.stream().filter(i -> gettingItem.contains(i.getCode())).findAny().orElse(null);
                if(heroInfo == null){
                    throw new MyCustomException("Fail! -> Cause: herostable not find.", ResponseErrorCode.NOT_FIND_DATA);
                }
                MyCharacterPiece myCharacterPiece = new MyCharacterPiece();
                myCharacterPiece.shopTableId = pieceShopTable.getId();
                myCharacterPiece.itemName = heroInfo.getName();
                myCharacterPiece.bought = false;
                myCharacterPiece.characterPieceCode = heroInfo.getCode();
                myCharacterPiece.slotIndex = slotIndex++;
                myCharacterPieceList.add(myCharacterPiece);
            }
        }
    }
    //어둠의 보주 샵
    public class MyDarkObeShopInfos{
        public List<MyEnchantItem> myEnchantItemList; //정해진 강화석 삭제 예정
        public List<MyResmeltItem> myResmeltItemList; //정해진 제련석
        public List<MyCharacterPiece> myCharacterPieceList;
        public List<MyEquipmentShopItem> myEquipmentShopItemList;//정해진 장비
        public List<MyShopItem> myEquipmentList; //균열보스 무기 상자 만들어야됨

        public void ResetShop(Long userId, List<DarkObeShopTable> darkObeShopTableList, List<HeroEquipmentsTable> heroEquipmentsTableList, HeroEquipmentClassProbabilityTable classValues, List<EquipmentOptionsInfoTable> optionsInfoTableList, List<SpendableItemInfoTable> spendableItemInfoTableList, List<herostable> herostableList, List<PassiveItemTable> passiveItemTableList){
            if(myEnchantItemList == null)
                myEnchantItemList = new ArrayList<>();
            if(myResmeltItemList == null)
                myResmeltItemList = new ArrayList<>();
            if(myCharacterPieceList == null)
                myCharacterPieceList = new ArrayList<>();
            if(myEquipmentShopItemList == null)
                myEquipmentShopItemList = new ArrayList<>();
            if(myEquipmentList == null)
                myEquipmentList = new ArrayList<>();

            myEnchantItemList.clear();
            myResmeltItemList.clear();
            myCharacterPieceList.clear();
            myEquipmentShopItemList.clear();
            myEquipmentList.clear();
            int slotIndex = 0;
            for(DarkObeShopTable darkObeShopTable : darkObeShopTableList) {
                String gettingItem = darkObeShopTable.getGettingItem();

                if(gettingItem.contains("enchant")) {
                    SpendableItemInfoTable spendableItemInfoTable = spendableItemInfoTableList.stream().filter(a -> a.getCode().equals(gettingItem)).findAny().orElse(null);
                    MyEnchantItem myEnchantItem = new MyEnchantItem();
                    myEnchantItem.shopTableId = darkObeShopTable.getId();
                    myEnchantItem.itemName = spendableItemInfoTable.getName();
                    myEnchantItem.bought = false;
                    myEnchantItem.itemCode = gettingItem;
                    myEnchantItem.slotIndex = slotIndex++;
                    myEnchantItemList.add(myEnchantItem);
                }
                else if(gettingItem.contains("resmelt")) {
                    SpendableItemInfoTable spendableItemInfoTable = spendableItemInfoTableList.stream().filter(a -> a.getCode().equals(gettingItem)).findAny().orElse(null);
                    MyResmeltItem myResmeltItem = new MyResmeltItem();
                    myResmeltItem.shopTableId = darkObeShopTable.getId();
                    myResmeltItem.itemName = spendableItemInfoTable.getName();
                    myResmeltItem.bought = false;
                    myResmeltItem.itemCode = gettingItem;
                    myResmeltItem.slotIndex = slotIndex++;
                    myResmeltItemList.add(myResmeltItem);
                }
                else if(gettingItem.contains("characterPiece")){
                    herostable heroInfo = herostableList.stream().filter(i -> gettingItem.contains(i.getCode())).findAny().orElse(null);
                    if(heroInfo == null){
                        throw new MyCustomException("Fail! -> Cause: herostable not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    MyCharacterPiece myCharacterPiece = new MyCharacterPiece();
                    myCharacterPiece.shopTableId = darkObeShopTable.getId();
                    myCharacterPiece.itemName = heroInfo.getName();
                    myCharacterPiece.bought = false;
                    myCharacterPiece.characterPieceCode = heroInfo.getCode();
                    myCharacterPiece.slotIndex = slotIndex++;
                    myCharacterPieceList.add(myCharacterPiece);
                }
                else if(gettingItem.contains("99") || gettingItem.contains("97") || gettingItem.equals("passiveItem_00_10")){
                    HeroEquipmentInventory generatedEquipment = null;

                    String selectedClass = "C";
                    int classValue = (int) EquipmentCalculate.GetClassValueFromClassCategory(selectedClass, classValues);

                    if(gettingItem.equals("passiveItem_00_10")){
                        PassiveItemTable passiveItemTable = passiveItemTableList.stream().filter(a -> a.getCode().equals(gettingItem)).findAny().orElse(null);
                        MyEquipmentShopItem myEquipmentShopItem = new MyEquipmentShopItem();
                        myEquipmentShopItem.shopTableId = darkObeShopTable.getId();
                        myEquipmentShopItem.itemName = passiveItemTable.getName();
                        myEquipmentShopItem.bought = false;
                        myEquipmentShopItem.item_id = passiveItemTable.getId();
                        myEquipmentShopItem.itemClassValue = classValue;
                        myEquipmentShopItem.decideDefaultAbilityValue = 0;
                        myEquipmentShopItem.decideSecondAbilityValue = 0;
                        myEquipmentShopItem.itemClass = selectedClass;
                        myEquipmentShopItem.optionIds = "";
                        myEquipmentShopItem.optionValues = "";
                        myEquipmentShopItem.slotIndex = slotIndex++;
                        myEquipmentShopItemList.add(myEquipmentShopItem);
                    }
                    else {
                        HeroEquipmentsTable heroEquipmentsTable = heroEquipmentsTableList.stream().filter(a -> a.getCode().equals(gettingItem)).findAny().orElse(null);
                        generatedEquipment = EquipmentCalculate.CreateEquipment(userId, heroEquipmentsTable, selectedClass, classValue, optionsInfoTableList);
                        MyEquipmentShopItem myEquipmentShopItem = new MyEquipmentShopItem();
                        myEquipmentShopItem.shopTableId = darkObeShopTable.getId();
                        myEquipmentShopItem.itemName = heroEquipmentsTable.getName();
                        myEquipmentShopItem.bought = false;
                        myEquipmentShopItem.item_id = generatedEquipment.getItem_Id();
                        myEquipmentShopItem.itemClassValue = generatedEquipment.getItemClassValue();
                        myEquipmentShopItem.decideDefaultAbilityValue = generatedEquipment.getDecideDefaultAbilityValue();
                        myEquipmentShopItem.decideSecondAbilityValue = generatedEquipment.getDecideSecondAbilityValue();
                        myEquipmentShopItem.itemClass = generatedEquipment.getItemClass();
                        myEquipmentShopItem.optionIds = generatedEquipment.getOptionIds();
                        myEquipmentShopItem.optionValues = generatedEquipment.getOptionValues();
                        myEquipmentShopItem.slotIndex = slotIndex++;
                        myEquipmentShopItemList.add(myEquipmentShopItem);
                    }

                }
                else if(gettingItem.contains("fieldBoss")){
                    MyShopItem myShopItem = new MyShopItem();
                    myShopItem.shopTableId = darkObeShopTable.getId();
                    myShopItem.itemName = darkObeShopTable.getName();
                    myShopItem.bought = false;
                    myShopItem.slotIndex = slotIndex++;
                    myEquipmentList.add(myShopItem);
                }
            }
        }
    }

    //특별상품샵 2021.01.07 작업 당시 기획(월정액 패키지 1, 스타트업 패키지 2 - 영웅 성장, 동료 성장, 한정 패키지 1 - 소환지원 패키지)
    public class PackageSpecialInfos {
        public List<PackagePerMonth> packagePerMonthList; //월정액 패키지
        public List<LimitPackage> fixLimitPackageList;    //스타트업 패키지
        public List<SeasonLimitPackage> seasonLimitPackageList;     //한정 패키지

        public void CheckSeasonLimitPackage() {
            LocalDateTime now = LocalDateTime.now();
            //한정 패키지 상품은 기간이 지나면 사라진다.
            List<SeasonLimitPackage> deleteSeasonLimitPackageTempList = new ArrayList<>();
            for(SeasonLimitPackage seasonLimitPackage : seasonLimitPackageList) {
                if(!Strings.isNullOrEmpty(seasonLimitPackage.seasonEndTime)) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime seasonEndTime = LocalDateTime.parse(seasonLimitPackage.seasonEndTime, formatter);

                    if(now.isAfter(seasonEndTime)) {
                        deleteSeasonLimitPackageTempList.add(seasonLimitPackage);
                    }
                }
            }
            seasonLimitPackageList.removeAll(deleteSeasonLimitPackageTempList);
        }
        public void ResetShop(List<PackageSpecialShopTable> packageSpecialShopTableList, List<SeasonLimitPackageSchedule> seasonLimitPackageScheduleList, List<IapTable> iapTableList){
            int slotIndex = 0;
            //월정액 상품의 경우 이미 유저가 구매후 기간이 남아 있다면 바꾸지 않는다. 추후 월정액 상품 기간이 지나고 모든 상품이 해당 유저의 우편함으로 들어갈때
            //리셋 시켜준다.
            List<PackagePerMonth> deletePackagePerMonthTempList = new ArrayList<>();
            for(PackagePerMonth packagePerMonth : packagePerMonthList) {
                if(packagePerMonth.bought && packagePerMonth.remainCount > 0)
                    continue;
                deletePackagePerMonthTempList.add(packagePerMonth);
            }
            packagePerMonthList.removeAll(deletePackagePerMonthTempList);

            //스타트업 상품은 한번 구매하면 초기화 되지 않는다.
            List<LimitPackage> deleteStartupPackageTempList = new ArrayList<>();
            for(LimitPackage limitPackage : fixLimitPackageList) {
                if(limitPackage.bought)
                    continue;
                deleteStartupPackageTempList.add(limitPackage);
            }
            fixLimitPackageList.removeAll(deleteStartupPackageTempList);
            LocalDateTime now = LocalDateTime.now();
            //한정 패키지 상품은 기간이 지나면 사라진다.
            List<SeasonLimitPackage> deleteSeasonLimitPackageTempList = new ArrayList<>();
            for(SeasonLimitPackage seasonLimitPackage : seasonLimitPackageList) {
                if(!Strings.isNullOrEmpty(seasonLimitPackage.seasonEndTime)) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime seasonEndTime = LocalDateTime.parse(seasonLimitPackage.seasonEndTime, formatter);

                    if(now.isAfter(seasonEndTime)) {
                        deleteSeasonLimitPackageTempList.add(seasonLimitPackage);
                    }
                }
            }
            seasonLimitPackageList.removeAll(deleteSeasonLimitPackageTempList);
            //월정액, 스타트업 패키지 셋팅(정해진 테이블 정보로 셋팅)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for(PackageSpecialShopTable packageSpecialShopTable : packageSpecialShopTableList) {
                String iapTableCode = packageSpecialShopTable.getIapTableCode();
                IapTable iapTable = iapTableList.stream().filter( a -> a.getCode().equals(iapTableCode))
                        .findAny()
                        .orElse(null);
                String iapType = iapTable.getType();
                if(iapType.equals("packagePerMonth")) {
                    PackagePerMonth packagePerMonth = packagePerMonthList.stream().filter( a -> a.iapCode.equals(iapTableCode)).findAny()
                            .orElse(null);
                    if(packagePerMonth == null) {
                        packagePerMonth = new PackagePerMonth();
                        packagePerMonth.slotIndex = slotIndex;
                        packagePerMonth.iapCode = iapTableCode;
                        packagePerMonth.bought = false;
                        packagePerMonth.iapTableId = iapTable.getId();
                        LocalDateTime monthlyPackageScheduleStartTime =  now.with(TemporalAdjusters.firstDayOfMonth());
                        monthlyPackageScheduleStartTime = LocalDateTime.of(monthlyPackageScheduleStartTime.toLocalDate(), LocalTime.of(5,0,0));
                        packagePerMonth.lastGettingTime = monthlyPackageScheduleStartTime.format(formatter);
                        packagePerMonth.scheduleStartTime = monthlyPackageScheduleStartTime.format(formatter);
                        packagePerMonth.remainCount = Defines.PACKAGE_MONTH_RECEIVABLE_COUNT;
                        packagePerMonthList.add(packagePerMonth);
                    }
                }
                else if(iapType.equals("packageLimit")) {
                    LimitPackage limitPackage = fixLimitPackageList.stream().filter(a -> a.iapCode.equals(iapTableCode)).findAny()
                            .orElse(null);
                    if(limitPackage == null){
                        limitPackage = new LimitPackage();
                        limitPackage.slotIndex = slotIndex;
                        limitPackage.iapTableId = iapTable.getId();
                        limitPackage.bought = false;
                        limitPackage.remainCount = iapTable.getLimit();
                        limitPackage.iapCode = iapTableCode;
                        fixLimitPackageList.add(limitPackage);
                    }
                }
                slotIndex++;
            }
            if(seasonLimitPackageScheduleList != null){
                //한정 패키지 셋팅(운영측에서 스케쥴링된 테이블로 셋팅)
                for(SeasonLimitPackageSchedule seasonLimitPackageSchedule : seasonLimitPackageScheduleList) {
                    String iapTableCode = seasonLimitPackageSchedule.getIapTableCode();
                    IapTable iapTable = iapTableList.stream().filter( a -> a.getCode().equals(iapTableCode))
                            .findAny()
                            .orElse(null);
                    SeasonLimitPackage seasonLimitPackage = seasonLimitPackageList.stream().filter(a -> a.iapCode.equals(iapTableCode)).findAny()
                            .orElse(null);
                    if(seasonLimitPackage == null){
                        seasonLimitPackage = new SeasonLimitPackage();
                        seasonLimitPackage.slotIndex = slotIndex;
                        seasonLimitPackage.iapTableId = seasonLimitPackageSchedule.getId();
                        seasonLimitPackage.bought = false;
                        seasonLimitPackage.remainCount = iapTable.getLimit();
                        seasonLimitPackage.iapCode = iapTableCode;
                        seasonLimitPackage.seasonStartTime = seasonLimitPackageSchedule.getSeasonStartTime().format(formatter);
                        seasonLimitPackage.seasonEndTime = seasonLimitPackageSchedule.getSeasonEndTime().format(formatter);
                        seasonLimitPackageList.add(seasonLimitPackage);
                    }
                    slotIndex++;
                }
            }
        }
    }
    //일일 패키지
    public class PackageDailyInfos {
        public List<LimitPackage> packageList;

        public void ResetShop(List<PackageDailyShopTable> packageDailyShopTableList, List<IapTable> iapTableList) {
            packageList.clear();
            int slotIndex = 0;
            for(PackageDailyShopTable packageDailyShopTable : packageDailyShopTableList) {
                String iapTableCode = packageDailyShopTable.getIapTableCode();
                IapTable iapTable = iapTableList.stream().filter( a -> a.getCode().equals(iapTableCode))
                        .findAny()
                        .orElse(null);

                LimitPackage limitPackage = new LimitPackage();
                limitPackage.slotIndex = slotIndex++;
                limitPackage.iapTableId = packageDailyShopTable.getId();
                limitPackage.bought = false;
                limitPackage.remainCount =iapTable.getLimit();
                limitPackage.iapCode = iapTableCode;
                packageList.add(limitPackage);
            }
        }
    }
    //주간 패키지
    public class PackageWeeklyInfos {
        public List<LimitPackage> packageList;

        public void ResetShop(List<PackageWeeklyShopTable> packageWeeklyShopTableList, List<IapTable> iapTableList) {
            packageList.clear();
            int slotIndex = 0;
            for(PackageWeeklyShopTable packageWeeklyShopTable : packageWeeklyShopTableList) {
                String iapTableCode = packageWeeklyShopTable.getIapTableCode();
                IapTable iapTable = iapTableList.stream().filter( a -> a.getCode().equals(iapTableCode))
                        .findAny()
                        .orElse(null);

                LimitPackage limitPackage = new LimitPackage();
                limitPackage.slotIndex = slotIndex++;
                limitPackage.iapTableId = packageWeeklyShopTable.getId();
                limitPackage.bought = false;
                limitPackage.remainCount =iapTable.getLimit();
                limitPackage.iapCode = iapTableCode;
                packageList.add(limitPackage);
            }
        }
    }
    //월간 패키지
    public class PackageMonthlyInfos {
        public List<LimitPackage> packageList;

        public void ResetShop(List<PackageMonthlyShopTable> packageMonthlyShopTableList, List<IapTable> iapTableList) {
            packageList.clear();
            int slotIndex = 0;
            for(PackageMonthlyShopTable packageMonthlyShopTable : packageMonthlyShopTableList) {
                String iapTableCode = packageMonthlyShopTable.getIapTableCode();
                IapTable iapTable = iapTableList.stream().filter( a -> a.getCode().equals(iapTableCode))
                        .findAny()
                        .orElse(null);

                LimitPackage limitPackage = new LimitPackage();
                limitPackage.slotIndex = slotIndex++;
                limitPackage.iapTableId = packageMonthlyShopTable.getId();
                limitPackage.bought = false;
                limitPackage.remainCount =iapTable.getLimit();
                limitPackage.iapCode = iapTableCode;
                packageList.add(limitPackage);
            }
        }
    }

    public MyShopMainInfos myShopMainInfos;
    public MyArenaShopInfos myArenaShopInfos;
    public MyAncientShopInfos myAncientShopInfos;
    public MyCurrencyShopInfos myCurrencyShopInfos;
    public MyPieceShopInfos myPieceShopInfos;
    public MyDarkObeShopInfos myDarkObeShopInfos;
    //패키지 상품들
    public PackageSpecialInfos packageSpecialInfos;
    public PackageDailyInfos packageDailyInfos;
    public PackageWeeklyInfos packageWeeklyInfos;
    public PackageMonthlyInfos packageMonthlyInfos;

    public void ResetAllShop(Long userId, List<ShopTable> shopTableList, List<ArenaShopTable> arenaShopTableList, List<GiftTable> giftTableList, List<HeroEquipmentsTable> heroEquipmentsTableList, HeroEquipmentClassProbabilityTable classValues, List<EquipmentOptionsInfoTable> optionsInfoTableList, List<herostable> herosTableList, List<AncientShopTable> ancientShopTableList, List<CurrencyShopTable> currencyShopTableList, List<SpendableItemInfoTable> spendableItemInfoTableList, List<PieceShopTable> pieceShopTableList, List<DarkObeShopTable> darkObeShopTableList, List<PassiveItemTable> passiveItemTableList){

        myShopMainInfos.ResetShop(userId, shopTableList, giftTableList, heroEquipmentsTableList, classValues, optionsInfoTableList);
        myArenaShopInfos.ResetShop(arenaShopTableList, herosTableList, giftTableList, spendableItemInfoTableList);
        myAncientShopInfos.ResetShop(ancientShopTableList);
        myCurrencyShopInfos.ResetShop(currencyShopTableList);
        myPieceShopInfos.ResetShop(pieceShopTableList, herosTableList);
        myDarkObeShopInfos.ResetShop(userId, darkObeShopTableList, heroEquipmentsTableList, classValues, optionsInfoTableList, spendableItemInfoTableList, herosTableList, passiveItemTableList);
    }
    public void ResetMainShop(Long userId, List<ShopTable> shopTableList, List<GiftTable> giftTableList, List<HeroEquipmentsTable> heroEquipmentsTableList, HeroEquipmentClassProbabilityTable classValues, List<EquipmentOptionsInfoTable> optionsInfoTableList){
        myShopMainInfos.ResetShop(userId, shopTableList, giftTableList, heroEquipmentsTableList, classValues, optionsInfoTableList);
    }
    public void ResetArenaShop(List<ArenaShopTable> arenaShopTableList, List<herostable> herosTableList, List<GiftTable> giftTableList, List<SpendableItemInfoTable> spendableItemInfoTableList){
        myArenaShopInfos.ResetShop(arenaShopTableList, herosTableList, giftTableList, spendableItemInfoTableList);
    }
    public void ResetAncientShop(List<AncientShopTable> ancientShopTableList){
        myAncientShopInfos.ResetShop(ancientShopTableList);
    }
    public void ResetCurrencyShop(List<CurrencyShopTable> currencyShopTableList){
        myCurrencyShopInfos.ResetShop(currencyShopTableList);
    }
    public void ResetPieceShop(List<PieceShopTable> pieceShopTableList, List<herostable> herostableList){
        myPieceShopInfos.ResetShop(pieceShopTableList, herostableList);
    }
    public void ResetDarkObeShop(Long userId, List<DarkObeShopTable> darkObeShopTableList, List<HeroEquipmentsTable> heroEquipmentsTableList, HeroEquipmentClassProbabilityTable classValues, List<EquipmentOptionsInfoTable> optionsInfoTableList, List<SpendableItemInfoTable> spendableItemInfoTableList, List<herostable> herosTableList, List<PassiveItemTable> passiveItemTableList){
        myDarkObeShopInfos.ResetShop(userId, darkObeShopTableList, heroEquipmentsTableList, classValues, optionsInfoTableList, spendableItemInfoTableList, herosTableList,passiveItemTableList);
    }

    public void CheckSeasonLimitPackage() {
        packageSpecialInfos.CheckSeasonLimitPackage();
    }

    public void ResetPackageSpecialInfos(List<PackageSpecialShopTable> packageSpecialShopTableList, List<SeasonLimitPackageSchedule> seasonLimitPackageScheduleList, List<IapTable> iapTableList){
        packageSpecialInfos.ResetShop( packageSpecialShopTableList, seasonLimitPackageScheduleList, iapTableList);
    }
    public void ResetPackageDailyInfos(List<PackageDailyShopTable> packageDailyShopTableList, List<IapTable> iapTableList){
        packageDailyInfos.ResetShop(packageDailyShopTableList, iapTableList);
    }
    public void ResetPackageWeeklyInfos(List<PackageWeeklyShopTable> packageWeeklyShopTableList, List<IapTable> iapTableList){
        packageWeeklyInfos.ResetShop(packageWeeklyShopTableList, iapTableList);
    }
    public void ResetPackageMonthlyInfos(List<PackageMonthlyShopTable> packageMonthlyShopTableList, List<IapTable> iapTableList){
        packageMonthlyInfos.ResetShop(packageMonthlyShopTableList, iapTableList);
    }

    public void CreateDarkObeShop(){
        this.myDarkObeShopInfos = new MyDarkObeShopInfos();
        this.myDarkObeShopInfos.myEnchantItemList = new ArrayList<>();
    }

    HeroEquipmentInventory generateShopHeroEquipment(Long userId, HeroEquipmentClassProbabilityTable classValues, List<EquipmentOptionsInfoTable> optionsInfoTableList, String gettingItem, List<HeroEquipmentsTable> probabilityEquipmentsList) {
        HeroEquipmentInventory generatedEquipment;
        int selectedRandomIndex = (int) MathHelper.Range(0, probabilityEquipmentsList.size());
        HeroEquipmentsTable heroEquipmentsTable = probabilityEquipmentsList.get(selectedRandomIndex);
        String itemClass = "";
        if(gettingItem.contains("ClassSS")){
            itemClass = "SS";
        }
        else if(gettingItem.contains("ClassS")){
            itemClass = "S";
        }
        else if(gettingItem.contains("ClassA")){
            itemClass = "A";
        }
        else if(gettingItem.contains("ClassB")){
            itemClass = "B";
        }
        else if(gettingItem.contains("ClassC")){
            itemClass = "C";
        }
        int classValue = (int) EquipmentCalculate.GetClassValueFromClassCategory(itemClass, classValues);
        generatedEquipment = EquipmentCalculate.CreateEquipment(userId, heroEquipmentsTable, itemClass, classValue, optionsInfoTableList);
        return generatedEquipment;
    }
}
