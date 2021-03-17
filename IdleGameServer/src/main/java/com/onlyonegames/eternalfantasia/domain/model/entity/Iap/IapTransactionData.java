package com.onlyonegames.eternalfantasia.domain.model.entity.Iap;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@Builder
public class IapTransactionData extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    @Column(length = 512)
    String transactionId;
    @Column(length = 64)
    String store;
    @Column(length = 128)
    String projectId;
    @Column(length = 256)
    String productId;
    @Column(length = 128)
    String platform;
    @Column(length = 64)
    String payment;
    @Column(length = 512)
    String gamepotOrderId;
    /**
     * 0, 트랜잭션 시작.
     * 1. 게임팟 webhook 완료.
     * 2. 클라이언트 확인 완료. 트랜잭션 종료.
     * */
    int state;
    //게임팟 통신시 사용하는 데이터이지만 필요 없어서 사용 안함.
    //String serverId;
    //String playerId;
    //String etc;

    public void SetWebHookData(String transactionId, String store, String projectId, String productId, String platform, String payment, String gamepotOrderId) {
        this.transactionId = transactionId;
        this.store = store;
        this.projectId = projectId;
        this.productId = productId;
        this.platform = platform;
        this.payment = payment;
        this.gamepotOrderId = gamepotOrderId;
        this.state = 1;
    }

    public void CompleteTransaction() {
        this.state = 2;
    }
}
