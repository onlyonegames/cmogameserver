package com.onlyonegames.eternalfantasia.domain.model.entity.Logging;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ArenaPlayLog extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence", allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    Long versusUserId;
    int winFailStatus;

    public void SetWin() {
        this.winFailStatus = 1;
    }

    public void SetFail() {
        this.winFailStatus = -1;
    }
}
