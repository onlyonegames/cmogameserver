package com.onlyonegames.eternalfantasia.config;
import com.onlyonegames.eternalfantasia.domain.model.dto.RdsScoreDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.RankingInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.RedisScore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisRepositoryConfig {
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, RdsScoreDto> redisRdsScoreDtoTemplate() {
        final RedisTemplate<String, RdsScoreDto> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer( new StringRedisSerializer() );
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(RdsScoreDto.class) );
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(RdsScoreDto.class) );
        return redisTemplate;
    }

    @Bean
    RedisTemplate<String, Long> redisLongTemplate() {
        final RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory( redisConnectionFactory() );
        template.setKeySerializer( new StringRedisSerializer() );
        template.setHashValueSerializer(new GenericToStringSerializer<>(Long.class) );
        template.setValueSerializer(new GenericToStringSerializer<>(Long.class) );
        return template;
    }
}
