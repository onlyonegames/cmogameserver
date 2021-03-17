package com.onlyonegames.eternalfantasia.Interceptor;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash("session")
public class OnlyoneSession implements Serializable {

    private static final long serialVersionUID = -3352190913886024004L;
    private static final long sessionExpire = 1800;
    @Id
    Long id; /**유저 아이디*/
    String jwt;
    LocalDateTime sessionExpireTime;

    public OnlyoneSession(Long id, String jwt) {
        this.id = id;
        this.jwt = jwt;
        RenewalExpireTime();
    }

    public void RenewalExpireTime() {
        sessionExpireTime = LocalDateTime.now();
        sessionExpireTime = sessionExpireTime.plusSeconds(sessionExpire);
    }

}
