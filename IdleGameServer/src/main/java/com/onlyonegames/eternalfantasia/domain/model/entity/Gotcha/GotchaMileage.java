package com.onlyonegames.eternalfantasia.domain.model.entity.Gotcha;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class GotchaMileage extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    int mileage;
    long totalCountGetMileage;

    @Builder
    public GotchaMileage(Long useridUser) {
        this.useridUser = useridUser;
        Init();
    }

    public void Init() {
        this.mileage = 0;
        this.totalCountGetMileage = 0;
    }

    public void AddMileage(int mileage) {
        this.mileage += mileage;
        this.totalCountGetMileage += mileage;
    }

    public boolean SpendMileageForHero() {
        int costForGotcha = 120;
        if(mileage < 120)
            return false;
        mileage -= costForGotcha;
        return true;
    }
}
