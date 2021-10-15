package com.onlyonegames.eternalfantasia.domain.model.entity.Mail;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class Mail extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long toId;/*0이면 시스템내 모든 유저*/
    String title;
    String gettingItems;
    String gettingItemCounts;
    LocalDateTime sendDate;//우편 보낸 날자
    LocalDateTime expireDate;//우편 만료 날자
    int mailType;/*0 only messeage, 1 아이템 선물*/
}
