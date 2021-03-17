package com.onlyonegames.eternalfantasia.domain.service.Companion;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.CompanionStarPointsAverageDtosList;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.CompanionStarPointsAverageTable;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.CompanionUserEvaluationTable;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyAncientDragonExpandSaveData;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.herostable;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.CompanionStarPointsAverageRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.CompanionUserEvaluationRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class CompanionUserEvaluationService {
    private final CompanionUserEvaluationRepository companionUserEvaluationRepository;
    private final CompanionStarPointsAverageRepository companionStarPointsAverageRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> GetEvaluation(Long userId, Map<String, Object> map){
        List<herostable> herostableList = gameDataTableService.HerosTableList();

        //신규 케릭터 추가시 해당 케릭터에 대한 유저별 별점 데이터 업데이트 해주기
        CompanionStarPointsAverageTable companionStarPointsAverageTable = companionStarPointsAverageRepository.findByUseridUser(userId)
                .orElse(null);
        String json_StarPointsAverage = companionStarPointsAverageTable.getJson_StarPointsAverage();
        CompanionStarPointsAverageDtosList companionStarPointsAverageDtosList = JsonStringHerlper.ReadValueFromJson(json_StarPointsAverage, CompanionStarPointsAverageDtosList.class);
        List<CompanionStarPointsAverageDtosList.StarPointsAverageDto> starPointsAverageDtoList = companionStarPointsAverageDtosList.starPointsAverageDtoList;
        for(herostable heroData : herostableList) {
            if(heroData.getCode().equals("hero"))
                continue;
            CompanionStarPointsAverageDtosList.StarPointsAverageDto starPointsAverageDto = starPointsAverageDtoList.stream().filter(a -> a.heroCode.equals(heroData.getCode())).findAny().orElse(null);
            if(starPointsAverageDto == null)
            {
                CompanionStarPointsAverageDtosList.StarPointsAverageDto newStartPointsAverageDto = new CompanionStarPointsAverageDtosList.StarPointsAverageDto();
                newStartPointsAverageDto.heroCode = heroData.getCode();
                newStartPointsAverageDto.starPoints = -1;
                starPointsAverageDtoList.add(newStartPointsAverageDto);
            }
        }
        json_StarPointsAverage = JsonStringHerlper.WriteValueAsStringFromData(companionStarPointsAverageDtosList);
        companionStarPointsAverageTable.ResetStarPointsAverage(json_StarPointsAverage);


        List<CompanionUserEvaluationTable> companionUserEvaluationTableList = companionUserEvaluationRepository.findAll();
        map.put("companionUserEvaluationList", companionUserEvaluationTableList);
        return map;
    }

    public Map<String, Object> AddEvaluation(Long userId , String heroCode, int starPoint, Map<String, Object> map){

        CompanionStarPointsAverageTable companionStarPointsAverageTable = companionStarPointsAverageRepository.findByUseridUser(userId)
                .orElse(null);
        if(companionStarPointsAverageTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: CompanionStarPointsAverageTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: CompanionStarPointsAverageTable not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String json_StarPointsAverage = companionStarPointsAverageTable.getJson_StarPointsAverage();
        CompanionStarPointsAverageDtosList companionStarPointsAverageDtosList = JsonStringHerlper.ReadValueFromJson(json_StarPointsAverage, CompanionStarPointsAverageDtosList.class);
        CompanionStarPointsAverageDtosList.StarPointsAverageDto starPointsAverageDto = companionStarPointsAverageDtosList.starPointsAverageDtoList.stream()
                .filter(a -> a.heroCode.equals(heroCode))
                .findAny()
                .orElse(null);
        if(starPointsAverageDto == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: starPointsAverageDto Can't Find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: starPointsAverageDto Can't Find", ResponseErrorCode.NOT_EXIST_CODE);
        }

        if(starPointsAverageDto.starPoints < 0) {//해당 유저의 해당 케릭터 별점 최초 셋팅
            List<CompanionUserEvaluationTable> companionUserEvaluationTableList = companionUserEvaluationRepository.findAll();
            CompanionUserEvaluationTable companionUserEvaluationTable = companionUserEvaluationTableList.stream()
                    .filter(a -> a.getCode().equals(heroCode))
                    .findAny()
                    .orElse(null);
            if(companionUserEvaluationTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: CompanionUserEvaluation not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: CompanionUserEvaluation not find.", ResponseErrorCode.NOT_FIND_DATA);
            }

            companionUserEvaluationTable.AddEvaluation(starPoint);
            map.put("companionUserEvaluationList", companionUserEvaluationTableList);
        }
        else {//기존 별점 수정
            List<CompanionUserEvaluationTable> companionUserEvaluationTableList = companionUserEvaluationRepository.findAll();
            CompanionUserEvaluationTable companionUserEvaluationTable = companionUserEvaluationTableList.stream()
                    .filter(a -> a.getCode().equals(heroCode))
                    .findAny()
                    .orElse(null);
            if(companionUserEvaluationTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: CompanionUserEvaluation not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: CompanionUserEvaluation not find.", ResponseErrorCode.NOT_FIND_DATA);
            }

            companionUserEvaluationTable.ModifyEvaluation(starPointsAverageDto.starPoints, starPoint);
            map.put("companionUserEvaluationList", companionUserEvaluationTableList);
        }
        //별점 통계 적용
        starPointsAverageDto.starPoints = starPoint;
        json_StarPointsAverage = JsonStringHerlper.WriteValueAsStringFromData(companionStarPointsAverageDtosList);
        companionStarPointsAverageTable.ResetStarPointsAverage(json_StarPointsAverage);
        return map;
    }
}
