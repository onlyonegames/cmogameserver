package com.onlyonegames.eternalfantasia.domain.model.entity.Companion;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MyLinkweaponInfo extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String json_LinkweaponRevolution;

    @Builder
    public MyLinkweaponInfo(Long useridUser, String json_LinkweaponRevolution) {
        this.useridUser = useridUser;
        this.json_LinkweaponRevolution = json_LinkweaponRevolution;
    }

    public void ResetLinkweaponRevolution(String json_LinkweaponRevolution) { this.json_LinkweaponRevolution = json_LinkweaponRevolution;}
}
