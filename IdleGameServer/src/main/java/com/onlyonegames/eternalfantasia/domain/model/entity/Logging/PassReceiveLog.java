package com.onlyonegames.eternalfantasia.domain.model.entity.Logging;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.*;

@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PassReceiveLog extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence", allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String passName;
    int levelIndex;
    int passIndex;
}