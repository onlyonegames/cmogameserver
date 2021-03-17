package com.onlyonegames.eternalfantasia.domain.service.Coupon;

import com.google.common.base.Strings;
import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.GamePot.CouponWebHookDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.MailSendRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Coupon.CouponInfo;
import com.onlyonegames.eternalfantasia.domain.repository.Cupon.CuponInfoRepository;
import com.onlyonegames.eternalfantasia.domain.service.Mail.MyMailBoxService;
import com.onlyonegames.util.StringMaker;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@AllArgsConstructor
public class CouponService {
    private final MyMailBoxService myMailBoxService;
    private final CuponInfoRepository cuponInfoRepository;

    public int CuponWebHookFromGamepot(CouponWebHookDto couponWebHookDto, Map<String, Object> map) {
        String userData = couponWebHookDto.getUserData();
        if(Strings.isNullOrEmpty(userData)) {
            map.put("message", "wrong gameUserId");
            return 0;
        }
        String []userDataArray = userData.split("and");

        Long userId = Long.parseLong(userDataArray[0]);
        String cuponNo = userDataArray[1];
        CouponInfo couponInfo = cuponInfoRepository.findByCouponNo(cuponNo).orElse(null);
        if(couponInfo == null) {
            StringMaker.Clear();
            StringMaker.stringBuilder.append("wrong cuponNo => ");
            StringMaker.stringBuilder.append(cuponNo);
            map.put("message", StringMaker.stringBuilder.toString());
            return 0;
        }

        String gettingItems = couponInfo.getGettingItemsInfo();
        String []gettingItemsArray = gettingItems.split(",");
        List<MailSendRequestDto.Item> itemList = new ArrayList<>();
        for(int i  = 0; i <  gettingItemsArray.length; i++) {
            MailSendRequestDto.Item item = new MailSendRequestDto.Item();
            String[] splitTemp = gettingItemsArray[i].split(":");
            item.setItem(splitTemp[0], Integer.parseInt(splitTemp[1]));
            itemList.add(item);
        }

        //원스의 경우 3종이므로 동종 못받게 처리
        if(couponInfo.getCouponPublisher().equals("원스토어")){
            List<CouponInfo> anotherReceivedCouponList = cuponInfoRepository.findAllByUseridUser(userId).orElse(null);
            if(anotherReceivedCouponList != null && anotherReceivedCouponList.size() > 0){
                CouponInfo sameKindCoupon = anotherReceivedCouponList.stream().filter(a -> a.getCouponPublisher().equals("원스토어")).findAny().orElse(null);
                if(sameKindCoupon != null) {
                    StringMaker.Clear();
                    StringMaker.stringBuilder.append("Duplicate Coupon_");
                    StringMaker.stringBuilder.append(cuponNo);
                    map.put("message", StringMaker.stringBuilder.toString());
                    return 0;
                }
            }
        }

        MailSendRequestDto mailSendRequestDto = new MailSendRequestDto();
        mailSendRequestDto.setToId(userId);
        mailSendRequestDto.setFromId(10000L);
        mailSendRequestDto.setTitle(couponInfo.getCuponTitle());
        mailSendRequestDto.setContent(couponInfo.getCuponContent());
        mailSendRequestDto.setExpireDate(LocalDateTime.now().plusYears(1));
        mailSendRequestDto.setItemList(itemList);
        mailSendRequestDto.setMailType(1);
        myMailBoxService.SendMail(mailSendRequestDto, map);
        couponInfo.WebHook(userId);
        return 1;
    }

    /**
     *  클라이언트가 게임팟의 응답후 최종 트랜잭션 완료 요청. uniqueId로 트랜잭션 체크하고 해당 상품에 대한 아이템 등록후 전송
     * */
    public Map<String, Object> CompleteCouponTransaction (Long userId, String cuponNo, Map<String, Object> map) {

//        CouponInfo couponInfo = cuponInfoRepository.findByCouponNo(cuponNo).orElse(null);
//        //원스의 경우 3종이므로 동종 못받게 처리
//        if(couponInfo.getCouponPublisher().equals("원스토어")){
//            List<CouponInfo> anotherReceivedCouponList = cuponInfoRepository.findAllByUseridUser(userId).orElse(null);
//            if(anotherReceivedCouponList != null && anotherReceivedCouponList.size() > 0){
//                CouponInfo sameKindCoupon = anotherReceivedCouponList.stream().filter(a -> a.getCouponPublisher().equals("원스토어")).findAny().orElse(null);
//                if(sameKindCoupon != null) {
//                    throw new MyCustomException("Fail! -> Cause: cuponInfo is used.", ResponseErrorCode.ALREADY_USED_COUPON);
//                }
//            }
//        }
//        if(couponInfo == null) {
//            throw new MyCustomException("Fail! -> Cause: cuponInfo not exist", ResponseErrorCode.COUPON_NOT_EXIST);
//        }
//        if(couponInfo.getCouponState() == 2)
//            throw new MyCustomException("Fail! -> Cause: cuponInfo is used.", ResponseErrorCode.ALREADY_USED_COUPON);
//        couponInfo.ReceiveCupon();
//        map.put("cuponInfo", couponInfo);
        myMailBoxService.GetMyMailBox(userId,map);
        return map;
    }
}
