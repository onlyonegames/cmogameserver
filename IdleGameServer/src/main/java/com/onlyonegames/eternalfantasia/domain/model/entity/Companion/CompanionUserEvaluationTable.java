package com.onlyonegames.eternalfantasia.domain.model.entity.Companion;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "companionuserevaluationtable")
public class CompanionUserEvaluationTable {
    @Id
    String code;
    int sum;
    double avarage;
    int totalCount;

    public void AddEvaluation(int starPoint){
        sum += starPoint;
        totalCount++;
        avarage = sum / (double)totalCount;
    }

    public void ModifyEvaluation(int oldStarPoint, int starPoint){
        sum -= oldStarPoint;
        sum += starPoint;
        avarage = sum / (double)totalCount;
    }
}
