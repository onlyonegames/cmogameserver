package com.onlyonegames.eternalfantasia.domain.model.entity.Mail;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class MyMailBox extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String json_myMailBoxInfo;

    public void ResetJsonMyMailBoxInfo(String json_myMailBoxInfo) {
        this.json_myMailBoxInfo = json_myMailBoxInfo;
    }
}
