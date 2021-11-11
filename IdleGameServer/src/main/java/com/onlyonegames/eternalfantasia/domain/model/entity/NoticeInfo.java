package com.onlyonegames.eternalfantasia.domain.model.entity;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class NoticeInfo extends BaseTimeEntity {
    @Id
    int id;
    String contents;
    LocalDateTime expireDate;

    public void ChangeContents(String contents) {
        this.contents = contents;
    }

    public void changeExpireDate(LocalDateTime expireDate) {
        this.expireDate = expireDate;
    }
}
