package com.waggle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class RedisConfig {
    
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        log.info("Redis 연결중...");
        try {
            LettuceConnectionFactory factory = new LettuceConnectionFactory("redis", 6379);
            factory.afterPropertiesSet();  // 연결 초기화
            log.info("Redis 연결 성공");
            return factory;
        } catch (Exception e) {
            log.error("Redis 연결 실패", e);
            throw e;
        }
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}
