package com.onlyonegames.eternalfantasia.domain.model.entity.Companion;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MyLinkforceInfo extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String json_LinkforceInfos;

    @Builder
    public MyLinkforceInfo(Long useridUser, String json_LinkforceInfos) {
        this.useridUser = useridUser;
        this.json_LinkforceInfos = json_LinkforceInfos;
    }

    public void ResetLinkforceInfos(String json_LinkforceInfos) { this.json_LinkforceInfos = json_LinkforceInfos; }
}
