package com.sgi.wallet.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;

@Configuration
@EnableCaching
@Slf4j
public class Redisson {
    @Value("${redis.host}")
    private String hostName;

    @Value("${redis.password}")
    private String password;

    @Value("${redis.port}")
    private Integer port;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(){
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(hostName);
        config.setPort(port);
        config.setPassword(password);
        log.info("Redisson environment : {}, {}", hostName, port);
        JedisConnectionFactory connectionFactory  = new JedisConnectionFactory(config);
        connectionFactory.afterPropertiesSet();
        if(Objects.equals(connectionFactory.getConnection().ping(), "PONG")){
            log.info("The connection to redis is correct : {}", connectionFactory);
        }
        return connectionFactory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

}
