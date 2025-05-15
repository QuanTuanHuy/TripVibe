package com.booking.inventory.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Test configuration for Redis to allow tests to run without a real Redis server
 */
@TestConfiguration
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "test")
public class TestRedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // Use a mock implementation for tests
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        return template;
    }
    
    @PostConstruct
    public void setUpTestConfigs() {
        // Log that we're using the test configuration
        System.out.println("Using test Redis configuration");
    }
}
