package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.model.dto.ErrorLoggingDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.ErrorLogging;
import com.onlyonegames.eternalfantasia.domain.repository.ErrorLoggingRepository;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class ErrorLoggingService {
    private final ErrorLoggingRepository errorLoggingRepository;
    public void SetErrorLog(Long userid, int errorCode, String errorDetail, String errorClass, String errorFunction, boolean isDirectWrighDB) {
        ErrorLoggingDto errorLoggingDto = new ErrorLoggingDto();
        errorLoggingDto.setErrorLoggingDto(userid, errorCode, errorDetail, errorClass, errorFunction);

        if(!isDirectWrighDB) {
            HttpHeaders headers = new HttpHeaders();
            RestTemplate restTemplate = new RestTemplate();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String url = "http://localhost:8081/api/Test/ErrorLogging";
            String json = JsonStringHerlper.WriteValueAsStringFromData(errorLoggingDto);
            HttpEntity<String> entity = new HttpEntity<>(json, headers);
            ResponseEntity<Void> response = restTemplate.postForEntity(url, entity, Void.class);
        }else {
            ErrorLogging errorLogging = errorLoggingDto.ToEntity();
            errorLoggingRepository.save(errorLogging);
        }
    }

    public void SetErrorLog(Long userid, int errorCode, String errorDetail, String errorClass, String errorFunction, int lineNumber, boolean isDirectWrighDB) {
        ErrorLoggingDto errorLoggingDto = new ErrorLoggingDto();
        errorLoggingDto.setErrorLoggingDto(userid, errorCode, errorDetail, errorClass, lineNumber, errorFunction);

        if(!isDirectWrighDB) {
            HttpHeaders headers = new HttpHeaders();
            RestTemplate restTemplate = new RestTemplate();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String url = "http://localhost:8081/api/Test/ErrorLogging";
            String json = JsonStringHerlper.WriteValueAsStringFromData(errorLoggingDto);
            HttpEntity<String> entity = new HttpEntity<>(json, headers);
            ResponseEntity<Void> response = restTemplate.postForEntity(url, entity, Void.class);
        }else {
            ErrorLogging errorLogging = errorLoggingDto.ToEntity();
            errorLoggingRepository.save(errorLogging);
        }
    }
}
