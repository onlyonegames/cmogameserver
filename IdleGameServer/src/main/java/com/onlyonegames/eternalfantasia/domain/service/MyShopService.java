package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.MailSendRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyShopInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.MyShopInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.Mail.MyMailBoxService;
import com.onlyonegames.util.MathHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyShopService {
    private final MyShopInfoRepository myShopInfoRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;
    private final MyMailBoxService myMailBoxService;
    private final UserRepository userRepository;

    public Map<String, Object> ShopBuy(Long userId, boolean purchase, int itemIndex, Map<String, Object> map) {
//        MyShopInfo myShopInfo = myShopInfoRepository.findByUseridUser(userId).orElse(null);
//        if (myShopInfo == null) {
//            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyShopInfo Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
//            throw new MyCustomException("Fail! -> Cause: MyShopInfo Can't find", ResponseErrorCode.NOT_FIND_DATA);
//        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: User Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: User Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<ShopRewardTable> shopRewardTableList = gameDataTableService.ShopRewardTable();
        ShopRewardTable shopRewardTable = shopRewardTableList.get(itemIndex);
        String[] rewardList = shopRewardTable.getRewardList().split(",");
        Map<String, Object> tempMap = new HashMap<>();
        for (String s : rewardList) {
            String[] reward = s.split(":");
            switch (reward[0]) {
                case "heroRune":
                    for (int j = 0; j < Integer.parseInt(reward[1]); j++) {
                        RuneInfoTable runeInfoTable = GetHeroRandomRune();
                        String code = runeInfoTable.getCode();
                        SendMail(userId, purchase, shopRewardTable.getItemName(), code, "1", tempMap);
                    }
                    break;
                case "accessory":
                    for (int j = 0; j < Integer.parseInt(reward[1]); j++) {
                        AccessoryTable accessoryTable = GetRandomAccessory();
                        String code = accessoryTable.getCode();
                        SendMail(userId, purchase, shopRewardTable.getItemName(), code, "1", tempMap);
                    }
                    break;
                case "legendClass":
                    for (int j = 0; j < Integer.parseInt(reward[1]); j++) {
                        HeroClassInfoTable heroClassInfoTable = GetLegendRandomHeroClassInfoTable();
                        String code = heroClassInfoTable.getCode();
                        SendMail(userId, purchase, shopRewardTable.getItemName(), code, "1", tempMap);
                    }
                    break;
                case "legendEquipment":
                    for (int j = 0; j < Integer.parseInt(reward[1]); j++) {
                        EquipmentTable equipmentTable = GetLegendRandomEquipment();
                        String code = equipmentTable.getCode();
                        SendMail(userId, purchase, shopRewardTable.getItemName(), code, "1", tempMap);
                    }
                    break;
                case "legendDGradeRune":
                    for (int j = 0; j < Integer.parseInt(reward[1]); j++) {
                        RuneInfoTable runeInfoTable = GetLegendDGradeRune();
                        String code = runeInfoTable.getCode();
                        SendMail(userId, purchase, shopRewardTable.getItemName(), code, "1", tempMap);
                    }
                    break;
                case "divineDGradeRune":
                    for (int j = 0; j < Integer.parseInt(reward[1]); j++) {
                        RuneInfoTable runeInfoTable = GetDivineDGradeRune();
                        String code = runeInfoTable.getCode();
                        SendMail(userId, purchase, shopRewardTable.getItemName(), code, "1", tempMap);
                    }
                    break;
                case "legendAGradeEquipment":
                    for (int j = 0; j < Integer.parseInt(reward[1]); j++) {
                        EquipmentTable equipmentTable = GetLegendAGradeRandomEquipment();
                        String code = equipmentTable.getCode();
                        SendMail(userId, purchase, shopRewardTable.getItemName(), code, "1", tempMap);
                    }
                    break;
                default:
                    SendMail(userId, purchase, shopRewardTable.getItemName(), reward[0], reward[1], tempMap);
                    break;
            }
        }
        SpendPrice(user, shopRewardTable.getCurrencyType(), shopRewardTable.getPrice());
        map.put("userInfo", user);
        return map;
    }
    private AccessoryTable GetRandomAccessory() {
        int randomIndex = (int) (Math.random() * 11);
        return gameDataTableService.AccessoryTable().get(randomIndex);
    }

    private EquipmentTable GetLegendRandomEquipment() {

        List<EquipmentTable> equipmentTableList = gameDataTableService.EquipmentTable();
        List<EquipmentTable> legendEquipmentList = equipmentTableList.stream().filter(i -> i.getGrade().equals("Legend")).collect(Collectors.toList());
        List<Double> probabilityList = new ArrayList<>();
        probabilityList.add(0.4);
        probabilityList.add(0.3);
        probabilityList.add(0.2);
        probabilityList.add(0.1);
        int index = MathHelper.RandomIndexWidthProbability(probabilityList) + 1;
        int classIndex = (int) (Math.random() * 5);
        return legendEquipmentList.stream().filter(i -> i.getGradeValue() == index).collect(Collectors.toList()).get(classIndex);
    }

    private EquipmentTable GetLegendAGradeRandomEquipment() {
        List<EquipmentTable> equipmentTableList = gameDataTableService.EquipmentTable();
        List<EquipmentTable> legendEquipmentList = equipmentTableList.stream().filter(i -> i.getGrade().equals("Legend") && i.getGradeValue() == 4).collect(Collectors.toList());
        int index = (int) (Math.random() * 5);
        return legendEquipmentList.get(index);
    }

    private HeroClassInfoTable GetLegendRandomHeroClassInfoTable() {
        List<HeroClassInfoTable> heroClassInfoTableList = gameDataTableService.HeroClassInfoTable();
        List<HeroClassInfoTable> legendHeroClassInfoTableList = heroClassInfoTableList.stream().filter(i -> i.getGrade().equals("legend")).collect(Collectors.toList());
        int index = (int) (Math.random() * 5);
        return legendHeroClassInfoTableList.get(index);
    }

    private RuneInfoTable GetHeroRandomRune() {
        List<RuneInfoTable> runeInfoTableList = gameDataTableService.RuneInfoTable();
        List<RuneInfoTable> heroRuneInfoTableList = runeInfoTableList.stream().filter(i -> i.getQualityNo() == 4).collect(Collectors.toList());
        List<Double> probabilityList = new ArrayList<>();
        probabilityList.add(0.4);
        probabilityList.add(0.3);
        probabilityList.add(0.15);
        probabilityList.add(0.1);
        probabilityList.add(0.05);
        int index = MathHelper.RandomIndexWidthProbability(probabilityList) + 1;
        int kindIndex = (int) (Math.random() * 5);
        return heroRuneInfoTableList.stream().filter(i -> i.getGradeNo() == index).collect(Collectors.toList()).get(kindIndex);
    }

    private RuneInfoTable GetLegendDGradeRune() {
        List<RuneInfoTable> runeInfoTableList = gameDataTableService.RuneInfoTable();
        List<RuneInfoTable> legendRuneInfoTableList = runeInfoTableList.stream().filter(i -> i.getQualityNo() == 5 && i.getGradeNo() == 1).collect(Collectors.toList());
        int index = (int) (Math.random() * 5);
        return legendRuneInfoTableList.get(index);
    }
    private RuneInfoTable GetDivineDGradeRune() {
        List<RuneInfoTable> runeInfoTableList = gameDataTableService.RuneInfoTable();
        List<RuneInfoTable> divineRuneInfoTableList = runeInfoTableList.stream().filter(i -> i.getQualityNo() == 6 && i.getGradeNo() == 1).collect(Collectors.toList());
        int index = (int) (Math.random() * 5);
        return divineRuneInfoTableList.get(index);
    }

    private void SendMail(Long userId, boolean purchase, String title, String gettingItem, String gettingItemCount, Map<String, Object> tempMap) {
        LocalDateTime now = LocalDateTime.now();
        MailSendRequestDto mailSendRequestDto = new MailSendRequestDto();
        mailSendRequestDto.setToId(userId);
        mailSendRequestDto.setSendDate(now);
        mailSendRequestDto.setMailType(purchase?1:0);
        mailSendRequestDto.setExpireDate(now.plusDays(30));
        mailSendRequestDto.setTitle(title);
        mailSendRequestDto.setGettingItem(gettingItem); //TODO 보상 테이블에 있는 보상으로 지급
        mailSendRequestDto.setGettingItemCount(gettingItemCount);
        myMailBoxService.SendMail(mailSendRequestDto, tempMap);
    }

    private void SpendPrice(User user, String currencyType, int price) {
        switch (currencyType) {
            case "arenaCoin":
                if (!user.SpendArenaCoin((long) price)) {
                    errorLoggingService.SetErrorLog(user.getId(), ResponseErrorCode.NEED_MORE_ARENACOIN.getIntegerValue(), "Fail! -> Cause: Need More ArenaCoin.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need More ArenaCoin.", ResponseErrorCode.NEED_MORE_ARENACOIN);
                }
                break;
            case "dragonCoin":
                if (!user.SpendDragonCoin((long) price)) {
                    errorLoggingService.SetErrorLog(user.getId(), ResponseErrorCode.NEED_MORE_DRAGONCOIN.getIntegerValue(), "Fail! -> Cause: Need More DragonCoin.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need More DragonCoin.", ResponseErrorCode.NEED_MORE_DRAGONCOIN);
                }
                break;
            case "cash":
                break;
            case "mileage":
                break;
        }
    }
}
