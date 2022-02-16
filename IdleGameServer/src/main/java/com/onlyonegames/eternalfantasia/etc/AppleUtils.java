package com.onlyonegames.eternalfantasia.etc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.AppleIap.AppStoreResponse;
import com.onlyonegames.eternalfantasia.domain.model.dto.AppleIap.UserReceipt;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Component
public class AppleUtils {

    @Autowired
    private final ErrorLoggingService errorLoggingService;

    private Logger logger = LoggerFactory.getLogger(AppleUtils.class);

    @Value("${APPLE.PRODUCTION.URL}")
    private String APPLE_PRODUCTION_URL;

    @Value("${APPLE.SANDBOX.URL}")
    private String APPLE_SANDBOX_URL;

//    @Value("${APPLE.PASSWORD}")
//    private String PASSWORD;

    private final ObjectMapper objectMapper;

    public AppleUtils(ErrorLoggingService errorLoggingService, ObjectMapper objectMapper) {
        this.errorLoggingService = errorLoggingService;
        this.objectMapper = objectMapper;
//        this.objectMapper = objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * App에서 전달받은 receipt-data를 Apple에 유효성 확인 요청
     * Apple Document URL ‣ https://developer.apple.com/documentation/appstorereceipts/verifyreceipt
     *
     * @param userReceipt
     * @throws JsonProcessingException
     */
    public AppStoreResponse verifyReceipt(UserReceipt userReceipt, Long userId) throws JsonProcessingException, IllegalStateException {

        Map<String, String> appStoreRequest = new HashMap<>();
        appStoreRequest.put("receipt-data", userReceipt.getReceiptData());
//        appStoreRequest.put("password", PASSWORD);

        String response = HttpClientUtils.doPost(APPLE_PRODUCTION_URL, appStoreRequest);
        AppStoreResponse appStoreResponse = objectMapper.readValue(response, AppStoreResponse.class);

        int statusCode = appStoreResponse.getStatus();
        if (statusCode == 21007) {
            response = HttpClientUtils.doPost(APPLE_SANDBOX_URL, appStoreRequest);
            appStoreResponse = objectMapper.readValue(response, AppStoreResponse.class);
        } else if (statusCode != 0) {
            verifyStatusCode(statusCode, userId);
        }

        return appStoreResponse;
    }

    private void verifyStatusCode(int statusCode, Long userId) {
        ResponseErrorCode responseErrorCode = ResponseErrorCode.UNDEFINED;
        switch (statusCode) {
            case 21000:
                logger.error("[Status code: " + statusCode + "] The request to the App Store was not made using the HTTP POST request method.");
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_USING_HTTP_POST.getIntegerValue(), ResponseErrorCode.NOT_USING_HTTP_POST.getStringValue(), this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                responseErrorCode = ResponseErrorCode.NOT_USING_HTTP_POST;
                break;
            case 21001:
                logger.error("[Status code: " + statusCode + "] This status code is no longer sent by the App Store.");
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NO_LONGER_SEND_BY_APP_STORE.getIntegerValue(), ResponseErrorCode.NO_LONGER_SEND_BY_APP_STORE.getStringValue(), this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                responseErrorCode = ResponseErrorCode.NO_LONGER_SEND_BY_APP_STORE;
                break;
            case 21002:
                logger.error("[Status code: " + statusCode + "] The data in the receipt-data property was malformed or the service experienced a temporary issue. Try again.");
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.WRONG_RECEIPT_DATA.getIntegerValue(), ResponseErrorCode.WRONG_RECEIPT_DATA.getStringValue(), this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                responseErrorCode = ResponseErrorCode.WRONG_RECEIPT_DATA;
                break;
            case 21003:
                logger.error("[Status code: " + statusCode + "] The receipt could not be authenticated.");
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.RECEIPT_COULD_NOT_BE_AUTHENTICATED.getIntegerValue(), ResponseErrorCode.RECEIPT_COULD_NOT_BE_AUTHENTICATED.getStringValue(), this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                responseErrorCode = ResponseErrorCode.RECEIPT_COULD_NOT_BE_AUTHENTICATED;
                break;
            case 21004:
                logger.error("[Status code: " + statusCode + "] The shared secret you provided does not match the shared secret on file for your account.");
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.SHARED_SECRET_DOES_NOT_MATCH.getIntegerValue(), ResponseErrorCode.SHARED_SECRET_DOES_NOT_MATCH.getStringValue(), this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                responseErrorCode = ResponseErrorCode.SHARED_SECRET_DOES_NOT_MATCH;
                break;
            case 21005:
                logger.error("[Status code: " + statusCode + "] The receipt server was temporarily unable to provide the receipt. Try again.");
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.RECEIPT_SERVER_NOT_WORK.getIntegerValue(), ResponseErrorCode.RECEIPT_SERVER_NOT_WORK.getStringValue(), this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                responseErrorCode = ResponseErrorCode.RECEIPT_SERVER_NOT_WORK;
                break;
            case 21006:
                logger.error("[Status code: " + statusCode + "] This receipt is valid but the subscription has expired. When this status code is returned to your server, the receipt data is also decoded and returned as part of the response. Only returned for iOS 6-style transaction receipts for auto-renewable subscriptions.");
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.SUBSCRIPTION_HAS_EXPIRED.getIntegerValue(), ResponseErrorCode.SUBSCRIPTION_HAS_EXPIRED.getStringValue(), this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                responseErrorCode = ResponseErrorCode.SUBSCRIPTION_HAS_EXPIRED;
                break;
            case 21008:
                logger.error("[Status code: " + statusCode + "] This receipt is from the production environment, but it was sent to the test environment for verification.");
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.RECEIPT_FROM_PRODUCTION_TO_TEST.getIntegerValue(), ResponseErrorCode.RECEIPT_FROM_PRODUCTION_TO_TEST.getStringValue(), this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                responseErrorCode = ResponseErrorCode.RECEIPT_FROM_PRODUCTION_TO_TEST;
                break;
            case 21009:
                logger.error("[Status code: " + statusCode + "] Internal data access error. Try again later.");
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.INTERNAL_DATA_ACCESS_ERROR.getIntegerValue(), ResponseErrorCode.INTERNAL_DATA_ACCESS_ERROR.getStringValue(), this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                responseErrorCode = ResponseErrorCode.INTERNAL_DATA_ACCESS_ERROR;
                break;
            case 21010:
                logger.error("[Status code: " + statusCode + "] The user account cannot be found or has been deleted.");
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.USER_ACCOUNT_NOT_FOUND.getIntegerValue(), ResponseErrorCode.USER_ACCOUNT_NOT_FOUND.getStringValue(), this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                responseErrorCode = ResponseErrorCode.USER_ACCOUNT_NOT_FOUND;
                break;
            default:
                logger.error("[Status code: " + statusCode + "] The receipt for the App Store is incorrect.");
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.UNDEFINED.getIntegerValue(), "The receipt for the App Store is incorrect.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                break;
        }

        throw new MyCustomException("Fail! -> "+responseErrorCode.getStringValue(), responseErrorCode);
    }

}
