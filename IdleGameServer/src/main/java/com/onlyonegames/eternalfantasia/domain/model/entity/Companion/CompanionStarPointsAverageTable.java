package com.onlyonegames.eternalfantasia.domain.model.entity.Companion;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class CompanionStarPointsAverageTable extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String json_StarPointsAverage;

    public void ResetStarPointsAverage(String json_StarPointsAverage) {
        this.json_StarPointsAverage = json_StarPointsAverage;
    }
}
