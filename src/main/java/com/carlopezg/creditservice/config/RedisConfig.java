package com.carlopezg.creditservice.config;

import com.carlopezg.creditservice.domain.CustomRateLimiter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "redis")
@Getter
@Setter
public class RedisConfig {

    private String server;
    private int port;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jcf = new JedisConnectionFactory(new RedisStandaloneConfiguration(this.server, this.port));
        jcf.afterPropertiesSet();
        return jcf;
    }

    @Bean
    public RedisTemplate<String, CustomRateLimiter> redisTemplate() {
        RedisTemplate<String, CustomRateLimiter> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.afterPropertiesSet();
        return template;
    }
}
