package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.CouponUsedLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.MailSendRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.CouponData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.CouponUsedLog;
import com.onlyonegames.eternalfantasia.domain.repository.CouponDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Logging.CouponUsedLogRepository;
import com.onlyonegames.eternalfantasia.domain.service.Mail.MyMailBoxService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class CouponService {
    private final CouponDataRepository couponDataRepository;
    private final CouponUsedLogRepository couponUsedLogRepository;
    private final MyMailBoxService myMailBoxService;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> UseCoupon(Long userId, String code, Map<String, Object> map) {
        CouponData couponData = couponDataRepository.findByCode(code).orElse(null);
        if (couponData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.COUPON_NOT_EXIST.getIntegerValue(), "Fail! -> Cause: Coupon Not Exist.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Coupon Not Exist.", ResponseErrorCode.COUPON_NOT_EXIST);
        }
        LocalDateTime now = LocalDateTime.now();
        if (couponData.getBeginDate().isAfter(now) || couponData.getExpireDate().isBefore(now)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.COUPON_NOT_EXIST.getIntegerValue(), "Fail! -> Cause: Coupon Not Exist.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Coupon Not Exist.", ResponseErrorCode.COUPON_NOT_EXIST);
        }
        CouponUsedLog couponUsedLog = couponUsedLogRepository.findByUseridUserAndUsedCouponId(userId, couponData.getId()).orElse(null);
        if (!couponData.UsedCoupon() || couponUsedLog != null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_USED_COUPON.getIntegerValue(), "Fail! -> Cause: Already Used Coupon.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Already Used Coupon.", ResponseErrorCode.ALREADY_USED_COUPON);
        }
        CouponUsedLogDto couponUsedLogDto = new CouponUsedLogDto();
        couponUsedLogDto.SetCouponUsedLogDto(userId, couponData.getId(), code);
        couponUsedLogRepository.save(couponUsedLogDto.ToEntity());
        Map<String, Object> tempMap = new HashMap<>();
        String[] gettingItemSplit = couponData.getGettingItem().split(",");
        for (String temp : gettingItemSplit){
            String[] itemSplit = temp.split(":");
            SendMail(userId, couponData.getName(), itemSplit[0], itemSplit[1], tempMap);
        }
        return map;
    }

    private void SendMail(Long userId, String title, String gettingItem, String gettingItemCount, Map<String, Object> tempMap) {
        LocalDateTime now = LocalDateTime.now();
        MailSendRequestDto mailSendRequestDto = new MailSendRequestDto();
        mailSendRequestDto.setToId(userId);
        mailSendRequestDto.setSendDate(now);
        mailSendRequestDto.setMailType(0);
        mailSendRequestDto.setExpireDate(now.plusDays(1));
        mailSendRequestDto.setTitle(title);
        mailSendRequestDto.setGettingItem(gettingItem); //TODO 보상 테이블에 있는 보상으로 지급
        mailSendRequestDto.setGettingItemCount(gettingItemCount);
        myMailBoxService.SendMail(mailSendRequestDto, tempMap);
    }
}
