package com.example.redis.increx;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisJedisConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String host;

    @Value("${spring.data.redis.port:6379}")
    private int port;

    @Value("${spring.data.redis.password:}")
    private String password;

    @Value("${spring.data.redis.timeout:2000}")
    private int timeout;

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(20);
        poolConfig.setMaxIdle(10);
        poolConfig.setMinIdle(2);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        // Avoid InstanceAlreadyExistsException: commons-pool2 and Spring both try to register the same MBean
        poolConfig.setJmxEnabled(false);

        // اگر رمز عبور وجود دارد
        if (password != null && !password.isEmpty()) {
            return new JedisPool(poolConfig, host, port, timeout, password);
        } else {
            return new JedisPool(poolConfig, host, port, timeout);
        }
    }
}