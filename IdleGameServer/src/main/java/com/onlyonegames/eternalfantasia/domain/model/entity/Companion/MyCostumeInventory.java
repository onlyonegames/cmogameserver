package com.onlyonegames.eternalfantasia.domain.model.entity.Companion;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MyCostumeInventory extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String json_CostumeInventory;

    @Builder
    public MyCostumeInventory(Long useridUser, String json_CostumeInventory) {
        this.useridUser = useridUser;
        this.json_CostumeInventory = json_CostumeInventory;
    }

    public void ResetCostumeInventory(String json_CostumeInventory) {
        this.json_CostumeInventory = json_CostumeInventory;
    }
}
