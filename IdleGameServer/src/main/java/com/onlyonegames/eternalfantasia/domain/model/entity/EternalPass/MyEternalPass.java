package com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass;
import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class MyEternalPass extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String json_myEternalPassInfo;

    public void ResetJsonMyEternalPassInfo(String json_myEternalPassInfo) {
        this.json_myEternalPassInfo = json_myEternalPassInfo;
    }
}
