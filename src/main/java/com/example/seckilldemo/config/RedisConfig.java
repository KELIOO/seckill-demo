package com.example.seckilldemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * @author 86187
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> redistemplate = new RedisTemplate<>();
        //key的序列化
        redistemplate.setKeySerializer(new StringRedisSerializer());
        //value的序列化
        redistemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        //hash的序列化
        redistemplate.setHashKeySerializer(new StringRedisSerializer());
        redistemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        redistemplate.setConnectionFactory(redisConnectionFactory);
        return redistemplate;
    }

    @Bean
    public DefaultRedisScript<Long> script(){
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setLocation(new ClassPathResource("stock.lua"));
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}
