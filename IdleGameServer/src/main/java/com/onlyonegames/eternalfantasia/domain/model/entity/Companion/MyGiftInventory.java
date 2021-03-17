package com.onlyonegames.eternalfantasia.domain.model.entity.Companion;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MyGiftInventory extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String inventoryInfos;

    @Builder
    public MyGiftInventory(Long useridUser, String inventoryInfos) {
        this.useridUser = useridUser;
        this.inventoryInfos = inventoryInfos;
    }

    public void ResetInventoryInfos(String inventoryInfos) {
        this.inventoryInfos = inventoryInfos;
    }
}
